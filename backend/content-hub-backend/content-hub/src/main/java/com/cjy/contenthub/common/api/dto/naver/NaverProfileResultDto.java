package com.cjy.contenthub.common.api.dto.naver;

import lombok.Getter;
import lombok.Setter;

/**
 * Naver API 회원 프로필 조회 결과 Response DTO
 * 
 * @see <a href=
 *      "https://developers.naver.com/docs/login/profile/profile.md#%EB%84%A4%EC%9D%B4%EB%B2%84-%ED%9A%8C%EC%9B%90-%ED%94%84%EB%A1%9C%ED%95%84-%EC%A1%B0%ED%9A%8C-api-%EB%AA%85%EC%84%B8">
 *      Naver Developers</a>
 */
@Setter
@Getter
public class NaverProfileResultDto {

	/** 응답 코드 */
	private String resultcode;

	/** 응답 메시지 */
	private String message;

	/** 회원 프로필 데이터 DTO */
	private NaverProfileDataDto response;

}
