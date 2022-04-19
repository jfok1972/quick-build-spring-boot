package com.jhopesoft.framework.interceptor.transcoding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.jhopesoft.framework.utils.Constants;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 *
 * @author 
 *  
 */

public class BeanArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String DEF_PARAM_BEAN = "_def_param_bean";

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		RequestBean requestBean = parameter.getParameterAnnotation(RequestBean.class);
		return requestBean != null;
	}

	@Override
	public Object resolveArgument(MethodParameter param, ModelAndViewContainer mavContainer, NativeWebRequest request,
			WebDataBinderFactory binderFactory) throws Exception {
		RequestBean requestBean = param.getParameterAnnotation(RequestBean.class);
		try {
			String paramstr = requestBean.value();
			if (DEF_PARAM_BEAN.equals(paramstr)) {
				paramstr = param.getParameterName();
			}
			Class<?> clazz = param.getParameterType();
			Object object = clazz.newInstance();
			HashMap<String, String[]> paramsMap = new HashMap<String, String[]>(0);
			Iterator<String> itor = request.getParameterNames();
			while (itor.hasNext()) {
				String webParam = (String) itor.next();
				String[] webValue = request.getParameterValues(webParam);
				List<String> webValueList = new ArrayList<String>();
				for (int i = 0; i < webValue.length; i++) {
					if (webValue[i] != null && !"".equals(webValue[i])) {
						webValueList.add(webValue[i]);
					}
				}
				if (webParam.startsWith(paramstr)) {
					paramsMap.put(webParam, webValueList.toArray(new String[webValueList.size()]));
				}
			}
			BeanWrapper obj = new BeanWrapperImpl(object);
			obj.registerCustomEditor(Date.class, null,
					new CustomDateEditor(new SimpleDateFormat(Constants.DATE_TIME_FORMAT), true));

			for (String propName : paramsMap.keySet()) {
				Object propVals = paramsMap.get(propName);
				String[] props = propName.split("\\.");
				if (props.length == 2) {
					obj.setPropertyValue(props[1], propVals);
				} else if (props.length == 3) {
					Object tmpObj = obj.getPropertyValue(props[1]);
					if (tmpObj == null) {
						obj.setPropertyValue(props[1], obj.getPropertyType(props[1]).newInstance());
					}
					obj.setPropertyValue(props[1] + "." + props[2], propVals);
				}

			}
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}