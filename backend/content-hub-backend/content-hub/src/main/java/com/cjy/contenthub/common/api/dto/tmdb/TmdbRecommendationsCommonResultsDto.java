package com.cjy.contenthub.common.api.dto.tmdb;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * TMDB API 추천 콘텐츠 공통 결과 Response DTO
 * 영화, TV 프로그램 등 추천 콘텐츠의 공통 속성을 포함하는 DTO로, 추천 영화나 TV 프로그램의 상세 정보를 포함
 * 
 * @see <a href=
 *      "https://developer.themoviedb.org/reference/movie-recommendations"> TMDB
 *      Movie Recommendations API 문서</a>
 * @see <a href=
 *      "https://developer.themoviedb.org/reference/tv-series-recommendations"> TMDB
 *      TV Recommendations API 문서</a>
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class TmdbRecommendationsCommonResultsDto {
	
	/** 성인물 여부 */
	private boolean adult;
	
	/** 썸네일 이미지 경로 */
	private String backdropPath;
	
	/** 장르 ID 리스트 */
	private List<Integer> genreIds;
	
	/** 작품 ID */
	private int id;
	
	/** 원어 */
	private String originalLanguage;
	
	/** 개요 */
	private String overview;
	
	/** 인기 점수 */
	private BigDecimal popularity;
	
	/** 포스터 이미지 경로 */
	private String posterPath;
	
	/** 평균 점수  */
	private BigDecimal voteAverage;
	
	/** 투표 수 */
	private int voteCount;

}
