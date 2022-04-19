package com.jhopesoft.platform.controller;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jhopesoft.framework.bean.ResultBean;
import com.jhopesoft.framework.bean.UserBean;
import com.jhopesoft.framework.core.annotation.SystemLogs;
import com.jhopesoft.framework.dao.entity.log.FUserloginlog;
import com.jhopesoft.framework.dao.entity.system.FSysteminfo;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.Globals;
import com.jhopesoft.framework.utils.SessionUtils;
import com.jhopesoft.framework.utils.ValidateCode;
import com.jhopesoft.platform.service.LoginService;
import com.jhopesoft.platform.service.SystemFrameService;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@RestController
@RequestMapping("/login")
public class Login {

	@Resource
	private LoginService service;

	@Resource
	private SystemFrameService systemFrameService;

	public static final String VALIDATECODE = "validateCode";
	public static final String LOGINTIMES = "logintimes";
	public static String LOGOUTMODE = "userLogoutMethod";
	public static String LOGOUTMODESTAND = "正常退出";
	public static String LOGOUTMODETIMEOUT = "超时退出";

	@SystemLogs("用户登陆")
	@RequestMapping(value = "/validate.do")

	public ResultBean validate(HttpServletRequest request, HttpServletResponse response, String companyid,
			String usercode, String password, Boolean invalidate, String identifingcode) throws Exception {
		if (StringUtils.isBlank(companyid)) {
			companyid = Constants.ZEROZERO;
		}
		ResultBean result = service.login(companyid, usercode, password, invalidate, identifingcode);
		if (!result.isSuccess()) {
			return result;
		}
		UserBean bean = login(request, companyid, usercode);
		if (Constants.NAVIGATEONE.equals(result.getData().toString())) {
			// 需要用户重置密码
			bean.setNeedResetPassword(true);
		}
		result.setData(bean);
		return result;
	}

	private UserBean login(HttpServletRequest request, String companyid, String usercode) {
		HttpSession session = request.getSession();
		String sessionid = session.getId();
		FSysteminfo systeminfo = service.getCompanySystemInfo(companyid);
		Integer m = systeminfo.getSessiontimeoutminute();
		// session超时时间，单位秒，默认2小时
		session.setMaxInactiveInterval(60 * (m == null ? 60 * 2 : m));
		UserBean bean = service.getUserInfo(usercode);
		session.setAttribute(Globals.SYSTEM_USER, bean);
		session.setAttribute(Globals.LOGINLOG, service.createLoginlog(bean.getUserid()));
		bean.setSessionid(sessionid);
		bean.setBasepath(request.getContextPath());
		SessionUtils.SESSIONCONTEXT.put(sessionid, session);
		return bean;
	}

	@SystemLogs("获取登录用户对象")
	@RequestMapping(value = "/getuserbean.do")
	public UserBean getUserBean(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (UserBean) session.getAttribute(Globals.SYSTEM_USER);
	}

	@SystemLogs("用户登出")
	@RequestMapping(value = "/logout.do", method = RequestMethod.POST)

	public String logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		String sessionid = session.getId();
		service.writeLogout((FUserloginlog) session.getAttribute(Globals.LOGINLOG), "正常登出");
		session.removeAttribute(Globals.LOGINLOG);
		SessionUtils.SESSIONCONTEXT.remove(sessionid);
		session.invalidate();
		return Constants.TRUE;
	}

	/**
	 * 生成验证码
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/validatecode.do")

	public void generateValidateCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ValidateCode.generateCode(request, response, VALIDATECODE);
	}

	/**
	 * 系统打开获取初始化配置信息
	 */
	@RequestMapping(value = "/getsysteminfo.do")
	public Map<String, Object> getFsysteminfo(String companyid) {
		if (StringUtils.isBlank(companyid)) {
			companyid = Constants.ZEROZERO;
		}
		return service.getSysteminfo(companyid);
	}

	@RequestMapping(value = "/systemfavicon.do")

	public void getSystemFavicon() throws IOException {
		systemFrameService.getSystemFavicon();
	}

	@RequestMapping(value = "/getuserfavicon.do")

	public void getUserFavicon(String userid) throws IOException {
		systemFrameService.getUserFavicon(userid);
	}

	@RequestMapping(value = "/getbackground.do", method = RequestMethod.GET)

	public void getBackGround(String type, String themename) throws IOException {
		systemFrameService.getBackGround(type, themename);

	}

}
