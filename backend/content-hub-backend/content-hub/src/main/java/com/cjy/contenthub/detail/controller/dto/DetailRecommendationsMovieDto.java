package com.cjy.contenthub.detail.controller.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailRecommendationsMovieDto {
	
	/** 현재 페이지 번호 */
	private int page;
	
	/** 추천 TV 목록 */
	private List<DetailRecommendationsMovieResultsDto> results;
	
	/** 총 페이지 수 */
	private int totalPages;
	
	/** 총 작품 수 */
	private int totalResults;

}
