package com.cjy.contenthub.common.api.dto.tmdb;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 시청 가능 서비스 제공자 정보 Response DTO 
 * 영화나 TV 프로그램의 서비스 제공자의 시청 가능 형식을 포함하는 API의 응답 형식
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbWatchProvidersTypeDto {
	
	/** TMDB API 해당 작품 정보 링크 */
	private String link;
	
	/** 광고 형식 */
	private List<TmdbWatchProvidersDetailDto> ads;
	
	/** 고정 요금 **/
	private List<TmdbWatchProvidersDetailDto> flatrate;
	
	/** 무료 */
	private List<TmdbWatchProvidersDetailDto> free;

	/** 렌트 */
	private List<TmdbWatchProvidersDetailDto> rent;
	
	/** 구매 */
	private List<TmdbWatchProvidersDetailDto> buy;
	
}
