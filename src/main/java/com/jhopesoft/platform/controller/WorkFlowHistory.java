package com.jhopesoft.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


import com.jhopesoft.framework.bean.ActionResult;

import com.jhopesoft.platform.service.WorkFlowHistoryService;

/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/workflow/history")
public class WorkFlowHistory {

	@Autowired
	private WorkFlowHistoryService workFlowHistoryService;

	/**
	 * 取得模块记录的历史审核记录，用于显示记录审核历史图的数据
	 * 
	 * @param moduleName
	 * @param idvalue
	 * @return
	 */
	@RequestMapping(value = "/gethistoryinfo.do")
	
	public ActionResult getHistoryInfo(String moduleName, String id) {
		return workFlowHistoryService.getHistoryInfo(moduleName, id);
	}

}
