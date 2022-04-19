package com.jhopesoft.platform.logic.define;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

public interface WorkFlowInterface {

	/**
	 * 
	 * 工作流的接收任务时事件的处理
	 * 
	 * @param delegateTask
	 * @param moduleName
	 * @param recordId
	 * 
	 */
	public void workFlowNotify(DelegateTask delegateTask, String moduleName, String recordId);

	/**
	 * 
	 * 工作流的任务完成时事件的处理
	 * 
	 * @param delegateTask
	 * @param moduleName
	 * @param recordId
	 * 
	 */
	public void workFlowNotify(DelegateExecution delegateTask, String moduleName, String recordId);

	/**
	 * 
	 * 工作流的任务启动时的事件
	 * 
	 * @param objectName
	 * @param recordId
	 * 
	 */
	public void workFlowStart(String objectName, String recordId);

	/**
	 * 
	 * 工作流的任务被取消后的事件
	 * 
	 * @param objectName
	 * @param recordId
	 * 
	 */
	public void workFlowCancel(String objectName, String recordId);

	/**
	 * 
	 * 工作流的任务被暂停后的事件
	 * 
	 * @param objectName
	 * @param recordId
	 * 
	 */
	public void workFlowPause(String objectName, String recordId);

	/**
	 * 工作流的任务被暂停后的事件
	 * 
	 * @param objectName
	 * @param recordId
	 * @param taskId
	 * @param outgoingid
	 * @param outgoingname
	 * @param type
	 * @param content
	 * @param moduledata
	 */
	public void workFlowComplete(String objectName, String recordId, String taskId, String outgoingid,
			String outgoingname, String type, String content, String moduledata);

	/**
	 * 取得当前模块的工作流启动时的Process中的title值，如果是null，则用默认值
	 * 
	 * 默认值是"『" + dataObject.getTitle() + "』" + object.getTitle()
	 * 
	 * @param objectName
	 * @param recordId
	 * @return
	 */
	public String getWorkFlowProcessTitle(String objectName, String recordId);

}
