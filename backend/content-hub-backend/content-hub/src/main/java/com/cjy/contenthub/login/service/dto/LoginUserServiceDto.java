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
	
	/** 유저 테이블 식별 ID */
	private Long userId;

	/** 로그인 제공자 */
	private String provider;

	/** 로그인 제공자의 유저 ID */
	private String providerId;

	/** 닉네임 */
	private String nickname;
	
	/** 이름 */
	private String name;

	/** 이메일 */
	private String email;
	
	/** 성별 */
	private String gender;
	
	/** 나이 */
	private String age;
	
	/** 생일 */
	private String birthday;
	
	/** 프로필 이미지 URL */
	private String profileImage;
	
	/** 생일 연도 */
	private String birthyear;
	
	/** 휴대폰 번호 */
	private String mobile;
	
	/** 상태 */
	private String status;

}
