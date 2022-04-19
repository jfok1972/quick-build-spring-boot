package com.jhopesoft.platform.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jhopesoft.platform.service.SystemBaseCodeService;

/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/basecode")
public class SystemBaseCode {

	@Autowired
	private SystemBaseCodeService service;

	@RequestMapping(value = "/getviewlist.do")
	
	public List<Map<String, Object>> getViewList(String viewname, String ids, String idfield, String textfield,
			String orderbyfield) {
		return service.getViewList(viewname, ids, idfield, textfield, orderbyfield);
	}

}
