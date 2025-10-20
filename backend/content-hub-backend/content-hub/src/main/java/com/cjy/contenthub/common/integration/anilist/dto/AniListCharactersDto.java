package com.cjy.contenthub.common.integration.anilist.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 캐릭터 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListCharactersDto {
	
	/** 페이지 정보 DTO */
	private AniListPageInfoDto pageInfo;
	
	/** 캐릭터 DTO 리스트 */
	private List<AniListCharactersEdgesDto> edges;

}