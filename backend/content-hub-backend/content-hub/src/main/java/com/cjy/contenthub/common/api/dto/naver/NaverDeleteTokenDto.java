package com.cjy.contenthub.common.api.dto.naver;

import lombok.Getter;
import lombok.Setter;

/**
 * Naver API 토큰 삭제 Response DTO
 * 
 * @see 
 *  * @see <a href=
 *      "https://developers.naver.com/docs/login/api/api.md#4-4--%EC%A0%91%EA%B7%BC-%ED%86%A0%ED%81%B0-%EC%82%AD%EC%A0%9C-%EC%9A%94%EC%B2%AD">
 *      Naver 접근 토큰 삭제 요청 API 문서</a>
 */
@Setter
@Getter
public class NaverDeleteTokenDto {
	
	/** 접근 토큰 */
	private String accessToken;
	
	/** 처리 결과 */
	private String result;
	
	/** 토큰 만료 시간 (초 단위) */
	private Integer expiresIn;
	
	/** 에러 코드 */
	private String error;
	
	/** 에러 설명 */
	private String errorDescription;

}
