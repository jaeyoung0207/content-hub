package com.cjy.contenthub.common.api.dto.aniList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 미디어 추천 상세 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListMediaRecommendationDetailDto {
	
	/** 추천 미디어 ID */
	private int id;
	
	/** 커버 이미지 DTO */
	private AniListCoverImageDto coverImage;
	
	/** 제목 DTO */
	private AniListTitleDto title;
	
	/** 미디어 타입 */
	private String type;

}
