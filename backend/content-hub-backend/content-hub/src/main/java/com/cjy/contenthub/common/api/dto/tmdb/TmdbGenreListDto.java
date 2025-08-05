package com.cjy.contenthub.common.api.dto.tmdb;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 장르 목록 Response DTO
 * 영화나 TV 프로그램의 장르 정보를 포함하는 장르 목록 API의 응답 형식
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbGenreListDto {

	/** 장르 목록 */
	private List<TmdbGenreDto> genres;

}
