package com.cjy.contenthub.common.exception;

import lombok.Getter;

/**
 * Device ID를 찾을 수 없을 때 발생하는 예외 클래스
 */
@Getter
public class DeviceIdNotFoundException extends CommonBusinessException {

	/** 직렬화 ID */
	private static final long serialVersionUID = 1L;
	
	/** 상태 코드 */
	private final Integer statusCode;

	/**
	 * 기본 생성자
	 * 
	 * @param message 예외 메시지
	 */
	public DeviceIdNotFoundException(String message) {
		super(message);
		this.statusCode = null;
	}
	
	/**
	 * 상태 코드와 메시지를 포함하는 생성자
	 * 
	 * @param message    예외 메시지
	 * @param statusCode 상태 코드
	 */
	public DeviceIdNotFoundException(String message, Integer statusCode) {
		super(message);
		this.statusCode = statusCode;
	}
	
}
