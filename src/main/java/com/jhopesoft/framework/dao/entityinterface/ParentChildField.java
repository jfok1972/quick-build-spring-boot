package com.jhopesoft.framework.dao.entityinterface;

import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectcondition;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
/**
 * 
 * @author θι jfok1972@qq.com
 * 
 */
public interface ParentChildField {

  /**
   * θΏε fieldahead
   * @return 
   */
  public String getFieldahead();

  public void setFieldahead(String value);

  public String getAggregate();

  public void setAggregate(String value);

  public FDataobjectfield getFDataobjectfield();

  public void setFDataobjectfield(FDataobjectfield value);

  public FDataobjectcondition getFDataobjectconditionBySubconditionid();

  public void setFDataobjectconditionBySubconditionid(FDataobjectcondition value);

  public String getCondition();

  public void setCondition(String value);

  public String getRemark();

  public void setRemark(String value);

}
