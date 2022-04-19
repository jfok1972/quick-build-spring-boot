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

import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectcondition;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;

/**
 *
 * @author 蒋锋
 * 
 */

@Entity
@DynamicUpdate
@SuppressWarnings("serial")
@Table(name = "f_dataanalyseselectfielddetail")
public class FDataanalyseselectfielddetail implements java.io.Serializable {

	private String selectfieldid;
	private FDataanalyseselectfielddetail FDataanalyseselectfielddetail;
	private FDataanalyseselectfieldscheme FDataanalyseselectfieldscheme;
	private String aggregate;
	private FDataobjectcondition FDataobjectcondition;
	private FDataobjectfield FDataobjectfield;
	private String fieldahead;
	private String title;
	private int orderno;
	private Boolean onlytotal;
	private String othersetting;
	private String remark;
	private Set<FDataanalyseselectfielddetail> FDataanalyseselectfielddetails = new HashSet<FDataanalyseselectfielddetail>(
			0);

	public FDataanalyseselectfielddetail() {
	}

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@Column(name = "selectfieldid", unique = true, nullable = false, length = 40)
	public String getSelectfieldid() {
		return this.selectfieldid;
	}

	public void setSelectfieldid(String selectfieldid) {
		this.selectfieldid = selectfieldid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentid")
	public FDataanalyseselectfielddetail getFDataanalyseselectfielddetail() {
		return this.FDataanalyseselectfielddetail;
	}

	public void setFDataanalyseselectfielddetail(FDataanalyseselectfielddetail FDataanalyseselectfielddetail) {
		this.FDataanalyseselectfielddetail = FDataanalyseselectfielddetail;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schemeid")
	public FDataanalyseselectfieldscheme getFDataanalyseselectfieldscheme() {
		return this.FDataanalyseselectfieldscheme;
	}

	public void setFDataanalyseselectfieldscheme(FDataanalyseselectfieldscheme FDataanalyseselectfieldscheme) {
		this.FDataanalyseselectfieldscheme = FDataanalyseselectfieldscheme;
	}

	@Column(name = "aggregate", length = 20)
	public String getAggregate() {
		return this.aggregate;
	}

	public void setAggregate(String aggregate) {
		this.aggregate = aggregate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "conditionid")
	public FDataobjectcondition getFDataobjectcondition() {
		return this.FDataobjectcondition;
	}

	public void setFDataobjectcondition(FDataobjectcondition FDataobjectcondition) {
		this.FDataobjectcondition = FDataobjectcondition;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fieldid")
	public FDataobjectfield getFDataobjectfield() {
		return this.FDataobjectfield;
	}

	public void setFDataobjectfield(FDataobjectfield FDataobjectfield) {
		this.FDataobjectfield = FDataobjectfield;
	}

	@Column(name = "title", nullable = false, length = 50)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "orderno", nullable = false)
	public int getOrderno() {
		return this.orderno;
	}

	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}

	@Column(name = "onlytotal")
	public Boolean getOnlytotal() {
		return this.onlytotal;
	}

	public void setOnlytotal(Boolean onlytotal) {
		this.onlytotal = onlytotal;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "FDataanalyseselectfielddetail", cascade = CascadeType.ALL)
	@OrderBy("orderno")
	public Set<FDataanalyseselectfielddetail> getFDataanalyseselectfielddetails() {
		return this.FDataanalyseselectfielddetails;
	}

	public void setFDataanalyseselectfielddetails(Set<FDataanalyseselectfielddetail> FDataanalyseselectfielddetails) {
		this.FDataanalyseselectfielddetails = FDataanalyseselectfielddetails;
	}

	@Column(name = "fieldahead", length = 200)
	public String getFieldahead() {
		return fieldahead;
	}

	public void setFieldahead(String fieldahead) {
		this.fieldahead = fieldahead;
	}

}
