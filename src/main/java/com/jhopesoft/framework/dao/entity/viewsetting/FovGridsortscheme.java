package com.jhopesoft.framework.dao.entity.viewsetting;
// default package
// Generated 2017-2-18 20:05:40 by Hibernate Tools 5.2.0.Beta1


import java.util.LinkedHashSet;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.JSONObject;
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
@Table(name = "fov_gridsortscheme")
@Cache(region = "beanCache", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)

public class FovGridsortscheme implements java.io.Serializable {

  private String schemeid;
  private FDataobject FDataobject;
  private FUser FUser;
  private int orderno;
  private String title;
  private Boolean isshare;
  private Boolean isshareowner;
  private Set<FovGridsortschemedetail> details = new LinkedHashSet<FovGridsortschemedetail>(0);

  public FovGridsortscheme() {}


  public FovGridsortscheme(String schemeid, FDataobject FDataobject, int orderno, String title) {
    this.schemeid = schemeid;
    this.FDataobject = FDataobject;
    this.orderno = orderno;
    this.title = title;
  }

  public FovGridsortscheme(String schemeid, FDataobject FDataobject, FUser FUser, int orderno, String title,
      Boolean isshare, Boolean isshareowner, Set<FovGridsortschemedetail> details) {
    this.schemeid = schemeid;
    this.FDataobject = FDataobject;
    this.FUser = FUser;
    this.orderno = orderno;
    this.title = title;
    this.isshare = isshare;
    this.isshareowner = isshareowner;
    this.details = details;
  }


  public JSONObject _genJson() {
    JSONObject result = new JSONObject();
    result.put("sortschemeid", this.schemeid);
    result.put("schemename", this.title);
    result.put("orderno", this.orderno);
    result.put("isshare", this.isshare);
    result.put("isshareowner", this.isshareowner);
    result.put("userid", this.getFUser() != null ? this.getFUser().getUserid() : null);
    return result;
  }

  @Id
  @GenericGenerator(name = "generator", strategy = "uuid.hex")
  @GeneratedValue(generator = "generator")
  @Column(name = "schemeid", unique = true, nullable = false, length = 40)
  public String getSchemeid() {
    return this.schemeid;
  }

  public void setSchemeid(String schemeid) {
    this.schemeid = schemeid;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "objectid", nullable = false)
  @JSONField(serialize = false)
  public FDataobject getFDataobject() {
    return this.FDataobject;
  }

  public void setFDataobject(FDataobject FDataobject) {
    this.FDataobject = FDataobject;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userid")
  @JSONField(serialize = false)
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


  @Column(name = "title", nullable = false, length = 50)
  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  @Column(name = "isshare")
  public Boolean getIsshare() {
    return this.isshare;
  }

  public void setIsshare(Boolean isshare) {
    this.isshare = isshare;
  }


  @Column(name = "isshareowner")
  public Boolean getIsshareowner() {
    return this.isshareowner;
  }

  public void setIsshareowner(Boolean isshareowner) {
    this.isshareowner = isshareowner;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "fovGridsortscheme")
  @OrderBy("orderno")
  @Cache(region = "beanCache", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  @JSONField(serialize = false)
  public Set<FovGridsortschemedetail> getDetails() {
    return this.details;
  }

  public void setDetails(Set<FovGridsortschemedetail> details) {
    this.details = details;
  }



}


