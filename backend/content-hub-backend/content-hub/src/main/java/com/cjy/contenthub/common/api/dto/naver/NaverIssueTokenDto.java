package com.cjy.contenthub.common.api.dto.naver;

import lombok.Getter;
import lombok.Setter;

/**
 * Naver API 토큰 발급 Response DTO
 * 접근 토큰 발급/갱신을 위한 요청에 대한 응답 DTO
 * 
 * @see <a href="https://developers.naver.com/docs/login/api/api.md#4-2--%EC%A0%91%EA%B7%BC-%ED%86%A0%ED%81%B0-%EB%B0%9C%EA%B8%89-%EC%9A%94%EC%B2%AD">Naver 접근 토큰 발급 요청 API 문서</a>
 */
@Setter
@Getter
public class NaverIssueTokenDto {
	
	/** 접근 토큰 */
	private String accessToken;
	
	/** 갱신 토큰 */
	private String refreshToken;
	
	/** 토큰 타입 */
	private String tokenType;
	
	/** 토큰 만료 시간 (초 단위) */
	private Integer expiresIn;
	
	/** 갱신 토큰 만료 시간 (초 단위) */
	private String error;
	
	/** 에러 설명 */
	private String errorDescription;

}
