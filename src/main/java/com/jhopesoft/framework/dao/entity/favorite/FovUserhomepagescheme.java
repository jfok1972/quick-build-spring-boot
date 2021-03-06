package com.jhopesoft.framework.dao.entity.favorite;
// default package
// Generated 2017-11-19 9:39:10 by Hibernate Tools 5.2.0.Beta1


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.dao.entity.viewsetting.FovHomepagescheme;
/**
 * FovUserhomepagescheme generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@DynamicUpdate
@Table(name = "fov_userhomepagescheme", uniqueConstraints = @UniqueConstraint(columnNames = {
    "homepageschemeid", "userid"}))
public class FovUserhomepagescheme implements java.io.Serializable {
  
  private String userhomepageid;
  private FUser FUser;
  private FovHomepagescheme fovHomepagescheme;
  private Integer orderno;
  private Boolean isdefault;

  public FovUserhomepagescheme() {}

  public FovUserhomepagescheme(FUser FUser, FovHomepagescheme fovHomepagescheme) {
    this.FUser = FUser;
    this.fovHomepagescheme = fovHomepagescheme;
  }

  @Id
  @GeneratedValue(generator = "generator")
  @GenericGenerator(name = "generator", strategy = "uuid.hex")
  @Column(name = "userhomepageid", unique = true, nullable = false, length = 40)
  public String getUserhomepageid() {
    return this.userhomepageid;
  }

  public void setUserhomepageid(String userhomepageid) {
    this.userhomepageid = userhomepageid;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userid", nullable = false)
  public FUser getFUser() {
    return this.FUser;
  }

  public void setFUser(FUser FUser) {
    this.FUser = FUser;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "homepageschemeid", nullable = false)
  public FovHomepagescheme getFovHomepagescheme() {
    return this.fovHomepagescheme;
  }

  public void setFovHomepagescheme(FovHomepagescheme fovHomepagescheme) {
    this.fovHomepagescheme = fovHomepagescheme;
  }

  @Column(name = "orderno")
  public Integer getOrderno() {
    return this.orderno;
  }

  public void setOrderno(Integer orderno) {
    this.orderno = orderno;
  }

  @Column(name = "isdefault")
  public Boolean getIsdefault() {
    return this.isdefault;
  }

  public void setIsdefault(Boolean isdefault) {
    this.isdefault = isdefault;
  }

}


