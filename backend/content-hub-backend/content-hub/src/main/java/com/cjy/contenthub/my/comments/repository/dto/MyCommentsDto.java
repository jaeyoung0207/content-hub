package com.cjy.contenthub.my.comments.repository.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 나의 코멘트 DTO 클래스 
 * 유저 코멘트와 관련된 데이터를 전송하기 위한 데이터 전송 객체
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MyCommentsDto {
	
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
	
	/** 작성 일시 */
	private Timestamp createTime;

}
