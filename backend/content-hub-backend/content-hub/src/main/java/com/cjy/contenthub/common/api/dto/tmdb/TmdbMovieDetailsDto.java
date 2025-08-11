package com.cjy.contenthub.common.api.dto.tmdb;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * TMDB API 영화 상세 Response DTO
 * TMDB API를 통해 영화의 상세 정보를 조회할 때 사용하는 DTO
 * 
 * @see <a href=
 *      "https://developer.themoviedb.org/reference/movie-details">TMDB
 *      Movie Details API 문서</a>
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class TmdbMovieDetailsDto extends TmdbVideoDetailsDto {
	
	/** 장르 목록 */
	List<TmdbGenreDto> genres;
	
	/** IMDB ID */
	private String imdbId;
	
	/** 원제목 */
	private String originalTitle;
	
	/** 개봉일 */
	private String releaseDate;
	
	/** 상영시간 */
	private int runtime;
	
	/** 작품 제목 */
	private String title;
	
	/** 크레딧 */
	private TmdbVideoCreditsDto credits;
	
}
