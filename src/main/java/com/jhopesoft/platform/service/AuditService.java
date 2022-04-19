package com.jhopesoft.platform.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.system.FOrganization;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.dao.entity.workflow.FWorkflowusertaskdesign;
import com.jhopesoft.framework.dao.entityinterface.AuditionInterface;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.ResultInfoUtils;
import com.jhopesoft.framework.utils.TypeChange;
import com.jhopesoft.platform.logic.define.LogicInterface;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Service
public class AuditService {

	@Autowired
	private DaoImpl dao;

	@Autowired
	private DataObjectService dataObjectService;

	/**
	 * 取消审核一条记录
	 * 
	 * @param moduleName
	 * @param recordId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionResult cancelAudit(String moduleName, String recordId) {
		FDataobject dataObject = DataObjectUtils.getDataObject(moduleName);
		Object entityBean = null;
		try {
			entityBean = dao.findById(Class.forName(dataObject.getClassname()), recordId);
		} catch (Exception e) {
			throw new RuntimeException("没找到名称为：" + dataObject.getClassname() + " 的bean文件！");
		}
		if (entityBean instanceof AuditionInterface) {
			AuditionInterface audition = (AuditionInterface) entityBean;
			// if (!Local.getUserid().equals(audition.getAuditingUserid())) {
			// 	if (!(Constants.ADMIN.equals(Local.getUsercode())
			// 			|| Constants.ADMINISTRATOR.equals(Local.getUsercode()))) {
			// 		throw new RuntimeException("只有审核的人员才能取消审核！");
			// 	}
			// }
			audition.setAuditingDate(null);
			audition.setAuditingRemark(null);
			dao.update(audition);
			dataObjectService.saveOperateLog(dataObject, recordId,
					dataObjectService.getRecordNameValue(dataObject, entityBean), "取消审核",
					"{ auditingDate: null, auditingRemark: null }");

			Object logic = Local.getLogicBean(dataObject.getObjectname() + "Logic");
			if (logic != null && logic instanceof LogicInterface) {
				((LogicInterface<Object>) logic).afterCancelAuditing(recordId);
			}
			ActionResult result = new ActionResult();
			result.setResultInfo(ResultInfoUtils.getResultInfoMessage());
			return result;
		} else {
			throw new RuntimeException(dataObject.getTitle() + " 的bean文件没有继续接口AuditionInterface！");
		}
	}

	/**
	 * 审核一条记录
	 * 
	 * @param moduleName
	 * @param recordId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionResult doAudit(String moduleName, String recordId) {
		FDataobject dataObject = DataObjectUtils.getDataObject(moduleName);
		Object entityBean = null;
		try {
			entityBean = dao.findById(Class.forName(dataObject.getClassname()), recordId);
		} catch (Exception e) {
			throw new RuntimeException("没找到名称为：" + dataObject.getClassname() + " 的bean文件！");
		}
		if (entityBean instanceof AuditionInterface) {
			AuditionInterface audition = (AuditionInterface) entityBean;
			if (!Local.getUserid().equals(audition.getAuditingUserid())) {
				throw new RuntimeException("你不是当前的记录审核人员，此异常已被记录！");
			}
			if (audition.getAuditingDate() != null) {
				throw new RuntimeException("此记录已被审核，不允许再审核！");
			}
			audition.setAuditingDate(new Date());
			audition.setAuditingRemark(null);
			dao.update(audition);
			dataObjectService.saveOperateLog(dataObject, recordId,
					dataObjectService.getRecordNameValue(dataObject, entityBean), "审核",
					"{ auditingDate: " + TypeChange.dateToString(new Date()) + ", auditingRemark: null }");

			Object logic = Local.getLogicBean(dataObject.getObjectname() + "Logic");
			if (logic != null && logic instanceof LogicInterface) {
				((LogicInterface<Object>) logic).afterAuditing(recordId);
			}
			ActionResult result = new ActionResult();
			result.setResultInfo(ResultInfoUtils.getResultInfoMessage());
			return result;
		} else {
			throw new RuntimeException(dataObject.getTitle() + " 的bean文件没有继续接口AuditionInterface！");
		}
	}

	/**
	 * 根据设置取得当前模块，当前记录的审核人员，如果未设置，返回null
	 * 
	 * @param dataObject
	 * @param bean
	 * @return
	 */
	public String getCanAuditUserid(FDataobject dataObject, Object bean) {
		List<FWorkflowusertaskdesign> designs = dao.findByProperty(FWorkflowusertaskdesign.class, Constants.OBJECTID,
				dataObject.getObjectid());
		if (designs.size() == 0) {
			return null;
		}
		String orgpath = null;
		for (FWorkflowusertaskdesign design : designs) {
			if (StringUtils.isNotEmpty(design.getOrgpath())) {
				orgpath = design.getOrgpath();
				break;
			}
		}
		// 如果没有一条记录设置部门，则返回null,审批的时候是返回异常
		if (orgpath == null) {
			return null;
		}
		String orgId = null;
		Object org = null;
		try {
			org = Ognl.getValue(orgpath, bean);
		} catch (OgnlException e) {
			throw new RuntimeException("在实体对象：『" + dataObject.getTitle() + "』的审核人员设置中，部门(组织机构)的访问字段设置错误！");
		}
		if (org != null) {
			if (org instanceof FOrganization) {
				orgId = ((FOrganization) org).getOrgid();
			} else {
				orgId = org.toString();
			}
		}
		// 找到当前模块记录对应的那一个工作流审批设置的记录
		FWorkflowusertaskdesign currentDesign = null;
		if (orgId != null) {
			// 找到工作流的审批人员设置中最接近 orgId的那一条记录。
			// 可以对一个模块设置多个审批人员记录，如 null,
			// 00，0010，001020，查找匹配的时候，从最末级的开始。如果都没找到到，则用缺省的未设置部门的设置
			// 先找有没有完全匹配的，如果没有的话，缩短一级，往上找，都没有就找null的
			FDataobject object = DataObjectUtils.getDataObject(FOrganization.class.getSimpleName());
			// 当前部门的级数
			int orgLevel = object._getCodeLevel(orgId);
			outer: for (int i = orgLevel; i >= 1; i--) {
				String s = orgId.substring(0, object._getCodeLevelLength(i));
				for (FWorkflowusertaskdesign design : designs) {
					if (design.getFOrganization() != null && s.equals(design.getFOrganization().getOrgid())) {
						currentDesign = design;
						break outer;
					}
				}
			}
		}
		if (currentDesign != null && currentDesign.getFUser() != null) {
			return currentDesign.getFUser().getUserid();
		}
		return null;
	}

	public ActionResult setAuditUser(String moduleName, String recordId, String usercode) {
		FDataobject dataObject = DataObjectUtils.getDataObject(moduleName);
		Object entityBean = null;
		try {
			entityBean = dao.findById(Class.forName(dataObject.getClassname()), recordId);
		} catch (Exception e) {
			throw new RuntimeException("没找到名称为：" + dataObject.getClassname() + " 的bean文件！");
		}
		if (entityBean instanceof AuditionInterface) {
			AuditionInterface audition = (AuditionInterface) entityBean;
			if (audition.getAuditingDate() != null) {
				throw new RuntimeException("当前记录已被审核，要换审核人员，请先取消审核！");
			}
			FUser user = dao.findById(FUser.class, usercode);
			if (user == null) {
				throw new RuntimeException("没有找到代码是 " + usercode + " 的用户，请先确认用户代码的正确性。");
			}
			audition.setAuditingUserid(user.getUserid());
			audition.setAuditingName(user.getUsername());
			dao.update(audition);
			dataObjectService.saveOperateLog(dataObject, recordId,
					dataObjectService.getRecordNameValue(dataObject, entityBean), "更改审核人员",
					"更改审核人员为：" + user.getUsername());
			ActionResult result = new ActionResult(true, user.getUsername());
			result.setResultInfo(ResultInfoUtils.getResultInfoMessage());
			return result;
		} else {
			throw new RuntimeException(dataObject.getTitle() + " 的bean文件没有继续接口AuditionInterface！");
		}
	}

}
