package com.cjy.contenthub.common.api.dto.aniList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 캐릭터 이미지 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListCharactersImageDto {
	
	/** 큰 사이즈 */
	private String large;

	/** 중간 사이즈 */
	private String medium;

}
