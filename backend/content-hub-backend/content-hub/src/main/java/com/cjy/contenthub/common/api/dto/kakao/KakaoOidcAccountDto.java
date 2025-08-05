package com.cjy.contenthub.common.api.dto.kakao;

import lombok.Getter;
import lombok.Setter;

/**
 * Kakao OIDC 계정 정보 DTO
 * 
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-oidc-user-info-response">
 *      Kakao OIDC: 유저 정보 조회 API 문서</a>
 */
@Setter
@Getter
public class KakaoOidcAccountDto {
	
	/** 회원번호 */
	private String sub;
	
	/** 이름 */
	private String name;
	
	/** 닉네임 */
	private String nickname;
	
	/** 유저 썸네일 이미지 URL */
	private int picture;
	
	/** 대표 이메일 */
	private String email;
	
	/** 이메일의 인증 및 유효 여부 */
	private boolean emailVrified;
	
	/** 성별 */
	private String gender;
	
	/** 생년월일 */
	private String birthdate;
	
	/** 전화번호 */
	private String phoneNumber;
	
	/** 전화번호 인증 여부 */
	private boolean phoneNumberVerified;
	
}
