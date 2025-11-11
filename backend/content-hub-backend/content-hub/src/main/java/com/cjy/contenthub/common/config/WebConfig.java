package com.cjy.contenthub.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.interceptor.ApiRateLimitInterceptor;
import com.cjy.contenthub.common.interceptor.CommonInterceptor;
import com.cjy.contenthub.common.interceptor.DeviceIdCheckInterceptor;
import com.cjy.contenthub.common.properties.ApiPrefixProperties;

import lombok.RequiredArgsConstructor;

/**
 * 스프링 애플리케이션에서 웹 요청을 처리하는 데 필요한 설정을 포함하는 클래스
 * 스프링 MVC의 WebMvcConfigurer 인터페이스를 구현하여 CORS 설정과 인터셉터 등록을 정의
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	/** 공통 인터셉터 */
	private final CommonInterceptor commonInterceptor;

	/** API Rate Limit 인터셉터 */
	private final ApiRateLimitInterceptor apiRateLimitInterceptor;
	
	/** Device ID 체크 인터셉터 */
	private final DeviceIdCheckInterceptor deviceIdCheckInterceptor;

	/** 어플리케이션 URL(프론트엔드) */
	@Value("${app.url}")
	private String appUrl;

	/** CORS 활성화 여부 */
	@Value("${app.cors.enabled:true}")
	private boolean isCorsEnabled;

	/** API 접두사 및 버전 설정 */
	private final ApiPrefixProperties apiPrefixProperties;

	/**
	 * CORS(Cross-Origin Resource Sharing) 설정을 정의하여 특정 도메인에서의 요청을 허용하고,
	 * 허용할 HTTP 메서드와 헤더를 지정하며, 응답에 credentials(쿠키, 인증 헤더)를 포함할 수 있는지 여부를 설정
	 * CORS 설정은 클라이언트 측에서 다른 도메인으로 요청을 보낼 때 발생하는 브라우저 보안 정책을 우회하기 위해 사용됨
	 * 
	 * @param registry CORS 설정을 위한 CorsRegistry 객체
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// CORS 활성화가 설정된 경우에만 CORS 설정 적용
		if (isCorsEnabled) {
			registry.addMapping("/**") // CORS를 적용할 URL 패턴을 정의
			.allowedOrigins(appUrl) // resources를 공유할 URL을 지정(IP주소:포트번호)
			.allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), 
					HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name()) // 허용할 HTTP method를 지정
			.allowedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, CommonConstants.CSRF_TOKEN_HEADER) // 클라이언트 측의 CORS 요청에 허용되는 헤더를 지정
			.exposedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, CommonConstants.CSRF_TOKEN_HEADER, HttpHeaders.RETRY_AFTER) // 클라이언트 측에서 접근할 수 있는 응답 헤더를 지정
			.allowCredentials(true) // 클라이언트 측에 대한 응답에 credentials(쿠키, 인증 헤더)를 포함할 수 있는지 여부를 지정(true 설정시, allowedOrigins에 와일드카드(*) 설정 불가)
			.maxAge(3600); // 지정한 시간만큼 preflight 리퀘스트(정식 요청처리 전에 OPTIONS 메소드로 사전에 CORS위반을 확인하기 위한 요청 처리)를 캐싱
		}
	}

	/**
	 * 인터셉터 등록 설정
	 * 인터셉터는 특정 경로에 대한 요청을 가로채고, 요청 처리 전후에 공통 작업을 수행할 수 있도록 함
	 * 
	 * @param registry 인터셉터 등록을 위한 InterceptorRegistry 객체
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 접두사 및 버전
		String fullPrefix = apiPrefixProperties.getFullPrefix();
		// 인터셉터 등록
		registry.addInterceptor(deviceIdCheckInterceptor)
		.addPathPatterns("/**") // 모든 경로에서 적용
		.excludePathPatterns(fullPrefix.concat("/app/**")); // 이 경로에서는 제외
		registry.addInterceptor(commonInterceptor)
		.addPathPatterns(fullPrefix.concat("/search/*"), fullPrefix.concat("/detail/*"), fullPrefix.concat("/app/*")); // 이 경로에서만 적용
		registry.addInterceptor(apiRateLimitInterceptor)
		.addPathPatterns("/**") // 모든 경로에서 적용
		.excludePathPatterns(fullPrefix.concat("/app/**"), fullPrefix.concat("/error")); // 이 경로에서는 제외

	}

}
