package com.cjy.contenthub.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.cjy.contenthub.common.constants.CommonConstants;

import lombok.RequiredArgsConstructor;

/**
 * Redis 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
public class RedisUtil {
	
	/** Redis 템플릿 */
	private final RedisTemplate<String, Object> redisTemplate;
	
	/** 리프레시 토큰 키 접두사 */
	private static final String KEY_REFRESH_TOKEN = "refreshToken:";
	
	/**
	 * 리프레시 토큰 저장 메소드
	 * 
	 * @param provider     로그인 제공자
	 * @param providerId   로그인 제공자의 고유 ID
	 * @param refreshToken 리프레시 토큰 값
	 */
	public void saveRefreshToken(String provider, String providerId, String refreshToken) {
		String key = KEY_REFRESH_TOKEN.concat(provider).concat(CommonConstants.COLON).concat(providerId);
		redisTemplate.opsForValue().set(key, refreshToken);
	}
	
	/**
	 * 리프레시 토큰 유효성 검사 메소드
	 * 
	 * @param provider     로그인 제공자
	 * @param providerId   로그인 제공자의 고유 ID
	 * @param refreshToken 리프레시 토큰 값
	 * @return boolean 유효성 검사 결과 (true: 유효, false: 유효하지 않음)
	 */
	public boolean validateRefreshToken(String provider, String providerId, String refreshToken) {
        String key = KEY_REFRESH_TOKEN.concat(provider).concat(CommonConstants.COLON).concat(providerId);
        String redisRefreshToken = (String) redisTemplate.opsForValue().get(key);
		return StringUtils.isNotEmpty(redisRefreshToken) && redisRefreshToken.equals(refreshToken);
	}
	
	/**
	 * 리프레시 토큰 삭제 메소드
	 * 
	 * @param provider   로그인 제공자
	 * @param providerId 로그인 제공자의 고유 ID
	 */
	public void deleteRefreshToken(String provider, String providerId) {
		String key = KEY_REFRESH_TOKEN.concat(provider).concat(CommonConstants.COLON).concat(providerId);
		if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
			redisTemplate.delete(key);
		}
	}
	

}
