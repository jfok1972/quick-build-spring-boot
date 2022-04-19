package com.jhopesoft.framework.dao.entity.viewsetting;
// Generated 2016-11-3 11:11:36 by Hibernate Tools 5.2.0.Beta1

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

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.system.FUser;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Entity
@DynamicUpdate
@Table(name = "fov_gridnavigatescheme")
public class FovGridnavigatescheme implements java.io.Serializable {

  private static final long serialVersionUID = 9016301776590320718L;
  private String schemeid;
  private FDataobject FDataobject;
  private FUser FUser;
  private Integer orderno;
  private String title;
  private String iconcls;
  private Boolean cascading;
  private Boolean enabled;
  private Boolean dynamicexpand;
  private Boolean allownullrecordbuttton;
  private Boolean iscontainnullrecord;
  private Boolean isshare;
  private Boolean isshareowner;
  private String remark;
  private Set<FovGridnavigateschemedetail> details = new HashSet<FovGridnavigateschemedetail>(0);

  public FovGridnavigatescheme() {}

  public FovGridnavigatescheme(FDataobject fDataobject, FUser fUser, Integer orderno, String title, String iconcls,
      Boolean cascading, Boolean enabled) {
    super();
    FDataobject = fDataobject;
    FUser = fUser;
    this.orderno = orderno;
    this.title = title;
    this.iconcls = iconcls;
    this.cascading = cascading;
    this.enabled = enabled;
  }

  public JSONObject genJson() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("navigateschemeid", this.schemeid);
    jsonObject.put("tf_order", "item_" + this.schemeid);
    jsonObject.put("tf_text", this.title);
    jsonObject.put("tf_dynamicExpand", this.dynamicexpand);
    jsonObject.put("tf_cascading", this.cascading);
    jsonObject.put("tf_iconCls", this.iconcls);
    jsonObject.put("tf_allLevel", this.details.size());
    jsonObject.put("tf_allowNullRecordButton", this.allownullrecordbuttton);
    jsonObject.put("tf_isContainNullRecord", this.iscontainnullrecord);
    jsonObject.put("isshare", this.isshare);
    jsonObject.put("isshareowner", this.isshareowner);
    if (FUser != null) {
      if (FUser.getUserid().equals(Local.getUserid())) {
        jsonObject.put("tf_username", "我的方案");
        jsonObject.put("isowner", true);
      } else {
        jsonObject.put("isothershare", true);
        jsonObject.put("tf_username", FUser.getUsername());
      }
    } else {
      jsonObject.put("issystem", true);
      jsonObject.put("tf_username", "系统方案");
    }
    jsonObject.put("othersetting", this.getRemark());
    return jsonObject;
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

  @Column(name = "iconcls", length = 50)
  public String getIconcls() {
    return this.iconcls;
  }

  public void setIconcls(String iconcls) {
    this.iconcls = iconcls;
  }

  @Column(name = "cascading")
  public Boolean getCascading() {
    return this.cascading;
  }

  public void setCascading(Boolean cascading) {
    this.cascading = cascading;
  }

  @Column(name = "enabled")
  public Boolean getEnabled() {
    return this.enabled == null ? false : this.enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  @Column(name = "dynamicexpand")
  public Boolean getDynamicexpand() {
    return this.dynamicexpand;
  }

  public void setDynamicexpand(Boolean dynamicexpand) {
    this.dynamicexpand = dynamicexpand;
  }

  @Column(name = "allownullrecordbuttton")
  public Boolean getAllownullrecordbuttton() {
    return this.allownullrecordbuttton;
  }

  public void setAllownullrecordbuttton(Boolean allownullrecordbuttton) {
    this.allownullrecordbuttton = allownullrecordbuttton;
  }

  @Column(name = "iscontainnullrecord")
  public Boolean getIscontainnullrecord() {
    return this.iscontainnullrecord;
  }

  public void setIscontainnullrecord(Boolean iscontainnullrecord) {
    this.iscontainnullrecord = iscontainnullrecord;
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

  @Column(name = "remark", length = 200)
  public String getRemark() {
    return this.remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "fovGridnavigatescheme")
  @OrderBy("orderno")
  public Set<FovGridnavigateschemedetail> getDetails() {
    return this.details;
  }

  public void setDetails(Set<FovGridnavigateschemedetail> details) {
    this.details = details;
  }

}
