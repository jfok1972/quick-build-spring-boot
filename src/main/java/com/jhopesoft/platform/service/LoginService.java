package com.jhopesoft.platform.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import com.jhopesoft.framework.bean.ResultBean;
import com.jhopesoft.framework.bean.UserBean;
import com.jhopesoft.framework.bean.UserCanSelectDataRole;
import com.jhopesoft.framework.core.annotation.SystemLogs;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.limit.FUserdatacanselectrole;
import com.jhopesoft.framework.dao.entity.log.FUserloginlog;
import com.jhopesoft.framework.dao.entity.system.FCompany;
import com.jhopesoft.framework.dao.entity.system.FSysteminfo;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.utils.CommonFunction;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.MD5;
import com.jhopesoft.framework.utils.SessionUtils;
import com.jhopesoft.framework.utils.Sm4Util;
import com.jhopesoft.platform.controller.Login;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Service
public class LoginService {
	@Resource
	private DaoImpl dao;

	@Resource
	private SystemCommonService systemCommonService;

	/** 用户登时的用于生成加密salt的字符串，每次重新启动应用都会更换 */
	public static final String SM4KEY = "9d1ebf8686d7811dc55fa5fa6d259861";
	// 每次重启都换一个salt 使用下面的代码
	// CommonUtils.getRandomHex(16);

	/**
	 * case "0": login(themetype); break; case "1": msg = "请输入正确的验证码!"; break; case
	 * "2": msg = "您所输入的用户名不存在!"; break; case "3": msg = "密码输入错误,请重新输入!"; break;
	 * case "4": msg = "当前用户名已被锁定,无法登录!"; break; case "5": msg = "当前用户名已被注销,无法登录!";
	 * break; case "6": msg = "当前用户所在公司已被注销,无法登录!"; break; case "7": msg =
	 * "当前用户已经在线"; break; default: msg = "提交失败, 可能存在网络故障或其他未知原因!"; break; 9':
	 * '用户密码解析错误，请刷新网页后再进行登录!',
	 * 
	 * @throws Exception
	 */
	@SystemLogs("用户登陆")
	public ResultBean login(String companyid, String usercode, String password, Boolean invalidate,
			String identifingcode) throws Exception {
		try {
			StringBuffer sb = new StringBuffer(SM4KEY);
			password = Sm4Util.decrypt(password, sb.reverse().toString());
		} catch (Exception e) {
			addFailureCount(usercode, companyid);
			return new ResultBean(false, "9");
		}
		FSysteminfo systeminfo = getCompanySystemInfo(companyid);
		if (BooleanUtils.isTrue(systeminfo.getAlwaysneedidentifingcode()) || identifingcode != null) {
			Object vcode = Local.getRequest().getSession().getAttribute(Login.VALIDATECODE);
			// vcode == null 可能是几个小时没登录，后台session没了,必须刷新页面？
			if (identifingcode == null || vcode == null || !identifingcode.equalsIgnoreCase(vcode.toString())) {
				return new ResultBean(false, "1");
			}
		}
		if (systeminfo.needFailureWait() && checkUserNameIsLock(usercode, companyid)) {
			return new ResultBean(false, "8");
		}
		FUser userinfo = dao.findByPropertyFirst(FUser.class, Constants.USERCODE, usercode, Constants.COMPANYID,
				companyid);
		if (userinfo == null) {
			addFailureCount(usercode, companyid);
			return new ResultBean(false, "2");
		}
		if (BooleanUtils.isTrue(userinfo.getIslocked())) {
			addFailureCount(usercode, companyid);
			return new ResultBean(false, "4");
		}
		if (BooleanUtils.isNotTrue(userinfo.getIsvalid())) {
			addFailureCount(usercode, companyid);
			return new ResultBean(false, "5");
		}
		if (userinfo.getFPersonnel() != null) {
			if (BooleanUtils.isNotTrue(userinfo.getFPersonnel().getIsvalid())) {
				addFailureCount(usercode, companyid);
				return new ResultBean(false, "5");
			}
		}
		if (!MD5.MD5Encode(password + userinfo.getSalt()).equals(userinfo.getPassword())) {
			addFailureCount(usercode, companyid);
			return new ResultBean(false, "2");
		}
		if (systeminfo.needFailureWait()) {
			cleanFailureCount(userinfo.getUserid());
		}
		// 同一帐户不允许同时登录
		if (BooleanUtils.isNotTrue(systeminfo.getAllowloginagain())) {
			if (CommonUtils.is(invalidate)) {
				SessionUtils.invalidateOnlineUser(userinfo.getUserid());
			} else if (SessionUtils.isOnlineUser(userinfo.getUserid())) {
				return new ResultBean(false, "7");
			}
		}
		if (BooleanUtils.isTrue(systeminfo.getNeedreplaceinitialpassword())
				&& Constants.DEFAULT_PASSWORD.equals(password)) {
			return new ResultBean(true, Constants.NAVIGATEONE);
		} else {
			return new ResultBean(true, "");
		}
	}

	public FUserloginlog createLoginlog(String userid) {
		FUserloginlog loginlog = new FUserloginlog();
		loginlog.setFUser(dao.findById(FUser.class, userid));
		loginlog.setLogindate(new Date());
		loginlog.setIpaddress(CommonFunction.getIpAddr(Local.getRequest()));
		dao.save(loginlog);
		return loginlog;
	}

	public void writeLogout(FUserloginlog loginlog, String logouttype) {
		loginlog.setLogoutdate(new Date());
		loginlog.setLogouttype(logouttype);
		dao.update(loginlog);
	}

	@SystemLogs("查询登陆成功用户信息")
	public UserBean getUserInfo(String usercode) {

		FUser user = dao.findByPropertyFirst(FUser.class, Constants.USERCODE, usercode);
		UserBean userBean = new UserBean();

		userBean.setUsercode(user.getUsercode());
		userBean.setUserid(user.getUserid());
		userBean.setUsername(user.getUsername());
		userBean.setUsertype(user.getUsertype());
		userBean.setPasswordstrong(user.getAdditionstr5());
		userBean.setPersonnelid(user.getFPersonnel().getPersonnelid());
		userBean.setPersonnelcode(user.getFPersonnel().getPersonnelcode());
		userBean.setPersonnelname(user.getFPersonnel().getPersonnelname());
		userBean.setDepartmentid(user.getFPersonnel().getFOrganization().getOrgid());
		userBean.setDepartmentcode(user.getFPersonnel().getFOrganization().getOrgcode());
		userBean.setDepartmentname(user.getFPersonnel().getFOrganization().getOrgname());
		userBean.setCompanyid(user.getFPersonnel().getFOrganization().getFCompany().getCompanyid());
		userBean.setCompanyname(user.getFPersonnel().getFOrganization().getFCompany().getCompanyname());
		userBean.setCompanylongname(user.getFPersonnel().getFOrganization().getFCompany().getCompanylongname());
		user.getFUserroles().forEach(role -> {
			userBean.getRoleCodes().add(role.getFRole().getRolecode());
		});
		// 用户可选择的数据角色, 下面这种方式对于多用户登录的时候，同步可能有问题。但是用到的情况不多，暂时先不处理
		// 以后可以在改变可选数据角后，只在session中改变状态，而不是改变所有该用户登录的session。
		if (user.getFUserdatacanselectroles().size() > 0) {
			userBean.setCanselectdatarole(new ArrayList<UserCanSelectDataRole>());
			for (FUserdatacanselectrole role : user.getFUserdatacanselectroles()) {
				if (role.getFDatacanselectfilterrole().getIsvalid()) {
					UserCanSelectDataRole dataRole = new UserCanSelectDataRole(
							role.getFDatacanselectfilterrole().getRoleid(),
							role.getFDatacanselectfilterrole().getRolename(), BooleanUtils.isTrue(role.getChecked()));
					// 加入了当前role的 object,如果要加入父或子的，都可以放进来。选择条件生效可不生效时可以刷新指定模块的数据
					dataRole.getModuleNames().add(role.getFDatacanselectfilterrole().getFDataobject().getObjectname());
					userBean.getCanselectdatarole().add(dataRole);
				}
			}
			if (userBean.getCanselectdatarole().size() == 0) {
				userBean.setCanselectdatarole(null);
			}
		}
		return userBean;

		// String sql = "select a.userid,a.usercode,a.username,a.usertype, "
		// + " b.companyid,b.companyname,b.companylongname,b.levelid, "
		// + " c.personnelid,c.personnelcode,c.personnelname, "
		// + " d.orgid departmentid,d.OrgCode departmentcode,d.orgname departmentname "
		// + " from f_user a "
		// + " inner join f_company b on a.companyid = b.companyid "
		// + " left join F_Personnel c on a.personnelid = c.personnelid "
		// + " left join F_Organization d on c.orgid = d.orgid" + " where a.usercode =
		// '" + usercode
		// + "' and a.companyid = '" + companyid + "'";
		// List<UserBean> list = dao.executeSQLQuery(sql, UserBean.class);
		// return list.size() == 0 ? null : list.get(0);
	}

	public Map<String, Object> getSysteminfo(String companyid) {
		FCompany company = dao.findById(FCompany.class, companyid);
		FSysteminfo systeminfo = getCompanySystemInfo(companyid);
		Map<String, Object> cfg = new HashMap<String, Object>(0);
		Map<String, Object> companymap = new HashMap<String, Object>(0);
		companymap.put(Constants.COMPANYID, company.getCompanyid());
		companymap.put("companyname", company.getCompanyname());
		companymap.put("companylongname", company.getCompanylongname());
		companymap.put("address", company.getAddress());
		companymap.put("linkmen", company.getLinkmen());
		companymap.put("telnumber", company.getTelnumber());
		companymap.put("servicedepartment", company.getServicedepartment());
		companymap.put("servicemen", company.getServicemen());
		companymap.put("servicetelnumber", company.getServicetelnumber());
		companymap.put("serviceqq", company.getServiceqq());
		companymap.put("serviceemail", company.getServiceemail());
		companymap.put("servicehomepage", company.getServicehomepage());
		cfg.put("company", companymap);
		Map<String, Object> systeminfomap = new HashMap<String, Object>(0);
		systeminfomap.put("systemname", systeminfo.getSystemname());
		systeminfomap.put("systemshortname", systeminfo.getSystemshortname());
		systeminfomap.put("systemversion", systeminfo.getSystemversion());
		systeminfomap.put("systemkey", systeminfo.getSystemkey());
		systeminfomap.put("iconurl", systeminfo.getIconurl());
		systeminfomap.put("iconcls", systeminfo.getIconcls());
		systeminfomap.put("systemaddition", systeminfo.getSystemaddition());
		systeminfomap.put("copyrightowner", systeminfo.getCopyrightowner());
		systeminfomap.put("copyrightinfo", systeminfo.getCopyrightinfo());
		systeminfomap.put("allowsavepassword", systeminfo.getAllowsavepassword());
		systeminfomap.put("savepassworddays", systeminfo.getSavepassworddays());
		systeminfomap.put("needidentifingcode", systeminfo.getNeedidentifingcode());
		systeminfomap.put("alwaysneedidentifingcode", systeminfo.getAlwaysneedidentifingcode());
		systeminfomap.put("forgetpassword", systeminfo.getForgetpassword());
		// 加入系统附件属性中的值
		// disablePopupWindow=true 不弹出提醒窗口
		systeminfomap.put("disablePopupWindow", Boolean
				.parseBoolean(systemCommonService.getProperty("disablePopupWindow", systeminfo.getProperites())));
		// disableActiviti=true 系统中没有工作流
		systeminfomap.put("disableActiviti",
				Boolean.parseBoolean(systemCommonService.getProperty("disableActiviti", systeminfo.getProperites())));
		// disableThemeSelect=true 系统不允许选择皮肤方案
		systeminfomap.put("disableThemeSelect", Boolean
				.parseBoolean(systemCommonService.getProperty("disableThemeSelect", systeminfo.getProperites())));
		cfg.put("systeminfo", systeminfomap);
		Map<String, Object> loginsettingmap = new HashMap<String, Object>(0);
		loginsettingmap.put("allowsavepassword", systeminfo.getAllowsavepassword());
		loginsettingmap.put("needidentifingcode", systeminfo.getNeedidentifingcode());
		loginsettingmap.put("alwaysneedidentifingcode", systeminfo.getAlwaysneedidentifingcode());
		loginsettingmap.put("needreplaceinitialpassword", systeminfo.getNeedreplaceinitialpassword());
		loginsettingmap.put("loginslatkey", SM4KEY);
		cfg.put("loginsettinginfo", loginsettingmap);
		return cfg;
	}

	public FSysteminfo getCompanySystemInfo(String companyid) {
		FCompany company = dao.findById(FCompany.class, companyid);
		List<FSysteminfo> infos = new ArrayList<FSysteminfo>(company.getFSysteminfos());
		return infos.get(0);
	}

	/**
	 * 检验用户在指定时间内的登录失败次数，返回true表示锁定了, 在所有客户端同时错误会累加
	 * 
	 * @param session
	 * @param username
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean checkUserNameIsLock(String username, String companyid) {
		Object object = Local.getRequest().getServletContext().getAttribute(username + "_login_info");
		if (object == null) {
			return false;
		}
		HashMap<String, Object> map = (HashMap<String, Object>) object;
		Integer num = (Integer) map.get(Constants.COUNT);
		Date date = (Date) map.get("lastDate");
		long timeDifference = ((System.currentTimeMillis() - date.getTime()) / 60 / 1000);
		FSysteminfo systeminfo = getCompanySystemInfo(companyid);
		if (num >= systeminfo.getLoginmaxfailurecount() && timeDifference < systeminfo.getLoginfailurewaitminute()) {
			return true;
		}
		return false;
	}

	/**
	 * 新增用户登录失败次数
	 * 
	 * @param session
	 * @param username
	 */
	@SuppressWarnings("unchecked")
	public void addFailureCount(String username, String companyid) {
		FSysteminfo systeminfo = getCompanySystemInfo(companyid);
		if (systeminfo.needFailureWait()) {
			Object object = Local.getRequest().getServletContext().getAttribute(username + "_login_info");
			HashMap<String, Object> map = null;
			Integer count = 0;
			if (object == null) {
				map = new HashMap<String, Object>(0);
			} else {
				map = (HashMap<String, Object>) object;
				count = (Integer) map.get(Constants.COUNT);
				Date date = (Date) map.get("lastDate");
				long timeDifference = ((System.currentTimeMillis() - date.getTime()) / 60 / 1000);
				if (timeDifference >= systeminfo.getLoginfailurewaitminute()) {
					count = 0;
				}
			}
			map.put(Constants.COUNT, count + 1);
			map.put("lastDate", new Date());
			Local.getRequest().getServletContext().setAttribute(username + "_login_info", map);
		}
	}

	/**
	 * 清理用户登录失败的记录
	 * 
	 * @param session
	 * @param username
	 */
	public void cleanFailureCount(String username) {
		Local.getRequest().getServletContext().removeAttribute(username + "_login_info");
	}

}
