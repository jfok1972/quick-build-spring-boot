package com.jhopesoft.platform.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.DataDeleteResponseInfo;
import com.jhopesoft.framework.bean.GridParams;
import com.jhopesoft.framework.bean.GroupParameter;
import com.jhopesoft.framework.bean.MapBean;
import com.jhopesoft.framework.bean.PageInfo;
import com.jhopesoft.framework.bean.ResultBean;
import com.jhopesoft.framework.bean.SortParameter;
import com.jhopesoft.framework.bean.TreeNode;
import com.jhopesoft.framework.bean.TreeNodeRecord;
import com.jhopesoft.framework.bean.TreeNodeRecordChecked;
import com.jhopesoft.framework.bean.TreeValueText;
import com.jhopesoft.framework.bean.UserBean;
import com.jhopesoft.framework.bean.ValueText;
import com.jhopesoft.framework.core.annotation.Module;
import com.jhopesoft.framework.core.objectquery.dao.ModuleDataDAO;
import com.jhopesoft.framework.core.objectquery.dao.TreeModuleDataDAO;
import com.jhopesoft.framework.core.objectquery.export.GridColumn;
import com.jhopesoft.framework.core.objectquery.filter.UserDefineFilter;
import com.jhopesoft.framework.core.objectquery.filter.UserNavigateFilter;
import com.jhopesoft.framework.core.objectquery.filter.UserParentFilter;
import com.jhopesoft.framework.core.objectquery.generate.SqlGenerate;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.Dao;
import com.jhopesoft.framework.dao.SqlMapperAdapter;
import com.jhopesoft.framework.dao.entity.attachment.FDataobjectattachment;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectview;
import com.jhopesoft.framework.dao.entity.log.FUseroperatelog;
import com.jhopesoft.framework.dao.entity.system.FOrganization;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.dao.entity.viewsetting.FovGridsortscheme;
import com.jhopesoft.framework.dao.entityinterface.AuditionInterface;
import com.jhopesoft.framework.exception.DataDeleteException;
import com.jhopesoft.framework.exception.DataUpdateException;
import com.jhopesoft.framework.utils.BeanUtils;
import com.jhopesoft.framework.utils.CommonFunction;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.DataobjectFieldConstraintUtils;
import com.jhopesoft.framework.utils.DateUtils;
import com.jhopesoft.framework.utils.KeyExtraProcessor;
import com.jhopesoft.framework.utils.ObjectFunctionUtils;
import com.jhopesoft.framework.utils.ProjectUtils;
import com.jhopesoft.framework.utils.ResultInfoUtils;
import com.jhopesoft.platform.logic.define.LogicInterface;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * 
 * @author ?????? jfok1972@qq.com
 * 
 */

@Service
@SuppressWarnings("unchecked")
public class DataObjectService extends SqlMapperAdapter {

	@Resource
	private ModuleDataDAO moduleDataDAO;

	@Resource
	private TreeModuleDataDAO treeModuleDataDAO;

	@Resource
	private CodeLevelDataobjectService codeLevelDataobjectService;

	@Resource
	private WorkFlowRuntimeService workFlowRuntimeService;

	@Resource
	private DataSourceService dataSourceService;

	@Resource
	private AuditService auditService;

	@Resource
	private DataObjectJdbcService dataObjectJdbcService;

	public PageInfo<Map<String, Object>> fetchDataInner(String moduleName, GridParams pg, List<GridColumn> columns,
			GroupParameter group, List<SortParameter> sorts, List<UserDefineFilter> querys,
			List<UserDefineFilter> userDefineFilters, List<UserNavigateFilter> userNavigateFilters,
			List<UserParentFilter> userParentFilters, FDataobjectview viewscheme, FovGridsortscheme sortscheme,
			JSONObject sqlparam) {
		PageInfo<Map<String, Object>> info = new PageInfo<Map<String, Object>>(pg.getStart(), pg.getLimit());
		FDataobject module = DataObjectUtils.getDataObject(moduleName);
		if (module == null || !ObjectFunctionUtils.allowQuery(module)) {
			return info;
		}
		SqlGenerate generate = new SqlGenerate();
		generate.setDataobject(module);
		generate.setSqlparam(sqlparam);
		generate.setUserDefineFilters(userDefineFilters);
		generate.setSearchFieldQuerys(querys);
		generate.setUserNavigateFilters(userNavigateFilters);
		generate.setUserParentFilters(userParentFilters);
		generate.setDataobjectview(viewscheme);
		generate.setGridsortscheme(sortscheme);
		generate.setSortParameters(sorts);
		generate.setGroup(group);
		// ????????????????????? ???query???userDefineFilter,??????????????????????????????param ?????????querys?????????
		UserDefineFilter queryFilter = null;
		if (userDefineFilters != null) {
			for (UserDefineFilter filter : userDefineFilters) {
				if ("_query_".equals(filter.getProperty())) {
					queryFilter = filter;
					break;
				}
			}
		}
		if (queryFilter != null) {
			generate.getUserDefineFilters().remove(queryFilter);
		}
		generate.pretreatment();

		String sql = generate.generateSelect();
		Dao dao = Local.getBusinessDao();
		String[] fields = generate.getFieldNames();
		if (!pg.isPaging()) {
			pg.setLimit(Integer.MAX_VALUE);
		}
		int total = dao.selectSQLCount(generate.generateSelectCount());
		if (total == 0) {
			info.setData(new ArrayList<>());
			if (BooleanUtils.isTrue(module.getHasremotesummary())) {
				generate.generateSelectRemoteTotal();
				JSONObject r = new JSONObject();
				for (String s : generate.getTotalFieldNames()) {
					r.put(s, 0);
				}
				info.setRemoteRoot(r);
			}
			return info;
		} else {
			if (pg.getStart() + 1 > total) {
				pg.setStart(0);
			}
			PageInfo<Map<String, Object>> result = dao.executeSQLQueryPage(sql, fields, pg.getStart(), pg.getLimit(),
					total, new Object[] {});
			// ????????????????????????????????????
			if (BooleanUtils.isTrue(module.getHasremotesummary())) {
				String totalsql = generate.generateSelectRemoteTotal();
				String[] totalFields = generate.getTotalFieldNames();
				PageInfo<Map<String, Object>> totalResult = dao.executeSQLQueryPage(totalsql, totalFields, 0, 1, 1,
						new Object[] {});
				result.setRemoteRoot(new JSONObject(totalResult.getData().get(0)));
			}
			Object logic = Local.getLogicBean(module.getObjectname() + "Logic");
			if (logic != null && logic instanceof LogicInterface) {
				result = ((LogicInterface<Object>) logic).afterFatchData(result);
			}
			if (Local.getRequest().getHeader(Constants.ANTD) != null) {
				// ????????????????????????????????????
				if (BooleanUtils.isTrue(module.getHasattachment())) {
					CommonUtils.changeAttachmentsToObject(result.getData());
				}
				if (BooleanUtils.isTrue(module.getHasapprove())) {
					CommonUtils.changeCompleteTaskInfoToObject(result.getData());
				}
				CommonUtils.addRecnoToRecord(result.getData(), result.getStart());
				CommonUtils.changeManyToManyToObject(result.getData(), module);

			}
			return result;
		}
	}

	public JSONObject fetchTreeDataInner(String moduleName, GridParams pg, List<GridColumn> columns,
			List<SortParameter> sort, List<UserDefineFilter> query, List<UserDefineFilter> filter,
			List<UserNavigateFilter> navigates, List<UserParentFilter> userParentFilters, FDataobjectview viewscheme) {
		return treeModuleDataDAO.getTreeModuleData(moduleName, filter, navigates, userParentFilters, sort);
	}

	/**
	 * ????????????????????????
	 * 
	 * @param objectname ????????????
	 * @param id         ??????id
	 * @return
	 */
	public ResultBean fetchInfo(String objectname, String id) {
		return new ResultBean(true, getObjectRecordMap(objectname, id));
	}

	/**
	 * ???????????????????????????????????????
	 * 
	 * @param objectname ????????????
	 * @param id         ??????id
	 * @return
	 */
	public Map<String, Object> getObjectRecordMap(String objectname, String id) {
		FDataobject dataObject = DataObjectUtils.getDataObject(objectname);
		if (!ObjectFunctionUtils.allowQuery(dataObject)) {
			return null;
		}
		SqlGenerate generate = new SqlGenerate();
		generate.setDataobject(dataObject);
		generate.setIdvalue(id);
		String sql = generate.pretreatment().generateSelect();
		String[] fields = generate.getFieldNames();
		Dao dao = Local.getBusinessDao();
		Map<String, Object> map = dao.executeSQLQueryFirst(sql, fields, new Object[] {});
		ProjectUtils.invokeLogic(Module.class, Module.Type.queryInfo, dataObject.getObjectname(), this,
				new MapBean("map", map));
		if (Local.getRequest().getHeader(Constants.ANTD) != null) {
			if (map != null) {
				CommonUtils.attachmentStrToObject(map);
				CommonUtils.completeTaskInfoToObject(map);
				CommonUtils.manyToManyFieldToObject(map, dataObject._getManyToManyFieldNames());
			}
		}
		return map;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param objectname   ?????????
	 * @param parentfilter ?????????????????????
	 * @param navigates    ????????????????????????
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public ResultBean getRecordNewDefault(String objectname, String parentfilter, String navigates) throws Exception {
		ResultBean result = new ResultBean();
		FDataobject dataObject = DataObjectUtils.getDataObject(objectname);
		objectname = dataObject.getObjectname();
		// ???????????????
		List<UserParentFilter> userParentFilters = UserParentFilter.changeToParentFilters(parentfilter, objectname);
		// ????????????
		List<UserNavigateFilter> navigateFilters = UserNavigateFilter.changeToNavigateFilters(navigates);

		Map<String, Object> map = new HashMap<String, Object>(0);
		setDefaultData(Constants.DEFAULT, map);
		// ?????????????????????????????????
		Iterator<String> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (dataObject._getModuleFieldByFieldName(key) == null) {
				iter.remove();
			}
		}
		Object logic = Local.getLogicBean(dataObject.getObjectname() + "Logic");
		if (logic != null && logic instanceof LogicInterface) {
			Map<String, Object> defaults = ((LogicInterface) logic).getNewDefultValue(userParentFilters,
					navigateFilters);
			if (defaults != null) {
				map.putAll(defaults);
			}
			result.setData(map);
			result.setResultInfo(ResultInfoUtils.getResultInfoMessage());
			return result;
		}
		ProjectUtils.invokeLogic(Module.class, Module.Type.newDefaultData, objectname, this, new MapBean("map", map));
		result.setData(map);
		result.setResultInfo(ResultInfoUtils.getResultInfoMessage());
		return result;
	}

	/**
	 * ?????????????????????
	 * 
	 * @param objectname ??????????????????
	 * @param inserted   ?????????????????????
	 * @param oldid      ????????????
	 * @param opertype   ???????????? add/edit
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public ResultBean saveOrUpdate(String objectname, String inserted, String oldid, String opertype)
			throws ClassNotFoundException, OgnlException, IllegalAccessException, InvocationTargetException {
		MapBean params = new MapBean();
		FDataobject dataObject = DataObjectUtils.getDataObject(objectname);
		objectname = dataObject.getObjectname();
		ResultBean result = new ResultBean();
		inserted = adjustNullParentId(inserted);
		Object logic = Local.getLogicBean(dataObject.getObjectname() + "Logic");
		// ??????????????????bean????????????jdbcTemplate ?????????????????????????????????????????????????????????
		if (StringUtils.isBlank(dataObject.getClassname())) {
			if (Constants.NEW.equals(opertype)) {
				return dataObjectJdbcService.save(dataObject, inserted);
			} else if (opertype.equals(Constants.EDIT)) {
				return dataObjectJdbcService.update(dataObject, inserted);
			} else {
				throw new RuntimeException("??????bean?????????????????????????????????!");
			}
		}
		Class<?> clazz = Class.forName(dataObject.getClassname());
		params.add("inserted", inserted).add("oldid", oldid).add("opertype", opertype);
		ProjectUtils.invokeLogic(Module.class, Module.Type.saveOrUpdate, objectname, this, params);
		inserted = String.valueOf(params.get("inserted"));
		Map<String, Object> map = JSON.parseObject(inserted);
		// ?????????????????????bean
		Object bean = JSON.parseObject(inserted, clazz, new KeyExtraProcessor());
		if (Constants.NEW.equals(opertype)) {
			if (!ObjectFunctionUtils.allowNew(dataObject)) {
				throw new DataUpdateException("?????????????????????????????????????????????!");
			}
			setDefaultData(opertype, bean);
			if (dataObject._isCodeLevel()) {
				codeLevelDataobjectService.addCodeLevelModuleKey(dataObject,
						Ognl.getValue(dataObject._getPrimaryKeyField().getFieldname(), bean).toString(), clazz);
			}
			// ??????????????????
			DataobjectFieldConstraintUtils.moduleFieldConstraintValid(dataObject, bean);
			if (logic != null && logic instanceof LogicInterface) {
				((LogicInterface<Object>) logic).beforeInsert(bean);
				dao.save(bean);
				((LogicInterface<Object>) logic).afterInsert(bean);
			} else {
				ProjectUtils.invokeLogic(Module.class, Module.Type.newDataBefore, objectname, this,
						new MapBean("bean", bean));
				dao.save(bean);
				ProjectUtils.invokeLogic(Module.class, Module.Type.newDataAfter, objectname, this,
						new MapBean("bean", bean));
			}
			// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
			// ?????????save???????????????????????????????????????
			if (BooleanUtils.isTrue(dataObject.getHasaudit())) {
				AuditionInterface audition = (AuditionInterface) bean;
				// ???????????????logic?????????????????????????????????????????????
				if (audition.getAuditingUserid() == null) {
					// ??????manytoone??????bean???????????????????????????????????????id?????????????????????????????????
					Serializable id = dao.getIdentifier(bean);
					dao.evict(bean);
					Object entitybean = dao.findById(clazz, id);
					audition = (AuditionInterface) entitybean;
					String canAuditUserid = auditService.getCanAuditUserid(dataObject, entitybean);
					if (canAuditUserid != null) {
						audition.setAuditingUserid(canAuditUserid);
						audition.setAuditingName(dao.findById(FUser.class, canAuditUserid).getUsername());
					} else {
						audition.setAuditingUserid(Local.getUserid());
						audition.setAuditingName(Local.getUsername());
					}
					dao.update(audition);
					ResultInfoUtils.addInfoMessage(
							"?????????" + dataObject.getTitle() + "?????????????????? " + audition.getAuditingName() + " ???????????????");
				}
			}
			Serializable id = dao.getIdentifier(bean);
			if (BooleanUtils.isTrue(dataObject.getAllowupdatemanytomany())) {
				try {
					setManyToManyDetail(dataObject, id, map);
				} catch (Throwable e) {
					throw new DataUpdateException("?????????????????????????????????" + CommonUtils.getThrowableOriginalMessage(e));
				}
			}
			result = fetchInfo(objectname, id.toString());
			saveOperateLog(dataObject, id.toString(), getRecordNameValue(dataObject, bean), Constants.NEW, inserted);
		} else if (opertype.equals(Constants.EDIT) || opertype.equals(Constants.APPROVEEDIT)) {
			boolean cond = (opertype.equals(Constants.EDIT) && !ObjectFunctionUtils.allowEdit(dataObject))
					&& !dataObject.getObjectname().equals(FDataobjectattachment.class.getSimpleName());
			if (cond) {
				// ????????????????????????????????????????????????
				throw new DataUpdateException("?????????????????????????????????????????????!");
			}
			Serializable keyName = dao.getIdentifierPropertyName(clazz);
			Serializable keyValue = dao.getIdentifier(bean);
			if (!CommonUtils.isEmpty(oldid)) {
				if (dataObject._isCodeLevel()) {
					codeLevelDataobjectService.replaceCodeLevelModuleKey(dataObject, oldid.toString(),
							keyValue.toString(), clazz);
				}
				String upsql = "update " + clazz.getSimpleName() + " set " + keyName + " = ?0 where " + keyName
						+ " = ?1 ";
				dao.executeUpdate(upsql, keyValue, oldid);
				// ????????????????????????
				if (BooleanUtils.isTrue(dataObject.getHasattachment())) {
					String attachmentupsql = "update " + FDataobjectattachment.class.getSimpleName()
							+ " set idvalue = ?0 where " + "objectid = ?1 and idvalue = ?2 ";
					dao.executeUpdate(attachmentupsql, keyValue, dataObject.getObjectid(), oldid);
				}
			}
			// ?????????????????????
			map.remove(keyName);
			// ??????????????????????????????????????????????????????????????????
			String[] upfield = map.keySet().toArray(new String[] {});
			// if (upfield.length == 0) throw new DaoException("?????????????????????!");
			for (int i = 0; i < upfield.length; i++) {
				// manytoone ????????????????????????????????????
				boolean isentity = false;
				if (upfield[i].indexOf('.') > 0) {
					isentity = true;
					upfield[i] = upfield[i].substring(0, upfield[i].indexOf('.'));
				}
				if (upfield[i].indexOf("_") != -1) {
					upfield[i] = CommonUtils.underlineToCamelhump(upfield[i]);
					if (isentity) {
						upfield[i] = CommonUtils.firstCharacterUpperCase(upfield[i]);
					}
				}
				upfield[i] = upfield[i].toLowerCase();
			}
			Object oldentitybean = dao.findById(clazz, keyValue);
			dao.evict(oldentitybean);
			Object entitybean = dao.findById(clazz, keyValue);
			if (upfield.length > 0) {
				// ???????????????bean
				BeanUtils.copyProperties(entitybean, bean, upfield);
			}
			setDefaultData(opertype, entitybean);
			// ??????????????????
			DataobjectFieldConstraintUtils.moduleFieldConstraintValid(dataObject, entitybean);

			if (logic != null && logic instanceof LogicInterface) {
				((LogicInterface<Object>) logic).beforeUpdate(Constants.EDIT, entitybean, oldentitybean);
				dao.update(entitybean);
				((LogicInterface<Object>) logic).afterUpdate(Constants.EDIT, entitybean, oldentitybean);
			} else {
				params.clear();
				params.add("bean", bean).add("map", map).add("entitybean", entitybean);
				ProjectUtils.invokeLogic(Module.class, Module.Type.updateDataBefore, objectname, this, params);
				// ??????????????????????????????
				dao.update(entitybean);
				params.clear();
				params.add("bean", entitybean);
				ProjectUtils.invokeLogic(Module.class, Module.Type.updateDataAfter, objectname, this, params);
			}
			if (BooleanUtils.isTrue(dataObject.getAllowupdatemanytomany())) {
				try {
					setManyToManyDetail(dataObject, keyValue, map);
				} catch (Throwable e) {
					e.printStackTrace();
					throw new DataUpdateException("?????????????????????????????????" + CommonUtils.getThrowableOriginalMessage(e));
				}
			}
			result = fetchInfo(objectname, keyValue.toString());
			saveOperateLog(dataObject, keyValue.toString(), getRecordNameValue(dataObject, entitybean), Constants.EDIT,
					inserted);
		}
		result.setResultInfo(ResultInfoUtils.getResultInfoMessage());
		return result;
	}

	/**
	 * ???FPersonnel.personnelid:null???????????? FPersonnel:null
	 * 
	 * @param inserted
	 * @return
	 */
	private String adjustNullParentId(String inserted) {
		JSONObject object = JSONObject.parseObject(inserted);
		List<String> keys = new ArrayList<String>();
		for (String name : object.keySet()) {
			if (name.indexOf('.') != -1 && object.get(name) == null) {
				keys.add(name);
			}
		}
		for (String name : keys) {
			String[] sep = name.split("\\.");
			object.put(sep[0], object.get(name));
			object.remove(name);
		}
		return JSONObject.toJSONString(object, SerializerFeature.WriteMapNullValue);
	}

	public String getRecordNameValue(FDataobject module, Object record) {
		String result = "";
		try {
			result = (module.getNamefield() != null && module.getNamefield().length() > 0)
					? Ognl.getValue(module.getNamefield(), record).toString()
					: "?????????";
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * ??????????????????
	 * 
	 * @param type ????????????=default???new???edit
	 * @param bean ????????????
	 */
	private void setDefaultData(String type, Object bean) {
		try {
			UserBean userbean = Local.getUserBean();
			if (userbean == null) {
				return;
			}
			if ((bean instanceof Map) && (Constants.DEFAULT.equals(type))) {
				Map<String, Object> map = (Map<String, Object>) bean;
				map.put(Constants.CREATER, userbean.getUsername());
				map.put(Constants.CREATEDATE, DateUtils.getTimestamp());
				map.put(Constants.CREATE_DATE, DateUtils.getTimestamp());
			} else {
				Field[] fields = bean.getClass().getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					Field field = fields[i];
					field.setAccessible(true);
					String propertyName = field.getName();
					Class<?> propertyType = field.getType();
					Object obj = field.get(bean);
					if (Constants.NEW.equals(type)) {
						if (Constants.CREATER.equals(propertyName) && propertyType.equals(String.class)) {
							// ??????creater??????????????????????????????????????????userid
							if (field.get(bean) == null || Local.getUsername().equals(field.get(bean))) {
								field.set(bean, userbean.getUserid());
							}
						} else if (Constants.CREATEDATE.equals(propertyName)
								&& Date.class.isAssignableFrom(propertyType)) {
							// ??????????????????????????????
							field.set(bean, DateUtils.getTimestamp());
						} else if (!CommonUtils.isEmpty(obj)) {
							// ???????????????????????????????????????????????????
							continue;
						} else if (Constants.COMPANYID.equals(propertyName) && propertyType.equals(String.class)) {
							field.set(bean, Local.getCompanyid());
						}
					}
					if (Constants.EDIT.equals(type)) {
						if (Constants.LASTMODIFIER.equalsIgnoreCase(propertyName)
								&& propertyType.equals(String.class)) {
							// ??????????????????
							field.set(bean, userbean.getUserid());
						} else if (Constants.LASTMODIFYDATE.equalsIgnoreCase(propertyName)
								&& Date.class.isAssignableFrom(propertyType)) {
							// ??????????????????
							field.set(bean, DateUtils.getTimestamp());
						}
					}
				}
			}
		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

	public DataDeleteResponseInfo remove(String objectname, String removed) {

		DataDeleteResponseInfo result = new DataDeleteResponseInfo();

		FDataobject dataObject = DataObjectUtils.getDataObject(objectname);

		if (!ObjectFunctionUtils.allowDelete(dataObject)
				&& !dataObject.getObjectname().equals(FDataobjectattachment.class.getSimpleName())) {
			// ????????????????????????????????????????????????
			throw new DataDeleteException("?????????????????????????????????????????????!");
		}

		// ?????????????????????????????????????????????????????????????????????????????????????????????
		if (BooleanUtils.isTrue(dataObject.getHasapprove())) {
			if (workFlowRuntimeService.isProcessInstanceStart(dataObject.getObjectname(), removed)) {
				throw new DataDeleteException("???????????????????????????????????????????????????????????????");
			}
		}

		// ????????id ?????????codelevel,???????????????????????????????????????????????????
		if (dataObject.getCodelevel() != null && dataObject.getCodelevel().length() > 0) {
			codeLevelDataobjectService.deleteCodeLevelModuleKey(dataObject, removed);
		}

		Class<?> beanClass = null;
		if (dataObject.getClassname() == null) {
			return dataObjectJdbcService.delete(dataObject, removed);
			// throw new DataDeleteException(dataObject.getTitle() + "?????????bean?????????????????????");
		}
		try {
			beanClass = Class.forName(dataObject.getClassname());
		} catch (ClassNotFoundException e) {
			throw new DataDeleteException(dataObject.getTitle() + "?????????bean???????????????");
		}

		Object record = dao.findById(beanClass, removed);
		if (record == null) {
			throw new DataDeleteException("?????????????????????????????????????????????");
		}

		// ????????????????????????????????????
		Object logic = Local.getLogicBean(dataObject.getObjectname() + "Logic");
		if (logic != null && logic instanceof LogicInterface) {
			((LogicInterface<Object>) logic).beforeDelete(record);
			if (BooleanUtils.isTrue(dataObject.getHasattachment())) {
				// ????????????????????????????????????????????????beforeDelete?????????????????????
				List<FDataobjectattachment> attachments = dao.findByProperty(FDataobjectattachment.class,
						Constants.OBJECTID, dataObject.getObjectid(), Constants.IDVALUE, removed);
				if (attachments.size() > 0) {
					throw new DataDeleteException("??????????????????????????????????????????????????????");
				}
			}
			dao.delete(record);
			((LogicInterface<Object>) logic).afterDelete(record);
		} else {
			ProjectUtils.invokeLogic(Module.class, Module.Type.deleteDataBefore, objectname, this,
					new MapBean("bean", record));
			if (BooleanUtils.isTrue(dataObject.getHasattachment())) {
				List<FDataobjectattachment> attachments = dao.findByProperty(FDataobjectattachment.class,
						Constants.OBJECTID, dataObject.getObjectid(), Constants.IDVALUE, removed);
				if (attachments.size() > 0) {
					throw new DataDeleteException("??????????????????????????????????????????????????????");
				}
			}
			dao.delete(record);
			ProjectUtils.invokeLogic(Module.class, Module.Type.deleteDataAfter, objectname, this,
					new MapBean("bean", record));
		}

		saveOperateLog(dataObject, removed, getRecordNameValue(dataObject, record), Constants.DELETE, null);
		result.setResultCode(0);
		dao.clear();
		return result;

	}

	public List<ValueText> fetchModuleComboData(String moduleName, boolean mainlinkage, String query,
			List<UserDefineFilter> filter) {
		FDataobject module = DataObjectUtils.getDataObject(moduleName);
		if (!ObjectFunctionUtils.allowQuery(module)) {
			return null;
		}
		List<UserDefineFilter> userDefineFilters = new ArrayList<UserDefineFilter>();
		List<ValueText> keys = null;
		if (query != null && query.length() > 0) {
			UserDefineFilter keyfilter = new UserDefineFilter();
			keyfilter.setProperty(module._getPrimaryKeyField().getFieldname());
			keyfilter.setOperator("=");
			keyfilter.setValue(query);
			userDefineFilters.add(keyfilter);
			// ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
			keys = moduleDataDAO.getRecordWithIdAndName(moduleName, userDefineFilters, null, mainlinkage, false);

			userDefineFilters.clear();
			UserDefineFilter namelikefilter = new UserDefineFilter();
			namelikefilter.setProperty(module._getNameField().getFieldname());
			namelikefilter.setOperator(Constants.LIKE);
			namelikefilter.setValue(query);
			userDefineFilters.add(namelikefilter);
			// ????????????????????? ????????????query ???????????????????????????????????????????????????????????????????????????????????????
		}
		if (filter != null && filter.size() > 0) {
			userDefineFilters.addAll(filter);
		}
		List<ValueText> names = moduleDataDAO.getRecordWithIdAndName(moduleName, userDefineFilters, null, mainlinkage,
				false);
		if (keys != null && keys.size() > 0) {
			// ?????????????????????????????????????????????????????????????????????????????????????????????????????????
			for (int i = names.size() - 1; i >= 0; i--) {
				if (names.get(i).getValue().equals(keys.get(0).getValue())) {
					names.remove(i);
					break;
				}
			}
			names.add(0, keys.get(0));
		}
		// ????????????????????????????????????????????????
		if (mainlinkage && moduleName.equals(FOrganization.class.getSimpleName())) {
			names.forEach(name -> {
				name.setText(name.getValue() + " " + name.getText());
			});
		}
		return names;
	}

	public List<TreeValueText> getModuleWithTreeData(String moduleName, boolean allowParentValue, Object object,
			boolean addCodeToText, boolean shortName) {
		return treeModuleDataDAO.getRecordWithTreeData(moduleName, allowParentValue, addCodeToText, null, null,
				shortName);
	}

	/**
	 * 
	 * @param request
	 * @param moduleName           ??????????????????
	 * @param id                   ????????????id
	 * @param manyToManyModuleName manyToMany???????????????
	 * @param linkModuleName       ??????????????????
	 * @return ????????????manyToManyModuleName?????????????????????????????????????????????manyToMany??????checked??????true
	 */
	public List<TreeNodeRecordChecked> getManyToManyDetail(HttpServletRequest request, String moduleName, String id,
			String manyToManyModuleName, String linkModuleName) {
		FDataobject module = DataObjectUtils.getDataObject(moduleName);
		FDataobject manyToManyModule = DataObjectUtils.getDataObject(manyToManyModuleName);
		FDataobject linkedModule = DataObjectUtils.getDataObject(linkModuleName);
		List<TreeNodeRecord> result = new ArrayList<TreeNodeRecord>();
		// ????????????manyToManyModuleName?????????????????????????????????????????????
		List<ValueText> allTreeItems = moduleDataDAO.getRecordWithIdAndName(manyToManyModuleName, null, null, false,
				false);
		for (ValueText vt : allTreeItems) {
			TreeNodeRecordChecked record = new TreeNodeRecordChecked();
			record.setFieldvalue(vt.getValue());
			record.setText(vt.getText());
			record.setLeaf(true);
			result.add(record);
		}
		String fn = null;
		for (FDataobjectfield field : linkedModule.getFDataobjectfields()) {
			if (field._isManyToOne() && field.getFieldtype().equals(moduleName)) {
				fn = field.getFieldname();
				break;
			}
		}
		UserDefineFilter filter = new UserDefineFilter();
		filter.setProperty(fn + "." + module.getPrimarykey());
		filter.setOperator("eq");
		filter.setValue(id);
		JSONArray dataArray = moduleDataDAO.getRecords(linkModuleName, filter);
		String manytomanyfn = null;
		for (FDataobjectfield field : linkedModule.getFDataobjectfields()) {
			if (field._isManyToOne() && field.getFieldtype().equals(manyToManyModuleName)) {
				manytomanyfn = field.getFieldname();
				break;
			}
		}
		// ??????TreeNodeRecordChecked,?????????checked??????
		for (int i = 0; i < dataArray.size(); i++) {
			String manytomanyid = dataArray.getJSONObject(i)
					.getString(manytomanyfn + "." + manyToManyModule.getPrimarykey());
			for (TreeNodeRecord record : result) {
				if (record.getFieldvalue().equals(manytomanyid)) {
					((TreeNodeRecordChecked) record).setChecked(true);
				}
			}
		}
		List<TreeNodeRecordChecked> root = new ArrayList<TreeNodeRecordChecked>();
		TreeNodeRecordChecked rootrecord = new TreeNodeRecordChecked();
		rootrecord.setText(manyToManyModule.getTitle());
		rootrecord.setChildren(result);
		rootrecord.setExpanded(true);
		root.add(rootrecord);
		return root;
	}

	/**
	 * @param request
	 * @param moduleName           ??????????????????
	 * @param id                   ????????????id
	 * @param manyToManyModuleName manyToMany???????????????
	 * @param linkModuleName       ??????????????????
	 * @return ????????????manyToManyModuleName??? id ??????
	 */
	public List<String> getManyToManyDetailIds(HttpServletRequest request, String moduleName, String id,
			String manyToManyModuleName, String linkModuleName) {
		FDataobject module = DataObjectUtils.getDataObject(moduleName);
		FDataobject manyToManyModule = DataObjectUtils.getDataObject(manyToManyModuleName);
		FDataobject linkedModule = DataObjectUtils.getDataObject(linkModuleName);
		List<String> result = new ArrayList<String>();
		String fn = null;
		for (FDataobjectfield field : linkedModule.getFDataobjectfields()) {
			if (field._isManyToOne() && field.getFieldtype().equals(moduleName)) {
				fn = field.getFieldname();
				break;
			}
		}
		UserDefineFilter filter = new UserDefineFilter();
		filter.setProperty(fn + "." + module.getPrimarykey());
		filter.setOperator("eq");
		filter.setValue(id);
		JSONArray dataArray = moduleDataDAO.getRecords(linkModuleName, filter);
		String manytomanyfn = null;
		for (FDataobjectfield field : linkedModule.getFDataobjectfields()) {
			if (field._isManyToOne() && field.getFieldtype().equals(manyToManyModuleName)) {
				manytomanyfn = field.getFieldname();
				break;
			}
		}
		// ??????TreeNodeRecordChecked,?????????checked??????
		for (int i = 0; i < dataArray.size(); i++) {
			String manytomanyid = dataArray.getJSONObject(i)
					.getString(manytomanyfn + "." + manyToManyModule.getPrimarykey());
			result.add(manytomanyid);
		}
		return result;
	}

	/**
	 * ??????manytomany?????????????????????form??????????????????manytomany???????????????????????????
	 * 
	 * @param moduleName
	 * @param id
	 * @param manytomanyfieldname
	 * @param value
	 * @return
	 * @throws Throwable
	 */
	public ActionResult setManyToManyDetail(FDataobject dataobject, Serializable id, Map<String, Object> updated)
			throws Throwable {
		String[] upfield = updated.keySet().toArray(new String[] {});
		for (String fieldname : upfield) {
			FDataobjectfield field = dataobject._getModuleFieldByFieldName(fieldname);
			if (field != null && field._isManyToMany()) {
				String linkModuleName = field.getJointable();
				String manyToManyModuleName = field._getManyToManyObjectName();
				String[] array;
				if (updated.get(fieldname) != null) {
					String updateStr = updated.get(fieldname).toString();
					if (updateStr.indexOf('[') != -1) {
						// ??????????????????['a','b']???????????????????????? a,b
						JSONArray jsonarray = JSONArray.parseArray(updateStr);
						array = new String[jsonarray.size()];
						for (int i = 0; i < jsonarray.size(); i++) {
							array[i] = jsonarray.get(i).toString();
						}
					} else {
						array = updateStr.split(Constants.COMMA);
					}
				} else {
					array = new String[0];
				}
				setManyToManyDetail(dataobject.getObjectname(), id.toString(), manyToManyModuleName, linkModuleName,
						array);
			}
		}
		return new ActionResult();
	}

	/**
	 * 
	 * @param moduleName
	 * @param id                   ???????????????????????????
	 * @param manyToManyModuleName
	 * @param linkModuleName
	 * @param selected
	 * @return
	 * @throws ClassNotFoundException
	 * @throws OgnlException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ActionResult setManyToManyDetail(String moduleName, String id, String manyToManyModuleName,
			String linkModuleName, String[] selected)
			throws ClassNotFoundException, OgnlException, IllegalAccessException, InvocationTargetException {

		FDataobject module = DataObjectUtils.getDataObject(moduleName);
		FDataobject manyToManyModule = DataObjectUtils.getDataObject(manyToManyModuleName);
		FDataobject linkedModule = DataObjectUtils.getDataObject(linkModuleName);
		// ???linkModuleName???????????????id???manyToMany??????,????????????????????????

		String fn = null;
		for (FDataobjectfield field : linkedModule.getFDataobjectfields()) {
			if (field._isManyToOne() && field.getFieldtype().equals(moduleName)) {
				fn = field.getFieldname();
				break;
			}
		}
		UserDefineFilter filter = new UserDefineFilter();
		filter.setProperty(fn + "." + module.getPrimarykey());
		filter.setOperator("eq");
		filter.setValue(id);
		JSONArray dataArray = moduleDataDAO.getRecords(linkModuleName, filter);

		String manytomanyfn = null;
		for (FDataobjectfield field : linkedModule.getFDataobjectfields()) {
			if (field._isManyToOne() && field.getFieldtype().equals(manyToManyModuleName)) {
				manytomanyfn = field.getFieldname();
				break;
			}
		}
		// ????????????????????????selected???????????????????????????????????????
		for (int i = 0; i < dataArray.size(); i++) {
			String manytomanyid = dataArray.getJSONObject(i)
					.getString(manytomanyfn + "." + manyToManyModule.getPrimarykey());
			boolean isfound = false;
			for (String selectedid : selected) {
				if (manytomanyid.equals(selectedid)) {
					isfound = true;
					break;
				}
			}
			if (!isfound) {
				// ??????????????????manyTomany???????????????Service???remove???????????????????????????????????????????????????
				// ?????????????????????
				remove(linkModuleName, dataArray.getJSONObject(i).getString(linkedModule.getPrimarykey()));
			}
		}
		// ???????????????????????????selected???????????????????????????????????????
		for (String selectedid : selected) {
			if (selectedid.length() > 0) {
				boolean isfound = false;
				for (int i = 0; i < dataArray.size(); i++) {
					String manytomanyid = dataArray.getJSONObject(i)
							.getString(manytomanyfn + "." + manyToManyModule.getPrimarykey());
					if (manytomanyid.equals(selectedid)) {
						isfound = true;
						break;
					}
				}
				if (!isfound) {
					JSONObject object = new JSONObject();
					object.put(manytomanyfn + "." + manyToManyModule.getPrimarykey(), selectedid);
					object.put(fn + "." + module.getPrimarykey(), id);
					// ??????????????????manyTomany,????????????Service???add ,???????????????????????????????????????????????????
					// ?????????????????????
					saveOrUpdate(linkModuleName, object.toString(), null, Constants.NEW);
				}
			}
		}
		ActionResult result = new ActionResult();
		return result;
	}

	public ActionResult updateOrderno(String objectid, String[] ids, Boolean addparent, Integer startnumber,
			Integer stepnumber, Integer parentnumber) throws ClassNotFoundException, OgnlException {
		FDataobject dataObject = DataObjectUtils.getDataObject(objectid);
		if (!ObjectFunctionUtils.allowEdit(dataObject)) {
			return null;
		}
		Class<?> clazz = Class.forName(dataObject.getClassname());
		List<ValueText> valuetexts = new ArrayList<ValueText>();
		int maxv = 2000000000;
		if (BooleanUtils.isTrue(addparent) && parentnumber != null && parentnumber * Constants.INT_100 < maxv) {
			startnumber += parentnumber * Constants.INT_100;
		}
		for (String id : ids) {
			Object bean = dao.findById(clazz, id);
			Ognl.setValue(dataObject.getOrderfield(), bean, startnumber);
			valuetexts.add(new ValueText(id, startnumber + ""));
			startnumber += stepnumber;
		}
		ActionResult result = new ActionResult();
		result.setMsg(valuetexts);
		result.setTag(dataObject.getOrderfield());
		return result;
	}

	/**
	 * ???????????????orderno??????grid???orderno
	 * 
	 * @param objectid
	 * @param ids
	 * @return
	 * @throws ClassNotFoundException
	 * @throws OgnlException
	 */
	public ActionResult updateGridPageOrderno(String objectid, String[] ids)
			throws ClassNotFoundException, OgnlException {
		FDataobject dataObject = DataObjectUtils.getDataObject(objectid);
		if (!ObjectFunctionUtils.allowEdit(dataObject)) {
			return null;
		}
		Class<?> clazz = Class.forName(dataObject.getClassname());
		int nowrecordno[] = new int[ids.length];
		Object[] beans = new Object[ids.length];
		int i = 0;
		for (String id : ids) {
			Object bean = dao.findById(clazz, id);
			beans[i] = bean;
			Object v = Ognl.getValue(dataObject.getOrderfield(), bean);
			if (v == null) {
				nowrecordno[i++] = 0;
			} else {
				nowrecordno[i++] = Integer.parseInt(v.toString());
			}
		}
		// ??? nowrecordno ????????????
		Arrays.sort(nowrecordno);
		for (i = 0; i < beans.length; i++) {
			Ognl.setValue(dataObject.getOrderfield(), beans[i], nowrecordno[i]);
		}
		ActionResult result = new ActionResult();
		result.setMsg(nowrecordno);
		result.setTag(dataObject.getOrderfield());
		return result;
	}

	/**
	 * ???10??????????????????????????? grid???orderno
	 * 
	 * @param objectid
	 * @param ids
	 * @return
	 * @throws ClassNotFoundException
	 * @throws OgnlException
	 */
	public ActionResult resetGridPageOrderno(String objectid, String[] ids)
			throws ClassNotFoundException, OgnlException {
		FDataobject dataObject = DataObjectUtils.getDataObject(objectid);
		if (!ObjectFunctionUtils.allowEdit(dataObject)) {
			return null;
		}
		Class<?> clazz = Class.forName(dataObject.getClassname());
		int[] nowrecordno = new int[ids.length];
		Object[] beans = new Object[ids.length];
		int i = 0;
		for (String id : ids) {
			Object bean = dao.findById(clazz, id);
			beans[i] = bean;
			nowrecordno[i] = (i + 1) * 10;
			i++;
		}
		for (i = 0; i < beans.length; i++) {
			Ognl.setValue(dataObject.getOrderfield(), beans[i], nowrecordno[i]);
		}
		ActionResult result = new ActionResult();
		result.setMsg(nowrecordno);
		result.setTag(dataObject.getOrderfield());
		return result;
	}

	public ActionResult updateParentkey(String objectname, String id, String parentkey) throws ClassNotFoundException {
		FDataobject dataObject = DataObjectUtils.getDataObject(objectname);
		if (!ObjectFunctionUtils.allowEdit(dataObject)) {
			return null;
		}
		// ??????????????????????????????bean
		Class<?> clazz = Class.forName(dataObject.getClassname());
		clazz.getClass();
		String upsql = "update " + dataObject._getTablename() + " set "
				+ dataObject._getParentKeyField().getFielddbname() + " = ?0 where "
				+ dataObject._getPrimaryKeyField()._getSelectName(null) + " = ?1 ";
		dao.executeSQLUpdate(upsql, new Object[] { parentkey, id });
		return new ActionResult();
	}

	public FUseroperatelog saveOperateLog(FDataobject dataobject, String id, String name, String doword,
			String remark) {

		if (doword != null) {
			if (doword.equals(Constants.NEW) || doword.equals(Constants.ADD)) {
				doword = "??????";
			}
			if (doword.equals(Constants.EDIT)) {
				doword = "??????";
			}
			if (doword.equals(Constants.DELETE)) {
				doword = "??????";
			} else if (doword.equals(Constants.AUDITING)) {
				doword = "??????";
			} else if (doword.equals(Constants.CANCELAUDITING)) {
				doword = "????????????";
			} else if (doword.equals(Constants.APPROVE)) {
				doword = "??????";
			} else if (doword.equals(Constants.CANCELAPPROVE)) {
				doword = "????????????";
			}
		}
		FUseroperatelog operateLog = new FUseroperatelog();
		operateLog.setOdate(new Date());
		operateLog.setDotype(doword);
		try {
			// ?????????????????????????????????????????????????????? Local
			operateLog.setIpaddress(CommonFunction.getIpAddr(Local.getRequest()));
		} catch (Exception e) {
			operateLog.setIpaddress("127.0.0.1");
		}
		operateLog.setFDataobject(dataobject);
		operateLog.setIdvalue(id);
		if (name != null && name.length() > Constants.INT_200) {
			name = name.substring(0, Constants.INT_200);
		}
		operateLog.setNamevalue(name);
		if (remark != null && remark.length() > Constants.INT_2000) {
			remark = remark.substring(0, Constants.INT_2000);
		}
		operateLog.setOcontent(remark);
		try {
			// ?????????????????????????????????????????????????????? Local
			operateLog.setFUser(dao.findById(FUser.class, Local.getUserid()));
		} catch (Exception e) {
			operateLog.setFUser(dao.findByPropertyFirst(FUser.class, Constants.USERCODE, Constants.ADMIN));
		}
		dao.save(operateLog);
		return operateLog;
	}

	public ActionResult fetchChildData(String objectid, String parentid, String childModuleName, String fieldahead,
			GridParams gp) {
		FDataobject pobject = DataObjectUtils.getDataObject(objectid);
		String[] s = fieldahead.split(Constants.DOTWITHDOT);
		UserDefineFilter udf = new UserDefineFilter();
		udf.setProperty(s[1] + "." + pobject._getPrimaryKeyField().getFieldname());
		udf.setOperator("=");
		udf.setValue(parentid);
		List<UserDefineFilter> udfs = new ArrayList<UserDefineFilter>();
		udfs.add(udf);
		PageInfo<Map<String, Object>> pageinfo = fetchDataInner(childModuleName, gp, null, null, null, null, udfs, null,
				null, null, null, null);
		ActionResult result = new ActionResult();
		result.setMsg(pageinfo.getData());
		result.setTag(pageinfo.getTotal());
		return result;
	}

	/**
	 * ???????????????????????????????????????????????????????????? treeselectpath?????????????????? ?????????????????????
	 * 
	 * @param objectname
	 * @param addselected
	 * @return
	 */
	public List<TreeNode> getTreeSelectPathData(String objectname, boolean addcheck, boolean disablenotleaf,
			String treeselectpath, String treeselectpathfieldid) {
		if (StringUtils.isNotBlank(treeselectpathfieldid)) {
			String[] f = treeselectpathfieldid.split("\\|");
			treeselectpath = dao.findById(FDataobjectfield.class, f[f.length - 1]).getFieldname();
			if (f.length == Constants.INT_2) {
				treeselectpath = f[0] + '.' + treeselectpath;
			}
		}
		return treeModuleDataDAO.getTreeSelectPathData(objectname, addcheck, disablenotleaf, treeselectpath);
	}

	public ActionResult getGridRecordExpandBody(String moduleName, String recordId) {
		ActionResult result = new ActionResult();
		FDataobject dataObject = DataObjectUtils.getDataObject(moduleName);
		Object logic = Local.getLogicBean(dataObject.getObjectname() + "Logic");
		if (logic == null) {
			result.setMsg("?????????????????????Logic??????");
			return result;
		}
		result.setMsg(((LogicInterface<Object>) logic).getGridRecordExpandBody(recordId));
		return result;
	}
}
