package com.cjy.contenthub.login.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 유저 정보 DTO
 */
@Setter
@Getter
@Builder
public class LoginUserInfoDto {
	
	/** ID */
	private String id;
	
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
	
	/** 생년월일 */
	private String birthday;
	
	/** 프로필 이미지 URL */
	private String profileImage;
	
	/** 생일 연도 */
	private String birthyear;
	
	/** 휴대폰 번호 */
	private String mobile;

}
