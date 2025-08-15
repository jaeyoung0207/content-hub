package com.cjy.contenthub.search.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.common.api.dto.aniist.AniListPageInfoDto;
import com.cjy.contenthub.common.api.dto.aniist.AniListResponseDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchMovieDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchMovieResultsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchMultiDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchMultiResultsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchTvDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchTvResultsDto;
import com.cjy.contenthub.common.client.DeepLApiClient;
import com.cjy.contenthub.common.client.TmdbApiGenreClient;
import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMediaTypeEnum;
import com.cjy.contenthub.common.constants.CommonEnum.TmdbGenreEnum;
import com.cjy.contenthub.common.util.GraphqlUtil;
import com.cjy.contenthub.common.util.SessionUtil;
import com.cjy.contenthub.search.controller.dto.SearchComicsResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;
import com.cjy.contenthub.search.helper.SearchHelper;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 검색 컨텐츠 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

	/** TMDB API 통신용 WebClient 클래스 */
	@Qualifier("tmdbWebClient")
	private final WebClient tmdbWebClient;

	/** AniList API 통신용 WebClient 클래스 */
	@Qualifier("anilistWebClient")
	private final WebClient anilistWebClient;

	/** DeepL API 통신용 WebClient 클래스 */
	@Qualifier("deeplWebClient")
	private final WebClient deeplWebClient;

	/** TMDB API 장르 WebClient 클래스 */
	private final TmdbApiGenreClient tmdbApiGenreClient;

	/** DeepL API 번역 WebClient 클래스 */
	private final DeepLApiClient deeplApiGenreClient;

	/** 세션 유틸 클래스 */
	private final SessionUtil session;
	
	/** 검색 헬퍼 클래스 */
	private final SearchHelper helper;

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

	/** 리퀘스트 파라미터 키 : 페이지당 표시 건수 */
	private static final String PARAM_PER_PAGE = "perPage";

	/** 리퀘스트 파라미터 키 : 검색 */
	private static final String PARAM_SEARCH = "search";

	/** 리퀘스트 파라미터 키 : 성인물 포함 여부 */
	private static final String PARAM_INCLUDE_ADULT = "include_adult";

	/** 리퀘스트 파라미터 키 : 언어 */
	private static final String PARAM_LANGUAGE = "language";

	/** 리퀘스트 파라미터 키 : 성인물 포함 여부 */
	private static final String PARAM_IS_ADULT = "isAdult";

	/** API 입구 판단용 파라미터 키 문자열  */
	private static final String PARAM_IS_MAIN_PAGE = "isMainPage";

	/** 언어 : 한국어 */
	private static final String LANGUAGE_KOREAN = "ko-KR";

	/**
	 * 어플리케이션 기동시 ApplicationReadyEvent를 이용하여, 
	 * 모든 빈 초기화 + 어플리케이션 준비 완료 후에 캐시화 로직을 실행
	 * (@Cacheable 가 AOP 프록시로 동작하므로, 이 시점에서는 사용 가능)
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void initializeTMdbApiGenreInfo() {
		// TMDB API 애니/영화 장르 정보 캐시화 
		tmdbApiGenreClient.getTvGenres();
		tmdbApiGenreClient.getMovieGenres();
	}

	/**
	 * TMDB API를 사용하여 TV 장르 정보 조회
	 * 
	 * @return TV 장르 정보 Map
	 */
	private Mono<Map<String, Integer>> getTvGenres() {
		return Mono.just(tmdbApiGenreClient.getTvGenres());
	}

	/**
	 * TMDB API를 사용하여 영화 장르 정보 조회
	 * 
	 * @return 영화 장르 정보 Map
	 */
	private Mono<Map<String, Integer>> getMovieGenres() {
		return Mono.just(tmdbApiGenreClient.getMovieGenres());
	}

	/**
	 * DeepL API를 사용하여 대상 문자열을 설정언어로 변역
	 * 
	 * @param keyword 번역할 문자열
	 * @return 번역된 문자열
	 */
	private Mono<String> getTranslationText(String keyword) {
		return Mono.just(deeplApiGenreClient.translateText(
				keyword, CommonConstants.API_LANGUAGE_JAPANESE, CommonConstants.API_LANGUAGE_KOREAN));
	}

	/**
	 * 애니메이션/드라마/영화 검색 API
	 * 
	 * @param keyword 검색어
	 * @return ResponseEntity<List<String>> 검색어 리스트
	 */
	@GetMapping(value = "/searchKeyword")
	public ResponseEntity<List<String>> searchKeyword(@RequestParam(PARAM_QUERY) String keyword) {

		Mono<Map<String, Integer>> tvGenreMapMono = getTvGenres();
		Mono<Map<String, Integer>> movieGenreMapMono = getMovieGenres();

		// TV 장르와 영화 장르를 병렬로 묶어서 처리
		return Mono.zip(tvGenreMapMono, movieGenreMapMono).flatMap(tuple -> {
			// 장르 맵 병합
			Map<String, Integer> genreMap = tuple.getT1();
			Map<String, Integer> movieGenreMap = tuple.getT2();
			genreMap.putAll(movieGenreMap);

			// 성인물 포함 플래그
			boolean isAdult = session.getSessionBooleanValue(CommonConstants.ADULT_FLG);
			// TMDB Multi API 실행 
			return tmdbWebClient.get()
					.uri(builder -> builder
							.path(multiSearchPath)
							.queryParam(PARAM_QUERY, keyword)
							.queryParam(PARAM_INCLUDE_ADULT, isAdult)
							.queryParam(PARAM_LANGUAGE, LANGUAGE_KOREAN)
							.build())
					.retrieve()
					.bodyToMono(TmdbSearchMultiDto.class)
					.map(response -> {
						// 검색 결과
						List<TmdbSearchMultiResultsDto> resultList = response.getResults();
						// 검색 결과가 없는 경우, 빈 리스트 반환
						if (CollectionUtils.isEmpty(resultList)) {
							List<String> emptyList = new ArrayList<>();
							return ResponseEntity.ok(emptyList);
						}
						// 검색 결과에서 TV, 영화 정보만 추출
						List<String> nameList = resultList.stream()
								.filter(e -> !StringUtils.equals(e.getMediaType(), CommonMediaTypeEnum.TMDB_MEDIA_TYPE_PERSON.getMediaTypeValue())
										&& !CollectionUtils.isEmpty(e.getGenreIds()) 
										&& ((StringUtils.equals(e.getMediaType(), CommonMediaTypeEnum.TMDB_MEDIA_TYPE_TV.getMediaTypeValue())
												&& (e.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_ANI.getGenreEnglish()))
														|| e.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_DRAMA.getGenreEnglish()))
														|| e.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_SOAP.getGenreEnglish()))
														))
												|| StringUtils.equals(e.getMediaType(), CommonMediaTypeEnum.TMDB_MEDIA_TYPE_MOVIE.getMediaTypeValue())
												)) // 인물 미디어 타입 제외 AND 장르 존재 AND ((TV 미디어 타입 AND (장르가 애니 or 드라마 or 연속극)) OR 영화 미디어 타입)
								.map(e -> StringUtils.defaultIfEmpty(e.getName(), e.getTitle())) // 둘 중 하나만 들어가 있으므로, 한쪽이 empty면 다른 한쪽을 설정
								.filter(StringUtils::isNotEmpty) // 빈 요소 제거
								.distinct() // 중복 제거
								.sorted() // 문자열 순으로 정렬
								.toList();
						// 키워드로 시작하는 검색결과가 먼저 오도록 정렬
						List<String> sortedList = helper.sortKeywordList(nameList, keyword); 
						// 표시개수 제한 후 결과값 반환
						return ResponseEntity.ok(sortedList.stream().limit(autoCompleteCount).toList());
					});
		}).block();
	}

	/**
	 * 애니메이션/드라마/영화 검색 API
	 * 
	 * @param keyword 검색어
	 * @return ResponseEntity<SearchVideoResponseDto> 애니메이션/드라마/영화 검색 결과 응답 오브젝트
	 */
	@GetMapping(value = "/searchVideo")
	public ResponseEntity<SearchVideoResponseDto> searchVideo(@RequestParam(PARAM_QUERY) String keyword) {

		Mono<Map<String, Integer>> tvGenreMapMono = getTvGenres();
		Mono<Map<String, Integer>> movieGenreMapMono = getMovieGenres();

		return Mono.zip(tvGenreMapMono, movieGenreMapMono).flatMap(genreTuple -> {
			Map<String, Integer> tvGenreMap = genreTuple.getT1();
			Map<String, Integer> movieGenreMap = genreTuple.getT2();

			// 성인물 포함 플래그
			boolean isAdult = session.getSessionBooleanValue(CommonConstants.ADULT_FLG);

			// 애니, 드라마 정보 취득
			Mono<SearchTvResponseDto> tvResponseMono = tmdbWebClient.get()
					.uri(builder -> builder
							.path(tvSearchPath)
							.queryParam(PARAM_QUERY, keyword)
							.queryParam(PARAM_INCLUDE_ADULT, isAdult)
							.queryParam(PARAM_LANGUAGE, LANGUAGE_KOREAN)
							.queryParam(PARAM_PAGE, 1) // 첫번째 페이지 고정(기본값)
							.build())
					.retrieve()
					.bodyToMono(TmdbSearchTvDto.class)
					.map(response -> {
						// 결과가 없는 경우, 빈 응답 반환
						if (response == null || CollectionUtils.isEmpty(response.getResults())) {
							return new SearchTvResponseDto();
						}

						// 애니/드라마 리스트 분배
						List<TmdbSearchTvResultsDto> aniList = helper.getAniList(response.getResults(), tvGenreMap);
						List<TmdbSearchTvResultsDto> dramaList = helper.getDramaList(response.getResults(), tvGenreMap);

						// 애니/드라마/영화 리스트 저장
						SearchTvResponseDto tvResponse = new SearchTvResponseDto();
						int totalResults = response.getTotalResults();
						// 애니 정보
						tvResponse.setAniResults(aniList);
						// 드라마 정보
						tvResponse.setDramaResults(dramaList);
						// 현재 페이지
						tvResponse.setPage(response.getPage());
						// 총 페이지 수
						tvResponse.setTotalPages(response.getTotalPages());
						// 총 건수
						tvResponse.setTotalResults(totalResults);

						return tvResponse;
					});

			// 영화 정보 취득
			Mono<TmdbSearchMovieDto> movieResponseMono = tmdbWebClient.get()
					.uri(builder -> builder
							.path(movieSearchPath)
							.queryParam(PARAM_QUERY, keyword)
							.queryParam(PARAM_INCLUDE_ADULT, isAdult)
							.queryParam(PARAM_LANGUAGE, LANGUAGE_KOREAN)
							.queryParam(PARAM_PAGE, 1) // 첫번째 페이지 고정(기본값)
							.build())
					.retrieve()
					.bodyToMono(TmdbSearchMovieDto.class);

			return Mono.zip(tvResponseMono, movieResponseMono).map(dtoTuple -> {
				// TV 응답 DTO 
				SearchTvResponseDto tvResponse = dtoTuple.getT1();
				// 영화 응답 DTO
				TmdbSearchMovieDto movieResponse = dtoTuple.getT2();
				// 애니 검색 결과 리스트
				List<TmdbSearchTvResultsDto> aniResultList = Optional.ofNullable(tvResponse.getAniResults()).orElse(new ArrayList<>());
				// 드라마 검색 결과 리스트
				List<TmdbSearchTvResultsDto> dramaResultList = Optional.ofNullable(tvResponse.getDramaResults()).orElse(new ArrayList<>());
				// 영화 검색 결과 리스트
				List<TmdbSearchMovieResultsDto> movieResultList = Optional.ofNullable(movieResponse.getResults()).orElse(new ArrayList<>());

				// 영화 정보 리스트 -> 애니 정보 리스트와 결합
				List<TmdbSearchMovieResultsDto> filteredMovieList = new ArrayList<>();
				// 영화 정보에서 애니메이션 정보 추출 
				aniResultList.addAll(helper.getAniMovieList(movieResultList, movieGenreMap));
				// 영화 정보 필터링
				filteredMovieList.addAll(helper.getMovieList(movieResultList, movieGenreMap));

                // 응답 오브젝트 설정				
				SearchVideoResponseDto videoResponse = helper.setVideoResponse(
						aniResultList, dramaResultList,
						filteredMovieList, tvResponse.getPage(), tvResponse.getTotalPages(),
						movieResponse.getPage(), movieResponse.getTotalPages());

				return ResponseEntity.ok(videoResponse);
			});
		}).block();
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

		int currentPage = Optional.ofNullable(page).orElse(1);

		Mono<Map<String, Integer>> tvGenreMapMono = getTvGenres();
		Mono<Map<String, Integer>> movieGenreMapMono = getMovieGenres();

		return Mono.zip(tvGenreMapMono, movieGenreMapMono).flatMap(genreTuple -> {
			Map<String, Integer> aniGenreMap = genreTuple.getT1();
			Map<String, Integer> movieGenreMap = genreTuple.getT2();

			// 성인물 포함 플래그
			boolean isAdult = session.getSessionBooleanValue(CommonConstants.ADULT_FLG);

			// TV 애니 정보 조회
			Mono<TmdbSearchTvDto> tvResponseMono = tmdbWebClient.get()
					.uri(builder -> builder
							.path(tvSearchPath)
							.queryParam(PARAM_QUERY, keyword)
							.queryParam(PARAM_INCLUDE_ADULT, isAdult)
							.queryParam(PARAM_LANGUAGE, LANGUAGE_KOREAN)
							.queryParam(PARAM_PAGE, currentPage)
							.build())
					.retrieve()
					.bodyToMono(TmdbSearchTvDto.class)
					.map(response -> 
						// 애니 리스트 저장
						TmdbSearchTvDto.builder()
								.results(helper.getAniList(response.getResults(), aniGenreMap))
								.page(response.getPage())
								.totalPages(response.getTotalPages())
								.totalResults(response.getTotalResults())
								.build()
					);

			// 영화 정보 취득
			Mono<TmdbSearchMovieDto> movieResponseMono = tmdbWebClient.get()
					.uri(builder -> builder
							.path(movieSearchPath)
							.queryParam(PARAM_QUERY, keyword)
							.queryParam(PARAM_INCLUDE_ADULT, isAdult)
							.queryParam(PARAM_LANGUAGE, LANGUAGE_KOREAN)
							.queryParam(PARAM_PAGE, currentPage)
							.build())
					.retrieve()
					.bodyToMono(TmdbSearchMovieDto.class);

			// 비동기로 API실행(webClient처리)후 결과를 병렬처리
			return Mono.zip(tvResponseMono, movieResponseMono).map(tuple -> {
				// TV 응답 DTO
				TmdbSearchTvDto tvResponse = tuple.getT1();
				// 영화 응답 DTO
				TmdbSearchMovieDto movieResponse = tuple.getT2();
				// 애니 검색 결과 리스트
				List<TmdbSearchTvResultsDto> aniResultList = Optional.ofNullable(tvResponse.getResults()).orElse(new ArrayList<>());
				// 영화 검색 결과 리스트
				List<TmdbSearchMovieResultsDto> movieResultList = Optional.ofNullable(movieResponse.getResults()).orElse(new ArrayList<>());

				// 영화 정보 리스트 -> 애니 정보 리스트와 결합
				aniResultList.addAll(helper.getAniMovieList(movieResultList, movieGenreMap));

				// 반환값 설정
				TmdbSearchTvDto aniResponse = TmdbSearchTvDto.builder()
						.results(aniResultList)
						.page(currentPage)
						.build();

				// 애니 응답 오브젝트 반환
				return ResponseEntity.ok(aniResponse);
			});
		}).block();
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

		// 드라마 장르 정보 취득
		return getTvGenres().flatMap(tvGenreMap -> 
		tmdbWebClient.get()
		.uri(builder -> builder
				.path(tvSearchPath)
				.queryParam(PARAM_QUERY, keyword)
				.queryParam(PARAM_INCLUDE_ADULT, session.getSessionBooleanValue(CommonConstants.ADULT_FLG))
				.queryParam(PARAM_LANGUAGE, LANGUAGE_KOREAN)
				.queryParam(PARAM_PAGE, Optional.ofNullable(page).orElse(1))
				.build())
		.retrieve()
		.bodyToMono(TmdbSearchTvDto.class)
		.map(response -> {
			// 결과값 설정
			TmdbSearchTvDto dramaResponse = TmdbSearchTvDto.builder()
					.results(helper.getDramaList(response.getResults(), tvGenreMap))
					.page(response.getPage())
					.totalPages(response.getTotalPages())
					.totalResults(response.getTotalResults())
					.build();

			// 드라마 응답 오브젝트 반환
			return ResponseEntity.ok(dramaResponse);
			
		})).block();
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

		// 영화 장르 정보 취득
		return getMovieGenres().flatMap(movieGenreMap -> 
		// 영화 정보 조회
		tmdbWebClient.get()
		.uri(builder -> builder
				.path(movieSearchPath)
				.queryParam(PARAM_QUERY, keyword)
				.queryParam(PARAM_INCLUDE_ADULT, session.getSessionBooleanValue(CommonConstants.ADULT_FLG))
				.queryParam(PARAM_LANGUAGE, LANGUAGE_KOREAN)
				.queryParam(PARAM_PAGE, Optional.ofNullable(page).orElse(1))
				.build())
		.retrieve()
		.bodyToMono(TmdbSearchMovieDto.class)
		.map(response -> {
			// 결과값 설정
			TmdbSearchMovieDto movieResponse = TmdbSearchMovieDto.builder()
					.results(helper.getMovieList(response.getResults(), movieGenreMap))
					.page(response.getPage())
					.totalPages(response.getTotalPages())
					.totalResults(response.getTotalResults())
					.build();

			// 영화 응답 오브젝트 반환
			return ResponseEntity.ok(movieResponse);
		})).block();
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

		// API를 어디서 불렀는지에 따라 표시 건수를 다르게 설정
		int perPage = isMainPage ? anilistPerMainPage : anilistPerMorePage;
		// 성인물 포함 플래그
		boolean isAdult = session.getSessionBooleanValue(CommonConstants.ADULT_FLG);

		// 한글 검색어 -> 일본어로 번역후(DeepL API), AniList API 조회
		return getTranslationText(keyword).flatMap(jaKeyword -> {
			try {
				// graphql 쿼리 파일 불러오기
				String query = GraphqlUtil.loadQuery("comicsList.graphql");
				// 리퀘스트 파라미터 작성
				Map<String, Object> variables = new HashMap<>(Map.of(
						PARAM_PAGE, Optional.ofNullable(page).orElse(1),
						PARAM_PER_PAGE, perPage,
						PARAM_SEARCH, jaKeyword
						));
				// 성인물 플래그가 false인 경우, 파라미터 추가
				if (!isAdult) {
					variables.put(PARAM_IS_ADULT, isAdult);
				}
				// graphql 쿼리에 리퀘스트 파라미터 적용
				String requestBody = GraphqlUtil.buildRequestBody(query, variables);
				// AniList API 실행
				return anilistWebClient.post()
						.bodyValue(requestBody)
						.retrieve()
						.bodyToMono(AniListResponseDto.class)
						.map(response -> {
							// 만화 정보가 없는 경우 빈 응답 반환
							if (ObjectUtils.isEmpty(response.getData())
									|| ObjectUtils.isEmpty(response.getData().getPage())
									|| CollectionUtils.isEmpty(response.getData().getPage().getMedia())
									|| ObjectUtils.isEmpty(response.getData().getPage().getPageInfo())) {
								return ResponseEntity.ok(new SearchComicsResponseDto());
							}							
							// 페이지 정보 설정
							AniListPageInfoDto comicsPageDto = response.getData().getPage().getPageInfo();
							int currentPage = comicsPageDto.getCurrentPage();
							int lastPage = comicsPageDto.getLastPage();
							// 응답 데이터 재분배
							SearchComicsResponseDto comicsResponse = SearchComicsResponseDto.builder()
									.page(currentPage)
									.totalPages(lastPage)
									.isComicsViewMore(currentPage < lastPage)
									.comicsResults(helper.setComicsResponse(response.getData().getPage().getMedia()))
									.build();

							// 만화 응답 오브젝트 반환
							return ResponseEntity.ok(comicsResponse);
						});
			} catch (IOException e) {
				return Mono.error(e);
			}
		}).block();
	}

}
