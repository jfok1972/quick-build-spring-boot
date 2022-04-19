package com.jhopesoft.platform.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.DataDeleteResponseInfo;
import com.jhopesoft.framework.bean.GridParams;
import com.jhopesoft.framework.bean.GroupParameter;
import com.jhopesoft.framework.bean.Name;
import com.jhopesoft.framework.bean.PageInfo;
import com.jhopesoft.framework.bean.ResultBean;
import com.jhopesoft.framework.bean.SortParameter;
import com.jhopesoft.framework.bean.TreeNode;
import com.jhopesoft.framework.bean.TreeNodeRecordChecked;
import com.jhopesoft.framework.bean.ValueText;
import com.jhopesoft.framework.core.annotation.SystemLogs;
import com.jhopesoft.framework.core.objectquery.export.GridColumn;
import com.jhopesoft.framework.core.objectquery.filter.UserDefineFilter;
import com.jhopesoft.framework.core.objectquery.filter.UserNavigateFilter;
import com.jhopesoft.framework.core.objectquery.filter.UserParentFilter;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.SqlMapperAdapter;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectview;
import com.jhopesoft.framework.dao.entity.viewsetting.FovGridsortscheme;
import com.jhopesoft.framework.exception.DataDeleteException;
import com.jhopesoft.framework.exception.DataUpdateException;
import com.jhopesoft.framework.exception.ProjectException;
import com.jhopesoft.framework.interceptor.transcoding.RequestList;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.FileUtils;
import com.jhopesoft.framework.utils.ProjectUtils;
import com.jhopesoft.platform.service.DataObjectService;

import ognl.OgnlException;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@RestController
@RequestMapping("/platform/dataobject")
public class DataObject extends SqlMapperAdapter {

	@Autowired
	private DataObjectService service;

	@RequestMapping(value = "/getexpandbody.do")

	public ActionResult getGridRecordExpandBody(String moduleName, String recordId) {

		return service.getGridRecordExpandBody(moduleName, recordId);

	}

	@SystemLogs("列表信息查询")
	@RequestMapping(value = "/fetchdata.do")

	public PageInfo<Map<String, Object>> fetchData(String moduleName, String moduleId, GridParams pg,
			@RequestList(clazz = SortParameter.class) List<SortParameter> sort, String group,
			@RequestList(clazz = UserDefineFilter.class) List<UserDefineFilter> filter,
			@RequestList(clazz = UserDefineFilter.class) List<UserDefineFilter> userfilter,
			@RequestList(clazz = UserDefineFilter.class) List<UserDefineFilter> query,
			@RequestList(clazz = GridColumn.class) List<GridColumn> columns,
			@RequestList(clazz = UserNavigateFilter.class) List<UserNavigateFilter> navigates,
			// @RequestList(clazz = UserParentFilter.class)
			// List<UserParentFilter>
			String parentFilter, String viewschemeid, String sortschemeid, String sqlparamstr,
			String dataminingFilter) {
		long s = System.currentTimeMillis();
		if (moduleName == null) {
			moduleName = moduleId;
		}
		if (filter == null) {
			filter = new ArrayList<UserDefineFilter>();
		}
		if (!CommonUtils.isEmpty(userfilter)) {
			filter.addAll(userfilter);
		}
		// 这里的parentFilter 是只有一个的，以后有多个的情况再另行考虑
		List<UserParentFilter> userParentFilters = UserParentFilter.changeToParentFilters(parentFilter, moduleName);
		FDataobjectview viewscheme = null;
		if (viewschemeid != null && viewschemeid.length() > 0) {
			viewscheme = Local.getDao().findById(FDataobjectview.class, viewschemeid);
		}
		FovGridsortscheme sortscheme = null;
		if (sortschemeid != null && sortschemeid.length() > 0) {
			sortscheme = Local.getDao().findById(FovGridsortscheme.class, sortschemeid);
		}
		JSONObject sqlparam = null;
		if (sqlparamstr != null && sqlparamstr.length() > 0) {
			sqlparam = JSONObject.parseObject(sqlparamstr);
		}
		PageInfo<Map<String, Object>> result = null;
		// 如果是数据分析的某一个数据的明细数据
		if (StringUtils.isNotBlank(dataminingFilter)) {
			JSONObject dataminingFilterObject = JSONObject.parseObject(dataminingFilter);
			if (dataminingFilterObject.containsKey(Constants.CONDITIONS)) {
				JSONArray array = dataminingFilterObject.getJSONArray(Constants.CONDITIONS);
				List<UserDefineFilter> dataminingCondition = JSONArray.parseArray(array.toJSONString(),
						UserDefineFilter.class);
				filter.addAll(dataminingCondition);
			}
			if (dataminingFilterObject.containsKey(Constants.NAVIGATEFILTERS)) {
				JSONArray array = dataminingFilterObject.getJSONArray(Constants.NAVIGATEFILTERS);
				List<UserDefineFilter> dataminingNavigate = JSONArray.parseArray(array.toJSONString(),
						UserDefineFilter.class);
				filter.addAll(dataminingNavigate);
			}
			if (dataminingFilterObject.containsKey(Constants.USERFILTERS)) {
				JSONArray array = dataminingFilterObject.getJSONArray(Constants.USERFILTERS);
				List<UserDefineFilter> dataminingUserfilters = JSONArray.parseArray(array.toJSONString(),
						UserDefineFilter.class);
				filter.addAll(dataminingUserfilters);
			}
			if (dataminingFilterObject.containsKey(Constants.VIEWSCHEMEID)) {
				viewscheme = Local.getDao().findById(FDataobjectview.class,
						dataminingFilterObject.getString(Constants.VIEWSCHEMEID));
			}
		}
		try {
			result = service.fetchDataInner(moduleName, pg, columns, GroupParameter.changeToGroupParameter(group), sort,
					query, filter, navigates, userParentFilters, viewscheme, sortscheme, sqlparam);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		result.setSpendtime(System.currentTimeMillis() - s);
		return result;
	}

	@RequestMapping(value = "/fetchtreedata.do")
	public JSONObject fetchTreeData(String moduleName, GridParams pg,
			@RequestList(clazz = SortParameter.class) List<SortParameter> sort,
			@RequestList(clazz = UserDefineFilter.class) List<UserDefineFilter> filter,
			@RequestList(clazz = UserDefineFilter.class) List<UserDefineFilter> userfilter,
			@RequestList(clazz = UserDefineFilter.class) List<UserDefineFilter> query,
			@RequestList(clazz = GridColumn.class) List<GridColumn> columns,
			@RequestList(clazz = UserNavigateFilter.class) List<UserNavigateFilter> navigates,
			// @RequestList(clazz = UserParentFilter.class)
			// List<UserParentFilter>
			String parentFilter, String viewschemeid, String node) {
		if (filter == null) {
			filter = new ArrayList<UserDefineFilter>();
		}
		if (!CommonUtils.isEmpty(userfilter)) {
			filter.addAll(userfilter);
		}
		// 这里的parentFilter 是只有一个的，以后有多个的情况再另行考虑
		List<UserParentFilter> userParentFilters = UserParentFilter.changeToParentFilters(parentFilter, moduleName);
		FDataobjectview viewscheme = null;
		if (viewschemeid != null && viewschemeid.length() > 0) {
			viewscheme = Local.getDao().findById(FDataobjectview.class, viewschemeid);
		}
		JSONObject result = null;
		try {
			result = service.fetchTreeDataInner(moduleName, pg, columns, sort, query, filter, navigates,
					userParentFilters, viewscheme);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	/**
	 * 获取某一个模块的选择数据，如果有query表示筛选的文本，如果主键id是整型，那么query是数值时主键会被加入
	 * @param moduleName
	 * @param mainlinkage
	 * @param query
	 * @param filter
	 * @return
	 */
	@RequestMapping(value = "/fetchcombodata.do")
	public List<ValueText> fetchModuleComboData(String moduleName, boolean mainlinkage, String query,
			@RequestList(clazz = UserDefineFilter.class) List<UserDefineFilter> filter) {
		return service.fetchModuleComboData(moduleName, mainlinkage, query, filter);
	}

	/**
	 * 在新增和修改模块时，该记录的manytoone字段,并且是分级的，需要取得数据
	 */
	@RequestMapping(value = "/fetchpickertreedata.do", method = RequestMethod.GET)
	public List<com.jhopesoft.framework.bean.TreeValueText> getModuleTreeData(String moduleName,
			Boolean allowParentValue, boolean addCodeToText, boolean shortName) {
		return service.getModuleWithTreeData(moduleName, allowParentValue == null ? false : allowParentValue, null,
				addCodeToText , shortName);
	}

	@SystemLogs("查询模块数据")
	@RequestMapping(value = "/fetchinfo.do")

	public ResultBean fetchInfo(String objectname, String id) {
		return service.fetchInfo(objectname, id);
	}

	@SystemLogs("查询模块数据")
	@RequestMapping(value = "/fetchchilddata.do")

	public ActionResult fetchChildData(String objectid, String parentid, String childModuleName, String fieldahead,
			GridParams pg) {
		return service.fetchChildData(objectid, parentid, childModuleName, fieldahead, pg);
	}

	@RequestMapping(value = "/getnewdefault.do")

	public ResultBean getRecordNewDefault(String objectname, String parentfilter, String navigates) {
		ResultBean result = new ResultBean();
		try {
			result = service.getRecordNewDefault(objectname, parentfilter, navigates);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@SystemLogs("新增或修改模块数据")
	@RequestMapping(value = "/saveorupdate.do")

	public ResultBean saveOrUpdate(String objectname, String oldid, String opertype) throws IOException {
		if (opertype == null) {
			opertype = Constants.EDIT;
		}
		StringBuffer sb = new StringBuffer();
		// 自己从文件流中获取传递的数据，解决当参数值为null，无法获取key的问题
		FileUtils.copy(Local.getRequest().getInputStream(), sb);
		return saveOrUpdate(objectname, sb.toString(), oldid, opertype);
	}

	/**
	 * 这个是用antd保存和新增用的，在post的时候，将修改的记录用JSON.stringify转化为字符串，然后再用data创建formdata
	 * 主要是用原来的方式windows下面nginx在转发的时候中文会乱码
	 * 
	 * @param objectname
	 * @param oldid
	 * @param opertype
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@SystemLogs("新增或修改模块数据")
	@RequestMapping(value = "/saveorupdatedata.do")

	public ResultBean saveOrUpdateWithFormData(String objectname, String oldid, String opertype, String data)
			throws IOException {
		if (opertype == null) {
			opertype = Constants.EDIT;
		}
		return saveOrUpdate(objectname, data, oldid, opertype);
	}

	public ResultBean saveOrUpdate(String objectname, String inserted, String oldid, String opertype) {
		if (opertype == null) {
			opertype = Constants.EDIT;
		}
		ResultBean result = new ResultBean();
		try {
			result = service.saveOrUpdate(objectname, inserted, oldid, opertype);
		} catch (DataUpdateException e) {
			System.out.println(e.getErrorMessage());
			result.setSuccess(false);
			result.setData(e.getErrorMessage());
			result.setMessage(e.getMessage());
		} catch (ConstraintViolationException e) {
			e.printStackTrace();
			FDataobject dataObject = DataObjectUtils.getDataObject(objectname);
			result = ProjectUtils.getErrorMassage(e, dataObject, dao, getSf());
		} catch (PersistenceException e) {
			e.printStackTrace();
			FDataobject dataObject = DataObjectUtils.getDataObject(objectname);
			result = ProjectUtils.getErrorMassage(e, dataObject, dao, getSf());
		} catch (DuplicateKeyException e) {
			e.printStackTrace();
			FDataobject dataObject = DataObjectUtils.getDataObject(objectname);
			result = ProjectUtils.getErrorMassage(e, dataObject, dao, getSf());
		} catch (ProjectException e) {
			e.printStackTrace();
			Throwable original = e.getOriginalThrowable();
			if (original.getClass().equals(DataUpdateException.class)) {
				result.setSuccess(false);
				result.setData(((DataUpdateException) original).getErrorMessage());
				result.setMessage(original.getMessage());
			} else {
				result.setSuccess(false);
				result.setMessage(e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@SystemLogs("删除数据")
	@RequestMapping(value = "/remove.do")

	public DataDeleteResponseInfo remove(String objectname, @RequestBody String removed) {
		JSONObject object = JSON.parseObject(removed);
		String id = null;
		for (String idfieldname : object.keySet()) {
			id = object.getString(idfieldname);
			break;
		}
		return removeWithId(objectname, id);
	}

	public DataDeleteResponseInfo removeWithId(String objectname, String id) {
		DataDeleteResponseInfo result = new DataDeleteResponseInfo();
		FDataobject object = DataObjectUtils.getDataObject(objectname);
		try {
			result = service.remove(objectname, id);
		} catch (PersistenceException e) {
			e.printStackTrace();
			result.setResultCode(-1);
			String fkTablename = null;
			// 外键冲突的异常
			if (e.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
				if (Constants.SQLSTATE_23000.equals(cve.getSQLState())) {
					String message = cve.getCause().getMessage();
					fkTablename = getSf().getFkConstraintTableName(dao, object.getTablename(), message,
							object.getSchemaname());
					if (fkTablename != null) {
						String sql = "select title as name from f_dataobject where lower(tablename) = ?0 ";
						Name name = dao.executeSQLQueryFirst(sql, Name.class, fkTablename.toLowerCase());
						result.getErrorMessageList()
								.add("与本记录相关联的『" + (name == null ? fkTablename : name.getName()) + "』数据没有全部清空");
					}
				}
			}
			// 其他数据库抛出的异常，包括触发器和存贮过程中的
			if (fkTablename == null) {
				String message = e.getCause() != null
						? e.getCause().getCause() != null ? e.getCause().getCause().getMessage()
								: e.getCause().getMessage()
						: e.getMessage();
				result.getErrorMessageList().add(message);
			}
		} catch (DataDeleteException e) {
			result.setResultCode(-1);
			result.getErrorMessageList().addAll(e.getErrorMessage());
		} catch (ProjectException e) {
			e.printStackTrace();
			Throwable original = e.getOriginalThrowable();
			if (original.getClass().equals(DataDeleteException.class)) {
				result.setResultCode(-1);
				result.getErrorMessageList().addAll(((DataDeleteException) original).getErrorMessage());
			} else {
				result.setResultCode(-1);
				result.getErrorMessageList().add(e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(-1);
			result.getErrorMessageList().add(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/removerecords.do")
	public DataDeleteResponseInfo removeRecords(String moduleName, String ids, String titles,
			HttpServletRequest request) {
		DataDeleteResponseInfo result = null;
		String[] idarray = ids.split(Constants.COMMA);
		String[] titlearray = titles.split("~~");
		result = new DataDeleteResponseInfo();
		for (int i = 0; i < idarray.length; i++) {
			DataDeleteResponseInfo recordDeleteResult = removeWithId(moduleName, idarray[i]);
			if (recordDeleteResult.getResultCode() == 0) {
				result.getOkMessageList().add(titlearray[i]);
				result.getOkIds().add(idarray[i]);
			} else {
				if (recordDeleteResult.getErrorMessageList().size() > 0) {
					result.getErrorMessageList()
							.add("『" + titlearray[i] + "』" + recordDeleteResult.getErrorMessageList().get(0));
				} else {
					result.getErrorMessageList().add("『" + titlearray[i] + "』" + "未知错误！");
				}
				result.getErrorIds().add(idarray[i]);
			}
		}
		result.setResultCode(result.getErrorMessageList().size());
		return result;
	}

	/**
	 * @param request
	 * @param moduleName           当前模块名称
	 * @param id                   当前记录id
	 * @param manyToManyModuleName manyToMany的模块名称
	 * @param linkModuleName       中间模块名称
	 * @return 返回所有manyToManyModuleName的记录数据，并把当前记录已有的manyToMany值的checked置为true
	 */
	@RequestMapping("/getmanytomanydetail.do")
	public List<TreeNodeRecordChecked> genManyToManyDetail(HttpServletRequest request, String moduleName, String id,
			String manyToManyModuleName, String linkModuleName) {
		return service.getManyToManyDetail(request, moduleName, id, manyToManyModuleName, linkModuleName);

	}

	/**
	 * @param request
	 * @param moduleName           当前模块名称
	 * @param id                   当前记录id
	 * @param manyToManyModuleName manyToMany的模块名称
	 * @param linkModuleName       中间模块名称
	 * @return 返回所有manyToManyModuleName的 id 值。
	 */
	@RequestMapping("/getmanytomanydetailids.do")
	public List<String> getManyToManyDetailIds(HttpServletRequest request, String moduleName, String id,
			String manyToManyModuleName, String linkModuleName) {
		return service.getManyToManyDetailIds(request, moduleName, id, manyToManyModuleName, linkModuleName);

	}

	/**
	 * @param request
	 * @param moduleName           当前模块名称
	 * @param id                   当前记录id
	 * @param manyToManyModuleName manyToMany的模块名称
	 * @param linkModuleName       中间模块名称
	 * @param selected             所有选中的值，以逗号分隔
	 * @return 返回所有manyToManyModuleName的记录数据，并把当前记录已有的manyToMany值的checked置为true
	 * @throws OgnlException
	 * @throws ClassNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */

	@RequestMapping("/setmanytomanydetail.do")
	public ActionResult setManyToManyDetail(String moduleName, String id, String manyToManyModuleName,
			String linkModuleName, String selected)
			throws ClassNotFoundException, OgnlException, IllegalAccessException, InvocationTargetException {
		return service.setManyToManyDetail(moduleName, id, manyToManyModuleName, linkModuleName,
				selected.split(Constants.COMMA));

	}

	/**
	 * 更改树形的orderno
	 * 
	 * @param objectid
	 * @param ids
	 * @return
	 */
	@RequestMapping("/updateorderno.do")
	public ActionResult updateOrderno(String objectid, String ids, Boolean addparent, Integer startnumber,
			Integer stepnumber, Integer parentnumber) {
		ActionResult result = null;
		try {
			result = service.updateOrderno(objectid, ids.split(Constants.COMMA), addparent, startnumber, stepnumber,
					parentnumber);
		} catch (Exception e) {
			e.printStackTrace();
			result = new ActionResult(false, e.getMessage());
		}
		return result;
	}

	/**
	 * 按照已有的orderno重排grid的orderno , 并不会生成新的orderno
	 * 
	 * @param objectid
	 * @param ids
	 * @return
	 */
	@RequestMapping("/updatepageorderno.do")
	public ActionResult updateGridPageOrderno(String objectid, String ids) {
		ActionResult result = null;
		try {
			result = service.updateGridPageOrderno(objectid, ids.split(Constants.COMMA));
		} catch (Exception e) {
			e.printStackTrace();
			result = new ActionResult(false, e.getMessage());
		}
		return result;
	}

	/**
	 * 按照已有的orderno重排grid的orderno
	 * 
	 * @param objectid
	 * @param ids
	 * @return
	 */
	@RequestMapping("/resetpageorderno.do")
	public ActionResult resetGridPageOrderno(String objectid, String ids) {
		ActionResult result = null;
		try {
			result = service.resetGridPageOrderno(objectid, ids.split(Constants.COMMA));
		} catch (Exception e) {
			e.printStackTrace();
			result = new ActionResult(false, e.getMessage());
		}
		return result;
	}

	@RequestMapping("/updateparentkey.do")
	public ActionResult updateParentkey(String objectname, String id, String parentkey) {
		ActionResult result = null;
		String[] ids = id.split(Constants.COMMA);
		try {
			for (String aid : ids) {
				result = service.updateParentkey(objectname, aid,
						parentkey == null || parentkey.length() == 0 ? null : parentkey);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = new ActionResult(false, e.getMessage());
		}
		return result;
	}

	/**
	 * 在进行多选或者单选的时候，根据模块设置的 treeselectpath树形选择路径 来生成树形结构
	 * 
	 * @param objectname
	 * @param addselected           是否加入选择框
	 * @param disablenotleaf        不是叶结点全部禁用
	 * @param treeselectpath        可以自己设置分组
	 * @param treeselectpathfieldid 设置的字段 fieldahead|fieldid,需要转换一下
	 * @return
	 */
	@RequestMapping("/fetchtreeselectpathdata.do")
	public List<TreeNode> getTreeSelectPathData(String objectname, boolean addcheck, boolean disablenotleaf,
			String treeselectpath, String treeselectpathfieldid) {
		return service.getTreeSelectPathData(objectname, addcheck, disablenotleaf, treeselectpath,
				treeselectpathfieldid);
	}

}
