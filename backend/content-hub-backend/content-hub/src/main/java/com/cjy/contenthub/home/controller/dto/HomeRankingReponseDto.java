package com.cjy.contenthub.home.controller.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 홈 랭킹 응답 DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class HomeRankingReponseDto {
	
	/** 콘텐츠 ID */
	private Long contentId;

	/** 순위 */
	private Long rowNum;

	/** 컨텐츠 미디어 타입 */
	private String contentMediaType;
	
	/** 미디어 타입(화면 표시용) */
	private String displayMediaType;

	/** API ID */
	private String apiId;

	/** 별점 평균 */
	private BigDecimal starRatingAverage;
	
	/** 별점 평가 개수 */
	private Long starRatingCount;

	/** 제목 */
	private String title;

	/** 썸네일 이미지 URL */
	private String thumbnailImageUrl;
	
	/** 위시리스트 여부 */
	private boolean isWishlisted;

}
