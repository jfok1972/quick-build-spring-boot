package com.jhopesoft.framework.dao.entity.datainorout;
// default package
// Generated 2017-2-4 15:34:48 by Hibernate Tools 5.2.0.Beta1


import java.util.HashSet;
import java.util.Set;
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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;

/**
 *
 * @author 蒋锋
 * 
 */

@SuppressWarnings("serial")
@Entity
@DynamicUpdate
@Table(name = "f_recordprintscheme", uniqueConstraints = @UniqueConstraint(columnNames = {"objectid",
    "title"}))
public class FRecordprintscheme implements java.io.Serializable {


  private String schemeid;
  private FDataobject FDataobject;
  private String title;
  private Integer orderno;
  private Boolean issystem;
  private Boolean issub;
  private String iconurl;
  private String iconcls;
  private Boolean isdisable;
  private String othersetting;
  private Set<FRecordprintschemegroup> FRecordprintschemegroups = new HashSet<FRecordprintschemegroup>(0);

  public FRecordprintscheme() {}

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


  @Column(name = "title", nullable = false, length = 50)
  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  @Column(name = "orderno")
  public Integer getOrderno() {
    return this.orderno;
  }

  public void setOrderno(Integer orderno) {
    this.orderno = orderno;
  }


  @Column(name = "issystem")
  public Boolean getIssystem() {
    return this.issystem;
  }

  public void setIssystem(Boolean issystem) {
    this.issystem = issystem;
  }


  @Column(name = "issub")
  public Boolean getIssub() {
    return this.issub;
  }

  public void setIssub(Boolean issub) {
    this.issub = issub;
  }


  @Column(name = "iconurl", length = 200)
  public String getIconurl() {
    return this.iconurl;
  }

  public void setIconurl(String iconurl) {
    this.iconurl = iconurl;
  }


  @Column(name = "iconcls", length = 50)
  public String getIconcls() {
    return this.iconcls;
  }

  public void setIconcls(String iconcls) {
    this.iconcls = iconcls;
  }


  @Column(name = "isdisable")
  public Boolean getIsdisable() {
    return this.isdisable;
  }

  public void setIsdisable(Boolean isdisable) {
    this.isdisable = isdisable;
  }


  @Column(name = "othersetting", length = 200)
  public String getOthersetting() {
    return this.othersetting;
  }

  public void setOthersetting(String othersetting) {
    this.othersetting = othersetting;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "FRecordprintscheme")
  @OrderBy("orderno")
  public Set<FRecordprintschemegroup> getFRecordprintschemegroups() {
    return this.FRecordprintschemegroups;
  }

  public void setFRecordprintschemegroups(Set<FRecordprintschemegroup> FRecordprintschemegroups) {
    this.FRecordprintschemegroups = FRecordprintschemegroups;
  }



}


