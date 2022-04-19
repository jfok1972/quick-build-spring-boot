package com.jhopesoft.platform.controller;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.dao.entity.viewsetting.FovDataobjectassociate;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.platform.service.DataObjectService;
import com.jhopesoft.platform.service.ModuleAssociateService;

import ognl.OgnlException;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@RestController
@RequestMapping("/platform/scheme/associate")

public class ModuleAssociate {

	@Resource
	private ModuleAssociateService moduleAssociateService;

	@Autowired
	private DataObjectService dataobjectService;

	@RequestMapping("/savesetting.do")
	public ActionResult saveSetting(String associateid, String savestr) {

		try {
			dataobjectService.saveOrUpdate(FovDataobjectassociate.class.getSimpleName(), savestr, null, Constants.EDIT);
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | OgnlException e) {
			e.printStackTrace();
		}

		return new ActionResult();
	}

	@RequestMapping("/addsubmodule.do")
	public ActionResult addSubModule(String objectid, String fieldahead, String title, Integer orderno,
			Boolean subobjectnavigate, Boolean subobjectsouthregion, Boolean subobjecteastregion, String region) {
		return moduleAssociateService.AddSubModule(objectid, fieldahead, title, orderno, subobjectnavigate,
				subobjectsouthregion, subobjecteastregion, region);
	}

	@RequestMapping("/addform.do")
	public ActionResult addForm(String objectid, String formschemeid, String title, Integer orderno, Boolean usedfornew,
			Boolean usedforedit, String region) {
		return moduleAssociateService.AddForm(objectid, formschemeid, title, orderno,
				usedfornew == null ? false : usedfornew, usedforedit == null ? false : usedforedit, region);
	}

	@RequestMapping("/adduserdefine.do")
	public ActionResult addUserDefine(String objectid, String name, String region) {
		return moduleAssociateService.addUserDefine(objectid, name, region);
	}

	@RequestMapping("/addattachment.do")
	public ActionResult addAttachment(String objectid, String region) {
		return moduleAssociateService.addAttachment(objectid, region);
	}

	@RequestMapping("/addchart.do")
	public ActionResult addChart(String objectid, String region) {
		return moduleAssociateService.addChart(objectid, region);
	}

	@RequestMapping("/remove.do")
	public ActionResult remove(String detailid) {
		return moduleAssociateService.removeAssociatedetail(detailid);
	}

}
