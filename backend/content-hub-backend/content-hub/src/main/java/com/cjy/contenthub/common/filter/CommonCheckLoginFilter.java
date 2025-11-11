package com.cjy.contenthub.common.filter;

import java.io.IOException;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMessagesErrorEnum;
import com.cjy.contenthub.common.constants.CommonEnum.JwtValidateResultEnum;
import com.cjy.contenthub.common.exception.CommonJwtException;
import com.cjy.contenthub.common.properties.ApiPrefixProperties;
import com.cjy.contenthub.common.util.JwtUtil;
import com.cjy.contenthub.common.util.MessageUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 공통 로그인 체크 필터 클래스
 * JWT 토큰을 검증하고, 유효한 경우 인증 정보를 SecurityContext에 설정
 * 
 * @see OncePerRequestFilter
 */
@RequiredArgsConstructor
public class CommonCheckLoginFilter extends OncePerRequestFilter {
	
	/** JWT 유틸리티 클래스 */
	private final JwtUtil jwtUtil;
	
	/** 메시지 유틸리티 클래스 */
	private final MessageUtil messageUtil;
	
	/** API 접두사 및 버전 설정 */
	private final ApiPrefixProperties apiPrefixProperties;
	
	/**
	 * 필터가 적용될 URL 패턴을 정의
	 * 모든 요청에 대해 필터가 적용되도록 설정
	 * 
	 * @return String[] 필터가 적용될 URL 패턴
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// 요청 URI 추출
		String uri = request.getRequestURI();
		// HTTP 메소드 추출
		String method = request.getMethod();
		// API 접두사 및 버전 정보 추출
		String fullPrefix = apiPrefixProperties.getFullPrefix();
		// 주소가 /login 으로 시작하는 주소는 처리대상에서 제외
		if (HttpMethod.OPTIONS.matches(method) || uri.matches(fullPrefix.concat("/login/.*"))) {
			filterChain.doFilter(request, response);
			return;
		}
		
		// 헤더 추출
		String authorization = request.getHeader(CommonConstants.AUTHORIZATION_HEADER);
		
		// Authorization 헤더가 존재하고, 인증토큰 접두어가 포함된 경우에만 JWT 검증을 수행
		if (StringUtils.isNotEmpty(authorization) && authorization.startsWith(CommonConstants.AUTHORIZATION_HEADER_PREFIX)) {
			// JWT 추출
			String jwt = authorization.substring(CommonConstants.AUTHORIZATION_HEADER_PREFIX.length());
			// JWT 검증
			try {
				// JWT 토큰의 유효성 검사
				String validateResult = jwtUtil.validateToken(jwt);
				// 유효하지 않은 토큰인 경우, 예외를 발생시킴
				if (!JwtValidateResultEnum.VALID_TOKEN.getJwtValidateResultCode().equals(validateResult)) {
					throw new CommonJwtException(JwtValidateResultEnum.getJwtValidateResult(validateResult).getJwtValidateResultMsg());
				}
			} catch (JwtException ex) {
				throw new CommonJwtException(
						messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_JWT_PARSING.getMessageCode()), ex);
			}

			// JWT에서 클레임 추출
			Claims claims = jwtUtil.parseClaims(jwt);
			// 클레임에서 providerId와 provider 추출
			String providerId = claims.getSubject();
			
			// 인증 객체 생성
			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(providerId, null, Collections.emptyList());
			// SecurityContext에 인증 객체 세팅
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		// 필터 체인을 계속 진행
		filterChain.doFilter(request, response);
	}
}
