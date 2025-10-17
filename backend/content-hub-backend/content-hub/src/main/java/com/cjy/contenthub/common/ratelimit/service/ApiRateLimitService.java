package com.cjy.contenthub.common.ratelimit.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import com.cjy.contenthub.common.properties.ApiPrefixProperties;
import com.cjy.contenthub.common.properties.ApiRateLimitProperties;
import com.cjy.contenthub.common.properties.ApiRateLimitProperties.ApiRateLimitRules;

import lombok.RequiredArgsConstructor;

/**
 * API Rate Limit 서비스
 * 특정 경로에 대한 최대 요청 횟수와 시간(초)을 반환
 */
@Service
@RequiredArgsConstructor
public class ApiRateLimitService {
	
	/** API 접두사 및 버전 설정  */
	private final ApiPrefixProperties apiPrefixProperties;
	
	/** API Rate Limit 설정 */
	private final ApiRateLimitProperties apiRateLimitProperties;
	
	/** 경로 패턴 매처 */
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
	
	/** API Rate Limit 결과 레코드 */
	private record ApiRateLimitResults(int maxRequestCount, int seconds) {}
	
	/**
	 * 특정 경로에 대한 최대 요청 횟수 반환
	 * 
	 * @param path 요청 경로
	 * @return 최대 요청 횟수와 시간(초)
	 */
	public int getMaxRequestCount(String path) {
		return getRateLimitForPath(path).maxRequestCount;
	}
	
	/**
	 * 특정 경로에 대한 시간(초) 반환
	 * 
	 * @param path 요청 경로
	 * @return 시간(초)
	 */
	public int getSeconds(String path) {
		return getRateLimitForPath(path).seconds;
	}
	
	/**
	 * 경로에 따른 API Rate Limit 설정 조회
	 * 
	 * @param path 요청 경로
	 * @return 해당 경로에 대한 최대 요청 횟수와 시간(초)
	 */
	private ApiRateLimitResults getRateLimitForPath(String path) {
		// 접두사 및 버전
		String fullPrefix = apiPrefixProperties.getFullPrefix();
		// 디폴트 설정
		int defaultMaxRequestCount = apiRateLimitProperties.getDefaults().getMaxRequestCount();
		int defaultSeconds = apiRateLimitProperties.getDefaults().getSeconds();
		// 경로 패턴 매칭을 통해 설정 반환
		for (ApiRateLimitRules rule : apiRateLimitProperties.getRules()) {
			// 개별 경로에 접두사 및 버전 추가
			String individualPath = fullPrefix.concat(rule.getPath());
			// 패턴 매칭
			if (PATH_MATCHER.match(individualPath, path)) {
				int maxRequestCount = Optional.ofNullable(rule.getMaxRequestCount()).orElse(defaultMaxRequestCount);
				int seconds = Optional.ofNullable(rule.getSeconds()).orElse(defaultSeconds);
				return new ApiRateLimitResults(maxRequestCount, seconds);
			}
		}
		return new ApiRateLimitResults(defaultMaxRequestCount, defaultSeconds);
	}

}
