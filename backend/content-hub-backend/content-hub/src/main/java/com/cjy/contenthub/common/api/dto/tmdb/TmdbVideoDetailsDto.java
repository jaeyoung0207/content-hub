package com.cjy.contenthub.common.api.dto.tmdb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * TMDB API 공통 상세 Response DTO
 * 영화나 TV 프로그램의 상세 정보를 조회할 때 사용하는 공통 DTO
 * 
 * @see <a href=
 *      "https://developer.themoviedb.org/reference/movie-details">TMDB
 *      Movie Details API 문서</a>
 * @see <a href="https://developer.themoviedb.org/reference/tv-series-details">TMDB TV
 *      Details API 문서</a>
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class TmdbVideoDetailsDto {
	
	/** 성인물 여부 */
	private boolean adult;
	
	/** 썸네일 이미지 경로 */
	private String backdropPath;
	
	/** 홈페이지 */
	private String homepage;
	
	/** 원어 */
	private String originalLanguage;
	
	/** 고유 ID */
	private int id;
	
	/** 개요 */
	private String overview;
	
	/** 포스터 이미지 경로 */
	private String posterPath;
	
	/** 작품 상태 */
	private String status;
	
}
