package com.cjy.contenthub.common.api.dto.aniList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 추천(상세) 노드 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListRecommendationsNodeDto {

	/** 추천 미디어 상세 DTO */
	private AniListMediaRecommendationDetailDto mediaRecommendation;

}
