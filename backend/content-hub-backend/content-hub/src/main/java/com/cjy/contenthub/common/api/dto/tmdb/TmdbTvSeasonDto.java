package com.cjy.contenthub.common.api.dto.tmdb;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API TV 시즌 정보 Response DTO
 * TV 프로그램의 시즌 정보를 포함하는 API의 응답 형식
 * 
 * @see <a href="https://developer.themoviedb.org/reference/tv-series-details">TMDB TV Season Details API</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbTvSeasonDto {
	
	/** 방영일 */
	private String airDate;
	
	/** 에피소드 수 */
	private int episodeCount;
	
	/** 시즌 ID */
	private int id;
	
	/** 시즌명 */
	private String name;
	
	/** 개요 */
	private String overview;
	
	/** 포스터 이미지 경로 */
	private String posterPath;
	
	/** 시즌 번호 */
	private String seasonNumber;
	
	/** 평균 점수 */
	private BigDecimal voteAverage;

}
