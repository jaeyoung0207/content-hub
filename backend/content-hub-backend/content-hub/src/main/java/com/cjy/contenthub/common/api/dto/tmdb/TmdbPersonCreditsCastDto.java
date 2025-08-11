package com.cjy.contenthub.common.api.dto.tmdb;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * TMDB API 인물 출연작 크레딧 DTO
 * 
 * @see <a href="https://developer.themoviedb.org/reference/person-tv-credits">TMDB Person TV Credits API 문서</a>
 * @see <a href="https://developer.themoviedb.org/reference/person-movie-credits">TMDB Person Movie Credits API 문서</a>
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class TmdbPersonCreditsCastDto {
	
	/** 성인물 여부 */
	private boolean adult;
	
	/** 썸네일 이미지 경로 */
	private String backdropPath;
	
	/** 장르 ID 목록 */
	private List<Integer> genreIds;
	
	/** 출연진의 고유 ID */
	private int id;
	
	/** 원어 */
	private String originalLanguage;
	
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
	
	/** 배역 */
	private String character;
	
	/** 크레딧 ID (TMDB에서 사용하는 고유 ID) */
	private String creditId;
	
}
