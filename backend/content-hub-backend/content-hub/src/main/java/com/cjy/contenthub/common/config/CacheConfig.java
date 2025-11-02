package com.cjy.contenthub.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cjy.contenthub.common.cache.CommonCacheManager;
import com.cjy.contenthub.common.properties.CacheProperties;

import lombok.RequiredArgsConstructor;

/**
 * 캐시 보존 설정 클래스
 */
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {
	
	/** 캐시 설정 프로퍼티 */
	private final CacheProperties cacheProperties;
	
	/**
	 * CacheManager 빈 생성
	 * 
	 * @return CacheManager 캐시 관리자 인스턴스
	 */
	@Bean
	CacheManager cacheManager() {
		return new CommonCacheManager(cacheProperties);
	}
	
}
