package com.cjy.contenthub.common.api.dto.aniList;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 미디어 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListMediaDto {

	/** 미디어 ID */
	private int id;

	/** 미디어 설명 */
	private String description;

	/** 미디어 장르 */
	private List<String> genres;

	/** 성인물 여부 */
	private boolean isAdult;

	/** 볼륨 */
	private int volumes;

	/** 챕터 */
	private int chapters;
	
    /** 미디어 상태 */
	private String status;

	/** 미디어 타입 */
	private String type;

	/** 사이트 URL */
	private String siteUrl;

	/** 미디어 제목 DTO */
	private AniListTitleDto title;

	/** 커버 이미지 DTO */
	private AniListCoverImageDto coverImage;

	/** 캐릭터 DTO */
	private AniListCharactersDto characters;

	/** 연재 시작일 DTO */
	private AniListStartDateDto startDate;
	
	/** 관련(상세) DTO */
	private AniListRelationsDto relations;
	
	/** 추천(상세) DTO */
	private AniListRecommendationsDto recommendations;
}