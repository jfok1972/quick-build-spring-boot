package com.jhopesoft.framework.core.datamining.service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.GridParams;
import com.jhopesoft.framework.bean.HierarchyIDPID;
import com.jhopesoft.framework.core.datamining.utils.DataminingSqlGenerate;
import com.jhopesoft.framework.core.objectquery.filter.UserDefineFilter;
import com.jhopesoft.framework.core.objectquery.generate.SqlGenerate;
import com.jhopesoft.framework.core.objectquery.module.BaseModule;
import com.jhopesoft.framework.core.objectquery.module.ModuleHierarchyGenerate;
import com.jhopesoft.framework.core.objectquery.module.ModuleOnlyHierarchyGenerate;
import com.jhopesoft.framework.core.objectquery.module.ParentModule;
import com.jhopesoft.framework.core.objectquery.sqlfield.AggregateField;
import com.jhopesoft.framework.core.objectquery.sqlfield.ColumnField;
import com.jhopesoft.framework.core.objectquery.sqlfield.DictionaryFieldGenerate;
import com.jhopesoft.framework.core.objectquery.sqlfield.GroupConditionUtils;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.Dao;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.datamining.FDataanalyseselectfielddetail;
import com.jhopesoft.framework.dao.entity.datamining.FDataanalyseselectfieldscheme;
import com.jhopesoft.framework.dao.entity.datamining.FDataminingexpandgroup;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectview;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.dao.entity.utils.FFunction;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectFieldUtils;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.HierarchyIDPIDUtils;
import com.jhopesoft.framework.utils.MD5;
import com.jhopesoft.framework.utils.ObjectFunctionUtils;
import com.jhopesoft.platform.service.ModuleHierarchyService;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Service
public class DataminingService {

	public static final String VALUE = "value";
	private static final String TEMPFIELDID = "tempfieldid";
	private static final String FIELDID = "fieldid";
	private static final String MENU = "menu";
	@Autowired
	private ModuleHierarchyService moduleHierarchyService;

	@Autowired
	private DaoImpl dao;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<?> fetchData(String moduleName, List<String> conditions, List<String> fields, String groupfieldid,
			List<String> parentconditions, List<UserDefineFilter> navigatefilters, String viewschemeid,
			List<UserDefineFilter> userfilters, boolean addchecked, JSONObject sqlparam, boolean isnumberordername) {
		FDataobject object = DataObjectUtils.getDataObject(moduleName);
		if (!ObjectFunctionUtils.allowQuery(object)) {
			return new ArrayList();
		}
		List<AggregateField> aggregateFields = new ArrayList<AggregateField>();
		int c = 101;
		int scalecount = 1;
		String[] fieldsScale = new String[(conditions != null ? conditions.size() : 0) * fields.size() * 2
				+ fields.size() * 2 + 1 + 1];
		fieldsScale[0] = VALUE;
		List<String> wavgs = new ArrayList<String>();
		int fieldCount = 1;
		for (String field : fields) {
			if (isnumberordername) {
				fieldsScale[scalecount] = "jf" + String.format("%03d", fieldCount++);
			} else {
				fieldsScale[scalecount] = "jf" + MD5.MD5Encode(field).substring(0, 27);
			}
			if (field.startsWith("wavg")) {
				wavgs.add(fieldsScale[scalecount]);
				fieldsScale[scalecount + 1] = fieldsScale[scalecount] + "2";
				fieldsScale[scalecount] = fieldsScale[scalecount] + "1";
				scalecount++;
			}
			AggregateField af = new AggregateField(object, moduleName, field, null, "af_" + c++);
			aggregateFields.add(af);
			scalecount++;
		}
		int conditionCount = 1;
		if (conditions != null) {
			for (String condition : conditions) {
				fieldCount = 1;
				for (String field : fields) {
					String fieldandcondition = field + (condition == null ? "" : condition);
					if (isnumberordername) {
						fieldsScale[scalecount] = "jf" + String.format("%03d", fieldCount++) + "jxy"
								+ String.format("%03d", conditionCount);
					} else {
						fieldsScale[scalecount] = "jf" + MD5.MD5Encode(fieldandcondition).substring(0, 27);
					}
					if (field.startsWith("wavg")) {
						wavgs.add(fieldsScale[scalecount]);
						fieldsScale[scalecount + 1] = fieldsScale[scalecount] + "2";
						fieldsScale[scalecount] = fieldsScale[scalecount] + "1";
						scalecount++;
					}
					AggregateField af = new AggregateField(object, moduleName, field, condition, "af_" + c++);
					aggregateFields.add(af);
					scalecount++;
				}
				conditionCount++;
			}
		}
		DataminingSqlGenerate generate = new DataminingSqlGenerate();
		generate.setUserDefineFilters(new ArrayList<UserDefineFilter>());
		if (userfilters != null) {
			generate.getUserDefineFilters().addAll(userfilters);
		}
		if (navigatefilters != null) {
			generate.getUserDefineFilters().addAll(navigatefilters);
		}
		if (sqlparam != null) {
			generate.setSqlparam(sqlparam);
		}
		if (viewschemeid != null && viewschemeid.length() > 0) {
			generate.setDataobjectview(dao.findById(FDataobjectview.class, viewschemeid));
		}
		List<String> fs = new ArrayList<String>();
		if (parentconditions != null) {
			fs.addAll(parentconditions);
		}
		if (fs.size() > 0) {
			generate.setParentConditions(GroupConditionUtils.changeGroupConditionTo(fs));
		}
		if (StringUtils.isNotBlank(groupfieldid) && groupfieldid.startsWith("{")) {
			groupfieldid = DataObjectFieldUtils.parseFieldStrFromObject(moduleName,
					JSONObject.parseObject(groupfieldid));
		}
		String groupfieldahead = groupfieldid;
		if (groupfieldid != null && groupfieldid.length() > 0) {
			String[] s = groupfieldid.split("-");
			if (s.length == Constants.INT_2) {
				groupfieldahead = s[0];
				if (s[1].length() < Constants.INT_3) {
					generate.setLeveltype(s[1]);
				} else { 
					FFunction function = dao.findById(FFunction.class, s[1]);
					if (function == null) {
						function = dao.findByPropertyFirst(FFunction.class, Constants.TITLE, s[1]);
						if (function == null) {
							throw new RuntimeException("自定义函数id或title未找到：" + s[1]);
						}
					}
					generate.setfFunction(function);
				}
			}
			generate.setGroupfield(GroupConditionUtils.getGroupField(groupfieldahead, moduleName));
			generate.setGroupfieldid(groupfieldahead);
		}
		generate.setDataobject(object);
		generate.setAggretageFields(aggregateFields);
		String dataminingSql = generate.generateSelect();
		String groupModuleName = null;
		if (generate.getGroupfield() != null) {
			FDataobject groupobject = generate.getGroupfield().getFDataobject();
			if (generate.getGroupfield() == groupobject._getPrimaryKeyField()) {
				dataminingSql = String.format(
						"SELECT datamining_.* , text_.%s FROM ( %s ) datamining_ "
								+ "left outer join %s text_ on text_.%s = datamining_.datamininggroup__",
						groupobject._getNameField()._getSelectName(null), dataminingSql, groupobject._getTablename(),
						groupobject._getPrimaryKeyField()._getSelectName(null));
				fieldsScale[scalecount] = Constants.TEXT;
				groupModuleName = groupobject.getObjectname();
			} else {
				if (generate.getGroupfield().getFDictionary() != null) {
					dataminingSql = String.format("SELECT datamining_.* , "
							+ DictionaryFieldGenerate.getDictionaryTextField("datamining_.datamininggroup__",
									generate.getGroupfield().getFDictionary())
							+ " FROM ( %s ) datamining_ ", dataminingSql);
					fieldsScale[scalecount] = Constants.TEXT;
				}
			}
		}
		List<?> result = getData(dataminingSql, fieldsScale, wavgs, groupModuleName, addchecked, object);
		String parentconditionsString = "";
		if (parentconditions != null) {
			for (String s : parentconditions) {
				parentconditionsString += s + "|||";
			}
		}
		if (groupfieldid == null && parentconditionsString.length() > 1) {
			parentconditionsString = parentconditionsString.substring(0, parentconditionsString.length() - 3);
		}
		for (Object resultobject : result) {
			Map resultjsonobject = (Map) resultobject;
			String rowCondition = parentconditionsString
					+ (groupfieldid != null
							? groupfieldid + "="
									+ (resultjsonobject.get(VALUE) != null ? resultjsonobject.get(VALUE).toString()
											: "未定义")
							: "");
			resultjsonobject.put(Constants.ROWID, MD5.MD5Encode(rowCondition));
		}
		if (generate.getLeveltype() != null) {
			if (generate.getGroupfield().getFDataobject()._isIdPidLevel()) {
				addHasChilden(generate.getGroupfield().getFDataobject().getObjectname(), result);
			} else {
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private void addHasChilden(String objectname, List<?> result) {
		for (Object object : result) {
			Map<String, Object> record = (Map<String, Object>) object;
			record.put("hasChildren", HierarchyIDPIDUtils.hasChildren(objectname, record.get(VALUE).toString()));
		}
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private void deleteNotThisLevelRecord(List<?> result, int length) {
		for (int i = result.size() - 1; i >= 0; i--) {
			Map<String, Object> record = (Map<String, Object>) result.get(i);
			if (record.get(VALUE).toString().length() != length) {
				result.remove(i);
			}
		}

	}

	/**
	 * 根据模块的codelevel属性加入所有的级别，在返回的数据中加入level_,用于前台使用
	 * 
	 * @param moduleName
	 * @param columns
	 * @param conditions
	 * @param fields
	 * @param groupfieldid
	 * @param parentconditions
	 * @param navigatefilters
	 * @param viewschemeid
	 * @param userfilters
	 * @param addchecked
	 * @return
	 */
	public List<?> fetchLevelAllData(String moduleName, List<String> conditions, List<String> fields,
			String groupfieldid, List<String> parentconditions, List<UserDefineFilter> navigatefilters,
			String viewschemeid, List<UserDefineFilter> userfilters, boolean addchecked, JSONObject sqlparam,
			boolean isnumberordername) {
		String onlygroupfield = groupfieldid;
		String[] part = groupfieldid.split("\\|");
		if (part.length > 1) {
			onlygroupfield = part[1];
		}
		FDataobjectfield groupfield = dao.findById(FDataobjectfield.class, onlygroupfield);
		FDataobject object = groupfield.getFDataobject();
		if (object._isIdPidLevel()) {
			return fetchIdPidAllData(moduleName, conditions, fields, groupfieldid, parentconditions, navigatefilters,
					viewschemeid, userfilters, addchecked, sqlparam, isnumberordername);
		} else {
			return fetchCodeLevelAllData(moduleName, conditions, fields, groupfieldid, parentconditions,
					navigatefilters, viewschemeid, userfilters, addchecked, sqlparam, isnumberordername);
		}
	}

	/**
	 * 根据模块的id-pid，在返回的数据中加入level_,用于前台使用
	 * 
	 * @param moduleName
	 * @param columns
	 * @param conditions
	 * @param fields
	 * @param groupfieldid
	 * @param parentconditions
	 * @param navigatefilters
	 * @param viewschemeid
	 * @param userfilters
	 * @param addchecked
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<?> fetchIdPidAllData(String moduleName, List<String> conditions, List<String> fields,
			String groupfieldid, List<String> parentconditions, List<UserDefineFilter> navigatefilters,
			String viewschemeid, List<UserDefineFilter> userfilters, boolean addchecked, JSONObject sqlparam,
			boolean isnumberordername) {

		String onlygroupfield = groupfieldid;
		String[] part = groupfieldid.split("\\|");
		if (part.length > 1) {
			onlygroupfield = part[1];
		}

		FDataobjectfield groupfield = dao.findById(FDataobjectfield.class, onlygroupfield);
		FDataobject object = groupfield.getFDataobject();

		int codelevels = HierarchyIDPIDUtils.getIDPIDMaxLevel(object);

		Object[] levels = new Object[codelevels];
		Map<String, Map<String, Object>> maps = new HashMap<String, Map<String, Object>>(0);
		for (int i = 1; i <= codelevels; i++) {
			List<?> alevelresult = fetchData(moduleName, conditions, fields, groupfieldid + "-" + i, parentconditions,
					navigatefilters, viewschemeid, userfilters, addchecked, sqlparam, isnumberordername);
			for (Object record : alevelresult) {
				Map<String, Object> maprecord = (Map<String, Object>) record;
				maprecord.put("level_", i);
				maps.put(maprecord.get(VALUE).toString(), maprecord);
			}
			levels[i - 1] = alevelresult;
		}
		Map<String, HierarchyIDPID> idmaps = HierarchyIDPIDUtils.getHierarchyIDPIDMapsFromRequest(object);
		for (int i = 1; i < codelevels; i++) {
			List<Map<String, Object>> alevelresult = (List<Map<String, Object>>) levels[i];
			for (Map<String, Object> record : alevelresult) {
				String pcode = idmaps.get(record.get(VALUE).toString()).getParent().getId();
				Map<String, Object> pnode = maps.get(pcode);
				pnode.put("leaf", false);
				pnode.put("expanded", true);
				if (!pnode.containsKey(Constants.CHILDREN)) {
					pnode.put(Constants.CHILDREN, new ArrayList<Map<String, Object>>());
				}
				((ArrayList<Map<String, Object>>) pnode.get(Constants.CHILDREN)).add(record);
			}
		}
		return (List<?>) removeOneLevel((List<?>) levels[0]);
	}

	/**
	 * 如果第一层一个，第二层一个，那么把第一层删了。1，2，3层都是一个，1，2层删了。
	 * 
	 * @param level0
	 * @return
	 */
	private List<?> removeOneLevel(List<?> level0) {
		if (level0.size() == 1) {
			Map<?, ?> childsmap = (Map<?, ?>) level0.get(0);
			if (childsmap != null) {
				List<?> nextlevel = (List<?>) childsmap.get(Constants.CHILDREN);
				if (nextlevel != null) {
					return removeOneLevel(nextlevel);
				}
			}
		}
		return level0;
	}

	/**
	 * 根据模块的codelevel属性加入所有的级别，在返回的数据中加入level_,用于前台使用
	 * 
	 * @param moduleName
	 * @param columns
	 * @param conditions
	 * @param fields
	 * @param groupfieldid
	 * @param parentconditions
	 * @param navigatefilters
	 * @param viewschemeid
	 * @param userfilters
	 * @param addchecked
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<?> fetchCodeLevelAllData(String moduleName, List<String> conditions, List<String> fields,
			String groupfieldid, List<String> parentconditions, List<UserDefineFilter> navigatefilters,
			String viewschemeid, List<UserDefineFilter> userfilters, boolean addchecked, JSONObject sqlparam,
			boolean isnumberordername) {

		String onlygroupfield = groupfieldid;
		String[] part = groupfieldid.split("\\|");
		if (part.length > 1) {
			onlygroupfield = part[1];
		}
		FDataobjectfield groupfield = dao.findById(FDataobjectfield.class, onlygroupfield);
		FDataobject object = groupfield.getFDataobject();
		String[] codelevels = object.getCodelevel().split(Constants.COMMA);
		int[] pcodelevels = new int[codelevels.length];
		for (int i = 1; i < pcodelevels.length; i++) {
			pcodelevels[i] = pcodelevels[i - 1] + Integer.parseInt(codelevels[i - 1]);
		}

		Object[] levels = new Object[codelevels.length];
		Map<String, Map<String, Object>> maps = new HashMap<String, Map<String, Object>>(0);
		for (int i = 1; i <= codelevels.length; i++) {
			List<?> alevelresult = fetchData(moduleName, conditions, fields, groupfieldid + "-" + i, parentconditions,
					navigatefilters, viewschemeid, userfilters, addchecked, sqlparam, isnumberordername);
			for (Object record : alevelresult) {
				Map<String, Object> maprecord = (Map<String, Object>) record;
				maprecord.put("level_", i);
				maps.put(maprecord.get(VALUE).toString(), maprecord);
			}
			levels[i - 1] = alevelresult;
		}
		for (int i = 1; i < codelevels.length; i++) {
			List<Map<String, Object>> alevelresult = (List<Map<String, Object>>) levels[i];
			for (Map<String, Object> record : alevelresult) {
				String pcode = record.get(VALUE).toString().substring(0, pcodelevels[i]);
				Map<String, Object> pnode = maps.get(pcode);
				pnode.put("leaf", false);
				pnode.put("expanded", true);
				if (!pnode.containsKey(Constants.CHILDREN)) {
					pnode.put(Constants.CHILDREN, new ArrayList<Map<String, Object>>());
				}
				((ArrayList<Map<String, Object>>) pnode.get(Constants.CHILDREN)).add(record);
			}
		}
		return (List<?>) levels[0];
	}

	private List<?> getData(String sql, String[] scales, List<String> wavgs, String groupModuleName, boolean addchecked,
			FDataobject object) {
		Dao dataSourceDao = Local.getBusinessDao();
		NativeQuery<?> query = dataSourceDao.getCurrentSession().createNativeQuery(sql);
		Map<String, Object> param = DataObjectUtils.getSqlParameter();
		if (param != null) {
			for (String key : param.keySet()) {
				if (sql.indexOf(":" + key) != -1) {
					query.setParameter(key, param.get(key));
				}
			}
		}
		List<?> result = query.getResultList();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < result.size(); i++) {
			Object obj = result.get(i);
			Map<String, Object> dataMap = new HashMap<String, Object>(0);
			if (obj instanceof Object[]) {
				Object[] datas = (Object[]) obj;
				for (int j = 0; j < scales.length; j++) {
					if (scales[j] != null) {
						dataMap.put(scales[j], j < datas.length ? datas[j] : null);
					}
				}
			} else {
				dataMap.put(scales[0], obj);
			}
			for (String s : wavgs) {
				addWavgKey(s, dataMap);
			}
			if (addchecked) {
				dataMap.put("checked", false);
			}
			if (groupModuleName != null) {
				dataMap.put(Constants.MODULE_NAME, groupModuleName);
			}
			dataMap.put("leaf", true);
			Object value = dataMap.get(VALUE);
			if (value != null) {
				if (value instanceof Boolean) {
					dataMap.put(VALUE, booleanToValue(value));
					dataMap.put(Constants.TEXT, booleanToText(value));
				}
			} else {
				dataMap.put(VALUE, Constants.NULL);
				dataMap.put(Constants.TEXT, "空");
			}
			if (!dataMap.containsKey(Constants.TEXT)) {
				dataMap.put(Constants.TEXT, dataMap.get(VALUE));
			}
			dataList.add(dataMap);
		}
		return dataList;
	}

	/**
	 * boolean型分组_的字段返回的结果，有时候是integer,有时候是boolean
	 * 
	 * @param b
	 * @return
	 */
	private String booleanToValue(Object b) {
		Boolean value = (Boolean) b;
		return value ? "1" : "0";
	}

	private String booleanToText(Object b) {
		Boolean value = (Boolean) b;
		return value ? "是" : "否";
	}

	public void addWavgKey(String key, Map<String, Object> dataMap) {
		Object fz = dataMap.get(key + "1");
		Object fm = dataMap.get(key + "2");
		if (fm == null) {
			dataMap.put(key, null);
			return;
		} else if (fz == null) {
			dataMap.put(key, 0.0);
			return;
		}
		try {
			dataMap.put(key, getDoubleValue(fz) / getDoubleValue(fm));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Double getDoubleValue(Object object) {
		if (object instanceof Double) {
			return (Double) object;
		} else if (object instanceof Integer) {
			return (Integer) object * 1.0;
		} else if (object instanceof BigInteger) {
			return ((BigInteger) object).doubleValue();
		} else if (object instanceof Float) {
			return ((Float) object).doubleValue();
		} else {
			return Double.parseDouble(object.toString());
		}
	}

	public JSONArray getAllAggregateFields(String moduleName, String modulePath) {
		FDataobject module = DataObjectUtils.getDataObject(moduleName);
		if (module == null) {
			return null;
		}
		JSONArray results = new JSONArray();

		for (FDataobjectfield field : module.getFDataobjectfields()) {
			if (BooleanUtils.isNotTrue(field.getIsdisable()) && BooleanUtils.isNotTrue(field.getUserdisable())) {
				boolean cond = (field.getAllowaggregate() == null || field.getAllowaggregate() == false)
						&& !field.getFieldname().equals(module.getPrimarykey());
				if (cond) {
					continue;
				}
				JSONObject f = new JSONObject();
				String itemId;
				itemId = field.getFieldid().toString();
				if (modulePath != null && modulePath.length() > 0) {
					itemId = modulePath + "|" + itemId;
					f.put("fieldahead", modulePath);
				}
				f.put("itemId", itemId);
				f.put(Constants.TEXT, field.getFieldtitle());
				if (field.getFieldname().equals(module.getPrimarykey())) {
					f.put(Constants.TEXT, module.getTitle());
				}
				f.put("fieldType", field.getFieldtype());
				f.put("leaf", false);
				f.put("cls", field._getFieldCss());
				f.put("iconCls", field._isDateField() ? "x-fa fa-calendar" : null);
				f.put("checked", false);
				f.put("expanded", true);
				f.put(Constants.CHILDREN, moduleHierarchyService.getAggregationItems(field, itemId, modulePath, true));
				JSONObject nowGroup = null;
				for (Object vt : results) {
					if (((JSONObject) vt).get(Constants.TEXT).equals(field.getFieldgroup())) {
						nowGroup = (JSONObject) vt;
						break;
					}
				}
				if (nowGroup == null) {
					nowGroup = new JSONObject();
					nowGroup.put("itemId", field.getFieldgroup());
					nowGroup.put(Constants.TEXT, field.getFieldgroup());
					nowGroup.put("leaf", false);
					nowGroup.put("expanded", true);
					nowGroup.put("checked", false);
					JSONArray child = new JSONArray();
					child.add(f);
					nowGroup.put(Constants.CHILDREN, child);
					results.add(nowGroup);
				} else {
					((JSONArray) (nowGroup.get(Constants.CHILDREN))).add(f);
				}
			}
		}
		return results;
	}

	/**
	 * 根据 fieldid和函数以及数值分组，来取得选中分组的数据
	 * @param baseModuleName
	 * @param fieldid
	 * @param functionid
	 * @param numbergroupid
	 * @param userdefinefunction
	 * @return
	 * 
	 */
	public JSONArray getGroupFieldData(String baseModuleName, String fieldid, List<String> parentconditions,
			List<String> navigatefilters, boolean onlycontainerdata) {
		FDataobject baseDataobject = DataObjectUtils.getDataObject(baseModuleName);
		String fieldahead = null;
		String[] part = fieldid.split("\\|");
		if (part.length > 1) {
			fieldahead = part[0];
			fieldid = part[1];
		}
		FDataobjectfield groupfield = dao.findById(FDataobjectfield.class, fieldid);
		if (onlycontainerdata) {
			return getGroupFieldDataOnlyConainerData(baseDataobject, groupfield, fieldahead, parentconditions,
					navigatefilters);
		} else {
			return null;
		}

	}

	/**
	 * 取得分组名称 ，只包括有记录数据值
	 * 
	 * @param field
	 * @param fieldahead
	 * @param parentfilter
	 * @return
	 */
	public JSONArray getGroupFieldDataOnlyConainerData(FDataobject baseDataobject, FDataobjectfield groupfield,
			String fieldahead, List<String> parentconditions, List<String> navigatefilters) {

		SqlGenerate generate = new SqlGenerate();
		generate.setDataobject(baseDataobject);
		generate.setDistinct(true);
		generate.setAddIdField(false);
		generate.setAddNameField(false);
		generate.setAddBaseField(false);
		generate.setAddAllFormScheme(false);
		generate.setAddAllGridScheme(false);

		ColumnField groupColumnfield = new ColumnField();
		groupColumnfield.setFieldahead(fieldahead);
		groupColumnfield.setFDataobjectfield(groupfield);
		Set<ColumnField> fields = new HashSet<ColumnField>();
		fields.add(groupColumnfield);
		generate.setColumnFields(fields);

		List<String> fs = new ArrayList<String>();
		if (parentconditions != null) {
			fs.addAll(parentconditions);
		}
		if (navigatefilters != null) {
			fs.addAll(navigatefilters);
		}
		if (fs.size() > 0) {
			generate.setUserDefineFilters(GroupConditionUtils.changeGroupConditionTo(fs));
		}

		generate.pretreatment();
		String sql = generate.generateSelect();

		GridParams pg = new GridParams();
		pg.setStart(0);
		pg.setPaging(false);
		pg.setLimit(Integer.MAX_VALUE);
		JSONArray result = new JSONArray();
		Dao dataSourceDao = Local.getBusinessDao();
		List<?> sqlresult = dataSourceDao.executeSQLQueryPage(sql, generate.getFieldNames(), pg.getStart(),
				pg.getLimit(), pg.getLimit(), new Object[] {}).getData();
		for (Object object : sqlresult) {
			JSONObject jo = new JSONObject();
			jo.put("value", ((Map<?, ?>) object).get(generate.getFieldNames()[0]));
			result.add(jo);
		}
		return result;
	}

	private boolean hasContainerField(Set<FDataminingexpandgroup> groups, FDataobjectfield field, String fieldahead,
			String leveltype, FFunction function) {
		for (FDataminingexpandgroup group : groups) {
			if (group.getFDataobjectfield().equals(field)) {
				boolean condition = ((fieldahead == null && group.getFieldahead() == null)
						|| (fieldahead != null && fieldahead.equals(group.getFieldahead())))
						&& ((leveltype == null && group.getLeveltype() == null)
								|| (leveltype != null && leveltype.equals(group.getLeveltype())))
						&& ((function == null && group.getFFunction() == null)
								|| (function != null && function.equals(group.getFFunction())));
				if (condition) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 对一个可数据分析的模块的创建一个字段组方案，里面加入当前模块所有的可以聚合的字段，默认加入sum
	 * 
	 * @param dataobject
	 * @return
	 */
	public ActionResult createDataminingSelectFieldScheme(FDataobject dataobject) {
		Set<FDataanalyseselectfieldscheme> schemes = dataobject.getFDataanalyseselectfieldschemes();
		if (schemes.size() == 0) {
			FDataanalyseselectfieldscheme scheme = new FDataanalyseselectfieldscheme();
			scheme.setFDataobject(dataobject);
			scheme.setOrderno(10);
			scheme.setFUser(dao.findById(FUser.class, Local.getUserid()));
			scheme.setCreater(Local.getUserid());
			scheme.setCreatedate(new Timestamp(System.currentTimeMillis()));
			scheme.setTitle(dataobject.getTitle() + "的所有可聚合字段");
			dao.save(scheme);
			schemes.add(scheme);
			FDataanalyseselectfielddetail pkdetail = new FDataanalyseselectfielddetail();
			pkdetail.setFDataanalyseselectfieldscheme(scheme);
			pkdetail.setAggregate(Constants.COUNT);
			pkdetail.setFDataobjectfield(dataobject._getPrimaryKeyField());
			pkdetail.setTitle("记录条数");
			pkdetail.setOrderno(10);
			dao.save(pkdetail);
			scheme.getFDataanalyseselectfielddetails().add(pkdetail);
			int[] orderno = new int[] { 20 };
			dataobject.getFDataobjectfields().forEach(field -> {
				if (BooleanUtils.isNotTrue(field.getIsdisable()) && BooleanUtils.isTrue(field.getAllowaggregate())) {
					FDataanalyseselectfielddetail detail = new FDataanalyseselectfielddetail();
					detail.setFDataanalyseselectfieldscheme(scheme);
					if (field._isPercentField()) {
						detail.setAggregate(Constants.WAVG);
					} else {
						detail.setAggregate(Constants.SUM);
					}
					detail.setFDataobjectfield(field);
					detail.setTitle(field.getFieldtitle());
					detail.setOrderno(orderno[0]);
					orderno[0] += 10;
					dao.save(detail);
					scheme.getFDataanalyseselectfielddetails().add(detail);
				}
			});
		}
		return new ActionResult();
	}

	public ActionResult importDataminingExpandGroup(FDataobject dataobject) {
		BaseModule baseModule = ModuleHierarchyGenerate.genModuleHierarchy(dataobject, "main_", true);
		Set<FDataminingexpandgroup> dataminingexpandgroups = dataobject.getFDataminingexpandgroups();
		int orderno = 0;
		int count = 0;
		for (FDataminingexpandgroup group : dataminingexpandgroups) {
			if (group.getOrderno() > orderno) {
				orderno = group.getOrderno();
			}
		}
		orderno += 10;
		List<FFunction> ffunctions = dao.findAll(FFunction.class);
		for (FDataobjectfield f : dataobject.getFDataobjectfields()) {
			if (f._isBaseField() && BooleanUtils.isTrue(f.getAllowgroup())) {
				if (f._isDateField()) {
					for (FFunction function : ffunctions) {
						if (function.isDateParamType()
								&& !hasContainerField(dataminingexpandgroups, f, null, null, function)) {
							FDataminingexpandgroup group = new FDataminingexpandgroup();
							group.setFDataobject(dataobject);
							group.setFDataobjectfield(f);
							group.setFFunction(function);
							group.setTitle(f.getFieldtitle() + "(" + function.getTitle() + ")");
							group.setOrderno(orderno);
							group.setFieldahead(null);
							orderno += 10;
							dataminingexpandgroups.add(group);
							dao.saveOrUpdate(group);
							count++;
						}
					}
				} else if (f._isNumberField() && !f._isPercentField()) {
					for (FFunction function : ffunctions) {
						if (function.isNumberParamType()
								&& !hasContainerField(dataminingexpandgroups, f, null, null, function)) {
							FDataminingexpandgroup group = new FDataminingexpandgroup();
							group.setFDataobject(dataobject);
							group.setFDataobjectfield(f);
							group.setFFunction(function);
							group.setTitle(f.getFieldtitle() + "(" + function.getTitle() + ")");
							group.setOrderno(orderno);
							group.setFieldahead(null);
							orderno += 10;
							dataminingexpandgroups.add(group);
							dao.saveOrUpdate(group);
							count++;
						}
					}
				} else if (f._isPercentField()) {
					for (FFunction function : ffunctions) {
						if (function.isPercentType()
								&& !hasContainerField(dataminingexpandgroups, f, null, null, function)) {
							FDataminingexpandgroup group = new FDataminingexpandgroup();
							group.setFDataobject(dataobject);
							group.setFDataobjectfield(f);
							group.setFFunction(function);
							group.setTitle(f.getFieldtitle() + "(" + function.getTitle() + ")");
							group.setOrderno(orderno);
							group.setFieldahead(null);
							orderno += 10;
							dataminingexpandgroups.add(group);
							dao.saveOrUpdate(group);
							count++;
						}
					}
				} else if (!hasContainerField(dataminingexpandgroups, f, null, null, null)) {
					FDataminingexpandgroup group = new FDataminingexpandgroup();
					group.setFDataobject(dataobject);
					group.setFDataobjectfield(f);
					group.setTitle(f.getFieldtitle());
					group.setOrderno(orderno);
					group.setFieldahead(null);
					orderno += 10;
					dataminingexpandgroups.add(group);
					dao.saveOrUpdate(group);
					count++;
				}
			}
		}
		for (String key : baseModule.getAllParents().keySet()) {
			ParentModule pm = baseModule.getAllParents().get(key);
			Object sonModule = pm.getSonModuleHierarchy();
			String ahead = null;
			if (sonModule instanceof ParentModule) {
				ahead = ((ParentModule) sonModule).getFieldahead();
			}
			if (!hasContainerField(dataminingexpandgroups, pm.getModuleField(), ahead, null, null)) {
				FDataminingexpandgroup group = new FDataminingexpandgroup(dataobject, pm.getModuleField(), orderno,
						pm._getNamePath(), ahead, null);
				orderno += 10;
				count++;
				dataminingexpandgroups.add(group);
				dao.saveOrUpdate(group);
			}
			if (pm.getModule()._isCodeLevel()) {
				if (!hasContainerField(dataminingexpandgroups, pm.getModuleField(), ahead, Constants.ALL, null)) {
					FDataminingexpandgroup group = new FDataminingexpandgroup(dataobject, pm.getModuleField(), orderno,
							pm._getNamePath() + "全级别树形", ahead, Constants.ALL);
					orderno += 10;
					count++;
					dataminingexpandgroups.add(group);
					dao.saveOrUpdate(group);
				}
				if (!hasContainerField(dataminingexpandgroups, pm.getModuleField(), ahead, "auto", null)) {
					FDataminingexpandgroup group = new FDataminingexpandgroup(dataobject, pm.getModuleField(), orderno,
							pm._getNamePath() + "自动分级", ahead, "auto");
					orderno += 10;
					count++;
					dataminingexpandgroups.add(group);
					dao.saveOrUpdate(group);
				}
				String[] levels = pm.getModule().getCodelevel().split(Constants.COMMA);
				for (int i = 1; i <= levels.length; i++) {
					if (!hasContainerField(dataminingexpandgroups, pm.getModuleField(), ahead, "" + i, null)) {
						FDataminingexpandgroup group = new FDataminingexpandgroup(dataobject, pm.getModuleField(),
								orderno, pm._getNamePath() + "第" + i + "级", ahead, "" + i);
						orderno += 10;
						count++;
						dataminingexpandgroups.add(group);
						dao.saveOrUpdate(group);
					}
				}
			}
			if (pm.getModule()._isIdPidLevel()) {

				if (!hasContainerField(dataminingexpandgroups, pm.getModuleField(), ahead, Constants.ALL, null)) {
					FDataminingexpandgroup group = new FDataminingexpandgroup(dataobject, pm.getModuleField(), orderno,
							pm._getNamePath() + "全级别树形", ahead, Constants.ALL);
					orderno += 10;
					count++;
					dataminingexpandgroups.add(group);
					dao.saveOrUpdate(group);
				}
				if (!hasContainerField(dataminingexpandgroups, pm.getModuleField(), ahead, "auto", null)) {
					FDataminingexpandgroup group = new FDataminingexpandgroup(dataobject, pm.getModuleField(), orderno,
							pm._getNamePath() + "自动分级", ahead, "auto");
					orderno += 10;
					count++;
					dataminingexpandgroups.add(group);
					dao.saveOrUpdate(group);
				}
				for (int i = 1; i <= Constants.INT_3; i++) {
					if (!hasContainerField(dataminingexpandgroups, pm.getModuleField(), ahead, "" + i, null)) {
						FDataminingexpandgroup group = new FDataminingexpandgroup(dataobject, pm.getModuleField(),
								orderno, pm._getNamePath() + "第" + i + "级", ahead, "" + i);
						orderno += Constants.INT_10;
						count++;
						dataminingexpandgroups.add(group);
						dao.saveOrUpdate(group);
					}
				}
			}
			for (FDataobjectfield f : pm.getModule().getFDataobjectfields()) {
				if (f._isBaseField() && f.getAllowgroup() != null && f.getAllowgroup()) {
					if (f._isDateField()) {
						for (FFunction function : ffunctions) {
							if (function.isDateParamType() && !hasContainerField(dataminingexpandgroups, f,
									pm.getFieldahead(), null, function)) {
								FDataminingexpandgroup group = new FDataminingexpandgroup();
								group.setFDataobject(dataobject);
								group.setFDataobjectfield(f);
								group.setFFunction(function);
								group.setTitle(
										pm._getNamePath() + "--" + f.getFieldtitle() + "(" + function.getTitle() + ")");
								group.setOrderno(orderno);
								group.setFieldahead(pm.getFieldahead());
								orderno += 10;
								dataminingexpandgroups.add(group);
								dao.saveOrUpdate(group);
								count++;
							}
						}
					} else if (f._isNumberField() && !f._isPercentField()) {
						for (FFunction function : ffunctions) {
							if (function.isNumberParamType() && !hasContainerField(dataminingexpandgroups, f,
									pm.getFieldahead(), null, function)) {
								FDataminingexpandgroup group = new FDataminingexpandgroup();
								group.setFDataobject(dataobject);
								group.setFDataobjectfield(f);
								group.setFFunction(function);
								group.setTitle(
										pm._getNamePath() + "--" + f.getFieldtitle() + "(" + function.getTitle() + ")");
								group.setOrderno(orderno);
								group.setFieldahead(pm.getFieldahead());
								orderno += 10;
								dataminingexpandgroups.add(group);
								dao.saveOrUpdate(group);
								count++;
							}
						}
					} else if (f._isPercentField()) {
						for (FFunction function : ffunctions) {
							if (function.isPercentType() && !hasContainerField(dataminingexpandgroups, f,
									pm.getFieldahead(), null, function)) {
								FDataminingexpandgroup group = new FDataminingexpandgroup();
								group.setFDataobject(dataobject);
								group.setFDataobjectfield(f);
								group.setFFunction(function);
								group.setTitle(
										pm._getNamePath() + "--" + f.getFieldtitle() + "(" + function.getTitle() + ")");
								group.setOrderno(orderno);
								group.setFieldahead(pm.getFieldahead());
								orderno += 10;
								dataminingexpandgroups.add(group);
								dao.saveOrUpdate(group);
								count++;
							}
						}
					} else if (!hasContainerField(dataminingexpandgroups, f, pm.getFieldahead(), null, null)) {
						FDataminingexpandgroup group = new FDataminingexpandgroup();
						group.setFDataobject(dataobject);
						group.setFDataobjectfield(f);
						group.setTitle(pm._getNamePath() + "--" + f.getFieldtitle());
						group.setOrderno(orderno);
						group.setFieldahead(pm.getFieldahead());
						orderno += 10;
						dataminingexpandgroups.add(group);
						dao.saveOrUpdate(group);
						count++;
					}
				}
			}
		}
		ActionResult result = new ActionResult();
		result.setSuccess(true);
		result.setTag(count);
		return result;
	}

	public JSONArray getModuleExpandGroupFieldsTree(String moduleName) {
		FDataobject module = DataObjectUtils.getDataObject(moduleName);
		BaseModule baseModule = ModuleOnlyHierarchyGenerate.genModuleHierarchy(module);
		JSONArray array = getParentModule(baseModule, baseModule.getParents());
		JSONArray basefields = getBaseModuleBaseField(baseModule, null);
		if (array.size() > 0 && basefields.size() > 0) {
			JSONObject s = new JSONObject();
			s.put(Constants.TEXT, "-");
			array.add(s);
		}
		array.addAll(basefields);
		adjustGroupFieldsTree(array);
		return array;
	}

	public void adjustGroupFieldsTree(JSONArray array) {
		for (int i = array.size() - 1; i >= 0; i--) {
			JSONObject field = (JSONObject) array.get(i);
			// 有子级
			if (field.containsKey(MENU)) {
				int childfieldcount = checkChildFieldIdCount(field.getJSONArray(MENU));
				// 检查子级里有多少个有fieldid的
				if (!field.containsKey(FIELDID) && childfieldcount == 0) {
					array.remove(field);
				} else if (childfieldcount == 0)
					field.remove(MENU);
				else {
					// 递归检查下级的是否有需要删除的节点
					adjustGroupFieldsTree(field.getJSONArray(MENU));
				}
			} else {
				if (!(field.containsKey(FIELDID) || "-".equals(field.getString(Constants.TEXT)))) {
					array.remove(field);
				}
			}
		}
	}

	public int checkChildFieldIdCount(JSONArray array) {
		int result = 0;
		for (Object obj : array) {
			JSONObject field = (JSONObject) obj;
			if (field.containsKey(FIELDID)) {
				result++;
			}
			if (field.containsKey(MENU)) {
				result += checkChildFieldIdCount(field.getJSONArray(MENU));
			}
		}
		return result;
	}

	private JSONArray getBaseModuleBaseField(BaseModule baseModule, String fieldahead) {
		Set<FDataminingexpandgroup> fieldgroups = baseModule.getModule().getFDataminingexpandgroups();
		// 不带function的一级的field
		JSONArray rootfieldarray = new JSONArray();
		for (FDataminingexpandgroup group : fieldgroups) {
			if (BooleanUtils.isNotTrue(group.getDisabled())) {
				if ((fieldahead == null ? group.getFieldahead() == null : fieldahead.equals(group.getFieldahead()))
						&& group.getFDataobjectfield()._isBaseField()) {
					String fieldid = (fieldahead == null ? "" : fieldahead + "|")
							+ group.getFDataobjectfield().getFieldid();
					JSONObject rootobject = null;
					// 找到所有的不包括函数的字段
					for (int i = 0; i < rootfieldarray.size(); i++) {
						if (rootfieldarray.getJSONObject(i).get(TEMPFIELDID).equals(fieldid)) {
							rootobject = rootfieldarray.getJSONObject(i);
							break;
						}
					}
					if (rootobject == null) {
						rootobject = new JSONObject();
						rootobject.put(Constants.TEXT, group.getFDataobjectfield().getFieldtitle());
						rootobject.put(TEMPFIELDID, fieldid);
						rootobject.put("iconCls", group.getFDataobjectfield().getIconcls());
						if (group.getIconcls() != null) {
							rootobject.put("iconCls", group.getIconcls());
						}
						rootfieldarray.add(rootobject);
					}
				}
			}
		}

		for (int i = 0; i < rootfieldarray.size(); i++) {
			JSONObject rootobject = rootfieldarray.getJSONObject(i);
			String rootfieldid = rootobject.getString(TEMPFIELDID);
			rootobject.remove(TEMPFIELDID);
			for (FDataminingexpandgroup group : fieldgroups) {
				if (BooleanUtils.isNotTrue(group.getDisabled())) {
					String fieldid;
					if ((fieldahead == null ? group.getFieldahead() == null : fieldahead.equals(group.getFieldahead()))
							&& group.getFDataobjectfield()._isBaseField()) {
						fieldid = (group.getFieldahead() != null ? group.getFieldahead() + "|" : "")
								+ group.getFDataobjectfield().getFieldid();
						if (fieldid.equals(rootfieldid)) {
							if (group.getFFunction() != null) {
								fieldid = fieldid + "-" + group.getFFunction().getFunctionid();
								JSONObject subobject = new JSONObject();
								subobject.put(FIELDID, fieldid);
								subobject.put(Constants.TEXT, group.getTitle());
								addFieldGroupInfo(subobject, group);
								JSONArray subarray = rootobject.getJSONArray(MENU);
								if (subarray == null) {
									subarray = new JSONArray();
									rootobject.put(MENU, subarray);
								}
								subarray.add(subobject);
							} else {
								// 这个就是rootfield原字段，没加函数
								rootobject.put(FIELDID, fieldid);
								rootobject.put(Constants.TEXT, group.getFDataobjectfield().getFieldtitle());
								addFieldGroupInfo(rootobject, group);
							}
						}
					}
				}
			}
		}
		return rootfieldarray;
	}

	/**
	 * 取得每一级的直接父级，然后在menu下面加入所有的可分组字段
	 * 
	 * @param parents
	 * @param deep
	 * @param nowdeep
	 * @return
	 */
	private JSONArray getParentModule(BaseModule baseModule, Map<String, ParentModule> parents) {
		JSONArray result = new JSONArray();
		if (parents.size() > 0) {
			for (String parentModuleName : parents.keySet()) {
				ParentModule parentModule = parents.get(parentModuleName);
				FDataobject pobject = parentModule.getModule();
				JSONObject pm = new JSONObject();
				pm.put(Constants.TEXT, parentModule.getModuleField().getFieldtitle());
				pm.put("fieldahead", parentModule.getFieldahead());
				pm.put(Constants.TITLE, parentModule._getNamePath());
				if (pobject.getIconcls() != null) {
					pm.put("iconCls", pobject.getIconcls());
				} else if (pobject.getIconurl() != null) {
					pm.put("icon", pobject.getIconurl());
				}
				JSONArray pms = new JSONArray();
				if (parentModule.getParents().size() > 0) {
					pms.addAll(getParentModule(baseModule, parentModule.getParents()));
				}
				Set<FDataminingexpandgroup> fieldgroups = baseModule.getModule().getFDataminingexpandgroups();
				JSONArray fieldarray = new JSONArray();

				for (FDataminingexpandgroup group : fieldgroups) {
					if ((group.getDisabled() == null || !group.getDisabled())) {
						JSONObject object = new JSONObject();
						if (group.getFDataobjectfield()._isManyToOne() || group.getFDataobjectfield()._isOneToOne()) {
							if (!parentModule.getFieldahead()
									.equals((group.getFieldahead() != null ? group.getFieldahead() + "." : "")
											+ group.getFDataobjectfield().getFieldname())) {
								continue;
							}
							FDataobject p = DataObjectUtils.getDataObject(group.getFDataobjectfield().getFieldtype());
							String fieldid = group.getFDataobjectfield().getFieldname() + "|"
									+ p._getPrimaryKeyField().getFieldid();
							if (group.getLeveltype() != null) {
								fieldid = fieldid + "-" + group.getLeveltype();
							}
							if (group.getFieldahead() != null) {
								fieldid = group.getFieldahead() + "." + fieldid;
							}
							object.put(FIELDID, fieldid);
							addFieldGroupInfo(object, group);
							if ((parentModule.getFieldahead() + "|" + pobject._getPrimaryKeyField().getFieldid())
									.equals(fieldid)) {
								if (group.getIconcls() != null) {
									pm.put("iconCls", group.getIconcls());
								}
								pm.put(FIELDID, fieldid);
							} else {
								fieldarray.add(object);
							}
						}
					}
				}
				fieldarray.addAll(getBaseModuleBaseField(baseModule, parentModule.getFieldahead()));
				if (fieldarray.size() > 0) {
					pms.addAll(fieldarray);
				}
				if (pms.size() > 0) {
					pm.put(MENU, pms);
				}
				result.add(pm);
			}
		}
		return result;
	}

	/**
	 * 返回某块所设置定的所有的可用于分组的字段
	 * 
	 * @param moduleName
	 * @return
	 */
	public List<JSONObject> getModuleExpandGroupFields(String moduleName) {
		List<JSONObject> result = new ArrayList<JSONObject>();
		FDataobject dataobject = DataObjectUtils.getDataObject(moduleName);
		for (FDataminingexpandgroup group : dataobject.getFDataminingexpandgroups()) {
			if (group.getDisabled() == null || !group.getDisabled()) {
				JSONObject object = new JSONObject();
				String fieldid;
				if (group.getFDataobjectfield()._isBaseField()) {
					fieldid = group.getFDataobjectfield().getFieldid();
					if (group.getFieldahead() != null) {
						fieldid = group.getFieldahead() + "|" + group.getFDataobjectfield().getFieldid();
					}
					if (group.getFFunction() != null) {
						fieldid = fieldid + "-" + group.getFFunction().getFunctionid();
					}
				} else {
					FDataobject p = DataObjectUtils.getDataObject(group.getFDataobjectfield().getFieldtype());
					if (p.getIconcls() != null) {
						object.put("iconCls", p.getIconcls());
					}
					fieldid = group.getFDataobjectfield().getFieldname() + "|" + p._getPrimaryKeyField().getFieldid();
					if (group.getLeveltype() != null) {
						fieldid = fieldid + "-" + group.getLeveltype();
					}
					if (group.getFieldahead() != null) {
						fieldid = group.getFieldahead() + "." + fieldid;
					}
				}
				if (group.getIconcls() != null) {
					object.put("iconCls", group.getIconcls());
				}
				object.put(FIELDID, fieldid);
				addFieldGroupInfo(object, group);
				result.add(object);
			}
		}
		return result;
	}

	private void addFieldGroupInfo(JSONObject object, FDataminingexpandgroup group) {
		if (group.getFFunction() != null) {
			object.put(Constants.FUNCTIONID, group.getFFunction().getFunctionid());
		}
		object.put("contextmenuorderno", group.getContextmenuorderno());
		object.put(Constants.TITLE, group.getTitle());
		object.put("tooltip", group.getTooltip());
		if (StringUtils.isNotBlank(group.getIconcls())) {
			object.put("iconCls", group.getIconcls());
		}
		object.put("ontoolbar", group.getOntoolbar());
		object.put("disablecolumngroup", group.getDisablecolumngroup());
		object.put("disablerowgroup", group.getDisablerowgroup());
		object.put("othersetting", group.getOthersetting());
	}

	public Integer[] getFilterCount(String moduleName, String filters, String fields, JSONObject sqlparam) {
		FDataobject object = DataObjectUtils.getDataObject(moduleName);
		JSONArray filterobjects = JSONArray.parseArray(filters);

		SqlGenerate generate = new SqlGenerate();
		generate.setDataobject(object);
		generate.setAddIdField(true);
		generate.setAddNameField(false);
		generate.setAddBaseField(false);
		generate.setAddAllFormScheme(false);
		generate.setAddAllGridScheme(false);
		generate.setSqlparam(sqlparam);
		List<UserDefineFilter> userfilters = new ArrayList<UserDefineFilter>();

		Integer[] result = new Integer[filterobjects.size()];

		for (int i = 0; i < filterobjects.size(); i++) {
			JSONObject jsonobject = filterobjects.getJSONObject(i);
			String type = jsonobject.getString(Constants.TYPE);
			if (type.equals("viewscheme")) {
				generate.setDataobjectview(dao.findById(FDataobjectview.class, jsonobject.getString("schemeid")));
			} else if (type.equals("userfilter")) {
				UserDefineFilter udf = JSONObject.parseObject(jsonobject.getJSONObject("userfilter").toJSONString(),
						UserDefineFilter.class);
				userfilters.add(udf);
				generate.setUserDefineFilters(userfilters);
			} else if (type.equals("navigatefilter")) {
				UserDefineFilter udf = JSONObject.parseObject(jsonobject.getJSONObject("navigatefilter").toJSONString(),
						UserDefineFilter.class);
				userfilters.add(udf);
				generate.setUserDefineFilters(userfilters);
			}
			generate.pretreatment();
			Dao dataSourceDao = Local.getBusinessDao();
			int total = dataSourceDao.selectSQLCount(generate.generateSelectCount());
			result[i] = total;
		}
		return result;
	}

	/**
	 * 四个链接变量
	 */
	private final int A = 0x67452301;
	private final int B = 0xefcdab89;
	private final int C = 0x98badcfe;
	private final int D = 0x10325476;
	/**
	 * ABCD的临时变量
	 */
	private int aTemp, bTemp, cTemp, dTemp;

	/**
	 * 常量ti 公式:floor(abs(sin(i+1))×(2pow32)
	 */
	private final int K[] = { 0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee, 0xf57c0faf, 0x4787c62a, 0xa8304613,
			0xfd469501, 0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be, 0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821,
			0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa, 0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8, 0x21e1cde6,
			0xc33707d6, 0xf4d50d87, 0x455a14ed, 0xa9e3e905, 0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a, 0xfffa3942, 0x8771f681,
			0x6d9d6122, 0xfde5380c, 0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70, 0x289b7ec6, 0xeaa127fa, 0xd4ef3085,
			0x04881d05, 0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665, 0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039,
			0x655b59c3, 0x8f0ccc92, 0xffeff47d, 0x85845dd1, 0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1, 0xf7537e82,
			0xbd3af235, 0x2ad7d2bb, 0xeb86d391 };

	/**
	 * 向左位移数,计算方法未知
	 */
	private final int s[] = { 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 5, 9, 14, 20, 5, 9, 14, 20, 5,
			9, 14, 20, 5, 9, 14, 20, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 6, 10, 15, 21, 6, 10,
			15, 21, 6, 10, 15, 21, 6, 10, 15, 21 };

	/**
	 * 初始化函数
	 */
	private void init() {
		aTemp = A;
		bTemp = B;
		cTemp = C;
		dTemp = D;
	}

	/**
	 * 移动一定位数
	 */
	private int shift(int a, int s) {
		// 右移的时候，高位一定要补零，而不是补充符号位
		return (a << s) | (a >>> (32 - s));
	}

	/**
	 * 主循环
	 * 
	 * @param M
	 */
	private void mainLoop(int m[]) {
		int f, g;
		int a = aTemp;
		int b = bTemp;
		int c = cTemp;
		int d = dTemp;
		for (int i = 0; i < Constants.INT_64; i++) {
			if (i < 16) {
				f = (b & c) | ((~b) & d);
				g = i;
			} else if (i < 32) {
				f = (d & b) | ((~d) & c);
				g = (5 * i + 1) % 16;
			} else if (i < 48) {
				f = b ^ c ^ d;
				g = (3 * i + 5) % 16;
			} else {
				f = c ^ (b | (~d));
				g = (7 * i) % 16;
			}
			int tmp = d;
			d = c;
			c = b;
			b = b + shift(a + f + K[i] + m[g], s[i]);
			a = tmp;
		}
		aTemp = a + aTemp;
		bTemp = b + bTemp;
		cTemp = c + cTemp;
		dTemp = d + dTemp;

	}

	/**
	 * 填充函数 处理后应满足bits≡448(mod512),字节就是bytes≡56（mode64) 填充方式为先加一个0,其它位补零
	 * 最后加上64位的原来长度
	 */
	private int[] add(String str) {
		// 以512位，64个字节为一组
		int num = ((str.length() + 8) / 64) + 1;
		// 64/4=16，所以有16个整数
		int[] strByte = new int[num * Constants.INT_16];
		for (int i = 0; i < num * Constants.INT_16; i++) {
			strByte[i] = 0;
		}
		int i;
		for (i = 0; i < str.length(); i++) {
			// 一个整数存储四个字节，小端序
			strByte[i >> 2] |= str.charAt(i) << ((i % 4) * 8);
		}
		// 尾部添加1
		strByte[i >> 2] |= 0x80 << ((i % 4) * 8);
		/*
		 * 添加原长度，长度指位的长度，所以要乘8，然后是小端序，所以放在倒数第二个,这里长度只用了32位
		 */
		strByte[num * 16 - 2] = str.length() * 8;
		return strByte;
	}

	/**
	 * 调用函数
	 */
	@SuppressWarnings("unused")
	private String getMd5(String source) {
		init();
		int strByte[] = add(source);
		for (int i = 0; i < strByte.length / Constants.INT_16; i++) {
			int num[] = new int[Constants.INT_16];
			for (int j = 0; j < Constants.INT_16; j++) {
				num[j] = strByte[i * Constants.INT_16 + j];
			}
			mainLoop(num);
		}
		return changeHex(aTemp) + changeHex(bTemp) + changeHex(cTemp) + changeHex(dTemp);

	}

	/**
	 * 整数变成16进制字符串
	 */
	private String changeHex(int a) {
		String str = "";
		for (int i = 0; i < Constants.INT_4; i++) {
			str += String.format("%2s", Integer.toHexString(((a >> i * 8) % (1 << 8)) & 0xff)).replace(' ', '0');

		}
		return str;
	}
}
