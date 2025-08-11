package com.cjy.contenthub.common.api.dto.aniist;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 추천 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListRecommendationDto {
	
	/** 추천 미디어 DTO */
	private AniListMediaRecommendationDto mediaRecommendation;

}
