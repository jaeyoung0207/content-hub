package com.cjy.contenthub.common.api.dto.aniist;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 스태프 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListStaffDto {
	
	/** 페이지 정보 */
	private AniListPageInfoDto pageInfo;
	
	/** 스태프 엣지 리스트 */
	private List<AniListStaffEdgesDto> edges;

}
