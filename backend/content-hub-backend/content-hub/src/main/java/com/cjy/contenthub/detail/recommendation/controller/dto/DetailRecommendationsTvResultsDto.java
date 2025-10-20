package com.cjy.contenthub.detail.recommendation.controller.dto;

import com.cjy.contenthub.common.integration.tmdb.dto.TmdbRecommendationsTvResultsDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class DetailRecommendationsTvResultsDto extends TmdbRecommendationsTvResultsDto {
	
	/** 컨텐츠 미디어 타입 */
	private String contentMediaType;
	
	/** 위시리스트 여부 */
	private boolean isWishlisted;
	
}
