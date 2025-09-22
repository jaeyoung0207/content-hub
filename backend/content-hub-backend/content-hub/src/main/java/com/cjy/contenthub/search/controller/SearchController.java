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
import com.cjy.contenthub.search.mapper.SearchMapper;
import com.cjy.contenthub.search.service.SearchNoCacheService;
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
	
	/** 검색 서비스 클래스(캐시 미사용) */
	private final SearchNoCacheService searchNoCacheService;
	
	/** 검색 매퍼 클래스 */
	private final SearchMapper searchMapper;
	
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
	
	/** 리퀘스트 파라미터 키 : 컨텐츠 미디어 타입 */
	private static final String PARAM_CONTENT_MEDIA_TYPE = "content_media_type";
	
	/** 리퀘스트 파라미터 키 : 페이지 */
	private static final String PARAM_PAGE = "page";
	
	/** 리퀘스트 파라미터 키 : 유저 테이블 ID */
	private static final String PARAM_USER_ID = "user_id";

	/** API 입구 판단용 파라미터 키 문자열  */
	private static final String PARAM_IS_MAIN_PAGE = "is_main_page";

	/**
	 * 검색어 리스트 조회 API
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
		SearchVideoResponseDto cachedResponse = searchService.searchVideo(keyword, isAdult, userId);
		
		// 캐시된 응답 객체를 깊은 복사하여 새로운 객체 생성(캐시된 객체를 직접 수정하지 않고 새로운 객체를 사용)
		SearchVideoResponseDto newResponse = searchMapper.deepCopyForVideoResponse(cachedResponse);
		
		// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
		if (userId != null) {
			searchNoCacheService.setWishlistFromVideoResponse(newResponse, userId);
		}
		return ResponseEntity.ok(newResponse);
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
		SearchTvResponseDto cachedResponse = searchService.searchAni(keyword, isAdult, page, userId);
		
		// 캐시된 응답 객체를 깊은 복사하여 새로운 객체 생성(캐시된 객체를 직접 수정하지 않고 새로운 객체를 사용)
		SearchTvResponseDto newResponse = searchMapper.deepCopyForTvResponse(cachedResponse);
		
		// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
		if (userId != null) {
			searchNoCacheService.setWishlistFromAniResponse(newResponse, userId);
		}
		return ResponseEntity.ok(newResponse);
	}

	/**
	 * 애니 제외한 TV 시리즈 검색 API
	 * 
	 * @param keyword 검색어
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param page 페이지
	 * @param userId 유저 테이블 ID
	 * @return ResponseEntity<TmdbSearchTvDto> 애니 제외한 TV 시리즈 정보 응답 오브젝트
	 */
	@GetMapping(value = "/searchTvExceptAni")
	public ResponseEntity<SearchTvResponseDto> searchTvExceptAni(
			@RequestParam(PARAM_KEYWORD) String keyword,
			@RequestParam(PARAM_CONTENT_MEDIA_TYPE) String contentMediaType,
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_USER_ID, required = false) Long userId
			) {
		boolean isAdult = session.getSessionBooleanValue(CommonConstants.ADULT_FLG);
		SearchTvResponseDto cachedResponse = searchService.searchTvExceptAni(keyword, isAdult, contentMediaType, page, userId);
		
		// 캐시된 응답 객체를 깊은 복사하여 새로운 객체 생성(캐시된 객체를 직접 수정하지 않고 새로운 객체를 사용)
		SearchTvResponseDto newResponse = searchMapper.deepCopyForTvResponse(cachedResponse);
		
		// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
		if (userId != null) {
			searchNoCacheService.setWishlistFromTvExceptAniResponse(newResponse, userId, contentMediaType);
		}
		return ResponseEntity.ok(newResponse);
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
		SearchMovieResponseDto cachedResponse = searchService.searchMovie(keyword, isAdult, page, userId);
		
		// 캐시된 응답 객체를 깊은 복사하여 새로운 객체 생성(캐시된 객체를 직접 수정하지 않고 새로운 객체를 사용)
		SearchMovieResponseDto newResponse = searchMapper.deepCopyForMovieResponse(cachedResponse);
		
		// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
		if (userId != null) {
			searchNoCacheService.setWishlistFromMovieResponse(newResponse, userId);
		}
		return ResponseEntity.ok(newResponse);
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
		SearchComicsResponseDto cachedResponse = searchService.searchComics(keyword, isAdult, page, isMainPage, userId);
		
		// 캐시된 응답 객체를 깊은 복사하여 새로운 객체 생성(캐시된 객체를 직접 수정하지 않고 새로운 객체를 사용)
		SearchComicsResponseDto newResponse = searchMapper.deepCopyForComicsResponse(cachedResponse);
		
		// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
		if (userId != null) {
			searchNoCacheService.setWishlistFromComicsResponse(newResponse, userId);
		}
		return ResponseEntity.ok(newResponse);
	}

}
