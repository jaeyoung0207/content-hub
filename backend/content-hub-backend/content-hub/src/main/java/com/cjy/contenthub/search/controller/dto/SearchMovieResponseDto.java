package com.cjy.contenthub.search.controller.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 검색 콘텐츠 TV 응답 DTO
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMovieResponseDto {

    /** TMDB API 영화 검색 결과 DTO 영화 */
	private List<SearchMovieResultsDto> movieResults;
	
	/** 페이지 */
	private int page;
	
	/** 총 페이지 수 */
	private int totalPages;
	
	/** 총 결과 수 */
	private int totalResults;
	
}
