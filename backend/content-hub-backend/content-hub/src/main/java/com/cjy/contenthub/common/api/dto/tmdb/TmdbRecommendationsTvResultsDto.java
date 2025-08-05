package com.cjy.contenthub.common.api.dto.tmdb;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API TV 추천 결과 Response DTO
 * 추천 TV 프로그램의 상세 정보를 포함하는 추천 API의 응답 형식
 * 
 * @see <a href=
 *      "https://developers.themoviedb.org/3/tv/get-tv-recommendations">TMDB TV
 *      Recommendations API 문서</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbRecommendationsTvResultsDto extends TmdbRecommendationsCommonResultsDto {
	
	/** 원 국가 */
	private List<String> originCountry;
	
	/** 원제목 */
	private String originalName;
	
	/** 첫 방영일 */
	private String firstAirDate;
	
	/** 작품명 */
	private String name;
	
}
