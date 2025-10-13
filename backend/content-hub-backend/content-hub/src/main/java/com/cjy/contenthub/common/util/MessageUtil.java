package com.cjy.contenthub.common.util;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * 다국어 메시지 처리를 위한 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
public class MessageUtil {
	
	/** 메시지 소스 인터페이스 */
	private final MessageSource messageSource;
	
	/**
	 * 한국어 메시지 가져오기
	 * 
	 * @param code 메시지 코드
	 * @return 한국어 메시지
	 */
	public String getMessageKO(String code) {
		return getMessageKO(code, null);
	}
	
	/**
	 * 한국어 메시지 가져오기 (매개변수 포함)
	 * 
	 * @param code 메시지 코드
	 * @param args 메시지 매개변수 배열
	 * @return 한국어 메시지
	 */
	public String getMessageKO(String code, Object[] args) {
		return messageSource.getMessage(code, args, Locale.KOREA);
	}
	
	/**
	 * 메시지 가져오기 (매개변수 및 로케일 포함)
	 * 
	 * @param code   메시지 코드
	 * @param args   메시지 매개변수 배열
	 * @param locale 로케일
	 * @return 해당 로케일의 메시지
	 */
	public String getMessage(String code, Object[] args, Locale locale) {
		return messageSource.getMessage(code, args, locale);
	}

}
