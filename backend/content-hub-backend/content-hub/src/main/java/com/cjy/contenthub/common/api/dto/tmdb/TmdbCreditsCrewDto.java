package com.cjy.contenthub.common.api.dto.tmdb;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 크레딧(제작진) Response DTO
 * * 영화나 TV 프로그램의 제작진 정보를 포함하는 DTO
 * 
 * @see <a href="https://developers.themoviedb.org/3/movies/get-movie-credits">TMDB Credits API 문서</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbCreditsCrewDto {
	
	/** 성인물 여부 */
	private boolean adult;
	
	/** 제작진의 성별 (1: 여성, 2: 남성, 0: 알려지지 않음) */
	private int gender;
	
	/** 제작진의 고유 ID */
	private int id;

	/** 제작진이 알려진 부서 */
	private String knownForDepartment;
	
	/** 제작진의 이름 */
	private String name;
	
	/** 제작진의 원래 이름 */
	private String originalName;
	
	/** 제작진의 인기 점수 */
	private BigDecimal popularity;
	
	/** 제작진의 프로필 이미지 경로 */
	private String profilePath;
	
	/** 크레딧 ID (TMDB에서 사용하는 고유 ID) */
	private String creditId;
	
	/** 부서 */
	private String department;
	
	/** 직업 */
	private String job;
	
}
