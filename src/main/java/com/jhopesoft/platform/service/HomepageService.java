package com.jhopesoft.platform.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.favorite.FovUserhomepagescheme;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.dao.entity.viewsetting.FovHomepagescheme;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Service
public class HomepageService {

	@Autowired
	private DaoImpl dao;

	public static final String QUERY_FUNCTION_SQL = "SELECT DISTINCT bf.fcode AS \"fcode\" FROM f_modulefunction mf"
			+ "        INNER JOIN f_rolefunctionlimit rfl ON rfl.functionid = mf.functionid"
			+ "        INNER JOIN f_role role_ ON role_.roleid = rfl.roleid"
			+ "        INNER JOIN f_userrole ur ON ur.roleid = role_.roleid"
			+ "        INNER JOIN f_companymodule cm ON cm.cmoduleid = mf.cmoduleid"
			+ "        INNER JOIN f_module m ON m.moduleid = cm.moduleid"
			+ "        INNER JOIN fov_homepagescheme do_ ON do_.homepageschemeid = m.homepageschemeid"
			+ "        INNER JOIN f_dataobjectbasefuncion bf ON bf.basefunctionid = mf.basefunctionid AND bf.fcode = 'query'"
			+ "WHERE bf.isdisable = 0 AND role_.isvalid = 1 AND mf.isvalid = 1 AND bf.basefunctionid IS NOT NULL"
			+ "        AND ur.userid = ?0 AND do_.homepageschemeid = ?1 "
			+ "UNION SELECT DISTINCT bf.fcode FROM f_modulefunction mf"
			+ "        INNER JOIN f_userfunctionlimit rfl ON rfl.functionid = mf.functionid"
			+ "        INNER JOIN f_companymodule cm ON cm.cmoduleid = mf.cmoduleid"
			+ "        INNER JOIN f_module m ON m.moduleid = cm.moduleid"
			+ "        INNER JOIN fov_homepagescheme do_ ON do_.homepageschemeid = m.homepageschemeid"
			+ "        INNER JOIN f_dataobjectbasefuncion bf ON bf.basefunctionid = mf.basefunctionid AND bf.fcode = 'query'"
			+ "WHERE bf.isdisable = 0 AND mf.isvalid = 1 AND bf.basefunctionid IS NOT NULL AND rfl.userid = ?2 "
			+ "        AND do_.homepageschemeid = ?3 ";

	private boolean isQueryHomePageScheme(String schemeid) {
		List<Map<String, Object>> fcodes = Local.getDao().executeSQLQuery(QUERY_FUNCTION_SQL, Local.getUserid(),
				schemeid, Local.getUserid(), schemeid);
		// 如果大于0，表示是当前的主页方案在权限中有query
		return fcodes.size() > 0;
	}

	/**
	 * 取得用户的所有主页方案，有显示权限
	 * 
	 * 主页方案的顺序：用户默认的第一个，其他有权限的，都加进去。无权限的不加
	 * 
	 */
	public List<FovHomepagescheme> getHomepageInfo(String type) {
		List<FovHomepagescheme> result = new ArrayList<FovHomepagescheme>();

		// 加入没有设置的，并且是无用户的，即为系统默认的,没有考虑到权限。
		List<FovHomepagescheme> schemes = dao.findAll(FovHomepagescheme.class);
		schemes.sort(new Comparator<FovHomepagescheme>() {
			@Override
			public int compare(FovHomepagescheme o1, FovHomepagescheme o2) {
				return (int) o1.getOrderno() - (int) o2.getOrderno();
			}
		});
		for (FovHomepagescheme scheme : schemes) {
			// if (scheme.getFUser() == null) {
			boolean found = false;
			for (FovHomepagescheme s : result) {
				if (s == scheme) {
					found = true;
					break;
				}
			}
			if (!found) {
				if (isQueryHomePageScheme(scheme.getHomepageschemeid())
						&& scheme.getFovHomepageschemedetails().size() > 0) {
					if (type == null || type.equals(scheme.getHomepagetype())) {
						result.add(scheme);
					}
				}
			}
		}
		return result;

	}

	/**
	 * 设置用户的缺省主页方案中的方案值
	 * 
	 * @param schemeid
	 * @return
	 */
	public ActionResult setUserDefault(String schemeid) {
		FUser user = dao.findById(FUser.class, Local.getUserid());
		// 如果当前方案没有加到该用户的主页方案中，则先加入
		boolean found = false;
		for (FovUserhomepagescheme userscheme : user.getFovUserhomepageschemes()) {
			if (schemeid.equals(userscheme.getFovHomepagescheme().getHomepageschemeid())) {
				found = true;
				break;
			}
		}
		if (!found) {
			FovUserhomepagescheme userscheme = new FovUserhomepagescheme(user,
					dao.findById(FovHomepagescheme.class, schemeid));
			dao.save(userscheme);
			user.getFovUserhomepageschemes().add(userscheme);
		}
		for (FovUserhomepagescheme userscheme : user.getFovUserhomepageschemes()) {
			FovHomepagescheme scheme = userscheme.getFovHomepagescheme();
			if (schemeid.equals(scheme.getHomepageschemeid())) {
				if (BooleanUtils.isNotTrue(userscheme.getIsdefault())) {
					userscheme.setIsdefault(true);
					dao.update(userscheme);
				}
			} else {
				if (BooleanUtils.isNotFalse(userscheme.getIsdefault())) {
					userscheme.setIsdefault(false);
					dao.update(userscheme);
				}
			}
		}
		return new ActionResult();
	}

	public ActionResult remove(String schemeid) {
		FUser user = dao.findById(FUser.class, Local.getUserid());
		for (FovUserhomepagescheme userscheme : user.getFovUserhomepageschemes()) {
			if (userscheme.getFovHomepagescheme().getHomepageschemeid().equals(schemeid)) {
				dao.delete(userscheme);
				break;
			}
		}
		return new ActionResult();
	}

	public ActionResult add(String schemeid) {
		ActionResult result = new ActionResult();
		boolean found = false;
		FovHomepagescheme scheme = dao.findById(FovHomepagescheme.class, schemeid);
		result.setMsg(scheme.getSchemename());
		FUser user = dao.findById(FUser.class, Local.getUserid());
		for (FovUserhomepagescheme userscheme : user.getFovUserhomepageschemes()) {
			if (userscheme.getFovHomepagescheme() == scheme) {
				found = true;
				break;
			}
		}
		if (!found) {
			FovUserhomepagescheme fovUserhomepagescheme = new FovUserhomepagescheme();
			fovUserhomepagescheme.setFUser(user);
			fovUserhomepagescheme.setFovHomepagescheme(scheme);
			dao.save(fovUserhomepagescheme);
		}
		return result;
	}
}
