package com.jhopesoft.configuration;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.jhopesoft.framework.interceptor.transcoding.BeanArgumentResolver;
import com.jhopesoft.framework.interceptor.transcoding.ListArgumentResolver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver getCommonsMultipartResolver(
			@Value("${spring.servlet.multipart.max-file-size}") long maxUploadSize) {
		CommonsMultipartResolver result = new CommonsMultipartResolver();
		result.setDefaultEncoding("utf-8");
		result.setMaxUploadSize(maxUploadSize);
		result.setMaxInMemorySize(40960);
		return result;
	}

	/**
	 * 解析spring mvc 的 List<Class>的转换器
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new ListArgumentResolver());
		argumentResolvers.add(new BeanArgumentResolver());
	}

	/**
	 * 使用fastjson来进行序列化输出,日期和时间也设置了输出的格式,对于byte[]类型采用base64编码
	 */
	private static final String DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
	private static String DATEFORMAT = "yyyy-MM-dd";

	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters() {
		FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setFeatures(Feature.DisableCircularReferenceDetect);
		SerializeConfig config = fastJsonConfig.getSerializeConfig();
		config.put(java.util.Date.class, new SimpleDateFormatSerializer(DATETIMEFORMAT));
		config.put(java.sql.Date.class, new SimpleDateFormatSerializer(DATEFORMAT));
		config.put(java.sql.Timestamp.class, new SimpleDateFormatSerializer(DATETIMEFORMAT));
		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
		List<MediaType> fastMediaTypes = new ArrayList<>();
		fastMediaTypes.add(MediaType.APPLICATION_JSON);
		fastConverter.setSupportedMediaTypes(fastMediaTypes);
		fastConverter.setFastJsonConfig(fastJsonConfig);
		HttpMessageConverter<?> converter = fastConverter;
		return new HttpMessageConverters(converter);
	}

}
