package com.jhopesoft.framework.bean;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

public class ExcelExportSetting {
	private boolean colorless;
	private boolean usemonetary;
	private Integer monetaryUnit;
	private String monetaryText;
	private boolean sumless;
	private boolean unitalone;
	private String pagesize;
	private boolean autofitwidth;
	private short scale;

	public ExcelExportSetting() {

	}

	public ExcelExportSetting(boolean colorless, boolean usemonetary, Integer monetaryUnit, String monetaryText,
			boolean sumless, boolean unitalone, String pagesize, boolean autofitwidth, short scale) {
		super();
		this.colorless = colorless;
		this.usemonetary = usemonetary;
		this.monetaryUnit = monetaryUnit;
		this.monetaryText = monetaryText;
		this.sumless = sumless;
		this.unitalone = unitalone;
		this.pagesize = pagesize;
		this.autofitwidth = autofitwidth;
		this.scale = scale;
	}

	@Override
	public String toString() {
		return "ExcelExportSetting [colorless=" + colorless + ", usemonetary=" + usemonetary + ", monetaryUnit="
				+ monetaryUnit + ", monetaryText=" + monetaryText + ", sumless=" + sumless + ", unitalone=" + unitalone
				+ ", pagesize=" + pagesize + ", autofitwidth=" + autofitwidth + ", scale=" + scale + "]";
	}

	public boolean isColorless() {
		return colorless;
	}

	public void setColorless(boolean colorless) {
		this.colorless = colorless;
	}

	public boolean isUsemonetary() {
		return usemonetary;
	}

	public void setUsemonetary(boolean usemonetary) {
		this.usemonetary = usemonetary;
	}

	public Integer getMonetaryUnit() {
		return monetaryUnit;
	}

	public void setMonetaryUnit(Integer monetaryUnit) {
		this.monetaryUnit = monetaryUnit;
	}

	public String getMonetaryText() {
		return monetaryText;
	}

	public void setMonetaryText(String monetaryText) {
		this.monetaryText = monetaryText;
	}

	public boolean isSumless() {
		return sumless;
	}

	public void setSumless(boolean sumless) {
		this.sumless = sumless;
	}

	public boolean isUnitalone() {
		return unitalone;
	}

	public void setUnitalone(boolean unitalone) {
		this.unitalone = unitalone;
	}

	public String getPagesize() {
		return pagesize;
	}

	public void setPagesize(String pagesize) {
		this.pagesize = pagesize;
	}

	public boolean isAutofitwidth() {
		return autofitwidth;
	}

	public void setAutofitwidth(boolean autofitwidth) {
		this.autofitwidth = autofitwidth;
	}

	public short getScale() {
		return scale;
	}

	public void setScale(short scale) {
		this.scale = scale;
	}

}
