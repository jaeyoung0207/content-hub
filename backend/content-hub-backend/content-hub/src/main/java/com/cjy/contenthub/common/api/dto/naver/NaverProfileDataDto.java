package com.cjy.contenthub.common.api.dto.naver;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Naver API 회원 프로필 데이터 Response DTO
 * 
 * @see <a href=
 *      "https://developers.naver.com/docs/login/profile/profile.md#%EB%84%A4%EC%9D%B4%EB%B2%84-%ED%9A%8C%EC%9B%90-%ED%94%84%EB%A1%9C%ED%95%84-%EC%A1%B0%ED%9A%8C-api-%EB%AA%85%EC%84%B8">
 *      Naver Developers</a>
 */
@Setter
@Getter
public class NaverProfileDataDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
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
	
	/** 생일 */
	private String birthday;
	
	/** 프로필 이미지 URL */
	private String profileImage;
	
	/** 생일 연도 */
	private String birthyear;
	
	/** 휴대폰 번호 */
	private String mobile;

}
