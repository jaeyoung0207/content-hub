package com.cjy.contenthub.detail.information.controller.dto;

import java.util.List;

import com.cjy.contenthub.common.integration.anilist.dto.AniListCharactersDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListStaffDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbVideoDetailsDto;

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
	
	/** 만화 다른 제목 */
	private List<String> synonyms;
	
	/** 만화 장르 */
	private List<String> comicsGenres;
	
	/** 장르 ID 목록 */
	private List<Integer> genreIds;

	/** 캐릭터 DTO */
	private AniListCharactersDto characters;
	
	/** 스태프 DTO */
	private AniListStaffDto staff;
	
	/** 권 수 */
	private int volumes;
	
	/** 챕터 수 */
	private int chapters;
	
	/** 연재 시작일 */
	private String startDate;
	
	/** 위시리스트 여부 */
	private boolean wishlisted;

}
