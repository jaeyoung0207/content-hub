package com.cjy.contenthub.common.integration.anilist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 스태프 엣지 DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListStaffEdgesDto {
	
	/** 미디어/스태프 간 연결 ID */
	private int id;
	
	/** 역할 */
	private String role;
	
	/** 스태프 노드 DTO */
	private AniListStaffNodeDto node;

}
