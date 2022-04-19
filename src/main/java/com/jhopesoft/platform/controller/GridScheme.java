package com.jhopesoft.platform.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.platform.service.GridSchemeService;

/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/scheme/grid")
public class GridScheme {

	@Resource
	private GridSchemeService gridSchemeService;

	/**
	 * 保存用户设置的 列表 方案
	 * 
	 * @param request
	 * @param gridSchemeId
	 * @param schemeDefine
	 *          传进来的所有column包括合并列头的定义
	 * @return
	 */

	@RequestMapping("/updatedetails.do")
	public  ActionResult updateGridSchemeColumns(HttpServletRequest request, String dataObjectId,
			String gridSchemeId, String schemeDefine, String gridSchemeName, Boolean mydefault, Boolean shareowner,
			Boolean shareall) {

		return gridSchemeService.updateGridSchemeColumns(request, dataObjectId, gridSchemeId, gridSchemeName, schemeDefine,
				mydefault, shareowner, shareall);
	}

	@RequestMapping("/getdetailsforedit.do")
	public  JSONObject getGridSchemeColumnsForEdit(HttpServletRequest request, String gridSchemeId) {
		return gridSchemeService.getGridSchemeColumnsForEdit(request, gridSchemeId);
	}

	@RequestMapping("/getdetailsfordisplay.do")
	public  JSONArray getGridSchemeColumnsForDisplay(HttpServletRequest request, String gridSchemeId) {
		return gridSchemeService.getGridSchemeColumnsForDisplay(request, gridSchemeId);
	}

	@RequestMapping("/deletescheme.do")
	public  ActionResult deleteGridScheme(HttpServletRequest request, String schemeid) {
		return gridSchemeService.deleteGridScheme(request, schemeid);
	}

	@RequestMapping("/schemesaveas.do")
	public  ActionResult gridSchemeSaveas(HttpServletRequest request, String schemeid, String schemename) {
		return gridSchemeService.gridSchemeSaveas(request, schemeid, schemename);
	}

	@RequestMapping("/updatecolumnwidth.do")
	public  ActionResult updateColumnWidth(String type, String gridFieldId, int width) {
		return gridSchemeService.updateColumnWidth(type, gridFieldId, width);
	}

	/**
	 * 检查用户录入名称的时候，是不是有重复的
	 * 
	 * @param request
	 * @param type
	 *          类型 列表名称 ，form名称 ， 筛选方案名称， 视图方案名称
	 * @param name
	 *          要检查的名称
	 * @param id
	 *          修改的话，id
	 * @return
	 * @author jiangfeng
	 */
	@RequestMapping("/checknamevalidate.do")
	public  ActionResult checkNameValidate(String name, String id) {
		return gridSchemeService.checkNameValidate(name, id);
	}

}
