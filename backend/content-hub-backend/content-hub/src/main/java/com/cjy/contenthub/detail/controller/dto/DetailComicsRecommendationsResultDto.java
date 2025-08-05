package com.cjy.contenthub.detail.controller.dto;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsCommonResultsDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 상세 만화 추천 결과 DTO
 * 만화 추천 결과를 담고 있으며, 클라이언트 요청에 대한 응답으로 사용됨
 * TMDB API 추천 콘텐츠 공통 결과를 포함
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class DetailComicsRecommendationsResultDto extends TmdbRecommendationsCommonResultsDto {
	
	/** 만화 제목 */
	private String title;

}
