package com.jhopesoft.framework.dao.entity.dataobject;
// default package

// Generated 2017-1-20 11:35:16 by Hibernate Tools 5.2.0.Beta1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicUpdate;

import com.jhopesoft.framework.dao.entity.module.FModulefunction;
import com.jhopesoft.framework.utils.CommonUtils;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@SuppressWarnings("serial")
@Entity
@DynamicUpdate
@Table(name = "f_dataobjectbasefuncion", uniqueConstraints = { @UniqueConstraint(columnNames = "title"),
    @UniqueConstraint(columnNames = "fcode") })
public class FDataobjectbasefuncion implements java.io.Serializable, Comparable<FDataobjectbasefuncion> {

  public static final String ATTACHMENTQUERY = "attachmentquery";
  public static final String ATTACHMENTADD = "attachmentadd";
  public static final String ATTACHMENTEDIT = "attachmentedit";
  public static final String ATTACHMENTDELETE = "attachmentdelete";
  public static final String QUERY = "query";
  public static final String NEW = "new";
  public static final String NEWNAVIGATE = "newnavigate";

  public static final String EDIT = "edit";
  public static final String DELETE = "delete";

  private String basefunctionid;
  private String title;
  private String fcode;
  private String fdescription;
  private Integer orderno;
  private Boolean isdisable;
  private String iconcls;
  private String icon;
  private byte[] iconfile;
  private Integer minselectrecordnum;
  private Integer maxselectrecordnum;
  private String menuname;
  private String menusetting;
  private String othersetting;
  private String windowclass;
  private String functionname;
  private String xtype;
  private Boolean clickvalidate;
  private String ftype;
  private String remark;
  private Set<FModulefunction> FModulefunctions = new HashSet<FModulefunction>(0);

  public FDataobjectbasefuncion() {
  }

  public FDataobjectbasefuncion(String basefunctionid, String title, String fcode) {
    this.basefunctionid = basefunctionid;
    this.title = title;
    this.fcode = fcode;
  }

  public FDataobjectbasefuncion(String basefunctionid, String title, String fcode, String fdescription, Integer orderno,
      Boolean isdisable, String iconcls, String icon, byte[] iconfile, Integer minselectrecordnum,
      Integer maxselectrecordnum, String menuname, String menusetting, String othersetting, String windowclass,
      String functionname, String xtype, Boolean clickvalidate, String ftype, String remark,
      Set<FModulefunction> FModulefunctions) {
    this.basefunctionid = basefunctionid;
    this.title = title;
    this.fcode = fcode;
    this.fdescription = fdescription;
    this.orderno = orderno;
    this.isdisable = isdisable;
    this.iconcls = iconcls;
    this.icon = icon;
    this.iconfile = iconfile;
    this.minselectrecordnum = minselectrecordnum;
    this.maxselectrecordnum = maxselectrecordnum;
    this.menuname = menuname;
    this.menusetting = menusetting;
    this.othersetting = othersetting;
    this.windowclass = windowclass;
    this.functionname = functionname;
    this.xtype = xtype;
    this.clickvalidate = clickvalidate;
    this.ftype = ftype;
    this.remark = remark;
    this.FModulefunctions = FModulefunctions;
  }

  public FDataobjectbasefuncion(String basefunctionid, String title, String fcode, String fdescription, Integer orderno,
      Boolean isdisable, String iconcls, String icon, byte[] iconfile, Integer minselectrecordnum,
      Integer maxselectrecordnum, String menuname, String menusetting, String othersetting, String windowclass,
      String functionname, String xtype, Boolean clickvalidate, String ftype, String remark) {
    super();
    this.basefunctionid = basefunctionid;
    this.title = title;
    this.fcode = fcode;
    this.fdescription = fdescription;
    this.orderno = orderno;
    this.isdisable = isdisable;
    this.iconcls = iconcls;
    this.icon = icon;
    this.iconfile = iconfile;
    this.minselectrecordnum = minselectrecordnum;
    this.maxselectrecordnum = maxselectrecordnum;
    this.menuname = menuname;
    this.menusetting = menusetting;
    this.othersetting = othersetting;
    this.windowclass = windowclass;
    this.functionname = functionname;
    this.xtype = xtype;
    this.clickvalidate = clickvalidate;
    this.ftype = ftype;
    this.remark = remark;
  }

  @Id
  @Column(name = "basefunctionid", unique = true, nullable = false, length = 40)
  public String getBasefunctionid() {
    return this.basefunctionid;
  }

  public void setBasefunctionid(String basefunctionid) {
    this.basefunctionid = basefunctionid;
  }

  @Column(name = "title", unique = true, nullable = false, length = 50)
  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Column(name = "fcode", unique = true, nullable = false, length = 50)
  public String getFcode() {
    return this.fcode;
  }

  public void setFcode(String fcode) {
    this.fcode = fcode;
  }

  @Column(name = "fdescription", length = 200)
  public String getFdescription() {
    return this.fdescription;
  }

  public void setFdescription(String fdescription) {
    this.fdescription = fdescription;
  }

  @Column(name = "orderno")
  public Integer getOrderno() {
    return this.orderno;
  }

  public void setOrderno(Integer orderno) {
    this.orderno = orderno;
  }

  @Column(name = "isdisable")
  public Boolean getIsdisable() {
    return this.isdisable;
  }

  public void setIsdisable(Boolean isdisable) {
    this.isdisable = isdisable;
  }

  @Column(name = "iconcls", length = 200)
  public String getIconcls() {
    return this.iconcls;
  }

  public void setIconcls(String iconcls) {
    this.iconcls = iconcls;
  }

  @Column(name = "icon", length = 200)
  public String getIcon() {
    return this.icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  @Column(name = "iconfile")
  public byte[] getIconfile() {
    return this.iconfile;
  }

  public void setIconfile(byte[] iconfile) {
    this.iconfile = CommonUtils.emptyBytesToNull(iconfile);
  }

  @Column(name = "minselectrecordnum")
  public Integer getMinselectrecordnum() {
    return this.minselectrecordnum;
  }

  public void setMinselectrecordnum(Integer minselectrecordnum) {
    this.minselectrecordnum = minselectrecordnum;
  }

  @Column(name = "maxselectrecordnum")
  public Integer getMaxselectrecordnum() {
    return this.maxselectrecordnum;
  }

  public void setMaxselectrecordnum(Integer maxselectrecordnum) {
    this.maxselectrecordnum = maxselectrecordnum;
  }

  @Column(name = "menuname", length = 50)
  public String getMenuname() {
    return this.menuname;
  }

  public void setMenuname(String menuname) {
    this.menuname = menuname;
  }

  @Column(name = "menusetting", length = 200)
  public String getMenusetting() {
    return this.menusetting;
  }

  public void setMenusetting(String menusetting) {
    this.menusetting = menusetting;
  }

  @Column(name = "othersetting", length = 200)
  public String getOthersetting() {
    return this.othersetting;
  }

  public void setOthersetting(String othersetting) {
    this.othersetting = othersetting;
  }

  @Column(name = "windowclass", length = 200)
  public String getWindowclass() {
    return this.windowclass;
  }

  public void setWindowclass(String windowclass) {
    this.windowclass = windowclass;
  }

  @Column(name = "functionname", length = 200)
  public String getFunctionname() {
    return this.functionname;
  }

  public void setFunctionname(String functionname) {
    this.functionname = functionname;
  }

  @Column(name = "xtype", length = 200)
  public String getXtype() {
    return this.xtype;
  }

  public void setXtype(String xtype) {
    this.xtype = xtype;
  }

  @Column(name = "clickvalidate")
  public Boolean getClickvalidate() {
    return this.clickvalidate;
  }

  public void setClickvalidate(Boolean clickvalidate) {
    this.clickvalidate = clickvalidate;
  }

  @Column(name = "ftype", length = 50)
  public String getFtype() {
    return this.ftype;
  }

  public void setFtype(String ftype) {
    this.ftype = ftype;
  }

  @Column(name = "remark", length = 200)
  public String getRemark() {
    return this.remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "FDataobjectbasefuncion")
  public Set<FModulefunction> getFModulefunctions() {
    return this.FModulefunctions;
  }

  public void setFModulefunctions(Set<FModulefunction> FModulefunctions) {
    this.FModulefunctions = FModulefunctions;
  }

  @Override
  public int compareTo(FDataobjectbasefuncion o) {
    return this.orderno - o.orderno;
  }

}
