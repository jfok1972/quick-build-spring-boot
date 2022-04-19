package com.jhopesoft.platform.controller;

import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.ValueText;
import com.jhopesoft.platform.service.DataobjectFieldService;

/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/dataobjectfield")
public class DataobjectField {

	@Autowired
	private DataobjectFieldService service;

	@RequestMapping("/getexpression.do")
	public  JSONObject getDataFilterRoleLimit(String additionfieldid) {
		return service.getUserDefineFieldExpression(additionfieldid);
	}

	@RequestMapping("/updateexpression.do")
	public  ActionResult updateExpression(String additionfieldid, String expression) {

		return service.updateUserDefineFieldExpression(additionfieldid, expression);
	}

	@RequestMapping(value = "/createonetomanyfield.do", method = RequestMethod.POST)
	
	public ActionResult createOneToManyField(String fieldid) {
		ActionResult result = null;
		try {
			result = service.createOneToManyField(fieldid);
		} catch (PersistenceException e) {
			result = new ActionResult(false,
					e.getCause().getCause() != null ? e.getCause().getCause().getMessage() : e.getCause().getMessage());
		} catch (Exception e) {
			result = new ActionResult(false, e.getCause().getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/createmanytomanyfield.do", method = RequestMethod.POST)
	
	public ActionResult createManyToManyField(String fieldid1, String fieldid2, String linkedobjectid) {
		ActionResult result = null;
		try {
			result = service.createManyToManyField(fieldid1, fieldid2, linkedobjectid, null);
		} catch (PersistenceException e) {
			result = new ActionResult(false,
					e.getCause().getCause() != null ? e.getCause().getCause().getMessage() : e.getCause().getMessage());
		} catch (Exception e) {
			result = new ActionResult(false, e.getCause().getMessage());
		}
		return result;
	}

	/**
	 * 如果fieldid的字段是manytoone或者数据字典，则取得combodata后返回数据，否则返回null
	 * 
	 * @param fieldid
	 * @return
	 */
	@RequestMapping(value = "/fetchcombodata.do", method = RequestMethod.POST)
	
	public List<ValueText> getFieldComboData(String fieldid) {
		return service.getFieldComboData(fieldid);
	}

	/**
	 * 测试一个自定义字段是否能使用
	 * 
	 * @param fieldid
	 * @return
	 */
	@RequestMapping(value = "/testadditionfield.do")
	
	public ActionResult testAdditionField(String additionFieldId) {
		ActionResult result = new ActionResult();
		try {
			result = service.testAdditionField(additionFieldId);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setMsg(e.getMessage());
		}
		return result;
	}

}
