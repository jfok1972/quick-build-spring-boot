package com.jhopesoft.framework.core.datamining.service;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.datamining.FDataanalysefilterscheme;
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
public class DataminingFilterSchemeService {

  @Autowired
  private DaoImpl dao;

  /**
   * 根据模块名称取得----筛选条件方案
   * 
   * @param moduleName
   * @return
   */
  public JSONArray getFilterSchemes(String moduleName) {
    FDataobject object = DataObjectUtils.getDataObject(moduleName);
    JSONArray result = new JSONArray();
    for (FDataanalysefilterscheme ascheme : object.getFDataanalysefilterschemes()) {
      JSONObject jsonobject = new JSONObject();
      jsonobject.put("schemeid", ascheme.getSchemeid());
      jsonobject.put(Constants.TEXT, ascheme.getTitle());
      jsonobject.put("iconCls", ascheme.getIconcls());
      result.add(jsonobject);
    }
    return result;
  }

  public ActionResult getFilterSchemeDetail(String schemeid) {
    FDataanalysefilterscheme scheme = dao.findById(FDataanalysefilterscheme.class, schemeid);
    ActionResult result = new ActionResult();
    result.setMsg(scheme.getOthersetting());
    return result;
  }

  public ActionResult addFilterScheme(String moduleName, String title, String othersetting) {
    FDataobject object = DataObjectUtils.getDataObject(moduleName);
    FDataanalysefilterscheme scheme = new FDataanalysefilterscheme();
    scheme.setFDataobject(object);
    scheme.setCreatedate(new Date());
    scheme.setCreater(Local.getUserid());
    scheme.setFUser(Local.getDao().findById(FUser.class, Local.getUserid()));
    if (object.getFDataanalysefilterschemes().size() == 0)
      scheme.setOrderno(10);
    else {
      int lastorderno = 0;
      for (FDataanalysefilterscheme ascheme : object.getFDataanalysefilterschemes()) {
        lastorderno = ascheme.getOrderno();
      }
      scheme.setOrderno(lastorderno + 10);
    }
    if (title == null || title.length() == 0) {
      title = "新建的筛选条件方案";
    }
    scheme.setTitle(title.length() > 50 ? title.substring(0, 50) : title);
    scheme.setOthersetting(othersetting);
    dao.save(scheme);
    ActionResult result = new ActionResult();
    result.setMsg(scheme.getTitle());
    result.setTag(scheme.getSchemeid());
    return result;
  }

  public ActionResult deleteFilterScheme(String schemeid) {
    ActionResult result = new ActionResult();
    FDataanalysefilterscheme scheme = dao.findById(FDataanalysefilterscheme.class, schemeid);
    if (scheme.getFUser() == null) {
      result.setSuccess(false);
      result.setMsg("这是系统筛选条件方案，你不能删除！");
    } else if (scheme.getFUser().getUserid().equals(Local.getUserid())) {
      dao.delete(scheme);
    } else {
      result.setSuccess(false);
      result.setMsg("这是其他用户的筛选条件方案，你不能删除！");
    }
    return result;
  }

}
