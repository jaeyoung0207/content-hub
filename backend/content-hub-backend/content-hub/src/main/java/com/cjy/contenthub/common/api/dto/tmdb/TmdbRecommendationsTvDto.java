package com.cjy.contenthub.common.api.dto.tmdb;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API TV 추천 Response DTO
 * 추천 TV 프로그램 목록을 포함하는 추천 API의 응답 형식
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbRecommendationsTvDto {
	
	/** 현재 페이지 번호 */
	private int page;
	
	/** 추천 TV 목록 */
	private List<TmdbRecommendationsTvResultsDto> results;
	
	/** 총 페이지 수 */
	private int totalPages;
	
	/** 총 작품 수 */
	private int totalResults;

}
