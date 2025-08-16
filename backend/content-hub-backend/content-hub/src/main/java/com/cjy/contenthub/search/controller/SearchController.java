package com.cjy.contenthub.search.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchMovieDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchTvDto;
import com.cjy.contenthub.search.controller.dto.SearchComicsResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;
import com.cjy.contenthub.search.service.SearchService;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 검색 컨텐츠 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

	/** 검색 서비스 클래스 */
	private final SearchService searchService;

	/** TMDB API 통신용 WebClient 클래스 */
	@Qualifier("tmdbWebClient")
	private final WebClient tmdbWebClient;

	/** AniList API 통신용 WebClient 클래스 */
	@Qualifier("anilistWebClient")
	private final WebClient anilistWebClient;

	/** DeepL API 통신용 WebClient 클래스 */
	@Qualifier("deeplWebClient")
	private final WebClient deeplWebClient;

	/** TMDB API TV시리즈 검색 API 패스 */
	@Value("${tmdb.url.tvSearchPath}")
	private String tvSearchPath;

	/** TMDB API 영화 검색 API 패스 */
	@Value("${tmdb.url.movieSearchPath}")
	private String movieSearchPath;

	/** TMDB API 멀티(TV, 영화, 인물) 검색 API 패스 */
	@Value("${tmdb.url.multiSearchPath}")
	private String multiSearchPath;

	/** TMDB API 자동완성 표시 개수 */
	@Value("${tmdb.custom.autoCompleteCount}")
	private int autoCompleteCount;

	/** AniList API 메인화면 작품 표시 개수 */
	@Value("${anilist.custom.perMainPage}")
	private int anilistPerMainPage;

	/** AniList API 전체보기화면 작품 표시 개수 */
	@Value("${anilist.custom.perMorePage}")
	private int anilistPerMorePage;

	/** 리퀘스트 파라미터 키 : 검색어 */
	private static final String PARAM_QUERY = "query";

	/** 리퀘스트 파라미터 키 : 페이지 */
	private static final String PARAM_PAGE = "page";

	/** API 입구 판단용 파라미터 키 문자열  */
	private static final String PARAM_IS_MAIN_PAGE = "isMainPage";

	/**
	 * 애니메이션/드라마/영화 검색 API
	 * 
	 * @param keyword 검색어
	 * @return ResponseEntity<List<String>> 검색어 리스트
	 */
	@GetMapping(value = "/searchKeyword")
	public ResponseEntity<List<String>> searchKeyword(@NotEmpty @RequestParam(PARAM_QUERY) String keyword) {
		return ResponseEntity.ok(searchService.searchKeyword(keyword));
	}

	/**
	 * 애니메이션/드라마/영화 검색 API
	 * 
	 * @param keyword 검색어
	 * @return ResponseEntity<SearchVideoResponseDto> 애니메이션/드라마/영화 검색 결과 응답 오브젝트
	 */
	@GetMapping(value = "/searchVideo")
	public ResponseEntity<SearchVideoResponseDto> searchVideo(@NotEmpty @RequestParam(PARAM_QUERY) String keyword) {
		return ResponseEntity.ok(searchService.searchVideo(keyword));
	}

	/**
	 * 애니 정보 검색 API
	 * 
	 * @param keyword 검색어
	 * @param page 페이지
	 * @return ResponseEntity<TmdbSearchTvDto> 애니 정보 응답 오브젝트
	 */
	@GetMapping(value = "/searchAni")
	public ResponseEntity<TmdbSearchTvDto> searchAni(
			@NotEmpty @RequestParam(PARAM_QUERY) String keyword,
			@Nullable @RequestParam(PARAM_PAGE) Integer page
			) {
		return ResponseEntity.ok(searchService.searchAni(keyword, page));
	}

	/**
	 * 드라마 정보 검색 API
	 * 
	 * @param keyword 검색어
	 * @param page 페이지
	 * @return ResponseEntity<TmdbSearchTvDto> 드라마 정보 응답 오브젝트
	 */
	@GetMapping(value = "/searchDrama")
	public ResponseEntity<TmdbSearchTvDto> searchDrama(
			@NotEmpty @RequestParam(PARAM_QUERY) String keyword, 
			@Nullable @RequestParam(PARAM_PAGE) Integer page) {
		return ResponseEntity.ok(searchService.searchDrama(keyword, page));
	}

	/**
	 * 영화 정보 검색 API
	 * 
	 * @param keyword 검색어
	 * @param page 페이지
	 * @return ResponseEntity<TmdbSearchMovieDto> 영화 정보 응답 오브젝트
	 */
	@GetMapping(value = "/searchMovie")
	public ResponseEntity<TmdbSearchMovieDto> searchMovie(
			@NotEmpty @RequestParam(PARAM_QUERY) String keyword, 
			@Nullable @RequestParam(PARAM_PAGE) Integer page
			) {
		return ResponseEntity.ok(searchService.searchMovie(keyword, page));
	}

	/**
	 * 만화 정보 검색 API
	 * 
	 * @param keyword 검색어
	 * @param page 페이지
	 * @return ResponseEntity<SearchComicsResponseDto> 만화 정보 응답 오브젝트
	 */
	@GetMapping(value = "/searchComics")
	public ResponseEntity<SearchComicsResponseDto> searchComics(
			@NotEmpty @RequestParam(PARAM_QUERY) String keyword, 
			@Nullable @RequestParam(PARAM_PAGE) Integer page,
			@RequestParam(PARAM_IS_MAIN_PAGE) boolean isMainPage
			) {
		return ResponseEntity.ok(searchService.searchComics(keyword, page, isMainPage));
	}

}
