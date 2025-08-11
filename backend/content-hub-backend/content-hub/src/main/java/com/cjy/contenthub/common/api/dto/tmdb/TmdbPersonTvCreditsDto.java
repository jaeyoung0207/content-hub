package com.cjy.contenthub.common.api.dto.tmdb;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 인물 TV 프로그램 크레딧 DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbPersonTvCreditsDto {

	/** TMDB API 인물 TV 프로그램 출연작 DTO */
	List<TmdbPersonTvCreditsCastDto> cast;
	
	/** TMDB API 인물 TV 프로그램 제작 참여작 DTO */
	List<TmdbPersonTvCreditsCrewDto> crew;

}
