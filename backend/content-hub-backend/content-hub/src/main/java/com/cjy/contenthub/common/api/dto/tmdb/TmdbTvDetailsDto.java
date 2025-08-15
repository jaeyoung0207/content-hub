package com.cjy.contenthub.common.api.dto.tmdb;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * TMDB API TV 시리즈 상세 Response DTO
 * TMDB API를 통해 TV 시리즈의 상세 정보를 조회할 때 사용하는 DTO 
 * 
 * @see <a href=
 *      "https://developer.themoviedb.org/reference/tv-series-details">TMDB TV
 *      Series Details API 문서</a>
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class TmdbTvDetailsDto extends TmdbVideoDetailsDto {
	
	/** 장르 목록 */
	List<TmdbGenreDto> genres;
	
	/** 방영 시간 */
	private List<Integer> episodeRunTime;

	/** 첫 방영일 */
	private String firstAirDate;
	
	/** 언어 */
	private List<String> languages;
	
	/** 마지막 방영일 */
	private String lastAirDate;
	
	/** 작품명 */
	private String name;
	
	/** 애파소드 수 */
	private String numberOfEpisodes;
	
	/** 시즌 수 */
	private String numberOfSeasons;
	
	/** 원저작물의 국가 */
	private List<String> originCountry;
	
	/** 원제목 */
	private String originalName;
	
	/** 시즌 목록 */
	private List<TmdbTvSeasonDto> seasons;
	
	/** 유형 */
	private String type;
	
	/** 크레딧 */
	private TmdbVideoCreditsDto credits;
	
	/** 종합 크레딧 */
	private TmdbVideoCreditsDto aggregateCredits;
	
}
