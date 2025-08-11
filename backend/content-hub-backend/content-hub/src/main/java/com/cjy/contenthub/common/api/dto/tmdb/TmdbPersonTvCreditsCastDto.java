package com.cjy.contenthub.common.api.dto.tmdb;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 인물 TV 프로그램 출연작 DTO
 * 
 * @see <a href="https://developer.themoviedb.org/reference/person-tv-credits">TMDB Person TV Credits API 문서</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbPersonTvCreditsCastDto extends TmdbPersonCreditsCastDto {
	
	/** 원산지 국가 리스트 */
	private List<String> originCountry;
	
	/** 원래 이름 */
	private String originalName;
	
	/** 첫 방영일 */
	private String firstAirDate;
	
	/** 이름 */
	private String name;
	
	/** 에피소드 수 */
	private int episodeCount;
	
	/** 첫 작업 에피소드 방영일 */
	private String firstCreditAirDate;
	
}
