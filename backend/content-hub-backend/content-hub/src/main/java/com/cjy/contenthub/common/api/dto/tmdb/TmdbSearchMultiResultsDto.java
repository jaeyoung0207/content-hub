package com.cjy.contenthub.common.api.dto.tmdb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 멀티 검색 결과 Response DTO
 * 영화, TV 프로그램 등 다양한 콘텐츠를 검색할 수 있는 멀티 검색 API의 응답 형식
 * 
 * @see <a href="https://developer.themoviedb.org/reference/search-multi">TMDB
 *      Search Multi API 문서</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbSearchMultiResultsDto extends TmdbSearchCommonResultsDto {

	/** 작품 제목 */
	private String title;
	
	/** 작품명 */
	private String name;
	
	/** 미디어 타입 */
	private String mediaType;
	
}
