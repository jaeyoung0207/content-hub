package com.cjy.contenthub.search.controller.dto;

import java.util.List;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchMovieResultsDto;

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
public class SearchMovieResultsDto extends TmdbSearchMovieResultsDto {
	
	/** 원작품 미디어 타입 */
	private String originalMediaType;
	
	/** 장르명 리스트 */
	private List<String> genreNames;
	
	/** 콘텐츠 ID */
	private Long contentId;
	
	/** 위시리스트 여부 */
	private boolean isWishlist;

}
