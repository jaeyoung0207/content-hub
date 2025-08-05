package com.cjy.contenthub.login.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 유저 서비스 DTO
 */
@Setter
@Getter
@Builder
public class LoginUserServiceDto {

	/** 로그인 제공자 */
	private String provider;

	/** 로그인 제공자의 유저 ID */
	private String providerId;

	/** 닉네임 */
	private String nickname;

	/** 이메일 */
	private String email;
	
	/** 상태 */
	private String status;

}
