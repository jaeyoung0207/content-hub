package com.cjy.contenthub.common.integration.anilist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 이름 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListNameDto {
	
	/** 캐릭터 전체 이름 */
	private String full;

	/** 캐릭터 원어 이름 */
	@JsonProperty("native")
	private String nativeName;

	/** 캐릭터 유저 선호 이름 */
	private String userPreferred;

}
