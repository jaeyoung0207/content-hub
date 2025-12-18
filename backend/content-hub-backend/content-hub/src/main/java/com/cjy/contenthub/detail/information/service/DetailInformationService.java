package com.cjy.contenthub.detail.information.service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import com.cjy.contenthub.common.integration.anilist.dto.AniListCharactersDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListStaffDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailComicsResponseDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailMovieResponseDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailTvResponseDto;

/**
 * 상세 정보 서비스 인터페이스
 */
public interface DetailInformationService {
	
	/**
	 * TV 시리즈 상세 정보 조회
	 *
	 * @param seriesId TV 시리즈 ID
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @return TV 상세 응답 DTO
	 */
	CompletableFuture<DetailTvResponseDto> getTvDetail(Integer seriesId, String contentMediaType);
	
	/**
	 * 영화 상세 정보 조회
	 *
	 * @param movieId 영화 ID
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @return 영화 상세 응답 DTO
	 */
	CompletableFuture<DetailMovieResponseDto> getMovieDetail(Integer movieId, String contentMediaType);
	
	/**
	 * 만화 상세 정보 조회
	 *
	 * @param comicsId Comics ID
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @return Comics 상세 응답 DTO
	 */
	CompletableFuture<DetailComicsResponseDto> getComicsDetail(Integer comicsId, String contentMediaType) throws IOException;
	
	/**
	 * 만화 등장인물 목록 조회
	 *
	 * @param comicsId Comics ID
	 * @param page     페이지 번호
	 * @return AniList 등장인물 DTO
	 */
	CompletableFuture<AniListCharactersDto> getComicsCharacterList(Integer comicsId, Integer page)  throws IOException;
	
	/**
	 * 만화 스태프 목록 조회
	 *
	 * @param comicsId Comics ID
	 * @param page     페이지 번호
	 * @return AniList 스태프 DTO
	 */
	CompletableFuture<AniListStaffDto> getComicsStaffList(Integer comicsId, Integer page) throws IOException;

}
