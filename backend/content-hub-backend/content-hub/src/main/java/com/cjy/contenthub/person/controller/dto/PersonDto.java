package com.cjy.contenthub.person.controller.dto;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbPersonDetailsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbPersonMovieCreditsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbPersonTvCreditsDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 인물 정보 DTO
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class PersonDto extends TmdbPersonDetailsDto {
	
	/** TV 프로그램 크레딧 정보 */
	private TmdbPersonTvCreditsDto tvCredits;
	
	/** 영화 크레딧 정보 */
	private TmdbPersonMovieCreditsDto movieCredits;
	
}
