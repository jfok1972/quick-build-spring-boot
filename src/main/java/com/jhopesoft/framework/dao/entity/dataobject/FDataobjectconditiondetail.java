package com.jhopesoft.framework.dao.entity.dataobject;
// default package

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.dao.entity.utils.FFunction;
import com.jhopesoft.framework.dao.entityinterface.ParentChildField;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectFieldUtils;
import com.jhopesoft.framework.utils.OperateUtils;

/**
 * FDataobjectconditiondetail generated by hbm2java
 */
@Entity
@DynamicUpdate
@Table(name = "f_dataobjectconditiondetail")
public class FDataobjectconditiondetail implements java.io.Serializable, ParentChildField {

  private static final long serialVersionUID = 9035308654019962744L;
  private String conditiondetailid;
  private FDataobjectcondition FDataobjectconditionBySubconditionid;
  private FDataobjectcondition FDataobjectcondition;
  private FDataobjectconditiondetail FDataobjectconditiondetail;
  private FDataobjectfield FDataobjectfield;
  private FFunction FFunction;
  private String title;
  private int orderno;
  private String fieldahead;
  private String aggregate;
  private String userfunction;
  private String operator;
  private String ovalue;
  private Boolean istreerecord;
  private String recordids;
  private String recordnames;
  private String remark;
  private List<FDataobjectconditiondetail> details = new ArrayList<FDataobjectconditiondetail>(0);

  public String _getConditionText(boolean istext) {
    String result = null;
    String functionString = null;
    if (FFunction != null) {
      functionString = FFunction.getSqlExpression(FDataobjectfield.getFDataobject());
    } else if (userfunction != null) {
      functionString = userfunction;
    }
    if (functionString != null) {
      StringBuffer resultBuffer = new StringBuffer();
      String s = "\\d+.\\d+|\\w+";
      Pattern patternthis = Pattern.compile(s);
      Matcher matcherthis = patternthis.matcher(functionString);
      while (matcherthis.find()) {
        if (Constants.THIS.equals(matcherthis.group())) {
          if (FDataobjectfield == null) {
            return "error:表达式中有this,但是没有选择字段";
          }
          matcherthis.appendReplacement(resultBuffer,
              istext
                  ? DataObjectFieldUtils.getTitle(FDataobjectfield, fieldahead, aggregate,
                      FDataobjectconditionBySubconditionid, _getFDataobject(this))
                  : DataObjectFieldUtils.getFieldnameJson(FDataobjectfield, fieldahead, aggregate,
                      FDataobjectconditionBySubconditionid, _getFDataobject(this)).toString());
        }
      }
      matcherthis.appendTail(resultBuffer);
      String patternStr = "\\d+?\\%";
      Pattern pattern = Pattern.compile(patternStr);
      String s1 = resultBuffer.toString();
      Matcher matcher = pattern.matcher(s1);
      resultBuffer = new StringBuffer();
      while (matcher.find()) {
        String group = matcher.group();
        Integer number = Integer.parseInt(group.substring(0, group.length() - 1));
        if (number == 0) {
          return "error:表达式中第一个参数是1%，不是0%";
        }
        if (number > details.size()) {
          return "error:表达式中的" + group + "大于参数记录的个数";
        }
        String childcondition = details.get(number - 1)._getConditionText(istext);
        matcher.appendReplacement(resultBuffer, childcondition);
      }
      matcher.appendTail(resultBuffer);
      result = resultBuffer.toString();
    } else if (FDataobjectfield != null) {
      result = istext
          ? DataObjectFieldUtils.getTitle(FDataobjectfield, fieldahead, aggregate, FDataobjectconditionBySubconditionid,
              _getFDataobject(this))
          : DataObjectFieldUtils.getFieldnameJson(FDataobjectfield, fieldahead, aggregate,
              FDataobjectconditionBySubconditionid, _getFDataobject(this)).toString();
    } else {
      return "error:既无表达式，又无字段";
    }
    if (recordids != null && recordids.length() > 0) {
      if (BooleanUtils.isTrue(istreerecord)) {
        result = OperateUtils.getIdPidOrCodeLevelCondition(
            DataObjectFieldUtils.getFieldDataobject(FDataobjectfield, _getFDataobject(this)), result, recordids);
      } else {
        result = OperateUtils.getCondition(result, Constants.IN, recordids);
      }
    } else {
      result = OperateUtils.getCondition(result, operator, ovalue);
    }
    return result;
  }

  public JSONObject genJsonObject() {
    JSONObject result = new JSONObject();
    result.put(Constants.KEY, this.getConditiondetailid());
    result.put(Constants.TITLE, this.getTitle());
    result.put(Constants.OPERATOR, this.getOperator());
    result.put(Constants.OVALUE, this.getOvalue());
    result.put(Constants.ISTREERECORD, this.getIstreerecord());
    result.put(Constants.RECORDIDS, this.getRecordids());
    result.put(Constants.RECORDNAMES, this.getRecordnames());
    result.put(Constants.USERFUNCTION, this.getUserfunction());
    result.put(Constants.REMARK, this.getRemark());
    if (FFunction != null) {
      result.put(Constants.FUNCTIONID, this.getFFunction().getFunctionid());
    }
    if (FDataobjectfield != null) {
      result.put(Constants.FIELDID, DataObjectFieldUtils.getItemId(getFDataobjectfield(), getFieldahead(),
          getAggregate(), getFDataobjectconditionBySubconditionid()));
      result.put("fieldtitle", DataObjectFieldUtils.getTitle(getFDataobjectfield(), getFieldahead(), getAggregate(),
          getFDataobjectconditionBySubconditionid(), _getFDataobject(this)));
    }
    if (details != null && details.size() > 0) {
      result.put("leaf", false);
      result.put("expanded", true);
      JSONArray array = new JSONArray();
      for (FDataobjectconditiondetail s : details) {
        array.add(s.genJsonObject());
      }
      result.put(Constants.CHILDREN, array);
    } else {
      result.put("leaf", true);
    }
    String text = this.getTitle();
    if (text == null) {
      text = "";
      if (FFunction != null) {
        text = text + FFunction.getTitle();
      }
      if (FDataobjectfield != null) {
        text = text + (text.length() > 0 ? "--" : "") + result.getString("fieldtitle");
      }
    }
    result.put(Constants.TEXT, text);
    return result;
  }

  private FDataobject _getFDataobject(FDataobjectconditiondetail detail) {
    if (detail.FDataobjectcondition == null) {
      return _getFDataobject(detail.getFDataobjectconditiondetail());
    } else {
      return detail.getFDataobjectcondition().getFDataobject();
    }
  }

  public FDataobjectconditiondetail() {
  }

  public FDataobjectconditiondetail(FDataobjectcondition fDataobjectcondition,
      FDataobjectcondition FDataobjectconditionBySubconditionid, FDataobjectconditiondetail fDataobjectconditiondetail,
      FDataobjectfield fDataobjectfield, FFunction fFunction, String title, int orderno, String userfunction,
      String fieldahead, String aggregate, String operator, String ovalue, String remark) {
    super();
    this.FDataobjectcondition = fDataobjectcondition;
    this.FDataobjectconditionBySubconditionid = FDataobjectconditionBySubconditionid;
    this.FDataobjectconditiondetail = fDataobjectconditiondetail;
    this.FDataobjectfield = fDataobjectfield;
    this.FFunction = fFunction;
    this.title = title;
    this.orderno = orderno;
    this.userfunction = userfunction;
    this.fieldahead = fieldahead;
    this.aggregate = aggregate;
    this.operator = operator;
    this.ovalue = ovalue;
    this.remark = remark;
  }

  @Id
  @GeneratedValue(generator = "generator")
  @GenericGenerator(name = "generator", strategy = "uuid.hex")
  @Column(name = "conditiondetailid", unique = true, nullable = false, length = 40)
  public String getConditiondetailid() {
    return this.conditiondetailid;
  }

  public void setConditiondetailid(String conditiondetailid) {
    this.conditiondetailid = conditiondetailid;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fieldid")
  public FDataobjectfield getFDataobjectfield() {
    return this.FDataobjectfield;
  }

  public void setFDataobjectfield(FDataobjectfield FDataobjectfield) {
    this.FDataobjectfield = FDataobjectfield;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subconditionid")
  public FDataobjectcondition getFDataobjectconditionBySubconditionid() {
    return this.FDataobjectconditionBySubconditionid;
  }

  public void setFDataobjectconditionBySubconditionid(FDataobjectcondition FDataobjectconditionBySubconditionid) {
    this.FDataobjectconditionBySubconditionid = FDataobjectconditionBySubconditionid;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conditionid")
  public FDataobjectcondition getFDataobjectcondition() {
    return this.FDataobjectcondition;
  }

  public void setFDataobjectcondition(FDataobjectcondition FDataobjectcondition) {
    this.FDataobjectcondition = FDataobjectcondition;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parentid")
  public FDataobjectconditiondetail getFDataobjectconditiondetail() {
    return this.FDataobjectconditiondetail;
  }

  public void setFDataobjectconditiondetail(FDataobjectconditiondetail FDataobjectconditiondetail) {
    this.FDataobjectconditiondetail = FDataobjectconditiondetail;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "functionid")
  public FFunction getFFunction() {
    return this.FFunction;
  }

  public void setFFunction(FFunction FFunction) {
    this.FFunction = FFunction;
  }

  @Column(name = "title", length = 200)
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

  @Column(name = "userfunction", length = 200)
  public String getUserfunction() {
    return this.userfunction;
  }

  public void setUserfunction(String userfunction) {
    this.userfunction = userfunction;
  }

  @Column(name = "fieldahead", length = 200)
  public String getFieldahead() {
    return this.fieldahead;
  }

  public void setFieldahead(String fieldahead) {
    this.fieldahead = fieldahead;
  }

  @Column(name = "aggregate", length = 20)
  public String getAggregate() {
    return this.aggregate;
  }

  public void setAggregate(String aggregate) {
    this.aggregate = aggregate;
  }

  @Column(name = "operator", length = 50)
  public String getOperator() {
    return this.operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  @Column(name = "ovalue", length = 200)
  public String getOvalue() {
    return this.ovalue;
  }

  public void setOvalue(String ovalue) {
    this.ovalue = ovalue;
  }

  @Column(name = "istreerecord")
  public Boolean getIstreerecord() {
    return this.istreerecord;
  }

  public void setIstreerecord(Boolean istreerecord) {
    this.istreerecord = istreerecord;
  }

  public String getRecordids() {
    return recordids;
  }

  public void setRecordids(String recordids) {
    this.recordids = recordids;
  }

  public String getRecordnames() {
    return recordnames;
  }

  public void setRecordnames(String recordnames) {
    this.recordnames = recordnames;
  }

  @Column(name = "remark", length = 200)
  public String getRemark() {
    return this.remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "FDataobjectconditiondetail")
  @OrderBy("orderno")
  public List<FDataobjectconditiondetail> getDetails() {
    return this.details;
  }

  public void setDetails(List<FDataobjectconditiondetail> details) {
    this.details = details;
  }

  @Override
  @Transient
  public String getCondition() {
    return null;
  }

  @Override
  public void setCondition(String value) {

  }

}
