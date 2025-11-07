package com.cjy.contenthub.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.stereotype.Component;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.properties.LoginCookieProperties;
import com.cjy.contenthub.common.record.CommonRecords.LoginCookiesRecord;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * 쿠키 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
public class CookieUtil {
	
	/** 로그인 쿠키 프로퍼티 */
	private final LoginCookieProperties loginCookieProperties;
	
	/**
	 * 로그인 쿠키 생성(등록용)
	 * 
	 * @param refreshToken 리프레시 토큰
	 * @param provider     소셜 로그인 제공자
	 * @param expiresIn    만료 시간(초)
	 * @return LoginCookiesInfo 로그인 쿠키 정보
	 */
	public LoginCookiesRecord getLoginCookiesForRegister(String refreshToken, String provider, long expiresIn) {
		// 로그인 쿠키 정보 반환
		return getLoginCookies(refreshToken, provider, expiresIn);
	}
	
	/**
	 * 로그인 쿠키 생성(삭제용)
	 * 
	 * @return LoginCookiesInfo 로그인 쿠키 정보
	 */
	public LoginCookiesRecord getLoginCookiesForDelete() {
		// 로그인 쿠키 정보 반환
		return getLoginCookies("", "", 0);
	}
	
	/**
	 * 로그인 쿠키 생성 공통 메소드
	 * 
	 * @param refreshToken 리프레시 토큰
	 * @param provider     소셜 로그인 제공자
	 * @param expiresIn    만료 시간(초)
	 * @return LoginCookiesInfo 로그인 쿠키 정보
	 */
	public LoginCookiesRecord getLoginCookies(String refreshToken, String provider, long expiresIn) {
		// 리프레시 토큰 쿠키
		ResponseCookieBuilder refreshTokenCookieBuilder = ResponseCookie.from(CommonConstants.REFRESH_TOKEN, refreshToken)
				.httpOnly(loginCookieProperties.isHttpOnly())
				.secure(loginCookieProperties.isSecure())
				.sameSite(loginCookieProperties.getSameSite())
				.path(loginCookieProperties.getPath())
				.maxAge(expiresIn);
		// provider 쿠키
		ResponseCookieBuilder providerCookieBuilder = ResponseCookie.from(CommonConstants.PROVIDER, provider)
				.httpOnly(loginCookieProperties.isHttpOnly())
				.secure(loginCookieProperties.isSecure())
				.sameSite(loginCookieProperties.getSameSite())
				.path(loginCookieProperties.getPath())
				.maxAge(expiresIn);
		// 도메인 설정
		if (StringUtils.isNotEmpty(loginCookieProperties.getDomain())) {
			refreshTokenCookieBuilder.domain(loginCookieProperties.getDomain());
			providerCookieBuilder.domain(loginCookieProperties.getDomain());
		}
		// 쿠키 빌드
		ResponseCookie refreshTokenCookie = refreshTokenCookieBuilder.build();
		ResponseCookie providerCookie = providerCookieBuilder.build();
		
		// 로그인 쿠키 정보 반환
		return new LoginCookiesRecord(providerCookie.toString(), refreshTokenCookie.toString());
	}
	
	/**
	 * 특정 쿠키 값 조회
	 * 
	 * @param request    HttpServletRequest
	 * @param cookieName 쿠키 이름
	 * @return String 쿠키 값
	 */
	public String getCookieValue(HttpServletRequest request, String cookieName) {
		String cookieValue = "";
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(cookieName)) {
					cookieValue = cookie.getValue();
					break;
				}
			}
		}
		return cookieValue;
	}
	
	/**
	 * 쿠키에서 로그인 유저의 리프레시 토큰 추출
	 *
	 * @param request HttpServletRequest
	 * @param provider 로그인 제공자
	 * @return 리프레시 토큰
	 */
	public String getRefreshToken(HttpServletRequest request, String provider) {
		// 쿠키 추출
		String refreshToken = null;
		Cookie[] cookies = request.getCookies();
		// 쿠키가 존재하는 경우
		if (cookies != null) {
			List<Cookie> cookieList = new ArrayList<>();
			if (Arrays.stream(cookies)
					.anyMatch(c -> 
					StringUtils.equals(c.getName(), CommonConstants.PROVIDER) && // 쿠키 이름이 PROVIDER이고
					StringUtils.equals(c.getValue(), provider)) // 쿠키 값이 provider 파라미터 값과 일치하는 경우
					) {
				// 쿠키이름이 리프레시 토큰인 쿠키 추출
				cookieList = Arrays.stream(cookies)
						.filter(c -> StringUtils.equals(c.getName(), CommonConstants.REFRESH_TOKEN))
						.toList();
			}
			// 쿠키가 존재하는 경우
			if (!ObjectUtils.isEmpty(cookieList)) {
				// 리프레시 토큰 값 추출
				refreshToken = cookieList.get(0).getValue();
			}
		}
		// 리프레시 토큰 반환
		return refreshToken;
	}

}
