package com.jhopesoft.framework.interceptor.transcoding;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented

/**
 *
 * @author 
 *  
 */

public @interface RequestBean {
	String value() default "_def_param_bean";
}