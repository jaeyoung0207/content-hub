package com.cjy.contenthub.search.controller.dto;

import java.util.List;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchCommonResultsDto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 검색 콘텐츠 만화 결과 DTO
 */
@Setter
@Getter
@SuperBuilder
public class SearchComicsResultDto extends TmdbSearchCommonResultsDto {

	/** 제목 */
	private String title;
	
	/** 원작품 미디어 타입 */
	private String originalMediaType;
	
	/** 장르명 리스트 */
	private List<String> genreNames;
	
	/** 위시리스트 여부 */
	private boolean isWishlisted;

}
