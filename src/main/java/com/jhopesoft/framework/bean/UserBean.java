package com.jhopesoft.framework.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.system.FOrganization;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;

/**
 * 
 * @author jiangfeng
 *
 */

public class UserBean implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 用户ID */
	private String userid;
	/** 用户代码 */
	private String usercode;
	/** 用户名称 */
	private String username;
	/** 用户类型 */
	private String usertype;
	/** 公司ID (本部) */
	private String companyid;
	/** 公司名称(本部) */
	private String companyname;
	/** 公司全称 */
	private String companylongname;
	/** 公司级别 */
	private String levelid;
	/** 部门ID */
	private String departmentid;
	/** 部门代码 */
	private String departmentcode;
	/** 部门名称 */
	private String departmentname;
	/** 上级部门id */
	private String parentdepartmentid;
	/** 人员ID */
	private String personnelid;
	/** 人员编号 */
	private String personnelcode;
	/** 人员姓名 */
	private String personnelname;
	/** 岗位 */
	private String position;
	/** 当前登录人员sessionid */
	private String sessionid;
	/** 项目上下文 */
	private String basepath;
	/** 密码强度 太短 弱 中 强 */
	private String passwordstrong;
	/** 当前用户需要重置密码 */
	private boolean needResetPassword;
	/** 当前用户的所有角色code */
	private List<String> roleCodes = new ArrayList<String>(0);
	/** 当前用户的可改变数据角色，包括 roleid,name,selected */
	private List<UserCanSelectDataRole> canselectdatarole;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCompanyid() {
		return companyid;
	}

	public void setCompanyid(String companyid) {
		this.companyid = companyid;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public String getCompanylongname() {
		return companylongname;
	}

	public void setCompanylongname(String companylongname) {
		this.companylongname = companylongname;
	}

	public String getDepartmentid() {
		return departmentid;
	}

	public void setDepartmentid(String departmentid) {
		this.departmentid = departmentid;
	}

	public String getParentdepartmentid() {
		if (parentdepartmentid == null) {
			FDataobject object = DataObjectUtils.getDataObject(FOrganization.class.getSimpleName());
			int len = departmentid.length();
			int level = 1;
			for (int i = 1; i <= Constants.INT_20; i++) {
				if (object._getCodeLevelLength(i) == len) {
					level = i;
					break;
				}
			}
			if (level == 1) {
				parentdepartmentid = "";
			} else {
				parentdepartmentid = departmentid.substring(0, object._getCodeLevelLength(level - 1));
			}
		}
		return parentdepartmentid;
	}

	public String getDepartmentcode() {
		return departmentcode;
	}

	public void setDepartmentcode(String departmentcode) {
		this.departmentcode = departmentcode;
	}

	public String getDepartmentname() {
		return departmentname;
	}

	public void setDepartmentname(String departmentname) {
		this.departmentname = departmentname;
	}

	public String getPersonnelid() {
		return personnelid;
	}

	public void setPersonnelid(String personnelid) {
		this.personnelid = personnelid;
	}

	public String getPersonnelname() {
		return personnelname;
	}

	public void setPersonnelname(String personnelname) {
		this.personnelname = personnelname;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getLevelid() {
		return levelid;
	}

	public void setLevelid(String levelid) {
		this.levelid = levelid;
	}

	public String getPersonnelcode() {
		return personnelcode;
	}

	public void setPersonnelcode(String personnelcode) {
		this.personnelcode = personnelcode;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getBasepath() {
		return basepath;
	}

	public void setBasepath(String basepath) {
		this.basepath = basepath;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String getPasswordstrong() {
		return passwordstrong;
	}

	public void setPasswordstrong(String passwordstrong) {
		this.passwordstrong = passwordstrong;
	}

	public boolean isNeedResetPassword() {
		return needResetPassword;
	}

	public void setNeedResetPassword(boolean needResetPassword) {
		this.needResetPassword = needResetPassword;
	}

	public List<String> getRoleCodes() {
		return roleCodes;
	}

	public void setRoleCodes(List<String> roleCodes) {
		this.roleCodes = roleCodes;
	}

	public List<UserCanSelectDataRole> getCanselectdatarole() {
		return canselectdatarole;
	}

	public void setCanselectdatarole(List<UserCanSelectDataRole> canselectdatarole) {
		this.canselectdatarole = canselectdatarole;
	}
}
