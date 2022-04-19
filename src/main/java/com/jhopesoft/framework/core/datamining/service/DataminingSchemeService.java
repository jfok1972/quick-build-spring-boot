package com.jhopesoft.framework.core.datamining.service;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.datamining.FDataminingcolumngroup;
import com.jhopesoft.framework.dao.entity.datamining.FDataminingfilter;
import com.jhopesoft.framework.dao.entity.datamining.FDatamininggroup;
import com.jhopesoft.framework.dao.entity.datamining.FDataminingrowgroup;
import com.jhopesoft.framework.dao.entity.datamining.FDataminingrowgrouppath;
import com.jhopesoft.framework.dao.entity.datamining.FDataminingscheme;
import com.jhopesoft.framework.dao.entity.datamining.FDataminingselectfield;
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
public class DataminingSchemeService {

	@Autowired
	private DaoImpl dao;

	/**
	 * 根据模块名称取得----行展开方案
	 * 
	 * @param moduleName
	 * @return
	 */
	public JSONArray getSchemes(String moduleName) {
		FDataobject object = DataObjectUtils.getDataObject(moduleName);
		JSONArray result = new JSONArray();
		for (FDataminingscheme ascheme : object.getFDataminingschemes()) {
			// 共享的方案，或者是本人的方案，或者是没有设定用户的方案都可以被当前登录用户看到
			// 有些只供图表和分析表格使用的方案可以不被所有用户看到。
			boolean isVisible = BooleanUtils.isTrue(ascheme.getIsshare()) || ascheme.getFUser() == null ||
					(ascheme.getFUser() != null && Local.getUserid().equals(ascheme.getFUser().getUserid()));
			if (isVisible) {
				result.add(getaScheme(ascheme));
			}
		}
		return result;
	}

	public JSONObject getaScheme(FDataminingscheme ascheme) {
		JSONObject jsonobject = new JSONObject();
		jsonobject.put("schemeid", ascheme.getSchemeid());
		jsonobject.put("savepath", ascheme.isRowexpandpath());
		jsonobject.put("ownerfilter", ascheme.isOwnerfilter());
		jsonobject.put(Constants.TEXT, ascheme.getTitle() + " (" + (ascheme.isRowexpandpath() ? "路径" : "每行")
				+ (ascheme.isOwnerfilter() ? ",筛选" : "") + ")");
		jsonobject.put(Constants.TITLE, ascheme.getTitle());
		jsonobject.put("iconCls", ascheme.getIconcls());
		return jsonobject;
	}

	public JSONObject getSchemeDetail(String schemeid) {
		JSONObject result = new JSONObject();
		FDataminingscheme scheme = dao.findById(FDataminingscheme.class, schemeid);
		result.put("rowGroup", getRowDetail(scheme));
		result.put("fieldGroup", getFieldColumns(scheme.getFDataminingselectfields()));
		result.put("columnGroup", getColumnSchemeDetail(scheme.getFDataminingcolumngroups()));
		result.put("setting", scheme.getOthersetting());
		if (scheme.isOwnerfilter()) {
			String filterString = "[]";
			for (FDataminingfilter filter : scheme.getFDataminingfilters()) {
				filterString = filter.getOthersetting();
			}
			result.put("filter", filterString);
		}
		return result;
	}

	public JSONArray getRowDetail(FDataminingscheme scheme) {
		if (scheme.isRowexpandpath()) {
			return getRowSchemePath(scheme);
		} else {
			return getRowSchemeDetail(scheme.getFDataminingrowgroups());
		}
	}

	private JSONArray getRowSchemeDetail(Set<FDataminingrowgroup> details) {
		JSONArray result = new JSONArray();
		for (FDataminingrowgroup detail : details) {
			JSONObject object = new JSONObject();
			object.put(Constants.TEXT, detail.getTitle());
			object.put("text_", detail.getOrgintitle());
			object.put("value", detail.getKeyvalue());
			object.put(Constants.CONDITION, detail.getGroupcondition());

			String[] part = detail.getOthersetting().split(";");

			if (part.length > 0 && part[0].length() > 0) {
				object.put("expanded", part[0].equalsIgnoreCase(Constants.TRUE));
			}
			if (part.length > 1 && part[1].length() > 0) {
				object.put(Constants.MODULE_NAME, part[1]);
			}

			if (detail.getFDataminingrowgroups().size() == 0)
				object.put("leaf", true);
			else {
				object.put("leaf", false);
				object.put(Constants.CHILDREN, getRowSchemeDetail(detail.getFDataminingrowgroups()));
			}
			result.add(object);
		}
		return result;
	}

	private JSONArray getFieldColumns(Set<FDataminingselectfield> details) {
		JSONArray result = new JSONArray();
		for (FDataminingselectfield detail : details) {
			JSONObject object = new JSONObject();
			object.put(Constants.TEXT, detail.getTitle());
			if (detail.getFDataminingselectfields().size() == 0) {
				object.put("width", 50);
				String aggregatefieldname = detail.getAggregate() + "." + detail.getFDataobjectfield().getFieldname();
				String itemId = detail.getFDataobjectfield().getFieldid() + "|" + detail.getAggregate();
				if (detail.getFieldahead() != null) {
					if (detail.getFieldahead().indexOf(".with") > 0) {
						// 子模块聚合字段
						itemId = detail.getFieldahead() + "|" + itemId;
						object.put("fieldahead", detail.getFieldahead());
						String[] withpart = detail.getFieldahead().split("\\.with\\.");
						aggregatefieldname = detail.getAggregate() + "." + withpart[0] + "."
								+ detail.getFDataobjectfield().getFieldname() + Constants.DOTWITHDOT + withpart[1];
					} else {
						// 父模块的聚合字段
						itemId = detail.getFieldahead() + "|" + itemId;
						object.put("fieldahead", detail.getFieldahead());
						aggregatefieldname = detail.getAggregate() + "." + detail.getFieldahead() + "."
								+ detail.getFDataobjectfield().getFieldname();
					}
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
				object.put("columns", getFieldColumns(detail.getFDataminingselectfields()));
			}
			result.add(object);
		}
		return result;
	}

	private JSONArray getColumnSchemeDetail(Set<FDataminingcolumngroup> details) {
		JSONArray result = new JSONArray();
		for (FDataminingcolumngroup detail : details) {
			JSONObject object = new JSONObject();
			object.put(Constants.TEXT, detail.getTitle());
			object.put(Constants.CONDITION, detail.getGroupcondition());
			if (detail.getFDataminingcolumngroups().size() == 0)
				object.put("leaf", true);
			else {
				object.put("leaf", false);
				object.put(Constants.CHILDREN, getColumnSchemeDetail(detail.getFDataminingcolumngroups()));
			}
			result.add(object);
		}
		return result;
	}

	/**
	 * 新增一个ＢＩ方案
	 * 
	 * @param moduleName      基本准块名称
	 * @param title           方案名称
	 * @param aggregateFields 选择的字段值
	 * @param columnGroup     分组方案定义
	 */
	public ActionResult addScheme(String moduleName, String title, Boolean savepath, String fieldGroup,
			String columnGroup, String rowGroup, Boolean ownerfilter, String filter, String setting, Boolean isshare) {
		FDataobject object = DataObjectUtils.getDataObject(moduleName);
		FDataminingscheme scheme = new FDataminingscheme();
		scheme.setFDataobject(object);
		scheme.setCreatedate(new Date());
		scheme.setCreater(Local.getUserid());
		scheme.setRowexpandpath(savepath);
		scheme.setOwnerfilter(ownerfilter);
		scheme.setIsshare(BooleanUtils.isTrue(isshare));
		scheme.setFUser(Local.getDao().findById(FUser.class, Local.getUserid()));
		if (StringUtils.isNotEmpty(setting)) {
			scheme.setOthersetting(setting);
		}
		if (object.getFDataminingschemes().size() == 0)
			scheme.setOrderno(10);
		else {
			int lastorderno = 0;
			for (FDataminingscheme ascheme : object.getFDataminingschemes()) {
				lastorderno = ascheme.getOrderno();
			}
			scheme.setOrderno(lastorderno + 10);
		}
		if (title == null || title.length() == 0) {
			title = "新建的数据分析方案";
		}
		scheme.setTitle(title.length() > 50 ? title.substring(0, 50) : title);
		scheme.setFDatamininggroup(dao.findByPropertyFirst(FDatamininggroup.class, Constants.TITLE, object.getTitle()));
		if (scheme.getFDatamininggroup() == null) {
			FDatamininggroup group = new FDatamininggroup();
			group.setTitle(object.getTitle());
			dao.save(group);
			scheme.setFDatamininggroup(group);
		}
		dao.save(scheme);

		JSONObject jsonobject = JSONObject.parseObject("{ children :" + rowGroup + "}");
		if (savepath) {
			saveNewPaths(scheme, (JSONArray) jsonobject.get(Constants.CHILDREN));
		} else {
			saveNewRows(scheme, (JSONArray) jsonobject.get(Constants.CHILDREN), null);
		}
		jsonobject = JSONObject.parseObject("{ children :" + fieldGroup + "}");
		saveFieldColumns(scheme, (JSONArray) jsonobject.get(Constants.CHILDREN), null);

		jsonobject = JSONObject.parseObject("{ children :" + columnGroup + "}");
		saveNewColumns(scheme, (JSONArray) jsonobject.get(Constants.CHILDREN), null);

		if (BooleanUtils.isTrue(ownerfilter)) {
			// 虽然选了要保存条件，但是没有条件
			if (StringUtils.isNotEmpty(filter) && filter.length() > Constants.INT_3) {
				FDataminingfilter afilter = new FDataminingfilter();
				afilter.setFDataminingscheme(scheme);
				afilter.setOthersetting(filter);
				dao.save(afilter);
			} else {
				// 虽然没有条件，也不设置为false,有时候这个方案不需要保存当前设置的条件
				// scheme.setOwnerfilter(false);
				// dao.save(scheme);
			}
		}

		ActionResult result = new ActionResult();
		result.setMsg(scheme.getTitle());
		result.setTag(getaScheme(scheme));
		return result;
	}

	/**
	 * 新增一个ＢＩ方案
	 * 
	 * @param moduleName      基本准块名称
	 * @param title           方案名称
	 * @param aggregateFields 选择的字段值
	 * @param columnGroup     分组方案定义
	 */
	public ActionResult editScheme(String schemeid, String name, Boolean savepath, String fieldGroup,
			String columnGroup, String rowGroup, Boolean ownerfilter, String filter, String setting) {
		ActionResult result = new ActionResult();
		FDataminingscheme scheme = dao.findById(FDataminingscheme.class, schemeid);
		if (scheme.getFUser() == null) {
			result.setSuccess(false);
			result.setMsg("这是系统数据分析方案，你不能进行修改！");
		} else if (scheme.getFUser().getUserid().equals(Local.getUserid())) {
			if (!scheme.getTitle().equals(name)) {
				scheme.setTitle(name);
			}
			if (StringUtils.isNotEmpty(setting)) {
				scheme.setOthersetting(setting);
			}
			// 字段分组
			if (StringUtils.isNotEmpty(fieldGroup)) {
				Iterator<FDataminingselectfield> iterator = scheme.getFDataminingselectfields().iterator();
				while (iterator.hasNext()) {
					FDataminingselectfield column = iterator.next();
					iterator.remove();
					column.setFDataminingscheme(null);
					dao.delete(column);
				}
				JSONObject jsonobject = JSONObject.parseObject("{ children :" + fieldGroup + "}");
				saveFieldColumns(scheme, (JSONArray) jsonobject.get(Constants.CHILDREN), null);
			}
			// 列分组
			if (StringUtils.isNotEmpty(columnGroup)) {
				Iterator<FDataminingcolumngroup> iterator = scheme.getFDataminingcolumngroups().iterator();
				while (iterator.hasNext()) {
					FDataminingcolumngroup column = iterator.next();
					iterator.remove();
					column.setFDataminingscheme(null);
					dao.delete(column);
				}
				JSONObject jsonobject = JSONObject.parseObject("{ children :" + columnGroup + "}");
				saveNewColumns(scheme, (JSONArray) jsonobject.get(Constants.CHILDREN), null);
			}
			// 筛选条件，如果ownerfilter=true,并且filter.length > 3,才置为true
			// 如果选择了更新筛选条件，那么将会更新条件，否则条件不变。并不能取消。
			if (BooleanUtils.isTrue(ownerfilter)) {

				Iterator<FDataminingfilter> iterator = scheme.getFDataminingfilters().iterator();
				while (iterator.hasNext()) {
					FDataminingfilter column = iterator.next();
					iterator.remove();
					column.setFDataminingscheme(null);
					dao.delete(column);
				}

				scheme.setOwnerfilter(true);
				if (StringUtils.isNotEmpty(filter) && filter.length() > Constants.INT_3) {
					FDataminingfilter afilter = new FDataminingfilter();
					afilter.setFDataminingscheme(scheme);
					afilter.setOthersetting(filter);
					dao.save(afilter);
				}
			}
			if (StringUtils.isNotEmpty(rowGroup)) {
				// 行分组
				scheme.setRowexpandpath(savepath);

				Iterator<FDataminingrowgroup> iterator = scheme.getFDataminingrowgroups().iterator();
				while (iterator.hasNext()) {
					FDataminingrowgroup column = iterator.next();
					iterator.remove();
					column.setFDataminingscheme(null);
					dao.delete(column);
				}

				Iterator<FDataminingrowgrouppath> iterator1 = scheme.getFDataminingrowgrouppaths().iterator();
				while (iterator1.hasNext()) {
					FDataminingrowgrouppath column = iterator1.next();
					iterator1.remove();
					column.setFDataminingscheme(null);
					dao.delete(column);
				}

				JSONObject jsonobject = JSONObject.parseObject("{ children :" + rowGroup + "}");
				if (savepath) {
					saveNewPaths(scheme, (JSONArray) jsonobject.get(Constants.CHILDREN));
				} else {
					saveNewRows(scheme, (JSONArray) jsonobject.get(Constants.CHILDREN), null);
				}
			}
			dao.update(scheme);
		} else {
			result.setSuccess(false);
			result.setMsg("这是其他用户的数据分析方案，你不能进行修改！");
		}
		result.setTag(getaScheme(scheme));
		return result;
	}

	private void saveNewPaths(FDataminingscheme scheme, JSONArray arrays) {
		for (int i = 0; i < arrays.size(); i++) {
			JSONObject rowObject = arrays.getJSONObject(i);

			FDataminingrowgrouppath path = new FDataminingrowgrouppath();
			path.setFDataminingscheme(scheme);
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
			if (path.getPathtype().equals("edittext") || path.getPathtype().equals("combinerows") ||
					"combineotherrows".equals(path.getPathtype())) {
				path.setTitle(getValue(rowObject, Constants.TEXT));
			}
			// 删除记录和删除子节点，把删除的记录的描述保存
			if (path.getPathtype().equals("deleterow") || path.getPathtype().equals("deletechildren")) {
				path.setTitle(getValue(rowObject, "conditiontext"));
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
	private void setFieldId(FDataminingrowgrouppath path, String fieldid) {
		String[] part = fieldid.split("-");
		if (part.length == 1) {
			path.setFDataobjectfield(new FDataobjectfield(part[0]));
		} else {
			path.setFDataobjectfield(new FDataobjectfield(part[0]));
			path.setFieldgrouptype(part[1]);
		}
	}

	public JSONArray getRowSchemePath(FDataminingscheme scheme) {
		JSONArray result = new JSONArray();
		for (FDataminingrowgrouppath path : scheme.getFDataminingrowgrouppaths()) {
			JSONObject object = new JSONObject();
			if (path.getPathtype().equals("combinerows") ||
					"combineotherrows".equals(path.getPathtype())) {
				object.put(Constants.CONDITION, path.getGroupcondition());
			}
			object.put("conditionpath", path.getConditionpath());
			object.put(Constants.TYPE, path.getPathtype());
			if (path.getPathtype().equals("edittext") || path.getPathtype().equals("combinerows") ||
					"combineotherrows".equals(path.getPathtype())) {
				object.put(Constants.TEXT, path.getTitle());
			} else {
				object.put(Constants.TITLE, path.getTitle());
			}
			if (path.getPathtype().equals("deleterow") || path.getPathtype().equals("deletechildren")) {
				object.put("conditiontext", path.getTitle());
				object.remove(Constants.TITLE);
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
	private void saveNewRows(FDataminingscheme scheme, JSONArray arrays, FDataminingrowgroup p) {
		for (int i = 0; i < arrays.size(); i++) {
			JSONObject rowObject = arrays.getJSONObject(i);
			FDataminingrowgroup row = new FDataminingrowgroup();
			row.setFDataminingrowgroup(p);
			row.setFDataminingscheme(scheme);
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

	/**
	 * 保存列分组方案名称
	 * 
	 * @param scheme
	 * @param arrays
	 * @param p
	 */
	private void saveFieldColumns(FDataminingscheme scheme, JSONArray arrays, FDataminingselectfield p) {
		for (int i = 0; i < arrays.size(); i++) {
			JSONObject columnObject = arrays.getJSONObject(i);
			FDataminingselectfield column = new FDataminingselectfield();
			column.setFDataminingselectfield(p);
			column.setFDataminingscheme(scheme);
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
				saveFieldColumns(null, (JSONArray) columnObject.get("columns"), column);
			}
		}
	}

	/**
	 * 保存列分组方案名称
	 * 
	 * @param scheme
	 * @param arrays
	 * @param p
	 */
	private void saveNewColumns(FDataminingscheme scheme, JSONArray arrays, FDataminingcolumngroup p) {
		for (int i = 0; i < arrays.size(); i++) {
			JSONObject columnObject = arrays.getJSONObject(i);
			FDataminingcolumngroup column = new FDataminingcolumngroup();
			column.setFDataminingcolumngroup(p);
			column.setFDataminingscheme(scheme);
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

	public ActionResult deleteScheme(String schemeid) {
		ActionResult result = new ActionResult();
		FDataminingscheme scheme = dao.findById(FDataminingscheme.class, schemeid);
		if (scheme.getFUser() == null) {
			result.setSuccess(false);
			result.setMsg("这是系统数据分析方案，你不能删除！");
		} else if (scheme.getFUser().getUserid().equals(Local.getUserid())) {
			if (scheme.getFovChartschemes().size() > 0) {
				result.setSuccess(false);
				result.setMsg("请先删除此数据分析方案的所有图表方案！");
			} else {
				dao.delete(scheme);
			}
		} else {
			result.setSuccess(false);
			result.setMsg("这是其他用户的数据分析方案，你不能删除！");
		}
		return result;
	}

}
