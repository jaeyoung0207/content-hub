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
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.cjy.contenthub.common.api.dto.aniist.AniListMediaDto;
import com.cjy.contenthub.common.api.dto.aniist.AniListResponseDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsMovieDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsTvDto;
import com.cjy.contenthub.common.constants.AnilistParamConstants;
import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMediaTypeEnum;
import com.cjy.contenthub.common.constants.TmdbParamConstants;
import com.cjy.contenthub.common.util.ApiUtil;
import com.cjy.contenthub.common.util.BusinessUtil;
import com.cjy.contenthub.common.util.GraphqlUtil;
import com.cjy.contenthub.detail.controller.dto.DetailComicsRecommendationsResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailComicsRecommendationsResultDto;
import com.cjy.contenthub.detail.controller.dto.DetailRecommendationsMovieDto;
import com.cjy.contenthub.detail.controller.dto.DetailRecommendationsMovieResultsDto;
import com.cjy.contenthub.detail.controller.dto.DetailRecommendationsTvDto;
import com.cjy.contenthub.detail.controller.dto.DetailRecommendationsTvResultsDto;
import com.cjy.contenthub.detail.helper.DetailRecoommendationHelper;
import com.cjy.contenthub.detail.mapper.DetailMapper;
import com.cjy.contenthub.wishlist.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 상세 추천 서비스 구현 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DetailRecommendationServiceImpl implements DetailRecommendationService {

	/** 상세 헬퍼 */
	private final DetailRecoommendationHelper recommendationHelper;

	/** 상세 매퍼 */
	private final DetailMapper detailMapper;

	/** 위시리스트 리포지토리 */
	private final WishlistRepository wishlistRepository;

	/** TMDB API 통신용 WebClient 클래스 */
	@Qualifier("tmdbWebClient")
	private final WebClient tmdbWebClient;

	/** AniList API 통신용 WebClient 클래스 */
	@Qualifier("anilistWebClient")
	private final WebClient anilistWebClient;

	/** API 유틸리티 클래스 */
	private final ApiUtil apiUtil;

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
	 * @param page 페이지 번호
	 * @param userId 유저 테이블 ID
	 * @return 추천 작품 응답 DTO
	 */
	@Override
	@Cacheable(value = "tmdbTvRecommendations", key = "#seriesId + '-' + #page + '-' + #userId", unless = "#result == null")
	public DetailRecommendationsTvDto getTvRecommendations(Integer seriesId, Integer page, Long userId) {

		// TMDB 장르 정보 조회
		return apiUtil.getTvGenres().flatMap(genreMap -> 
		// TMDB TV 추천 작품 조회
		tmdbWebClient.get()
		.uri(recommendationHelper.getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_KOREAN))
		.retrieve()
		.bodyToMono(TmdbRecommendationsTvDto.class)
		.onErrorResume(WebClientResponseException.class, ex -> {
			// 404의 경우 재시도
			if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
				// 로그 출력
				log.warn("TMDB TV Recommendations not found then retry for seriesId: {}", seriesId);
				// 영어로 재시도
				return tmdbWebClient.get()
						.uri(recommendationHelper.getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_ENGLISH))
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
			// 그 이외의 경우는 공통 예외 처리로 보냄
			return Mono.error(new WebClientResponseException(TMDB_TV_API_ERROR_MSG, ex.getStatusCode().value(),
					null, null, ex.getResponseBodyAsByteArray(), null));
		})
		.map(response -> {

			// 빈 응답인 경우 빈 객체 반환
			if (response == null || CollectionUtils.isEmpty(response.getResults())) {
				return new DetailRecommendationsTvDto();
			}
			// TMDB 응답 DTO -> 상세 화면 DTO 변환
			List<DetailRecommendationsTvResultsDto> tvResultList =
					detailMapper.tmdbRecommendationsTvListToDetailRecommendationsTvList(response.getResults());
			// 응답 정보 필터링
			List<DetailRecommendationsTvResultsDto> filterdResultList = 
					recommendationHelper.setTvRecommendationResults(tvResultList, genreMap);

			// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
			if (userId != null) {
				BusinessUtil.setWishlisted(
						filterdResultList, 
						CommonMediaTypeEnum.MEDIA_TYPE_ANI.getMediaTypeCode(), 
						userId,
						dto -> String.valueOf(dto.getId()),
						DetailRecommendationsTvResultsDto::setWishlisted, 
						wishlistRepository);
				BusinessUtil.setWishlisted(
						filterdResultList, 
						CommonMediaTypeEnum.MEDIA_TYPE_DRAMA.getMediaTypeCode(), 
						userId,
						dto -> String.valueOf(dto.getId()),
						DetailRecommendationsTvResultsDto::setWishlisted, 
						wishlistRepository);
			}

			// 필터링된 응답 반환
			return DetailRecommendationsTvDto.builder()
					.page(response.getPage())
					.totalPages(response.getTotalPages())
					.totalResults(response.getTotalResults())
					.results(filterdResultList)
					.build();

		})).block();
	}

	/**
	 * TMDB 영화 추천 작품 조회 API
	 * 
	 * @param movieId 영화 ID
	 * @param page 페이지 번호
	 * @param userId 유저 테이블 ID
	 * @return ResponseEntity<TmdbRecommendationsMovieDto> 추천 작품 응답 DTO
	 */
	@Override
	@Cacheable(value = "tmdbMovieRecommendations", key = "#movieId + '-' + #page + '-' + #userId", unless = "#result == null")
	public DetailRecommendationsMovieDto getMovieRecommendations(Integer movieId, Integer page, Long userId) {

		// TMDB 영화 추천 작품 조회
		return tmdbWebClient.get()
				.uri(recommendationHelper.getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_KOREAN))
				.retrieve()
				.bodyToMono(TmdbRecommendationsMovieDto.class)
				.onErrorResume(WebClientResponseException.class, ex -> {
					// 404의 경우 재시도
					if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
						// 로그 출력
						log.warn("TMDB Movie Recommendations not found then retry for movieId: {}", movieId);
						// 영어로 재시도
						return tmdbWebClient.get()
								.uri(recommendationHelper.getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_ENGLISH))
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
					// 그 이외의 경우는 공통 예외 처리로 보냄
					return Mono.error(new WebClientResponseException(TMDB_MOVIE_API_ERROR_MSG, ex.getStatusCode().value(),
							null, null, ex.getResponseBodyAsByteArray(), null));
				})
				.map(response -> {
					// 빈 응답인 경우 빈 객체 반환
					if (response == null || CollectionUtils.isEmpty(response.getResults())) {
						return new DetailRecommendationsMovieDto();
					}
					// TMDB 응답 DTO -> 상세 화면 DTO 변환
					List<DetailRecommendationsMovieResultsDto> movieResultList = 
							detailMapper.tmdbRecommendationsMovieListToDetailRecommendationsMovieList(response.getResults());

					// 미디어 타입
					String originalMediaType = CommonMediaTypeEnum.MEDIA_TYPE_MOVIE.getMediaTypeCode();

					// 응답 정보 필터링
					movieResultList.stream()
					.filter(result -> !CollectionUtils.isEmpty(result.getGenreIds()))
					.forEach(result -> result.setOriginalMediaType(originalMediaType));

					// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
					if (userId != null) {
						BusinessUtil.setWishlisted(
								movieResultList,
								CommonMediaTypeEnum.MEDIA_TYPE_ANI.getMediaTypeCode(), 
								userId,
								dto -> String.valueOf(dto.getId()),
								DetailRecommendationsMovieResultsDto::setWishlisted, 
								wishlistRepository);
					}

					// 필터링된 응답 반환
					return DetailRecommendationsMovieDto.builder()
							.page(response.getPage())
							.totalPages(response.getTotalPages())
							.totalResults(response.getTotalResults())
							.results(movieResultList).build();
				}).block();
	}

	/**
	 * AniList Comics 추천 작품 조회 API
	 * 
	 * @param mediaId 미디어 추천 ID
	 * @param page 페이지 번호
	 * @param userId 유저 테이블 ID
	 * @return ResponseEntity<DetailComicsRecommendationsResponseDto> 추천 작품 응답 DTO
	 */
	@Override
	@Cacheable(value = "anilistComicsRecommendations", key = "#mediaId + '-' + #page + '-' + #userId", unless = "#result == null")
	public DetailComicsRecommendationsResponseDto getComicsRecommendations(Integer mediaId, Integer page, Long userId) throws IOException {

		// graphql 쿼리 파일 불러오기
		String query = GraphqlUtil.loadQuery("comicsRecomendationList.graphql");
		// 리퀘스트 파라미터 작성
		Map<String, Object> variables = new HashMap<>(Map.of(
				AnilistParamConstants.PARAM_MEDIA_ID, mediaId,
				AnilistParamConstants.PARAM_PAGE, Optional.ofNullable(page).orElse(CommonConstants.FIRST_PAGE_NO),
				AnilistParamConstants.PARAM_PER_PAGE, anilistPerMorePage
				));
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
						if (page == CommonConstants.FIRST_PAGE_NO) {
							recommendationHelper.getComicsRelations(media, results);
						}
						// 추천 작품 설정
						recommendationHelper.getComicsRecommendations(media, results);

						// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
						if (userId != null) {
							BusinessUtil.setWishlisted(
									results,
									CommonMediaTypeEnum.MEDIA_TYPE_ANI.getMediaTypeCode(), 
									userId,
									dto -> String.valueOf(dto.getId()),
									DetailComicsRecommendationsResultDto::setWishlisted, 
									wishlistRepository);
						}

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
