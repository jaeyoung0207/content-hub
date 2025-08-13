package com.cjy.contenthub.common.api.dto.kakao;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Kakao API 프로필 Response DTO
 * 
 * @see <a href=
 *      "https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#profile">
 *      Kakao Profile API 문서</a>
 */
@Setter
@Getter
public class KakaoProfileDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** 닉네임 */
	private String nickname;
	
	/** 프로필 미리보기 이미지 URL */
	private String thumbnailImageUrl;
	
	/** 프로필 사진 URL */
	private String profileImageUrl;
	
	/** 프로필 사진 URL이 기본 프로필 사진 URL인지 여부 */
	private boolean isDefaultImage;
	
	/** 닉네임이 기본 닉네임인지 여부 */
	private boolean isDefaultNickname;
	
}
