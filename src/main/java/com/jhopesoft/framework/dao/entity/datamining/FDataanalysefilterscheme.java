package com.jhopesoft.framework.dao.entity.datamining;
// default package
// Generated 2017-9-30 11:16:34 by Hibernate Tools 5.2.0.Beta1


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

@SuppressWarnings("serial")
@Entity
@DynamicUpdate
@Table(name = "f_dataanalysefilterscheme")
public class FDataanalysefilterscheme implements java.io.Serializable {

  private String schemeid;
  private FDataobject FDataobject;
  private FUser FUser;
  private int orderno;
  private String iconcls;
  private String title;
  private String subtitle;
  private Short isshare;
  private Short isshareowner;
  private String othersetting;
  private String remark;
  private String creater;
  private Date createdate;

  public FDataanalysefilterscheme() {}


  public FDataanalysefilterscheme(String schemeid, FDataobject FDataobject, int orderno, String title, String creater,
      Date createdate) {
    this.schemeid = schemeid;
    this.FDataobject = FDataobject;
    this.orderno = orderno;
    this.title = title;
    this.creater = creater;
    this.createdate = createdate;
  }

  public FDataanalysefilterscheme(String schemeid, FDataobject FDataobject, FUser FUser, int orderno, String iconcls,
      String title, String subtitle, Short isshare, Short isshareowner, String othersetting, String remark,
      String creater, Date createdate) {
    this.schemeid = schemeid;
    this.FDataobject = FDataobject;
    this.FUser = FUser;
    this.orderno = orderno;
    this.iconcls = iconcls;
    this.title = title;
    this.subtitle = subtitle;
    this.isshare = isshare;
    this.isshareowner = isshareowner;
    this.othersetting = othersetting;
    this.remark = remark;
    this.creater = creater;
    this.createdate = createdate;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userid")
  public FUser getFUser() {
    return this.FUser;
  }

  public void setFUser(FUser FUser) {
    this.FUser = FUser;
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


  @Column(name = "isshare")
  public Short getIsshare() {
    return this.isshare;
  }

  public void setIsshare(Short isshare) {
    this.isshare = isshare;
  }


  @Column(name = "isshareowner")
  public Short getIsshareowner() {
    return this.isshareowner;
  }

  public void setIsshareowner(Short isshareowner) {
    this.isshareowner = isshareowner;
  }


  @Column(name = "othersetting", length = 65535)
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



}


