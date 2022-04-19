package com.jhopesoft.platform.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.alibaba.fastjson.JSONArray;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.UserCanSelectDataRole;
import com.jhopesoft.framework.core.annotation.SystemLogs;
import com.jhopesoft.framework.interceptor.transcoding.RequestList;
import com.jhopesoft.platform.service.UserFavouriteService;

/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/userfavourite")
public class UserFavourite {

	@Resource
	private UserFavouriteService userFavouriteService;

	@SystemLogs("修改用户的个性标签")
	@RequestMapping(value = "/updatesignature.do")
	
	public ActionResult updateSignature(@RequestParam(name = "text") String text) throws IOException {
		return userFavouriteService.updateSignature(text);
	}

	@SystemLogs("用户给自己加了一个标签tag")
	@RequestMapping(value = "/addtag.do")
	
	public ActionResult addTag(@RequestParam(name = "label") String label) throws IOException {
		return userFavouriteService.addTag(label);
	}

	@SystemLogs("用户删除了一个自己的标签tag")
	@RequestMapping(value = "/removetag.do")
	
	public ActionResult removeTag(@RequestParam(name = "label") String label) throws IOException {
		return userFavouriteService.removeTag(label);
	}

	@SystemLogs("用户选中或者取消了某个用户可选数据角色")
	@RequestMapping(value = "/toggledatarole.do")
	
	public ActionResult toggleDataRole(String roleid, boolean checked) {
		return userFavouriteService.toggleDataRole(roleid, checked);
	}

	@SystemLogs("用户保存可选数据角色的选中设置")
	@RequestMapping(value = "/updatedefaultdatarole.do")
	
	public ActionResult updateDefaultDataRole(
			@RequestList(clazz = UserCanSelectDataRole.class) List<UserCanSelectDataRole> rolestates) {
		return userFavouriteService.updateDefaultDataRole(rolestates);
	}

	@SystemLogs("用户重置可选数据角色的选中设置")
	@RequestMapping(value = "/resetdefaultdatarole.do")
	
	public ActionResult resetDefaultDataRole() {
		return userFavouriteService.resetDefaultDataRole();
	}

	@SystemLogs("用户设置缺省的列表方案")
	@RequestMapping(value = "/setdefaultgridscheme.do")
	
	public ActionResult setDefaultGridScheme(String schemeid) {
		return userFavouriteService.setDefaultGridScheme(schemeid);
	}

	@SystemLogs("用户设置缺省的筛选方案")
	@RequestMapping(value = "/setdefaultfilterscheme.do")
	
	public ActionResult setDefaultFilterScheme(String schemeid) {
		return userFavouriteService.setDefaultFilterScheme(schemeid);
	}

	@SystemLogs("用户设置缺省的导航方案")
	@RequestMapping(value = "/setdefaultnavigatescheme.do")
	
	public ActionResult setDefaultNavigateScheme(String schemeid) {
		return userFavouriteService.setDefaultNavigateScheme(schemeid);
	}

	@SystemLogs("读取用户收藏的模块")
	@RequestMapping(value = "/getuserobjects.do")
	
	public JSONArray getUserObject() {
		return userFavouriteService.getUserObjects();
	}

	@SystemLogs("设置用户收藏的模块")
	@RequestMapping(value = "/adduserobject.do")
	
	public ActionResult setUserObject(String objectid) {
		return userFavouriteService.addUserObject(objectid);
	}

	@SystemLogs("取消用户收藏的模块")
	@RequestMapping(value = "/removeuserobject.do")
	
	public ActionResult removeUserObject(String objectid) {
		return userFavouriteService.removeUserObject(objectid);
	}

	@SystemLogs("设置用户收藏的模块数据分析")
	@RequestMapping(value = "/adduserdatamining.do")
	
	public ActionResult setUserObjectDatamining(String objectid) {
		return userFavouriteService.addUserObjectDatamining(objectid);
	}

	@SystemLogs("取消用户收藏的模块数据分析")
	@RequestMapping(value = "/removeuserdatamining.do")
	
	public ActionResult removeUserObjectDatamining(String objectid) {
		return userFavouriteService.removeUserObjectDatamining(objectid);
	}

	@SystemLogs("设置用户收藏的模块方案")
	@RequestMapping(value = "/addusermodulescheme.do")
	
	public ActionResult setUserModuleScheme(String moduleschemeid) {
		return userFavouriteService.addUserModuleScheme(moduleschemeid);
	}

	@SystemLogs("取消用户收藏的模块方案")
	@RequestMapping(value = "/removeusermodulescheme.do")
	
	public ActionResult removeUserModuleScheme(String moduleschemeid) {
		return userFavouriteService.removeUserModuleScheme(moduleschemeid);
	}

	@SystemLogs("保存用户的模块module设置")
	@RequestMapping(value = "/savemodulesetting.do")
	
	public ActionResult saveModuleSetting(String objectid, String gridType, String param, boolean moduleDefault)
			throws IOException {
		return userFavouriteService.saveModuleSetting(objectid, gridType, param, moduleDefault);
	}

	@SystemLogs("清除用户的模块module设置")
	@RequestMapping(value = "/clearmodulesetting.do")
	
	public ActionResult clearModuleSetting(String objectid, String gridType, String clearType) throws IOException {
		return userFavouriteService.clearModuleSetting(objectid, gridType, clearType);
	}

	@SystemLogs("保存用户的模块表单设置")
	@RequestMapping(value = "/saveformsetting.do")
	
	public ActionResult saveFormSetting(String objectid, String formType, String param, boolean formDefault)
			throws IOException {
		return userFavouriteService.saveFormSetting(objectid, formType, param, formDefault);
	}

	@SystemLogs("清除用户的模块表单设置")
	@RequestMapping(value = "/clearformsetting.do")
	
	public ActionResult clearFormSetting(String objectid, String formType, String clearType) throws IOException {
		return userFavouriteService.clearFormSetting(objectid, formType, clearType);
	}

	@SystemLogs("保存用户的数据分析设置")
	@RequestMapping(value = "/savedataminingsetting.do")
	
	public ActionResult saveDataminingSetting(String objectid, String dataminingType, String param,
			boolean dataminingDefault) throws IOException {
		return userFavouriteService.saveDataminingSetting(objectid, dataminingType, param, dataminingDefault);
	}

	@SystemLogs("清除用户的数据分析设置")
	@RequestMapping(value = "/cleardataminingsetting.do")
	
	public ActionResult clearDataminingSetting(String objectid, String dataminingType, String clearType)
			throws IOException {
		return userFavouriteService.clearDataminingSetting(objectid, dataminingType, clearType);
	}

}
