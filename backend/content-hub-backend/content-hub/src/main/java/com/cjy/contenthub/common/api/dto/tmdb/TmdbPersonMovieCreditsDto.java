package com.cjy.contenthub.common.api.dto.tmdb;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 인물 영화 크레딧 DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbPersonMovieCreditsDto {

	/** TMDB API 인물 영화 출연작 DTO */
	List<TmdbPersonMovieCreditsCastDto> cast;
	
	/** TMDB API 인물 영화 제작 참여작 DTO */
	List<TmdbPersonMovieCreditsCrewDto> crew;

}
