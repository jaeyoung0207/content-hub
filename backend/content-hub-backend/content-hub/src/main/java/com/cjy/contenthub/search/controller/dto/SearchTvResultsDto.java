package com.cjy.contenthub.search.controller.dto;

import java.util.List;

import com.cjy.contenthub.common.integration.tmdb.dto.TmdbSearchTvResultsDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SearchTvResultsDto extends TmdbSearchTvResultsDto {
	
	/** 컨텐츠 미디어 타입 */
	private String contentMediaType;
	
	/** 장르명 리스트 */
	private List<String> genreNames;
	
	/** 위시리스트 여부 */
	private boolean isWishlisted;

}
