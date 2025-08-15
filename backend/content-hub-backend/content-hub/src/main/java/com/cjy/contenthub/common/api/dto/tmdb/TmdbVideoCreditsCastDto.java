package com.cjy.contenthub.common.api.dto.tmdb;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 크레딧(출연진) Response DTO
 * TMDB에서 제공하는 영화 및 TV 프로그램의 출연진 정보를 포함하는 DTO
 * 
 * @see <a href="https://developer.themoviedb.org/reference/tv-series-aggregate-credits">TMDB TV Credits API 문서</a>
 * @see <a href="https://developers.themoviedb.org/3/movies/get-movie-credits">TMDB Movie Credits API 문서</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbVideoCreditsCastDto {
	
	/** 성인물 여부 */
	private boolean adult;
	
	/** 출연진의 성별 (1: 여성, 2: 남성, 0: 알려지지 않음) */
	private int gender;
	
	/** 출연진의 고유 ID */
	private int id;

	/** 출연진이 알려진 부서 */
	private String knownForDepartment;
	
	/** 출연진의 이름 */
	private String name;
	
	/** 출연진의 원래 이름 */
	private String originalName;
	
	/** 출연진의 인기 점수 */
	private BigDecimal popularity;
	
	/** 출연진의 프로필 이미지 경로 */
	private String profilePath;
	
	/** 출연진 ID (movie only) */
	private int castId;
	
	/** 출연진의 배역 */
	private String character;
	
	/** 크레딧 ID (TMDB에서 사용하는 고유 ID) */
	private String creditId;
	
	/** 역할 정보 (TV Only) */
	private List<TmdbRoleDto> roles;
	
	/** 총 에피소드 수 (TV Only) */
	private int totalEpisodeCount;
	
	/** 순서 */
	private int order;

}
