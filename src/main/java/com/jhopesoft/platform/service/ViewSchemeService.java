package com.jhopesoft.platform.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectcondition;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectview;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectviewdetail;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.utils.Constants;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Service
public class ViewSchemeService {

  @Resource
  private DaoImpl dao;

  @Resource
  private ModuleService moduleService;

  @Resource
  private UserFavouriteService userFavouriteService;

  @Resource
  private ModuleHierarchyService moduleHierarchyService;

  /**
   * 保存用户定义的列表方案
   * 
   * @param request
   * @param viewSchemeId
   * @param schemeDefine
   * @param shareall
   * @param shareowner
   * @param mydefault
   * @param schemeDefine2
   */
  public ActionResult updateViewSchemeDetails(String dataobjectid, String viewschemeid, String title,
      Boolean isshareowner, Boolean isshare, String operator, String remark, String details) {
    FDataobjectview viewScheme;
    // 如果ViewSchemeId为null,那么就表示是新增
    if (viewschemeid != null && viewschemeid.length() > 1) {
      viewScheme = dao.findById(FDataobjectview.class, viewschemeid);
      viewScheme.setTitle(title);
      viewScheme.setIsshare(isshare);
      viewScheme.setIsshareowner(isshareowner);
      viewScheme.setOperator(operator);
      viewScheme.setRemark(remark);
      dao.saveOrUpdate(viewScheme);
      dao.executeSQLUpdate("delete from f_dataobjectviewdetail where viewschemeid=?", viewschemeid);
    } else {
      viewScheme = new FDataobjectview();
      FDataobject d = dao.findById(FDataobject.class, dataobjectid);
      viewScheme.setFDataobject(d);
      int orderno = 10;
      if (d.getFDataobjectviews() != null && d.getFDataobjectviews().size() > 0) {
        List<FDataobjectview> schemes = new ArrayList<FDataobjectview>(d.getFDataobjectviews());
        orderno = schemes.get(schemes.size() - 1).getOrderno() + 10;
      }
      viewScheme.setTitle(title);
      viewScheme.setOrderno(orderno);
      viewScheme.setIsshare(isshare);
      viewScheme.setIsshareowner(isshareowner);
      viewScheme.setOperator(operator);
      viewScheme.setRemark(remark);
      viewScheme.setFUser(dao.findById(FUser.class, Local.getUserid()));
      dao.save(viewScheme);
    }
    int orderno = 10;
    if (details != null && details.length() > 0) {
      for (String detail : details.split(Constants.COMMA)) {
        FDataobjectviewdetail viewdetail = new FDataobjectviewdetail();
        viewdetail.setOrderno(orderno);
        orderno += 10;
        viewdetail.setFDataobjectview(viewScheme);
        viewdetail.setFDataobjectcondition(new FDataobjectcondition(detail));
        dao.save(viewdetail);
      }
    }

    // 要将修改过后的数据发送到前台。
    ActionResult result = new ActionResult();
    result.setTag(viewScheme);
    return result;
  }

  /**
   * 根据 ViewSchemeid 读取列表方案的定义，以供修改
   * 
   * @param request
   * @param ViewSchemeId
   */

  public JSONObject getViewSchemeDetails(String viewschemeid, String dataobjectid) {
    if (viewschemeid != null && viewschemeid.length() > 0) {
      FDataobjectview viewScheme = dao.findById(FDataobjectview.class, viewschemeid);
      return getViewDetails(viewScheme.getFDataobject(), viewScheme);
    } else {
      return getViewDetails(dao.findById(FDataobject.class, dataobjectid), null);
    }
  }

  public JSONObject getViewDetails(FDataobject dataobject, FDataobjectview viewScheme) {
    JSONObject result = new JSONObject();
    result.put("expanded", true);
    result.put(Constants.TEXT, "所有自定义条件");
    JSONObject system = new JSONObject();
    system.put(Constants.TEXT, "系统自定义条件");
    system.put("leaf", false);
    system.put("expanded", true);
    system.put(Constants.CHILDREN, new JSONArray());
    JSONObject owner = new JSONObject();
    owner.put(Constants.TEXT, "我的自定义条件");
    owner.put("leaf", false);
    owner.put("expanded", true);
    owner.put(Constants.CHILDREN, new JSONArray());
    JSONObject share = new JSONObject();
    share.put(Constants.TEXT, "其他人共享的自定义条件");
    share.put("leaf", false);
    share.put("expanded", true);
    share.put(Constants.CHILDREN, new JSONArray());
    for (FDataobjectcondition condition : dataobject.getFDataobjectconditions()) {
      JSONObject cond = getConditionObject(condition, viewScheme != null ? viewScheme.getDetails() : null);
      if (condition.getFUser() == null) {
        ((JSONArray) system.get(Constants.CHILDREN)).add(cond);
      } else if (condition.getFUser().getUserid().equals(Local.getUserid())) {
        ((JSONArray) owner.get(Constants.CHILDREN)).add(cond);
      } else {
        ((JSONArray) share.get(Constants.CHILDREN)).add(cond);
      }
    }
    JSONArray array = new JSONArray();
    if (((JSONArray) owner.get(Constants.CHILDREN)).size() > 0) {
      array.add(owner);
    }
    if (((JSONArray) system.get(Constants.CHILDREN)).size() > 0) {
      array.add(system);
    }
    if (((JSONArray) share.get(Constants.CHILDREN)).size() > 0) {
      array.add(share);
    }
    result.put(Constants.CHILDREN, array);
    return result;
  }

  public JSONObject getConditionObject(FDataobjectcondition condition, Set<FDataobjectviewdetail> details) {
    JSONObject result = new JSONObject();
    result.put(Constants.TEXT, condition.getTitle());
    result.put("itemId", condition.getConditionid());
    result.put("leaf", true);
    result.put("checked", false);
    if (details != null) {
      for (FDataobjectviewdetail detail : details) {
        if (detail.getFDataobjectcondition().getConditionid().equals(condition.getConditionid())) {
          result.put("checked", true);
        }
      }
    }
    return result;
  }

  public ActionResult deleteViewScheme(String viewschemeid) {
    dao.executeSQLUpdate("delete from f_dataobjectviewdetail where viewschemeid=?", viewschemeid);
    dao.executeSQLUpdate("delete from f_dataobjectview where viewschemeid=?", viewschemeid);
    return new ActionResult();
  }

}
