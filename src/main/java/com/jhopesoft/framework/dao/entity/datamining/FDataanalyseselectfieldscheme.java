package com.jhopesoft.framework.dao.entity.datamining;
// default package

// Generated 2017-6-18 11:15:29 by Hibernate Tools 5.2.0.Beta1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.system.FUser;

/**
 *
 * @author 蒋锋
 * 
 */

@Entity
@DynamicUpdate
@SuppressWarnings("serial")
@Table(name = "f_dataanalyseselectfieldscheme")
public class FDataanalyseselectfieldscheme implements java.io.Serializable {

	private String schemeid;
	private FDataobject FDataobject;
	private int orderno;
	private String iconcls;
	private String title;
	private String subtitle;
	private FUser FUser;
	private Boolean isshare;
	private Boolean isshareowner;
	private String othersetting;
	private String remark;
	private String creater;
	private Date createdate;
	private Set<FDataanalyseselectfielddetail> FDataanalyseselectfielddetails = new HashSet<FDataanalyseselectfielddetail>(
			0);

	public FDataanalyseselectfieldscheme() {
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

	@Column(name = "orderno", nullable = false)
	public int getOrderno() {
		return this.orderno;
	}

	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}

	@Column(name = "iconcls", length = 50)
	public String getIconcls() {
		return this.iconcls;
	}

	public void setIconcls(String iconcls) {
		this.iconcls = iconcls;
	}

	@Column(name = "title", nullable = false, length = 50)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "subtitle", length = 50)
	public String getSubtitle() {
		return this.subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userid", nullable = false)
	public FUser getFUser() {
		return this.FUser;
	}

	public void setFUser(FUser FUser) {
		this.FUser = FUser;
	}

	@Column(name = "isshare")
	public Boolean getIsshare() {
		return this.isshare;
	}

	public void setIsshare(Boolean isshare) {
		this.isshare = isshare;
	}

	@Column(name = "isshareowner")
	public Boolean getIsshareowner() {
		return this.isshareowner;
	}

	public void setIsshareowner(Boolean isshareowner) {
		this.isshareowner = isshareowner;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "FDataanalyseselectfieldscheme", cascade = CascadeType.ALL)
	@OrderBy("orderno")
	public Set<FDataanalyseselectfielddetail> getFDataanalyseselectfielddetails() {
		return this.FDataanalyseselectfielddetails;
	}

	public void setFDataanalyseselectfielddetails(Set<FDataanalyseselectfielddetail> FDataanalyseselectfielddetails) {
		this.FDataanalyseselectfielddetails = FDataanalyseselectfielddetails;
	}

}
