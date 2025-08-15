package com.cjy.contenthub.common.api.dto.aniist;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 페이지 정보 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListPageInfoDto {
	
	/** 총 아이템 수 */
	private int total;
	
	/** 마지막 페이지 */
	private int lastPage;
	
	/** 현재 페이지 */
	private int currentPage;
	
	/** 다음 페이지 여부 */
	private boolean hasNextPage;
	
	/** 페이지당 표시 개수 */
	private boolean perPage;

}
