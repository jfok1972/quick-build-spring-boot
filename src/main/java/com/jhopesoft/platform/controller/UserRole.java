package com.jhopesoft.platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.TreeNode;
import com.jhopesoft.platform.service.UserRoleService;

/**
 * 
 * @author jiangfeng jfok1972@qq.com
 *
 */

@RestController
@RequestMapping("/platform/userrole")
public class UserRole {
	@Autowired
	private UserRoleService userRoleService;

	/**
	 * 返回用户的数据角色和操作角色
	 * 
	 * @param roleid
	 * @return
	 */
	@RequestMapping("/getuserroles.do")
	public  List<TreeNode> getUserRoles(String userid) {
		return userRoleService.getUserRoles(userid);

	}

	@RequestMapping("/getrolelimit.do")
	public  List<TreeNode> getRoleLimitTree(String roleid) {
		return userRoleService.getRoleLimitTree(roleid);

	}

	@RequestMapping("/saverolelimit.do")
	public  ActionResult saveRoleLimit(String roleid, String ids) {
		try {
			return userRoleService.saveRoleLimit(roleid, ids);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@RequestMapping("/getuserlimit.do")
	public  List<TreeNode> getUserLimitTree(String roleid, boolean addall, String userid) {
		return userRoleService.getUserLimitTree(userid == null ? roleid : userid, addall);

	}

	@RequestMapping("/getuseralllimit.do")
	public  List<TreeNode> getUserAllLimitTree(String roleid, boolean addall, String userid) {
		// 最早的名字搞错了，后来加了一个userid
		return userRoleService.getUserAllLimitTree(userid == null ? roleid : userid, addall);
	}

	@RequestMapping("/saveuserlimit.do")
	public  ActionResult saveUserLimit(String roleid, String ids, String userid) {
		try {
			return userRoleService.saveUserLimit(userid == null ? roleid : userid, ids);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 将模块附加功能加入到公司模块功能里。加入了之后才可以在角色的权限设置中进行设置。
	 * 
	 * @param functionid
	 * @return
	 */
	@RequestMapping("/updateadditionfunctiontocmodule.do")
	public  ActionResult updateAdditionFunctionToCmodule(String functionid) {
		return userRoleService.updateAdditionFunctionTocModule(functionid);

	}

}
