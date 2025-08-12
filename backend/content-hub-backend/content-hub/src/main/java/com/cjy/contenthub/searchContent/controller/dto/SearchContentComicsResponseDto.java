package com.cjy.contenthub.searchContent.controller.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 검색 컨텐츠 만화 응답 DTO
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchContentComicsResponseDto {

	/** 만화 결과 DTO 리스트 */
	private List<SearchContentComicsMediaResultDto> comicsResults;
	
	/** 페이지 */
	private int page;
	
	/** 총 페이지 수 */
	private int totalPages;
	
	/** 전체 결과 수 */
	private int totalResults;
	
	/** 만화 전체보기 여부 */
	private boolean isComicsViewMore;

}
