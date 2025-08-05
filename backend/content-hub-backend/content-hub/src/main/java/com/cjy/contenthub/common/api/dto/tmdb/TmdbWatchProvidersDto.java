package com.cjy.contenthub.common.api.dto.tmdb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 시청 가능한 국가 및 서비스 제공자 정보 Response DTO 
 * 특정 콘텐츠에 대한 시청 가능한 국가 및 서비스 제공자 정보를 포함하는 API의 응답 형식
 * 
 * @see <a href="https://developer.themoviedb.org/reference/tv-series-watch-providers">TMDB TV Watch Providers API</a>
 * @see <a href="https://developer.themoviedb.org/reference/movie-watch-providers">TMDB TV Watch Providers API</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbWatchProvidersDto {
	
	/** 콘텐츠 ID */
	private int id;
	
	/** 시청 가능한 국가 및 서비스 제공자 정보 */
	private TmdbWatchProvidersResultsDto results;

}
