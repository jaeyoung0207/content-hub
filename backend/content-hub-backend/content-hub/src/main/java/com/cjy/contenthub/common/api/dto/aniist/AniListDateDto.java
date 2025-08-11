package com.cjy.contenthub.common.api.dto.aniist;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 시작 날짜 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListDateDto {

	/** 연도 */
	private int year;

	/** 월 */
	private int month;

	/** 일 */
	private int day;

}