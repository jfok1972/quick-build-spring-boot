package com.jhopesoft.framework.bean;

import java.io.Serializable;

/**
 * 
 * 用户登录后弹出式窗口中弹出的内容的类
 * 
 * @author jiangfeng
 *
 */
public class PopupMessage implements Serializable {

	/**
	 * 优先级，顺序号
	 */
	private int orderno;

	/**
	 * 模块名称
	 */
	private String moduleName;

	private String viewSchemeName;

	/**
	 * 需要的筛选的字段名称
	 */
	private String filterFieldName;

	/**
	 * 操作符
	 */
	private String filterFieldOperator;

	/**
	 * 需要的筛选的字段值
	 */
	private String filterFieldValue = "=";

	/**
	 * 筛选的条件文字说明，放在 模块后面比如 还本计划(一个月之内)
	 */
	private String filterText;

	/**
	 * 图标类型 info,warn,error
	 */
	private String hintlevel;

	/**
	 * 标题文字
	 */
	private String header;

	/**
	 * 弹出信息
	 */
	private String message;

	/**
	 * 操作
	 */
	private String action;

	/**
	 * 不允许打开模块
	 */
	private boolean disableOpenModule;
	
	/**
	 * 记录数
	 */
	private Integer count;

	public PopupMessage() {

	}

	public int getOrderno() {
		return orderno;
	}

	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getFilterFieldName() {
		return filterFieldName;
	}

	public void setFilterFieldName(String filterFieldName) {
		this.filterFieldName = filterFieldName;
	}

	public String getFilterFieldValue() {
		return filterFieldValue;
	}

	public void setFilterFieldValue(String filterFieldValue) {
		this.filterFieldValue = filterFieldValue;
	}

	public String getViewSchemeName() {
		return viewSchemeName;
	}

	public void setViewSchemeName(String viewSchemeName) {
		this.viewSchemeName = viewSchemeName;
	}

	public String getFilterText() {
		return filterText;
	}

	public void setFilterText(String filterText) {
		this.filterText = filterText;
	}

	public String getHintlevel() {
		return hintlevel;
	}

	public void setHintlevel(String hintlevel) {
		this.hintlevel = hintlevel;
	}

	public boolean isDisableOpenModule() {
		return disableOpenModule;
	}

	public void setDisableOpenModule(boolean disableOpenModule) {
		this.disableOpenModule = disableOpenModule;
	}

	public String getFilterFieldOperator() {
		return filterFieldOperator;
	}

	public void setFilterFieldOperator(String filterFieldOperator) {
		this.filterFieldOperator = filterFieldOperator;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
