package com.jhopesoft.framework.bean;

import java.io.Serializable;

import com.jhopesoft.framework.utils.Constants;

/**
 * 暂时不用 grid 的每一个field 的信息，存放每一个字段的id,fieldname , title
 * 
 * @author jiangfeng
 * 
 */
public class GridFieldInfo implements Serializable {

	public static final String GRIDFIELDINFO = "gridfieldinfo";

	private String groupName;
	private String fieldId;
	private String fieldname;
	private String fieldtype;
	private String valueFieldname;
	private String title;
	private Boolean allowSubTotal;
	/** 如果表头分组没有的话，则为true,表示写在excel里需要2行 */
	private Boolean twoRows;
	/** 字段单位 ，元，米，万元等等 */
	private String unitText;
	private boolean ismonetary;
	/** 百分比分子 */
	private String divisor;
	/** 百分比分母 */
	private String denominator;

	/** 是否是manytomany字段 */
	private boolean manyToMany = false;

	/** 当前字段在树形的结构中处在第几级，0--顶级，1--子级，2-孙级 */
	private int treeLevel;

	private boolean doubleType;
	private boolean percentType;
	private boolean intType;
	private boolean dateType;
	private boolean datetimeType;
	private boolean booleanType;
	private boolean blobType;

	public GridFieldInfo() {

	}

	public void setFieldtype(String fieldtype) {
		this.fieldtype = fieldtype;
		String type = fieldtype.toLowerCase();
		setDoubleType(Constants.DOUBLE.equals(type) || Constants.FLOAT.equals(type) || Constants.MONEY.equals(type));
		setPercentType(Constants.PERCENT.equals(type));
		setIntType(type.startsWith(Constants.INT));
		setDateType(Constants.DATE.equals(type));
		setDatetimeType(Constants.DATETIME.equals(type) || Constants.TIMESTAMP.equals(type));
		setBooleanType(type.startsWith("bool"));
		setBlobType("blob".equals(type) || "image".equals(type) || "file".equals(type));
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public String getFieldname() {
		return fieldname;
	}

	public String getFactFieldname() {
		if (valueFieldname != null) {
			return valueFieldname;
		} else {
			return fieldname;
		}
	}

	public String getDivisorKey() {
		return getFactFieldname() + "_divisor_key";
	}

	public String getDenominatorKey() {
		return getFactFieldname() + "_denominator_key";
	}

	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	public String getFieldtype() {
		return fieldtype;
	}

	public String getValueFieldname() {
		return valueFieldname;
	}

	public void setValueFieldname(String valueFieldname) {
		this.valueFieldname = valueFieldname;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	// 如果有分组，并且字段的前面字和 分组相同，那么就把相同的去掉
	// 如果分组是 ＡＡＡ，字段title AAABBB,那么title 改成 BBB

	public String getTitleAndUnitTextWithOutGroupName() {
		String result;
		if (unitText == null || unitText.length() == 0) {
			result = title.replaceAll("--", "");
		} else {
			result = title.replaceAll("--", "") + "\n(" + unitText + ")";
		}
		if (groupName != null && result != null) {
			if (result.indexOf(groupName) == 0) {
				result = result.substring(groupName.length());
			}
			if (result.length() == 0) {
				result = groupName;
			}
		}
		return result;
	}

	public String getTitleAndUnitText() {
		if (unitText == null || unitText.length() == 0) {
			return title.replaceAll("--", "");
		} else {
			return title.replaceAll("--", "") + "\n(" + unitText + ")";
		}
	}

	public Boolean getAllowSubTotal() {
		return allowSubTotal == null ? false : allowSubTotal;
	}

	public void setAllowSubTotal(Boolean allowSubTotal) {
		this.allowSubTotal = allowSubTotal;
	}

	public Boolean getTwoRows() {
		return twoRows;
	}

	public void setTwoRows(Boolean twoRows) {
		this.twoRows = twoRows;
	}

	public String getUnitText() {
		return unitText;
	}

	public void setUnitText(String unitText) {
		this.unitText = unitText;
	}

	public boolean isIsmonetary() {
		return ismonetary;
	}

	public void setIsmonetary(boolean ismonetary) {
		this.ismonetary = ismonetary;
	}

	public String getDivisor() {
		return divisor;
	}

	public void setDivisor(String divisor) {
		this.divisor = divisor;
	}

	public String getDenominator() {
		return denominator;
	}

	public void setDenominator(String denominator) {
		this.denominator = denominator;
	}

	@Override
	public String toString() {
		return "GridFieldInfo [groupName=" + groupName + ", fieldId=" + fieldId + ", fieldname=" + fieldname
				+ ", fieldtype=" + fieldtype + ", valueFieldname=" + valueFieldname + ", title=" + title
				+ ", allowSubTotal=" + allowSubTotal + ", twoRows=" + twoRows + ", unitText=" + unitText
				+ ", ismonetary=" + ismonetary + ", divisor=" + divisor + ", denominator=" + denominator + "]";
	}

	public boolean isManyToMany() {
		return manyToMany;
	}

	public void setManyToMany(boolean manyToMany) {
		this.manyToMany = manyToMany;
	}

	public int getTreeLevel() {
		return treeLevel;
	}

	public void setTreeLevel(int treeLevel) {
		this.treeLevel = treeLevel;
	}

	public boolean isDoubleType() {
		return doubleType;
	}

	public void setDoubleType(boolean doubleType) {
		this.doubleType = doubleType;
	}

	public boolean isPercentType() {
		return percentType;
	}

	public void setPercentType(boolean percentType) {
		this.percentType = percentType;
	}

	public boolean isIntType() {
		return intType;
	}

	public void setIntType(boolean intType) {
		this.intType = intType;
	}

	public boolean isDateType() {
		return dateType;
	}

	public void setDateType(boolean dateType) {
		this.dateType = dateType;
	}

	public boolean isDatetimeType() {
		return datetimeType;
	}

	public void setDatetimeType(boolean datetimeType) {
		this.datetimeType = datetimeType;
	}

	public boolean isBooleanType() {
		return booleanType;
	}

	public void setBooleanType(boolean booleanType) {
		this.booleanType = booleanType;
	}

	public boolean isBlobType() {
		return blobType;
	}

	public void setBlobType(boolean blobType) {
		this.blobType = blobType;
	}

}
