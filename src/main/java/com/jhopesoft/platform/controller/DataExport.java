package com.jhopesoft.platform.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ExcelExportSetting;
import com.jhopesoft.framework.bean.GridParams;
import com.jhopesoft.framework.bean.GroupParameter;
import com.jhopesoft.framework.bean.SortParameter;
import com.jhopesoft.framework.core.annotation.SystemLogs;
import com.jhopesoft.framework.core.objectquery.export.ExcelColumn;
import com.jhopesoft.framework.core.objectquery.export.GridColumn;
import com.jhopesoft.framework.core.objectquery.filter.UserDefineFilter;
import com.jhopesoft.framework.core.objectquery.filter.UserNavigateFilter;
import com.jhopesoft.framework.core.objectquery.filter.UserParentFilter;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectview;
import com.jhopesoft.framework.dao.entity.viewsetting.FovGridsortscheme;
import com.jhopesoft.framework.interceptor.transcoding.RequestList;
import com.jhopesoft.framework.utils.CommonFunction;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.PdfUtils;
import com.jhopesoft.platform.logic.define.LogicInterface;
import com.jhopesoft.platform.service.DataExportService;

/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/dataobjectexport")
public class DataExport {

	@Autowired
	private DataExportService dataExportService;

	@SystemLogs("导出excel数据表")
	@RequestMapping(value = "/exporttoexcel.do")

	public void exportToExcel(HttpServletRequest request, HttpServletResponse response, String moduleName,
			GridParams pg, @RequestList(clazz = SortParameter.class) List<SortParameter> sort, String group,
			@RequestList(clazz = UserDefineFilter.class) List<UserDefineFilter> filter,
			@RequestList(clazz = UserDefineFilter.class) List<UserDefineFilter> userfilter,
			@RequestList(clazz = UserDefineFilter.class) List<UserDefineFilter> query,
			@RequestList(clazz = GridColumn.class) List<ExcelColumn> columns,
			@RequestList(clazz = UserNavigateFilter.class) List<UserNavigateFilter> navigates,
			@RequestList(clazz = UserDefineFilter.class) List<UserDefineFilter> conditions, String parentFilter,
			String viewschemeid, String sortschemeid, String sqlparamstr, String dataminingFilter, boolean topdf,
			boolean colorless, boolean usemonetary, Integer monetaryUnit, String monetaryText, boolean sumless,
			boolean unitalone, String formschemeid, String formschemetitle, String pagesize, boolean autofitwidth,
			short scale) throws IOException {
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
			if (dataminingFilterObject.containsKey(Constants.DATAMININGFILTER)) {
				JSONArray conditionarray = dataminingFilterObject.getJSONArray(Constants.DATAMININGFILTER);
				for (int i = 0; i < conditionarray.size(); i++) {
					if (conditions == null) {
						conditions = new ArrayList<UserDefineFilter>();
					}
					JSONObject object = conditionarray.getJSONObject(i);
					if (object.getString("source").equals("视图方案")) {
						conditions.add(
								new UserDefineFilter(object.getString("source"), ":", object.getString("displaycond")));
					} else {
						conditions.add(new UserDefineFilter(object.getString("source")
								+ (object.containsKey("fieldtitle") ? ":" + object.getString("fieldtitle") : ""),
								object.getString(Constants.OPERATOR), object.getString("displaycond")));
					}
				}
			}
		}

		ExcelExportSetting setting = new ExcelExportSetting(colorless, usemonetary, monetaryUnit, monetaryText, sumless,
				unitalone, pagesize, autofitwidth, scale);

		// 根据formscheme的设置来导出树状的或者grid类型的excel，根据formtype来决定
		FDataobject module = DataObjectUtils.getDataObject(moduleName);
		if (formschemeid != null) {
			OutputStream os = dataExportService.generateTreeModeExcel(moduleName, pg, columns, sort,
					GroupParameter.changeToGroupParameter(group), query, filter, navigates, userParentFilters,
					conditions, viewscheme, sortscheme, sqlparam, formschemeid, setting);
			if (topdf) {
				ByteArrayOutputStream pdfos = new ByteArrayOutputStream();
				InputStream inputstream = new ByteArrayInputStream(((ByteArrayOutputStream) os).toByteArray());
				String fileName = module.getTitle() + "列表--" + CommonFunction.genOrderNumberWithDate()
						+ Constants.DOTPDF;
				PdfUtils.convert(inputstream, pdfos, Constants.XLSX, Constants.PDF);
				CommonFunction.download(pdfos, fileName, Local.getResponse());
			} else {
				String fn = module.getTitle() + "--" + formschemetitle + "--" + CommonFunction.genOrderNumberWithDate()
						+ Constants.DOTXLSX;
				CommonFunction.download(os, fn, Local.getResponse());
			}
		} else {
			// gridscheme导出Excel
			OutputStream os = dataExportService.generateExcel(moduleName, pg, columns, sort,
					GroupParameter.changeToGroupParameter(group), query, filter, navigates, userParentFilters,
					conditions, viewscheme, sortscheme, sqlparam, topdf, setting);
			if (topdf) {
				ByteArrayOutputStream pdfos = new ByteArrayOutputStream();
				InputStream inputstream = new ByteArrayInputStream(((ByteArrayOutputStream) os).toByteArray());
				String fileName = module.getTitle() + "列表--" + CommonFunction.genOrderNumberWithDate()
						+ Constants.DOTPDF;
				PdfUtils.convert(inputstream, pdfos, Constants.XLSX, Constants.PDF);
				CommonFunction.download(pdfos, fileName, Local.getResponse());
			} else {
				String fn = module.getTitle() + "列表--" + CommonFunction.genOrderNumberWithDate() + Constants.DOTXLSX;
				CommonFunction.download(os, fn, Local.getResponse());
			}
		}
	}

	@SystemLogs("根据选中的模块和记录，导出excel或word的模板")
	@RequestMapping(value = "/exportexcelscheme.do")

	/**
	 * platform/dataobjectexport/exportexcelscheme.do?schemeid={1}&objectid={2}&recordid={3}&filetype={4}
	 */
	public void exportExcelScheme(String objectid, String moduleName, String schemeid, String recordids,
			String filetype, boolean inline) {
		if (objectid == null) {
			objectid = moduleName;
		}
		try {
			dataExportService.exportExcelScheme(objectid, schemeid, recordids, filetype, inline);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				CommonFunction.downloadFileError(Local.getResponse(),
						e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), e);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 根据选中的字段生成模块的excel模板，再加工一下就可以上传
	 * 
	 * @param fieldids
	 * @throws IOException
	 */
	@RequestMapping(value = "/exportexceltemplate.do")

	public void exportExcelTemplate(String fieldids) throws IOException {
		try {
			String moduleTitle = dataExportService.getModuleTitle(fieldids);
			OutputStream os = dataExportService.exportExcelTemplate(fieldids);
			CommonFunction.download(os, moduleTitle + "的记录excel模板文件.xlsx", Local.getResponse());
		} catch (Exception e) {
			e.printStackTrace();
			try {
				CommonFunction.downloadFileError(Local.getResponse(),
						e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), e);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 生成选中的记录的打印的html
	 * 
	 * @param moduleName
	 * @param id
	 * @param schemeId
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/printrecord.do")
	public void printRecordExcel(String moduleName, String id, String schemeId, String title)
			throws ClassNotFoundException {

		String[] htmlMarkup = { "<html><head><link rel=\"icon\" href=\"/favicon.png\" type=\"image/x-icon\" />",
				"<link href=\"/styles/printrecord.css\" rel=\"stylesheet\" type=\"text/css\" />",
				"<title>打印" + title + "</title>", "</head><body>",
				// "<div class=\"printer-noprint\">",
				// "<div class=\"buttons\">",
				// "<a class=\"button-print\" href=\"javascript:void(0);\"
				// onclick=\"window.print();return false;\">打 印</a>",
				// "<a class=\"button-exit\" href=\"javascript:void(0);\"
				// onclick=\"window.close();return false;\">关 闭</a>",
				// "</div>",
				// "</div>",
				dataExportService.genPrintHtml(moduleName, id, schemeId), "</body>", "</html>" };

		Object logic = Local.getLogicBean(moduleName + "Logic");
		if (logic != null && logic instanceof LogicInterface) {
			((LogicInterface<Object>) logic).afterPrintRecord(id, schemeId);
		}
		HttpServletResponse response = Local.getResponse();
		PrintWriter pw = null;
		response.setHeader("Content-Type", "text/html;charset=UTF-8");
		try {
			pw = response.getWriter();
			pw.write(String.join("", htmlMarkup));
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pw.close();
		}
	}

}
