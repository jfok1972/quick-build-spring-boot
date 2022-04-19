package com.jhopesoft.framework.dao.entity.datamining;

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
import com.jhopesoft.framework.utils.Constants;

/**
 *
 * @author 蒋锋
 * 
 */

@SuppressWarnings("serial")
@Entity
@DynamicUpdate
@Table(name = "f_dataminingselectfield")
public class FDataminingselectfield implements java.io.Serializable {

	private String selectfieldid;
	private FDataminingscheme FDataminingscheme;
	private FDataminingselectfield FDataminingselectfield;
	private FDataobjectcondition FDataobjectcondition;
	private FDataobjectfield FDataobjectfield;
	private String fieldahead;
	private String aggregate;
	private String title;
	private int orderno;
	private Boolean onlytotal;
	private String othersetting;
	private String remark;
	private Set<FDataminingselectfield> FDataminingselectfields = new HashSet<FDataminingselectfield>(0);

	public FDataminingselectfield() {
	}

	public FDataminingselectfield(String selectfieldid, String title, int orderno) {
		this.selectfieldid = selectfieldid;
		this.title = title;
		this.orderno = orderno;
	}

	public FDataminingselectfield(String selectfieldid, FDataminingscheme FDataminingscheme,
			FDataminingselectfield FDataminingselectfield, FDataobjectcondition FDataobjectcondition,
			FDataobjectfield FDataobjectfield, String aggregate, String title, int orderno, Boolean onlytotal,
			String othersetting, String remark, Set<FDataminingselectfield> FDataminingselectfields) {
		this.selectfieldid = selectfieldid;
		this.FDataminingscheme = FDataminingscheme;
		this.FDataminingselectfield = FDataminingselectfield;
		this.FDataobjectcondition = FDataobjectcondition;
		this.FDataobjectfield = FDataobjectfield;
		this.aggregate = aggregate;
		this.title = title;
		this.orderno = orderno;
		this.onlytotal = onlytotal;
		this.othersetting = othersetting;
		this.remark = remark;
		this.FDataminingselectfields = FDataminingselectfields;
	}

	public String _getAggregateFieldame() {
		String aggregatefieldname = null;
		if (getFDataminingselectfields().size() == 0) {
			aggregatefieldname = getAggregate() + "." + getFDataobjectfield().getFieldname();
			if (getFieldahead() != null) {
				String[] withpart = getFieldahead().split("\\.with\\.");
				aggregatefieldname = getAggregate() + "." + withpart[0] + "." + getFDataobjectfield().getFieldname()
						+ Constants.DOTWITHDOT + withpart[1];
			}
			if (getFDataobjectcondition() != null) {
				aggregatefieldname = aggregatefieldname + "|" + getFDataobjectcondition().getConditionid();
			}
		}
		return aggregatefieldname;
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
	@JoinColumn(name = "schemeid")
	public FDataminingscheme getFDataminingscheme() {
		return this.FDataminingscheme;
	}

	public void setFDataminingscheme(FDataminingscheme FDataminingscheme) {
		this.FDataminingscheme = FDataminingscheme;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentid")
	public FDataminingselectfield getFDataminingselectfield() {
		return this.FDataminingselectfield;
	}

	public void setFDataminingselectfield(FDataminingselectfield FDataminingselectfield) {
		this.FDataminingselectfield = FDataminingselectfield;
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

	@Column(name = "aggregate", length = 20)
	public String getAggregate() {
		return this.aggregate;
	}

	public void setAggregate(String aggregate) {
		this.aggregate = aggregate;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "FDataminingselectfield", cascade = CascadeType.ALL)
	@OrderBy("orderno")
	public Set<FDataminingselectfield> getFDataminingselectfields() {
		return this.FDataminingselectfields;
	}

	public void setFDataminingselectfields(Set<FDataminingselectfield> FDataminingselectfields) {
		this.FDataminingselectfields = FDataminingselectfields;
	}

	@Column(name = "fieldahead", length = 200)
	public String getFieldahead() {
		return fieldahead;
	}

	public void setFieldahead(String fieldahead) {
		this.fieldahead = fieldahead;
	}

}
