package com.cjy.contenthub.common.api.dto.tmdb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB API 시청 가능 제공자 상세 정보 Response DTO
 * 특정 콘텐츠의 시청 가능 제공자에 대한 상세 정보를 포함하는 API의 응답 형식
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbWatchProvidersDetailDto {
	
	/** 로고 이미지 경로 */
	private String logoPpath;
	
	/** 시청 가능 제공자 ID */
	private int providerId;
	
	/** 시청 가능 제공자 이름 */
	private String providerName;
	
	/** 표시 우선 순위 */
	private int displayPriority;

}
