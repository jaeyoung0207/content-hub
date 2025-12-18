package com.cjy.contenthub.search.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.cjy.contenthub.search.controller.dto.SearchComicsResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;

/**
 * 검색 콘텐츠 서비스 인터페이스
 */
public interface SearchService {
	
	/**
	 * 검색어 리스트 조회
	 * 
	 * @param keyword 검색어
	 * @param isAdult 성인물 포함 여부
	 * @return 검색어 리스트
	 */
	CompletableFuture<List<String>> searchKeyword(String keyword, boolean isAdult);
	
	/**
	 * TV 시리즈/영화 검색 데이터 조회
	 * 
	 * @param keyword 검색어
	 * @param isAdult 성인물 포함 여부
	 * @return 검색 결과 DTO
	 */
	CompletableFuture<SearchVideoResponseDto> searchVideo(String keyword, boolean isAdult);
	
	/**
	 * 애니 검색 데이터 조회
	 * 
	 * @param keyword 검색어
	 * @param isAdult 성인물 포함 여부
	 * @param page    페이지 번호
	 * @return 애니 검색 결과 DTO
	 */
	CompletableFuture<SearchTvResponseDto> searchAni(String keyword, boolean isAdult, Integer page);
	
	/**
	 * TV 시리즈 검색 데이터 조회(애니 제외)
	 * 
	 * @param keyword 검색어
	 * @param isAdult 성인물 포함 여부
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param page    페이지 번호
	 * @return 드라마 검색 결과 DTO
	 */
	CompletableFuture<SearchTvResponseDto> searchTvExceptAni(String keyword, boolean isAdult, String contentMediaType, Integer page);
	
	/**
	 * 영화 검색 데이터 조회
	 * 
	 * @param keyword 검색어
	 * @param isAdult 성인물 포함 여부
	 * @param page    페이지 번호
	 * @return 영화 검색 결과 DTO
	 */
	CompletableFuture<SearchMovieResponseDto> searchMovie(String keyword, boolean isAdult, Integer page);
	
	/**
	 * 만화 검색 데이터 조회
	 * 
	 * @param keyword    검색어
	 * @param isAdult 성인물 포함 여부
	 * @param page       페이지 번호
	 * @param isMainPage 메인 페이지 여부
	 * @return 만화 검색 결과 DTO
	 */
	CompletableFuture<SearchComicsResponseDto> searchComics(String keyword, boolean isAdult, Integer page, boolean isMainPage);
}
