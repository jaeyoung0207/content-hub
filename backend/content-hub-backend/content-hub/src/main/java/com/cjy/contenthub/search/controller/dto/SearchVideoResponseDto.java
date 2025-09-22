package com.cjy.contenthub.search.controller.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 검색 콘텐츠 비디오 응답 DTO
 */
@Setter
@Getter
@SuperBuilder(toBuilder = true)
public class SearchVideoResponseDto extends SearchTvResponseDto {

	/** TMDB API 영화 검색 결과 DTO 영화 리스트 */
	private List<SearchMovieResultsDto> movieResults;
	
	/** 애니 전체보기 여부 */
	private Boolean isAniViewMore;
	
	/** 드라마 전체보기 여부 */
	private Boolean isDramaViewMore;

	/** 다큐멘터리 전체보기 여부 */
	private Boolean isDocumentaryViewMore;
	
	/** 키즈 전체보기 여부 */
	private Boolean isKidsViewMore;
	
	/** 뉴스 전체보기 여부 */
	private Boolean isNewsViewMore;
	
	/** 버라이어티 전체보기 여부 */
	private Boolean isVarietyViewMore;
	
	/** 영화 전체보기 여부 */
	private Boolean isMovieViewMore;
	
}
