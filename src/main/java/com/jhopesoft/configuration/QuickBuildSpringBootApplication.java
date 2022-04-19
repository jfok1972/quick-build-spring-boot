package com.jhopesoft.configuration;
import com.jhopesoft.framework.context.contextAware.AppContextAware;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 *         spring boot 集成 Activiti 需要加入 (exclude =
 *         SecurityAutoConfiguration.class)
 * 
 *         为了解决异常 org.s.orm.jpa.EntityManagerHolder cannot be cast to
 *         org.s.orm.hibernate5.SessionHolder 需要加入 exclude =
 *         HibernateJpaAutoConfiguration.class
 */

@EnableCaching
@EnableTransactionManagement
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ServletComponentScan(basePackages = { "com.jhopesoft" })
@ComponentScan(basePackages = { "com.jhopesoft" })
@EntityScan(basePackages = { "com.jhopesoft" })
public class QuickBuildSpringBootApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(QuickBuildSpringBootApplication.class, args);
		AppContextAware.setStaticApplicationContext(ctx);
	}
}
