package com.jhopesoft.platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.dao.entity.viewsetting.FovHomepagescheme;
import com.jhopesoft.platform.service.HomepageService;

/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/homepage")
public class Homepage {

	@Autowired
	private HomepageService homepageService;

	@RequestMapping(value = "/getinfo.do")
	public List<FovHomepagescheme> getHomepageInfo(String type) {
		return homepageService.getHomepageInfo(type);
	}

	@RequestMapping(value = "/setdefault.do")
	
	public ActionResult setDefault(String schemeid) {
		return homepageService.setUserDefault(schemeid);
	}

	@RequestMapping(value = "/remove.do")
	
	public ActionResult remove(String schemeid) {
		return homepageService.remove(schemeid);
	}

	@RequestMapping(value = "/add.do")
	
	public ActionResult add(String schemeid) {
		return homepageService.add(schemeid);
	}
}
