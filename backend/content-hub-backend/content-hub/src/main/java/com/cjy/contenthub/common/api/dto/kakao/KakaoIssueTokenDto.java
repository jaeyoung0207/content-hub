package com.cjy.contenthub.common.api.dto.kakao;

import lombok.Getter;
import lombok.Setter;

/**
 * Kakao API 토큰 발급 Response DTO
 * 
 * @see <a href=
 *      "https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token-response">
 *      Kakao 토큰 요청 API 문서</a>
 */
@Setter
@Getter
public class KakaoIssueTokenDto {
	
	/** 토큰 타입 */
	private String tokenType;
	
	/** 유저 액세스 토큰 값 */
	private String accessToken;
	
	/** ID 토큰 값(OpenID Connect 확장 기능으로 발급하는 ID 토큰, Base64 인코딩 된 유저 인증 정보 포함) */
	private String idToken;
	
	/** 액세스 토큰과 ID 토큰의 만료 시간(초) */
	private int expiresIn;
	
	/** 유저 리프레시 토큰 값 */
	private String refreshToken;
	
	/** 리프레시 토큰 만료 시간(초) */
	private int refreshTokenExpiresIn;
	
	/** 인증된 유저의 정보 조회 권한 범위(범위가 여러 개일 경우, 공백으로 구분) */
	private String scope;

}
