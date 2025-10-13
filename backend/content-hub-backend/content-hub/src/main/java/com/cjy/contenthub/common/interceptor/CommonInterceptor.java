package com.cjy.contenthub.common.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.cjy.contenthub.common.constants.CommonEnum.MessagesDebugEnum;
import com.cjy.contenthub.common.constants.CommonEnum.MessagesErrorEnum;
import com.cjy.contenthub.common.exception.CommonBusinessException;
import com.cjy.contenthub.common.util.MessageUtil;
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
	
	/** 메시지 유틸 */
	private final MessageUtil messageUtil;
	
	/** 점검모드 */
	@Value("${app.maintenance.mode}")
	private boolean isMaintenanceMode;
	
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
			throw new CommonBusinessException(
					messageUtil.getMessageKO(MessagesErrorEnum.ERROR_COMMON_MAINTENANCE.getMessageCode()), HttpStatus.SERVICE_UNAVAILABLE.value());
		}
		
		// 세션을 가져오거나 새로 생성
		HttpSession httpSession = request.getSession(false);
		
		// 세션이 없으면 새로 생성
		if (httpSession == null) {
			httpSession = request.getSession(true);
		}

		// 세션이 새로 생성되었을 때 로그 출력
		Object[] messageParams = { httpSession.getId() };
		if (httpSession.isNew()) {
			log.debug(messageUtil.getMessageKO(MessagesDebugEnum.DEBUG_COMMON_CREATE_SESSION.getMessageCode(), messageParams));
		} else {
			log.debug(messageUtil.getMessageKO(MessagesDebugEnum.DEBUG_COMMON_EXISTING_SESSION.getMessageCode(), messageParams));
		}
		
		// 세션 유틸리티 클래스에 세션 설정
		sessionUtil.setSession(httpSession);
		
		// true를 반환하여 요청 처리를 계속 진행
		return true;
	}
	  
}
