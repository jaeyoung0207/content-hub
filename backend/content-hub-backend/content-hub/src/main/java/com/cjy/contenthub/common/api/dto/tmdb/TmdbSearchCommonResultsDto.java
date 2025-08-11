package com.cjy.contenthub.common.api.dto.tmdb;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * TMDB API 공통 검색 결과 Response DTO
 * 영화, TV 프로그램 등 다양한 콘텐츠의 공통 검색 결과를 포함하는 DTO
 * 
 * @see <a href=
 *      "https://developer.themoviedb.org/reference/search-multi">TMDB
 *      Search Multi API 문서</a>
 * @see <a href=
 *      "https://developer.themoviedb.org/reference/search-movie">TMDB
 *      Search Movie API 문서</a>
 * @see <a href=
 *      "https://developer.themoviedb.org/reference/search-tv">TMDB
 *      Search TV API 문서</a>
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class TmdbSearchCommonResultsDto {

	/** 성인물 여부 */
	private boolean adult;
	
	/** 썸네일 이미지 경로 */
	private String backdropPath;
	
	/** 장르 ID 리스트 */
	private List<Integer> genreIds;
	
	/** 작품 ID */
	private int id;
	
	/** 원어 */
	private String originalLanguage;
	
	/** 원제목 */
	private String originalName;
	
	/** 개요 */
	private String overview;
	
	/** 인기 점수 */
	private BigDecimal popularity;
	
	/** 포스터 이미지 경로 */
	private String posterPath;
	
	/** 평균 점수 */
	private BigDecimal voteAverage;
	
	/** 투표 수 */
	private int voteCount;
	
	/** 장르명 리스트 */
	private List<String> genreNames;

	/** 원작품 미디어 타입 */
	private String originalMediaType;
}
