package com.cjy.contenthub.common.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.test.util.ReflectionTestUtils;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.properties.ApiPrefixProperties;
import com.cjy.contenthub.common.properties.ApiRateLimitProperties;
import com.cjy.contenthub.common.properties.ApiRateLimitProperties.ApiRateLimitRules;
import com.cjy.contenthub.common.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class) 
class DeviceIdCheckInterceptorTest {
	
	@InjectMocks
	DeviceIdCheckInterceptor deviceIdCheckInterceptor;
	
	@Mock
	private CookieUtil cookieUtil;
	
	private ApiRateLimitProperties apiRateLimitProperties = new ApiRateLimitProperties();
	
	private ApiPrefixProperties apiPrefixProperties = new ApiPrefixProperties();
	
	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;
	
	@Mock
	private Object handler;
	
	private List<String> fullPathList;
	
	@BeforeEach
	void setUp() {
        // 전체 경로 리스트 설정
		fullPathList = List.of("/api/home/rankings", "/api/search/searchKeyword");
		// 의존성 주입
		ReflectionTestUtils.setField(deviceIdCheckInterceptor, "fullPathList", fullPathList);
		ReflectionTestUtils.setField(deviceIdCheckInterceptor, "apiRateLimitProperties", apiRateLimitProperties);
        ReflectionTestUtils.setField(deviceIdCheckInterceptor, "apiPrefixProperties", apiPrefixProperties);
	}
	
	@Test
	@DisplayName("init: 전체 경로 리스트 초기화")
	void test_init() {
		// 값 설정
		List<ApiRateLimitRules> rules = new ArrayList<>();
		ApiRateLimitRules rule1 = new ApiRateLimitRules();
		rule1.setPath("/home/rankings");
		rule1.setMaxRequestCount(100);
		rule1.setSeconds(60);
		rules.add(rule1);
		ApiRateLimitRules rule2 = new ApiRateLimitRules();
		rule2.setPath("/search/searchKeyword");
		rule2.setMaxRequestCount(100);
		rule2.setSeconds(60);
		rules.add(rule2);
		apiRateLimitProperties.setRules(rules);
		apiPrefixProperties.setPrefix("/api");
		
		// 초기화 메서드 호출
		ReflectionTestUtils.invokeMethod(deviceIdCheckInterceptor, "init");
		
		// fullPathList 필드 검증
		List<String> expectedFullPathList = List.of("/api/home/rankings", "/api/search/searchKeyword");
		assertThat(deviceIdCheckInterceptor).extracting("fullPathList").isEqualTo(expectedFullPathList);
	}
	
	@Test
	@DisplayName("preHandle: OPTIONS 메서드 요청")
	void test_preHandle_optionsMethod() throws Exception {
		// Mock 설정
		when(request.getRequestURI()).thenReturn("/api/home/rankings");
		when(request.getMethod()).thenReturn(HttpMethod.OPTIONS.name());
		
		// 테스트 대상 메서드 호출
		boolean result = deviceIdCheckInterceptor.preHandle(request, response, handler);
		
		// 결과 검증
		assertThat(result).isTrue();
		
		// 쿠키 유틸리티가 호출되지 않았는지 검증
		verifyNoInteractions(cookieUtil);
	}
	
	@Test
	@DisplayName("preHandle: 대상 URI가 아닌 경우")
	void test_preHandle_notTargetUri() throws Exception {
		// Mock 설정
		String uri = "/api/app/getMediaTypes";
		when(request.getRequestURI()).thenReturn(uri);
		when(request.getMethod()).thenReturn(HttpMethod.GET.name());
		
		// 테스트 대상 메서드 호출
		boolean result = deviceIdCheckInterceptor.preHandle(request, response, handler);
		
		// 결과 검증
		assertThat(result).isTrue();
		
		// 쿠키 유틸리티가 호출되지 않았는지 검증
		verifyNoInteractions(cookieUtil);
	}
	
	@Test
	@DisplayName("preHandle: 대상 URI이면서 디바이스 ID가 있는 경우")
	void test_preHandle_targetUriAndExistDeviceId() throws Exception {
		// Mock 설정
		String uri = "/api/home/rankings";
		when(request.getRequestURI()).thenReturn(uri);
		when(request.getMethod()).thenReturn(HttpMethod.GET.name());
		String deviceId = UUID.randomUUID().toString();
		when(cookieUtil.getCookieValue(request, CommonConstants.DEVICE_ID)).thenReturn(deviceId);
		
		// 테스트 대상 메서드 호출
		boolean result = deviceIdCheckInterceptor.preHandle(request, response, handler);
		
		// 결과 검증
		assertThat(result).isTrue();
		
		// cookieUtil.setDeviceId 메서드가 호출되지 않았는지 검증
		verify(cookieUtil, never()).setDeviceId(any());
	}
	
	@Test
	@DisplayName("preHandle: 대상 URI이면서 디바이스 ID가 없는 경우")
	void test_preHandle_targetUriAndNotExistDeviceId() throws Exception {
		// Mock 설정
		String uri = "/api/home/rankings";
		when(request.getRequestURI()).thenReturn(uri);
		when(request.getMethod()).thenReturn(HttpMethod.GET.name());
		String deviceId = UUID.randomUUID().toString();
		when(cookieUtil.getCookieValue(request, CommonConstants.DEVICE_ID)).thenReturn("");
		when(cookieUtil.setDeviceId(response)).thenReturn(deviceId);
		
		// 테스트 대상 메서드 호출
		boolean result = deviceIdCheckInterceptor.preHandle(request, response, handler);
		
		// 결과 검증
		assertThat(result).isTrue();
		
		// cookieUtil.setDeviceId 메서드가 호출되었는지 검증
		verify(cookieUtil, times(1)).setDeviceId(response);
	}

}
