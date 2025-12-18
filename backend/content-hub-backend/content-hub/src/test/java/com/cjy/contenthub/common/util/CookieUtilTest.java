package com.cjy.contenthub.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import com.cjy.contenthub.common.properties.LoginCookieProperties;

import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class CookieUtilTest {
	
    private CookieUtil cookieUtil;
    
    private LoginCookieProperties loginCookieProperties;
    
    @Mock
    private HttpServletResponse response;
    
    @BeforeEach
    void setUp() {
    	// 로그인 쿠키 프로퍼티 설정
        loginCookieProperties = new LoginCookieProperties();
        loginCookieProperties.setHttpOnly(true);
        loginCookieProperties.setSecure(false);
        loginCookieProperties.setSameSite("Lax");
        // 쿠키 유틸 초기화
        cookieUtil = new CookieUtil(loginCookieProperties);
    }
	
	@Test
	@DisplayName("[UT]setDeviceId: 디바이스 ID 생성 및 쿠키 설정")
	void test_setDeviceId_createDeviceIdAndSetCookie() {
		// 테스트 대상 메서드 호출
		String deviceId = cookieUtil.setDeviceId(response);
		
		// 디바이스 ID 검증
		assertThat(deviceId).isNotNull();		
		assertThat(UUID.fromString(deviceId)).isNotNull();
		
		// 응답 헤더에 쿠키 설정 검증
		verify(response).setHeader(eq(HttpHeaders.SET_COOKIE), contains("deviceId=" + deviceId));
	}

}
