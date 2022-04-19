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
import com.jhopesoft.framework.dao.entity.datamining.FDataanalyserowgroupdetail;
import com.jhopesoft.framework.dao.entity.datamining.FDataanalyserowgrouppath;
import com.jhopesoft.framework.dao.entity.datamining.FDataanalyserowgroupscheme;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Service
public class DataminingRowSchemeService {

  @Autowired
  private DaoImpl dao;

  /**
   * 根据模块名称取得----行展开方案
   * 
   * @param moduleName
   * @return
   */
  public JSONArray getRowSchemes(String moduleName) {
    FDataobject object = DataObjectUtils.getDataObject(moduleName);
    JSONArray result = new JSONArray();
    for (FDataanalyserowgroupscheme ascheme : object.getFDataanalyserowgroupschemes()) {
      JSONObject jsonobject = new JSONObject();
      jsonobject.put("schemeid", ascheme.getSchemeid());
      jsonobject.put("savepath", ascheme.isRowexpandpath());
      jsonobject.put(Constants.TEXT, ascheme.getTitle() + " (" + (ascheme.isRowexpandpath() ? "路径" : "每行") + ")");
      jsonobject.put("iconCls", ascheme.getIconcls());
      result.add(jsonobject);
    }
    return result;
  }

  public JSONArray getRowSchemeDetail(String schemeid) {
    FDataanalyserowgroupscheme scheme = dao.findById(FDataanalyserowgroupscheme.class, schemeid);
    if (scheme.isRowexpandpath()) {
      return getRowSchemePath(scheme);
    } else {
      return getRowSchemeDetail(scheme.getFDataanalyserowgroupdetails());
    }
  }

  private JSONArray getRowSchemeDetail(Set<FDataanalyserowgroupdetail> details) {
    JSONArray result = new JSONArray();
    for (FDataanalyserowgroupdetail detail : details) {
      JSONObject object = new JSONObject();
      object.put(Constants.TEXT, detail.getTitle());
      object.put("text_", detail.getOrgintitle());
      object.put("value", detail.getKeyvalue());
      object.put(Constants.CONDITION, detail.getGroupcondition());

      String[] part = detail.getOthersetting().split(";");
      if (part.length > 0 && part[0].length() > 0) {
        object.put("expanded", Constants.TRUE.equalsIgnoreCase(part[0]));
      }
      if (part.length > 1 && part[1].length() > 0) {
        object.put(Constants.MODULE_NAME, part[1]);
      }

      if (detail.getFDataanalyserowgroupdetails().size() == 0)
        object.put("leaf", true);
      else {
        object.put("leaf", false);
        object.put(Constants.CHILDREN, getRowSchemeDetail(detail.getFDataanalyserowgroupdetails()));
      }
      result.add(object);
    }
    return result;
  }

  /**
   * 新增一个----行展开方案
   * 
   * @param moduleName
   * @param title
   * @param rowGroup
   */
  public ActionResult addRowScheme(String moduleName, String title, Boolean savepath, String rowGroup) {
    FDataobject object = DataObjectUtils.getDataObject(moduleName);
    FDataanalyserowgroupscheme scheme = new FDataanalyserowgroupscheme();
    scheme.setFDataobject(object);
    scheme.setCreatedate(new Date());
    scheme.setCreater(Local.getUserid());
    scheme.setRowexpandpath(savepath);
    scheme.setFUser(Local.getDao().findById(FUser.class, Local.getUserid()));
    if (object.getFDataanalyserowgroupschemes().size() == 0) {
      scheme.setOrderno(10);
    } else {
      int lastorderno = 0;
      for (FDataanalyserowgroupscheme ascheme : object.getFDataanalyserowgroupschemes()) {
        lastorderno = ascheme.getOrderno();
      }
      scheme.setOrderno(lastorderno + 10);
    }
    if (title == null || title.length() == 0) {
      title = "新建的行展开方案";
    }
    scheme.setTitle(title.length() > 50 ? title.substring(0, 50) : title);
    dao.save(scheme);
    JSONObject jsonobject = JSONObject.parseObject("{ children :" + rowGroup + "}");
    if (savepath) {
      saveNewPaths(scheme, (JSONArray) jsonobject.get(Constants.CHILDREN));
    } else {
      saveNewRows(scheme, (JSONArray) jsonobject.get(Constants.CHILDREN), null);
    }
    ActionResult result = new ActionResult();
    result.setMsg(scheme.getTitle());
    result.setTag(scheme.getSchemeid());
    return result;
  }

  private void saveNewPaths(FDataanalyserowgroupscheme scheme, JSONArray arrays) {
    for (int i = 0; i < arrays.size(); i++) {
      JSONObject rowObject = arrays.getJSONObject(i);
      FDataanalyserowgrouppath path = new FDataanalyserowgrouppath();
      path.setFDataanalyserowgroupscheme(scheme);
      path.setOrderno((i + 1) * 10);
      path.setConditionpath(getValue(rowObject, "conditionpath"));
      path.setTitle(getValue(rowObject, Constants.TITLE));
      path.setPathtype(getValue(rowObject, Constants.TYPE));
      // fieldid
      if (rowObject.containsKey(Constants.FIELDID)) {
        String[] part = rowObject.getString(Constants.FIELDID).split("\\|");
        if (part.length == 1)
          setFieldId(path, part[0]);
        else {
          path.setFieldahead(part[0]);
          setFieldId(path, part[1]);
        }
      }
      if (rowObject.containsKey("addSelectedChildrens")) {
        path.setAddselectedchildrens(rowObject.getBoolean("addSelectedChildrens"));
      }
      if (rowObject.containsKey(Constants.CONDITION)) {
        path.setGroupcondition(rowObject.getString(Constants.CONDITION));
      }
      if ("edittext".equals(path.getPathtype()) || "combinerows".equals(path.getPathtype()) ||
          "combineotherrows".equals(path.getPathtype())) {
        path.setTitle(getValue(rowObject, Constants.TEXT));
      }
      if (rowObject.containsKey("pos")) {
        path.setPos(rowObject.getInteger("pos"));
      }
      if (rowObject.containsKey("records")) {
        path.setConditionpaths(rowObject.getString("records"));
      }
      dao.save(path);
    }
  }

  /**
   * 可能是这样 "SSalesman.SSalesdepartment|402881ec5bc69fce015bc6ac120300b6-all"
   * 
   * @param path
   * @param fieldid
   */
  private void setFieldId(FDataanalyserowgrouppath path, String fieldid) {
    String[] part = fieldid.split("-");
    if (part.length == 1) {
      path.setFDataobjectfield(new FDataobjectfield(part[0]));
    } else {
      path.setFDataobjectfield(new FDataobjectfield(part[0]));
      path.setFieldgrouptype(part[1]);
    }
  }

  public JSONArray getRowSchemePath(FDataanalyserowgroupscheme scheme) {
    JSONArray result = new JSONArray();
    for (FDataanalyserowgrouppath path : scheme.getFDataanalyserowgrouppaths()) {
      JSONObject object = new JSONObject();
      if ("combinerows".equals(path.getPathtype()) ||
          "combineotherrows".equals(path.getPathtype())) {
        object.put(Constants.CONDITION, path.getGroupcondition());
      }
      object.put("conditionpath", path.getConditionpath());
      object.put(Constants.TYPE, path.getPathtype());
      if ("edittext".equals(path.getPathtype()) || "combinerows".equals(path.getPathtype()) ||
          "combineotherrows".equals(path.getPathtype())) {
        object.put(Constants.TEXT, path.getTitle());
      } else {
        object.put(Constants.TITLE, path.getTitle());
      }
      object.put("addSelectedChildrens", path.getAddselectedchildrens());
      if (path.getFDataobjectfield() != null) {
        object.put(Constants.FIELDID, path._getFieldid());
      }
      if (path.getPos() != null) {
        object.put("pos", path.getPos());
      }
      if (path.getConditionpaths() != null) {
        object.put("records", JSONArray.parseArray(path.getConditionpaths()));
      }
      result.add(object);
    }
    return result;
  }

  private String getValue(JSONObject object, String name) {
    if (object.containsKey(name)) {
      return object.getString(name);
    } else {
      return null;
    }
  }

  /**
   * 保存行展开方案名称
   * 
   * @param scheme
   * @param arrays
   * @param p
   */
  private void saveNewRows(FDataanalyserowgroupscheme scheme, JSONArray arrays, FDataanalyserowgroupdetail p) {
    for (int i = 0; i < arrays.size(); i++) {
      JSONObject rowObject = arrays.getJSONObject(i);
      FDataanalyserowgroupdetail row = new FDataanalyserowgroupdetail();
      row.setFDataanalyserowgroupdetail(p);
      row.setFDataanalyserowgroupscheme(scheme);
      if (rowObject.containsKey(Constants.CONDITION)) {
        row.setGroupcondition(rowObject.getString(Constants.CONDITION));
      }
      if (rowObject.containsKey(Constants.TEXT)) {
        row.setTitle(rowObject.getString(Constants.TEXT));
      }
      if (rowObject.containsKey("text_")) {
        row.setOrgintitle(rowObject.getString("text_"));
      }
      if (rowObject.containsKey("value")) {
        row.setKeyvalue(rowObject.getString("value"));
      }
      row.setOthersetting("");
      if (rowObject.containsKey("expanded")) {
        if (rowObject.getBoolean("expanded")) {
          row.setOthersetting(Constants.TRUE);
        } else {
          row.setOthersetting(Constants.FALSE);
        }
      }
      row.setOthersetting(row.getOthersetting() + ";");
      if (rowObject.containsKey(Constants.MODULE_NAME)) {
        row.setOthersetting(row.getOthersetting() + rowObject.getString(Constants.MODULE_NAME));
      }
      row.setOrderno((i + 1) * 10);
      dao.save(row);
      if (rowObject.containsKey(Constants.CHILDREN)) {
        saveNewRows(null, (JSONArray) rowObject.get(Constants.CHILDREN), row);
      }
    }
  }

  public ActionResult deleteRowScheme(String schemeid) {
    ActionResult result = new ActionResult();
    FDataanalyserowgroupscheme scheme = dao.findById(FDataanalyserowgroupscheme.class, schemeid);
    if (scheme.getFUser() == null) {
      result.setSuccess(false);
      result.setMsg("这是系统行展开方案，你不能删除！");
    } else if (scheme.getFUser().getUserid().equals(Local.getUserid())) {
      dao.delete(scheme);
    } else {
      result.setSuccess(false);
      result.setMsg("这是其他用户的行展开方案，你不能删除！");
    }
    return result;
  }

}
