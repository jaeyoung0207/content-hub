package com.cjy.contenthub.app.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 어플리케이션 로그인 쿠키 응답 DTO 클래스
 */
@Setter
@Getter
@NoArgsConstructor
public class AppLoginCookiesResponseDto {

	/** 로그인 제공자 */
	private String provider;
	
	/** 리프레시 토큰 보존 여부 */
	private boolean hasRefreshToken;
	
	/** 디바이스 ID */
	private String deviceId;

}
