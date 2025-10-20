package com.cjy.contenthub.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import com.cjy.contenthub.common.filter.CommonAuthenticationEntryPoint;
import com.cjy.contenthub.common.filter.CommonCheckLoginFilter;
import com.cjy.contenthub.common.properties.ApiPrefixProperties;
import com.cjy.contenthub.common.util.JwtUtil;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.common.constants.CommonConstants;

import lombok.RequiredArgsConstructor;

/**
 * 스프링 세큐리티 설정 클래스 
 * 스프링 세큐리티의 보안 필터 체인을 구성하여 HTTP 요청에 대한 보안 설정을 정의
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	/** JWT 유틸리티 클래스 */
	private final JwtUtil jwtUtil;
	
	/** 메시지 유틸리티 클래스 */
	private final MessageUtil messageUtil;
	
	/** 인증 예외 처리 필터 */
	private final CommonAuthenticationEntryPoint authenticationEntryPoint;
	
	/** API 접두사 및 버전 설정 */
	private final ApiPrefixProperties apiPrefixProperties;

	/**
	 * 스프링 세큐리티 설정
	 * HttpSecurity 객체를 사용하여 HTTP 요청에 대한 보안 설정을 구성
	 * CommonCheckLoginFilter를 사용하여 JWT 토큰의 유효성을 검사하고 유저의 인증 상태를 확인
     * 유저 인증 실패 시 CommonAuthenticationEntryPoint를 통해 예외를 처리
	 * 
	 * @param httpSecurity
	 * @return SecurityFilterChain
	 * @throws Exception
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		
		// API 접두사 및 버전
		String fullPrefix = apiPrefixProperties.getFullPrefix();
		
		// CSRF TOKEN 쿠키 저장소 설정 
		CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
		csrfTokenRepository.setCookieCustomizer(cookie -> 
			cookie.path("/").sameSite("lax").secure(false).build()
		);
		// HTTP 보안 설정
		httpSecurity
		.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS preflight 요청 허용
				.requestMatchers(fullPrefix.concat(CommonConstants.WISHLIST_PATH).concat("/**")).authenticated() // 위시리스트 경로에 대한 요청은 인증 필요
				.requestMatchers(fullPrefix.concat("/my/**")).authenticated() // my 하위 경로에 대한 요청은 인증 필요
				.anyRequest().permitAll() // 로그인 없이 접근 가능하므로, 모든 요청에 대해 인증 없이 접근 허용
				)
		.httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 비활성화
//		.csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
		.csrf(csrf -> csrf
				.csrfTokenRepository(csrfTokenRepository)
				.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
				.ignoringRequestMatchers(fullPrefix.concat("/login/**"))
				) // CSRF 보호 활성화
		.addFilterBefore(new CommonCheckLoginFilter(jwtUtil, messageUtil), UsernamePasswordAuthenticationFilter.class) // JWT 인증 필터
		.addFilterBefore(new ExceptionTranslationFilter(authenticationEntryPoint), CommonCheckLoginFilter.class); // 예외 처리 필터

		// HTTP 보안 설정을 빌드하여 반환
		return httpSecurity.build();
	}
}
