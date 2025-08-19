package com.cjy.contenthub.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정 클래스
 */
@Configuration
public class RedisConfig {
	
	/** Redis 호스트 이름 */
	@Value("${spring.data.redis.host}")
	private String redisHost;
	
	/** Redis 포트 번호 */
	@Value("${spring.data.redis.port}")
	private int redisPort;
	
	/**
	 * RedisConnectionFactory 빈을 생성하여 Redis와의 연결을 관리
	 * LettuceConnectionFactory를 사용하여 Redis 서버에 연결
	 * 
	 * @return RedisConnectionFactory Redis 연결 팩토리
	 */
	@Bean
	RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisHost, redisPort);
	}
	
	/**
	 * RedisTemplate 빈을 생성하여 Redis에 데이터를 저장하고 조회하는 데 사용
	 * RedisTemplate은 Redis 서버와의 상호작용을 위한 핵심 클래스
	 * 
	 * @param redisConnectionFactory RedisConnectionFactory 빈
	 * @return RedisTemplate<String, Object> Redis 템플릿
	 */
	@Bean
	RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer()); // 키 직렬화 방식 설정
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // JSON 직렬화 방식 설정(문자열은 그대로, 객체는 JSON으로 직렬화되어 저장됨)
		return template;
	}
}
