package com.jhopesoft.framework.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jiangfeng
 *
 */
public class DataDeleteResponseInfo implements Serializable {

  private static final long serialVersionUID = 5921848922737249324L;
  private Integer resultCode;
  private List<String> okMessageList;
  private List<String> errorMessageList;
  private List<String> okIds;
  private List<String> errorIds;

  public DataDeleteResponseInfo() {
    super();
    resultCode = 0;
    okMessageList = new ArrayList<String>();
    errorMessageList = new ArrayList<String>();
    okIds = new ArrayList<String>();
    errorIds = new ArrayList<String>();
  }

  public void setResultMessage(Integer resultCode, String message) {
    this.resultCode = resultCode;
    this.errorMessageList.add(message);
  }

  public String getMessage() {
    StringBuilder result = new StringBuilder("");
    for (String s : errorMessageList) {
      result.append(s + "<br/>");
    }
    return result.toString();
  }

  public Integer getResultCode() {
    return resultCode;
  }

  public void setResultCode(Integer resultCode) {
    this.resultCode = resultCode;
  }

  public List<String> getErrorMessageList() {
    return errorMessageList;
  }

  public void setErrorMessageList(List<String> errorMessageList) {
    this.errorMessageList = errorMessageList;
  }

  public List<String> getOkMessageList() {
    return okMessageList;
  }

  public void setOkMessageList(List<String> okMessageList) {
    this.okMessageList = okMessageList;
  }

  public List<String> getOkIds() {
    return okIds;
  }

  public void setOkIds(List<String> okIds) {
    this.okIds = okIds;
  }

  public List<String> getErrorIds() {
    return errorIds;
  }

  public void setErrorIds(List<String> errorIds) {
    this.errorIds = errorIds;
  }

}
