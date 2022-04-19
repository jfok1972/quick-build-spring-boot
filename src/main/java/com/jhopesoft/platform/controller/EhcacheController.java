package com.jhopesoft.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.platform.service.EhcacheService;

/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/ehcache")
public class EhcacheController {

	@Autowired
	private EhcacheService ehcacheService;

	/**
	 * 清除所有的二级缓存
	 * 
	 * @return
	 */
	@RequestMapping("clean.do")
	public  ActionResult clean() {
		return ehcacheService.clean();
	}
}
