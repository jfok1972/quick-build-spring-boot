package com.jhopesoft.platform.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.core.datamining.service.DataminingColumnSchemeService;
import com.jhopesoft.framework.core.datamining.service.DataminingDataService;
import com.jhopesoft.framework.core.datamining.service.DataminingExportService;
import com.jhopesoft.framework.core.datamining.service.DataminingFieldSchemeService;
import com.jhopesoft.framework.core.datamining.service.DataminingFilterSchemeService;
import com.jhopesoft.framework.core.datamining.service.DataminingRowSchemeService;
import com.jhopesoft.framework.core.datamining.service.DataminingSchemeService;
import com.jhopesoft.framework.core.datamining.service.DataminingService;
import com.jhopesoft.framework.core.objectquery.export.ExcelColumn;
import com.jhopesoft.framework.core.objectquery.filter.UserDefineFilter;
import com.jhopesoft.framework.interceptor.transcoding.RequestList;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectFieldUtils;
import com.jhopesoft.framework.utils.DataObjectUtils;

/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/datamining")
public class Datamining {

	@Autowired
	private DataminingService dataminingService;

	@Autowired
	private DataminingFieldSchemeService dataminingFieldSchemeService;

	@Autowired
	private DataminingColumnSchemeService dataminingColumnSchemeService;

	@Autowired
	private DataminingRowSchemeService dataminingRowSchemeService;

	@Autowired
	private DataminingSchemeService dataminingSchemeService;

	@Autowired
	private DataminingExportService dataminingExportService;

	@Autowired
	private DataminingFilterSchemeService dataminingFilterSchemeService;

	@Autowired
	private DataminingDataService dataminingDataService;

	// ????????????????????????
	/**
	 * 
	 * @param schemeid          ??????????????????id
	 * @param schemetitle       ??????????????????title,?????????????????????
	 * @param treemodel         ?????????????????????????????????????????????list??????
	 * @param isnumberordername ??????????????????????????????????????????jf001,jf002
	 * @return
	 */
	@RequestMapping(value = "/fetchdataminingdata.do")

	public List<?> fetchDataminingData(String schemeid, String schemetitle, boolean treemodel,
			Boolean isnumberordername) {
		return dataminingDataService.fetchDataminingData(schemeid, treemodel, null, null, schemetitle,
				BooleanUtils.isTrue(isnumberordername));
	}

	// ????????????????????????,????????????????????????????????????????????????????????????????????????parent??????????????????????????????
	/**
	 * { children : [text : '???', dataValue : 100],
	 * text : '??????'
	 * } ==>
	 * [ zb1 : '??????', zb2 : '???', value : 100],
	 * 
	 * @param schemeid    ??????????????????id
	 * @param schemetitle ??????????????????title,?????????????????????
	 * @param addTotal	  ????????????????????????????????? true,????????????????????????????????????
	 * @return
	 */
	@RequestMapping(value = "/fetchpivotdata.do")

	public List<?> fetchPivotData(String schemeid, String schemetitle, Boolean addTotal) {
		return dataminingDataService.fetchPivotData(schemeid, schemetitle, null, null, BooleanUtils.isTrue(addTotal));
	}

	// ????????????

	@RequestMapping(value = "/getschemes.do")

	public JSONArray getSchemes(String moduleName) {
		return dataminingSchemeService.getSchemes(moduleName);
	}

	@RequestMapping(value = "/addscheme.do")

	public ActionResult addScheme(String moduleName, String title, Boolean savepath, String fieldGroup,
			String columnGroup, String rowGroup, Boolean ownerfilter, String filter, String setting, Boolean isshare) {
		return dataminingSchemeService.addScheme(moduleName, title, savepath, fieldGroup, columnGroup, rowGroup,
				ownerfilter, filter, setting , isshare);
	}

	@RequestMapping(value = "/editscheme.do")

	public ActionResult editScheme(String schemeid, String name, Boolean savepath, String fieldGroup,
			String columnGroup, String rowGroup, Boolean ownerfilter, String filter, String setting) {
		return dataminingSchemeService.editScheme(schemeid, name, savepath, fieldGroup, columnGroup, rowGroup,
				ownerfilter, filter, setting);
	}

	@RequestMapping(value = "/getschemedetail.do")

	public JSONObject getSchemeDetail(String schemeid) {
		return dataminingSchemeService.getSchemeDetail(schemeid);
	}

	@RequestMapping(value = "/deletescheme.do")

	public ActionResult deleteScheme(String schemeid) {
		return dataminingSchemeService.deleteScheme(schemeid);
	}

	///////////////////////////////////////////////////

	/** ??????????????? */
	@RequestMapping(value = "/getcolumnschemes.do")

	public JSONArray getColumnSchemes(String moduleName) {
		return dataminingColumnSchemeService.getColumnSchemes(moduleName);
	}

	@RequestMapping(value = "/getcolumnschemedetail.do")

	public JSONArray getColumnSchemeDetail(String schemeid) {
		return dataminingColumnSchemeService.getColumnSchemeDetail(schemeid);
	}

	@RequestMapping(value = "/addcolumnscheme.do")

	public ActionResult addColumnScheme(String moduleName, String title, String columnGroup) {
		return dataminingColumnSchemeService.addColumnScheme(moduleName, title, columnGroup);
	}

	@RequestMapping(value = "/deletecolumnscheme.do")

	public ActionResult deleteColumnScheme(String schemeid) {
		return dataminingColumnSchemeService.deleteColumnScheme(schemeid);
	}

	///////////////////////////////////////////////////

	/** ??????????????? */
	@RequestMapping(value = "/getrowschemes.do")

	public JSONArray getRowSchemes(String moduleName) {
		return dataminingRowSchemeService.getRowSchemes(moduleName);
	}

	@RequestMapping(value = "/getrowschemedetail.do")

	public JSONArray getRowSchemeDetail(String schemeid) {
		return dataminingRowSchemeService.getRowSchemeDetail(schemeid);
	}

	@RequestMapping(value = "/addrowscheme.do")

	public ActionResult addRowScheme(String moduleName, String title, Boolean savepath, String rowGroup) {
		return dataminingRowSchemeService.addRowScheme(moduleName, title, savepath, rowGroup);
	}

	@RequestMapping(value = "/deleterowscheme.do")

	public ActionResult deleteRowScheme(String schemeid) {
		return dataminingRowSchemeService.deleteRowScheme(schemeid);
	}

	/** ?????????????????? */
	@RequestMapping(value = "/getfieldschemes.do")

	public JSONArray getFieldSchemes(String moduleName) {
		return dataminingFieldSchemeService.getFieldSchemes(moduleName);
	}

	/**
	 * ???????????????moduleName,????????????????????????????????????????????????????????????
	 * 
	 * @param schemeid
	 * @param moduleName
	 * @return
	 */
	@RequestMapping(value = "/getfieldschemedetail.do")

	public JSONArray getFieldSchemeDetail(String schemeid, String moduleName) {
		return dataminingFieldSchemeService.getFieldSchemeDetail(schemeid, moduleName);
	}

	@RequestMapping(value = "/addfieldscheme.do")

	public ActionResult addFieldScheme(String moduleName, String title, String fieldGroup) {
		return dataminingFieldSchemeService.addFieldScheme(moduleName, title, fieldGroup);
	}

	@RequestMapping(value = "/deletefieldscheme.do")

	public ActionResult deleteFieldScheme(String schemeid) {
		return dataminingFieldSchemeService.deleteFieldScheme(schemeid);
	}

	///////////////////////////////////////////////////

	/** ?????????????????? */
	@RequestMapping(value = "/getfilterschemes.do")

	public JSONArray getFilterSchemes(String moduleName) {
		return dataminingFilterSchemeService.getFilterSchemes(moduleName);
	}

	@RequestMapping(value = "/getfilterschemedetail.do")

	public ActionResult getFilterSchemeDetail(String schemeid) {
		return dataminingFilterSchemeService.getFilterSchemeDetail(schemeid);
	}

	@RequestMapping(value = "/addfilterscheme.do")

	public ActionResult addFilterScheme(String moduleName, String title, String othersetting) {
		return dataminingFilterSchemeService.addFilterScheme(moduleName, title, othersetting);
	}

	@RequestMapping(value = "/deletefilterscheme.do")

	public ActionResult deleteFilterScheme(String schemeid) {
		return dataminingFilterSchemeService.deleteFilterScheme(schemeid);
	}

	@RequestMapping(value = "/getfiltercount.do")

	public Integer[] getFilterCount(String moduleName, String filters, String fields, String sqlparamstr) {
		JSONObject sqlparam = null;
		if (sqlparamstr != null && sqlparamstr.length() > 0) {
			sqlparam = JSONObject.parseObject(sqlparamstr);
		}
		return dataminingService.getFilterCount(moduleName, filters, fields, sqlparam);
	}

	private static final String ALL = "-all";

	@RequestMapping(value = "/fetchdata.do")
	@SuppressWarnings({ "unchecked" })
	public List<?> fetchData(String moduleName, @RequestList(clazz = String.class) List<String> conditions,
			@RequestList(clazz = String.class) List<String> fields, String groupfieldid, String groupfieldid2,
			@RequestList(clazz = String.class) List<String> parentconditions,
			@RequestList(clazz = UserDefineFilter.class) List<UserDefineFilter> navigatefilters, String viewschemeid,
			@RequestList(clazz = UserDefineFilter.class) List<UserDefineFilter> userfilters, boolean addchecked,
			String sqlparamstr, boolean isnumberordername) {
		List<?> result = null;
		JSONObject sqlparam = null;
		if (sqlparamstr != null && sqlparamstr.length() > 0) {
			sqlparam = JSONObject.parseObject(sqlparamstr);
		}
		try {
			// ?????????codelevel????????????????????????????????????
			if (groupfieldid != null && groupfieldid.endsWith(ALL)) {
				result = dataminingService.fetchLevelAllData(moduleName, conditions, fields,
						groupfieldid.replace(ALL, ""), parentconditions, navigatefilters, viewschemeid, userfilters,
						addchecked, sqlparam, isnumberordername);
			} else {
				result = dataminingService.fetchData(moduleName, conditions, fields,
						(groupfieldid != null && groupfieldid.length() > 0 ? groupfieldid : null), parentconditions,
						navigatefilters, viewschemeid, userfilters, addchecked, sqlparam, isnumberordername);
				// ????????????????????????????????????????????????????????????????????????????????????????????????children
				if (groupfieldid2 != null) {
					for (Object obj : result) {
						Map<String, Object> mapObj = (Map<String, Object>) obj;
						// ??????????????????????????????????????????????????????????????????
						List<UserDefineFilter> pUserFilters = new ArrayList<UserDefineFilter>();
						if (userfilters != null) {
							pUserFilters.addAll(userfilters);
						}
						UserDefineFilter filter = new UserDefineFilter();
						filter.setOperator("=");
						filter.setValue(mapObj.get("value").toString());
						if (groupfieldid.startsWith("{")) {
							// ???????????????field???????????? string?????????
							groupfieldid = DataObjectFieldUtils.parseFieldStrFromObject(moduleName,
									JSONObject.parseObject(groupfieldid));
						}
						filter.setProperty_(groupfieldid);
						pUserFilters.add(filter);
						mapObj.put(Constants.CHILDREN,
								dataminingService.fetchData(moduleName, conditions, fields, groupfieldid2,
										parentconditions, navigatefilters, viewschemeid, pUserFilters, addchecked,
										sqlparam, isnumberordername));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	/**
	 * ????????????????????????????????????????????????????????????
	 * ????????????????????????????????????????????????????????????,????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param moduleName
	 * @return
	 */
	@RequestMapping(value = "/getexpandgroupfields.do")

	public JSONObject getModuleExpandGroupFields(String moduleName) {
		JSONObject result = new JSONObject();
		result.put("list", dataminingService.getModuleExpandGroupFields(moduleName));
		result.put("tree", dataminingService.getModuleExpandGroupFieldsTree(moduleName));
		return result;
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????column???
	 * 
	 * @param baseModuleName     // ???????????????????????????
	 * @param fieldid            //??????????????????????????? SOrder.SCustomer|fieldid
	 * @param functionid         //??????????????????????????????
	 * @param numbergroupid      //??????????????????????????????
	 * @param userdefinefunction //??????????????????????????????
	 * @param parentfilter       // ???????????????????????????
	 * @param onlycontainerdata  // ???????????????????????????
	 * @return
	 */

	@RequestMapping(value = "/getgroupfielddata.do")

	public JSONArray getGroupFieldData(String baseModuleName, String fieldid, String functionid, String numbergroupid,
			String userdefinefunction, @RequestList(clazz = String.class) List<String> parentconditions,
			@RequestList(clazz = String.class) List<String> navigatefilters, boolean onlycontainerdata) {
		JSONArray result = null;
		try {
			result = dataminingService.getGroupFieldData(baseModuleName, fieldid, parentconditions, navigatefilters,
					true);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	/**
	 * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param moduleName
	 * @return
	 */
	@RequestMapping(value = "/getallaggregatefields.do")

	public JSONArray getAllAggregateFields(String moduleName, String modulePath) {
		return dataminingService.getAllAggregateFields(moduleName, modulePath);
	}

	@RequestMapping(value = "/importexpandgroup.do")

	public ActionResult importDataminingExpandGroup(String dataobjectid) {
		return dataminingService.importDataminingExpandGroup(DataObjectUtils.getDataObject(dataobjectid));
	}

	@RequestMapping(value = "/exporttoexcel.do")

	public void exportToExcel(String moduletitle, String schemename, String conditions,
			@RequestList(clazz = ExcelColumn.class) List<ExcelColumn> columns,
			@RequestList(clazz = ExcelColumn.class) List<ExcelColumn> leafcolumns, String data, boolean colorless,
			int monerary, String moneraryText, boolean disablerowgroup, boolean unittextalone, boolean topdf,
			String pagesize, boolean autofitwidth, short scale) throws IOException {

		dataminingExportService.exportToExcel(moduletitle, schemename, conditions, columns, leafcolumns, data,
				colorless, monerary, moneraryText, disablerowgroup, unittextalone, topdf, pagesize, autofitwidth,
				scale);

	}

}
