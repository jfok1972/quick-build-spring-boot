package com.jhopesoft.framework.dao.entity.datamining;
// default package
// Generated 2017-9-30 11:16:34 by Hibernate Tools 5.2.0.Beta1


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

/**
 *
 * @author 蒋锋
 * 
 */

@Entity
@SuppressWarnings("serial")
@DynamicUpdate
@Table(name = "f_dataminingfilter")
public class FDataminingfilter implements java.io.Serializable {


  private String filterid;
  private FDataminingscheme FDataminingscheme;
  private String othersetting;
  private String remark;

  public FDataminingfilter() {}


  public FDataminingfilter(String filterid) {
    this.filterid = filterid;
  }

  public FDataminingfilter(String filterid, FDataminingscheme FDataminingscheme, String othersetting, String remark) {
    this.filterid = filterid;
    this.FDataminingscheme = FDataminingscheme;
    this.othersetting = othersetting;
    this.remark = remark;
  }

  @Id
  @GeneratedValue(generator = "generator")
  @GenericGenerator(name = "generator", strategy = "uuid.hex")
  @Column(name = "filterid", unique = true, nullable = false, length = 40)
  public String getFilterid() {
    return this.filterid;
  }

  public void setFilterid(String filterid) {
    this.filterid = filterid;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "schemeid")
  public FDataminingscheme getFDataminingscheme() {
    return this.FDataminingscheme;
  }

  public void setFDataminingscheme(FDataminingscheme FDataminingscheme) {
    this.FDataminingscheme = FDataminingscheme;
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



}


