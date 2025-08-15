package com.cjy.contenthub.common.api.dto.tmdb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 역할 정보 Response DTO
 * 
 * @see <a href=
 *      "https://developer.themoviedb.org/reference/tv-series-aggregate-credits">TMDB Aggregate Credits API 문서</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbRoleDto {
	
	/** 크레딧 ID */
	private String creditId;
	
	/** 역할 */
	private String character;
	
	/** 에피소드 수 */
	private String episodeCount;

}
