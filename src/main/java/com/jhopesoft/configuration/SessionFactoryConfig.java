package com.jhopesoft.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Component
@ConfigurationProperties(prefix = "spring.jpa.properties")
public class SessionFactoryConfig {

    private Map<String, String> hibernate = new HashMap<String, String>();

    public void setHibernate(Map<String, String> hibernate) {
        this.hibernate = hibernate;
    }

    /**
     * 根据 spring.jpa.properties 的属性值来生成 SessionFactory
     * 
     * 缓存也是在这里生成，由于hibernate是5.2的，因此不能加入pring-boot-starter-cache
     * 
     * @param dataSource
     * @return
     */
    @Bean(name = "sessionFactory")
    public SessionFactory getSessionFactory(DataSource dataSource) {
        LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource);
        builder.scanPackages("com.jhopesoft.**.entity");
        Properties properties = new Properties();
        for (String key : hibernate.keySet()) {
            properties.setProperty("hibernate." + key, hibernate.get(key));
        }
        builder.addProperties(properties);
        return builder.buildSessionFactory();
    }

    /**
     * 注入HibernateTransactionManager为transactionManager，否则spring boot
     * 自动生成的JpaTransactionManager在activiti中不能和hibernate的dao在同一个事务当中
     * 
     * @param sessionFactory
     * @return
     */
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    /**
     * 确保事务在当前请求的线程结束前都有效，否则在Controller中返回的json中如果有懒加载就会报无事务的错误。
     * 
     * @return
     */
    @Bean
    public FilterRegistrationBean<?> registerOpenEntityManagerInViewFilterBean() {
        FilterRegistrationBean<OpenSessionInViewFilter> registrationBean = new FilterRegistrationBean<OpenSessionInViewFilter>();
        OpenSessionInViewFilter filter = new OpenSessionInViewFilter();
        filter.setSessionFactoryBeanName("sessionFactory");
        registrationBean.setFilter(filter);
        registrationBean.setOrder(5);
        return registrationBean;
    }

}