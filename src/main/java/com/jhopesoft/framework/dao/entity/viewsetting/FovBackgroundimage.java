package com.jhopesoft.framework.dao.entity.viewsetting;
// default package

// Generated 2018-3-28 11:55:55 by Hibernate Tools 5.2.6.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.jhopesoft.framework.utils.CommonUtils;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@SuppressWarnings("serial")
@Entity
@DynamicUpdate
@Table(name = "fov_backgroundimage")
public class FovBackgroundimage implements java.io.Serializable {

	private String backgroundid;
	private String title;
	private String positiontype;
	private String usetype;
	private String usevalue;
	private String themename;
	private String fieldtype;
	private Boolean disabled;
	private String rgbcolor;
	private byte[] imagefile;
	private String othersetting;
	private String remark;

	public FovBackgroundimage() {
	}

	public FovBackgroundimage(String backgroundid, String title, String positiontype) {
		this.backgroundid = backgroundid;
		this.title = title;
		this.positiontype = positiontype;
	}

	public FovBackgroundimage(String backgroundid, String title, String positiontype, String usetype, String usevalue,
			String themename, String fieldtype, Boolean disabled, String rgbcolor, byte[] imagefile, String othersetting,
			String remark) {
		this.backgroundid = backgroundid;
		this.title = title;
		this.positiontype = positiontype;
		this.usetype = usetype;
		this.usevalue = usevalue;
		this.themename = themename;
		this.fieldtype = fieldtype;
		this.disabled = disabled;
		this.rgbcolor = rgbcolor;
		this.imagefile = imagefile;
		this.othersetting = othersetting;
		this.remark = remark;
	}

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@Column(name = "backgroundid", unique = true, nullable = false, length = 40)
	public String getBackgroundid() {
		return this.backgroundid;
	}

	public void setBackgroundid(String backgroundid) {
		this.backgroundid = backgroundid;
	}

	@Column(name = "title", nullable = false, length = 50)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "positiontype", nullable = false, length = 10)
	public String getPositiontype() {
		return this.positiontype;
	}

	public void setPositiontype(String positiontype) {
		this.positiontype = positiontype;
	}

	@Column(name = "usetype", length = 10)
	public String getUsetype() {
		return this.usetype;
	}

	public void setUsetype(String usetype) {
		this.usetype = usetype;
	}

	@Column(name = "usevalue", length = 50)
	public String getUsevalue() {
		return this.usevalue;
	}

	public void setUsevalue(String usevalue) {
		this.usevalue = usevalue;
	}

	@Column(name = "themename", length = 50)
	public String getThemename() {
		return this.themename;
	}

	public void setThemename(String themename) {
		this.themename = themename;
	}

	@Column(name = "fieldtype", length = 20)
	public String getFieldtype() {
		return this.fieldtype;
	}

	public void setFieldtype(String fieldtype) {
		this.fieldtype = fieldtype;
	}

	@Column(name = "disabled")
	public Boolean getDisabled() {
		return this.disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	@Column(name = "rgbcolor", length = 50)
	public String getRgbcolor() {
		return this.rgbcolor;
	}

	public void setRgbcolor(String rgbcolor) {
		this.rgbcolor = rgbcolor;
	}

	@Column(name = "imagefile")
	public byte[] getImagefile() {
		return this.imagefile;
	}

	public void setImagefile(byte[] imagefile) {
		this.imagefile = CommonUtils.emptyBytesToNull(imagefile);
	}

	@Column(name = "othersetting", length = 200)
	public String getOthersetting() {
		return this.othersetting;
	}

	public void setOthersetting(String othersetting) {
		this.othersetting = othersetting;
	}

	@Column(name = "remark", length = 200)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
