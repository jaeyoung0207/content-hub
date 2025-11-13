package com.cjy.contenthub.detail.comments.controller.dto;

import java.math.BigDecimal;
import java.util.List;

import com.cjy.contenthub.common.annotation.MaskingTarget;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 상세 코멘트 저장 요청 DTO
 * 클라이언트에서 전달된 유저 코멘트을 저장하기 위한 요청 정보를 포함
 */
@Setter
@Getter
@NoArgsConstructor
public class DetailCommentsSaveRequestDto {
	
	/** 컨텐츠 미디어 타입 */
	@NotEmpty
	private String contentMediaType;
	
	/** API ID */
	@NotEmpty
	private String apiId;
	
	/** 장르 ID 목록 */
	@NotEmpty
	private List<Integer> genreIds;
	
	/** 제목 */
	@NotEmpty
	private String title;
	
	/** 썸네일 이미지 URL */
	private String thumbnailImageUrl;
	
	/** 콘텐츠 제공자 */
	@NotEmpty
	private String provider;
	
	/** 유저 ID */
	@NotEmpty
	@MaskingTarget
	private String providerId;
	
	/** 닉네임 */
	@NotEmpty
	private String nickname;
	
	/** 별점 */
	@Digits(integer = 1, fraction = 1)
	private BigDecimal starRating;
	
	/** 코멘트 */
	@NotEmpty
	private String comment;
	
	/** 추천 수 */
	private Long good;
	
	/** 비추천 수 */
	private Long bad;
	
}
