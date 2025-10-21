package com.cjy.contenthub.common.config;

import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.common.constants.CommonConstants;
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

	/** WebClient 최대 메모리 크기 설정 */
	@Value("${app.web-client.max-in-memory-size}")
	private int maxInMemorySize;

	/** TMDB API 인증 토큰 */
	@Value("${tmdb.api.token}")
	private String tmdbApiAccessToken;

	/** TMDB API 기본 URL */
	@Value("${tmdb.url.base-url}")
	private String tmdbBaseUrl;

	/** TMDB API ConnectionRequestTimeout */
	@Value("${tmdb.custom.connection-request-timeout}")
	private int tmdbConnectionRequestTimeout;

	/** TMDB API ResponseTimeout */
	@Value("${tmdb.custom.response-timeout}")
	private int tmdbResponseTimeout;
	
	/** TMDB API 최대 커넥션 수 */
	@Value("${tmdb.custom.max-conn-total}")
	private int tmdbMaxConnTotal;
	
	/** TMDB API 라우트당 최대 커넥션 수 */
	@Value("${tmdb.custom.max-conn-per-route}")
	private int tmdbMaxConnPerRoute;

	/** AniList API 기본 URL */
	@Value("${anilist.url.base-url}")
	private String anilistBaseUrl;

	/** AniList API ConnectionRequestTimeout */
	@Value("${anilist.custom.connection-request-timeout}")
	private int anilistConnectionRequestTimeout;

	/** AniList API ResponseTimeout */
	@Value("${anilist.custom.response-timeout}")
	private int anilistResponseTimeout;
	
	/** AniList API 최대 커넥션 수 */
	@Value("${anilist.custom.max-conn-total}")
	private int anilistMaxConnTotal;
	
	/** AniList API 라우트당 최대 커넥션 수 */
	@Value("${anilist.custom.max-conn-per-route}")
	private int anilistMaxConnPerRoute;

	/** DeepL API 기본 URL */
	@Value("${deepl.url.base-url}")
	private String deeplBaseUrl;

	/** DeepL API 인증 키 */
	@Value("${deepl.api.key}")
	private String deeplApiKey;

	/** DeepL API ConnectionRequestTimeout */
	@Value("${deepl.custom.connection-request-timeout}")
	private int deeplConnectionRequestTimeout;

	/** DeepL API ResponseTimeout */
	@Value("${deepl.custom.response-timeout}")
	private int deeplResponseTimeout;
	
	/** DeepL API 최대 커넥션 수 */
	@Value("${deepl.custom.max-conn-total}")
	private int deeplMaxConnTotal;
	
	/** DeepL API 라우트당 최대 커넥션 수 */
	@Value("${deepl.custom.max-conn-per-route}")
	private int deeplMaxConnPerRoute;

	/** NAVER API ConnectionRequestTimeout */
	@Value("${login.naver.custom.connection-request-timeout}")
	private int naverConnectionRequestTimeout;

	/** NAVER API ResponseTimeout */
	@Value("${login.naver.custom.response-timeout}")
	private int naverResponseTimeout;
	
	/** NAVER API 최대 커넥션 수 */
	@Value("${login.naver.custom.max-conn-total}")
	private int naverMaxConnTotal;
	
	/** NAVER API 라우트당 최대 커넥션 수 */
	@Value("${login.naver.custom.max-conn-per-route}")
	private int naverMaxConnPerRoute;

	/** KAKAO API ConnectionRequestTimeout */
	@Value("${login.kakao.custom.connection-request-timeout}")
	private int kakaoConnectionRequestTimeout;

	/** KAKAO API ResponseTimeout */
	@Value("${login.kakao.custom.response-timeout}")
	private int kakaoResponseTimeout;
	
	/** KAKAO API 최대 커넥션 수 */
	@Value("${login.kakao.custom.max-conn-total}")
	private int kakaoMaxConnTotal;
	
	/** KAKAO API 라우트당 최대 커넥션 수 */
	@Value("${login.kakao.custom.max-conn-per-route}")
	private int kakaoMaxConnPerRoute;

	/**
	 * TMDB API와 통신하기 위한 WebClient를 설정
	 * @Bean 어노테이션을 사용하여 스프링 컨테이너에 WebClient 빈으로 등록
	 * 
	 * @return WebClient 인스턴스
	 */
	@Bean
	WebClient tmdbWebClient() {
		// 타임아웃 설정
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(tmdbConnectionRequestTimeout, TimeUnit.SECONDS) // 커넥션 풀에서 커넥션을 가져올 때의 타임아웃
				.setResponseTimeout(tmdbResponseTimeout, TimeUnit.SECONDS) // 서버 응답(읽기) 타임아웃
				.build();
		
		// 커넥션 풀 설정
		AsyncClientConnectionManager connManager = PoolingAsyncClientConnectionManagerBuilder.create()
				.setMaxConnTotal(tmdbMaxConnTotal) // 최대 커넥션 수
				.setMaxConnPerRoute(tmdbMaxConnPerRoute) // 라우트당 최대 커넥션 수
				.build();
		
		// WebClient 공통설정
		return WebClient.builder()
				.baseUrl(tmdbBaseUrl) // TMDB API 기본 URL 설정
				.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ".concat(tmdbApiAccessToken)) // 헤더에 인증에 필요한 토큰 설정
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE) // 헤더에 응답 데이터 타입 설정
				.clientConnector(new HttpComponentsClientHttpConnector(
						HttpAsyncClients.custom()
						.setConnectionManager(connManager)
						.setDefaultRequestConfig(requestConfig)
						.build())) // HttpClient 커넥터 설정
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
		// 타임아웃 설정
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(anilistConnectionRequestTimeout, TimeUnit.SECONDS) // 커넥션 풀에서 커넥션을 가져올 때의 타임아웃
				.setResponseTimeout(anilistResponseTimeout, TimeUnit.SECONDS) // 서버 응답(읽기) 타임아웃
				.build();
		
		// 커넥션 풀 설정
		AsyncClientConnectionManager connManager = PoolingAsyncClientConnectionManagerBuilder.create()
				.setMaxConnTotal(anilistMaxConnTotal) // 최대 커넥션 수
				.setMaxConnPerRoute(anilistMaxConnPerRoute) // 라우트당 최대 커넥션 수
				.build();
		
		// WebClient 공통설정
		return WebClient.builder() // WebClient 빌더 생성
				.baseUrl(anilistBaseUrl) // AniList API 기본 URL 설정
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // 헤더에 전송하는 데이터 타입 설정
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE) // 헤더에 응답 데이터 타입 설정
				.clientConnector(new HttpComponentsClientHttpConnector(
						HttpAsyncClients.custom()
						.setConnectionManager(connManager)
						.setDefaultRequestConfig(requestConfig)
						.build())) // HttpClient 커넥터 설정
				.exchangeStrategies(ExchangeStrategies.builder()
						.codecs(configurer -> configurer.defaultCodecs()
								.maxInMemorySize(CommonConstants.ONE_MB * maxInMemorySize))
						.build()) // 응답 데이터 매핑 전략 설정(최대 메모리 크기 설정)
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
		// 타임아웃 설정
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(deeplConnectionRequestTimeout, TimeUnit.SECONDS) // 커넥션 풀에서 커넥션을 가져올 때의 타임아웃
				.setResponseTimeout(deeplResponseTimeout, TimeUnit.SECONDS) // 서버 응답(읽기) 타임아웃
				.build();
		
		// 커넥션 풀 설정
		AsyncClientConnectionManager connManager = PoolingAsyncClientConnectionManagerBuilder.create()
				.setMaxConnTotal(deeplMaxConnTotal) // 최대 커넥션 수
				.setMaxConnPerRoute(deeplMaxConnPerRoute) // 라우트당 최대 커넥션 수
				.build();

		// WebClient 공통설정
		return WebClient.builder()
				.baseUrl(deeplBaseUrl) // DeepL API 기본 URL 설정
				.defaultHeader(HttpHeaders.AUTHORIZATION, "DeepL-Auth-Key ".concat(deeplApiKey)) // 헤더에 인증 키 설정
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE) // 헤더에 전송하는 데이터 타입 설정
				.clientConnector(new HttpComponentsClientHttpConnector(
						HttpAsyncClients.custom()
						.setConnectionManager(connManager)
						.setDefaultRequestConfig(requestConfig)
						.build())) // HttpClient 커넥터 설정
				.exchangeStrategies(ExchangeStrategies.builder()
						.codecs(configurer -> configurer.defaultCodecs()
								.maxInMemorySize(CommonConstants.ONE_MB * maxInMemorySize)).build()) // 응답 데이터 매핑 전략 설정(최대 메모리 크기 설정)
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
		// 타임아웃 설정
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(naverConnectionRequestTimeout, TimeUnit.SECONDS) // 커넥션 풀에서 커넥션을 가져올 때의 타임아웃
				.setResponseTimeout(naverResponseTimeout, TimeUnit.SECONDS) // 서버 응답(읽기) 타임아웃
				.build();

		// 커넥션 풀 설정
		AsyncClientConnectionManager connManager = PoolingAsyncClientConnectionManagerBuilder.create()
				.setMaxConnTotal(naverMaxConnTotal) // 최대 커넥션 수
				.setMaxConnPerRoute(naverMaxConnPerRoute) // 라우트당 최대 커넥션 수
				.build();
		
		// WebClient 공통설정
		return WebClient.builder()
				.clientConnector(new HttpComponentsClientHttpConnector(
						HttpAsyncClients.custom()
						.setConnectionManager(connManager)
						.setDefaultRequestConfig(requestConfig)
						.build())) // HttpClient 커넥터 설정
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
		// 타임아웃 설정
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(kakaoConnectionRequestTimeout, TimeUnit.SECONDS) // 커넥션 풀에서 커넥션을 가져올 때의 타임아웃
				.setResponseTimeout(kakaoResponseTimeout, TimeUnit.SECONDS) // 서버 응답(읽기) 타임아웃
				.build();
		
		// 커넥션 풀 설정
		AsyncClientConnectionManager connManager = PoolingAsyncClientConnectionManagerBuilder.create()
				.setMaxConnTotal(kakaoMaxConnTotal) // 최대 커넥션 수
				.setMaxConnPerRoute(kakaoMaxConnPerRoute) // 라우트당 최대 커넥션 수
				.build();

		// WebClient 공통설정
		return WebClient.builder()
				.defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8") // 헤더에 전송하는 데이터 타입 설정
				.clientConnector(new HttpComponentsClientHttpConnector(
						HttpAsyncClients.custom()
						.setConnectionManager(connManager)
						.setDefaultRequestConfig(requestConfig)
						.build())) // HttpClient 커넥터 설정
				.exchangeStrategies(getExchangeStrategies()) // 응답 데이터 매핑 전략 설정
				.build();
	}

	/**
	 * WebClient에서 JSON 응답을 파싱할 때 사용할 ExchangeStrategies를 설정
	 * 
	 * @return ExchangeStrategies 인스턴스
	 */
	private ExchangeStrategies getExchangeStrategies() {
		// ObjectMapper(JSON <-> Java 객체 간 변환을 담당) 생성
		ObjectMapper objectMapper = new ObjectMapper();
		// JSON에서 오는 snake_case 키(ex:first_air_date)를 → Java의 camelCase 필드(ex:firstAirDate)에 자동으로 매핑하도록 설정
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		// 응답 JSON에 정의되고 DTO에 정의 되어있지 않아도 무시하는 설정
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return ExchangeStrategies.builder()
				.codecs(configurer -> {
					// 응답 시 최대 메모리 크기 설정
					configurer.defaultCodecs().maxInMemorySize(CommonConstants.ONE_MB * maxInMemorySize);
					// Decoder: JSON 응답 역직렬화 처리(응답(JSON → Java)에서 snake_case → camelCase 매핑)
					configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
					// Encoder: JSON 요청 직렬화 처리(요청(Java → JSON)에서 camelCase → snake_case 매핑)
					configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
				})
				.build();
	}
}
