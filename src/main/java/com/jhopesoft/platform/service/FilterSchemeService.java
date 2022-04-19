package com.jhopesoft.platform.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.dao.entity.viewsetting.FovFilterscheme;
import com.jhopesoft.framework.dao.entity.viewsetting.FovFilterschemedetail;
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
public class FilterSchemeService {

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
	 * @param filterSchemeId
	 * @param schemeDefine
	 * @param shareall
	 * @param shareowner
	 * @param mydefault
	 * @param schemeDefine2
	 */
	public ActionResult updateFilterSchemeDetails(HttpServletRequest request, String dataObjectId,
			String filterSchemeId, String filterSchemeName, String schemeDefine, Boolean mydefault, Boolean shareowner,
			Boolean shareall) {
		FovFilterscheme filterScheme;
		// 如果FilterSchemeId为null,那么就表示是新增
		if (filterSchemeId != null && filterSchemeId.length() > 1) {
			filterScheme = dao.findById(FovFilterscheme.class, filterSchemeId);
			filterScheme.setSchemename(filterSchemeName);
			filterScheme.setIsshare(BooleanUtils.isTrue(shareall));
			filterScheme.setIsshareowner(BooleanUtils.isTrue(shareowner));
			dao.saveOrUpdate(filterScheme);
			deleteAllColumns(filterScheme);
			// dao.executeSQLUpdate("delete from fov_filterschemedetail where
			// filterschemeid=?", filterSchemeId);
		} else {
			filterScheme = new FovFilterscheme();
			filterScheme.setSchemename(filterSchemeName);
			FDataobject d = dao.findById(FDataobject.class, dataObjectId);
			filterScheme.setFDataobject(d);
			int orderno = 10;
			if (d.getFovFilterschemes() != null && d.getFovFilterschemes().size() > 0) {
				List<FovFilterscheme> schemes = new ArrayList<FovFilterscheme>(d.getFovFilterschemes());
				orderno = schemes.get(schemes.size() - 1).getOrderno() + 10;
			}
			filterScheme.setIsshareowner(shareowner);
			filterScheme.setIsshare(shareall);
			filterScheme.setOrderno(orderno);
			filterScheme.setFUser(dao.findById(FUser.class, Local.getUserid()));
			dao.save(filterScheme);
		}
		JSONObject object = JSONObject.parseObject("{ children :" + schemeDefine + "}");
		JSONArray arrays = (JSONArray) object.get(Constants.CHILDREN);
		saveNewDetails(filterScheme, arrays, null);
		// 如果mydefault = true,设置为默认方案
		if (BooleanUtils.isTrue(mydefault)) {
			userFavouriteService.setDefaultFilterScheme(filterScheme.getFilterschemeid());
		}
		// 要将修改过后的数据发送到前台。
		ActionResult result = new ActionResult();
		result.setTag(filterScheme.getFilterschemeid());
		return result;
	}

	private void deleteAllColumns(FovFilterscheme gridScheme) {
		Iterator<FovFilterschemedetail> iterator = gridScheme.getDetails().iterator();
		while (iterator.hasNext()) {
			FovFilterschemedetail column = iterator.next();
			iterator.remove();
			column.setFovFilterscheme(null);
			dao.delete(column);
		}
	}

	/**
	 * 根据 FilterSchemeid 读取列表方案的定义，以供修改
	 * 
	 * @param request
	 * @param filterSchemeId
	 */

	public JSONObject getFilterSchemeDetails(HttpServletRequest request, String filterSchemeId) {
		FovFilterscheme filterScheme = dao.findById(FovFilterscheme.class, filterSchemeId);
		JSONObject object = new JSONObject();
		if (filterScheme == null) {
			object.put(Constants.CHILDREN, new JSONArray());
			return object;
		}
		FDataobject baseModule = DataObjectUtils.getDataObject(filterScheme.getFDataobject().getObjectname());
		List<FovFilterschemedetail> details = dao.findByProperty(FovFilterschemedetail.class, "Filterschemeid",
				filterSchemeId);
		object.put("moduleTitle", filterScheme.getFDataobject().getTitle());
		object.put("schemename", filterScheme.getSchemename());
		if (details != null) {
			object.put(Constants.CHILDREN, genFilterSchemedetailForEdit(details, baseModule));
		}
		return object;
	}

	public JSONArray genFilterSchemedetailForEdit(List<FovFilterschemedetail> details, FDataobject baseModule) {
		JSONArray result = new JSONArray();
		for (FovFilterschemedetail detail : details) {
			JSONObject object = new JSONObject();
			object.put("key", detail.getDetailid());
			object.put(Constants.TITLE, detail.getTitle());
			object.put("rowspan", detail.getRowspan());
			object.put("colspan", detail.getColspan());
			object.put("othersetting", detail.getOthersetting());
			object.put(Constants.REMARK, detail.getRemark());
			object.put("xtype", detail.getXtype());
			if (detail.getFDataobjectfield() == null) {
				// 上层的span
				object.put(Constants.TEXT, detail.getTitle());
				object.put("expanded", true);
				object.put("leaf", false);
				object.put("rows", detail.getRowss());
				object.put("cols", detail.getCols());
				object.put("widths", detail.getWidths());
				if (detail.getDetails() != null) {
					object.put(Constants.CHILDREN, genFilterSchemedetailForEdit(detail.getDetails(), baseModule));
				}
			} else {
				// 字段
				object.put("filtertype", detail.getFiltertype());
				object.put("hiddenoperator", detail.getHiddenoperator());
				object.put(Constants.OPERATOR, detail.getOperator());
				object.put("leaf", true);
				object.put("cls", detail.getFDataobjectfield()._getFieldCss());
				object.put("itemId", DataObjectFieldUtils.getItemId(detail.getFDataobjectfield(),
						detail.getFieldahead(), detail.getAggregate(), null));
				object.put("key", object.get("itemId"));
				object.put(Constants.TEXT, DataObjectFieldUtils.getTitle(detail.getFDataobjectfield(),
						detail.getFieldahead(), detail.getAggregate(), null, baseModule));
				if (detail.getFDataobjectfield()._isManyToOne() || detail.getFDataobjectfield()._isOneToOne()) {
					FDataobject m = DataObjectUtils.getDataObject(detail.getFDataobjectfield().getFieldtype());
					object.put("iconCls", m.getIconcls());
					object.put("icon", m.getIconurl());
				}

			}
			result.add(object);
		}
		return result;
	}

	private void saveNewDetails(FovFilterscheme filterScheme, JSONArray arrays, FovFilterschemedetail p) {
		for (int i = 0; i < arrays.size(); i++) {
			JSONObject detailObject = arrays.getJSONObject(i);
			FovFilterschemedetail detail = new FovFilterschemedetail();
			detail.setFovFilterschemedetail(p);
			detail.setFovFilterscheme(filterScheme);
			if (detailObject.containsKey(Constants.TITLE)) {
				detail.setTitle(detailObject.getString(Constants.TITLE));
			}
			if (detailObject.containsKey("rowspan")) {
				detail.setRowspan(detailObject.getInteger("rowspan"));
			}
			if (detailObject.containsKey("colspan")) {
				detail.setColspan(detailObject.getInteger("colspan"));
			}
			if (detailObject.containsKey("othersetting")) {
				detail.setOthersetting(detailObject.getString("othersetting"));
			}
			if (detailObject.containsKey(Constants.REMARK)) {
				detail.setRemark(detailObject.getString(Constants.REMARK));
			}
			if (detailObject.containsKey("xtype")) {
				detail.setXtype(detailObject.getString("xtype"));
			}
			detail.setOrderno((i + 1) * 10);
			if (detailObject.containsKey(Constants.CHILDREN)) {
				if (detailObject.containsKey("rows")) {
					detail.setRowss(detailObject.getInteger("rows"));
				}
				if (detailObject.containsKey("cols")) {
					detail.setCols(detailObject.getInteger("cols"));
				}
				if (detailObject.containsKey("widths")) {
					detail.setWidths(detailObject.getString("widths"));
				}
				dao.save(detail);
				saveNewDetails(null, (JSONArray) detailObject.get(Constants.CHILDREN), detail);
			} else {
				ParentChildFieldUtils.updateToField(detail, detailObject.getString("itemId"));
				if (detailObject.containsKey("filtertype")) {
					detail.setFiltertype(detailObject.getString("filtertype"));
				}
				if (detailObject.containsKey(Constants.OPERATOR)) {
					detail.setOperator(detailObject.getString(Constants.OPERATOR));
				}
				if (detailObject.containsKey("hiddenoperator")) {
					detail.setHiddenoperator(detailObject.getBoolean("hiddenoperator"));
				}
				dao.save(detail);
			}
		}
	}

	public ActionResult deleteFilterScheme(HttpServletRequest request, String schemeid) {
		dao.executeSQLUpdate("delete from fov_filterschemedetail where parentid in "
				+ "(select detailid from fov_filterschemedetail where filterschemeid=?) ", schemeid);
		dao.executeSQLUpdate("delete from fov_filterschemedetail where filterschemeid=?", schemeid);
		dao.executeSQLUpdate("delete from fov_filterscheme where filterschemeid=?", schemeid);
		return new ActionResult();
	}

	public ActionResult checkNameValidate(String name, String id) {
		FUser user = dao.findById(FUser.class, Local.getUserid());
		ActionResult result = new ActionResult();
		for (FovFilterscheme scheme : user.getFovFilterschemes()) {
			boolean cond = (id == null || !id.equals(scheme.getFilterschemeid()))
					&& scheme.getSchemename().equals(name);
			if (cond) {
				result.setSuccess(false);
				break;
			}
		}
		return result;
	}

	public ActionResult filterSchemeSaveas(HttpServletRequest request, String schemeid, String schemename) {
		FovFilterscheme scheme = dao.findById(FovFilterscheme.class, schemeid);
		FovFilterscheme saveas = new FovFilterscheme();

		int orderno = 10;
		FDataobject d = scheme.getFDataobject();
		if (d.getFovFilterschemes() != null && d.getFovFilterschemes().size() > 0) {
			List<FovFilterscheme> schemes = new ArrayList<FovFilterscheme>(d.getFovFilterschemes());
			orderno = schemes.get(schemes.size() - 1).getOrderno() + 10;
		}
		saveas.setOrderno(orderno);
		saveas.setFDataobject(scheme.getFDataobject());
		saveas.setFUser(dao.findById(FUser.class, Local.getUserid()));
		saveas.setSchemename(schemename);
		dao.save(saveas);
		for (FovFilterschemedetail c : scheme.getDetails()) {
			FovFilterschemedetail newdetail = new FovFilterschemedetail(c.getFDataobjectfield(),
					c.getFDataobjectconditionBySubconditionid(), c.getOrderno(), c.getTitle(), c.getFieldahead(),
					c.getAggregate(), c.getFiltertype(), c.getHiddenoperator(), c.getOperator(), c.getXtype(),
					c.getRowss(), c.getCols(), c.getRowspan(), c.getColspan(), c.getWidths(), c.getOthersetting(),
					c.getRemark());
			newdetail.setFovFilterscheme(saveas);
			dao.save(newdetail);
			if (c.getDetails() != null && c.getDetails().size() > 0) {
				copyFilterSchemedetail(c, newdetail);
			}
		}
		ActionResult result = new ActionResult();
		result.setTag(saveas.getFilterschemeid());
		return result;
	}

	private void copyFilterSchemedetail(FovFilterschemedetail detail, FovFilterschemedetail pdetail) {
		for (FovFilterschemedetail c : detail.getDetails()) {
			FovFilterschemedetail newdetail = new FovFilterschemedetail(c.getFDataobjectfield(),
					c.getFDataobjectconditionBySubconditionid(), c.getOrderno(), c.getTitle(), c.getFieldahead(),
					c.getAggregate(), c.getFiltertype(), c.getHiddenoperator(), c.getOperator(), c.getXtype(),
					c.getRowss(), c.getCols(), c.getRowspan(), c.getColspan(), c.getWidths(), c.getOthersetting(),
					c.getRemark());
			newdetail.setFovFilterschemedetail(pdetail);
			dao.save(newdetail);
			if (c.getDetails() != null && c.getDetails().size() > 0) {
				copyFilterSchemedetail(c, newdetail);
			}
		}
	}

}
