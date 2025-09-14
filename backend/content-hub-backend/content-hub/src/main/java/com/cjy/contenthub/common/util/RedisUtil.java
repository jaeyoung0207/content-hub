package com.cjy.contenthub.common.util;

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.LoginProviderEnum;

import lombok.RequiredArgsConstructor;

/**
 * Redis 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
public class RedisUtil {
	
	/** Redis 템플릿 */
	private final RedisTemplate<String, Object> redisTemplate;
	
	/** 네이버 리프레시 토큰 만료 시간 (일) */
	@Value("${login.naver.custom.refreshTokenExpiresIn}")
	private long naverExpiresIn;
	
	/** 네이버 리프레시 토큰 만료 시간 (일) */
	@Value("${login.kakao.custom.refreshTokenExpiresIn}")
	private long kakaoExpiresIn;
	
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
		long refreshTokenExpiresIn;
		if (StringUtils.equals(provider, LoginProviderEnum.KAKAO.getProvider())) {
			refreshTokenExpiresIn = kakaoExpiresIn;
		} else {
			refreshTokenExpiresIn = naverExpiresIn;
		}
		String key = KEY_REFRESH_TOKEN.concat(provider).concat(CommonConstants.COLON).concat(providerId);
		setValue(key, refreshToken, Duration.ofDays(refreshTokenExpiresIn));
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
        String redisRefreshToken = (String) getValue(key);
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
			deleteKey(key);
		}
	}
	
	/**
	 * 일반 키-값 쌍 저장 메소드
	 * 
	 * @param key      키
	 * @param value    값
	 * @param duration 만료 시간 (분)
	 */
	public void setValue(String key, Object value, Duration duration) {
		redisTemplate.opsForValue().set(key, value, duration);
	}
	
	/**
	 * 일반 키-값 쌍 조회 메소드
	 * 
	 * @param key 키
	 * @return Object 값
	 */
	public Object getValue(String key) {
		return redisTemplate.opsForValue().get(key);
	}
	
	/**
	 * 일반 키-값 쌍 삭제 메소드
	 * 
	 * @param key 키
	 */
	public void deleteKey(String key) {
		if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
			redisTemplate.delete(key);
		}
	}

}
