package com.cjy.contenthub.common.util;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.LoginProviderEnum;
import com.cjy.contenthub.common.constants.CommonEnum.MessagesErrorEnum;

import lombok.RequiredArgsConstructor;

/**
 * Redis 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
public class RedisUtil {
	
	/** Redis 템플릿 */
	private final RedisTemplate<String, Object> redisTemplate;
	
	/** String Redis 템플릿 */
	private final StringRedisTemplate stringRedisTemplate;
	
	/** 메시지 유틸 */
	private final MessageUtil messageUtil;
	
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

	/**
	 * 키 값 1 증가 메소드
	 * 
	 * @param key
	 * @return Long 증가된 값
	 */
	public Long increment(String key) {
		Long count = stringRedisTemplate.opsForValue().increment(key);
		if (count == null) {
			throw new IllegalStateException(
					messageUtil.getMessageKO(MessagesErrorEnum.ERROR_COMMON_REDIS_INCREMENT.getMessageCode()));
		}
		return count;
	}
	
	/**
	 * 키 만료 시간 설정 메소드
	 * 
	 * @param key     키
	 * @param timeout 만료 시간
	 * @param unit    시간 단위
	 */
	public void expire(String key, long timeout, TimeUnit unit) {
		stringRedisTemplate.expire(key, timeout, unit);
	}
	
	/**
	 * 키 만료 시간 조회 메소드
	 * 
	 * @param key  키
	 * @param unit 시간 단위
	 * @return Long 만료 시간
	 */
	public Long getExpire(String key, TimeUnit unit) {
		return stringRedisTemplate.getExpire(key, unit);
	}

	/**
	 * Lua 스크립트 실행 메소드
	 * Lua 스크립트를 사용하여 Redis에서 원자적인 작업을 수행
	 * 
	 * @param <T>        반환 타입
	 * @param script     실행할 Lua 스크립트
	 * @param keyList    키 목록 (immutable)
	 * @param args       인수 목록 (immutable)
	 * @param resultType 반환 타입 클래스
	 * @return T 스크립트 실행 결과
	 */
	public <T> T executeScript(String script, List<String> keyList, List<String> args, Class<T> resultType) {
		return stringRedisTemplate.execute(
			    new DefaultRedisScript<>(script, resultType),
			    keyList,
			    args.toArray());
	}

}
