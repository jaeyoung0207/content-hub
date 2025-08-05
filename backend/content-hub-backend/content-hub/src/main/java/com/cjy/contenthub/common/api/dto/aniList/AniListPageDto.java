package com.cjy.contenthub.common.api.dto.aniList;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 페이지 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListPageDto {
	
	/** 페이지 DTO */
	private AniListPageInfoDto pageInfo;
	
	/** 미디어 DTO 리스트 */
	private List<AniListMediaDto> Media;

}
