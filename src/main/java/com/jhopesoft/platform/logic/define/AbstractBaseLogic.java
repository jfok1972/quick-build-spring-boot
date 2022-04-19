package com.jhopesoft.platform.logic.define;

import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;

import com.jhopesoft.framework.bean.PageInfo;
import com.jhopesoft.framework.core.objectquery.filter.UserNavigateFilter;
import com.jhopesoft.framework.core.objectquery.filter.UserParentFilter;
import com.jhopesoft.framework.core.objectquery.generate.SqlGenerate;
import com.jhopesoft.framework.dao.entity.attachment.FDataobjectattachment;

/**
 * 
 * @author jiangfeng
 *
 * @param <T>
 */
public abstract class AbstractBaseLogic<T> implements LogicInterface<T> {

	@Override
	public void beforeGenerateSelect(SqlGenerate generate) {

	}

	@Override
	public void beforeGenerateSelectCount(SqlGenerate generate) {

	}

	@Override
	public PageInfo<Map<String, Object>> afterFatchData(PageInfo<Map<String, Object>> pageInfo) {
		return pageInfo;
	}

	@Override
	public void beforeInsert(T inserted) {

	}

	@Override
	public void afterInsert(T inserted) {

	}

	@Override
	public void beforeUpdate(String type, T updatedObject, T oldObject) {

	}

	@Override
	public void afterUpdate(String type, T updatedObject, T oldObject) {

	}

	@Override
	public void beforeDelete(T deleted) {

	}

	@Override
	public void afterDelete(T deleted) {

	}

	@Override
	public Map<String, Object> getNewDefultValue(List<UserParentFilter> userParentFilters,
			List<UserNavigateFilter> navigateFilters) {
		return null;

	}

	@Override
	public void afterAuditing(String recordId) {

	}

	@Override
	public void afterCancelAuditing(String recordId) {

	}

	@Override
	public void workFlowNotify(DelegateTask delegateTask, String moduleName, String recordId) {

	}

	@Override
	public void workFlowNotify(DelegateExecution delegateTask, String moduleName, String recordId) {

	}

	@Override
	public void workFlowStart(String objectName, String recordId) {

	}

	@Override
	public void workFlowCancel(String objectName, String recordId) {

	}

	@Override
	public void workFlowPause(String objectName, String recordId) {

	}

	@Override
	public void workFlowComplete(String objectName, String recordId, String taskId, String outgoingid,
			String outgoingname, String type, String content, String moduledata) {

	}

	@Override
	public String getWorkFlowProcessTitle(String objectName, String recordId) {
		return null;
	}

	@Override
	public String getGridRecordExpandBody(String recordId) {
		return null;
	}

	@Override
	public void afterUploadAttachment(String recordId, FDataobjectattachment attachment) {
		return;
	}

	@Override
	public void afterPrintRecord(String recordId, String schemeId) {
		return;
	}

}
