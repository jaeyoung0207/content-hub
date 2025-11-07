package com.cjy.contenthub.common.integration.tmdb.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API TV 시리즈 에피소드 Response DTO
 * 
 * @see <a href=
 *      "https://developer.themoviedb.org/reference/tv-series-details">TMDB TV
 *      Series Details API 문서</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbTvEpisodeToAir {
	
	/** 에피소드 ID */
	private int id;
	
	/** 에피소드 제목 */
	private String name;
	
	/** 에피소드 줄거리 */
	private String overview;
	
	/** 평균 점수 */
	private BigDecimal voteAverage;
	
	/** 투표 수   */
	private int voteCount;
	
	/** 방영일 */
	private String airDate;
	
	/** 에피소드 번호 */
	private int episodeNumber;
	
	/** 에피소드 타입 */
	private String episodeType;
	
	/** 작품 코드 */
	private String productionCode;
	
	/** 방영 시간 */
	private int runTime;
	
	/** 시즌 번호 */
	private int seasonNumber;
	
	/** 작품 고유 ID(series_id) */
	private int showId;
	
	/** 스틸 컷 이미지 경로 */
	private String stillPath;

}
