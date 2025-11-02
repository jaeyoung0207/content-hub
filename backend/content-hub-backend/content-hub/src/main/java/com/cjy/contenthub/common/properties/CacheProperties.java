package com.cjy.contenthub.common.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 캐시 설정 프로퍼티 클래스
 */
@ConfigurationProperties(prefix = "cache")
@Setter
@Getter
public class CacheProperties {
	
	/** 기본 캐시 설정 */
	private Spec defaultCache = new Spec(15L, 1000L);
	
	/** 개별 캐시 설정 */
	private Map<String, Spec> individuals;

	/**
	 * 캐시 설정 클래스
	 */
	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Spec {
		
		/** 캐시 만료 시간(분) */
		private Long expireAfterWrite;
		
		/** 캐시 최대 크기 */
		private Long maximumSize;
	}

}
