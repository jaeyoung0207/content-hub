package com.cjy.contenthub.search.service;

import java.util.List;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchMovieDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchTvDto;
import com.cjy.contenthub.search.controller.dto.SearchComicsResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;

/**
 * 검색 컨텐츠 서비스 인터페이스
 */
public interface SearchService {
	
	/**
	 * 검색어 리스트 조회
	 * 
	 * @param keyword 검색어
	 * @return 검색어 리스트
	 */
	List<String> searchKeyword(String keyword);
	
	/**
	 * 애니/드라마/영화 검색 데이터 조회
	 * 
	 * @param keyword 검색어
	 * @return 검색 결과 DTO
	 */
	SearchVideoResponseDto searchVideo(String keyword);
	
	/**
	 * 애니 검색 데이터 조회
	 * 
	 * @param keyword 검색어
	 * @param page    페이지 번호
	 * @return 애니 검색 결과 DTO
	 */
	TmdbSearchTvDto searchAni(String keyword, Integer page);
	
	/**
	 * 드라마 검색 데이터 조회
	 * 
	 * @param keyword 검색어
	 * @param page    페이지 번호
	 * @return 드라마 검색 결과 DTO
	 */
	TmdbSearchTvDto searchDrama(String keyword, Integer page);
	
	/**
	 * 영화 검색 데이터 조회
	 * 
	 * @param keyword 검색어
	 * @param page    페이지 번호
	 * @return 영화 검색 결과 DTO
	 */
	TmdbSearchMovieDto searchMovie(String keyword, Integer page);
	
	/**
	 * 만화 검색 데이터 조회
	 * 
	 * @param keyword    검색어
	 * @param page       페이지 번호
	 * @param isMainPage 메인 페이지 여부
	 * @return 만화 검색 결과 DTO
	 */
	SearchComicsResponseDto searchComics(String keyword, Integer page, boolean isMainPage);
}
