package com.cjy.contenthub.common.cache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.caffeine.CaffeineCacheManager;

import com.cjy.contenthub.common.properties.CacheProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import lombok.RequiredArgsConstructor;

/**
 * 공통 캐시 관리자 클래스
 * CaffeineCacheManager를 확장하여 개별 캐시 설정을 적용
 */
@RequiredArgsConstructor
public class CommonCacheManager extends CaffeineCacheManager {

	/** 캐시 설정 프로퍼티 */
	private final CacheProperties cacheProperties;
	
	/** 메트릭 레지스트리 */
	private final MeterRegistry meterRegistry;

	/**
	 * 네이티브 Caffeine 캐시 생성
	 * 
	 * @param name 캐시 이름
	 * @return Caffeine 캐시 인스턴스
	 */
	@Override
	protected Cache<Object, Object> createNativeCaffeineCache(String name) {
		// 개별 캐시 설정이 있는지 확인하고, 없으면 기본 캐시 설정 사용
		CacheProperties.Spec spec = Optional.ofNullable(cacheProperties.getIndividuals())
				.map(individuals -> individuals.get(name)).orElse(cacheProperties.getDefaultCache());

		// Caffeine 캐시 빌더 생성
		Caffeine<Object, Object> caffeineCache = Caffeine.newBuilder()
				.expireAfterWrite(Optional.ofNullable(spec.getExpireAfterWrite())
						.orElse(cacheProperties.getDefaultCache().getExpireAfterWrite()), TimeUnit.MINUTES)
				.maximumSize(Optional.ofNullable(spec.getMaximumSize())
						.orElse(cacheProperties.getDefaultCache().getMaximumSize()))
				.recordStats();
		
		// Caffeine 캐시 빌드
		Cache<Object, Object> nativeCache = caffeineCache.build();
		
		// Micrometer에 캐시 메트릭 등록
		CaffeineCacheMetrics.monitor(meterRegistry, nativeCache, name);

		// Caffeine 캐시 인스턴스 반환
		return nativeCache;
	}

}
