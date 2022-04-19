package com.jhopesoft.platform.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.dictionary.FNumbergroup;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.dao.entity.utils.FFunction;
import com.jhopesoft.framework.dao.entity.viewsetting.FovGridnavigatescheme;
import com.jhopesoft.framework.dao.entity.viewsetting.FovGridnavigateschemedetail;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectFieldUtils;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.ParentChildFieldUtils;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Service
public class NavigateSchemeService {

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
   * @param navigateSchemeId
   * @param schemeDefine
   * @param shareall
   * @param shareowner
   * @param mydefault
   * @param schemeDefine2
   */
  public ActionResult updateNavigateSchemeDetails(String dataObjectId, String navigateschemeid,
      String navigateSchemeName, String iconCls, Boolean cascading, Boolean allowNullRecordButton,
      Boolean isContainNullRecord, Boolean mydefault, Boolean shareowner, Boolean shareall, String schemeDefine) {
    FovGridnavigatescheme navigateScheme;
    // 如果NavigateSchemeId为null,那么就表示是新增
    if (navigateschemeid != null && navigateschemeid.length() > 1) {
      navigateScheme = dao.findById(FovGridnavigatescheme.class, navigateschemeid);
      if (dataObjectId != null) {
        // antd 版本的不传入 dataObjectid,修改的时候不改方案的内容
        navigateScheme.setTitle(navigateSchemeName);
        navigateScheme.setIconcls(iconCls);
        navigateScheme.setCascading(cascading);
        navigateScheme.setAllownullrecordbuttton(allowNullRecordButton);
        navigateScheme.setIscontainnullrecord(isContainNullRecord);
        navigateScheme.setIsshare(shareall);
        navigateScheme.setIsshareowner(shareowner);
        dao.saveOrUpdate(navigateScheme);
      }
      dao.executeSQLUpdate("delete from fov_gridnavigateschemedetail where schemeid=?", navigateschemeid);
    } else {
      navigateScheme = new FovGridnavigatescheme();
      navigateScheme.setTitle(navigateSchemeName);
      FDataobject d = dao.findById(FDataobject.class, dataObjectId);
      navigateScheme.setFDataobject(d);
      int orderno = 10;
      if (d.getFovGridnavigateschemes() != null && d.getFovGridnavigateschemes().size() > 0) {
        List<FovGridnavigatescheme> schemes = new ArrayList<FovGridnavigatescheme>(d.getFovGridnavigateschemes());
        orderno = schemes.get(schemes.size() - 1).getOrderno() + 10;
      }
      navigateScheme.setIconcls(iconCls);
      navigateScheme.setCascading(cascading);
      navigateScheme.setAllownullrecordbuttton(allowNullRecordButton);
      navigateScheme.setIscontainnullrecord(isContainNullRecord);
      navigateScheme.setIsshareowner(shareowner);
      navigateScheme.setIsshare(shareall);
      navigateScheme.setOrderno(orderno);
      navigateScheme.setFUser(dao.findById(FUser.class, Local.getUserid()));
      navigateScheme.setEnabled(true);
      dao.save(navigateScheme);
    }
    JSONObject object = JSONObject.parseObject("{ children :" + schemeDefine + "}");
    JSONArray arrays = (JSONArray) object.get(Constants.CHILDREN);
    saveNewDetails(navigateScheme, arrays, null);

    // 如果mydefault = true,设置为默认方案
    if (BooleanUtils.isTrue(mydefault)) {
      userFavouriteService.setDefaultNavigateScheme(navigateScheme.getSchemeid());
    }
    // 要将修改过后的数据发送到前台。
    ActionResult result = new ActionResult();
    result.setTag(navigateScheme.genJson());
    return result;
  }

  /**
   * 根据 NavigateSchemeid 读取列表方案的定义，以供修改
   * 
   * @param request
   * @param NavigateSchemeId
   */

  public JSONObject getNavigateSchemeDetails(HttpServletRequest request, String navigateSchemeId) {
    FovGridnavigatescheme navigateScheme = dao.findById(FovGridnavigatescheme.class, navigateSchemeId);
    JSONObject object = new JSONObject();
    if (navigateScheme == null) {
      object.put(Constants.CHILDREN, new JSONArray());
    } else {
      FDataobject baseModule = DataObjectUtils.getDataObject(navigateScheme.getFDataobject().getObjectname());
      object.put("leaf", false);
      object.put(Constants.CHILDREN, genNavigateSchemedetail(navigateScheme.getDetails(), baseModule));
    }
    return object;
  }

  public JSONArray genNavigateSchemedetail(Set<FovGridnavigateschemedetail> details, FDataobject baseModule) {
    JSONArray result = new JSONArray();
    Set<String> keys = new HashSet<String>();
    for (FovGridnavigateschemedetail detail : details) {
      JSONObject object = new JSONObject();
      object.put("key", detail.getSchemedetailid());
      object.put(Constants.TITLE, detail.getTitle());
      object.put(Constants.FUNCTIONID, detail.getFFunction() != null ? detail.getFFunction().getFunctionid() : null);
      object.put("numbergroupid",
          detail.getFNumbergroup() != null ? detail.getFNumbergroup().getNumbergroupid() : null);
      object.put("fieldfunction", detail.getFieldfunction());
      object.put("addparentfilter", detail.getAddparentfilter());
      object.put("reverseorder", detail.getReverseorder());
      object.put("collapsed", detail.getCollapsed());
      object.put("addcodelevel", detail.getAddcodelevel());
      object.put("iconcls", detail.getIconcls());
      object.put("cls", detail.getCls());
      object.put(Constants.REMARK, detail.getRemark());
      object.put("leaf", true);
      String itemId = DataObjectFieldUtils.getItemId(detail.getFDataobjectfield(), detail.getFieldahead(),
          detail.getAggregate(), null);
      object.put("itemId", itemId);
      if (!keys.contains(itemId)) {
        // keys 可能会有重复 ，一个字段在导航方案中可能出现二次
        keys.add(itemId);
        object.put("key", itemId);
      }
      object.put(Constants.TEXT, DataObjectFieldUtils.getTitle(detail.getFDataobjectfield(), detail.getFieldahead(),
          detail.getAggregate(), null, baseModule));
      result.add(object);
    }
    return result;
  }

  private void saveNewDetails(FovGridnavigatescheme navigateScheme, JSONArray arrays, FovGridnavigateschemedetail p) {
    for (int i = 0; i < arrays.size(); i++) {
      JSONObject detailObject = arrays.getJSONObject(i);
      FovGridnavigateschemedetail detail = new FovGridnavigateschemedetail();
      detail.setFovGridnavigatescheme(navigateScheme);
      detail.setOrderno((i + 1) * 10);
      if (detailObject.containsKey(Constants.TITLE)) {
        detail.setTitle(detailObject.getString(Constants.TITLE));
      }
      if (detailObject.containsKey("fieldfunction")) {
        detail.setFieldfunction(detailObject.getString("fieldfunction"));
      }
      if (detailObject.containsKey("addparentfilter")) {
        detail.setAddparentfilter(detailObject.getBoolean("addparentfilter"));
      }
      if (detailObject.containsKey("reverseorder")) {
        detail.setReverseorder(detailObject.getBoolean("reverseorder"));
      }
      if (detailObject.containsKey("collapsed")) {
        detail.setCollapsed(detailObject.getBoolean("collapsed"));
      }
      if (detailObject.containsKey("addcodelevel")) {
        detail.setAddcodelevel(detailObject.getBoolean("addcodelevel"));
      }

      if (detailObject.containsKey("iconcls")) {
        detail.setIconcls(detailObject.getString("iconcls"));
      }
      if (detailObject.containsKey("cls")) {
        detail.setCls(detailObject.getString("cls"));
      }
      if (detailObject.containsKey(Constants.REMARK)) {
        detail.setRemark(detailObject.getString(Constants.REMARK));
      }
      if (detailObject.containsKey(Constants.FUNCTIONID)) {
        detail.setFFunction(dao.findById(FFunction.class, detailObject.getString(Constants.FUNCTIONID)));
      }
      if (detailObject.containsKey("numbergroupid")) {
        detail.setFNumbergroup(dao.findById(FNumbergroup.class, detailObject.getString("numbergroupid")));
      }
      ParentChildFieldUtils.updateToField(detail, detailObject.getString("itemId"));
      if (StringUtils.isBlank(detail.getTitle())) {
        FDataobjectfield field = dao.findById(FDataobjectfield.class, detail.getFDataobjectfield().getFieldid());
        detail.setTitle(field.getFieldtitle());
      }
      dao.save(detail);
      navigateScheme.getDetails().add(detail);
    }
  }

  public ActionResult deleteNavigateScheme(HttpServletRequest request, String schemeid) {
    dao.executeSQLUpdate("delete from fov_gridnavigateschemedetail where schemeid=?", schemeid);
    dao.executeSQLUpdate("delete from fov_gridnavigatescheme where schemeid=?", schemeid);
    return new ActionResult();
  }

  public ActionResult checkNameValidate(String name, String id) {
    FUser user = dao.findById(FUser.class, Local.getUserid());
    ActionResult result = new ActionResult();
    for (FovGridnavigatescheme scheme : user.getFovGridnavigateschemes()) {
      boolean cond = (id == null || !id.equals(scheme.getSchemeid())) && scheme.getTitle().equals(name);
      if (cond) {
        result.setSuccess(false);
        break;
      }
    }
    return result;
  }

  public ActionResult navigateSchemeSaveas(HttpServletRequest request, String schemeid, String schemename) {
    FovGridnavigatescheme scheme = dao.findById(FovGridnavigatescheme.class, schemeid);
    FovGridnavigatescheme saveas = new FovGridnavigatescheme();
    int orderno = 10;
    FDataobject d = scheme.getFDataobject();
    if (d.getFovGridnavigateschemes() != null && d.getFovGridnavigateschemes().size() > 0) {
      List<FovGridnavigatescheme> schemes = new ArrayList<FovGridnavigatescheme>(d.getFovGridnavigateschemes());
      orderno = schemes.get(schemes.size() - 1).getOrderno() + 10;
    }
    saveas.setOrderno(orderno);
    saveas.setFDataobject(scheme.getFDataobject());
    saveas.setFUser(dao.findById(FUser.class, Local.getUserid()));
    saveas.setTitle(schemename);
    saveas.setIconcls(scheme.getIconcls());
    saveas.setCascading(scheme.getCascading());
    saveas.setAllownullrecordbuttton(scheme.getAllownullrecordbuttton());
    saveas.setIscontainnullrecord(scheme.getIscontainnullrecord());
    saveas.setEnabled(true);

    dao.save(saveas);
    for (FovGridnavigateschemedetail c : scheme.getDetails()) {
      FovGridnavigateschemedetail newdetail = new FovGridnavigateschemedetail(saveas, c.getFDataobjectfield(),
          c.getFDataobjectconditionBySubconditionid(), c.getFFunction(), c.getFNumbergroup(), c.getOrderno(),
          c.getTitle(), c.getFielddescription(), c.getFieldahead(), c.getAggregate(), c.getFieldfunction(),
          c.getNtype(), c.getAddparentfilter(), c.getReverseorder(), c.getCollapsed(), c.getAddcodelevel(),
          c.getIconcls(), c.getCls(), c.getRemark());
      dao.save(newdetail);
    }
    ActionResult result = new ActionResult();
    result.setTag(saveas.genJson());
    return result;
  }
}
