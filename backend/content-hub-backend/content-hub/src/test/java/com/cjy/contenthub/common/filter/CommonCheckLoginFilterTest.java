package com.cjy.contenthub.common.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import com.cjy.contenthub.common.constants.CommonEnum.JwtValidateResultEnum;
import com.cjy.contenthub.common.exception.CommonJwtException;
import com.cjy.contenthub.common.properties.ApiPrefixProperties;
import com.cjy.contenthub.common.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class CommonCheckLoginFilterTest {
	
	CommonCheckLoginFilter commonCheckLoginFilter;
	
	@Mock
	JwtUtil jwtUtil;
	
	@Mock
	ApiPrefixProperties apiPrefixProperties;
	
	@Mock
	HttpServletRequest request;
	
	@Mock
	HttpServletResponse response;
	
	@Mock
	FilterChain filterChain;
	
	@BeforeEach
	void setUp() {
		// 테스트 대상 초기화
		commonCheckLoginFilter = new CommonCheckLoginFilter(
				jwtUtil, 
				apiPrefixProperties);
		// SecurityContext 초기화
		SecurityContextHolder.clearContext();
	}
	
	@Test
	@DisplayName("doFilterInternal: 공통 로그인 체크 필터 - JWT 검증 성공")
	void test_doFilterInternal_jwtCheckSuccess() throws ServletException, IOException {
		
		String uri = "/api/home/rankings";
		String method = "GET";
		String fullPrefix = "/api";
		String jwt = "header.paload.signature";
		String authorization = "Bearer " + jwt;
		String validationResult = "0"; // VALID
		Claims claims = Mockito.mock(Claims.class);
		String providerId = "user123";
		
		when(request.getRequestURI()).thenReturn(uri);
		when(request.getMethod()).thenReturn(method);
		when(apiPrefixProperties.getFullPrefix()).thenReturn(fullPrefix);
		when(request.getHeader("Authorization")).thenReturn(authorization);
		when(jwtUtil.validateToken(jwt)).thenReturn(validationResult);
		when(jwtUtil.parseClaims(jwt)).thenReturn(claims);
		when(claims.getSubject()).thenReturn(providerId);
		
		// 실제 메서드 호출
		commonCheckLoginFilter.doFilterInternal(request, response, filterChain);
		
		// SecurityContext에 인증 정보가 설정되었는지 확인
		assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
		.isEqualTo(providerId);
		
		// verify 호출 횟수 및 매개변수 검증
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getMethod();
		verify(apiPrefixProperties, times(1)).getFullPrefix();
		verify(request, times(1)).getHeader("Authorization");
		verify(jwtUtil, times(1)).validateToken(jwt);
		verify(filterChain, times(1)).doFilter(request, response);
	}
	
	@ParameterizedTest
	@MethodSource("uriAndMethodParams")
	@DisplayName("doFilterInternal: 공통 로그인 체크 필터 - JWT 검증 건너뛰기")
	void test_doFilterInternal_skipJwtCheck(String uri, String method) throws ServletException, IOException {
		
		String fullPrefix = "/api";
		
		when(request.getRequestURI()).thenReturn(uri);
		when(request.getMethod()).thenReturn(method);
		when(apiPrefixProperties.getFullPrefix()).thenReturn(fullPrefix);
		
		// 실제 메서드 호출
		commonCheckLoginFilter.doFilterInternal(request, response, filterChain);
		
		// SecurityContext에 인증 정보가 설정되지 않았는지 확인
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		
		// verify 호출 횟수 및 매개변수 검증
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getMethod();
		verify(apiPrefixProperties, times(1)).getFullPrefix();
		verify(filterChain, times(1)).doFilter(request, response);
	}
	
	static Stream<Arguments> uriAndMethodParams() {
		return Stream.of(
				Arguments.of("/api/home/rankings", "OPTIONS"), 
				Arguments.of("/api/login/getNaverLoginInfo", "GET"),
				Arguments.of("/api/app/getCsrfToken", "GET")
				);
	}
	
	@Test
	@DisplayName("doFilterInternal: 공통 로그인 체크 필터 - JWT 검증 실패")
	void test_doFilterInternal_jwtCheckFailure() throws ServletException, IOException {
		
		String uri = "/api/home/rankings";
		String method = "GET";
		String fullPrefix = "/api";
		String jwt = "header.paload.signature";
		String authorization = "Bearer " + jwt;
		String validationResult = "1"; // EXPIRED
		
		when(request.getRequestURI()).thenReturn(uri);
		when(request.getMethod()).thenReturn(method);
		when(apiPrefixProperties.getFullPrefix()).thenReturn(fullPrefix);
		when(request.getHeader("Authorization")).thenReturn(authorization);
		when(jwtUtil.validateToken(jwt)).thenReturn(validationResult);
		
		// 실제 메서드 호출
		assertThatThrownBy(
				() -> commonCheckLoginFilter.doFilterInternal(request, response, filterChain))
		.isInstanceOf(CommonJwtException.class)
		.hasMessage(JwtValidateResultEnum.getJwtValidateResult(validationResult).getJwtValidateResultMsg());
		
		// SecurityContext에 인증 정보가 설정되지 않았는지 확인
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		
		// verify 호출 횟수 및 매개변수 검증
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getMethod();
		verify(apiPrefixProperties, times(1)).getFullPrefix();
		verify(request, times(1)).getHeader("Authorization");
		verify(filterChain, times(0)).doFilter(request, response);
	}
	
	@ParameterizedTest
	@MethodSource("jwtParams")
	@DisplayName("doFilterInternal: 공통 로그인 체크 필터 - Authorization 헤더 없음")
	void test_doFilterInternal_notExistAuthorization(String jwt) throws ServletException, IOException {
		
		String uri = "/api/home/rankings"; 
		String method = "GET";
		String fullPrefix = "/api";
		
		when(request.getRequestURI()).thenReturn(uri);
		when(request.getMethod()).thenReturn(method);
		when(apiPrefixProperties.getFullPrefix()).thenReturn(fullPrefix);
		when(request.getHeader("Authorization")).thenReturn(jwt);
		
		// 실제 메서드 호출
		commonCheckLoginFilter.doFilterInternal(request, response, filterChain);
		
		// SecurityContext에 인증 정보가 설정되지 않았는지 확인
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		
		// verify 호출 횟수 및 매개변수 검증
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getMethod();
		verify(apiPrefixProperties, times(1)).getFullPrefix();
		verify(request, times(1)).getHeader("Authorization");
		verify(filterChain, times(1)).doFilter(request, response);
	}
	
	static Stream<Arguments> jwtParams() {
		return Stream.of(
				Arguments.of(""), 
				Arguments.of("header.paload.signature")
				);
	}

}
