package com.jhopesoft.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


import com.alibaba.fastjson.JSONArray;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.platform.service.ChartSchemeService;
/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/chart")
public class ChartScheme {
  @Autowired
  private ChartSchemeService chartSchemeService;

  @RequestMapping(value = "/getschemes.do")
  
  public JSONArray getSchemes(String moduleName, String dataminingschemeid) {
    return chartSchemeService.getSchemes(moduleName, dataminingschemeid);
  }

  @RequestMapping(value = "/getscheme.do")
  
  public ActionResult getScheme(String schemeid, String viewschemeid, String userfilters) {
    return chartSchemeService.getScheme(schemeid, viewschemeid, userfilters);
  }

  @RequestMapping(value = "/addscheme.do")
  
  public ActionResult addScheme(String moduleName, String dataminingschemeid, String schemename, String groupname,
      String subname, String option) {
    return chartSchemeService.addScheme(moduleName, dataminingschemeid, schemename, groupname, subname, option);
  }

  @RequestMapping(value = "/editscheme.do")
  
  public ActionResult editScheme(String schemeid, String option) {
    return chartSchemeService.editScheme(schemeid, option);
  }

  @RequestMapping(value = "/getschemeoption.do")
  
  public ActionResult getSchemeOption(String schemeid) {
    return chartSchemeService.getSchemeOption(schemeid);
  }


  @RequestMapping(value = "/deletescheme.do")
  
  public ActionResult deleteScheme(String schemeid) {
    return chartSchemeService.deleteScheme(schemeid);
  }
}
