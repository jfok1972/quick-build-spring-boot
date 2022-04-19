package com.jhopesoft.framework.core.annotation;

import java.lang.annotation.*;

/**
 * 
 * 自定义注解拦截
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemLogs {

    String value() default "";

}