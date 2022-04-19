package com.jhopesoft.framework.dao.entity.viewsetting;
// default package

import java.util.List;

import javax.persistence.CascadeType;

// Generated 2016-11-25 16:37:05 by Hibernate Tools 5.2.0.Beta1

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
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectcondition;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entityinterface.ParentChildField;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectFieldUtils;
import com.jhopesoft.framework.utils.DataObjectUtils;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Entity
@DynamicUpdate
@Table(name = "fov_filterschemedetail")
public class FovFilterschemedetail implements java.io.Serializable, ParentChildField {

	private static final long serialVersionUID = 5858500722345004170L;
	private String detailid;
	private FDataobjectfield FDataobjectfield;
	private FovFilterscheme fovFilterscheme;
	private FovFilterschemedetail fovFilterschemedetail;
	private FDataobjectcondition FDataobjectconditionBySubconditionid;
	private Integer orderno;
	private String title;
	private String fieldahead;
	private String aggregate;
	private String filtertype;
	private Boolean hiddenoperator;
	private String operator;
	private String xtype;
	private Integer rowss;
	private Integer cols;
	private Integer rowspan;
	private Integer colspan;
	private String widths;
	private String othersetting;
	private String remark;
	private List<FovFilterschemedetail> details;

	private String fieldid; // 用于传送到前台

	public FovFilterschemedetail() {
	}

	public FovFilterschemedetail(String detailid, Integer orderno) {
		this.detailid = detailid;
		this.orderno = orderno;
	}

	public FovFilterschemedetail(FDataobjectfield FDataobjectfield,
			FDataobjectcondition FDataobjectconditionBySubconditionid, Integer orderno, String title, String fieldahead,
			String aggregate, String filtertype, Boolean hiddenoperator, String operator, String xtype, Integer rowss,
			Integer cols, Integer rowspan, Integer colspan, String widths, String othersetting, String remark) {
		this.FDataobjectfield = FDataobjectfield;
		this.FDataobjectconditionBySubconditionid = FDataobjectconditionBySubconditionid;
		this.orderno = orderno;
		this.title = title;
		this.fieldahead = fieldahead;
		this.aggregate = aggregate;
		this.filtertype = filtertype;
		this.hiddenoperator = hiddenoperator;
		this.operator = operator;
		this.xtype = xtype;
		this.rowss = rowss;
		this.cols = cols;
		this.rowspan = rowspan;
		this.colspan = colspan;
		this.widths = widths;
		this.othersetting = othersetting;
		this.remark = remark;
	}

	@Transient
	public JSONObject getManyToOneInfo() {
		JSONObject result = null;
		boolean condition = FDataobjectfield != null
				&& (FDataobjectfield._isManyToOne() || FDataobjectfield._isOneToOne());
		if (condition) {
			result = new JSONObject();
			FDataobject manytoone = DataObjectUtils.getDataObject(FDataobjectfield.getFieldtype());
			result.put("objectname", manytoone.getObjectname());
			result.put("primarykey", manytoone.getPrimarykey());
		}
		return result;
	}

	@Transient
	public Boolean getIsDateField() {
		if (aggregate != null && Constants.COUNT.equals(aggregate)) {
			return false;
		}
		return FDataobjectfield != null ? FDataobjectfield._isDateField() ? true : null : null;
	}

	@Transient
	public Boolean getIsNumberField() {
		if (aggregate != null && Constants.COUNT.equals(aggregate)) {
			return true;
		}
		return FDataobjectfield != null
				? FDataobjectfield._isNumberField() || FDataobjectfield._isOneToMany() ? true : null
				: null;
	}

	@Transient
	public Boolean getIsBooleanField() {
		if (aggregate != null && Constants.COUNT.equals(aggregate)) {
			return false;
		}
		return FDataobjectfield != null ? FDataobjectfield._isBooleanField() ? true : null : null;
	}

	@Transient
	public String getFDictionaryid() {
		return FDataobjectfield != null
				? FDataobjectfield.getFDictionary() != null ? FDataobjectfield.getFDictionary().getDictionaryid() : null
				: null;
	}

	/**
	 * 聚合字段的名称
	 * 
	 * @return
	 */
	@Transient
	public String getFieldname() {
		if (FDataobjectfield != null) {
			if (fieldahead == null) {
				return FDataobjectfield.getFieldname();
			} else {
				return DataObjectFieldUtils.getAdditionFieldname(FDataobjectfield, fieldahead, aggregate,
						FDataobjectconditionBySubconditionid, _getFDataobject(this).getObjectname(), false);
			}
		} else {
			return null;
		}
	}

	@Transient
	public String getObjectname() {
		if (fieldahead != null && fieldahead.length() > 0 && FDataobjectfield != null) {
			return FDataobjectfield.getFDataobject().getObjectname();
		} else {
			return null;
		}
	}

	@Transient
	public String getDefaulttitle() {
		if (FDataobjectfield != null) {
			if (fieldahead == null) {
				return FDataobjectfield.getFieldtitle();
			} else {
				return DataObjectFieldUtils.getDefaulttitle(FDataobjectfield, fieldahead, aggregate, null,
						_getFDataobject(this).getObjectname());
			}
		} else {
			return null;
		}
	}

	/**
	 * 返回propertyId,可以在生成筛选的时候使用combobox下拉选择
	 * 
	 * @return
	 */
	@Transient
	public String getPropertyId() {
		if (FDataobjectfield != null && FDataobjectfield.getFObjectfieldproperty() != null) {
			return FDataobjectfield.getFObjectfieldproperty().getPropertyid();
		} else {
			return null;
		}
	}

	/**
	 * 找到当前column是在哪个FDataobject之下的
	 * 
	 * @param column
	 * @return
	 */
	private FDataobject _getFDataobject(FovFilterschemedetail detail) {
		if (detail.fovFilterscheme == null) {
			return _getFDataobject(detail.getFovFilterschemedetail());
		} else {
			return detail.getFovFilterscheme().getFDataobject();
		}
	}

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@Column(name = "detailid", unique = true, nullable = false, length = 40)
	public String getDetailid() {
		return this.detailid;
	}

	public void setDetailid(String detailid) {
		this.detailid = detailid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fieldid")
	@JSONField(serialize = false)
	public FDataobjectfield getFDataobjectfield() {
		return this.FDataobjectfield;
	}

	public void setFDataobjectfield(FDataobjectfield FDataobjectfield) {
		this.FDataobjectfield = FDataobjectfield;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "filterschemeid")
	@JSONField(serialize = false)
	public FovFilterscheme getFovFilterscheme() {
		return this.fovFilterscheme;
	}

	public void setFovFilterscheme(FovFilterscheme fovFilterscheme) {
		this.fovFilterscheme = fovFilterscheme;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentid")
	@JSONField(serialize = false)
	public FovFilterschemedetail getFovFilterschemedetail() {
		return this.fovFilterschemedetail;
	}

	public void setFovFilterschemedetail(FovFilterschemedetail fovFilterschemedetail) {
		this.fovFilterschemedetail = fovFilterschemedetail;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subconditionid")
	public FDataobjectcondition getFDataobjectconditionBySubconditionid() {
		return this.FDataobjectconditionBySubconditionid;
	}

	public void setFDataobjectconditionBySubconditionid(FDataobjectcondition FDataobjectconditionBySubconditionid) {
		this.FDataobjectconditionBySubconditionid = FDataobjectconditionBySubconditionid;
	}

	@Column(name = "orderno", nullable = false)
	public Integer getOrderno() {
		return this.orderno;
	}

	public void setOrderno(Integer orderno) {
		this.orderno = orderno;
	}

	@Column(name = "title", length = 200)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "fieldahead", length = 200)
	public String getFieldahead() {
		return this.fieldahead;
	}

	public void setFieldahead(String fieldahead) {
		this.fieldahead = fieldahead;
	}

	@Column(name = "aggregate", length = 20)
	public String getAggregate() {
		return this.aggregate;
	}

	public void setAggregate(String aggregate) {
		this.aggregate = aggregate;
	}

	@Column(name = "filtertype", length = 50)
	public String getFiltertype() {
		return this.filtertype;
	}

	public void setFiltertype(String filtertype) {
		this.filtertype = filtertype;
	}

	public Boolean getHiddenoperator() {
		return hiddenoperator;
	}

	public void setHiddenoperator(Boolean hiddenoperator) {
		this.hiddenoperator = hiddenoperator;
	}

	@Column(name = "operator", length = 50)
	public String getOperator() {
		return this.operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Column(name = "xtype", length = 50)
	public String getXtype() {
		return this.xtype;
	}

	public void setXtype(String xtype) {
		this.xtype = xtype;
	}

	@Column(name = "rowss")
	public Integer getRowss() {
		return this.rowss;
	}

	public void setRowss(Integer rows) {
		this.rowss = rows;
	}

	@Column(name = "cols")
	public Integer getCols() {
		return this.cols;
	}

	public void setCols(Integer cols) {
		this.cols = cols;
	}

	@Column(name = "rowspan")
	public Integer getRowspan() {
		return this.rowspan;
	}

	public void setRowspan(Integer rowspan) {
		this.rowspan = rowspan;
	}

	@Column(name = "colspan")
	public Integer getColspan() {
		return this.colspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	@Column(name = "widths", length = 200)
	public String getWidths() {
		return this.widths;
	}

	public void setWidths(String widths) {
		this.widths = widths;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fovFilterschemedetail", cascade = CascadeType.ALL)
	@OrderBy("orderno")
	public List<FovFilterschemedetail> getDetails() {
		return this.details == null || this.details.size() == 0 ? null : this.details;
	}

	public void setDetails(List<FovFilterschemedetail> details) {
		this.details = details;
	}

	@Column(updatable = false, insertable = false)
	public String getFieldid() {
		return fieldid;
	}

	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
	}

	@Override
	@Transient
	public String getCondition() {
		return null;
	}

	@Override
	public void setCondition(String value) {

	}

}
