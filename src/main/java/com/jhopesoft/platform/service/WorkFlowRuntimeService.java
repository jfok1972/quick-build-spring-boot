package com.jhopesoft.platform.service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.ResultBean;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.limit.FRole;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.dao.entity.viewsetting.FovFormscheme;
import com.jhopesoft.framework.dao.entity.viewsetting.FovFormschemedetail;
import com.jhopesoft.framework.dao.entity.workflow.ActHiProcinst;
import com.jhopesoft.framework.dao.entity.workflow.FWorkflowdesign;
import com.jhopesoft.framework.exception.WorkFlowException;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.DateUtils;
import com.jhopesoft.framework.utils.ParentChildFieldUtils;
import com.jhopesoft.framework.utils.ResultInfoUtils;
import com.jhopesoft.platform.logic.define.LogicInterface;

import ognl.OgnlException;

/**
 * 
 * @author ?????? jfok1972@qq.com
 * 
 */

@Service
public class WorkFlowRuntimeService {

	@Autowired
	private DaoImpl dao;

	@Autowired
	private DataObjectService dataObjectService;

	@Autowired
	public HistoryService historyService;

	@Autowired
	public RuntimeService runtimeService;

	@Autowired
	public IdentityService identityService;

	@Autowired
	public TaskService taskService;

	@Autowired
	public ProcessEngineConfiguration processEngineConfiguration;

	@Autowired
	public RepositoryService repositoryService;

	@Autowired
	public ProcessEngine processEngine;

	/**
	 * ?????????????????????????????????????????????????????????
	 * 
	 * @param objectName ????????????,??????proc_def_key
	 * @param id         ????????????id
	 * @return
	 */
	public boolean isProcessInstanceStart(String objectName, String id) {
		// ?????????????????????????????????,????????????true
		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
				.processDefinitionKey(objectName).processInstanceBusinessKey(id).singleResult();
		return historicProcessInstance != null;

	}

	public HistoricProcessInstance getProcessInstance(String objectName, String id) {
		// ?????????????????????????????????,????????????true
		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
				.processDefinitionKey(objectName).processInstanceBusinessKey(id).singleResult();
		return historicProcessInstance;
	}

	/**
	 * ????????????????????????????????????????????????????????????????????????
	 * 
	 * @param objectName
	 * @return
	 */
	public String getValidProcessInstanceById(String objectName) {
		List<FWorkflowdesign> workflowdesigns = dao.findByProperty(FWorkflowdesign.class, "FDataobject.objectid",
				objectName, "latestversion", true);
		if (workflowdesigns.size() != 1) {
			FDataobject object = DataObjectUtils.getDataObject(objectName);
			if (workflowdesigns.size() == 0) {
				throw new WorkFlowException("?????????" + object.getTitle() + "???????????????????????????????????????");
			} else {
				throw new WorkFlowException("?????????" + object.getTitle() + "??????????????????????????????????????????");
			}
		} else {
			return workflowdesigns.get(0).getProcDefId();
		}
	}

	/**
	 * ?????????????????????,?????????????????????????????????
	 * 
	 * @param objectName ????????????,??????proc_def_key
	 * @param id         ????????????id
	 * @param name       ?????????????????????
	 * @param processInstanceId ???????????????????????????id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionResult startProcessInstance(String objectName, String id, String name, String processInstanceId)
			throws WorkFlowException {
		// ???????????????????????????????????????
		ActionResult result = new ActionResult();
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceBusinessKey(id, objectName).singleResult();
		if (processInstance != null) {
			if (processInstance.isSuspended()) {
				runtimeService.activateProcessInstanceById(processInstance.getId());
				return result;
			} else {
				throw new WorkFlowException("???" + name + "?????????????????????????????????");
			}
		}
		// ?????????????????????????????????
		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
				.processDefinitionKey(objectName).processInstanceBusinessKey(id).singleResult();
		if (historicProcessInstance != null) {
			throw new WorkFlowException("???" + name + "???????????????????????????");
		}
		try {
			identityService.setAuthenticatedUserId(Local.getUserid());
			// ???????????????????????????title????????????
			Object logic = Local.getLogicBean(objectName + "Logic");
			if (logic != null && logic instanceof LogicInterface) {
				String title = ((LogicInterface<Object>) logic).getWorkFlowProcessTitle(objectName, id);
				if (StringUtils.isNotBlank(title)) {
					name = title;
				}
			}
			Map<String, Object> variables = getVariables(objectName, id);
			// ????????????????????????????????????????????????????????????
			variables.put("BUSINESS_NAME_", name);
			// ?????????????????????????????????
			if (processInstanceId == null) {
				processInstanceId = getValidProcessInstanceById(objectName);
			}
			ProcessInstance instance = runtimeService.startProcessInstanceById(processInstanceId, id, variables);
			// ???????????????????????????
			// ProcessInstance instance =
			// runtimeService.startProcessInstanceByKey(objectName, id, variables);
			ActHiProcinst procinst = dao.findById(ActHiProcinst.class, instance.getId());
			FDataobject dataObject = DataObjectUtils.getDataObject(objectName);
			procinst.setObjectid(dataObject.getObjectid());
			procinst.setObjectname(dataObject.getObjectname());
			procinst.setObjecttitle(dataObject.getTitle());
			procinst.setStartUserName(Local.getUsername());
			procinst.setBusinessTitle(name);
			procinst.setBusinessName(name);
			if (logic != null && logic instanceof LogicInterface) {
				((LogicInterface<Object>) logic).workFlowStart(objectName, id);
			}
			dao.update(procinst);
			updateCurrentAssignName(instance.getId());
		} catch (ActivitiException e) {
			if (e.getCause() != null) {
				throw new WorkFlowException(
						e.getCause().getMessage().replaceFirst("Exception while invoking TaskListener:", ""));
			} else {
				throw new WorkFlowException("??????????????????????????????????????????????????????????????????");
			}
		}
		return result;
	}

	/**
	 * ????????????????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param processInstanceId
	 */
	public void updateCurrentAssignName(String processInstanceId) {
		ActHiProcinst procinst = dao.findById(ActHiProcinst.class, processInstanceId);
		procinst.setCurrentAssignName(null);
		procinst.setCurrentCandidateName(null);

		// ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
		// ???????????????task????????????????????????????????????????????????
		for (Task task : tasks) {
			if (StringUtils.isNotEmpty(task.getAssignee())) {
				FUser user = dao.findById(FUser.class, task.getAssignee());
				procinst.setCurrentAssignName((procinst.getCurrentAssignName() == null ? ""
						: procinst.getCurrentAssignName() + Constants.COMMA)
						+ (user != null ? user.getUsername() : task.getAssignee()));

			} else {
				// ?????? assignee ???null,????????????????????????????????????
				List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
				for (IdentityLink link : identityLinks) {
					if ("candidate".equalsIgnoreCase(link.getType())) {
						// ????????????????????????
						if (link.getUserId() != null) {
							FUser user = dao.findById(FUser.class, link.getUserId());
							if (user != null && BooleanUtils.isTrue(user.getIsvalid())
									&& BooleanUtils.isTrue(user.getFPersonnel().getIsvalid())) {
								procinst.setCurrentCandidateName((procinst.getCurrentCandidateName() == null ? ""
										: procinst.getCurrentCandidateName() + Constants.COMMA)
										+ (user != null ? user.getUsername() : link.getUserId()));
							}
						} else if (link.getGroupId() != null) {
							// ?????????????????????
							FRole role = dao.findById(FRole.class, link.getGroupId());
							if (role != null) {
								role.getFUserroles().forEach(userRole -> {
									FUser user = userRole.getFUser();
									if (BooleanUtils.isTrue(user.getIsvalid())
											&& BooleanUtils.isTrue(user.getFPersonnel().getIsvalid())) {
										procinst.setCurrentCandidateName(
												(procinst.getCurrentCandidateName() == null ? ""
														: procinst.getCurrentCandidateName() + Constants.COMMA)
														+ user.getUsername());
									}
								});
							}
						}
					}
				}
			}
		}
		dao.update(procinst);
	}

	/**
	 * ???form?????????????????????????????????????????????formtype='workflow'
	 * 
	 * @param objectName
	 * @param id
	 * @return
	 */
	public Map<String, Object> getVariables(String objectName, String id) {
		Map<String, Object> variables = new HashMap<String, Object>(0);
		FDataobject dataobject = DataObjectUtils.getDataObject(objectName);
		for (FovFormscheme scheme : dataobject.getFovFormschemes()) {
			// ??????form??????????????????????????????????????????
			if (StringUtils.isNotBlank(scheme.getFormtype()) && scheme.getFormtype().equalsIgnoreCase("workflow")) {
				Map<String, Object> moduleRecord = dataObjectService.getObjectRecordMap(objectName, id);
				Set<FovFormschemedetail> fields = scheme._getFields();
				for (FovFormschemedetail field : fields) {
					String fieldname = ParentChildFieldUtils.generateFieldName(field);
					Object value = moduleRecord.get(fieldname);
					if (value != null) {
						String classname = value.getClass().getSimpleName().toLowerCase();
						if (Constants.DOUBLE.equals(classname)) {
							value = ((Double) value).doubleValue();
						} else if (Constants.FLOAT.equals(classname)) {
							value = ((Float) value).doubleValue();
						} else if (Constants.BYTE.equals(classname)) {
							value = ((Byte) value).intValue();
						} else if (Constants.SHORT.equals(classname)) {
							value = ((Short) value).intValue();
						} else if (Constants.INTEGER.equals(classname)) {
							value = ((Integer) value).intValue();
						} else if (Constants.LONG.equals(classname)) {
							value = ((Long) value).intValue();
						} else if (Constants.BIGDECIMAL.equals(classname)) {
							value = ((java.math.BigDecimal) value).doubleValue();
						} else if (Constants.BOOLEAN.equals(classname)) {
							value = ((Boolean) value).booleanValue();
						} else if (Constants.TIMESTAMP.equals(classname)) {
							value = ((java.sql.Timestamp) value);
						} else if (Constants.DATE.equals(classname)) {
							// Java.sql.date ??? java.util.date
							value = ((java.util.Date) value);
						}
					}
					variables.put(fieldname, value);
				}
				break;
			}
		}
		return variables;
	}

	/**
	 * ?????????????????????
	 * 
	 * @param objectName ????????????,??????proc_def_key
	 * @param id         ????????????id
	 * @param name       ?????????????????????
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResultBean cancelProcessInstance(String objectName, String id, String name) throws WorkFlowException {
		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
				.processDefinitionKey(objectName).processInstanceBusinessKey(id).singleResult();
		if (historicProcessInstance == null) {
			throw new WorkFlowException("???" + name + "??????????????????????????????");
		}
		ActHiProcinst procinst = Local.getDao().findById(ActHiProcinst.class, historicProcessInstance.getId());
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(historicProcessInstance.getId()).singleResult();
		if (processInstance != null) {
			runtimeService.deleteProcessInstance(historicProcessInstance.getId(), null);
		}
		historyService.deleteHistoricProcessInstance(historicProcessInstance.getId());
		Object logic = Local.getLogicBean(objectName + "Logic");
		if (logic != null && logic instanceof LogicInterface) {
			((LogicInterface<Object>) logic).workFlowCancel(objectName, id);
		}
		FDataobject dataObject = DataObjectUtils.getDataObject(procinst.getObjectname());
		dataObjectService.saveOperateLog(dataObject, procinst.getBusinessKey(), procinst.getBusinessName(), "??????????????????",
				null);
		ResultBean result = new ResultBean();
		result.setResultInfo(ResultInfoUtils.getResultInfoMessage());
		return result;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param objectName
	 * @param id
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionResult pauseProcessInstance(String objectName, String id, String name) throws WorkFlowException {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceBusinessKey(id, objectName).singleResult();
		if (processInstance != null) {
			if (processInstance.isSuspended()) {
				throw new WorkFlowException("???" + name + "??????????????????????????????");
			} else {
				runtimeService.suspendProcessInstanceById(processInstance.getId());
				Object logic = Local.getLogicBean(objectName + "Logic");
				if (logic != null && logic instanceof LogicInterface) {
					((LogicInterface<Object>) logic).workFlowPause(objectName, id);
				}
			}
		} else {
			HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
					.processDefinitionKey(objectName).processInstanceBusinessKey(id).singleResult();
			if (historicProcessInstance != null) {
				throw new WorkFlowException("???" + name + "???????????????????????????");
			} else {
				throw new WorkFlowException("???" + name + "??????????????????????????????");
			}
		}
		return new ActionResult();
	}

	/**
	 * ?????????????????????????????????
	 * 
	 * @param objectName
	 * @param id
	 * @param name
	 * @param outgoingid   ??????????????????id
	 * @param outgoingname ??????????????????name
	 * @param taskId
	 * @param moduledata   ???????????????????????????
	 * @return
	 * @throws OgnlException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public ResultBean completeProcessTask(String objectName, String id, String name, String taskId, String outgoingid,
			String outgoingname, String type, String content, String moduledata)
			throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, OgnlException {
		ResultBean result = new ResultBean();
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new WorkFlowException("???" + name + "??????????????????????????????????????????????????????????????????");
		}
		if (task.getAssignee() == null) {
			// ??????????????????????????????????????????
			taskService.claim(taskId, Local.getUserid());
		} else {
			if (!task.getAssignee().equals(Local.getUserid())) {
				throw new WorkFlowException("???" + name + "??????????????????????????????????????????????????????????????????");
			}
		}
		if (StringUtils.isNotEmpty(moduledata)) {
			result = dataObjectService.saveOrUpdate(objectName, moduledata, null, Constants.APPROVEEDIT);
			// ?????????????????????????????????????????????????????????????????????????????????????????????
			Map<String, Object> variables = getVariables(objectName, id);
			// ???????????????????????????????????????????????????????????????
			taskService.setVariables(taskId, variables);
		}
		taskService.addComment(taskId, task.getProcessInstanceId(), type, content);
		Map<String, Object> variables = new HashMap<String, Object>(0);
		variables.put("outgoingid", outgoingid);
		// ????????????????????????????????????????????????
		variables.put("outgoingname", outgoingname);
		taskService.complete(taskId, variables);

		// ??????????????????comment ????????? acthiprocinst??????????????????
		// ????????????????????????????????????????????????
		ActHiProcinst procinst = dao.findById(ActHiProcinst.class, task.getProcessInstanceId());
		String message = task.getName() + "|" + Local.getUsername() + "|" + type + "|"
				+ DateUtils.format(Constants.DATE_TIME_FORMAT) + "|" + (content == null ? "" : content);
		if (StringUtils.isBlank(procinst.getCompleteTaskInfo())) {
			procinst.setCompleteTaskInfo(message);
		} else {
			procinst.setCompleteTaskInfo(procinst.getCompleteTaskInfo() + " ||| " + message);
		}
		dao.update(procinst);
		updateCurrentAssignName(task.getProcessInstanceId());

		Object logic = Local.getLogicBean(objectName + "Logic");
		if (logic != null && logic instanceof LogicInterface) {
			((LogicInterface<Object>) logic).workFlowComplete(objectName, id, taskId, outgoingid, outgoingname, type,
					content, moduledata);
		}
		result.setResultInfo(ResultInfoUtils.getResultInfoMessage());
		return result;
	}

	public ActionResult claimProcessTask(String objectName, String id, String name, String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new WorkFlowException("???" + name + "??????????????????????????????????????????????????????????????????");
		}
		if (task.getAssignee() != null) {
			if (task.getAssignee().equals(Local.getUserid())) {
				throw new WorkFlowException("???" + name + "????????????????????????????????????????????????????????????");
			} else {
				throw new WorkFlowException("???" + name + "??????????????????????????????????????????????????????????????????");
			}
		}
		taskService.claim(taskId, Local.getUserid());
		updateCurrentAssignName(task.getProcessInstanceId());
		return new ActionResult();
	}

	public ActionResult unclaimProcessTask(String objectName, String id, String name, String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new WorkFlowException("???" + name + "??????????????????????????????????????????????????????????????????");
		}
		if (task.getAssignee() == null) {
			throw new WorkFlowException("???" + name + "?????????????????????????????????????????????????????????");
		}
		taskService.claim(taskId, null);
		updateCurrentAssignName(task.getProcessInstanceId());
		return new ActionResult();
	}

	public void getInstanceDiagram(String processInstanceId) throws IOException {
		// ????????????????????????
		HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		// ???????????????
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
		processEngineConfiguration = processEngine.getProcessEngineConfiguration();
		Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);

		ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
		ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processInstance.getProcessDefinitionId());

		List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstanceId).list();
		// ????????????id??????
		List<String> highLightedActivitis = new ArrayList<String>();

		// ????????????id??????
		List<String> highLightedFlows = getHighLightedFlows(definitionEntity, highLightedActivitList);

		for (HistoricActivityInstance tempActivity : highLightedActivitList) {
			String activityId = tempActivity.getActivityId();
			highLightedActivitis.add(activityId);
		}

		// ???????????????????????????????????????????????????
		InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, Constants.PNG, highLightedActivitis,
				highLightedFlows, "??????", "??????", null, null, 2.0);
		// ???????????????????????????????????????
		// ?????????????????????????????????
		byte[] b = new byte[1024];
		int len;
		Local.getResponse().addHeader("Content-Disposition", "inline");
		Local.getResponse().setContentLength(imageStream.available());
		Local.getResponse().setContentType("image/png;charset=utf-8");
		while ((len = imageStream.read(b, 0, Constants.INT_1024)) != -1) {
			Local.getResponse().getOutputStream().write(b, 0, len);
		}

	}

	/**
	 * ????????????????????????
	 * 
	 * @param processDefinitionEntity
	 * @param historicActivityInstances
	 * @return
	 */
	private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinitionEntity,
			List<HistoricActivityInstance> historicActivityInstances) {
		// ????????????????????????flowId
		List<String> highFlows = new ArrayList<String>();
		for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
			// ?????????????????????????????????
			ActivityImpl activityImpl = processDefinitionEntity
					.findActivity(historicActivityInstances.get(i).getActivityId());
			// ?????????????????????????????????
			List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();
			// ?????????????????????????????????????????????
			ActivityImpl sameActivityImpl1 = processDefinitionEntity
					.findActivity(historicActivityInstances.get(i + 1).getActivityId());
			// ????????????????????????????????????????????????????????????
			sameStartTimeNodes.add(sameActivityImpl1);
			for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
				// ?????????????????????
				HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);
				// ?????????????????????
				HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);
				if (activityImpl1.getStartTime().equals(activityImpl2.getStartTime())) {
					// ???????????????????????????????????????????????????????????????
					ActivityImpl sameActivityImpl2 = processDefinitionEntity
							.findActivity(activityImpl2.getActivityId());
					sameStartTimeNodes.add(sameActivityImpl2);
				} else {
					// ????????????????????????
					break;
				}
			}
			// ?????????????????????????????????
			List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();
			for (PvmTransition pvmTransition : pvmTransitions) {
				// ???????????????????????????
				ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition.getDestination();
				// ?????????????????????????????????????????????????????????????????????????????????id?????????????????????
				if (sameStartTimeNodes.contains(pvmActivityImpl)) {
					highFlows.add(pvmTransition.getId());
				}
			}
		}
		return highFlows;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param processinstanceid
	 * @return
	 */
	public List<Map<String, Object>> getCommentList(String processinstanceid) {
		String sql = "select a.type_ as type,a.time_ as times, a.message_ as content, b.act_name_ as nodename, "
				+ "  c.userid , c.username, b.start_time_ as starttime, b.end_time_ as endtime"
				+ " from act_hi_comment a " + " left join act_hi_actinst b on a.task_id_ = b.task_id_ "
				+ " left join f_user c on b.assignee_ = c.userid" + " where a.proc_inst_id_ = '" + processinstanceid
				+ "' " + " order by a.time_";
		return dao.executeSQLQuery(sql);
	}

}
