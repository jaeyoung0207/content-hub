package com.cjy.contenthub.person.controller.dto;

import java.util.List;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbPersonDetailsDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 인물 정보 응답 DTO
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class PersonResponseDto extends TmdbPersonDetailsDto {
	
	/** 성별 */
	private String genderValue;
	
	/** 출연작 수 */
	private int castCount;
	
	/** 제작 참여작 수 */
	private int crewCount;
	
	/** 출연작 목록 */
	private List<PersonCreditsCastDto> cast;
	
	/** 제작 참여작 목록 */
	private List<PersonCreditsCrewDto> crew;
	
}
