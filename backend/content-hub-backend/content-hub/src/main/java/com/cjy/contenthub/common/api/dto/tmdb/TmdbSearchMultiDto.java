package com.cjy.contenthub.common.api.dto.tmdb;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 멀티 검색 Response DTO
 * 검색 결과의 페이지 정보와 영화, TV 프로그램 등 다양한 콘텐츠를 포함 하는 멀티 검색 API의 응답 형식
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbSearchMultiDto {

	private int page;
	
	private List<TmdbSearchMultiResultsDto> results;
	
	private int totalPages;
	
	private int totalResults;
}
