package com.cjy.contenthub.detail.service;

import java.io.IOException;

import com.cjy.contenthub.detail.controller.dto.DetailComicsResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailMovieResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailTvResponseDto;

public interface DetailInformationService {
	
	/**
	 * TV 시리즈 상세 정보 조회
	 *
	 * @param seriesId TV 시리즈 ID
	 * @return TV 상세 응답 DTO
	 */
	DetailTvResponseDto	getTvDetail(Integer seriesId);
	
	/**
	 * 영화 상세 정보 조회
	 *
	 * @param movieId 영화 ID
	 * @return 영화 상세 응답 DTO
	 */
	DetailMovieResponseDto getMovieDetail(Integer movieId);
	
	/**
	 * 만화 상세 정보 조회
	 *
	 * @param comicsId Comics ID
	 * @return Comics 상세 응답 DTO
	 */
	DetailComicsResponseDto getComicsDetail(Integer comicsId, Integer page) throws IOException;

}
