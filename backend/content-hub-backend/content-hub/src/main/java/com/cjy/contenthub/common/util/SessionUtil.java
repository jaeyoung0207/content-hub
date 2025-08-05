package com.cjy.contenthub.common.util;

import java.util.Optional;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 세션 제어용 공통 클래스
 * 스레드 세이프한 방식으로 HttpSession을 관리하기 위해 ThreadLocal을 사용
 */
@Component
@Slf4j
public class SessionUtil {
	
	/** 스레드 세이프를 위한 ThreadLocal */
	private static final ThreadLocal<HttpSession> sessionHolder = new ThreadLocal<>();
	
	/**
	 * HttpSession 저장
	 * HttpSession을 ThreadLocal에 저장하여 현재 스레드에서만 접근할 수 있도록 함
	 * 
	 * @param session HttpSession
	 */
	public void setSession(HttpSession session) {
		sessionHolder.set(session);
	}
	
	/**
	 * HttpSession 취득
	 * 현재 스레드에 저장된 HttpSession을 반환
	 * 만약 현재 스레드에 HttpSession이 저장되어 있지 않으면 null을 반환
	 * 
	 * @return HttpSession 현재 스레드의 HttpSession
	 */
	public HttpSession getSession() {
		return sessionHolder.get();
	}
	
	/**
	 * 세션에서 지정된 키에 해당하는 Boolean 값을 반환
	 * 
	 * @param key 세션 키
	 */
	public boolean getSessionBooleanValue(String key) {
		// 현재 스레드에 저장된 HttpSession을 가져옴
		HttpSession session = getSession();
		// 세션이 null인 경우 false 반환
		if (session == null) {
			log.warn("Session is null for key: {}", key);
			return false;
		}
		// 세션에서 지정된 키에 해당하는 값을 Optional로 감싸고, 값이 없으면 false를 반환
		return (boolean) Optional.ofNullable(session.getAttribute(key)).orElse(false);
	}
	
	/**
	 * 현재 스레드에 저장된 HttpSession을 제거하여 세션을 클리어
	 */
	public void sessionClear() {
		// 현재 스레드에 저장된 HttpSession을 제거
		sessionHolder.remove();
		log.debug("Session is cleared");
	}

}
