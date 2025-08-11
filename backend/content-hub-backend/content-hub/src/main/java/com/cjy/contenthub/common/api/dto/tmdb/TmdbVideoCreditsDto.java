package com.cjy.contenthub.common.api.dto.tmdb;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 크레딧(출연진 및 제작진) Response DTO
 * 영화나 TV 프로그램의 출연진과 제작진 정보를 포함하는 크레딧 API의 응답 형식
 * 
 * @see <a href=
 *      "https://developers.themoviedb.org/3/movies/get-movie-credits">TMDB
 *      Credits API 문서</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbVideoCreditsDto {
	
	/** 출연진 목록 */
	private List<TmdbVideoCreditsCastDto> cast;
	
	/** 제작진 목록 */
	private List<TmdbVideoCreditsCrewDto> crew;

}
