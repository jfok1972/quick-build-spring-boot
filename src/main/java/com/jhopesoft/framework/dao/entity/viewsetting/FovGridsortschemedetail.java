package com.jhopesoft.framework.dao.entity.viewsetting;
// default package
// Generated 2017-2-18 20:05:40 by Hibernate Tools 5.2.0.Beta1


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

import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectcondition;
import com.jhopesoft.framework.dao.entity.utils.FFunction;
import com.jhopesoft.framework.dao.entityinterface.ParentChildField;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@SuppressWarnings("serial")
@Entity
@DynamicUpdate
@Table(name = "fov_gridsortschemedetail")
public class FovGridsortschemedetail implements java.io.Serializable, ParentChildField {

  private String schemedetailid;
  private FDataobjectcondition FDataobjectcondition;
  private FDataobjectfield FDataobjectfield;
  private FFunction FFunction;
  private FovGridsortscheme fovGridsortscheme;
  private int orderno;
  private String fieldahead;
  private String aggregate;
  private String fieldfunction;
  private String direction;

  public FovGridsortschemedetail() {}


  public FovGridsortschemedetail(String schemedetailid, FDataobjectfield FDataobjectfield,
      FovGridsortscheme fovGridsortscheme, int orderno) {
    this.schemedetailid = schemedetailid;
    this.FDataobjectfield = FDataobjectfield;
    this.fovGridsortscheme = fovGridsortscheme;
    this.orderno = orderno;
  }

  public FovGridsortschemedetail(String schemedetailid, FDataobjectcondition FDataobjectcondition,
      FDataobjectfield FDataobjectfield, FFunction FFunction, FovGridsortscheme fovGridsortscheme, int orderno,
      String fieldahead, String aggregate, String fieldfunction, String direction) {
    this.schemedetailid = schemedetailid;
    this.FDataobjectcondition = FDataobjectcondition;
    this.FDataobjectfield = FDataobjectfield;
    this.FFunction = FFunction;
    this.fovGridsortscheme = fovGridsortscheme;
    this.orderno = orderno;
    this.fieldahead = fieldahead;
    this.aggregate = aggregate;
    this.fieldfunction = fieldfunction;
    this.direction = direction;
  }

  @Id
  @GenericGenerator(name = "generator", strategy = "uuid.hex")
  @GeneratedValue(generator = "generator")
  @Column(name = "schemedetailid", unique = true, nullable = false, length = 40)
  public String getSchemedetailid() {
    return this.schemedetailid;
  }

  public void setSchemedetailid(String schemedetailid) {
    this.schemedetailid = schemedetailid;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subconditionid")
  public FDataobjectcondition getFDataobjectcondition() {
    return this.FDataobjectcondition;
  }

  public void setFDataobjectcondition(FDataobjectcondition FDataobjectcondition) {
    this.FDataobjectcondition = FDataobjectcondition;
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
  @JoinColumn(name = "schemeid", nullable = false)
  public FovGridsortscheme getFovGridsortscheme() {
    return this.fovGridsortscheme;
  }

  public void setFovGridsortscheme(FovGridsortscheme fovGridsortscheme) {
    this.fovGridsortscheme = fovGridsortscheme;
  }


  @Column(name = "orderno", nullable = false)
  public int getOrderno() {
    return this.orderno;
  }

  public void setOrderno(int orderno) {
    this.orderno = orderno;
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


  @Column(name = "fieldfunction", length = 200)
  public String getFieldfunction() {
    return this.fieldfunction;
  }

  public void setFieldfunction(String fieldfunction) {
    this.fieldfunction = fieldfunction;
  }


  @Column(name = "direction", length = 10)
  public String getDirection() {
    return this.direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  @Override
  @Transient
  public FDataobjectcondition getFDataobjectconditionBySubconditionid() {
    return null;
  }

  @Override
  public void setFDataobjectconditionBySubconditionid(FDataobjectcondition value) {}

  @Override
  @Transient
  public String getCondition() {
    return null;
  }

  @Override
  public void setCondition(String value) {

  }

  @Override
  @Transient
  public String getRemark() {
    return null;
  }


  @Override
  public void setRemark(String value) {    
  }

}


