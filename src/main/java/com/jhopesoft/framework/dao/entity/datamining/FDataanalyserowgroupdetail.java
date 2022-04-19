package com.jhopesoft.framework.dao.entity.datamining;
// default package

// Generated 2017-6-18 11:15:29 by Hibernate Tools 5.2.0.Beta1

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

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author 蒋锋
 * 
 */

@Entity
@DynamicUpdate
@SuppressWarnings("serial")
@Table(name = "f_dataanalyserowgroupdetail")
public class FDataanalyserowgroupdetail implements java.io.Serializable {

	private String rowgroupid;
	private FDataanalyserowgroupdetail FDataanalyserowgroupdetail;
	private FDataanalyserowgroupscheme FDataanalyserowgroupscheme;
	private int orderno;
	private String orgintitle;
	private String title;
	private String keyvalue;
	private String groupcondition;
	private String othersetting;
	private String remark;
	private Set<FDataanalyserowgroupdetail> FDataanalyserowgroupdetails = new HashSet<FDataanalyserowgroupdetail>(0);

	public FDataanalyserowgroupdetail() {
	}

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@Column(name = "rowgroupid", unique = true, nullable = false, length = 40)
	public String getRowgroupid() {
		return this.rowgroupid;
	}

	public void setRowgroupid(String rowgroupid) {
		this.rowgroupid = rowgroupid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentid")
	public FDataanalyserowgroupdetail getFDataanalyserowgroupdetail() {
		return this.FDataanalyserowgroupdetail;
	}

	public void setFDataanalyserowgroupdetail(FDataanalyserowgroupdetail FDataanalyserowgroupdetail) {
		this.FDataanalyserowgroupdetail = FDataanalyserowgroupdetail;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schemeid")
	public FDataanalyserowgroupscheme getFDataanalyserowgroupscheme() {
		return this.FDataanalyserowgroupscheme;
	}

	public void setFDataanalyserowgroupscheme(FDataanalyserowgroupscheme FDataanalyserowgroupscheme) {
		this.FDataanalyserowgroupscheme = FDataanalyserowgroupscheme;
	}

	@Column(name = "orderno", nullable = false)
	public int getOrderno() {
		return this.orderno;
	}

	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}

	@Column(name = "orgintitle", length = 100)
	public String getOrgintitle() {
		return this.orgintitle;
	}

	public void setOrgintitle(String orgintitle) {
		this.orgintitle = orgintitle;
	}

	@Column(name = "title", nullable = false, length = 100)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "keyvalue", length = 40)
	public String getKeyvalue() {
		return this.keyvalue;
	}

	public void setKeyvalue(String keyvalue) {
		this.keyvalue = keyvalue;
	}

	@Column(name = "groupcondition", length = 65535)
	public String getGroupcondition() {
		return this.groupcondition;
	}

	public void setGroupcondition(String groupcondition) {
		this.groupcondition = groupcondition;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "FDataanalyserowgroupdetail", cascade = CascadeType.ALL)
	@OrderBy("orderno")
	public Set<FDataanalyserowgroupdetail> getFDataanalyserowgroupdetails() {
		return this.FDataanalyserowgroupdetails;
	}

	public void setFDataanalyserowgroupdetails(Set<FDataanalyserowgroupdetail> FDataanalyserowgroupdetails) {
		this.FDataanalyserowgroupdetails = FDataanalyserowgroupdetails;
	}

}
