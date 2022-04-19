package com.jhopesoft.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.platform.service.AuditService;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@RestController
@RequestMapping("/platform/audit")
public class Audit {

	@Autowired
	private AuditService auditService;

	/**
	 * 取消一条记录的审核
	 * 
	 * @param moduleName
	 * @param recordId
	 * @return
	 */
	@RequestMapping(value = "/cancel.do")
	public ActionResult cancelAudit(String moduleName, String recordId) {
		try {
			return auditService.cancelAudit(moduleName, recordId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ActionResult(false, CommonUtils.getThrowableOriginalMessage(e));
		}
	}

	/**
	 * 取消一条记录的审核
	 * 
	 * @param moduleName
	 * @param recordId
	 * @return
	 */
	@RequestMapping(value = "/doaudit.do")
	public ActionResult doAudit(String moduleName, String recordId) {
		try {
			return auditService.doAudit(moduleName, recordId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ActionResult(false, CommonUtils.getThrowableOriginalMessage(e));
		}
	}

	/**
	 * 批量审核
	 * 
	 * @param moduleName
	 * @param ids
	 * @param titles
	 * @return
	 */
	@RequestMapping(value = "/doaudits.do")
	public ActionResult doAudits(String moduleName, String ids, String titles) {
		ActionResult result = new ActionResult();
		String[] idarray = ids.split(Constants.COMMA);
		String[] titlearray = titles.split("~~");
		List<String> okList = new ArrayList<String>(0);
		List<String> cancelList = new ArrayList<String>(0);
		for (int i = 0; i < idarray.length; i++) {
			try {
				ActionResult aresult = auditService.doAudit(moduleName, idarray[i]);
				if (aresult.getSuccess()) {
					okList.add(titlearray[i]);
				} else {
					cancelList.add(titlearray[i]);
				}
			} catch (Exception e) {
				e.printStackTrace();
				cancelList.add(titlearray[i]);
			}
		}
		JSONObject resultInfo = new JSONObject();
		resultInfo.put("success", okList);
		resultInfo.put("error", cancelList);
		result.setResultInfo(resultInfo);
		return result;
	}

	/**
	 * 设置记录的审核人员
	 * 
	 * @param moduleName
	 * @param recordId
	 * @param usercode
	 * @return
	 */
	@RequestMapping(value = "/setaudituser.do")
	public ActionResult setAuditUser(String moduleName, String recordId, String usercode) {
		try {
			return auditService.setAuditUser(moduleName, recordId, usercode);
		} catch (Exception e) {
			e.printStackTrace();
			return new ActionResult(false, CommonUtils.getThrowableOriginalMessage(e));
		}
	}

}
