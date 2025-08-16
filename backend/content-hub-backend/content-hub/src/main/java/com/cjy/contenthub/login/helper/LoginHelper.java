package com.cjy.contenthub.login.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.cjy.contenthub.common.constants.CommonConstants;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 로그인 관련 유틸리티 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginHelper {

	/**
	 * 쿠키에서 로그인 유저의 프로바이더 정보를 추출
	 *
	 * @param request HttpServletRequest
	 * @param provider 로그인 제공자
	 * @return 리프레시 토큰
	 */
	public static String getRefreshToken(HttpServletRequest request, String provider) {
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
