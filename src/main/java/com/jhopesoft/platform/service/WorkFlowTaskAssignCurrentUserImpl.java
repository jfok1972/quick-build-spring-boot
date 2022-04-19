package com.jhopesoft.platform.service;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.utils.Constants;

/**
 * 
 * @author jiangfeng
 * 
 *         指定当前人员为流程审核人员，一般用于流程启动后的第一级，是录入--启动--审核第一级
 * 
 *         设计流程图的时候，选中UserTask--监听器--任务监听器--事件类型（创建）--监听器类型（代理表达式）
 *         --代理表达式（${workFlowTaskAssignCurrentUserImpl}）
 */

@Service(value = "workFlowTaskAssignCurrentUserImpl")
public class WorkFlowTaskAssignCurrentUserImpl implements TaskListener {

	private static final long serialVersionUID = -6239461320082291252L;

	@Autowired
	public RepositoryService repositoryService;

	@Autowired
	public HistoryService historyService;

	@Override
	public void notify(DelegateTask delegateTask) {
		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(delegateTask.getProcessInstanceId()).list();
		if (Constants.CREATE.equals(delegateTask.getEventName())) {
			if (list.size() == 0) {
				delegateTask.setAssignee(Local.getUserid());
			} else {
				// 回退过来，也指定第一个进行审批
				delegateTask.setAssignee(list.get(0).getAssignee());
			}
		}
	}

}
