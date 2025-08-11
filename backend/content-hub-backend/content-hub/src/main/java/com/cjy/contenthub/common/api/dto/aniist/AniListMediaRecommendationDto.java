package com.cjy.contenthub.common.api.dto.aniist;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 미디어 추천 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListMediaRecommendationDto {
	
	/** 추천 미디어(상세) DTO */
	private AniListRecommendationsDto recommendations;
	
	/** 관련 미디어(상세) DTO */
	private AniListRelationsDto relations;

}
