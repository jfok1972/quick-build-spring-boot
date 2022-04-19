package com.jhopesoft.framework.context.contextAware;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

public class AppContextAware implements ApplicationContextAware{
	
	private static ApplicationContext appCtx;

	public void setApplicationContext(ApplicationContext context) {
		appCtx = context;
	}

	public static void setStaticApplicationContext(ApplicationContext context) {
		appCtx = context;
	}

	public static ApplicationContext getApplicationContext() {
		return appCtx;
	}
}