package com.cjy.contenthub.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cjy.contenthub.common.cache.CommonCacheManager;
import com.cjy.contenthub.common.properties.CacheProperties;

import io.micrometer.core.instrument.MeterRegistry;
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
	
	/** 메트릭 레지스트리 */
	private final MeterRegistry meterRegistry;
	
	/**
	 * CacheManager 빈 생성
	 * 
	 * @return CacheManager 캐시 관리자 인스턴스
	 */
	@Bean
	CacheManager cacheManager() {
		// CommonCacheManager 인스턴스 생성
		CaffeineCacheManager caffeineCacheManager = new CommonCacheManager(cacheProperties, meterRegistry);
		// null 값 허용 안 함
		caffeineCacheManager.setAllowNullValues(false);
		// 캐시 관리자 인스턴스 반환
		return caffeineCacheManager;
	}
	
	/**
	 * KeyGenerator 빈 생성
	 * 메서드 이름이 빈 이름으로 사용됨
	 * @Cacheable 어노테이션에서 key 속성을 지정하지 않은 경우 기본 키 생성기로 사용
	 * 단, 빈 이름을 지정할 경우 @Cacheable 의 keyGenerator 속성을 사용하여 해당 빈 이름 지정 필요
	 * @Cacheable가 사용된 메서드의 매개변수를 기반으로 캐시 키를 생성
	 * 
	 * @return KeyGenerator 캐시 키 생성기 인스턴스
	 */
	@Bean
	KeyGenerator keyGenerator() {
		return new SimpleKeyGenerator();
	}
	
}
