package com.cjy.contenthub.detail.controller.dto;

import java.math.BigDecimal;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbTvDetailsDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * TV 상세 응답 DTO
 * TV 프로그램의 상세 정보를 담고 있으며, 클라이언트 요청에 대한 응답으로 사용됨
 * TMDB API TV 상세 정보를 포함
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DetailTvResponseDto extends TmdbTvDetailsDto {
	
	/** 링크 */
	private String link;
	
	/** 별점 */
	private BigDecimal starRatingAverage;

}
