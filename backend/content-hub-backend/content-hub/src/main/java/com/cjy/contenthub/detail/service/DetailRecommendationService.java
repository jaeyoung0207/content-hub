package com.cjy.contenthub.detail.service;

import java.io.IOException;

import com.cjy.contenthub.detail.controller.dto.DetailComicsRecommendationsResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailRecommendationsMovieDto;
import com.cjy.contenthub.detail.controller.dto.DetailRecommendationsTvDto;

/**
 * 상세 추천 서비스 인터페이스
 */
public interface DetailRecommendationService {
	
	/**
	 * TV 시리즈 추천 정보 조회
	 *
	 * @param seriesId TV 시리즈 ID
	 * @param page     페이지 번호
	 * @param userId   유저 테이블 ID
	 * @return 추천 TV 시리즈 정보
	 */
	DetailRecommendationsTvDto getTvRecommendations(Integer seriesId, Integer page, Long userId);
	
	/**
	 * 영화 추천 정보 조회
	 *
	 * @param movieId 영화 ID
	 * @param page    페이지 번호
	 * @param userId  유저 테이블 ID
	 * @return 추천 영화 정보
	 */
	DetailRecommendationsMovieDto getMovieRecommendations(Integer movieId, Integer page, Long userId);
	
	/**
	 * 만화 추천 정보 조회
	 *
	 * @param comicsId 만화 ID
	 * @param page     페이지 번호
	 * @param userId   유저 테이블 ID
	 * @return 추천 만화 정보
	 */
	DetailComicsRecommendationsResponseDto getComicsRecommendations(Integer comicsId, Integer page, Long userId) throws IOException;

}
