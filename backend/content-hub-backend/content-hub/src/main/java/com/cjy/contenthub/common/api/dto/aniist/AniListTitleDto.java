package com.cjy.contenthub.common.api.dto.aniist;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 제목 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListTitleDto {

	/** 로마자 제목 */
	private String romaji;

	/** 영어 제목 */
	private String english;

	/** 원어 제목 */
	@JsonProperty("native")
	private String nativeTitle;
	
	/** 유저 선호 제목 */
	private String userPreferred;
}