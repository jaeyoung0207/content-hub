package com.cjy.contenthub.common.api.dto.tmdb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 직무 정보 Response DTO
 * 
 * @see <a href=
 *      "https://developer.themoviedb.org/reference/tv-series-aggregate-credits">TMDB Aggregate Credits API 문서</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbJobDto {
	
	/** 크레딧 ID */
	private String creditId;
	
	/** 직무 */
	private String job;
	
	/** 에피소드 수 */
	private String episodeCount;

}
