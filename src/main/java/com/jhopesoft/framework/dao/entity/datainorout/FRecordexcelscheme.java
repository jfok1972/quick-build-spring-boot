package com.jhopesoft.framework.dao.entity.datainorout;
// default package

// Generated 2017-2-4 15:34:48 by Hibernate Tools 5.2.0.Beta1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.framework.utils.Constants;


/**
 *
 * @author 蒋锋
 * 
 */

@SuppressWarnings("serial")
@Entity
@DynamicUpdate
@Table(name = "f_recordexcelscheme")
public class FRecordexcelscheme implements java.io.Serializable {

	private String schemeid;
	private FDataobject FDataobject;
	private String title;
	private Boolean issystem;
	private Boolean allowrecords;
	private Boolean multisheet;
	private Integer startrow;
	private Integer endrow;
	private Integer orderno;
	private String iconurl;
	private String iconcls;
	private Boolean isdisable;
	private String stype;
	private String filename;
	private Integer filesize;
	private String author;
	private Date uploaddate;
	private byte[] filedata;
	private String othersetting;

	public FRecordexcelscheme() {
	}

	public FRecordexcelscheme(String schemeid, FDataobject FDataobject, String title) {
		this.schemeid = schemeid;
		this.FDataobject = FDataobject;
		this.title = title;
	}

	public FRecordexcelscheme(String schemeid, FDataobject FDataobject, String title, Boolean issystem, Integer orderno,
			String iconurl, String iconcls, Boolean isdisable, String stype, String filename, Integer filesize, String author,
			Date uploaddate, byte[] filedata, String othersetting) {
		this.schemeid = schemeid;
		this.FDataobject = FDataobject;
		this.title = title;
		this.issystem = issystem;
		this.orderno = orderno;
		this.iconurl = iconurl;
		this.iconcls = iconcls;
		this.isdisable = isdisable;
		this.stype = stype;
		this.filename = filename;
		this.filesize = filesize;
		this.author = author;
		this.uploaddate = uploaddate;
		this.filedata = filedata;
		this.othersetting = othersetting;
	}

	public JSONObject _getJsonData() {
		JSONObject result = new JSONObject();
		result.put("schemeid", schemeid);
		result.put(Constants.TITLE, title);
		result.put("iconurl", iconurl);
		result.put("iconcls", iconcls);
		// 只允许下载和打开pdf文件
		result.put("onlypdf", issystem); 
		return result;
	}

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")

	@Column(name = "schemeid", unique = true, nullable = false, length = 40)
	public String getSchemeid() {
		return this.schemeid;
	}

	public void setSchemeid(String schemeid) {
		this.schemeid = schemeid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "objectid", nullable = false)
	public FDataobject getFDataobject() {
		return this.FDataobject;
	}

	public void setFDataobject(FDataobject FDataobject) {
		this.FDataobject = FDataobject;
	}

	@Column(name = "title", nullable = false, length = 50)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "issystem")
	public Boolean getIssystem() {
		return this.issystem;
	}

	public void setIssystem(Boolean issystem) {
		this.issystem = issystem;
	}

	@Column(name = "allowrecords")

	public Boolean getAllowrecords() {
		return allowrecords;
	}

	public void setAllowrecords(Boolean allowrecords) {
		this.allowrecords = allowrecords;
	}

	@Column(name = "multisheet")

	public Boolean getMultisheet() {
		return multisheet;
	}

	public void setMultisheet(Boolean multisheet) {
		this.multisheet = multisheet;
	}

	@Column(name = "startrow")
	public Integer getStartrow() {
		return startrow;
	}

	public void setStartrow(Integer startrow) {
		this.startrow = startrow;
	}

	@Column(name = "endrow")
	public Integer getEndrow() {
		return endrow;
	}

	public void setEndrow(Integer endrow) {
		this.endrow = endrow;
	}

	@Column(name = "orderno")
	public Integer getOrderno() {
		return this.orderno;
	}

	public void setOrderno(Integer orderno) {
		this.orderno = orderno;
	}

	@Column(name = "iconurl", length = 200)
	public String getIconurl() {
		return this.iconurl;
	}

	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}

	@Column(name = "iconcls", length = 50)
	public String getIconcls() {
		return this.iconcls;
	}

	public void setIconcls(String iconcls) {
		this.iconcls = iconcls;
	}

	@Column(name = "isdisable")
	public Boolean getIsdisable() {
		return this.isdisable;
	}

	public void setIsdisable(Boolean isdisable) {
		this.isdisable = isdisable;
	}

	@Column(name = "stype", length = 20)
	public String getStype() {
		return this.stype;
	}

	public void setStype(String stype) {
		this.stype = stype;
	}

	@Column(name = "filename", length = 200)
	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Column(name = "filesize")
	public Integer getFilesize() {
		return this.filesize;
	}

	public void setFilesize(Integer filesize) {
		this.filesize = filesize;
	}

	@Column(name = "author", length = 40)
	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "uploaddate", length = 19)
	public Date getUploaddate() {
		return this.uploaddate;
	}

	public void setUploaddate(Date uploaddate) {
		this.uploaddate = uploaddate;
	}

	@Column(name = "filedata")
	public byte[] getFiledata() {
		return this.filedata;
	}

	public void setFiledata(byte[] filedata) {
		this.filedata = CommonUtils.emptyBytesToNull(filedata);
	}

	@Column(name = "othersetting", length = 200)
	public String getOthersetting() {
		return this.othersetting;
	}

	public void setOthersetting(String othersetting) {
		this.othersetting = othersetting;
	}

}
