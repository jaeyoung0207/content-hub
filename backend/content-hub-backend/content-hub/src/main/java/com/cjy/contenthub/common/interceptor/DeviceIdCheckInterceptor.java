package com.cjy.contenthub.common.interceptor;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMessagesErrorEnum;
import com.cjy.contenthub.common.exception.DeviceIdNotFoundException;
import com.cjy.contenthub.common.properties.ApiPrefixProperties;
import com.cjy.contenthub.common.properties.ApiRateLimitProperties;
import com.cjy.contenthub.common.properties.ApiRateLimitProperties.ApiRateLimitRules;
import com.cjy.contenthub.common.util.CookieUtil;
import com.cjy.contenthub.common.util.MessageUtil;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Device ID 체크 인터셉터 클래스 
 * 특정 API 요청에 대해 Device ID 쿠키가 존재하는지 확인
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceIdCheckInterceptor implements HandlerInterceptor {
	
	/** 메시지 유틸 */
	private final MessageUtil messageUtil;
	
	/** 쿠키 유틸 */
	private final CookieUtil cookieUtil;
	
	/** API Rate Limit 설정 */
	private final ApiRateLimitProperties apiRateLimitProperties;
	
	/** API 접두사 및 버전 설정 */
	private final ApiPrefixProperties apiPrefixProperties;
	
	/** 전체 경로 리스트 */
	private List<String> fullPathList;
	
	/**
	 * 초기화 메소드
	 * 어플리케이션 기동시 실행됨
	 * API Rate Limit 규칙에서 전체 경로 리스트를 생성
	 */
	@PostConstruct
	private void init() {
		List<ApiRateLimitRules> rules = apiRateLimitProperties.getRules();
		String fullPrefix = apiPrefixProperties.getFullPrefix();
		fullPathList = rules.stream().map(rule -> fullPrefix + rule.getPath()).toList();
	}
	
	/**
	 * 요청 처리 전 실행되는 메소드
	 * 
	 * @param request  HTTP 요청 객체
	 * @param response HTTP 응답 객체
	 * @param handler  핸들러 객체
	 * @return boolean true: 요청 처리 계속 진행, false: 요청 처리 중단
	 * @throws Exception 예외 발생 시
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		// 요청 URI 가져오기
		String uri = request.getRequestURI();
		
		// OPTIONS 요청이거나, 경로 리스트에 URI가 포함되지 않으면 통과
		if (HttpMethod.OPTIONS.matches(request.getMethod()) || !fullPathList.contains(uri)) {
			return true;
		}
		
		// 쿠키에서 Device ID 값 가져오기
		String deviceId = cookieUtil.getCookieValue(request, CommonConstants.DEVICE_ID);
		
		// Device ID 값이 없으면 예외 발생
		if (StringUtils.isEmpty(deviceId)) {
			throw new DeviceIdNotFoundException(messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_DEVICE_ID_NOT_FOUND.getMessageCode()), 
					HttpStatus.UNAUTHORIZED.value());
		}
		return true;
	}

}
