package com.cjy.contenthub.common.api.dto.tmdb;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * TMDB API TV 프로그램 검색 결과 Response DTO
 * TV 프로그램의 상세 정보를 포함하는 API의 응답 형식
 * 
 * @see <a href=
 *      "https://developer.themoviedb.org/reference/search-tv">TMDB Search TV API 문서</a>
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TmdbSearchTvResultsDto extends TmdbSearchCommonResultsDto {

	/** 원저작물의 국가 리스트 */
	private List<String> originCountry;
	
	/** 첫 방영일 */
	private String firstAirDate;
	
	/** 작품명 */
	private String name;
	
}
