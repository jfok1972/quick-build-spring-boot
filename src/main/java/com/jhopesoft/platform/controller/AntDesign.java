package com.jhopesoft.platform.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * 在将antDesign编译后的发布程序放在resources/static下以后，在直接访问内部页面如
 * 
 * http://localhost:8080/dashboard/analysis 时或者网页刷新时候，会找不到网址，用此类进行网址重定向。
 * 
 * 在application.yml中要加入配置项：spring.mvc.view.suffix: .html
 * 
 * @author 蒋锋 jfok1972@qq.com
 *
 */

@Controller
class AntDesign implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(value = "/error")
    public String getIndex() {
        return "index";
    }

}