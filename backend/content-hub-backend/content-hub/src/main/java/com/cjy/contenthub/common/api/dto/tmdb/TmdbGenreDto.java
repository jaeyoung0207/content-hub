package com.cjy.contenthub.common.api.dto.tmdb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 장르 Response DTO
 * 영화 및 TV 프로그램의 장르 정보를 담고 있는 DTO
 * 
 * @see <a href="https://developer.themoviedb.org/reference/genre-movie-list">TMDB Movie Genres API 문서</a>
 * @see <a href="https://developer.themoviedb.org/reference/genre-tv-list">TMDB TV Genres API 문서</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbGenreDto {

	/** 장르 ID */
	private int id;

	/** 장르명 */
	private String name;

}
