package com.jhopesoft.framework.dao.entity.viewsetting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.jhopesoft.framework.core.objectquery.sqlfield.SqlFieldUtils;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectcondition;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.dictionary.FNumbergroup;
import com.jhopesoft.framework.dao.entity.utils.FFunction;
import com.jhopesoft.framework.dao.entityinterface.ParentChildField;
import com.jhopesoft.framework.utils.Constants;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Entity
@DynamicUpdate
@Table(name = "fov_gridnavigateschemedetail")
public class FovGridnavigateschemedetail implements java.io.Serializable, ParentChildField {

  private static final long serialVersionUID = -8469781351207535416L;
  private String schemedetailid;
  private FDataobjectfield FDataobjectfield;
  private FFunction FFunction;
  private FNumbergroup FNumbergroup;
  private FovGridnavigatescheme fovGridnavigatescheme;
  private FDataobjectcondition FDataobjectconditionBySubconditionid;
  private Integer orderno;
  private String title;
  private String fielddescription;
  private String fieldahead;
  private String aggregate;
  private String fieldfunction;
  private String ntype;
  private Boolean addparentfilter;
  private Boolean reverseorder;
  private Boolean collapsed;
  private Boolean addcodelevel;
  private String iconcls;
  private String cls;
  private String remark;

  public FovGridnavigateschemedetail() {
  }

  public FovGridnavigateschemedetail(com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield fDataobjectfield,
      FovGridnavigatescheme fovGridnavigatescheme, Integer orderno, String title, String fieldahead,
      Boolean addparentfilter, Boolean collapsed, Boolean addcodelevel, String iconcls) {
    super();
    FDataobjectfield = fDataobjectfield;
    this.fovGridnavigatescheme = fovGridnavigatescheme;
    this.orderno = orderno;
    this.title = title;
    this.fieldahead = fieldahead;
    this.addparentfilter = addparentfilter;
    this.collapsed = collapsed;
    this.addcodelevel = addcodelevel;
    this.iconcls = iconcls;
  }

  public FovGridnavigateschemedetail(FovGridnavigatescheme fovGridnavigatescheme, FDataobjectfield fDataobjectfield,
      FDataobjectcondition FDataobjectconditionBySubconditionid, FFunction fFunction, FNumbergroup fNumbergroup,
      Integer orderno, String title, String fielddescription, String fieldahead, String aggregate, String fieldfunction,
      String ntype, Boolean addparentfilter, Boolean reverseorder, Boolean collapsed, Boolean addcodelevel,
      String iconcls, String cls, String remark) {
    super();
    this.fovGridnavigatescheme = fovGridnavigatescheme;
    this.FDataobjectfield = fDataobjectfield;
    this.FDataobjectconditionBySubconditionid = FDataobjectconditionBySubconditionid;
    this.FFunction = fFunction;
    this.FNumbergroup = fNumbergroup;
    this.orderno = orderno;
    this.title = title;
    this.fielddescription = fielddescription;
    this.fieldahead = fieldahead;
    this.aggregate = aggregate;
    this.fieldfunction = fieldfunction;
    this.ntype = ntype;
    this.addparentfilter = addparentfilter;
    this.reverseorder = reverseorder;
    this.collapsed = collapsed;
    this.addcodelevel = addcodelevel;
    this.iconcls = iconcls;
    this.cls = cls;
    this.remark = remark;
  }

  public String _getCondition() {
    String result = null;
    if (getFFunction() != null) {
      result = getFFunction().getSqlExpression(FDataobjectfield.getFDataobject());
    }
    if (getFieldfunction() != null) {
      if (result == null) {
        result = getFieldfunction();
      } else {
        result = SqlFieldUtils.addConditionToFieldSql(result, getFieldfunction());
      }
    }
    if (getFNumbergroup() != null) {
      result = getFNumbergroup().genExpression(result == null ? Constants.THIS : result);
    }
    return result;
  }

  public String _getFactAheadPath() {
    if (FDataobjectfield != null && (FDataobjectfield._isManyToOne() || FDataobjectfield._isOneToOne())) {
      if (fieldahead == null) {
        return FDataobjectfield.getFieldname();
      } else {
        return fieldahead + "." + FDataobjectfield.getFieldname();
      }
    }
    return fieldahead;
  }

  @GenericGenerator(name = "generator", strategy = "uuid.hex")
  @Id
  @GeneratedValue(generator = "generator")
  @Column(name = "schemedetailid", unique = true, nullable = false, length = 40)
  public String getSchemedetailid() {
    return this.schemedetailid;
  }

  public void setSchemedetailid(String schemedetailid) {
    this.schemedetailid = schemedetailid;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fieldid", nullable = false)
  public FDataobjectfield getFDataobjectfield() {
    return this.FDataobjectfield;
  }

  public void setFDataobjectfield(FDataobjectfield FDataobjectfield) {
    this.FDataobjectfield = FDataobjectfield;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "functionid")
  public FFunction getFFunction() {
    return this.FFunction;
  }

  public void setFFunction(FFunction FFunction) {
    this.FFunction = FFunction;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "numbergroupid")
  public FNumbergroup getFNumbergroup() {
    return this.FNumbergroup;
  }

  public void setFNumbergroup(FNumbergroup FNumbergroup) {
    this.FNumbergroup = FNumbergroup;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "schemeid", nullable = false)
  public FovGridnavigatescheme getFovGridnavigatescheme() {
    return this.fovGridnavigatescheme;
  }

  public void setFovGridnavigatescheme(FovGridnavigatescheme fovGridnavigatescheme) {
    this.fovGridnavigatescheme = fovGridnavigatescheme;
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

  @Column(name = "title", nullable = false, length = 50)
  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Column(name = "fielddescription", length = 200)
  public String getFielddescription() {
    return this.fielddescription;
  }

  public void setFielddescription(String fielddescription) {
    this.fielddescription = fielddescription;
  }

  @Column(name = "fieldahead", length = 200)
  public String getFieldahead() {
    return this.fieldahead;
  }

  public void setFieldahead(String fieldahead) {
    this.fieldahead = fieldahead;
  }

  @Column(name = "aggregate", length = 10)
  public String getAggregate() {
    return this.aggregate;
  }

  public void setAggregate(String aggregate) {
    this.aggregate = aggregate;
  }

  @Column(name = "fieldfunction", length = 2000)
  public String getFieldfunction() {
    return this.fieldfunction;
  }

  public void setFieldfunction(String fieldfunction) {
    this.fieldfunction = fieldfunction;
  }

  @Column(name = "ntype", length = 50)
  public String getNtype() {
    return ntype;
  }

  public void setNtype(String ntype) {
    this.ntype = ntype;
  }

  @Column(name = "addparentfilter")
  public Boolean getAddparentfilter() {
    return this.addparentfilter == null ? false : this.addparentfilter;
  }

  public void setAddparentfilter(Boolean addparentfilter) {
    this.addparentfilter = addparentfilter;
  }

  @Column(name = "reverseorder")
  public Boolean getReverseorder() {
    return this.reverseorder == null ? false : this.reverseorder;
  }

  public void setReverseorder(Boolean reverseorder) {
    this.reverseorder = reverseorder;
  }

  @Column(name = "collapsed")
  public Boolean getCollapsed() {
    return this.collapsed == null ? false : this.collapsed;
  }

  public void setCollapsed(Boolean collapsed) {
    this.collapsed = collapsed;
  }

  @Column(name = "addcodelevel")
  public Boolean getAddcodelevel() {
    return this.addcodelevel == null ? false : this.addcodelevel;
  }

  public void setAddcodelevel(Boolean addcodelevel) {
    this.addcodelevel = addcodelevel;
  }

  @Column(name = "iconcls", length = 50)
  public String getIconcls() {
    return this.iconcls;
  }

  public void setIconcls(String iconcls) {
    this.iconcls = iconcls;
  }

  @Column(name = "cls", length = 50)
  public String getCls() {
    return this.cls;
  }

  public void setCls(String cls) {
    this.cls = cls;
  }

  @Column(name = "remark", length = 200)
  public String getRemark() {
    return this.remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
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
