package com.cjy.contenthub.common.api.dto.aniist;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 캐릭터 노드 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListCharactersNodesDto {
	
	/** 캐릭터 ID */
	private int id;

	/** 캐릭터 이미지 DTO */
	private AniListCharactersImageDto image;

	/** 캐릭터 이름 DTO */
	private AniListCharactersNameDto name;

	/** 캐릭터 나이 */
	private String age;

	/** 캐릭터 성별 */
	private String gender;

	/** 캐릭터 설명 */
	private String description;
	
	/** 캐릭터 혈액형 */
	private String bloodType;
	
	/** 캐릭터 생일 */
	private AniListDateDto dateOfBirth;
	
	/** 인기 점수 */
	private int favourites;
	
	/** 즐겨찾기 여부 */
	private boolean isFavourite;
	
	/** 인기 점수 추가 가능 여부 */
	private boolean isFavouriteBlocked;

	/** 사이트 URL */
	private String siteUrl;
	
}
