package com.jhopesoft.framework.dao.entity.attachment;

import java.io.File;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.utils.CommonUtils;

/**
 *
 * @author 蒋锋
 * 
 */

@SuppressWarnings("serial")
@Entity
@DynamicUpdate
@Table(name = "f_dataobjectattachment")
public class FDataobjectattachment implements java.io.Serializable {
	public static final String COUNT = "attachmentcount";
	public static final String TOOLTIP = "attachmenttooltip";

	private String attachmentid;
	private FDataobject FDataobject;
	private FDataobjectfield FDataobjectfield;
	private String idvalue;
	private String titlevalue;
	private String title;
	private Integer orderno;
	private String ftype;
	private String atype;
	private String alevel;
	private String keyword;
	private String archivenumber;
	private String keeper;
	private String filename;
	private String suffixname;
	private Long filesize;
	private Boolean iscompressed;
	private Boolean isencryption;
	private String reducemode;
	private Integer pwidth;
	private Integer pheight;
	private String localpathname;
	private String localfilename;
	private Boolean hasimagepreviewdata;
	private Boolean haspdfpreviewviewdata;
	private String originalpreviewmode;
	private byte[] previewdata;
	private Integer downloadnum;
	private Integer viewnum;
	private String remark;
	private Date uploaddate;
	private String creater;
	private Date createdate;
	private String lastmodifier;
	private Date lastmodifydate;
	private FDataobjectattachmentfile FDataobjectattachmentfile;
	private FDataobjectattachmentpdffile FDataobjectattachmentpdffile;

	public FDataobjectattachment() {
	}

	public FDataobjectattachment(String attachmentid, FDataobject FDataobject, String idvalue, String creater,
			Date createdate) {
		this.attachmentid = attachmentid;
		this.FDataobject = FDataobject;
		this.idvalue = idvalue;
		this.creater = creater;
		this.createdate = createdate;
	}

	public FDataobjectattachment(String attachmentid, FDataobject FDataobject, FDataobjectfield FDataobjectfield,
			String idvalue, String titlevalue, String title, Integer orderno, String ftype, String atype, String alevel,
			String keyword, String archivenumber, String keeper, String filename, String suffixname, Long filesize,
			Boolean iscompressed, Boolean isencryption, String reducemode, Integer pwidth, Integer pheight,
			String localpathname, String localfilename, Boolean hasimagepreviewdata, Boolean haspdfpreviewviewdata,
			byte[] previewdata, Integer downloadnum, Integer viewnum, String remark, Date uploaddate, String creater,
			Date createdate, String lastmodifier, Date lastmodifydate,
			FDataobjectattachmentfile FDataobjectattachmentfile,
			FDataobjectattachmentpdffile FDataobjectattachmentpdffile) {
		this.attachmentid = attachmentid;
		this.FDataobject = FDataobject;
		this.FDataobjectfield = FDataobjectfield;
		this.idvalue = idvalue;
		this.titlevalue = titlevalue;
		this.title = title;
		this.orderno = orderno;
		this.ftype = ftype;
		this.atype = atype;
		this.alevel = alevel;
		this.keyword = keyword;
		this.archivenumber = archivenumber;
		this.keeper = keeper;
		this.filename = filename;
		this.suffixname = suffixname;
		this.filesize = filesize;
		this.iscompressed = iscompressed;
		this.isencryption = isencryption;
		this.reducemode = reducemode;
		this.pwidth = pwidth;
		this.pheight = pheight;
		this.localpathname = localpathname;
		this.localfilename = localfilename;
		this.hasimagepreviewdata = hasimagepreviewdata;
		this.haspdfpreviewviewdata = haspdfpreviewviewdata;
		this.previewdata = previewdata;
		this.downloadnum = downloadnum;
		this.viewnum = viewnum;
		this.remark = remark;
		this.uploaddate = uploaddate;
		this.creater = creater;
		this.createdate = createdate;
		this.lastmodifier = lastmodifier;
		this.lastmodifydate = lastmodifydate;
		this.FDataobjectattachmentfile = FDataobjectattachmentfile;
		this.FDataobjectattachmentpdffile = FDataobjectattachmentpdffile;
	}

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@Column(name = "attachmentid", unique = true, nullable = false, length = 40)
	public String getAttachmentid() {
		return this.attachmentid;
	}

	public void setAttachmentid(String attachmentid) {
		this.attachmentid = attachmentid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "objectid", nullable = false)
	public FDataobject getFDataobject() {
		return this.FDataobject;
	}

	public void setFDataobject(FDataobject FDataobject) {
		this.FDataobject = FDataobject;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fieldid")
	public FDataobjectfield getFDataobjectfield() {
		return this.FDataobjectfield;
	}

	public void setFDataobjectfield(FDataobjectfield FDataobjectfield) {
		this.FDataobjectfield = FDataobjectfield;
	}

	@Column(name = "idvalue", nullable = false, length = 40)
	public String getIdvalue() {
		return this.idvalue;
	}

	public void setIdvalue(String idvalue) {
		this.idvalue = idvalue;
	}

	@Column(name = "titlevalue", length = 200)
	public String getTitlevalue() {
		return this.titlevalue;
	}

	public void setTitlevalue(String titlevalue) {
		this.titlevalue = titlevalue;
	}

	@Column(name = "title", length = 200)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "orderno")
	public Integer getOrderno() {
		return this.orderno;
	}

	public void setOrderno(Integer orderno) {
		this.orderno = orderno;
	}

	@Column(name = "ftype", length = 40)
	public String getFtype() {
		return this.ftype;
	}

	public void setFtype(String ftype) {
		this.ftype = ftype;
	}

	@Column(name = "atype", length = 40)
	public String getAtype() {
		return this.atype;
	}

	public void setAtype(String atype) {
		this.atype = atype;
	}

	@Column(name = "alevel", length = 50)
	public String getAlevel() {
		return this.alevel;
	}

	public void setAlevel(String alevel) {
		this.alevel = alevel;
	}

	@Column(name = "keyword", length = 200)
	public String getKeyword() {
		return this.keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@Column(name = "archivenumber", length = 50)
	public String getArchivenumber() {
		return this.archivenumber;
	}

	public void setArchivenumber(String archivenumber) {
		this.archivenumber = archivenumber;
	}

	@Column(name = "keeper", length = 50)
	public String getKeeper() {
		return this.keeper;
	}

	public void setKeeper(String keeper) {
		this.keeper = keeper;
	}

	@Column(name = "filename", length = 200)
	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Column(name = "suffixname", length = 20)
	public String getSuffixname() {
		return this.suffixname;
	}

	public void setSuffixname(String suffixname) {
		this.suffixname = suffixname;
	}

	@Column(name = "filesize")
	public Long getFilesize() {
		return this.filesize;
	}

	public void setFilesize(Long filesize) {
		this.filesize = filesize;
	}

	@Column(name = "iscompressed")
	public Boolean getIscompressed() {
		return this.iscompressed;
	}

	public void setIscompressed(Boolean iscompressed) {
		this.iscompressed = iscompressed;
	}

	@Column(name = "isencryption")
	public Boolean getIsencryption() {
		return this.isencryption;
	}

	public void setIsencryption(Boolean isencryption) {
		this.isencryption = isencryption;
	}

	@Column(name = "reducemode", length = 20)
	public String getReducemode() {
		return this.reducemode;
	}

	public void setReducemode(String reducemode) {
		this.reducemode = reducemode;
	}

	@Column(name = "pwidth")
	public Integer getPwidth() {
		return this.pwidth;
	}

	public void setPwidth(Integer pwidth) {
		this.pwidth = pwidth;
	}

	@Column(name = "pheight")
	public Integer getPheight() {
		return this.pheight;
	}

	public void setPheight(Integer pheight) {
		this.pheight = pheight;
	}

	public String _getLocalFilename() {
		return StringUtils.isNotBlank(this.localpathname)
				? File.separator + this.localpathname + File.separator + this.localfilename
				: File.separator + this.localfilename;
	}

	public String _getLocalPDFFilename() {
		return StringUtils.isNotBlank(this.localpathname)
				? File.separator + this.localpathname + File.separator + this.localfilename + "_pdf.pdf"
				: File.separator + this.localfilename + "_pdf.pdf";
	}

	@Column(name = "localpathname", length = 200)
	public String getLocalpathname() {
		return this.localpathname;
	}

	public void setLocalpathname(String localpathname) {
		this.localpathname = localpathname;
	}

	@Column(name = "localfilename", length = 200)
	public String getLocalfilename() {
		return this.localfilename;
	}

	public void setLocalfilename(String localfilename) {
		this.localfilename = localfilename;
	}

	@Column(name = "hasimagepreviewdata")
	public Boolean getHasimagepreviewdata() {
		return this.hasimagepreviewdata;
	}

	public void setHasimagepreviewdata(Boolean hasimagepreviewdata) {
		this.hasimagepreviewdata = hasimagepreviewdata;
	}

	@Column(name = "haspdfpreviewviewdata")
	public Boolean getHaspdfpreviewviewdata() {
		return this.haspdfpreviewviewdata;
	}

	public void setHaspdfpreviewviewdata(Boolean haspdfpreviewviewdata) {
		this.haspdfpreviewviewdata = haspdfpreviewviewdata;
	}

	@Column(name = "originalpreviewmode", length = 20)
	public String getOriginalpreviewmode() {
		return this.originalpreviewmode;
	}

	public void setOriginalpreviewmode(String originalpreviewmode) {
		this.originalpreviewmode = originalpreviewmode;
	}

	@Column(name = "previewdata")
	public byte[] getPreviewdata() {
		return this.previewdata;
	}

	public void setPreviewdata(byte[] previewdata) {
		this.previewdata = CommonUtils.emptyBytesToNull(previewdata);
	}

	@Column(name = "downloadnum")
	public Integer getDownloadnum() {
		return this.downloadnum;
	}

	public void setDownloadnum(Integer downloadnum) {
		this.downloadnum = downloadnum;
	}

	@Column(name = "viewnum")
	public Integer getViewnum() {
		return this.viewnum;
	}

	public void setViewnum(Integer viewnum) {
		this.viewnum = viewnum;
	}

	@Column(name = "remark", length = 200)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "uploaddate", length = 19)
	public Date getUploaddate() {
		return this.uploaddate;
	}

	public void setUploaddate(Date uploaddate) {
		this.uploaddate = uploaddate;
	}

	@Column(name = "creater", nullable = false, length = 40)
	public String getCreater() {
		return this.creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdate", nullable = false, length = 19)
	public Date getCreatedate() {
		return this.createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	@Column(name = "lastmodifier", length = 40)
	public String getLastmodifier() {
		return this.lastmodifier;
	}

	public void setLastmodifier(String lastmodifier) {
		this.lastmodifier = lastmodifier;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "lastmodifydate", length = 19)
	public Date getLastmodifydate() {
		return this.lastmodifydate;
	}

	public void setLastmodifydate(Date lastmodifydate) {
		this.lastmodifydate = lastmodifydate;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "FDataobjectattachment")
	public FDataobjectattachmentfile getFDataobjectattachmentfile() {
		return this.FDataobjectattachmentfile;
	}

	public void setFDataobjectattachmentfile(FDataobjectattachmentfile FDataobjectattachmentfile) {
		this.FDataobjectattachmentfile = FDataobjectattachmentfile;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "FDataobjectattachment")
	public FDataobjectattachmentpdffile getFDataobjectattachmentpdffile() {
		return this.FDataobjectattachmentpdffile;
	}

	public void setFDataobjectattachmentpdffile(FDataobjectattachmentpdffile FDataobjectattachmentpdffile) {
		this.FDataobjectattachmentpdffile = FDataobjectattachmentpdffile;
	}

}
