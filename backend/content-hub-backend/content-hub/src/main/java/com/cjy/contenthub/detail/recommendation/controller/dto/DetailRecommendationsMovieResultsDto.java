package com.cjy.contenthub.detail.recommendation.controller.dto;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsMovieResultsDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
public class DetailRecommendationsMovieResultsDto extends TmdbRecommendationsMovieResultsDto {
	
	/** 원작품 미디어 타입 */
	private String originalMediaType;
	
	/** 위시리스트 여부 */
	private boolean isWishlisted;

}
