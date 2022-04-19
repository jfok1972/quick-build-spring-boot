package com.jhopesoft.framework.bean;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

public class TreeNodeRecordChecked extends TreeNodeRecord {

  private static final long serialVersionUID = 1L;

  public TreeNodeRecordChecked() {

  }

  public TreeNodeRecordChecked(String moduleName, String tableAsName, String text, String fieldname, String fieldvalue,
      String equalsMethod, Boolean isCodeLevel) {
    super(moduleName, tableAsName, text, fieldname, fieldvalue, equalsMethod, isCodeLevel);
  }

  public Boolean checked = false;

  public Boolean getChecked() {
    return checked;
  }

  public void setChecked(Boolean checked) {
    this.checked = checked;
  }

}
