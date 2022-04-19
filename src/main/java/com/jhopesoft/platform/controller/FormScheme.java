package com.jhopesoft.platform.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.ValueText;
import com.jhopesoft.platform.service.FormSchemeService;
/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/scheme/form")
public class FormScheme {

	@Resource
	private FormSchemeService formSchemeService;

	/**
	 * 保存用户设置的 列表 方案
	 * 
	 * @param request
	 * @param formSchemeId
	 * @param schemeDefine 传进来的所有column包括合并列头的定义
	 * @return
	 */

	@RequestMapping("/updatedetails.do")
	public  ActionResult updateFormSchemeDetails(HttpServletRequest request, String dataObjectId,
			String formSchemeId, String schemeDefine, String formSchemeName, Boolean mydefault, Boolean shareowner,
			Boolean shareall) {

		return formSchemeService.updateFormSchemeDetails(request, dataObjectId, formSchemeId, formSchemeName,
				schemeDefine, mydefault, shareowner, shareall);
	}

	@RequestMapping("/getdetails.do")
	public  JSONObject getFormSchemeDetails(HttpServletRequest request, String formSchemeId) {
		return formSchemeService.getFormSchemeDetails(request, formSchemeId);
	}

	@RequestMapping("/deletescheme.do")
	public  ActionResult deleteFormScheme(HttpServletRequest request, String schemeid) {
		return formSchemeService.deleteFormScheme(request, schemeid);
	}


  @RequestMapping("/getobjectschemename.do")
    public  List<ValueText> getObjectSchemename( String objectid) {
        return formSchemeService.getObjectSchemename(objectid);
    }
	
	@RequestMapping("/schemesaveas.do")
	public  ActionResult formSchemeSaveas(HttpServletRequest request, String schemeid, String schemename) {
		return formSchemeService.formSchemeSaveas(schemeid, schemename);
	}

	/**
	 * 检查用户录入名称的时候，是不是有重复的
	 * 
	 * @param request
	 * @param type 类型 列表名称 ，form名称 ， 筛选方案名称， 视图方案名称
	 * @param name 要检查的名称
	 * @param id 修改的话，id
	 * @return
	 * @author jiangfeng
	 */
	@RequestMapping("/checknamevalidate.do")
	public  ActionResult checkNameValidate(String name, String id) {
		return formSchemeService.checkNameValidate(name, id);
	}

}
