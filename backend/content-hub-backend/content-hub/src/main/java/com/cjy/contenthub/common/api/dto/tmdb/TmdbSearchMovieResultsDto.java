package com.cjy.contenthub.common.api.dto.tmdb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 영화 검색 결과 Response DTO
 * 영화의 상세 정보를 포함하는 API의 응답 형식
 * 
 * @see <a href=
 *      "https://developer.themoviedb.org/reference/search-movie">TMDB Search Movie API 문서</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbSearchMovieResultsDto extends TmdbSearchCommonResultsDto {

	/** 원제목 */
	private String originalTitle;
	
	/** 개봉일 */
	private String releaseDate;
	
	/** 작품 제목 */
	private String title;
	
	/** 동영상 여부 */
	private boolean video;
	
}
