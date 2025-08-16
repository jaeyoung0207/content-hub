package com.cjy.contenthub.detail.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.cjy.contenthub.common.api.dto.aniist.AniListMediaDto;
import com.cjy.contenthub.common.api.dto.aniist.AniListResponseDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsMovieDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsTvDto;
import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.util.GraphqlUtil;
import com.cjy.contenthub.common.util.SessionUtil;
import com.cjy.contenthub.detail.controller.dto.DetailComicsRecommendationsResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailComicsRecommendationsResultDto;
import com.cjy.contenthub.detail.helper.DetailRecoommendationHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 상세 화면 추천 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DetailRecommendationServiceImpl implements DetailRecommendationService {

	/** 공통 세션 유팅 */
	private final SessionUtil session;

	/** 상세 헬퍼 */
	private final DetailRecoommendationHelper helper;

	/** TMDB API 통신용 WebClient 클래스 */
	@Qualifier("tmdbWebClient")
	private final WebClient tmdbWebClient;

	/** AniList API 통신용 WebClient 클래스 */
	@Qualifier("anilistWebClient")
	private final WebClient anilistWebClient;

	/** TMDB API TV 추천 작품 API 패스 */
	@Value("${tmdb.url.tvRecommendationsPath}")
	private String tvRecommendationsPath;

	/** TMDB API Movie 추천 작품 API 패스 */
	@Value("${tmdb.url.movieRecommendationsPath}")
	private String movieRecommendationsPath;

	/** TMDB API TV 비슷한 작품 API 패스 */
	@Value("${tmdb.url.tvSimilarPath}")
	private String tvSimilarPath;

	/** TMDB API Movie 비슷한 작품 API 패스 */
	@Value("${tmdb.url.movieSimilarPath}")
	private String movieSimilarPath;

	/** AniList API 전체보기화면 작품 표시 개수 */
	@Value("${anilist.custom.perMorePage}")
	private int anilistPerMorePage;
	
	/** AniList API 상세화면 캐릭터 표시 개수 */
	@Value("${anilist.custom.perCharacterPage}")
	private int anilistPerCharacterPage;

	/** 리퀘스트 파라미터 키 : TV SERIES ID */
	private static final String PARAM_TV_SERIES_ID = "series_id";

	/** 리퀘스트 파라미터 키 : MOVIE ID */
	private static final String PARAM_MOVIE_ID = "movie_id";

	/** 리퀘스트 파라미터 키 : 언어 */
	private static final String PARAM_LANGUAGE = "language";

	/** 리퀘스트 파라미터 키 : 페이지 번호 */
	private static final String PARAM_PAGE = "page";

	/** 리퀘스트 파라미터 키 : 페이지당 표시 건수 */
	private static final String PARAM_PER_PAGE = "perPage";

	/** 리퀘스트 파라미터 키 : 미디어 ID */
	private static final String PARAM_MEDIA_ID = "mediaId";
	
	/** 리퀘스트 파라미터 키 : 성인물 포함 여부 */
	private static final String PARAM_IS_ADULT = "isAdult";

	/** 언어 : 한국어 */
	private static final String LANGUAGE_KOREAN = "ko-KR";

	/** 언어 : 영어 */
	private static final String LANGUAGE_ENGLISH = "en-US";

	/** 첫번째 페이지 번호 */
	private static final int FIRST_PAGE_NO = 1;

	/** TMDB TV API Error */
	private static final String TMDB_TV_API_ERROR_MSG = "TMDB TV API Error";

	/** TMDB Movie API Error */
	private static final String TMDB_MOVIE_API_ERROR_MSG = "TMDB Movie API Error";

	/** AniList API Error */
	private static final String ANILIST_API_ERROR_MSG = "AniList API Error";

	/**
	 * TMDB TV 추천 작품 조회 API
	 * 
	 * @param seriesId TV 시리즈 ID
	 * @param page 페이지 번호 (선택)
	 * @return 추천 작품 응답 DTO
	 */
	@Override
	@Cacheable(value = "tmdbTvRecommendations", key = "#seriesId + '-' + #page", unless = "#result == null")
	public TmdbRecommendationsTvDto getTvRecommendations(Integer seriesId, Integer page) {

		// TMDB TV 추천 작품 조회
		return tmdbWebClient.get()
				.uri(helper.getTVRecommendationUri(seriesId, page, LANGUAGE_KOREAN))
				.retrieve()
				.bodyToMono(TmdbRecommendationsTvDto.class)
				.onErrorResume(WebClientResponseException.class, ex -> {
					// 404의 경우 재시도
					if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
						// 로그 출력
						log.warn("TMDB TV Recommendations not found then retry for seriesId: {}", seriesId);
						// 영어로 재시도
						return tmdbWebClient.get()
								.uri(helper.getTVRecommendationUri(seriesId, page, LANGUAGE_ENGLISH))
								.retrieve()
								.onStatus(HttpStatusCode::isError, response ->
								response.bodyToMono(String.class).flatMap(body -> {
									// 404의 경우는 무시하고 빈 응답 반환
									if (response.statusCode() == HttpStatus.NOT_FOUND) {
										log.warn("TMDB TV Recommendations not found for seriesId: {}", seriesId);
										return Mono.empty(); 
									}
									// 나머지는 공통 예외 처리로 보냄
									return Mono.error(new WebClientResponseException(
											TMDB_TV_API_ERROR_MSG, response.statusCode().value(), null, null, body.getBytes(), null));
								}))
								.bodyToMono(TmdbRecommendationsTvDto.class);
					}
					return Mono.error(new WebClientResponseException(TMDB_TV_API_ERROR_MSG, ex.getStatusCode().value(),
							null, null, ex.getResponseBodyAsByteArray(), null));
				})
				.flatMap(response -> {
					// 추천 작품이 존재하지 않는 경우, 유사한 작품으로 재시도
					if (response.getTotalResults() == 0) {
						return tmdbWebClient.get()
								.uri(builder -> builder
										.path(String.format(tvSimilarPath, seriesId))
										.queryParam(PARAM_TV_SERIES_ID, seriesId)
										.queryParam(PARAM_LANGUAGE, LANGUAGE_KOREAN)
										.queryParam(PARAM_PAGE, Optional.ofNullable(page).orElse(1))
										.build())
								.retrieve()
								.bodyToMono(TmdbRecommendationsTvDto.class);
					} else {
						// 추천 작품이 존재하는 경우, 그대로 응답 반환
						return Mono.just(response);
					}
				}).block();
	}

	/**
	 * TMDB 영화 추천 작품 조회 API
	 * 
	 * @param movieId 영화 ID
	 * @param page 페이지 번호 (선택)
	 * @return ResponseEntity<TmdbRecommendationsMovieDto> 추천 작품 응답 DTO
	 */
	@Override
	@Cacheable(value = "tmdbMovieRecommendations", key = "#movieId + '-' + #page", unless = "#result == null")
	public TmdbRecommendationsMovieDto getMovieRecommendations(Integer movieId, Integer page) {

		// TMDB 영화 추천 작품 조회
		return tmdbWebClient.get()
				.uri(helper.getMovieRecommendationUri(movieId, page, LANGUAGE_KOREAN))
				.retrieve()
				.bodyToMono(TmdbRecommendationsMovieDto.class)
				.onErrorResume(WebClientResponseException.class, ex -> {
					// 404의 경우 재시도
					if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
						// 로그 출력
						log.warn("TMDB Movie Recommendations not found then retry for movieId: {}", movieId);
						// 영어로 재시도
						return tmdbWebClient.get()
								.uri(helper.getMovieRecommendationUri(movieId, page, LANGUAGE_ENGLISH))
								.retrieve()
								.onStatus(HttpStatusCode::isError, response ->
								response.bodyToMono(String.class).flatMap(body -> {
									// 404의 경우는 무시하고 빈 응답 반환
									if (response.statusCode() == HttpStatus.NOT_FOUND) {
										log.warn("TMDB Movie Recommendations not found for movieId: {}", movieId);
										return Mono.empty(); 
									}
									// 나머지는 공통 예외 처리로 보냄
									return Mono.error(new WebClientResponseException(
											TMDB_MOVIE_API_ERROR_MSG, response.statusCode().value(), null, null, body.getBytes(), null));
								}))
								.bodyToMono(TmdbRecommendationsMovieDto.class);
					}
					return Mono.error(new WebClientResponseException(TMDB_MOVIE_API_ERROR_MSG, ex.getStatusCode().value(),
							null, null, ex.getResponseBodyAsByteArray(), null));
				})
				.flatMap(response -> {
					// 추천 작품이 존재하지 않는 경우, 유사한 작품으로 재시도
					if (response.getTotalResults() == 0) {
						return tmdbWebClient.get()
								.uri(builder -> builder
										.path(String.format(movieSimilarPath, movieId))
										.queryParam(PARAM_MOVIE_ID, movieId)
										.queryParam(PARAM_LANGUAGE, LANGUAGE_KOREAN)
										.queryParam(PARAM_PAGE, Optional.ofNullable(page).orElse(1))
										.build())
								.retrieve()
								.bodyToMono(TmdbRecommendationsMovieDto.class);
					} else {
						// 추천 작품이 존재하는 경우, 그대로 응답 반환
						return Mono.just(response);
					}
				}).block();
	}

	/**
	 * AniList Comics 추천 작품 조회 API
	 * 
	 * @param mediaId 미디어 추천 ID
	 * @param page 페이지 번호 (선택)
	 * @return ResponseEntity<DetailComicsRecommendationsResponseDto> 추천 작품 응답 DTO
	 */
	@Override
	@Cacheable(value = "anilistComicsRecommendations", key = "#mediaId + '-' + #page", unless = "#result == null")
	public DetailComicsRecommendationsResponseDto getComicsRecommendations(Integer mediaId, Integer page) throws IOException {

		// 성인콘텐츠 포함 여부
		boolean isAdult = session.getSessionBooleanValue(CommonConstants.ADULT_FLG);
		// graphql 쿼리 파일 불러오기
		String query = GraphqlUtil.loadQuery("comicsRecomendation.graphql");
		// 리퀘스트 파라미터 작성
		Map<String, Object> variables = new HashMap<>(Map.of(
				PARAM_MEDIA_ID, mediaId,
				PARAM_PAGE, Optional.ofNullable(page).orElse(FIRST_PAGE_NO),
				PARAM_PER_PAGE, anilistPerMorePage
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
				.onStatus(HttpStatusCode::isError, response ->
				response.bodyToMono(String.class).flatMap(body -> {
					// 404의 경우는 무시하고 빈 응답 반환
					if (response.statusCode() == HttpStatus.NOT_FOUND) {
						log.warn("AniList Comics Recommendations not found for mediaId: {}", mediaId);
						return Mono.empty(); 
					}
					// 나머지는 공통 예외 처리로 보냄
					return Mono.error(new WebClientResponseException(
							ANILIST_API_ERROR_MSG, response.statusCode().value(), null, null, body.getBytes(), null));
				}))
				.bodyToMono(AniListResponseDto.class)
				.map(response -> {
					// 추천 작품 응답 DTO 생성
					DetailComicsRecommendationsResponseDto recommendationResponse = new DetailComicsRecommendationsResponseDto();
					// 추천 작품이 존재하는 경우
					if (ObjectUtils.isNotEmpty(response.getData())
							&& ObjectUtils.isNotEmpty(response.getData().getMedia())) {
						// 결과 리스트 생성
						List<DetailComicsRecommendationsResultDto> results = new ArrayList<>();
						// 응답 데이터에서 미디어 추천 데이터 추출
						AniListMediaDto media =  response.getData().getMedia();

						// 첫번째 페이지인 경우, 관련 작품 노드 리스트를 추가
						if (page == FIRST_PAGE_NO) {
							helper.getComicsRelations(media, results);
						}
						// 추천 작품 설정
						helper.getComicsRecommendations(media, results);

						// 추천 작품 응답 DTO 설정
						recommendationResponse = DetailComicsRecommendationsResponseDto.builder()
								.results(results)
								.build();
					}

					// 추천 작품 응답 DTO 반환
					return recommendationResponse;
				}).block();
	}

}
