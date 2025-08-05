package com.cjy.contenthub.common.api.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 시청 가능 국가 정보 Response DTO 
 * 특정 국가의 시청 가능한 국가 정보를 포함하는 API의 응답 형식
 * 각 국가별로 시청 가능한 서비스 제공자 정보를 담고 있는 DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbWatchProvidersResultsDto {
	
	/** 한국 시청 가능 서비스 제공자 정보 */
	@JsonProperty(value = "KR")
	private TmdbWatchProvidersTypeDto kr;
	
	/** 일본 시청 가능 서비스 제공자 정보 */
	@JsonProperty(value = "JP")
	private TmdbWatchProvidersTypeDto jp;
	

}
