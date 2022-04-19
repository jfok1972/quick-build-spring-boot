package com.jhopesoft.framework.core.datamining.service;

import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.datamining.FDataanalysecolumngroupdetail;
import com.jhopesoft.framework.dao.entity.datamining.FDataanalysecolumngroupscheme;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Service
public class DataminingColumnSchemeService {

  @Autowired
  private DaoImpl dao;

  /**
   * 根据模块名称取得----列分组方案
   * 
   * @param moduleName
   * @return
   */
  public JSONArray getColumnSchemes(String moduleName) {
    FDataobject object = DataObjectUtils.getDataObject(moduleName);
    JSONArray result = new JSONArray();
    for (FDataanalysecolumngroupscheme ascheme : object.getFDataanalysecolumngroupschemes()) {
      JSONObject jsonobject = new JSONObject();
      jsonobject.put("schemeid", ascheme.getSchemeid());
      jsonobject.put(Constants.TEXT, ascheme.getTitle());
      jsonobject.put("iconCls", ascheme.getIconcls());
      result.add(jsonobject);
    }
    return result;
  }

  public JSONArray getColumnSchemeDetail(String schemeid) {
    FDataanalysecolumngroupscheme scheme = dao.findById(FDataanalysecolumngroupscheme.class, schemeid);
    return getColumnSchemeDetail(scheme.getFDataanalysecolumngroupdetails());
  }

  private JSONArray getColumnSchemeDetail(Set<FDataanalysecolumngroupdetail> details) {
    JSONArray result = new JSONArray();
    for (FDataanalysecolumngroupdetail detail : details) {
      JSONObject object = new JSONObject();
      object.put(Constants.TEXT, detail.getTitle());
      object.put(Constants.CONDITION, detail.getGroupcondition());
      if (detail.getFDataanalysecolumngroupdetails().size() == 0)
        object.put("leaf", true);
      else {
        object.put("leaf", false);
        object.put(Constants.CHILDREN, getColumnSchemeDetail(detail.getFDataanalysecolumngroupdetails()));
      }
      result.add(object);
    }
    return result;
  }

  /**
   * 新增一个----列分组方案
   * 
   * @param moduleName
   * @param title
   * @param columnGroup
   */
  public ActionResult addColumnScheme(String moduleName, String title, String columnGroup) {
    FDataobject object = DataObjectUtils.getDataObject(moduleName);
    FDataanalysecolumngroupscheme scheme = new FDataanalysecolumngroupscheme();
    scheme.setFDataobject(object);
    scheme.setCreatedate(new Date());
    scheme.setCreater(Local.getUserid());
    scheme.setFUser(Local.getDao().findById(FUser.class, Local.getUserid()));
    if (object.getFDataanalysecolumngroupschemes().size() == 0)
      scheme.setOrderno(10);
    else {
      int lastorderno = 0;
      for (FDataanalysecolumngroupscheme ascheme : object.getFDataanalysecolumngroupschemes()) {
        lastorderno = ascheme.getOrderno();
      }
      scheme.setOrderno(lastorderno + 10);
    }
    if (title == null || title.length() == 0) {
      title = "新建的列分组方案";
    }
    scheme.setTitle(title.length() > 50 ? title.substring(0, 50) : title);
    dao.save(scheme);
    JSONObject jsonobject = JSONObject.parseObject("{ children :" + columnGroup + "}");
    saveNewColumns(scheme, (JSONArray) jsonobject.get(Constants.CHILDREN), null);
    ActionResult result = new ActionResult();
    result.setMsg(scheme.getTitle());
    result.setTag(scheme.getSchemeid());
    return result;
  }

  /**
   * 保存列分组方案名称
   * @param scheme
   * @param arrays
   * @param p
   */
  private void saveNewColumns(FDataanalysecolumngroupscheme scheme, JSONArray arrays, FDataanalysecolumngroupdetail p) {
    for (int i = 0; i < arrays.size(); i++) {
      JSONObject columnObject = arrays.getJSONObject(i);
      FDataanalysecolumngroupdetail column = new FDataanalysecolumngroupdetail();
      column.setFDataanalysecolumngroupdetail(p);
      column.setFDataanalysecolumngroupscheme(scheme);
      if (columnObject.containsKey(Constants.CONDITION)) {
        column.setGroupcondition(columnObject.getString(Constants.CONDITION));
      }
      if (columnObject.containsKey(Constants.TEXT)) {
        column.setTitle(columnObject.getString(Constants.TEXT));
      }
      column.setOrderno((i + 1) * 10);
      dao.save(column);
      if (columnObject.containsKey("columns")) {
        saveNewColumns(null, (JSONArray) columnObject.get("columns"), column);
      }
    }
  }

  public ActionResult deleteColumnScheme(String schemeid) {
    ActionResult result = new ActionResult();
    FDataanalysecolumngroupscheme scheme = dao.findById(FDataanalysecolumngroupscheme.class, schemeid);
    if (scheme.getFUser() == null) {
      result.setSuccess(false);
      result.setMsg("这是系统列分组方案，你不能删除！");
    } else if (scheme.getFUser().getUserid().equals(Local.getUserid())) {
      dao.delete(scheme);
    } else {
      result.setSuccess(false);
      result.setMsg("这是其他用户的列分组方案，你不能删除！");
    }
    return result;
  }

}
