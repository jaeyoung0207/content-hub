package com.cjy.contenthub.common.integration.anilist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 캐릭터 엣지 DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListCharactersEdgesDto {
	
	/** 미디어/캐릭터 간 연결 ID */
	private int id;

	/** 역할 */
	private String role;

	/** 캐릭터 노드 DTO */
	private AniListCharactersNodeDto node;

}
