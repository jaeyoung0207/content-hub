package com.cjy.contenthub.common.integration.anilist.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 추천(상세) Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListRecommendationsDto {
	
	/** 추천(상세) 노드 DTO 리스트 */
	private List<AniListRecommendationsNodeDto> nodes;

}
