package com.jhopesoft.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.platform.service.WorkFlowTaskService;

/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/workflow/task")
public class WorkFlowTask {

	@Autowired
	private WorkFlowTaskService workFlowTaskService;

	/**
	 * 取得流程定义中的某个usertask的信息，包括formproperty 和 outgoing
	 * 
	 * @param procdefid
	 * @param taskkey
	 * @return JSONObject
	 * 
	 */
	@RequestMapping(value = "/getdefinfo.do")

	public JSONObject getDefInfo(String procdefid, String taskkey) {
		return workFlowTaskService.getTaskDefInfo(procdefid, taskkey);
	}

	/**
	 * 取得这个任务的属性信息，包括formproperty 和 outgoing
	 * 
	 * @param taskid
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "/getinfowithtaskid.do")

	public JSONObject getInfo(String taskid) {
		return workFlowTaskService.getTaskInfo(taskid);
	}

	@Deprecated
	@RequestMapping(value = "/getoutgoingwithtaskid.do")

	public JSONArray getTaskOutGoing(String taskid) {
		return workFlowTaskService.getTaskOutGoing(taskid);
	}

	@RequestMapping(value = "/changeassign.do")
	public ActionResult changeTaskAssign(String taskid, String assignid) {
		ActionResult result = new ActionResult();
		try {
			workFlowTaskService.changeTaskAssign(taskid, assignid);
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setMsg(CommonUtils.getThrowableOriginalMessage(e));
		}
		return result;
	}

}
