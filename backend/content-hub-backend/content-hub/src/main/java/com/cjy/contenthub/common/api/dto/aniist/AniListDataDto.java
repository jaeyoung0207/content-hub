package com.cjy.contenthub.common.api.dto.aniist;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 데이터 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListDataDto {
	
	/** 미디어 DTO */
	@JsonProperty("Media")
	private AniListMediaDto media;
	
	/** 페이지 DTO */
	@JsonProperty("Page")
	private AniListPageDto page;
	
	/** 추천 DTO */
	@JsonProperty("Recommendation")
	private AniListRecommendationDto recommendation;
	
	/** 캐릭터 DTO */
	@JsonProperty("Character")
	private AniListCharactersNodesDto character;

}
