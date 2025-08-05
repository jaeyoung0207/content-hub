package com.cjy.contenthub.detail.controller.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 상세 코멘트 수정 요청 DTO
 * 클라이언트에서 전달된 유저 코멘트을 수정하기 위한 요청 정보를 포함
 */
@Setter
@Getter
@NoArgsConstructor
public class DetailCommentUpdateRequestDto {
	
	/** 코멘트 번호 */
	@NotNull
	private Long commentNo;
	
	/** 원본 미디어 타입 */
	@NotEmpty
	private String originalMediaType;
	
	/** API ID */
	@NotEmpty
	private String apiId;
	
	/** 유저 ID */
	@NotEmpty
	private String userId;
	
	/** 닉네임 */
	@NotEmpty
	private String nickname;
	
	/** 별점 */
	@NotNull
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
