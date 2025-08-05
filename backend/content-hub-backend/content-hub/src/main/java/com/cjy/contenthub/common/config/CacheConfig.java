package com.cjy.contenthub.common.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cjy.contenthub.common.constants.CommonEnum.CacheTypeEnum;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * 캐시 보존 설정 클래스
 */
@Configuration
@EnableCaching
public class CacheConfig {

	/**
	 * Caffeine을 사용하여 캐시를 관리하는 CacheManager를 생성
	 * 비동기 캐시 모드를 활성화하고, 캐시의 만료 시간과 최대 크기를 설정
	 * CacheManager를 반환하여 스프링 애플리케이션에서 캐시를 사용할 수 있도록 설정
	 * 
	 * @return CacheManager
	 */
	@Bean
	CacheManager cacheManager() {
		
		// CaffeineCacheManager 생성
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();
		// 비동기 캐시 모드 활성화
		cacheManager.setAsyncCacheMode(true); 
		
		// Caffeine Cache 의 커스텀 설정 
		Caffeine<Object, Object> caffeineCache = Caffeine.newBuilder()
				.expireAfterWrite(CacheTypeEnum.EXPIRE_AFTER_WRITE, TimeUnit.MINUTES) // 캐시 생성 후, 삭제되는 시간 설정
				.maximumSize(CacheTypeEnum.MAXIMUM_SIZE) // 캐시 등록 개수 설정
				.recordStats(); // 캐시 통계 기록 활성화
		// CaffeineCacheManager에 설정
		cacheManager.setCaffeine(caffeineCache); 
		
		return cacheManager;
	}
}
