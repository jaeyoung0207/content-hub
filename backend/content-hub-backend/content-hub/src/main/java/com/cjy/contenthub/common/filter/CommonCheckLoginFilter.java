package com.cjy.contenthub.common.filter;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.JwtValidateResultEnum;
import com.cjy.contenthub.common.repository.UserRepository;
import com.cjy.contenthub.common.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 공통 로그인 체크 필터 클래스 
 * 모든 요청에 대해 실행되며 OncePerRequestFilter를 상속받아 HTTP 요청에 대해 JWT 토큰의 유효성을 검사하고,
 * 유저 인증 상태를 확인하는 필터
 * 
 * @see OncePerRequestFilter
 */
@RequiredArgsConstructor
public class CommonCheckLoginFilter extends OncePerRequestFilter {
	
	/** JWT 유틸리티 클래스 */
	private final JwtUtil jwtUtil;
	
	/** User 리포지토리 */
	private final UserRepository repository;
	
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
		String pathInfo = request.getRequestURI();
		// 주소가 /login 으로 시작하는 주소는 처리대상에서 제외  
		if (pathInfo.matches("^/login/.*")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		// 헤더 추출
		String authorization = request.getHeader(CommonConstants.AUTHORIZATION_HEADER);
		
		// Authorization 헤더가 비어있거나 null인 경우, 필터 체인을 계속 진행(로그인하지 않은 유저도 접근 가능하도록 설정)
		// Authorization 헤더가 존재하고, 인증토큰 접두어가 포함된 경우에만 JWT 검증을 수행
		if (StringUtils.isNotEmpty(authorization) && authorization.startsWith(CommonConstants.AUTHORIZATION_HEADER_PREFIX)) {
			// JWT 추출
			String jwt = authorization.substring(CommonConstants.AUTHORIZATION_HEADER_PREFIX.length());
			// JWT 검증
			try {
				// JWT 토큰의 유효성 검사
				String validateResult = jwtUtil.validateToken(jwt);
				// 유효하지 않은 토큰인 경우, AccountExpiredException 예외를 발생시킴
				if (!JwtValidateResultEnum.VALID_TOKEN.getJwtValidateResultCode().equals(validateResult)) {
					throw new AccountExpiredException(JwtValidateResultEnum.getJwtValidateResult(validateResult).getJwtValidateResultMsg());
				}
			} catch (JwtException ex) {
				throw new AccountExpiredException("JWT 파싱 중 에러", ex);
			}

			// 토큰에서 id와 provider를 추출하여 user테이블에 존재하는지 확인
			// JWT에서 클레임 추출
			Claims claims = jwtUtil.parseClaims(jwt);
			// 클레임에서 providerId와 provider 추출
			String providerId = claims.getSubject();
			String provider = (String) claims.get("provider");
			// user 테이블에 등록되어 있는지 확인
			boolean isSaved = repository.existsByProviderAndProviderId(provider, providerId);
			// 유저가 존재하지 않는 경우, UsernameNotFoundException 예외를 발생시킴
			if(!isSaved) {
				throw new UsernameNotFoundException("유저ID가 존재하지 않습니다.");
			}
		}
		// 필터 체인을 계속 진행
		filterChain.doFilter(request, response);
	}
}
