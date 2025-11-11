package com.cjy.contenthub.common.exception;

import org.springframework.security.core.AuthenticationException;

import lombok.Getter;

/**
 * 공통 JWT 예외 클래스
 */
@Getter
public class CommonJwtException extends AuthenticationException {
	
	/** 직렬화 ID */
	private static final long serialVersionUID = 1L;
	
	/** 상태 코드 */
	private final Integer statusCode;
	
	/**
	 * 기본 생성자
	 * 
	 * @param msg 예외 메시지
	 * @param cause 원인 예외
	 */
	public CommonJwtException(String msg, Throwable cause) {
		super(msg, cause);
		this.statusCode = null;
	}

	/**
	 * 메시지만 포함하는 생성자
	 * 
	 * @param msg 예외 메시지
	 */
	public CommonJwtException(String msg) {
		super(msg);
		this.statusCode = null;
	}
	
	/**
	 * 메시지와 상태 코드를 포함하는 생성자
	 * 
	 * @param msg        예외 메시지
	 * @param statusCode 상태 코드
	 */
	public CommonJwtException(String msg, int statusCode) {
		super(msg);
		this.statusCode = statusCode;
	}

}
