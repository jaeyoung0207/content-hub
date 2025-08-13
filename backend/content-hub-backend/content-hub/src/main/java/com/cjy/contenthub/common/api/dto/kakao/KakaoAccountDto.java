package com.cjy.contenthub.common.api.dto.kakao;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

/**
 * Kakao API 계정 정보 Response DTO
 * 
 * @see <a href=
 *      "https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#kakaoaccount">
 *      Kakao KakaoAccount API 문서</a>
 */
@Setter
@Getter
public class KakaoAccountDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** 유저 동의 시 프로필 정보(닉네임/프로필 사진) 제공 가능 */
	private boolean profileNeedsAgreement;
	
	/** 유저 동의 시 닉네임 제공 가능 */
	private boolean profileNicknameNeedsAgreement;
	
	/** 유저 동의 시 프로필 사진 제공 가능 */
	private boolean profileImageNeedsAgreement;
	
	/** 프로필 정보 */
	private KakaoProfileDto profile;
	
	/** 유저 동의 시 카카오계정 이름 제공 가능 */
	private boolean nameNeedsAgreement;
	
	/** 카카오계정 이름 */
	private String name;
	
	/** 유저 동의 시 카카오계정 대표 이메일 제공 가능 */
	private boolean emailNeedsAgreement;
	
	/** 이메일 유효 여부 */
	private boolean isEmailValid;
	
	/** 이메일 인증 여부 */
	private boolean isEmailVerified;
	
	/** 카카오계정 대표 이메일 */
	private String email;
	
	/** 유저 동의 시 연령대 제공 가능 */
	private boolean ageRangeNeedsAgreement;
	
	/** 연령대 */
	private String ageRange;
	
	/** 유저 동의 시 출생 연도 제공 가능 */
	private boolean birthyearNeedsAgreement;
	
	/** 출생 연도(YYYY 형식) */
	private String birthyear;
	
	/** 유저 동의 시 생일 제공 가능 */
	private boolean birthdayNeedsAgreement;
	
	/** 생일(MMDD 형식) */
	private String birthday;
	
	/** 생일 타입 */
	private String birthdayType;
	
	/** 생일의 윤달 여부 */
	private boolean isLeapMonth;
	
	/** 유저 동의 시 성별 제공 가능 */
	private boolean genderNeedsAgreement;
	
	/** 성별 */
	private String gender;
	
	/** 유저 동의 시 전화번호 제공 가능 */
	private boolean phoneNumberNeedsAgreement;
	
	/** 카카오계정의 전화번호 */
	private String phoneNumber;
	
	/** 유저 동의 시 CI 참고 가능 */
	private boolean ciNeedsAgreement;
	
	/** 연계정보 */
	private String ci;
	
	/** CI 발급 시각, UTC */
	private Timestamp ciAuthenticatedAt;
	
}
