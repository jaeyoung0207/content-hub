package com.cjy.contenthub.my.comments.controller.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 나의 코멘트 데이터 응답 DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class MyCommentsDataResponseDto {
	
	/** 코멘트 ID */
	private Long commentId;
	
	/** 컨텐츠 ID */
	private Long contentId;

	/** 컨텐츠 미디어 타입 */
	private String contentMediaType;

	/** API ID */
	private String apiId;	

	/** 제목 */
	private String title;

	/** 썸네일 이미지 URL */
	private String thumbnailImageUrl;
	
	/** 코멘트 */
	private String comment;
	
	/** 별점 */
	private BigDecimal starRating;
	
	/** 작성 일자 문자열 */
	private String createTimeStr;

}
