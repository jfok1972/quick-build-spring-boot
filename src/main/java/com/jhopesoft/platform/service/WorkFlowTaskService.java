package com.jhopesoft.platform.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.el.UelExpressionCondition;
import org.activiti.engine.impl.form.FormPropertyImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.IdentityLinkType;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.dao.entity.workflow.ActHiProcinst;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Service
public class WorkFlowTaskService {

	@Autowired
	public RepositoryService repositoryService;

	@Autowired
	public TaskService taskService;

	@Autowired
	public RuntimeService runtimeService;

	@Autowired
	public FormService formService;

	@Autowired
	public DataObjectService dataObjectService;

	private static final String EXCLUSIVEGATEWAY = "exclusiveGateway";
	private static final String FORMDATA = "formdata";
	private static final String PROPERTIES = "properties";
	private static final String OUTGOING = "outgoing";
	private static final String DOCUMENTATION = "documentation";

	/** 根据 procdefid + taskkey 的一个缓存，用来保存每一个usertask的form和outgoing数据 */
	private static Map<String, JSONObject> userTaskInfos = new HashMap<String, JSONObject>();

	/**
	 * 更换一个审批任务的审批人员。
	 * 
	 * @param taskid
	 * @param assignid userid
	 */
	public void changeTaskAssign(String taskid, String assignid) {
		// 获取当前结点审批人 考虑到有多个审批人的情况 需要先查询出所有的审批人 所以涉及到identityLink
		List<String> userIds = taskService.getIdentityLinksForTask(taskid).stream().map(IdentityLink::getUserId)
				.distinct().collect(Collectors.toList());
		// 删除identityLink中的用户关系
		for (String userId : userIds) {
			taskService.deleteUserIdentityLink(taskid, userId, IdentityLinkType.CANDIDATE);
		}
		// 清空task中的assignee
		taskService.unclaim(taskid);
		// 当前节点审核人列表 清空申请人
		userIds.clear();
		// 当前节点审核人列表 追加流转人
		userIds.add(assignid);
		// 给当前任务节点分配审核人
		for (String userId : userIds) {
			taskService.setAssignee(taskid, userId);
		}
		Task task = taskService.createTaskQuery().taskId(taskid).singleResult();
		FUser user = Local.getDao().findById(FUser.class, assignid);
		ActHiProcinst procinst = Local.getDao().findById(ActHiProcinst.class, task.getProcessInstanceId());
		procinst.setCurrentAssignName(user.getUsername());
		procinst.setCurrentCandidateName(null);
		Local.getDao().update(procinst);
		FDataobject dataObject = DataObjectUtils.getDataObject(procinst.getObjectname());
		dataObjectService.saveOperateLog(dataObject, procinst.getBusinessKey(), procinst.getBusinessName(), "更改审批人员",
				task.getName() + "的审批人员更改为：" + user.getUsername());

	}

	/**
	 * 根据流程定义id和 taskkey来取得formproperty 和 usertask的出口流向
	 * 
	 * @param procdefid
	 * @param taskkey
	 * @return
	 */
	public JSONObject getTaskDefInfo(String procdefid, String taskkey) {

		String key = procdefid + "+" + taskkey;
		if (userTaskInfos.containsKey(key)) {
			return userTaskInfos.get(key);
		}
		JSONArray outgoing = new JSONArray();
		JSONArray formdata = new JSONArray();
		JSONObject result = new JSONObject();
		result.put(OUTGOING, outgoing);
		result.put(FORMDATA, formdata);
		BpmnModel model = repositoryService.getBpmnModel(procdefid);
		// usertask的定义
		UserTask userTask = (UserTask) model.getFlowElement(taskkey);
		for (FormProperty formProperty : userTask.getFormProperties()) {
			formdata.add(formProperty);
		}
		// 如果是用 bpmn.io设置的图，form和上面的不一样，需要使用下面的
		// id,label,type,defaultValue, 其他可以加在属性里面,也会转化成相应的id:value。
		/**
		 * defaultValue: "defaul", id: "sendPrice", label: "发送金额", properties: [{id:
		 * "hello", value: "hello的值"}, {id: "prop1", value: "prop1value"}], ---》》》
		 * hello:hello的值, prop1: prop1value，改成这样的了 type: "string"
		 */
		userTask.getExtensionElements().forEach((element, list) -> {
			if (FORMDATA.equals(element)) {
				list.forEach(action -> {
					action.getChildElements().forEach((n1, child) -> {
						child.forEach(formitem -> {
							JSONObject object = new JSONObject();
							formdata.add(object);
							formitem.getAttributes().forEach((name, avalue) -> {
								object.put(name, avalue.get(0).getValue());
							});
							formitem.getChildElements().forEach((name, props) -> {
								if (PROPERTIES.equals(name)) {
									props.forEach(list1 -> {
										list1.getChildElements().forEach((atrname, atrvalue) -> {
											atrvalue.forEach(e1 -> {
												JSONObject pobject = new JSONObject();
												e1.getAttributes().forEach((an, av) -> {
													pobject.put(av.get(0).getName(), av.get(0).getValue());
												});
												object.put(pobject.getString(Constants.ID), pobject.get("value"));
											});
										});
									});
								}
							});
						});
					});
				});
			}
		});

		// 如果当前usertask只有一个出口，那么计算一下下一个节点是不是 排他网关，如果是的话，
		// 查找排他网关的所有out连线条件里面是不是有 outgoingid 或 outgoingname,如果有的话，将out连线的名称作为按钮的名称。
		if (userTask.getOutgoingFlows().size() == 1) {
			SequenceFlow sequenceFlow = (SequenceFlow) userTask.getOutgoingFlows().get(0);
			// 连线出口指向的目标id
			String targetId = sequenceFlow.getTargetRef();
			FlowElement flowElement = model.getFlowElement(targetId);
			if (flowElement instanceof ExclusiveGateway) {
				ExclusiveGateway exclusiveGateway = (ExclusiveGateway) flowElement;
				List<SequenceFlow> gatewayOutList = exclusiveGateway.getOutgoingFlows();
				for (SequenceFlow flow : gatewayOutList) {
					String condition = flow.getConditionExpression();
					boolean cond = condition != null
							&& (condition.indexOf("outgoingid") > 0 || condition.indexOf("outgoingname") > 0);
					if (cond) {
						// 这个排他网关是用于控制前一个usertask的操作的。
						outgoing.add(getFlowJsonObject(flow));
					}
				}
			}
		}
		if (outgoing.size() == 0) {
			for (SequenceFlow flow : userTask.getOutgoingFlows()) {
				outgoing.add(getFlowJsonObject(flow));
			}
		}
		userTaskInfos.put(key, result);
		return result;
	}

	private JSONObject getFlowJsonObject(SequenceFlow flow) {
		JSONObject object = new JSONObject();
		object.put(Constants.ID, flow.getId());
		object.put(Constants.NAME, flow.getName());
		// 写在 元素文档中的 json 串
		String otherSetting = flow.getDocumentation();
		if (otherSetting != null) {
			object.put(DOCUMENTATION, otherSetting);
			JSONObject other = JSONObject.parseObject("{" + flow.getDocumentation() + "}");
			other.keySet().forEach(key -> {
				object.put(key, other.get(key));
			});
		}
		// 写在扩展 属性 中的值
		flow.getExtensionElements().keySet().forEach(key -> {
			flow.getExtensionElements().get(key).forEach(k -> {
				k.getChildElements().values().forEach(v -> {
					v.forEach(rec -> {
						Map<String, List<ExtensionAttribute>> attr = rec.getAttributes();
						object.put(attr.get("name").get(0).getValue(), attr.get("value").get(0).getValue());
					});
				});
			});
		});
		return object;
	}

	/**
	 * 根据taskid 获取当前task的所有出口线，取得名称的id值，用作审批时候的按钮，以确定流程走向
	 * 
	 * @param taskid
	 * @return
	 */
	@Deprecated
	public JSONArray getTaskOutGoing(String taskId) {
		JSONArray result = new JSONArray();

		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			return result;
		}
		// 流程定义的文件
		ProcessDefinitionEntity entity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(task.getProcessDefinitionId());

		// 找到executionid,而不是processinstanceid,不然如果是并行任务，就会找到并行的节点
		String executionId = task.getExecutionId();
		Execution execution = runtimeService.createExecutionQuery().executionId(executionId).singleResult();

		ActivityImpl activityImpl = entity.findActivity(execution.getActivityId());

		List<PvmTransition> list = activityImpl.getOutgoingTransitions();
		if (list.size() == 1) {
			// 如果只有一条线，那么计算一下下一个节点是不是 排他网关，如果是的话，
			// 查找排他网关的所有out连线条件里面是不是有 outgoingid 或 outgoingname,如果有的话，将out连线的名称作为按钮的名称。
			PvmTransition pvm = list.get(0);
			// 当前连线的下一个节点
			ActivityImpl impl = (ActivityImpl) pvm.getDestination();
			// 节点的类型是不是排他网关
			String type = impl.getProperties().get(Constants.TYPE).toString();
			if (EXCLUSIVEGATEWAY.equalsIgnoreCase(type)) {
				// 如果是排他网关，判断有每个outline的条件里面有没有包含 outgoingid 或 outgoingname
				List<PvmTransition> gatewayOutList = impl.getOutgoingTransitions();
				for (PvmTransition apvm : gatewayOutList) {
					UelExpressionCondition elcondition = (UelExpressionCondition) apvm.getProperty(Constants.CONDITION);
					if (elcondition != null) {
						// initialConditionExpression 是 protected 的字段
						String condition = (String) getObjectFieldValue(elcondition, "initialConditionExpression");
						if (condition != null) {
							condition = condition.toLowerCase();
							if (condition.indexOf("outgoingid") > 0 || condition.indexOf("outgoingname") > 0) {
								// 这个排他网关是用于控制前一个usertask的操作的。
								JSONObject object = new JSONObject();
								object.put(Constants.ID, apvm.getId());
								object.put(Constants.NAME, apvm.getProperty(Constants.NAME));
								object.put(DOCUMENTATION, apvm.getProperty(DOCUMENTATION));
								result.add(object);
							}
						}
					}
				}
			}
		}
		// 如果当前节点连线的下面排他网关不是此usertask控制的
		if (result.size() == 0) {
			for (PvmTransition apvm : list) {
				JSONObject object = new JSONObject();
				object.put(Constants.ID, apvm.getId());
				object.put(Constants.NAME, apvm.getProperty(Constants.NAME));
				object.put(DOCUMENTATION, apvm.getProperty(DOCUMENTATION));
				result.add(object);
			}
		}
		return result;
	}

	@Deprecated
	public JSONArray getFormData(String taskid) {
		JSONArray result = new JSONArray();
		TaskFormData formData = formService.getTaskFormData(taskid);
		for (org.activiti.engine.form.FormProperty property : formData.getFormProperties()) {
			result.add((FormPropertyImpl) property);
		}
		return result;
	}

	@Deprecated
	public JSONObject getTaskInfo(String taskid) {
		JSONObject result = new JSONObject();
		result.put(OUTGOING, getTaskOutGoing(taskid));
		result.put(FORMDATA, getFormData(taskid));
		return result;
	}

	/**
	 * 返回一个类的字段的值，可以是private和protected
	 * 
	 * @param object
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static Object getObjectFieldValue(Object object, String name) {
		Class<?> clazz = object.getClass();
		Field field = null;
		Field[] files = clazz.getDeclaredFields();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().toLowerCase().equals(name.toLowerCase())) {
				field = files[i];
				break;
			}
		}
		if (field == null) {
			return null;
		}
		field.setAccessible(true);
		Object childObj = null;
		try {
			childObj = field.get(object);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return childObj;
	}

}
