package com.jhopesoft.framework.bean;

import java.io.Serializable;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

public class ValueText implements Serializable {
	protected String value;
	protected String text;
	protected String remark;

	public ValueText() {

	}

	public ValueText(String value, String text) {
		super();
		this.value = value;
		this.setText(text);
	}
	
	public ValueText(String value, String text, String remark) {
		super();
		this.value = value;
		this.remark = remark;
		this.setText(text);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "ValueText [remark=" + remark + ", text=" + text + ", value=" + value + "]";
	}

}
