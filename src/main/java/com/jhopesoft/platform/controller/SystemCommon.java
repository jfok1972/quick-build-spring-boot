package com.jhopesoft.platform.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.ResultBean;
import com.jhopesoft.framework.bean.TreeNode;
import com.jhopesoft.framework.dao.entity.viewsetting.FovDataobjectwidget;
import com.jhopesoft.framework.dao.entity.viewsetting.FovHomepagescheme;
import com.jhopesoft.framework.interceptor.transcoding.RequestList;
import com.jhopesoft.framework.utils.Sm4Util;
import com.jhopesoft.framework.utils.TreeBuilder;
import com.jhopesoft.platform.service.LoginService;
import com.jhopesoft.platform.service.SystemCommonService;

/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/systemcommon")
public class SystemCommon {

	@Autowired
	private SystemCommonService service;

	/**
	 * 获取全部的(模块分组+模块)信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getmoduletree.do")
	public List<TreeNode> getModuleTree(String companyid) {
		return TreeBuilder.buildListToTree(service.getModuleTree(companyid));
	}

	/**
	 * 获取全部的(模块分组+模块)信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getcompanymoduletree.do")
	public List<Map<String, Object>> getCompanyModuleTree(String companyid) {
		return TreeBuilder.buildListToTree(service.getCompanyModuleTree(companyid));
	}

	/**
	 * 保存公司(模块分组+模块)信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/savecompanymodule.do")
	public ResultBean saveCompanyModule(@RequestList List<Map<String, Object>> datalist, String companyid) {
		ResultBean result = new ResultBean();
		try {
			result = service.saveCompanyModule(datalist, companyid);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		return result;
	} // 获取实体对象分组列表

	@RequestMapping(value = "/getobjectgroups.do")
	public JSONArray getObjectgGroups() throws SQLException {
		return service.getObjectgGroups();
	}

	/**
	 * 保存模块的帮助信息，markdown文本
	 * 
	 * @param moduleName
	 * @param text
	 * @return
	 */
	@RequestMapping(value = "/saveobjectmarkdown.do")
	public ActionResult saveObjectMarkDown(String moduleName, String text) {
		return service.saveObjectMarkDown(moduleName, text);
	}

	/**
	 * 执行前台发送的sql命令，有很大的安全风险，因此采用了加密的方式
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/executesql.do")
	public ActionResult executeSql(String sql) throws Exception {
		StringBuffer sb = new StringBuffer(LoginService.SM4KEY);
		return service.executeSql(Sm4Util.decrypt(sql, sb.reverse().toString()));
	}

	/**
	 * 获取 widget 的定义，可以在前台进行预览
	 * 
	 * @param widgetid
	 * @return
	 */
	@RequestMapping(value = "/widgetdefine.do")
	public FovDataobjectwidget getWidgetDefine(String widgetid) {
		return service.getWidgetDefine(widgetid);
	}

	/**
	 * 获取 widget 的定义，可以在前台进行预览
	 * 
	 * @param widgetid
	 * @return
	 */
	@RequestMapping(value = "/homepageschemedefine.do")
	public FovHomepagescheme getHomepageSchemeDefine(String homepageschemeid) {
		return service.getHomepageSchemeDefine(homepageschemeid);
	}

}
