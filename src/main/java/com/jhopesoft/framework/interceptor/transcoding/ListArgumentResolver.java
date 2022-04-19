package com.jhopesoft.framework.interceptor.transcoding;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.alibaba.fastjson.JSON;

/**
 *
 * @author 
 * 
 */

public class ListArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String DEF_PARAM_LIST = "_def_param_list";

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		RequestList requestList = parameter.getParameterAnnotation(RequestList.class);
		return requestList != null;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		RequestList requestList = parameter.getParameterAnnotation(RequestList.class);
		try {
			if (requestList != null) {
				String paramstr = requestList.value();
				if (DEF_PARAM_LIST.equals(paramstr)) {
					paramstr = parameter.getParameterName();
				}
				String text = webRequest.getParameter(paramstr);
				if (text == null) {
					return null;
				}
				Type type = parameter.getGenericParameterType();
				Class<?> clazz = Map.class;
				if (type instanceof ParameterizedType) {
					Type[] types = ((ParameterizedType) type).getActualTypeArguments();
					if (types[0] instanceof ParameterizedType) {
						ParameterizedType parameterizedType = (ParameterizedType) types[0];
						clazz = (Class<?>) parameterizedType.getRawType();
					} else if (types[0] instanceof Class) {
						clazz = (Class<?>) types[0];
					}
				}
				return JSON.parseArray(text, clazz);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}
}