package com.jhopesoft.configuration;

import com.jhopesoft.framework.context.contextAware.AppContextAware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.context.WebApplicationContext;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 *         生成 war 包时需要有此类
 * 
 */

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(QuickBuildSpringBootApplication.class);
	}

	@Override
	protected WebApplicationContext run(SpringApplication application) {
		WebApplicationContext webApplicationContext = (WebApplicationContext) application.run();
		AppContextAware.setStaticApplicationContext(webApplicationContext);
		return webApplicationContext;
	}

}
