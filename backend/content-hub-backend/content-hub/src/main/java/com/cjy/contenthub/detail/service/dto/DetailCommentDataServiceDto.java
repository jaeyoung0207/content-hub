package com.cjy.contenthub.detail.service.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 상세 코멘트 데이터 서비스 DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class DetailCommentDataServiceDto {
	
	/** 코멘트 번호 */
	private Long commentNo;
	
	/** 원본 미디어 타입 */
	private String originalMediaType;
	
	/** API ID */
	private String apiId;
	
	/** 로그인 제공자 */
	private String provider;
	
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
