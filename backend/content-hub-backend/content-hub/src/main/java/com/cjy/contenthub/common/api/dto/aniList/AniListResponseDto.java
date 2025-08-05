package com.cjy.contenthub.common.api.dto.aniList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 응답 DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListResponseDto {
	
	/** 데이터 DTO */
	private AniListDataDto data;
	
}