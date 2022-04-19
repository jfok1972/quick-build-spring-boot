package com.jhopesoft.framework.core.datamining.service;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.datamining.FDataanalyseselectfielddetail;
import com.jhopesoft.framework.dao.entity.datamining.FDataanalyseselectfieldscheme;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectcondition;
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
public class DataminingFieldSchemeService {

	@Autowired
	private DaoImpl dao;

	/**
	 * 根据模块名称取得----列分组方案
	 * 
	 * @param moduleName
	 * @return
	 */
	public JSONArray getFieldSchemes(String moduleName) {
		FDataobject object = DataObjectUtils.getDataObject(moduleName);
		JSONArray result = new JSONArray();
		for (FDataanalyseselectfieldscheme ascheme : object.getFDataanalyseselectfieldschemes()) {
			JSONObject jsonobject = new JSONObject();
			jsonobject.put("schemeid", ascheme.getSchemeid());
			jsonobject.put(Constants.TEXT, ascheme.getTitle());
			jsonobject.put("iconCls", ascheme.getIconcls());
			result.add(jsonobject);
		}
		return result;
	}

	/**
	 * 取得一个方案或者一个模块的所有的聚合字段
	 * 
	 * @param schemeid
	 * @param moduleName
	 * @return
	 */
	public JSONArray getFieldSchemeDetail(String schemeid, String moduleName) {
		if (StringUtils.isNotBlank(schemeid)) {
			FDataanalyseselectfieldscheme scheme = dao.findById(FDataanalyseselectfieldscheme.class, schemeid);
			return getColumnSchemeDetail(scheme.getFDataanalyseselectfielddetails());
		} else {
			JSONArray result = new JSONArray();
			FDataobject object = DataObjectUtils.getDataObject(moduleName);
			for (FDataanalyseselectfieldscheme scheme : object.getFDataanalyseselectfieldschemes()) {
				result.addAll(getColumnSchemeDetail(scheme.getFDataanalyseselectfielddetails()));
			}
			return result;
		}
	}

	private JSONArray getColumnSchemeDetail(Set<FDataanalyseselectfielddetail> details) {
		JSONArray result = new JSONArray();
		for (FDataanalyseselectfielddetail detail : details) {
			JSONObject object = new JSONObject();
			object.put(Constants.TEXT, detail.getTitle());
			if (detail.getFDataanalyseselectfielddetails().size() == 0) {
				object.put("width", 50);
				String aggregatefieldname = detail.getAggregate() + "." + detail.getFDataobjectfield().getFieldname();
				String itemId = detail.getFDataobjectfield().getFieldid() + "|" + detail.getAggregate();
				if (detail.getFieldahead() != null) {
					itemId = detail.getFieldahead() + "|" + itemId;
					object.put("fieldahead", detail.getFieldahead());
					String[] withpart = detail.getFieldahead().split("\\.with\\.");
					aggregatefieldname = detail.getAggregate() + "." + withpart[0] + "."
							+ detail.getFDataobjectfield().getFieldname() + Constants.DOTWITHDOT + withpart[1];
				}
				object.put("aggregate", detail.getAggregate());
				object.put("fieldname", detail.getFDataobjectfield().getFieldname());
				object.put("fieldtype", detail.getFDataobjectfield().getFieldtype());
				if (detail.getFDataobjectcondition() != null) {
					object.put("subconditionid", detail.getFDataobjectcondition().getConditionid());
					aggregatefieldname = aggregatefieldname + "|" + detail.getFDataobjectcondition().getConditionid();
					itemId = itemId + "|" + detail.getFDataobjectcondition().getConditionid();
				}
				object.put("aggregatefieldname", aggregatefieldname);
				if (detail.getFDataobjectfield().getUnittext() != null) {
					object.put("unittext", detail.getFDataobjectfield().getUnittext());
				}
				if (detail.getFDataobjectfield().getIsmonetary() != null
						&& detail.getFDataobjectfield().getIsmonetary()) {
					object.put("ismonetary", true);
				}
				object.put("tf_itemId", itemId);
				object.put("leaf", true);
			} else {
				object.put("leaf", false);
				object.put("expanded", true);
				object.put("columns", getColumnSchemeDetail(detail.getFDataanalyseselectfielddetails()));
			}
			result.add(object);
		}
		return result;
	}

	public ActionResult addFieldScheme(String moduleName, String title, String fieldGroup) {
		FDataobject object = DataObjectUtils.getDataObject(moduleName);
		FDataanalyseselectfieldscheme scheme = new FDataanalyseselectfieldscheme();
		scheme.setFDataobject(object);
		scheme.setCreatedate(new Date());
		scheme.setCreater(Local.getUserid());
		scheme.setFUser(Local.getDao().findById(FUser.class, Local.getUserid()));
		if (object.getFDataanalyseselectfieldschemes().size() == 0)
			scheme.setOrderno(10);
		else {
			int lastorderno = 0;
			for (FDataanalyseselectfieldscheme ascheme : object.getFDataanalyseselectfieldschemes()) {
				lastorderno = ascheme.getOrderno();
			}
			scheme.setOrderno(lastorderno + 10);
		}
		if (title == null || title.length() == 0) {
			title = "新建的字段组方案";
		}
		scheme.setTitle(title.length() > 50 ? title.substring(0, 50) : title);
		dao.save(scheme);
		JSONObject jsonobject = JSONObject.parseObject("{ children :" + fieldGroup + "}");
		saveNewColumns(scheme, (JSONArray) jsonobject.get(Constants.CHILDREN), null);
		ActionResult result = new ActionResult();
		result.setMsg(scheme.getTitle());
		result.setTag(scheme.getSchemeid());
		return result;
	}

	/**
	 * 保存列分组方案名称
	 * 
	 * @param scheme
	 * @param arrays
	 * @param p
	 */
	private void saveNewColumns(FDataanalyseselectfieldscheme scheme, JSONArray arrays,
			FDataanalyseselectfielddetail p) {
		for (int i = 0; i < arrays.size(); i++) {
			JSONObject columnObject = arrays.getJSONObject(i);
			FDataanalyseselectfielddetail column = new FDataanalyseselectfielddetail();
			column.setFDataanalyseselectfielddetail(p);
			column.setFDataanalyseselectfieldscheme(scheme);
			if (columnObject.containsKey("aggregate")) {
				column.setAggregate(columnObject.getString("aggregate"));
			}
			if (columnObject.containsKey("subconditionid") && columnObject.getString("subconditionid") != null) {
				column.setFDataobjectcondition(new FDataobjectcondition(columnObject.getString("subconditionid")));
			}
			String itemid = columnObject.getString("tf_itemId");
			if (itemid != null) {
				String[] part = itemid.split("\\|");
				if (itemid.indexOf(Constants.DOTWITHDOT) > -1) {
					// 子模块的聚合字段
					column.setFieldahead(part[0]);
					column.setFDataobjectfield(new FDataobjectfield(part[1]));
				} else {
					String s = columnObject.getString("fieldahead");
					// 有 fieldahead 没有 .with.是父模块字段
					if (s != null && s.length() > 0) {
						// 父模块的字段
						column.setFieldahead(part[0]);
						column.setFDataobjectfield(new FDataobjectfield(part[1]));
					} else {
						// 当前模块的基本字段
						column.setFDataobjectfield(new FDataobjectfield(part[0]));
					}
				}
			} else {
				column.setFDataobjectfield(scheme.getFDataobject()._getPrimaryKeyField());
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

	public ActionResult deleteFieldScheme(String schemeid) {

		ActionResult result = new ActionResult();
		FDataanalyseselectfieldscheme scheme = dao.findById(FDataanalyseselectfieldscheme.class, schemeid);
		if (scheme.getFUser() == null) {
			result.setSuccess(false);
			result.setMsg("这是系统字段组方案，你不能删除！");
		} else if (scheme.getFUser().getUserid().equals(Local.getUserid())) {
			dao.delete(scheme);
		} else {
			result.setSuccess(false);
			result.setMsg("这是其他用户的字段组方案，你不能删除！");
		}
		return result;

	}

}
