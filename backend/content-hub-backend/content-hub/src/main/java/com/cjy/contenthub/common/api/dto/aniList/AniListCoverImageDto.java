package com.cjy.contenthub.common.api.dto.aniList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 커버 이미지 Response DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListCoverImageDto {

	/** 커버 이미지 색상 */
	private String color;

	/** 중간 사이즈 이미지 */
	private String medium;

	/** 큰 사이즈 이미지 */
	private String large;

	/** 초대형 사이즈 이미지 */
	private String extraLarge;

}