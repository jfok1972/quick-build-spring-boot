package com.jhopesoft.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.exception.WorkFlowException;
import com.jhopesoft.platform.service.EhcacheService;
import com.jhopesoft.platform.service.WorkFlowDesignService;

/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/workflowdesign")
public class WorkFlowDesign {

	@Autowired
	private WorkFlowDesignService workFlowDesignService;

	@Autowired
	private EhcacheService ehcacheService;

	@RequestMapping(value = "/deploy.do")
	public ActionResult deploy(String workflowid) {
		ActionResult result = null;
		try {
			result = workFlowDesignService.saveDeploy(workflowid);
			ehcacheService.clean();
		} catch (WorkFlowException e) {
			e.printStackTrace();
			result = new ActionResult(false, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = new ActionResult(false, e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/setvalid.do")
	public ActionResult setValid(String workflowid) {
		ActionResult result = null;
		try {
			result = workFlowDesignService.setValid(workflowid);
			ehcacheService.clean();
		} catch (Exception e) {
			e.printStackTrace();
			result = new ActionResult(false, e.getMessage());
		}
		return result;
	}

	/**
	 * 根据流程定义id,返回定义的svg信息
	 * 
	 * @param procDefId
	 * @return
	 */
	@RequestMapping(value = "/getsvg.do")
	public String getSvgFromProcDefId(String procDefId) {
		return workFlowDesignService.getSvgFromProcDefId(procDefId);
	}

}
