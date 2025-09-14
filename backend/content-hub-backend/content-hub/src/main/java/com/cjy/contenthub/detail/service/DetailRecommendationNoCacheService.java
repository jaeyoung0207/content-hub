package com.cjy.contenthub.detail.service;

import com.cjy.contenthub.detail.controller.dto.DetailComicsRecommendationsResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailRecommendationsMovieDto;
import com.cjy.contenthub.detail.controller.dto.DetailRecommendationsTvDto;

/**
 * 상세 추천 서비스 인터페이스 (캐시 미사용)
 */
public interface DetailRecommendationNoCacheService {
	
	/**
	 * TV 상세 추천 응답에서 위시리스트 상태 설정
	 * 
	 * @param tvResponse TV 상세 응답 DTO
	 * @param userId     사용자 ID
	 */
	void setWishlistFromTvResponse(DetailRecommendationsTvDto tvResponse, Long userId);
	
	/**
	 * 영화 상세 추천 응답에서 위시리스트 상태 설정
	 * 
	 * @param movieResponse 영화 상세 응답 DTO
	 * @param userId        사용자 ID
	 */
	void setWishlistFromMovieResponse(DetailRecommendationsMovieDto movieResponse, Long userId);
	
	/**
	 * 만화 상세 추천 응답에서 위시리스트 상태 설정
	 * 
	 * @param comicsResponse 만화 상세 응답 DTO
	 * @param userId         사용자 ID
	 */
	void setWishlistFromComicsResponse(DetailComicsRecommendationsResponseDto comicsResponse, Long userId);

}
