package com.cjy.contenthub.common.api.dto.aniist;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 관련(상세) Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListRelationsDto {
	
	/** 관련(상세) 노드 DTO 리스트 */
	private List<AniListRelationsNodeDto> nodes;

}
