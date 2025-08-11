package com.cjy.contenthub.person.controller.dto;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbPersonCreditsCastDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 인물 크레딧 출연진 DTO
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class PersonCreditsCastDto extends TmdbPersonCreditsCastDto {
	
	/** 원제목 */
	private String originalTitle;
	
	/** 첫 상영일 */
	private String releaseDate;
	
	/** 첫 상영연도 */
	private String releaseYear;
	
	/** 제목 */
	private String title;
	
	/** 에피소드 수 (TV Only) */
	private int episodeCount;
	
	/** 미디어 타입 */
	private String mediaType;

}
