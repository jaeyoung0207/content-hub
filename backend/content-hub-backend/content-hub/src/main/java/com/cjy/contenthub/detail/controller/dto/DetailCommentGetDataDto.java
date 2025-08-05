package com.cjy.contenthub.detail.controller.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 상세 코멘트 데이터 DTO
 * 콘텐츠에 대한 유저 코멘트의 상세 정보를 담고 있으며, 클라이언트 요청에 대한 응답으로 사용됨
 */
@Setter
@Getter
@NoArgsConstructor
public class DetailCommentGetDataDto {
	
	/** 코멘트 번호 */
	private Long commentNo;
	
	/** 원본 미디어 타입 */
	private String originalMediaType;
	
	/** API ID */
	private String apiId;
	
	/** 유저 ID */
	private String userId;
	
	/** 닉네임 */
	private String nickname;
	
	/** 별점 */
	private BigDecimal starRating;
	
	/** 코멘트 내용 */
	private String comment;
	
	/** 추천 수 */
	private Long good;
	
	/** 비추천 수 */
	private Long bad;
	
	/** 작성 시간 */
	private String createTime;
	
}
