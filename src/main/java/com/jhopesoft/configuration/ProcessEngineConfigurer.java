package com.jhopesoft.configuration;

import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.stereotype.Component;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Component
public class ProcessEngineConfigurer implements ProcessEngineConfigurationConfigurer {

	private static final String SONGTI = "宋体";

	@Override
	public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
		processEngineConfiguration.setActivityFontName(SONGTI);
		processEngineConfiguration.setLabelFontName(SONGTI);
		processEngineConfiguration.setAnnotationFontName(SONGTI);
	}

}
