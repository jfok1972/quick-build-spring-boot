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

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.annotation.JSONField;
import com.jhopesoft.framework.dao.entity.datamining.FDataminingscheme;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Entity
@DynamicUpdate
@Table(name = "fov_homepageschemedetail")
public class FovHomepageschemedetail implements java.io.Serializable {

  private String detailid;
  private FDataminingscheme FDataminingscheme;
  private FDataobject FDataobject;
  private FovChartscheme fovChartscheme;
  private FovHomepagescheme fovHomepagescheme;
  private FovHomepageschemedetail fovHomepageschemedetail;
  private FovDataobjectwidget fovDataobjectwidget;
  private FovFilterscheme fovFilterscheme;

  private String title;
  private int orderno;
  private String xtype;
  private String layout;
  private String region;
  private Integer rowss;
  private Integer cols;
  private Integer rowspan;
  private Integer colspan;
  private Integer flex;
  private String widths;
  private Boolean collapsible;
  private Boolean collapsed;
  private String width;
  private String height;
  private String othersetting;
  private String remark;
  private Set<FovHomepageschemedetail> fovHomepageschemedetails = new HashSet<FovHomepageschemedetail>(0);

  public FovHomepageschemedetail() {
  }

  @Id
  @GenericGenerator(name = "generator", strategy = "uuid.hex")
  @GeneratedValue(generator = "generator")
  @Column(name = "detailid", unique = true, nullable = false, length = 40)
  public String getDetailid() {
    return this.detailid;
  }

  public void setDetailid(String detailid) {
    this.detailid = detailid;
  }

  @JSONField(serialize = false)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "schemeid")
  public FDataminingscheme getFDataminingscheme() {
    return this.FDataminingscheme;
  }

  @Transient
  public String getDataminingschemeid() {
    if (this.FDataminingscheme != null) {
      return this.FDataminingscheme.getSchemeid();
    } else {
      return null;
    }
  }

  @Transient
  public String getDataminingobjectid() {
    if (this.FDataminingscheme != null) {
      return this.FDataminingscheme.getFDataobject().getObjectid();
    } else {
      return null;
    }
  }

  public void setFDataminingscheme(FDataminingscheme FDataminingscheme) {
    this.FDataminingscheme = FDataminingscheme;
  }

  @JSONField(serialize = false)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "objectid")
  public FDataobject getFDataobject() {
    return this.FDataobject;
  }

  @Transient
  public String getDataobjectid() {
    if (this.FDataobject != null) {
      return this.FDataobject.getObjectid();
    } else {
      return null;
    }
  }

  public void setFDataobject(FDataobject FDataobject) {
    this.FDataobject = FDataobject;
  }

  @JSONField(serialize = false)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chartschemeid")
  public FovChartscheme getFovChartscheme() {
    return this.fovChartscheme;
  }

  @Transient
  public String getChartschemeid() {
    if (this.fovChartscheme != null) {
      return this.fovChartscheme.getChartschemeid();
    } else {
      return null;
    }
  }

  @Transient
  public String getChartobjectid() {
    if (this.fovChartscheme != null) {
      if (this.fovChartscheme.getFDataminingscheme() != null) {
        return this.fovChartscheme.getFDataminingscheme().getFDataobject().getObjectid();
      } else if (this.fovChartscheme.getFDataobject() != null) {
        return this.fovChartscheme.getFDataobject().getObjectid();
      }
    }
    return null;
  }

  public void setFovChartscheme(FovChartscheme fovChartscheme) {
    this.fovChartscheme = fovChartscheme;
  }

  @JSONField(serialize = false)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "homepageschemeid")
  public FovHomepagescheme getFovHomepagescheme() {
    return this.fovHomepagescheme;
  }

  public void setFovHomepagescheme(FovHomepagescheme fovHomepagescheme) {
    this.fovHomepagescheme = fovHomepagescheme;
  }

  @JSONField(serialize = false)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parentid")
  public FovHomepageschemedetail getFovHomepageschemedetail() {
    return this.fovHomepageschemedetail;
  }

  public void setFovHomepageschemedetail(FovHomepageschemedetail fovHomepageschemedetail) {
    this.fovHomepageschemedetail = fovHomepageschemedetail;
  }

  @Column(name = "title", length = 50)
  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Column(name = "orderno", nullable = false)
  public int getOrderno() {
    return this.orderno;
  }

  public void setOrderno(int orderno) {
    this.orderno = orderno;
  }

  @Column(name = "xtype", length = 50)
  public String getXtype() {
    return this.xtype;
  }

  public void setXtype(String xtype) {
    this.xtype = xtype;
  }

  @Column(name = "layout", length = 50)
  public String getLayout() {
    return this.layout;
  }

  public void setLayout(String layout) {
    this.layout = layout;
  }

  @Column(name = "region", length = 20)
  public String getRegion() {
    return this.region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  @Column(name = "rowss")
  public Integer getRowss() {
    return this.rowss;
  }

  public void setRowss(Integer rowss) {
    this.rowss = rowss;
  }

  @Column(name = "cols")
  public Integer getCols() {
    return this.cols;
  }

  public void setCols(Integer cols) {
    this.cols = cols;
  }

  @Column(name = "rowspan")
  public Integer getRowspan() {
    return this.rowspan;
  }

  public void setRowspan(Integer rowspan) {
    this.rowspan = rowspan;
  }

  @Column(name = "colspan")
  public Integer getColspan() {
    return this.colspan;
  }

  public void setColspan(Integer colspan) {
    this.colspan = colspan;
  }

  @Column(name = "flex")
  public Integer getFlex() {
    return this.flex;
  }

  public void setFlex(Integer flex) {
    this.flex = flex;
  }

  @Column(name = "widths", length = 200)
  public String getWidths() {
    return this.widths;
  }

  public void setWidths(String widths) {
    this.widths = widths;
  }

  @Column(name = "collapsible")
  public Boolean getCollapsible() {
    return this.collapsible;
  }

  public void setCollapsible(Boolean collapsible) {
    this.collapsible = collapsible;
  }

  @Column(name = "collapsed")
  public Boolean getCollapsed() {
    return this.collapsed;
  }

  public void setCollapsed(Boolean collapsed) {
    this.collapsed = collapsed;
  }

  @Column(name = "width", length = 20)
  public String getWidth() {
    return this.width;
  }

  public void setWidth(String width) {
    this.width = width;
  }

  @Column(name = "height", length = 20)
  public String getHeight() {
    return this.height;
  }

  public void setHeight(String height) {
    this.height = height;
  }

  @Column(name = "othersetting", length = 2000)
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

  @JSONField(serialize = false)
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "fovHomepageschemedetail")
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "widgetid")
  public FovDataobjectwidget getFovDataobjectwidget() {
    return this.fovDataobjectwidget;
  }

  public void setFovDataobjectwidget(FovDataobjectwidget fovDataobjectwidget) {
    this.fovDataobjectwidget = fovDataobjectwidget;
  }

  @JSONField(serialize = false)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "filterschemeid")
  public FovFilterscheme getFovFilterscheme() {
    return fovFilterscheme;
  }

  public void setFovFilterscheme(FovFilterscheme fovFilterscheme) {
    this.fovFilterscheme = fovFilterscheme;
  }

  /**
   * 如果设置了筛选方案，返回方案id
   * 
   * @return
   */
  @Transient
  public String getFilterSchemeid() {
    if (getFovFilterscheme() != null) {
      return getFovFilterscheme().getFilterschemeid();
    }
    return null;
  }

  /**
   * 如果设置了筛选方案，返回模块id
   * 
   * @return
   */
  @Transient
  public String getModuleName() {
    if (getFovFilterscheme() != null) {
      return getFovFilterscheme().getFDataobject().getObjectid();
    }
    return null;
  }

}
