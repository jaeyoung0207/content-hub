package com.cjy.contenthub.searchContent.controller.dto;

import java.util.List;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchTvResultsDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 검색 컨텐츠 TV 응답 DTO
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchContentTvResponseDto {

	/** TMDB API TV 프로그램 검색 결과 DTO 애니 리스트 */
	private List<TmdbSearchTvResultsDto> aniResults;
	
	/** TMDB API TV 프로그램 검색 결과 DTO 드라마 리스트 */
	private List<TmdbSearchTvResultsDto> dramaResults;
	
	/** 페이지 */
	private int page;
	
	/** 총 페이지 수 */
	private int totalPages;
	
	/** 총 결과 수 */
	private int totalResults;
	
}
