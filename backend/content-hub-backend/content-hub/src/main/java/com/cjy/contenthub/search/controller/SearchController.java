package com.cjy.contenthub.search.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.common.annotation.ApiController;
import com.cjy.contenthub.common.annotation.MaskingTarget;
import com.cjy.contenthub.common.util.SessionUtil;
import com.cjy.contenthub.core.constants.DomainConstants;
import com.cjy.contenthub.search.controller.dto.SearchComicsResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;
import com.cjy.contenthub.search.mapper.SearchMapper;
import com.cjy.contenthub.search.service.SearchNoCacheService;
import com.cjy.contenthub.search.service.SearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 검색 콘텐츠 API 컨트롤러 클래스
 */
@Tag(name = "search-api", description = "Search APIs")
@ApiController
@RequestMapping("/search")
@RequiredArgsConstructor
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
	@Value("${tmdb.url.tv-search-path}")
	private String tvSearchPath;

	/** TMDB API 영화 검색 API 패스 */
	@Value("${tmdb.url.movie-search-path}")
	private String movieSearchPath;

	/** TMDB API 멀티(TV, 영화, 인물) 검색 API 패스 */
	@Value("${tmdb.url.multi-search-path}")
	private String multiSearchPath;

	/** TMDB API 자동완성 표시 개수 */
	@Value("${tmdb.custom.auto-complete-count}")
	private int autoCompleteCount;

	/** AniList API 메인화면 작품 표시 개수 */
	@Value("${anilist.custom.per-main-page}")
	private int anilistPerMainPage;

	/** AniList API 전체보기화면 작품 표시 개수 */
	@Value("${anilist.custom.per-more-page}")
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
	@Operation(summary = "검색어 리스트 조회")
	@GetMapping(value = "/searchKeyword")
	public CompletableFuture<ResponseEntity<List<String>>> searchKeyword(@RequestParam(PARAM_KEYWORD) String keyword) {
		boolean isAdult = session.getSessionBooleanValue(DomainConstants.ADULT_FLG);
		return searchService.searchKeyword(keyword, isAdult)
				.thenApply(ResponseEntity::ok);
	}

	/**
	 * 비디오 검색 API
	 * 
	 * @param keyword 검색어
	 * @param userId 유저 테이블 ID
	 * @return ResponseEntity<SearchVideoResponseDto> 비디오 검색 결과 응답 오브젝트
	 */
	@Operation(summary = "비디오 검색")
	@GetMapping(value = "/searchVideo")
	public CompletableFuture<ResponseEntity<SearchVideoResponseDto>> searchVideo(
			@RequestParam(PARAM_KEYWORD) String keyword,
			@RequestParam(value = PARAM_USER_ID, required = false) Long userId
			) {
		boolean isAdult = session.getSessionBooleanValue(DomainConstants.ADULT_FLG);
		CompletableFuture<SearchVideoResponseDto> response = searchService.searchVideo(keyword, isAdult)
				.thenApplyAsync(cachedResponse -> {
					// 캐시된 응답 객체를 깊은 복사하여 새로운 객체 생성(캐시된 객체를 직접 수정하지 않고 새로운 객체를 사용)
					SearchVideoResponseDto newResponse = searchMapper.deepCopyForVideoResponse(cachedResponse);

					// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
					if (userId != null) {
						searchNoCacheService.setWishlistFromVideoResponse(newResponse, userId);
					}
					return newResponse;
				});
		return response.thenApply(ResponseEntity::ok);
	}

	/**
	 * 애니 정보 검색 API
	 * 
	 * @param keyword 검색어
	 * @param page 페이지
	 * @param userId 유저 테이블 ID
	 * @return ResponseEntity<TmdbSearchTvDto> 애니 정보 응답 오브젝트
	 */
	@Operation(summary = "애니메이션 검색")
	@GetMapping(value = "/searchAni")
	public CompletableFuture<ResponseEntity<SearchTvResponseDto>> searchAni(
			@RequestParam(PARAM_KEYWORD) String keyword,
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_USER_ID, required = false) @MaskingTarget Long userId
			) {
		boolean isAdult = session.getSessionBooleanValue(DomainConstants.ADULT_FLG);
		CompletableFuture<SearchTvResponseDto> response = searchService.searchAni(keyword, isAdult, page)
				.thenApplyAsync(cachedResponse -> {
					// 캐시된 응답 객체를 깊은 복사하여 새로운 객체 생성(캐시된 객체를 직접 수정하지 않고 새로운 객체를 사용)
					SearchTvResponseDto newResponse = searchMapper.deepCopyForTvResponse(cachedResponse);

					// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
					if (userId != null) {
						searchNoCacheService.setWishlistFromAniResponse(newResponse, userId);
					}
					return newResponse;
				});
		return response.thenApply(ResponseEntity::ok);
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
	@Operation(summary = "애니메이션 제외한 TV 시리즈 검색")
	@GetMapping(value = "/searchTvExceptAni")
	public CompletableFuture<ResponseEntity<SearchTvResponseDto>> searchTvExceptAni(
			@RequestParam(PARAM_KEYWORD) String keyword,
			@RequestParam(PARAM_CONTENT_MEDIA_TYPE) String contentMediaType,
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_USER_ID, required = false) @MaskingTarget Long userId
			) {
		boolean isAdult = session.getSessionBooleanValue(DomainConstants.ADULT_FLG);
		CompletableFuture<SearchTvResponseDto> response = searchService.searchTvExceptAni(keyword, isAdult, contentMediaType, page)
				.thenApplyAsync(cachedResponse -> {
					// 캐시된 응답 객체를 깊은 복사하여 새로운 객체 생성(캐시된 객체를 직접 수정하지 않고 새로운 객체를 사용)
					SearchTvResponseDto newResponse = searchMapper.deepCopyForTvResponse(cachedResponse);
					
					// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
					if (userId != null) {
						searchNoCacheService.setWishlistFromTvExceptAniResponse(newResponse, userId, contentMediaType);
					}
					return newResponse;
				});
		return response.thenApply(ResponseEntity::ok);
	}

	/**
	 * 영화 정보 검색 API
	 * 
	 * @param keyword 검색어
	 * @param page 페이지
	 * @param userId 유저 테이블 ID
	 * @return ResponseEntity<TmdbSearchMovieDto> 영화 정보 응답 오브젝트
	 */
	@Operation(summary = "영화 정보 검색")
	@GetMapping(value = "/searchMovie")
	public CompletableFuture<ResponseEntity<SearchMovieResponseDto>> searchMovie(
			@RequestParam(PARAM_KEYWORD) String keyword, 
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_USER_ID, required = false) @MaskingTarget Long userId
			) {
		boolean isAdult = session.getSessionBooleanValue(DomainConstants.ADULT_FLG);
		CompletableFuture<SearchMovieResponseDto> response = searchService.searchMovie(keyword, isAdult, page)
				.thenApplyAsync(cachedResponse -> {
					// 깊은 복사 수행
					SearchMovieResponseDto newResponse = searchMapper.deepCopyForMovieResponse(cachedResponse);

					// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
					if (userId != null) {
						searchNoCacheService.setWishlistFromMovieResponse(newResponse, userId);
					}
					return newResponse;
				});
		return response.thenApply(ResponseEntity::ok);
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
	@Operation(summary = "만화 정보 검색")
	@GetMapping(value = "/searchComics")
	public CompletableFuture<ResponseEntity<SearchComicsResponseDto>> searchComics(
			@RequestParam(PARAM_KEYWORD) String keyword, 
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_IS_MAIN_PAGE) boolean isMainPage,
			@RequestParam(value = PARAM_USER_ID, required = false) @MaskingTarget Long userId
			) {
		boolean isAdult = session.getSessionBooleanValue(DomainConstants.ADULT_FLG);
		CompletableFuture<SearchComicsResponseDto> response = 
				searchService.searchComics(keyword, isAdult, page, isMainPage).thenApplyAsync(cachedResponse -> {
					// 캐시된 응답 객체를 깊은 복사하여 새로운 객체 생성(캐시된 객체를 직접 수정하지 않고 새로운 객체를 사용)
					SearchComicsResponseDto newResponse = searchMapper.deepCopyForComicsResponse(cachedResponse);

					// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
					if (userId != null) {
						searchNoCacheService.setWishlistFromComicsResponse(newResponse, userId);
					}
					return newResponse;
				});
		return response.thenApply(ResponseEntity::ok);
	}

}
