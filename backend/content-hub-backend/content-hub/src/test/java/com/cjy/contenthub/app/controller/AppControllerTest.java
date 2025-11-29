package com.cjy.contenthub.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cjy.contenthub.app.controller.dto.AppLoginCookiesResponseDto;
import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.util.CookieUtil;
import com.cjy.contenthub.common.util.SessionUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AppControllerTest {

	@InjectMocks
	private AppController appController;

	@Mock
	private SessionUtil sessionUtil;

	@Mock
	private CookieUtil cookieUtil;

	@Mock
	private HttpServletRequest mockRequest;

	@Mock
	private HttpServletResponse mockResponse;

	@Test
	@DisplayName("getLoginCookies: 쿠키가 존재하지 않는 경우")
	void test_getLoginCookies_notExistCookie() {
		// Mock 설정
		when(mockRequest.getCookies()).thenReturn(null);
		String deviceId = UUID.randomUUID().toString();
		when(cookieUtil.setDeviceId(mockResponse)).thenReturn(deviceId);

		// 테스트 대상 메서드 호출
		AppLoginCookiesResponseDto resultDto = appController.getLoginCookies(mockRequest, mockResponse);

		// 결과 검증
		assertThat(resultDto.isHasRefreshToken()).isFalse();
		assertThat(resultDto.getProvider()).isNull();
		assertThat(resultDto.getDeviceId()).isEqualTo(deviceId);
		
		// cookieUtil.setDeviceId 메서드가 한 번 호출되었는지 검증
		verify(cookieUtil, times(1)).setDeviceId(mockResponse);
	}

	@Test
	@DisplayName("getLoginCookies: 쿠키가 존재하는 경우 - 디바이스 ID, 리프레시 토큰, 제공자")
	void test_getLoginCookies_existAllCookies() {
		// Mock 설정
		String provider = "NAVER";
		String deviceId = UUID.randomUUID().toString();
		Cookie refreshTokenCookie = new Cookie(CommonConstants.REFRESH_TOKEN, "refresh_token");
		Cookie providerCookie = new Cookie(CommonConstants.PROVIDER, provider);
		Cookie deviceIdCookie = new Cookie(CommonConstants.DEVICE_ID, deviceId);
		when(mockRequest.getCookies()).thenReturn(new Cookie[] { refreshTokenCookie, deviceIdCookie, providerCookie });

		// 테스트 대상 메서드 호출
		AppLoginCookiesResponseDto resultDto = appController.getLoginCookies(mockRequest, mockResponse);

		// 결과 검증
		assertThat(resultDto.getProvider()).isEqualTo(providerCookie.getValue());
		assertThat(resultDto.isHasRefreshToken()).isTrue();
		assertThat(resultDto.getDeviceId()).isEqualTo(deviceIdCookie.getValue());
		
		// cookieUtil.setDeviceId 메서드가 호출되지 않았는지 검증
		verifyNoInteractions(cookieUtil);
	}

	@Test
	@DisplayName("getLoginCookies: 쿠키가 존재하는 경우 - 리프레시 토큰, 제공자")
	void test_getLoginCookies_notExistDeviceIdCookie() {
		// Mock 설정
		String provider = "NAVER";
		Cookie refreshTokenCookie = new Cookie(CommonConstants.REFRESH_TOKEN, "refresh_token");
		Cookie providerCookie = new Cookie(CommonConstants.PROVIDER, provider);
		Cookie dummyCookie = new Cookie("DUMMY", null);
		when(mockRequest.getCookies()).thenReturn(new Cookie[] { refreshTokenCookie, providerCookie, dummyCookie });
		String deviceId = UUID.randomUUID().toString();
		when(cookieUtil.setDeviceId(mockResponse)).thenReturn(deviceId);

		// 테스트 대상 메서드 호출
		AppLoginCookiesResponseDto resultDto = appController.getLoginCookies(mockRequest, mockResponse);

		// 결과 검증
		assertThat(resultDto.isHasRefreshToken()).isTrue();
		assertThat(resultDto.getProvider()).isEqualTo(providerCookie.getValue());
		assertThat(resultDto.getDeviceId()).isEqualTo(deviceId);
		
		// cookieUtil.setDeviceId 메서드가 한 번 호출되었는지 검증
		verify(cookieUtil, times(1)).setDeviceId(mockResponse);
	}

}
