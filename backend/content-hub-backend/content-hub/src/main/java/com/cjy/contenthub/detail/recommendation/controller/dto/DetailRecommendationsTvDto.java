package com.cjy.contenthub.detail.recommendation.controller.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API TV 추천 Response DTO
 * 추천 TV 프로그램 목록을 포함하는 추천 API의 응답 형식
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailRecommendationsTvDto {
	
	/** 현재 페이지 번호 */
	private int page;
	
	/** 추천 TV 목록 */
	private List<DetailRecommendationsTvResultsDto> results;
	
	/** 총 페이지 수 */
	private int totalPages;
	
	/** 총 작품 수 */
	private int totalResults;

}
