package com.jhopesoft.framework.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jhopesoft.framework.utils.Constants;

/**
 * 用于生成树的记录
 * 
 * @author jiangfeng
 *
 */
public class TreeNodeRecord implements Serializable {

	/** treeitem中显示的值 */
	private String text;
	/** 是否是叶节点 */
	private Boolean leaf;
	/** 此父节点是否展开 */
	private Boolean expanded;
	/** 此节点的icon */
	private String icon;
	/** */
	private String tooltip;
	/** 子节点数组 */
	private List<TreeNodeRecord> children;

	/** 这个筛选条件的模块名称，如果不是父模块筛选，那么就是本模块自己 */
	private String moduleName;
	/** 模块的别名 */
	private String tableAsName;

	private String parentId;
	/** 当前条件对应于被筛选模块的字段，如果是父模块，则是父模块的主键，如果是本模块，即为本模块的字段名 */
	private String fieldname;
	/** 字段的中文名称，需要加在显示条件的字符串中，还有打印的时候，也要加 */
	private String fieldtitle;
	/** 需要查询的字段值 */
	private String fieldvalue;
	/** 判断相等的方法 equals like 等 */
	private String equalsMethod;
	/** 原始的值 */
	private String nativeValue;
	/** 有多少条记录 */
	private Integer count;
	/** code 是不是分级的 */
	private Boolean isCodeLevel;
	/** 附加值 */
	private Integer tag;
	/** 聚合类型 */
	private String aggregationType;

	public TreeNodeRecord() {

	}

	public TreeNodeRecord(String moduleName, String tableAsName, String text, String fieldname, String fieldvalue,
			String equalsMethod, Boolean isCodeLevel) {
		super();
		this.moduleName = moduleName;
		this.tableAsName = tableAsName;
		if (text != null) {
			if (Constants.TRUE.equals(text)) {
				text = "已选中";
			}
			if (Constants.FALSE.equals(text)) {
				text = "未选中";
			}
		}
		this.leaf = true;
		this.expanded = true;
		this.text = text;
		this.fieldname = fieldname;
		this.fieldvalue = fieldvalue;
		this.equalsMethod = equalsMethod;
		this.nativeValue = this.fieldvalue;
		this.isCodeLevel = isCodeLevel;
		this.tag = -1;
	}

	public String getText() {
		// if (count != null && count > 0)
		// return text + "-" + count;
		// else
		//
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getIcon() {
		if (icon != null) {
			return "resources/images/module/" + icon + ".png";
		} else {
			return null;
		}
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getFieldtitle() {
		return fieldtitle;
	}

	public void setFieldtitle(String fieldtitle) {
		this.fieldtitle = fieldtitle;
	}

	public String getFieldname() {
		return fieldname;
	}

	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	public String getFieldvalue() {
		return fieldvalue;
	}

	public void setFieldvalue(String fieldvalue) {
		this.fieldvalue = fieldvalue;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public String getEqualsMethod() {
		return equalsMethod;
	}

	public void setEqualsMethod(String equalsMethod) {
		this.equalsMethod = equalsMethod;
	}

	public Boolean getLeaf() {
		return leaf == null ? false : leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	public String getNativeValue() {
		return nativeValue;
	}

	public void setNativeValue(String nativeValue) {
		this.nativeValue = nativeValue;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getTableAsName() {
		return tableAsName;
	}

	public void setTableAsName(String tableAsName) {
		this.tableAsName = tableAsName;
	}

	public Integer getTag() {
		return tag;
	}

	public void setTag(Integer tag) {
		this.tag = tag;
	}

	public Integer getCount() {
		return count == null ? 0 : count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Boolean getIsCodeLevel() {
		return isCodeLevel;
	}

	public void setIsCodeLevel(Boolean isCodeLevel) {
		this.isCodeLevel = isCodeLevel;
	}

	public List<TreeNodeRecord> getChildren() {
		if (children == null) {
			children = new ArrayList<TreeNodeRecord>();
		}
		return children;
	}

	public void setChildren(List<TreeNodeRecord> children) {
		this.children = children;
	}

	public Boolean hasChildren() {
		return (children != null && children.size() > 0);
	}

	public Boolean getExpanded() {
		return expanded;
	}

	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}

	public String getAggregationType() {
		return aggregationType;
	}

	public void setAggregationType(String aggregationType) {
		this.aggregationType = aggregationType;
	}

}
