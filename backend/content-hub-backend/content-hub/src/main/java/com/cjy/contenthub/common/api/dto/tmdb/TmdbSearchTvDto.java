package com.cjy.contenthub.common.api.dto.tmdb;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API TV 프로그램 검색 Response DTO
 * 검색 결과의 페이지 정보와 TV 프로그램 목록을 포함 하는 API의 응답 형식
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TmdbSearchTvDto {

	private int page;
	
	private List<TmdbSearchTvResultsDto> results;
	
	private int totalPages;
	
	private int totalResults;
}
