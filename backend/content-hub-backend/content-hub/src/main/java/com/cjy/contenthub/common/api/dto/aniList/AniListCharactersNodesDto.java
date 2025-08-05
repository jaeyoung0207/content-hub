package com.cjy.contenthub.common.api.dto.aniList;

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

	/** 사이트 URL */
	private String siteUrl;

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

}
