package com.cjy.contenthub.search.controller.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 검색 콘텐츠 TV 응답 DTO
 */
@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SearchTvResponseDto {

	/** TMDB API TV 프로그램 검색 결과 DTO 애니 리스트 */
	private List<SearchTvResultsDto> aniResults;
	
	/** TMDB API TV 프로그램 검색 결과 DTO 드라마 리스트 */
	private List<SearchTvResultsDto> dramaResults;
	
	/** TMDB API TV 프로그램 검색 결과 DTO 다큐멘터리 리스트 */
	private List<SearchTvResultsDto> documentaryResults;
	
	/** TMDB API TV 프로그램 검색 결과 DTO 키즈 리스트 */
	private List<SearchTvResultsDto> kidsResults;
	
	/** TMDB API TV 프로그램 검색 결과 DTO 뉴스 리스트 */
	private List<SearchTvResultsDto> newsResults;
	
	/** TMDB API TV 프로그램 검색 결과 DTO 버라이어티 리스트 */
	private List<SearchTvResultsDto> varietyResults;
	
	/** 페이지 */
	private int page;
	
	/** 총 페이지 수 */
	private int totalPages;
	
	/** 총 결과 수 */
	private int totalResults;
	
}
