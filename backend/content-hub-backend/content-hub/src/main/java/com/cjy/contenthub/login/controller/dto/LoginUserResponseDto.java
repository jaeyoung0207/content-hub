package com.cjy.contenthub.login.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 유저 응답 DTO
 */
@Setter
@Getter
@Builder
public class LoginUserResponseDto {
	
	/** 결과 코드 */
	private String resultcode;
	
	/** 결과 메시지 */
	private String message;
	
	/** 로그인 유저 정보 */
	private LoginUserInfoDto userInfo;
	
	/** 액세스 토큰 */
	private String accessToken; 
	
	/** JSON With Token */
	private String jwt;
	
	/** 토큰 만료까지의 시간(초 단위) */
	private Integer expiresIn;
	
	/** 토큰 만료 날짜 */
	private String expireDate;

}
