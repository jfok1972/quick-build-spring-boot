package com.jhopesoft.framework.dao.entity.datamining;
// default package

// Generated 2017-6-19 9:00:05 by Hibernate Tools 5.2.0.Beta1

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

import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.utils.FFunction;

/**
 *
 * @author 蒋锋
 * 
 */

@Entity
@DynamicUpdate
@SuppressWarnings("serial")
@Table(name = "f_dataminingrowgrouppath")
public class FDataminingrowgrouppath implements java.io.Serializable {

  private String pathid;
  private FDataminingscheme FDataminingscheme;
  private FDataobjectfield FDataobjectfield;
  private FFunction FFunction;
  private String fieldfunction;
  private int orderno;
  private String pathtype;
  private String conditionpath;
  private String title;
  private String groupcondition;
  private String fieldahead;
  private String fieldgrouptype;
  private Boolean addselectedchildrens;
  private Integer pos;
  private String acondition;
  private String conditionpaths;
  private String othersetting;
  private String remark;

  public FDataminingrowgrouppath() {
  }

  public String _getFieldidahead() {
    String fieldid = getFDataobjectfield().getFieldid();
    if (getFieldahead() != null && getFieldahead().length() > 0) {
      fieldid = getFieldahead() + "|" + fieldid;
    }
    return fieldid;
  }

  public String _getFieldid() {
    if (getFDataobjectfield() != null) {
      String fieldid = getFDataobjectfield().getFieldid();
      if (getFieldahead() != null && getFieldahead().length() > 0) {
        fieldid = getFieldahead() + "|" + fieldid;
      }
      if (getFieldgrouptype() != null && getFieldgrouptype().length() > 0) {
        fieldid = fieldid + '-' + getFieldgrouptype();
      }
      return fieldid;
    }
    return null;
  }

  @Id
  @GeneratedValue(generator = "generator")
  @GenericGenerator(name = "generator", strategy = "uuid.hex")

  @Column(name = "pathid", unique = true, nullable = false, length = 40)
  public String getPathid() {
    return this.pathid;
  }

  public void setPathid(String pathid) {
    this.pathid = pathid;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "schemeid", nullable = false)
  public FDataminingscheme getFDataminingscheme() {
    return this.FDataminingscheme;
  }

  public void setFDataminingscheme(FDataminingscheme FDataminingscheme) {
    this.FDataminingscheme = FDataminingscheme;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fieldid")
  public FDataobjectfield getFDataobjectfield() {
    return this.FDataobjectfield;
  }

  public void setFDataobjectfield(FDataobjectfield FDataobjectfield) {
    this.FDataobjectfield = FDataobjectfield;
  }

  @Column(name = "orderno", nullable = false)
  public int getOrderno() {
    return this.orderno;
  }

  public void setOrderno(int orderno) {
    this.orderno = orderno;
  }

  @Column(name = "pathtype", nullable = false, length = 40)
  public String getPathtype() {
    return this.pathtype;
  }

  public void setPathtype(String pathtype) {
    this.pathtype = pathtype;
  }

  @Column(name = "conditionpath", length = 65535)
  public String getConditionpath() {
    return this.conditionpath;
  }

  public void setConditionpath(String conditionpath) {
    this.conditionpath = conditionpath;
  }

  @Column(name = "title", length = 100)
  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Column(name = "groupcondition", length = 65535)
  public String getGroupcondition() {
    return this.groupcondition;
  }

  public void setGroupcondition(String groupcondition) {
    this.groupcondition = groupcondition;
  }

  @Column(name = "fieldahead", length = 200)
  public String getFieldahead() {
    return this.fieldahead;
  }

  public void setFieldahead(String fieldahead) {
    this.fieldahead = fieldahead;
  }

  @Column(name = "fieldgrouptype", length = 40)
  public String getFieldgrouptype() {
    return this.fieldgrouptype;
  }

  public void setFieldgrouptype(String fieldgrouptype) {
    this.fieldgrouptype = fieldgrouptype;
  }

  @Column(name = "addselectedchildrens")
  public Boolean getAddselectedchildrens() {
    return this.addselectedchildrens;
  }

  public void setAddselectedchildrens(Boolean addselectedchildrens) {
    this.addselectedchildrens = addselectedchildrens;
  }

  @Column(name = "pos")
  public Integer getPos() {
    return pos;
  }

  public void setPos(Integer pos) {
    this.pos = pos;
  }

  @Column(name = "acondition", length = 65535)
  public String getAcondition() {
    return this.acondition;
  }

  public void setAcondition(String acondition) {
    this.acondition = acondition;
  }

  @Column(name = "conditionpaths", length = 65535)
  public String getConditionpaths() {
    return this.conditionpaths;
  }

  public void setConditionpaths(String conditionpaths) {
    this.conditionpaths = conditionpaths;
  }

  @Column(name = "othersetting", length = 200)
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "functionid")
  public FFunction getFFunction() {
    return this.FFunction;
  }

  public void setFFunction(FFunction FFunction) {
    this.FFunction = FFunction;
  }

  @Column(name = "fieldfunction", length = 200)
  public String getFieldfunction() {
    return this.fieldfunction;
  }

  public void setFieldfunction(String fieldfunction) {
    this.fieldfunction = fieldfunction;
  }

  @Override
  public String toString() {
    return "FDataminingrowgrouppath [orderno=" + orderno + ", pathtype=" + pathtype + ", conditionpath=" + conditionpath
        + ", title=" + title + ", groupcondition=" + groupcondition + ", fieldahead=" + fieldahead + ", fieldgrouptype="
        + fieldgrouptype + ", addselectedchildrens=" + addselectedchildrens + ", acondition=" + acondition
        + ", conditionpaths=" + conditionpaths + "]";
  }

}
