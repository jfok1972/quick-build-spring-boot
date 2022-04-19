package com.jhopesoft.framework.dao.entity.viewsetting;
// default package

// Generated 2017-1-28 13:13:20 by 蒋锋

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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectFieldUtils;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Entity
@DynamicUpdate
@Table(name = "fov_dataobjectassociate", uniqueConstraints = @UniqueConstraint(columnNames = { "objectid", "region" }))
public class FovDataobjectassociate implements java.io.Serializable {

  private String associateid;
  private FDataobject FDataobject;
  private String region;
  private Integer weight;
  private String worh;
  private Boolean iscollapsed;
  private Boolean ishidden;
  private Boolean isdisable;
  private Boolean isdisabledesign;
  private String othersetting;
  private Set<FovDataobjectassociatedetail> fovDataobjectassociatedetails = new HashSet<FovDataobjectassociatedetail>(
      0);

  public FovDataobjectassociate() {
  }

  public FovDataobjectassociate(String associateid, FDataobject FDataobject, String region) {
    this.associateid = associateid;
    this.FDataobject = FDataobject;
    this.region = region;
  }

  @Transient
  public JSONArray getDetails() {
    JSONArray result = new JSONArray();
    for (FovDataobjectassociatedetail detail : getFovDataobjectassociatedetails()) {
      JSONObject object = new JSONObject();
      object.put("associatedetailid", detail.getAssociatedetailid());
      object.put(Constants.TITLE, detail.getTitle());
      object.put("issystem", detail.getFUser() == null);

      if (detail.getFDataobjectBySubobjectid() != null) {
        object.put("subobjectname", detail.getFDataobjectBySubobjectid().getObjectname());
        object.put("fieldahead", detail.getFieldahead());
        object.put("defaulttitle",
            DataObjectFieldUtils.getPCModuletitle(FDataobject.getObjectname(), detail.getFieldahead()));
        object.put("subobjectnavigate", detail.getSubobjectnavigate());
        object.put("subobjectsouthregion", detail.getSubobjectsouthregion());
        object.put("subobjecteastregion", detail.getSubobjecteastregion());
      } else if (detail.getFovFormscheme() != null) {
        object.put("formschemeid", detail.getFovFormscheme().getFormschemeid());
        object.put("defaulttitle", detail.getFovFormscheme().getSchemename());
        object.put("usedfornew", true);
        object.put("usedforedit", true);
      } else if (detail.getXtype() != null) {
        object.put("xtype", detail.getXtype());
      } else if (detail.getIsattchment()) {
        object.put("isattachment", true);
      }
      result.add(object);
    }
    return result;
  }

  @Id
  @GeneratedValue(generator = "generator")
  @GenericGenerator(name = "generator", strategy = "uuid.hex")
  @Column(name = "associateid", unique = true, nullable = false, length = 40)
  public String getAssociateid() {
    return this.associateid;
  }

  public void setAssociateid(String associateid) {
    this.associateid = associateid;
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

  @Column(name = "region", nullable = false, length = 10)
  public String getRegion() {
    return this.region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  @Column(name = "weight")
  public Integer getWeight() {
    return this.weight;
  }

  public void setWeight(Integer weight) {
    this.weight = weight;
  }

  @Column(name = "worh", length = 20)
  public String getWorh() {
    return this.worh;
  }

  public void setWorh(String worh) {
    this.worh = worh;
  }

  @Column(name = "iscollapsed")
  public Boolean getIscollapsed() {
    return iscollapsed;
  }

  public void setIscollapsed(Boolean isCollapsed) {
    this.iscollapsed = isCollapsed;
  }

  @Column(name = "ishidden")
  public Boolean getIshidden() {
    return ishidden;
  }

  public void setIshidden(Boolean isHhidden) {
    this.ishidden = isHhidden;
  }

  @Column(name = "isdisable")
  public Boolean getIsdisable() {
    return isdisable;
  }

  public void setIsdisable(Boolean isDisable) {
    this.isdisable = isDisable;
  }

  @Column(name = "isdisabledesign")
  public Boolean getIsdisabledesign() {
    return isdisabledesign;
  }

  public void setIsdisabledesign(Boolean isdisabledesign) {
    this.isdisabledesign = isdisabledesign;
  }

  @Column(name = "othersetting", length = 200)
  public String getOthersetting() {
    return this.othersetting;
  }

  public void setOthersetting(String othersetting) {
    this.othersetting = othersetting;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "fovDataobjectassociate")
  @OrderBy("orderno")
  @JSONField(serialize = false)
  public Set<FovDataobjectassociatedetail> getFovDataobjectassociatedetails() {
    return this.fovDataobjectassociatedetails;
  }

  public void setFovDataobjectassociatedetails(Set<FovDataobjectassociatedetail> fovDataobjectassociatedetails) {
    this.fovDataobjectassociatedetails = fovDataobjectassociatedetails;
  }

}
