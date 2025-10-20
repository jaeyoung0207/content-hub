package com.cjy.contenthub.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.cjy.contenthub.common.annotation.ApiController;
import com.cjy.contenthub.common.properties.ApiPrefixProperties;
import com.cjy.contenthub.common.constants.CommonConstants;

import lombok.RequiredArgsConstructor;

/**
 * API 접두사 및 버전 설정 클래스
 */
@Configuration
@RequiredArgsConstructor
public class ApiPrefixConfig implements WebMvcConfigurer {
	
	/** API 접두사 및 버전 설정 */
	private final ApiPrefixProperties apiPrefixProperties;
	
	/**
	 * 경로 접두사 구성 
	 * 특정 어노테이션 대해 지정된 접두사와 버전을 경로에 자동으로 추가
	 *
	 * @param configurer PathMatchConfigurer 객체
	 */
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		String fullPrefix = apiPrefixProperties.getFullPrefix();
		if (fullPrefix.isEmpty() || fullPrefix.equals(CommonConstants.SLASH)) {
			return;
		}
		configurer.addPathPrefix(fullPrefix, clazz -> clazz.isAnnotationPresent(ApiController.class));
	}
	
}
