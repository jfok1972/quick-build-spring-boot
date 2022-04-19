package com.jhopesoft.framework.dao.entity.viewsetting;
// default package
// Generated 2017-10-16 12:53:11 by Hibernate Tools 5.2.0.Beta1


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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.annotation.JSONField;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.favorite.FUserobjectfavorite;
import com.jhopesoft.framework.dao.entity.module.FModule;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@SuppressWarnings("serial")
@DynamicUpdate
@Entity
@Table(name = "fov_homepagescheme", uniqueConstraints = @UniqueConstraint(columnNames = {"userid", "schemename"}))
public class FovHomepagescheme implements java.io.Serializable {


  private String homepageschemeid;
  private FUser FUser;
  private String schemename;
  private Integer orderno;
  private String operatetype;
  private String homepagetype;
  private String iconurl;
  private String iconcls;
  private String layout;
  private Integer height;
  private Integer width;
  private Integer cols;
  private String widths;
  private String othersetting;
  private String buttonsposition;
  private Boolean isshare;
  private Boolean isshareowner;
  private String remark;
  private Set<FovHomepageschemedetail> fovHomepageschemedetails = new HashSet<FovHomepageschemedetail>(0);
  private Set<FModule> FModules = new HashSet<FModule>(0);

  public FovHomepagescheme() {}


  public FovHomepagescheme(String homepageschemeid, String schemename) {
    this.homepageschemeid = homepageschemeid;
    this.schemename = schemename;
  }


  @Id
  @GenericGenerator(name = "generator", strategy = "uuid.hex")
  @GeneratedValue(generator = "generator")
  @Column(name = "homepageschemeid", unique = true, nullable = false, length = 40)
  public String getHomepageschemeid() {
    return this.homepageschemeid;
  }

  public void setHomepageschemeid(String homepageschemeid) {
    this.homepageschemeid = homepageschemeid;
  }

  @Transient
  public boolean getHasfavorite() {
    return Local.getDao().findByPropertyFirst(FUserobjectfavorite.class, "userid", Local.getUserid(),
        "homepageschemeid", homepageschemeid) != null;
  }

  @JSONField(name = "icon")
  @Column(length = 50)
  public String getIconurl() {
    return iconurl;
  }


  public void setIconurl(String iconurl) {
    this.iconurl = iconurl;
  }

  @JSONField(name = "iconCls")
  @Column(length = 50)
  public String getIconcls() {
    return iconcls;
  }


  public void setIconcls(String iconcls) {
    this.iconcls = iconcls;
  }

  @JSONField(serialize = false)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userid")
  public FUser getFUser() {
    return this.FUser;
  }

  public void setFUser(FUser FUser) {
    this.FUser = FUser;
  }

  @JSONField(name = "title")
  @Column(name = "schemename", nullable = false, length = 50)
  public String getSchemename() {
    return this.schemename;
  }

  public void setSchemename(String schemename) {
    this.schemename = schemename;
  }


  @Column(name = "orderno")
  public Integer getOrderno() {
    return this.orderno != null ? this.orderno : 0;
  }

  public void setOrderno(Integer orderno) {
    this.orderno = orderno;
  }


  @Column(name = "operatetype", length = 20)
  public String getOperatetype() {
    return this.operatetype;
  }

  public void setOperatetype(String operatetype) {
    this.operatetype = operatetype;
  }


  @Column(name = "homepagetype", length = 20)
  public String getHomepagetype() {
    return this.homepagetype;
  }

  public void setHomepagetype(String homepagetype) {
    this.homepagetype = homepagetype;
  }


  @Column(name = "layout", length = 50)
  public String getLayout() {
    return this.layout;
  }

  public void setLayout(String layout) {
    this.layout = layout;
  }


  @Column(name = "height")
  public Integer getHeight() {
    return this.height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }


  @Column(name = "width")
  public Integer getWidth() {
    return this.width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }


  @Column(name = "cols")
  public Integer getCols() {
    return this.cols;
  }

  public void setCols(Integer cols) {
    this.cols = cols;
  }


  @Column(name = "widths", length = 200)
  public String getWidths() {
    return this.widths;
  }

  public void setWidths(String widths) {
    this.widths = widths;
  }


  @Column(name = "othersetting", length = 2000)
  public String getOthersetting() {
    return this.othersetting;
  }

  public void setOthersetting(String othersetting) {
    this.othersetting = othersetting;
  }


  @Column(name = "buttonsposition", length = 20)
  public String getButtonsposition() {
    return this.buttonsposition;
  }

  public void setButtonsposition(String buttonsposition) {
    this.buttonsposition = buttonsposition;
  }


  @Column(name = "remark", length = 200)
  public String getRemark() {
    return this.remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  @JSONField(serialize = false)
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "fovHomepagescheme")
  @OrderBy("orderno")
  public Set<FovHomepageschemedetail> getFovHomepageschemedetails() {
    return this.fovHomepageschemedetails;
  }

  @Transient
  public Set<FovHomepageschemedetail> getItems() {
    return getFovHomepageschemedetails();
  }

  public void setFovHomepageschemedetails(Set<FovHomepageschemedetail> fovHomepageschemedetails) {
    this.fovHomepageschemedetails = fovHomepageschemedetails;
  }

  @JSONField(serialize = false)
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "fovHomepagescheme")
  public Set<FModule> getFModules() {
    return this.FModules;
  }

  public void setFModules(Set<FModule> FModules) {
    this.FModules = FModules;
  }


  public Boolean getIsshare() {
    return isshare;
  }


  public void setIsshare(Boolean isshare) {
    this.isshare = isshare;
  }


  public Boolean getIsshareowner() {
    return isshareowner;
  }


  public void setIsshareowner(Boolean isshareowner) {
    this.isshareowner = isshareowner;
  }

}


