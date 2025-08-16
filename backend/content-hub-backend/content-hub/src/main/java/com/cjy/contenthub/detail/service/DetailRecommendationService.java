package com.cjy.contenthub.detail.service;

import java.io.IOException;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsMovieDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsTvDto;
import com.cjy.contenthub.detail.controller.dto.DetailComicsRecommendationsResponseDto;

/**
 * 상세 정보 추천 서비스 인터페이스
 */
public interface DetailRecommendationService {
	
	/**
	 * TV 시리즈 추천 정보 조회
	 *
	 * @param seriesId TV 시리즈 ID
	 * @param page     페이지 번호
	 * @return 추천 TV 시리즈 정보
	 */
	TmdbRecommendationsTvDto getTvRecommendations(Integer seriesId, Integer page);
	
	/**
	 * 영화 추천 정보 조회
	 *
	 * @param movieId 영화 ID
	 * @param page    페이지 번호
	 * @return 추천 영화 정보
	 */
	TmdbRecommendationsMovieDto getMovieRecommendations(Integer movieId, Integer page);
	
	/**
	 * 만화 추천 정보 조회
	 *
	 * @param comicsId 만화 ID
	 * @param page     페이지 번호
	 * @return 추천 만화 정보
	 */
	DetailComicsRecommendationsResponseDto getComicsRecommendations(Integer comicsId, Integer page) throws IOException;

}
