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

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.util.SessionUtil;
import com.cjy.contenthub.search.controller.dto.SearchComicsResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;
import com.cjy.contenthub.search.service.SearchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 검색 콘텐츠 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

	/** 검색 서비스 클래스 */
	private final SearchService searchService;
	
	/** 세션 유틸 클래스 */
	private final SessionUtil session;

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
	private static final String PARAM_KEYWORD = "keyword";

	/** 리퀘스트 파라미터 키 : 페이지 */
	private static final String PARAM_PAGE = "page";
	
	/** 리퀘스트 파라미터 키 : 유저 테이블 ID */
	private static final String PARAM_USER_ID = "user_id";

	/** API 입구 판단용 파라미터 키 문자열  */
	private static final String PARAM_IS_MAIN_PAGE = "is_main_page";

	/**
	 * 애니메이션/드라마/영화 검색 API
	 * 
	 * @param keyword 검색어
	 * @return ResponseEntity<List<String>> 검색어 리스트
	 */
	@GetMapping(value = "/searchKeyword")
	public ResponseEntity<List<String>> searchKeyword(@RequestParam(PARAM_KEYWORD) String keyword) {
		boolean isAdult = session.getSessionBooleanValue(CommonConstants.ADULT_FLG);
		return ResponseEntity.ok(searchService.searchKeyword(keyword, isAdult));
	}

	/**
	 * 애니메이션/드라마/영화 검색 API
	 * 
	 * @param keyword 검색어
	 * @param userId 유저 테이블 ID
	 * @return ResponseEntity<SearchVideoResponseDto> 애니메이션/드라마/영화 검색 결과 응답 오브젝트
	 */
	@GetMapping(value = "/searchVideo")
	public ResponseEntity<SearchVideoResponseDto> searchVideo(
			@RequestParam(PARAM_KEYWORD) String keyword,
			@RequestParam(value = PARAM_USER_ID, required = false) Long userId
			) {
		boolean isAdult = session.getSessionBooleanValue(CommonConstants.ADULT_FLG);
		return ResponseEntity.ok(searchService.searchVideo(keyword, isAdult, userId));
	}

	/**
	 * 애니 정보 검색 API
	 * 
	 * @param keyword 검색어
	 * @param page 페이지
	 * @param userId 유저 테이블 ID
	 * @return ResponseEntity<TmdbSearchTvDto> 애니 정보 응답 오브젝트
	 */
	@GetMapping(value = "/searchAni")
	public ResponseEntity<SearchTvResponseDto> searchAni(
			@RequestParam(PARAM_KEYWORD) String keyword,
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_USER_ID, required = false) Long userId
			) {
		boolean isAdult = session.getSessionBooleanValue(CommonConstants.ADULT_FLG);
		return ResponseEntity.ok(searchService.searchAni(keyword, isAdult, page, userId));
	}

	/**
	 * 드라마 정보 검색 API
	 * 
	 * @param keyword 검색어
	 * @param page 페이지
	 * @param userId 유저 테이블 ID
	 * @return ResponseEntity<TmdbSearchTvDto> 드라마 정보 응답 오브젝트
	 */
	@GetMapping(value = "/searchDrama")
	public ResponseEntity<SearchTvResponseDto> searchDrama(
			@RequestParam(PARAM_KEYWORD) String keyword, 
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_USER_ID, required = false) Long userId
			) {
		boolean isAdult = session.getSessionBooleanValue(CommonConstants.ADULT_FLG);
		return ResponseEntity.ok(searchService.searchDrama(keyword, isAdult, page, userId));
	}

	/**
	 * 영화 정보 검색 API
	 * 
	 * @param keyword 검색어
	 * @param page 페이지
	 * @param userId 유저 테이블 ID
	 * @return ResponseEntity<TmdbSearchMovieDto> 영화 정보 응답 오브젝트
	 */
	@GetMapping(value = "/searchMovie")
	public ResponseEntity<SearchMovieResponseDto> searchMovie(
			@RequestParam(PARAM_KEYWORD) String keyword, 
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_USER_ID, required = false) Long userId
			) {
		boolean isAdult = session.getSessionBooleanValue(CommonConstants.ADULT_FLG);
		return ResponseEntity.ok(searchService.searchMovie(keyword, isAdult, page, userId));
	}

	/**
	 * 만화 정보 검색 API
	 * 
	 * @param keyword 검색어
	 * @param page 페이지
	 * @param isMainPage API 입구 판단용 파라미터 (true: 메인화면, false: 전체보기화면)
	 * @param userId 유저 테이블 ID
	 * @return ResponseEntity<SearchComicsResponseDto> 만화 정보 응답 오브젝트
	 */
	@GetMapping(value = "/searchComics")
	public ResponseEntity<SearchComicsResponseDto> searchComics(
			@RequestParam(PARAM_KEYWORD) String keyword, 
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_IS_MAIN_PAGE) boolean isMainPage,
			@RequestParam(value = PARAM_USER_ID, required = false) Long userId
			) {
		boolean isAdult = session.getSessionBooleanValue(CommonConstants.ADULT_FLG);
		return ResponseEntity.ok(searchService.searchComics(keyword, isAdult, page, isMainPage, userId));
	}

}
