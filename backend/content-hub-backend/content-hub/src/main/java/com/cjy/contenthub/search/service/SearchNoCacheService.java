package com.cjy.contenthub.search.service;

import com.cjy.contenthub.search.controller.dto.SearchComicsResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;

/**
 * 검색 콘텐츠 서비스 인터페이스 (캐시 미사용)
 */
public interface SearchNoCacheService {
	
	/**
	 * 검색 결과에 위시리스트 여부 설정
	 * 
	 * @param searchVideoResults 검색 결과 DTO
	 * @param userId             유저 ID
	 */
	void setWishlistFromVideoResponse(SearchVideoResponseDto serchVideoResponse, Long userId);
	
	/**
	 * 애니 검색 결과에 위시리스트 여부 설정
	 * 
	 * @param searchTvResponse 검색 결과 DTO
	 * @param userId            유저 ID
	 */
	void setWishlistFromAniResponse(SearchTvResponseDto searchTvResponse, Long userId);
	
	/**
	 * TV 검색 결과에 위시리스트 여부 설정 (애니 제외)
	 * 
	 * @param searchTvResponse 검색 결과 DTO
	 * @param userId           유저 ID
	 * @param contentMediaType 콘텐츠 미디어 타입
	 */
	void setWishlistFromTvExceptAniResponse(SearchTvResponseDto searchTvResponse, Long userId, String contentMediaType);
	
	/**
	 * 영화 검색 결과에 위시리스트 여부 설정
	 * 
	 * @param searchMovieResponse 검색 결과 DTO
	 * @param userId          유저 ID
	 */
	void setWishlistFromMovieResponse(SearchMovieResponseDto searchMovieResponse, Long userId);
	
	/**
	 * 만화 검색 결과에 위시리스트 여부 설정
	 * 
	 * @param searchComicsResponse 검색 결과 DTO
	 * @param userId         유저 ID
	 */
	void setWishlistFromComicsResponse(SearchComicsResponseDto searchComicsResponse, Long userId);

}
