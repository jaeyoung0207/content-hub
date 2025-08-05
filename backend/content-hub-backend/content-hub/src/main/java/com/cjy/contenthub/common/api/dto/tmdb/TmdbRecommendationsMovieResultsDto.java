package com.cjy.contenthub.common.api.dto.tmdb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 영화 추천 결과 Response DTO
 * 추천 영화의 상세 정보를 포함하는 추천 API의 응답 형식
 * 
 * @see <a href=
 *      "https://developer.themoviedb.org/reference/movie-recommendations">TMDB
 *      Movie Recommendations API 문서</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbRecommendationsMovieResultsDto extends TmdbRecommendationsCommonResultsDto {
	
	/** 원제목 */
	private String originalTitle;
	
	/** 개봉일 */
	private String releaseDate;
	
	/** 작품 제목 */
	private String title;
	
	/** 동영상 여부 */
	private boolean video;
	
}
