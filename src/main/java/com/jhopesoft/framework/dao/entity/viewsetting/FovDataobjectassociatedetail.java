package com.jhopesoft.framework.dao.entity.viewsetting;
// default package
// Generated 2017-1-28 13:13:20 by 蒋锋


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.annotation.JSONField;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.system.FUser;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@SuppressWarnings("serial")
@Entity
@DynamicUpdate
@Table(name = "fov_dataobjectassociatedetail")
public class FovDataobjectassociatedetail implements java.io.Serializable {


  private String associatedetailid;
  private FDataobject FDataobjectBySubobjectid;
  private FUser FUser;
  private FovChartscheme fovChartscheme;
  private FovDataobjectassociate fovDataobjectassociate;
  private FovFormscheme fovFormscheme;
  private String formtype;
  private int orderno;
  private String title;
  private String fieldahead;
  private String recordtpl;
  private String xtype;
  private Boolean isattchment;
  private Boolean isapproveinfo;
  private Boolean subobjectnavigate;
  private Boolean subobjectsouthregion;
  private Boolean subobjecteastregion;
  private String othersetting;

  public FovDataobjectassociatedetail() {}


  public FovDataobjectassociatedetail(String associatedetailid, FovDataobjectassociate fovDataobjectassociate,
      int orderno, String title) {
    this.associatedetailid = associatedetailid;
    this.fovDataobjectassociate = fovDataobjectassociate;
    this.orderno = orderno;
    this.title = title;
  }

  public FovDataobjectassociatedetail(String associatedetailid, FDataobject FDataobjectBySubobjectid, FUser FUser,
      FovChartscheme fovChartscheme, FovDataobjectassociate fovDataobjectassociate, FovFormscheme fovFormscheme,
      int orderno, String title, String fieldahead, String recordtpl, String xtype, Boolean isattchment,
      Boolean isapproveinfo, Boolean subobjectnavigate, Boolean subobjectsouthregion, Boolean subobjecteastregion,
      String othersetting) {
    this.associatedetailid = associatedetailid;
    this.FDataobjectBySubobjectid = FDataobjectBySubobjectid;
    this.FUser = FUser;
    this.fovChartscheme = fovChartscheme;
    this.fovDataobjectassociate = fovDataobjectassociate;
    this.fovFormscheme = fovFormscheme;
    this.orderno = orderno;
    this.title = title;
    this.fieldahead = fieldahead;
    this.recordtpl = recordtpl;
    this.xtype = xtype;
    this.isattchment = isattchment;
    this.isapproveinfo = isapproveinfo;
    this.subobjectnavigate = subobjectnavigate;
    this.subobjectsouthregion = subobjectsouthregion;
    this.subobjecteastregion = subobjecteastregion;
    this.othersetting = othersetting;
  }

  @Id
  @GeneratedValue(generator = "generator")
  @GenericGenerator(name = "generator", strategy = "uuid.hex")
  @Column(name = "associatedetailid", unique = true, nullable = false, length = 40)
  public String getAssociatedetailid() {
    return this.associatedetailid;
  }

  public void setAssociatedetailid(String associatedetailid) {
    this.associatedetailid = associatedetailid;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subobjectid")
  @JSONField(serialize = false)

  public FDataobject getFDataobjectBySubobjectid() {
    return FDataobjectBySubobjectid;
  }


  public void setFDataobjectBySubobjectid(FDataobject fDataobjectBySubobjectid) {
    FDataobjectBySubobjectid = fDataobjectBySubobjectid;
  }


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userid")
  public FUser getFUser() {
    return this.FUser;
  }


  public void setFUser(FUser FUser) {
    this.FUser = FUser;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chartschemeid")
  public FovChartscheme getFovChartscheme() {
    return this.fovChartscheme;
  }

  public void setFovChartscheme(FovChartscheme fovChartscheme) {
    this.fovChartscheme = fovChartscheme;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "associateid", nullable = false)
  public FovDataobjectassociate getFovDataobjectassociate() {
    return this.fovDataobjectassociate;
  }

  public void setFovDataobjectassociate(FovDataobjectassociate fovDataobjectassociate) {
    this.fovDataobjectassociate = fovDataobjectassociate;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "formschemeid")
  public FovFormscheme getFovFormscheme() {
    return this.fovFormscheme;
  }

  public void setFovFormscheme(FovFormscheme fovFormscheme) {
    this.fovFormscheme = fovFormscheme;
  }

  @Column(name = "formtype")

  public String getFormtype() {
    return formtype;
  }


  public void setFormtype(String formtype) {
    this.formtype = formtype;
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


  @Column(name = "fieldahead", length = 200)
  public String getFieldahead() {
    return this.fieldahead;
  }

  public void setFieldahead(String fieldahead) {
    this.fieldahead = fieldahead;
  }


  @Column(name = "recordtpl", length = 4000)
  public String getRecordtpl() {
    return this.recordtpl;
  }

  public void setRecordtpl(String recordtpl) {
    this.recordtpl = recordtpl;
  }


  @Column(name = "xtype", length = 200)
  public String getXtype() {
    return this.xtype;
  }

  public void setXtype(String xtype) {
    this.xtype = xtype;
  }


  @Column(name = "isattchment")
  public Boolean getIsattchment() {
    return this.isattchment;
  }

  public void setIsattchment(Boolean isattchment) {
    this.isattchment = isattchment;
  }


  @Column(name = "isapproveinfo")
  public Boolean getIsapproveinfo() {
    return this.isapproveinfo;
  }

  public void setIsapproveinfo(Boolean isapproveinfo) {
    this.isapproveinfo = isapproveinfo;
  }


  @Column(name = "subobjectnavigate")
  public Boolean getSubobjectnavigate() {
    return this.subobjectnavigate;
  }

  public void setSubobjectnavigate(Boolean subobjectnavigate) {
    this.subobjectnavigate = subobjectnavigate;
  }


  @Column(name = "subobjectsouthregion")
  public Boolean getSubobjectsouthregion() {
    return this.subobjectsouthregion;
  }

  public void setSubobjectsouthregion(Boolean subobjectsouthregion) {
    this.subobjectsouthregion = subobjectsouthregion;
  }


  @Column(name = "subobjecteastregion")
  public Boolean getSubobjecteastregion() {
    return this.subobjecteastregion;
  }

  public void setSubobjecteastregion(Boolean subobjecteastregion) {
    this.subobjecteastregion = subobjecteastregion;
  }


  @Column(name = "othersetting", length = 200)
  public String getOthersetting() {
    return this.othersetting;
  }

  public void setOthersetting(String othersetting) {
    this.othersetting = othersetting;
  }



}


