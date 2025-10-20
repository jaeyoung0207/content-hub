package com.cjy.contenthub.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMessagesErrorEnum;

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
	
	/** 제공자 정보 키 접두사 */
	private static final String KEY_PROVIDER_INFO = "providerInfo:";
	
	/** 제공자 정보 레코드 */
	public record ProviderInfo(String provider, String providerId) {}
	
	/**
	 * 해시 키 변환 메소드
	 * 
	 * @param key 원본 키
	 * @return String 변환된 해시 키
	 */
	public String convertHashKey(String key) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = md.digest(key.getBytes(StandardCharsets.UTF_8));
			return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
		} catch (Exception e) {
			throw new IllegalStateException(
					messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_CONVERT_HASHKEY.getMessageCode()), e);
		}
	}
	
	/**
	 * 리프레시 토큰 저장 메소드
	 * 
	 * @param provider     로그인 제공자
	 * @param providerId   로그인 제공자의 고유 ID
	 * @param refreshToken 리프레시 토큰 값
	 * @param expiresIn 토큰 만료 시간 (일)
	 */
	public void saveRefreshToken(String provider, String providerId, String refreshToken, long expiresIn) {
		// 리프레시 토큰 해시 값 생성
		String refreshTokenHash = convertHashKey(refreshToken);
		// 리프레시 토큰 해시를 제공자 정보를 저장할 키로 정의
		String providerInfoKey = KEY_PROVIDER_INFO.concat(refreshTokenHash);
		// 제공자 정보를 리프레시 토큰을 저장할 키로 정의
		String refreshTokenKey = KEY_REFRESH_TOKEN.concat(provider).concat(CommonConstants.COLON).concat(providerId);
		// 제공자 정보 키와 사용자 정보 매핑 저장
		setValue(providerInfoKey, provider.concat(CommonConstants.COLON).concat(providerId), Duration.ofDays(expiresIn));
		// 리프레시 토큰 키와 리프레시 토큰 매핑 저장
		setValue(refreshTokenKey, refreshTokenHash, Duration.ofDays(expiresIn));
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
        String redisRefreshTokenHash = (String) getValue(key);
		return StringUtils.isNotEmpty(redisRefreshTokenHash) && redisRefreshTokenHash.equals(convertHashKey(refreshToken));
	}
	
	/**
	 * 리프레시 토큰으로 제공자 정보 조회 메소드
	 * 
	 * @param refreshToken 리프레시 토큰 값
	 * @return ProviderInfo 제공자 정보 객체 (null: 정보 없음)
	 */
	public ProviderInfo getProviderInfoByRefreshToken(String refreshToken) {
		String key = KEY_PROVIDER_INFO.concat(convertHashKey(refreshToken));
		String providerInfoStr = (String) getValue(key);
		if (StringUtils.isNotEmpty(providerInfoStr)) {
			String[] providerInfoArray = providerInfoStr.split(CommonConstants.COLON);
			if (providerInfoArray.length == 2) {
				return new ProviderInfo(providerInfoArray[0], providerInfoArray[1]);
			}
		}
		return null;
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
	 * 리프레시 토큰으로 사용자 정보 삭제 메소드
	 * 
	 * @param refreshToken 리프레시 토큰 값
	 */
	public void deleteProviderInfoByRefreshToken(String refreshToken) {
		String key = KEY_PROVIDER_INFO.concat(convertHashKey(refreshToken));
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
					messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_REDIS_INCREMENT.getMessageCode()));
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
