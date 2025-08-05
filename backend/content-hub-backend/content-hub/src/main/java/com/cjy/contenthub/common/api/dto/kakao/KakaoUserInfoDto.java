package com.cjy.contenthub.common.api.dto.kakao;

import java.sql.Timestamp;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Kakao API 유저 정보 Response DTO
 * 
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info-response">Kakao 유저 정보 조회 API 문서</a>
 */
@Setter
@Getter
public class KakaoUserInfoDto {
	
	/** 회원번호 */
	private Long id;
	
	/** 연결하기 호출의 완료 여부(자동 연결 설정을 비활성화한 경우만 존재)
	 * false: 연결 대기(Preregistered) / true: 연결(Registered) */
	private boolean hasSignedUp;
	
	/** 서비스에 연결 완료된 시각(UTC) */
	private Timestamp connectedAt;
	
	/** 카카오싱크 간편가입으로 로그인한 시각(UTC) */
	private Timestamp synchedAt;
	
	/** 유저 프로퍼티 */
	private Map<String, String> properties;
	
	/** 카카오계정 정보 */
	private KakaoAccountDto kakaoAccount;
	
	/** 추가 정보 */
	private KakaoPartnerDto forPartner;

}
