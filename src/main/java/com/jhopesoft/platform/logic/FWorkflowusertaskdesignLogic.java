package com.jhopesoft.platform.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.jhopesoft.framework.core.annotation.Module;
import com.jhopesoft.framework.core.objectquery.module.BaseModule;
import com.jhopesoft.framework.core.objectquery.module.ModuleHierarchyGenerate;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.system.FOrganization;
import com.jhopesoft.framework.dao.entity.workflow.FWorkflowdesign;
import com.jhopesoft.framework.dao.entity.workflow.FWorkflowusertaskdesign;
import com.jhopesoft.framework.exception.DataUpdateException;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.platform.logic.define.AbstractBaseLogic;

/**
 * 
 * @author jiangfeng
 *
 */
@Module
public class FWorkflowusertaskdesignLogic extends AbstractBaseLogic<FWorkflowusertaskdesign> {

	@Autowired
	private DaoImpl dao;

	@Autowired
	private RepositoryService repositoryService;

	/**
	 * 可以对审批和审核的人员进行设置,审核的时候,只能加入用户
	 */
	@Override
	public void beforeInsert(FWorkflowusertaskdesign inserted) {
		String objectid = inserted.getFDataobject().getObjectid();
		FDataobject dataobject = DataObjectUtils.getDataObject(objectid);
		// 审核设置的检查
		if (BooleanUtils.isTrue(dataobject.getHasaudit())) {
			inserted.setTaskid(null);
			inserted.setTaskname("审核");
			validateAudit(inserted);
			if (StringUtils.isEmpty(inserted.getTitle())) {
				updateAuditTitle(inserted);
			}
			updateOrgPath(inserted);
			return;
		}
		// 审批设置的检查
		validate(inserted);
		if (StringUtils.isEmpty(inserted.getTitle())) {
			updateTitle(inserted);
		}
		updateOrgPath(inserted);
		validateTaskName(inserted);
	}

	public void updateTitle(FWorkflowusertaskdesign bean) {
		FOrganization org = dao.findById(FOrganization.class, bean.getFOrganization().getOrgid());
		FDataobject object = dao.findById(FDataobject.class, bean.getFDataobject().getObjectid());
		bean.setTitle("(" + org.getOrgcode() + ")" + org.getOrgname() + "对 " + object.getTitle() + " "
				+ (bean.getTaskname() != null ? bean.getTaskname() : "")
				+ (bean.getTaskid() != null ? bean.getTaskid() : "") + " 的审批设置");
		if (bean.getTitle().length() > Constants.INT_200) {
			bean.setTitle(bean.getTitle().substring(0, Constants.INT_200));
		}
	}

	public void updateAuditTitle(FWorkflowusertaskdesign bean) {
		FOrganization org = dao.findById(FOrganization.class, bean.getFOrganization().getOrgid());
		FDataobject object = dao.findById(FDataobject.class, bean.getFDataobject().getObjectid());
		bean.setTitle("(" + org.getOrgcode() + ")" + org.getOrgname() + "对 " + object.getTitle() + " 的审核设置");
		if (bean.getTitle().length() > Constants.INT_200) {
			bean.setTitle(bean.getTitle().substring(0, Constants.INT_200));
		}
	}

	@Override
	public void beforeUpdate(String type, FWorkflowusertaskdesign updatedObject, FWorkflowusertaskdesign oldObject) {
		String objectid = updatedObject.getFDataobject().getObjectid();
		FDataobject dataobject = DataObjectUtils.getDataObject(objectid);
		// 审核设置的检查
		if (BooleanUtils.isTrue(dataobject.getHasaudit())) {
			updatedObject.setTaskid(null);
			updatedObject.setTaskname("审核");
			validateAudit(updatedObject);
			if (StringUtils.isEmpty(updatedObject.getTitle())) {
				updateAuditTitle(updatedObject);
			}
			updateOrgPath(updatedObject);
			return;
		}
		// 审批设置的检查
		if (StringUtils.isBlank(updatedObject.getTitle())) {
			updateTitle(updatedObject);
		}
		validate(updatedObject);
		updateOrgPath(updatedObject);
		// 修改的时候先不要验证名称了，不然尚未发布的流程将不能进行人员设计
		// validateTaskName(updatedObject);
	}

	public void validate(FWorkflowusertaskdesign record) {
		boolean cond = (record.getFUser() == null && record.getFRole() == null)
				|| (record.getFUser() != null && record.getFRole() != null);
		// 如果有附加设置，则可以不设置，附加设置放在remark中，使用ognl来进行操作
		if (cond && StringUtils.isBlank(record.getRemark())) {
			throw new DataUpdateException("FUser", "审批用户或审批角色必须选择一个！");
		}
		if (record.getTaskid() == null && record.getTaskname() == null) {
			throw new DataUpdateException("taskname", "用户任务name和用户任务id必须选择一个！");
		}
	}

	public void validateAudit(FWorkflowusertaskdesign record) {
		if (record.getFRole() != null || record.getFUser() == null) {
			throw new DataUpdateException("FUser", "审核时只能选择用户！");
		}
	}

	private void updateOrgPath(FWorkflowusertaskdesign updated) {
		if (StringUtils.isBlank(updated.getOrgpath())) {
			// 如果组织机构字段路径没有设置，那么就自动生成一下
			FDataobject dataobject = dao.findById(FDataobject.class, updated.getFDataobject().getObjectid());
			// 生成parent树，在里面找找有没有 组织机构的
			BaseModule baseModule = ModuleHierarchyGenerate.genModuleHierarchy(dataobject, "t_", false);
			baseModule.getAllParents().forEach((key, parent) -> {
				if (parent.getModule().getObjectname().equals(FOrganization.class.getSimpleName())) {
					// 在父节点中找到了 组织机构的模块
					updated.setOrgpath(parent.getFieldahead() + ".orgid");
				}
			});
		}
	}

	private boolean validateTaskName(FWorkflowusertaskdesign design) {
		List<String> names = getTaskNames(dao.findById(FDataobject.class, design.getFDataobject().getObjectid()));
		if (!names.contains(design.getTaskname())) {
			throw new DataUpdateException("taskname",
					"任务名称必须在以下列表中：" + String.join(Constants.COMMA, names.toArray(new String[names.size()])));
		}
		return true;
	}

	/**
	 * 返回 dataobject 当前激活的流程中的所有userTask的title
	 * 
	 * @param dataobject
	 */
	private List<String> getTaskNames(FDataobject dataobject) {
		FWorkflowdesign design = dao.findByPropertyFirst(FWorkflowdesign.class, Constants.OBJECTID,
				dataobject.getObjectid(), "latestversion", true);
		if (design == null) {
			throw new DataUpdateException("FDataobject", "没有找到可用的 " + dataobject.getTitle() + " 的审批流程定义记录！");
		}
		List<String> result = new ArrayList<String>();
		BpmnModel model = repositoryService.getBpmnModel(design.getProcDefId());
		if (model != null) {
			Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
			for (FlowElement e : flowElements) {
				if (e instanceof UserTask) {
					result.add(e.getName());
				}
			}
		}
		return result;
	}

}
