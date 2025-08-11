package com.cjy.contenthub.detail.controller.dto;

import java.util.List;

import com.cjy.contenthub.common.api.dto.aniist.AniListCharactersDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbVideoDetailsDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 상세 만화 응답 DTO
 * 만화의 상세 정보를 담고 있으며, 클라이언트 요청에 대한 응답으로 사용됨
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class DetailComicsResponseDto extends TmdbVideoDetailsDto {
	
	/** 만화 제목 */
	private String title;
	
	/** 만화 장르 */
	private List<String> comicsGenres;

	/** 캐릭터 DTO */
	private AniListCharactersDto characters;
	
	/** 권 수 */
	private int volumes;
	
	/** 챕터 수 */
	private int chapters;
	
	/** 연재 시작일 */
	private String startDate;

}
