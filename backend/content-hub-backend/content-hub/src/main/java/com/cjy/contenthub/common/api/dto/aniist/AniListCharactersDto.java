package com.cjy.contenthub.common.api.dto.aniist;

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
	
	/** 캐릭터 DTO 리스트 */
	private List<AniListCharactersNodesDto> nodes;
	
	/** 페이지 정보 DTO */
	private AniListPageInfoDto pageInfo;

}