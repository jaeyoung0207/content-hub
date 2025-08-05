package com.cjy.contenthub.common.api.dto.kakao;

import lombok.Getter;
import lombok.Setter;

/**
 * Kakao API 파트너 정보 Response DTO
 * 
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaopartners/common#kakaopartners-api">Kakao Partner API 문서</a>
 */
@Setter
@Getter
public class KakaoPartnerDto {
	
	/** 고유 ID */
	private String uuid;

}
