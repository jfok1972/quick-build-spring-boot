package com.jhopesoft.framework.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

public class UserCanSelectDataRole implements Serializable {

	private String roleId;
	private String roleName;
	private boolean checked;
	/** 当前条件影响的所有模块 */
	private List<String> moduleNames = new ArrayList<String>();

	public UserCanSelectDataRole() {

	}

	public UserCanSelectDataRole(String roleId, String roleName, boolean checked) {
		super();
		this.roleId = roleId;
		this.roleName = roleName;
		this.checked = checked;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public List<String> getModuleNames() {
		return moduleNames;
	}

	public void setModuleNames(List<String> moduleNames) {
		this.moduleNames = moduleNames;
	}

}
