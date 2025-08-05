package com.cjy.contenthub.common.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.cjy.contenthub.common.exception.CommonBusinessException;
import com.cjy.contenthub.common.util.SessionUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 공통 인터셉터 클래스
 * Spring MVC의 HandlerInterceptor 인터페이스를 구현하며, 모든 요청에 대해 실행됨
 * Controller 전후의 처리를 담당하는 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CommonInterceptor implements HandlerInterceptor {
	
	/** 공통 세션 유틸 */
	private final SessionUtil sessionUtil;
	
	/** 점검모드 */
	@Value("${app.maintenance.mode}")
	private boolean isMaintenanceMode;
	
	/** 서비스 점검중 메세지 */
	private static final String MAINTENANCE_MESSAGE = "서비스 점검중 입니다.";
	
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
		
		// 점검모드가 true인 경우에 점검화면 표시하도록 503에러
		if (isMaintenanceMode) {
			log.warn("서비스 점검중. 요청 URL: {}", request.getRequestURI());
			throw new CommonBusinessException(MAINTENANCE_MESSAGE, HttpStatus.SERVICE_UNAVAILABLE.value());
		}
		
		// 세션을 가져오거나 새로 생성
		HttpSession httpSession = request.getSession(false);
		
		// 세션이 없으면 새로 생성
		if (httpSession == null) {
			httpSession = request.getSession(true);
		}

		// 세션이 새로 생성되었을 때 로그 출력
		if (httpSession.isNew()) {
			log.debug("새로운 세션이 생성되었습니다. 세션 ID: {}", httpSession.getId());
		} else {
			log.debug("기존 세션이 사용됩니다. 세션 ID: {}", httpSession.getId());
		}
		
		// 세션 유틸리티 클래스에 세션 설정
		sessionUtil.setSession(httpSession);
		
		// true를 반환하여 요청 처리를 계속 진행
		return true;
	}
	  
}
