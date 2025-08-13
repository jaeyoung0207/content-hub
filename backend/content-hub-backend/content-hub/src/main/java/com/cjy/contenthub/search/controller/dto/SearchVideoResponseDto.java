package com.cjy.contenthub.search.controller.dto;

import java.util.List;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchMovieResultsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchTvResultsDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 검색 컨텐츠 비디오 응답 DTO
 */
@Setter
@Getter
@Builder
public class SearchVideoResponseDto {

	/** TMDB API TV 프로그램 검색 결과 DTO 애니 리스트 */
	private List<TmdbSearchTvResultsDto> aniResults;
	
	/** TMDB API TV 프로그램 검색 결과 DTO 드라마 리스트 */
	private List<TmdbSearchTvResultsDto> dramaResults;
	
	/** TMDB API 영화 검색 결과 DTO 영화 리스트 */
	private List<TmdbSearchMovieResultsDto> movieResults;
	
	/** 페이지 */
	private int page;
	
	/** 총 페이지 수 */
	private int totalPages;
	
	/** 총 결과 수 */
	private int totalResults;
	
	/** 애니 전체보기 여부 */
	private boolean isAniViewMore;
	
	/** 드라마 전체보기 여부 */
	private boolean isDramaViewMore;
	
	/** 영화 전체보기 여부 */
	private boolean isMovieViewMore;
	
}
