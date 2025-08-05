package com.cjy.contenthub.searchContent.controller.dto;

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
public class SearchContentComicsMediaResultDto extends TmdbSearchCommonResultsDto {

	/** 제목 */
	private String title;

}
