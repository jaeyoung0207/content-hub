package com.cjy.contenthub.common.api.dto.tmdb;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 영화 검색 Response DTO
 * 검색 결과의 페이지 정보와 영화 목록을 포함 하는 API의 응답 형식
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TmdbSearchMovieDto {

	/** 현재 페이지 번호 */
	private int page;
	
	/** 검색 결과 영화 목록 */
	private List<TmdbSearchMovieResultsDto> results;
	
	/** 총 페이지 수 */
	private int totalPages;
	
	/** 총 작품 수 */
	private int totalResults;
}
