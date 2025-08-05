package com.cjy.contenthub.common.exception;

import lombok.Getter;

/**
 * 비즈니스 로직에서 발생할 수 있는 예외를 처리하기 위한 유저 정의 예외 클래스
 */
@Getter
public class CommonBusinessException extends RuntimeException {
	
	/** 직렬화 ID */
	private static final long serialVersionUID = 1L;
	
	/** 상태 코드 */
	private Integer statusCode;
	
	/**
	 * 기본 생성자
	 * 
	 * @param message    예외 메시지
	 */
	public CommonBusinessException(String message) {
		super(message);
	}

	/**
	 * 상태 코드와 메시지를 포함하는 생성자
	 * 
	 * @param message    예외 메시지
	 * @param statusCode 상태 코드
	 */
	public CommonBusinessException(String message, Integer statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

}
