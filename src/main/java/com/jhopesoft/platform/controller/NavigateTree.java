package com.jhopesoft.platform.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.core.objectquery.navigate.NavigateGenerateService;

/**
 * 取得模块的控制树的值
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/navigatetree")
public class NavigateTree {

	@Resource
	private NavigateGenerateService navigateGenerateService;

	@RequestMapping(value = "/fetchnavigatedata.do", method = RequestMethod.GET)
	public  JSONObject getTreeRecords(String moduleName, String navigateschemeid,
			Boolean isContainNullRecord, Boolean cascading, String parentFilter, String sqlparamstr,
			HttpServletRequest request) {

		JSONObject sqlparam = null;
		if (sqlparamstr != null && sqlparamstr.length() > 0) {
			sqlparam = JSONObject.parseObject(sqlparamstr);
		}
		try {
			return navigateGenerateService.genNavigateTree(moduleName, navigateschemeid, parentFilter, cascading,
					isContainNullRecord, sqlparam);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
