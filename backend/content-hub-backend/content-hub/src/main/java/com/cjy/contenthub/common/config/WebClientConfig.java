package com.cjy.contenthub.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import lombok.RequiredArgsConstructor;

/**
 * WebClient 설정 클래스
 * 외부 API와 통신하기 위한 WebClient를 설정하는 클래스
 * 각 API에 대한 기본 URL, 인증 헤더 등을 설정하며 응답 데이터의 매핑 전략을 정의
 */
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

	/** TMDB API 인증 토큰 */
	@Value("${tmdb.api.token}")
	private String tmdbApiAccessToken;

	/** TMDB API 기본 URL */
	@Value("${tmdb.url.baseUrl}")
	private String tmdbBaseUrl;

	/** AniList API 기본 URL */
	@Value("${anilist.url.baseUrl}")
	private String anilistBaseUrl;

	/** DeepL API 기본 URL */
	@Value("${deepl.url.baseUrl}")
	private String deeplBaseUrl;

	/** DeepL API 인증 키 */
	@Value("${deepl.api.key}")
	private String deeplApiKey;

	/**
	 * TMDB API와 통신하기 위한 WebClient를 설정
	 * @Bean 어노테이션을 사용하여 스프링 컨테이너에 WebClient 빈으로 등록
	 * 
	 * @return WebClient 인스턴스
	 */
	@Bean
	WebClient tmdbWebClient() {
		// WebClient 공통설정
		return WebClient.builder()
				.baseUrl(tmdbBaseUrl) // TMDB API 기본 URL 설정
				.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ".concat(tmdbApiAccessToken)) // 헤더에 인증에 필요한 토큰 설정
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE) // 헤더에 응답 데이터 타입 설정
				.exchangeStrategies(getExchangeStrategies()) // 응답 데이터 매핑 전략 설정
				.build();
	}

	/**
	 * AniList API와 통신하기 위한 WebClient를 설정
	 * AniList API는 인증 헤더가 필요하지 않으므로, 인증 헤더 설정은 생략
	 * @Bean 어노테이션을 사용하여 스프링 컨테이너에 WebClient 빈으로 등록
	 * 
	 * @return WebClient 인스턴스
	 */
	@Bean
	WebClient anilistWebClient() {
		// WebClient 공통설정
		return WebClient.builder() // WebClient 빌더 생성
				.baseUrl(anilistBaseUrl) // AniList API 기본 URL 설정
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // 헤더에 전송하는 데이터 타입 설정
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE) // 헤더에 응답 데이터 타입 설정
				.build();
	}

	/**
	 * AniList API 통신용 WebClient를 설정
	 * @Bean 어노테이션을 사용하여 스프링 컨테이너에 WebClient 빈으로 등록
	 * 
	 * @return WebClient 인스턴스
	 */
	@Bean
	WebClient deeplWebClient() {
		// WebClient 공통설정
		return WebClient.builder()
				.baseUrl(deeplBaseUrl) // DeepL API 기본 URL 설정
				.defaultHeader(HttpHeaders.AUTHORIZATION, "DeepL-Auth-Key ".concat(deeplApiKey)) // 헤더에 인증 키 설정
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE) // 헤더에 전송하는 데이터 타입 설정
				.build();
	}

	/**
	 * 네이버 API 통신용 WebClient를 설정
	 * @Bean 어노테이션을 사용하여 스프링 컨테이너에 WebClient 빈으로 등록
	 * 
	 * @return WebClient 인스턴스
	 */
	@Bean
	WebClient naverWebClient() {
		// WebClient 공통설정
		return WebClient.builder()
				.exchangeStrategies(getExchangeStrategies()) // 응답 데이터 매핑 전략 설정
				.build();
	}

	/**
	 * 카카오 API 통신용 WebClient를 설정
	 * @Bean 어노테이션을 사용하여 스프링 컨테이너에 WebClient 빈으로 등록
	 * 
	 * @return WebClient 인스턴스
	 */
	@Bean
	WebClient kakaoWebClient() {
		// WebClient 공통설정
		return WebClient.builder()
				.defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8") // 헤더에 전송하는 데이터 타입 설정
				.exchangeStrategies(getExchangeStrategies()) // 응답 데이터 매핑 전략 설정
				.build();
	}

	/**
	 * WebClient에서 JSON 응답을 파싱할 때 사용할 ExchangeStrategies를 설정
	 * 
	 * @return ExchangeStrategies 인스턴스
	 */
	private ExchangeStrategies getExchangeStrategies() {
		// snake_case → camelCase 매핑되는 ObjectMapper(JSON <-> Java 객체 간 변환을 담당) 생성
		ObjectMapper objectMapper = new ObjectMapper();
		// JSON에서 오는 snake_case 키(ex:first_air_date)를 → Java의 camelCase 필드(ex:firstAirDate)에 자동으로 매핑하도록 설정
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		// 응답 JSON에 정의되고 DTO에 정의 되어있지 않아도 무시하는 설정
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// WebClient에서 사용할 ExchangeStrategie를 통해 JSON 응답을 파싱할 때 사용할 디코더를 설정
		// objectMapper를 사용하는 Jackson2JsonDecoder를 설정
		// JSON 응답에 대해서만 해당 매핑 전략을 적용
		ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder() // ExchangeStrategies 빌더 생성
				.codecs(configurer -> {
					configurer.defaultCodecs().maxInMemorySize(1024 * 1024); // 최대 메모리 크기 설정 (1MB)
					configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON)); // JSON 디코더 설정
				}).build();  // ExchangeStrategies 빌드
		// 빌드된 ExchangeStrategies를 반환
		return exchangeStrategies;
	}
}
