package com.cjy.contenthub.search.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.common.api.dto.aniist.AniListPageInfoDto;
import com.cjy.contenthub.common.api.dto.aniist.AniListResponseDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchMovieDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchMovieResultsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchMultiDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchMultiResultsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchTvDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchTvResultsDto;
import com.cjy.contenthub.common.constants.AnilistParamConstants;
import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMediaTypeEnum;
import com.cjy.contenthub.common.constants.CommonEnum.TmdbGenreEnum;
import com.cjy.contenthub.common.constants.TmdbParamConstants;
import com.cjy.contenthub.common.util.ApiUtil;
import com.cjy.contenthub.common.util.BusinessUtil;
import com.cjy.contenthub.common.util.GraphqlUtil;
import com.cjy.contenthub.search.controller.dto.SearchComicsResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchComicsResultDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;
import com.cjy.contenthub.search.helper.SearchHelper;
import com.cjy.contenthub.search.mapper.SearchMapper;
import com.cjy.contenthub.wishlist.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 검색 콘텐츠 API 컨트롤러 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

	/** TMDB API 통신용 WebClient 클래스 */
	@Qualifier("tmdbWebClient")
	private final WebClient tmdbWebClient;

	/** AniList API 통신용 WebClient 클래스 */
	@Qualifier("anilistWebClient")
	private final WebClient anilistWebClient;

	/** 공통 API 유틸 클래스 */
	private final ApiUtil apiUtil;

	/** 검색 헬퍼 클래스 */
	private final SearchHelper helper;

	/** 검색 매퍼 클래스 */
	private final SearchMapper mapper;

	/** wishlist 레포지토리 */
	private final WishlistRepository wishlistRepository;

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

	/** TMDB API 재시도 횟수 */
	@Value("${tmdb.custom.retryCount}")
	private int tmdbRetryCount;

	/** AniList API 메인화면 작품 표시 개수 */
	@Value("${anilist.custom.perMainPage}")
	private int anilistPerMainPage;

	/** AniList API 전체보기화면 작품 표시 개수 */
	@Value("${anilist.custom.perMorePage}")
	private int anilistPerMorePage;

	/**
	 * 검색어 리스트 조회
	 * 
	 * @param keyword 검색어
	 * @return 검색어 리스트
	 */
	@Override
	@Cacheable(value = "searchKeyword", key = "#keyword + '_' + #isAdult", unless = "#result == null")
	public List<String> searchKeyword(String keyword, boolean isAdult) {

		Mono<Map<String, Integer>> tvGenreMapMono = apiUtil.getTvGenres();
		Mono<Map<String, Integer>> movieGenreMapMono = apiUtil.getMovieGenres();

		// TV 장르와 영화 장르를 병렬로 묶어서 처리
		return Mono.zip(tvGenreMapMono, movieGenreMapMono).flatMap(tuple -> {
			// 장르 맵 병합
			Map<String, Integer> genreMap = tuple.getT1();
			Map<String, Integer> movieGenreMap = tuple.getT2();
			genreMap.putAll(movieGenreMap);

			// TMDB Multi API 실행 
			return tmdbWebClient.get()
					.uri(builder -> builder
							.path(multiSearchPath)
							.queryParam(TmdbParamConstants.PARAM_QUERY, keyword)
							.queryParam(TmdbParamConstants.PARAM_INCLUDE_ADULT, isAdult)
							.queryParam(TmdbParamConstants.PARAM_LANGUAGE, TmdbParamConstants.LANGUAGE_KOREAN)
							.build())
					.retrieve()
					.bodyToMono(TmdbSearchMultiDto.class)
					.map(response -> {
						// 검색 결과
						List<TmdbSearchMultiResultsDto> resultList = response.getResults();
						// 검색 결과가 없는 경우, 빈 리스트 반환
						if (CollectionUtils.isEmpty(resultList)) {
							List<String> emptyList = Collections.emptyList();
							return emptyList;
						}
						// 검색 결과에서 TV, 영화 정보만 추출
						List<String> nameList = resultList.stream()
								.filter(e -> !StringUtils.equals(e.getMediaType(), CommonMediaTypeEnum.TMDB_MEDIA_TYPE_PERSON.getMediaTypeValue()) // 인물 제외
										&& !CollectionUtils.isEmpty(e.getGenreIds()) 
										&& (StringUtils.equals(e.getMediaType(), CommonMediaTypeEnum.TMDB_MEDIA_TYPE_TV.getMediaTypeValue())
												&& (e.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_ANI.getGenreEnglish())) // 애니 필터링
														|| (!e.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_DOCUMENTARY.getGenreEnglish()))
																&& !e.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_KIDS.getGenreEnglish()))
																&& !e.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_NEWS.getGenreEnglish()))
																&& !e.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_REALITY.getGenreEnglish()))
																&& !e.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_TALK.getGenreEnglish()))) // 드라마 필터링
														))
										|| StringUtils.equals(e.getMediaType(), CommonMediaTypeEnum.TMDB_MEDIA_TYPE_MOVIE.getMediaTypeValue()) // 영화 필터링
										)
								.map(e -> StringUtils.defaultIfEmpty(e.getName(), e.getTitle())) // 둘 중 하나만 들어가 있으므로, 한쪽이 empty면 다른 한쪽을 설정
								.filter(StringUtils::isNotEmpty) // 빈 요소 제거
								.distinct() // 중복 제거
								.sorted() // 문자열 순으로 정렬
								.toList();
						// 키워드로 시작하는 검색결과가 먼저 오도록 정렬
						List<String> sortedList = helper.sortKeywordList(nameList, keyword); 
						// 표시개수 제한 후 결과값 반환
						return sortedList.stream().limit(autoCompleteCount).toList();
					});
		}).block();
	}

	/**
	 * 애니메이션/드라마/영화 검색 API
	 * 
	 * @param keyword 검색어
	 * @return 애니메이션/드라마/영화 검색 결과 응답 오브젝트
	 */
	@Override
	@Cacheable(value = "searchVideo", key = "#keyword + '_' + #isAdult + '_' + #userId", unless = "#result == null")
	public SearchVideoResponseDto searchVideo(String keyword, boolean isAdult, Long userId) {

		Mono<Map<String, Integer>> tvGenreMapMono = apiUtil.getTvGenres();
		Mono<Map<String, Integer>> movieGenreMapMono = apiUtil.getMovieGenres();

		return Mono.zip(tvGenreMapMono, movieGenreMapMono).flatMap(genreTuple -> {
			Map<String, Integer> tvGenreMap = genreTuple.getT1();
			Map<String, Integer> movieGenreMap = genreTuple.getT2();

			// 애니, 드라마 정보 조회
			Mono<SearchTvResponseDto> tvResponseMono = Flux
					.range(CommonConstants.FIRST_PAGE_NO, tmdbRetryCount) // 한꺼번에 검색할 페이지 번호 생성
					.flatMap(
							// 설정한 페이지 수 만큼 TMDB API 호출
							page -> tmdbWebClient.get()
							.uri(helper.getSearchUri(tvSearchPath, keyword, isAdult, page))
							.retrieve()
							.bodyToMono(TmdbSearchTvDto.class)
							.map(response -> {
								// API 응답을 검색 결과 DTO 리스트로 변환 
								List<SearchTvResultsDto> tvResultsList = mapper.tvResultsListToTmdbTvResultsList(response.getResults());
								// 애니, 드라마 리스트 분리
								List<SearchTvResultsDto> aniList = helper.getAniList(tvResultsList, tvGenreMap);
								List<SearchTvResultsDto> dramaList = helper.getDramaList(tvResultsList, tvGenreMap);
								// 응답 오브젝트 생성
								return SearchTvResponseDto.builder()
										.aniResults(aniList)
										.dramaResults(dramaList)
										.page(response.getPage())
										.totalPages(response.getTotalPages())
										.totalResults(response.getTotalResults())
										.build();
							}), tmdbRetryCount) // 병렬로 동시에 호출할 최대 페이지 수
					.collectList() // 모든 페이지의 결과를 리스트로 모음
					.map(resultList -> {
						// 검색 결과가 없는 경우 빈 응답 반환
						if (CollectionUtils.isEmpty(resultList)) {
							return new SearchTvResponseDto();
						}
						// 결과 리스트를 모아서 하나의 응답 오브젝트로 반환
						SearchTvResponseDto tvResponse = SearchTvResponseDto.builder()
								.aniResults(new ArrayList<>())
								.dramaResults(new ArrayList<>())
								.page(0)
								.totalPages(0)
								.totalResults(0)
								.build();
						for (SearchTvResponseDto result : resultList) {
							tvResponse.getAniResults().addAll(result.getAniResults());
							tvResponse.getDramaResults().addAll(result.getDramaResults());
						}
						tvResponse.setPage(resultList.get(0).getPage());
						tvResponse.setTotalPages(resultList.get(0).getTotalPages());
						tvResponse.setTotalResults(resultList.get(0).getTotalResults());
						return tvResponse;
					})
					.defaultIfEmpty(new SearchTvResponseDto()); // 조회 결과가 없는 경우 빈 응답 오브젝트 반환

			// 영화 정보 조회
			Mono<SearchMovieResponseDto> movieResponseMono = Flux
					.range(CommonConstants.FIRST_PAGE_NO, tmdbRetryCount) // 한꺼번에 검색할 페이지 번호 생성
					.flatMap(
							// 설정한 페이지 수 만큼 TMDB API 호출
							page -> tmdbWebClient.get()
							.uri(helper.getSearchUri(movieSearchPath, keyword, isAdult, page))
							.retrieve()
							.bodyToMono(TmdbSearchMovieDto.class)
							.map(response -> {
								// API 응답을 검색 결과 DTO 리스트로 변환
								List<SearchMovieResultsDto> movieResultsList = mapper.movieResultsListToTmdbMovieResultsList(response.getResults());
								// 응답 오브젝트 생성
								return SearchMovieResponseDto.builder()
										.movieResults(movieResultsList)
										.page(response.getPage())
										.totalPages(response.getTotalPages())
										.totalResults(response.getTotalResults())
										.build();
							})
							, tmdbRetryCount) // 병렬로 동시에 호출할 최대 페이지 수
					.collectList() // 모든 페이지의 결과를 리스트로 모음
					.map(resultList -> {
						// 검색 결과가 없는 경우 빈 응답 반환
						if (CollectionUtils.isEmpty(resultList)) {
							return new SearchMovieResponseDto();
						}
						// 결과 리스트를 모아서 하나의 응답 오브젝트로 반환
						SearchMovieResponseDto movieResponse = SearchMovieResponseDto.builder()
								.movieResults(new ArrayList<>())
								.page(0)
								.totalPages(0)
								.totalResults(0)
								.build();
						for (SearchMovieResponseDto result : resultList) {
							movieResponse.getMovieResults().addAll(result.getMovieResults());
						}
						movieResponse.setPage(resultList.get(0).getPage());
						movieResponse.setTotalPages(resultList.get(0).getTotalPages());
						movieResponse.setTotalResults(resultList.get(0).getTotalResults());
						return movieResponse;
					})
					.defaultIfEmpty(new SearchMovieResponseDto()); // 조회 결과가 없는 경우 빈 응답 오브젝트 반환

			return Mono.zip(tvResponseMono, movieResponseMono).map(dtoTuple -> {
				// TV 응답 DTO 
				SearchTvResponseDto tvResponse = dtoTuple.getT1();
				// 영화 응답 DTO
				SearchMovieResponseDto movieResponse = dtoTuple.getT2();
				// 애니 검색 결과 리스트
				List<SearchTvResultsDto> aniResultList = Optional.ofNullable(tvResponse.getAniResults()).orElse(Collections.emptyList());
				// 드라마 검색 결과 리스트
				List<SearchTvResultsDto> dramaResultList = Optional.ofNullable(tvResponse.getDramaResults()).orElse(Collections.emptyList());
				// 영화 검색 결과 리스트
				List<SearchMovieResultsDto> movieResultList = Optional.ofNullable(movieResponse.getMovieResults()).orElse(Collections.emptyList());
				// 필터링된 영화 리스트
				List<SearchMovieResultsDto> filteredMovieList = new ArrayList<>();
				// 영화 정보에서 애니메이션 정보 추출 
				aniResultList.addAll(helper.getAniMovieList(movieResultList, movieGenreMap));
				// 영화 정보에서 애니영화 제외한 정보 추출
				filteredMovieList.addAll(helper.getMovieList(movieResultList, movieGenreMap));

				// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
				if (userId != null) {
					BusinessUtil.setWishlisted(
							aniResultList, 
							CommonMediaTypeEnum.MEDIA_TYPE_ANI.getMediaTypeCode(), 
							userId, 
							dto -> String.valueOf(dto.getId()),
							SearchTvResultsDto::setWishlisted, // (dto, wishlisted) -> dto.setWishlisted(wishlisted)
							wishlistRepository);
					BusinessUtil.setWishlisted(
							dramaResultList, 
							CommonMediaTypeEnum.MEDIA_TYPE_DRAMA.getMediaTypeCode(), 
							userId, 
							dto -> String.valueOf(dto.getId()),
							SearchTvResultsDto::setWishlisted,
							wishlistRepository);
					BusinessUtil.setWishlisted(
							filteredMovieList, 
							CommonMediaTypeEnum.MEDIA_TYPE_MOVIE.getMediaTypeCode(), 
							userId, 
							dto -> String.valueOf(dto.getId()),
							SearchMovieResultsDto::setWishlisted, 
							wishlistRepository);
				}

				// 응답 오브젝트 설정
				return helper.setVideoResponse(
						aniResultList, dramaResultList,
						filteredMovieList, tvResponse.getPage(), tvResponse.getTotalPages(),
						movieResponse.getPage(), movieResponse.getTotalPages());
			});
		}).block();
	}

	/**
	 * 애니 검색 데이터 조회
	 * 
	 * @param keyword 검색어
	 * @param page 페이지
	 * @return 애니 정보 응답 오브젝트
	 */
	@Override
	@Cacheable(value = "searchAni", key = "#keyword + '_' + #isAdult + '_' + #page + '_' + #userId", unless = "#result == null")
	public SearchTvResponseDto searchAni(String keyword, boolean isAdult, Integer page, Long userId) {

		int currentPage = Optional.ofNullable(page).orElse(1);

		Mono<Map<String, Integer>> tvGenreMapMono = apiUtil.getTvGenres();
		Mono<Map<String, Integer>> movieGenreMapMono = apiUtil.getMovieGenres();

		return Mono.zip(tvGenreMapMono, movieGenreMapMono).flatMap(genreTuple -> {
			Map<String, Integer> aniGenreMap = genreTuple.getT1();
			Map<String, Integer> movieGenreMap = genreTuple.getT2();

			// TV 애니 정보 조회
			Mono<TmdbSearchTvDto> tvResponseMono = tmdbWebClient.get()
					.uri(helper.getSearchUri(tvSearchPath, keyword, isAdult, currentPage))
					.retrieve()
					.bodyToMono(TmdbSearchTvDto.class);

			// 영화 정보 조회
			Mono<TmdbSearchMovieDto> movieResponseMono = tmdbWebClient.get()
					.uri(builder -> builder
							.path(movieSearchPath)
							.queryParam(TmdbParamConstants.PARAM_QUERY, keyword)
							.queryParam(TmdbParamConstants.PARAM_INCLUDE_ADULT, isAdult)
							.queryParam(TmdbParamConstants.PARAM_LANGUAGE, TmdbParamConstants.LANGUAGE_KOREAN)
							.queryParam(TmdbParamConstants.PARAM_PAGE, currentPage)
							.build())
					.retrieve()
					.bodyToMono(TmdbSearchMovieDto.class);

			// 비동기로 API실행(webClient처리)후 결과를 병렬처리
			return Mono.zip(tvResponseMono, movieResponseMono).map(tuple -> {
				// TV 응답 DTO
				TmdbSearchTvDto tvResponse = tuple.getT1();
				// 영화 응답 DTO
				TmdbSearchMovieDto movieResponse = tuple.getT2();
				// TV, Movie 검색 결과가 존재하지 않는 경우 빈 응답 반환
				if (tvResponse.getResults() == null && movieResponse.getResults() == null) {
					return new SearchTvResponseDto();
				}

				// 애니 검색 결과 리스트
				List<SearchTvResultsDto> aniResultList = new ArrayList<>();
				// TV 검색 결과에서 애니메이션 정보 추출
				if (tvResponse.getResults() != null) {
					List<TmdbSearchTvResultsDto> tvResultList = tvResponse.getResults();
					List<SearchTvResultsDto> tvList = mapper.tvResultsListToTmdbTvResultsList(tvResultList);
					aniResultList = helper.getAniList(tvList, aniGenreMap);
				}
				// Movie 검색 결과에서 애니메이션 정보 추출
				if (movieResponse.getResults() != null) {
					List<TmdbSearchMovieResultsDto> movieResultList = movieResponse.getResults();
					List<SearchMovieResultsDto> movieList = mapper.movieResultsListToTmdbMovieResultsList(movieResultList);
					aniResultList.addAll(helper.getAniMovieList(movieList, movieGenreMap));
				}

				// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
				if (userId != null) {
					BusinessUtil.setWishlisted(
							aniResultList, 
							CommonMediaTypeEnum.MEDIA_TYPE_ANI.getMediaTypeCode(), 
							userId, 
							dto -> String.valueOf(dto.getId()),
							SearchTvResultsDto::setWishlisted, 
							wishlistRepository);
				}

				// 반환값 설정
				SearchTvResponseDto aniResponse = SearchTvResponseDto.builder()
						.aniResults(aniResultList)
						.page(currentPage)
						.totalPages(tvResponse.getTotalPages() > movieResponse.getTotalPages() ? tvResponse.getTotalPages() : movieResponse.getTotalPages())
						.build();

				// 응답 오브젝트 반환
				return aniResponse;
			});
		}).block();
	}

	/**
	 * 드라마 검색 데이터 조회
	 * 
	 * @param keyword 검색어
	 * @param page 페이지
	 * @return 드라마 정보 응답 오브젝트
	 */
	@Override
	@Cacheable(value = "searchDrama", key = "#keyword + '_' + #isAdult + '_' + #page + '_' + #userId", unless = "#result == null")
	public SearchTvResponseDto searchDrama(String keyword, boolean isAdult, Integer page, Long userId) {

		// 드라마 장르 정보 조회
		return apiUtil.getTvGenres().flatMap(tvGenreMap -> 
		tmdbWebClient.get()
		.uri(helper.getSearchUri(tvSearchPath, keyword, isAdult, Optional.ofNullable(page).orElse(1)))
		.retrieve()
		.bodyToMono(TmdbSearchTvDto.class)
		.map(response -> {
			// TBMD TV 결과를 검색 결과 DTO 리스트로 변환
			List<SearchTvResultsDto> tvResultsList = mapper.tvResultsListToTmdbTvResultsList(response.getResults());

			// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
			if (userId != null) {
				BusinessUtil.setWishlisted(
						tvResultsList, 
						CommonMediaTypeEnum.MEDIA_TYPE_DRAMA.getMediaTypeCode(), 
						userId,
						dto -> String.valueOf(dto.getId()),
						SearchTvResultsDto::setWishlisted, 
						wishlistRepository);
			}

			// 결과값 설정
			SearchTvResponseDto dramaResponse = SearchTvResponseDto.builder()
					.dramaResults(helper.getDramaList(tvResultsList, tvGenreMap))
					.page(response.getPage())
					.totalPages(response.getTotalPages())
					.totalResults(response.getTotalResults())
					.build();

			// 응답 오브젝트 반환
			return dramaResponse;
		})).block();
	}

	/**
	 * 영화 검색 데이터 조회
	 * 
	 * @param keyword 검색어
	 * @param page 페이지
	 * @return 영화 정보 응답 오브젝트
	 */
	@Override
	@Cacheable(value = "searchMovie", key = "#keyword + '_' + #isAdult + '_' + #page + '_' + #userId", unless = "#result == null")
	public SearchMovieResponseDto searchMovie(String keyword, boolean isAdult, Integer page, Long userId) {

		// 영화 장르 정보 조회
		return apiUtil.getMovieGenres().flatMap(movieGenreMap -> 
		// 영화 정보 조회
		tmdbWebClient.get()
		.uri(helper.getSearchUri(movieSearchPath, keyword, isAdult, Optional.ofNullable(page).orElse(1)))
		.retrieve()
		.bodyToMono(TmdbSearchMovieDto.class)
		.map(response -> {
			// TBMD Movie 결과를 검색 결과 DTO 리스트로 변환
			List<SearchMovieResultsDto> movieResultsList = mapper.movieResultsListToTmdbMovieResultsList(response.getResults());

			// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
			if (userId != null) {
				BusinessUtil.setWishlisted(
						movieResultsList, 
						CommonMediaTypeEnum.MEDIA_TYPE_MOVIE.getMediaTypeCode(), 
						userId,
						dto -> String.valueOf(dto.getId()),
						SearchMovieResultsDto::setWishlisted, 
						wishlistRepository);
			}

			// 결과값 설정
			SearchMovieResponseDto movieResponse = SearchMovieResponseDto.builder()
					.movieResults(helper.getMovieList(movieResultsList, movieGenreMap))
					.page(response.getPage())
					.totalPages(response.getTotalPages())
					.totalResults(response.getTotalResults())
					.build();

			// 응답 오브젝트 반환
			return movieResponse;
		})).block();
	}

	/**
	 * 만화 검색 데이터 조회
	 * 
	 * @param keyword 검색어
	 * @param page 페이지
	 * @return 만화 정보 응답 오브젝트
	 */
	@Override
	@Cacheable(value = "searchComics", key = "#keyword + '_' + #isAdult + '_' + #page + '_' + #isMainPage + '_' + #userId", unless = "#result == null")
	public SearchComicsResponseDto searchComics(String keyword, boolean isAdult, Integer page, boolean isMainPage, Long userId) {

		// API를 어디서 불렀는지에 따라 표시 건수를 다르게 설정
		int perPage = isMainPage ? anilistPerMainPage : anilistPerMorePage;

		// 한글 검색어 -> 일본어로 번역후(DeepL API), AniList API 조회
		return apiUtil.getTranslationText(keyword).flatMap(jaKeyword -> {
			try {
				// graphql 쿼리 파일 불러오기
				String query = GraphqlUtil.loadQuery("comicsList.graphql");
				// 리퀘스트 파라미터 작성
				Map<String, Object> variables = new HashMap<>(Map.of(
						AnilistParamConstants.PARAM_PAGE, Optional.ofNullable(page).orElse(1),
						AnilistParamConstants.PARAM_PER_PAGE, perPage,
						AnilistParamConstants.PARAM_SEARCH, jaKeyword
						));
				// 성인물 플래그가 false인 경우, 파라미터 추가
				if (!isAdult) {
					variables.put(AnilistParamConstants.PARAM_IS_ADULT, isAdult);
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
								return new SearchComicsResponseDto();
							}							
							// 페이지 정보 설정
							AniListPageInfoDto comicsPageDto = response.getData().getPage().getPageInfo();
							int currentPage = comicsPageDto.getCurrentPage();
							int lastPage = comicsPageDto.getLastPage();
							
							// 응답 데이터 매핑
							List<SearchComicsResultDto> comicsResultsList = 
									helper.setComicsResponse(response.getData().getPage().getMedia());
							
							// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
							if (userId != null) {
								BusinessUtil.setWishlisted(
										comicsResultsList, 
										CommonMediaTypeEnum.MEDIA_TYPE_COMICS.getMediaTypeCode(), 
										userId,
										dto -> String.valueOf(dto.getId()),
										SearchComicsResultDto::setWishlisted, 
										wishlistRepository);
							}
							
							// 응답 데이터 재분배
							SearchComicsResponseDto comicsResponse = SearchComicsResponseDto.builder()
									.page(currentPage)
									.totalPages(lastPage)
									.isComicsViewMore(currentPage < lastPage)
									.comicsResults(comicsResultsList)
									.build();

							// 응답 오브젝트 반환
							return comicsResponse;
						});
			} catch (IOException e) {
				return Mono.error(e);
			}
		}).block();
	}

}
