package com.jhopesoft.framework.dao.entity.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.jhopesoft.framework.dao.entityinterface.AdditionFieldAbstract;
import com.jhopesoft.framework.utils.CommonUtils;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */
@SuppressWarnings("serial")
@Entity
@DynamicUpdate
@Table(name = "f_systeminfo", uniqueConstraints = @UniqueConstraint(columnNames = "companyid"))
@Cache(region = "beanCache", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class FSysteminfo extends AdditionFieldAbstract {

	private String systeminfoid;
	private FCompany FCompany;
	private String systemname;
	private String systemshortname;
	private String systemversion;
	private String systemkey;
	private String iconurl;
	private String iconcls;
	private byte[] iconfile;
	private String systemaddition;
	private String copyrightowner;
	private String copyrightinfo;
	private Boolean allowsavepassword;
	private Integer savepassworddays;
	private Boolean needreplaceinitialpassword;
	private Boolean needidentifingcode;
	private Boolean alwaysneedidentifingcode;
	private String forgetpassword;
	private Boolean allowloginagain;

	private Integer loginmaxfailurecount;
	private Integer loginfailurewaitminute;
	private Integer changepassworddays;

	private Integer maxusers;
	private Integer sessiontimeoutminute;

	private boolean saveinfilesystem;
	private String rootpath;
	private String pathmode;
	private Integer filemaxsize;
	private boolean createpreviewimage;
	private boolean createpreviewpdf;
	private Integer imagewidth;
	private Integer imageheight;

	private String backupfilename;
	private String properites;
	private String systemlicense;
	private String datamininglicense;
	private String otherlicense;

	private String remark;

	public FSysteminfo() {
	}

	/**
	 * 是否需要输错误几次密码后锁定帐号一段时间
	 * 
	 * @return
	 */
	public boolean needFailureWait() {
		return loginmaxfailurecount != null && loginfailurewaitminute != null && loginmaxfailurecount > 0
				&& loginfailurewaitminute > 0;
	}

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@Column(name = "systeminfoid", unique = true, nullable = false, length = 40)
	public String getSysteminfoid() {
		return this.systeminfoid;
	}

	public void setSysteminfoid(String systeminfoid) {
		this.systeminfoid = systeminfoid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "companyid", unique = true, nullable = false)
	public FCompany getFCompany() {
		return this.FCompany;
	}

	public void setFCompany(FCompany FCompany) {
		this.FCompany = FCompany;
	}

	@Column(name = "systemname", length = 50)
	public String getSystemname() {
		return this.systemname;
	}

	public void setSystemname(String systemname) {
		this.systemname = systemname;
	}

	@Column(name = "systemshortname", length = 50)
	public String getSystemshortname() {
		return systemshortname;
	}

	public void setSystemshortname(String systemshortname) {
		this.systemshortname = systemshortname;
	}

	@Column(name = "systemversion", length = 50)
	public String getSystemversion() {
		return this.systemversion;
	}

	public void setSystemversion(String systemversion) {
		this.systemversion = systemversion;
	}

	@Column(name = "systemkey", length = 50)
	public String getSystemkey() {
		return systemkey;
	}

	public void setSystemkey(String systemkey) {
		this.systemkey = systemkey;
	}

	@Column(name = "iconurl", length = 50)
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

	@Column(name = "iconfile")
	public byte[] getIconfile() {
		return this.iconfile;
	}

	public void setIconfile(byte[] iconfile) {
		this.iconfile = CommonUtils.emptyBytesToNull(iconfile);
	}

	@Column(name = "systemaddition", length = 200)
	public String getSystemaddition() {
		return this.systemaddition;
	}

	public void setSystemaddition(String systemaddition) {
		this.systemaddition = systemaddition;
	}

	@Column(name = "copyrightowner", length = 50)
	public String getCopyrightowner() {
		return this.copyrightowner;
	}

	public void setCopyrightowner(String copyrightowner) {
		this.copyrightowner = copyrightowner;
	}

	@Column(name = "copyrightinfo", length = 50)
	public String getCopyrightinfo() {
		return this.copyrightinfo;
	}

	public void setCopyrightinfo(String copyrightinfo) {
		this.copyrightinfo = copyrightinfo;
	}

	@Column(name = "allowsavepassword")
	public Boolean getAllowsavepassword() {
		return this.allowsavepassword;
	}

	public void setAllowsavepassword(Boolean allowsavepassword) {
		this.allowsavepassword = allowsavepassword;
	}

	@Column(name = "savepassworddays")
	public Integer getSavepassworddays() {
		return this.savepassworddays;
	}

	public void setSavepassworddays(Integer savepassworddays) {
		this.savepassworddays = savepassworddays;
	}

	@Column(name = "needreplaceinitialpassword")
	public Boolean getNeedreplaceinitialpassword() {
		return this.needreplaceinitialpassword;
	}

	public void setNeedreplaceinitialpassword(Boolean needreplaceinitialpassword) {
		this.needreplaceinitialpassword = needreplaceinitialpassword;
	}

	@Column(name = "needidentifingcode")
	public Boolean getNeedidentifingcode() {
		return this.needidentifingcode;
	}

	public void setNeedidentifingcode(Boolean needidentifingcode) {
		this.needidentifingcode = needidentifingcode;
	}

	@Column(name = "alwaysneedidentifingcode")
	public Boolean getAlwaysneedidentifingcode() {
		return this.alwaysneedidentifingcode;
	}

	public void setAlwaysneedidentifingcode(Boolean alwaysneedidentifingcode) {
		this.alwaysneedidentifingcode = alwaysneedidentifingcode;
	}

	@Column(name = "forgetpassword", length = 200)
	public String getForgetpassword() {
		return this.forgetpassword;
	}

	public void setForgetpassword(String forgetpassword) {
		this.forgetpassword = forgetpassword;
	}

	@Column(name = "allowloginagain")
	public Boolean getAllowloginagain() {
		return this.allowloginagain;
	}

	public void setAllowloginagain(Boolean allowloginagain) {
		this.allowloginagain = allowloginagain;
	}

	@Column(name = "loginmaxfailurecount")
	public Integer getLoginmaxfailurecount() {
		return loginmaxfailurecount;
	}

	public void setLoginmaxfailurecount(Integer loginmaxfailurecount) {
		this.loginmaxfailurecount = loginmaxfailurecount;
	}

	@Column(name = "loginfailurewaitminute")
	public Integer getLoginfailurewaitminute() {
		return loginfailurewaitminute;
	}

	public void setLoginfailurewaitminute(Integer loginfailurewaitminute) {
		this.loginfailurewaitminute = loginfailurewaitminute;
	}

	@Column(name = "changepassworddays")
	public Integer getChangepassworddays() {
		return changepassworddays;
	}

	public void setChangepassworddays(Integer changepassworddays) {
		this.changepassworddays = changepassworddays;
	}

	@Column(name = "maxusers")
	public Integer getMaxusers() {
		return this.maxusers;
	}

	public void setMaxusers(Integer maxusers) {
		this.maxusers = maxusers;
	}

	@Column(name = "sessiontimeoutminute")
	public Integer getSessiontimeoutminute() {
		return this.sessiontimeoutminute;
	}

	public void setSessiontimeoutminute(Integer sessiontimeoutminute) {
		this.sessiontimeoutminute = sessiontimeoutminute;
	}

	@Column(name = "saveinfilesystem", nullable = false)
	public boolean getSaveinfilesystem() {
		return this.saveinfilesystem;
	}

	public void setSaveinfilesystem(boolean saveinfilesystem) {
		this.saveinfilesystem = saveinfilesystem;
	}

	@Column(name = "rootpath", length = 200)
	public String getRootpath() {
		return this.rootpath;
	}

	public void setRootpath(String rootpath) {
		this.rootpath = rootpath;
	}

	@Column(name = "pathmode", length = 20)
	public String getPathmode() {
		return this.pathmode;
	}

	public void setPathmode(String pathmode) {
		this.pathmode = pathmode;
	}

	@Column(name = "filemaxsize")
	public Integer getFilemaxsize() {
		return this.filemaxsize;
	}

	public void setFilemaxsize(Integer filemaxsize) {
		this.filemaxsize = filemaxsize;
	}

	@Column(name = "createpreviewimage")
	public boolean getCreatepreviewimage() {
		return this.createpreviewimage;
	}

	public void setCreatepreviewimage(boolean createpreviewimage) {
		this.createpreviewimage = createpreviewimage;
	}

	@Column(name = "createpreviewpdf")
	public boolean getCreatepreviewpdf() {
		return this.createpreviewpdf;
	}

	public void setCreatepreviewpdf(boolean createpreviewpdf) {
		this.createpreviewpdf = createpreviewpdf;
	}

	@Column(name = "imagewidth")
	public Integer getImagewidth() {
		return this.imagewidth;
	}

	public void setImagewidth(Integer imagewidth) {
		this.imagewidth = imagewidth;
	}

	@Column(name = "imageheight")
	public Integer getImageheight() {
		return this.imageheight;
	}

	public void setImageheight(Integer imageheight) {
		this.imageheight = imageheight;
	}

	@Column(name = "backupfilename", length = 200)
	public String getBackupfilename() {
		return this.backupfilename;
	}

	public void setBackupfilename(String backupfilename) {
		this.backupfilename = backupfilename;
	}

	@Column(name = "remark", length = 200)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "properites", length = 2000)
	public String getProperites() {
		return properites;
	}

	public void setProperites(String properites) {
		this.properites = properites;
	}

	@Column(name = "systemlicense", length = 4000)
	public String getSystemlicense() {
		return systemlicense;
	}

	public void setSystemlicense(String systemlicense) {
		this.systemlicense = systemlicense;
	}

	@Column(name = "datamininglicense", length = 4000)
	public String getDatamininglicense() {
		return datamininglicense;
	}

	public void setDatamininglicense(String datamininglicense) {
		this.datamininglicense = datamininglicense;
	}

	@Column(name = "otherlicense", length = 4000)
	public String getOtherlicense() {
		return otherlicense;
	}

	public void setOtherlicense(String otherlicense) {
		this.otherlicense = otherlicense;
	}

}
