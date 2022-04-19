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
@SuppressWarnings("serial")
@DynamicUpdate
@Table(name = "f_dataanalysecolumngroupdetail")
public class FDataanalysecolumngroupdetail implements java.io.Serializable {

	private String columngroupid;
	private FDataanalysecolumngroupdetail FDataanalysecolumngroupdetail;
	private FDataanalysecolumngroupscheme FDataanalysecolumngroupscheme;
	private int orderno;
	private String title;
	private String groupcondition;
	private String othersetting;
	private String remark;
	private Set<FDataanalysecolumngroupdetail> FDataanalysecolumngroupdetails = new HashSet<FDataanalysecolumngroupdetail>(
			0);

	public FDataanalysecolumngroupdetail() {
	}

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@Column(name = "columngroupid", unique = true, nullable = false, length = 40)
	public String getColumngroupid() {
		return this.columngroupid;
	}

	public void setColumngroupid(String columngroupid) {
		this.columngroupid = columngroupid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentid")
	public FDataanalysecolumngroupdetail getFDataanalysecolumngroupdetail() {
		return this.FDataanalysecolumngroupdetail;
	}

	public void setFDataanalysecolumngroupdetail(FDataanalysecolumngroupdetail FDataanalysecolumngroupdetail) {
		this.FDataanalysecolumngroupdetail = FDataanalysecolumngroupdetail;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schemeid")
	public FDataanalysecolumngroupscheme getFDataanalysecolumngroupscheme() {
		return this.FDataanalysecolumngroupscheme;
	}

	public void setFDataanalysecolumngroupscheme(FDataanalysecolumngroupscheme FDataanalysecolumngroupscheme) {
		this.FDataanalysecolumngroupscheme = FDataanalysecolumngroupscheme;
	}

	@Column(name = "orderno", nullable = false)
	public int getOrderno() {
		return this.orderno;
	}

	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}

	@Column(name = "title", nullable = false, length = 50)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "FDataanalysecolumngroupdetail", cascade = CascadeType.ALL)
	@OrderBy("orderno")
	public Set<FDataanalysecolumngroupdetail> getFDataanalysecolumngroupdetails() {
		return this.FDataanalysecolumngroupdetails;
	}

	public void setFDataanalysecolumngroupdetails(Set<FDataanalysecolumngroupdetail> FDataanalysecolumngroupdetails) {
		this.FDataanalysecolumngroupdetails = FDataanalysecolumngroupdetails;
	}

}
