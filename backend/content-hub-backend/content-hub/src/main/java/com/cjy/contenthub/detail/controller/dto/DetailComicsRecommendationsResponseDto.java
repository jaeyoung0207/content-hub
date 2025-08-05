package com.cjy.contenthub.detail.controller.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 상세 만화 추천 응답 DTO
 * 만화 추천 결과를 담고 있으며, 클라이언트 요청에 대한 응답으로 사용됨
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class DetailComicsRecommendationsResponseDto {
	
	/** 추천 결과 리스트 */
	private List<DetailComicsRecommendationsResultDto> results;

}
