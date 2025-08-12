package com.cjy.contenthub.detail.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.cjy.contenthub.common.api.dto.aniist.AniListMediaDto;
import com.cjy.contenthub.common.api.dto.aniist.AniListResponseDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbMovieDetailsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsMovieDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsTvDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbTvDetailsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbWatchProvidersDto;
import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.util.GraphqlUtil;
import com.cjy.contenthub.common.util.SessionUtil;
import com.cjy.contenthub.detail.controller.dto.DetailComicsRecommendationsResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailComicsRecommendationsResultDto;
import com.cjy.contenthub.detail.controller.dto.DetailComicsResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailCommentGetDataDto;
import com.cjy.contenthub.detail.controller.dto.DetailCommentGetResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailCommentSaveRequestDto;
import com.cjy.contenthub.detail.controller.dto.DetailCommentUpdateRequestDto;
import com.cjy.contenthub.detail.controller.dto.DetailMovieResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailTvResponseDto;
import com.cjy.contenthub.detail.helper.DetailHelper;
import com.cjy.contenthub.detail.mapper.DetailMapper;
import com.cjy.contenthub.detail.service.DetailCommentService;
import com.cjy.contenthub.detail.service.dto.DetailCommentDataServiceDto;
import com.cjy.contenthub.detail.service.dto.DetailCommentServiceDto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 상세 화면 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/detail")
@RequiredArgsConstructor
@Slf4j
public class DetailController {

	/** 코멘트 서비스 */
	private final DetailCommentService service;

	/** 상세 매퍼 */
	private final DetailMapper mapper;

	/** 공통 세션 유팅 */
	private final SessionUtil session;

	/** 상세 헬퍼 */
	private final DetailHelper helper;

	/** TMDB API 통신용 WebClient 클래스 */
	@Qualifier("tmdbWebClient")
	private final WebClient tmdbWebClient;

	/** AniList API 통신용 WebClient 클래스 */
	@Qualifier("anilistWebClient")
	private final WebClient anilistWebClient;

	/** DeepL API 통신용 WebClient 클래스 */
	@Qualifier("deeplWebClient")
	private final WebClient deeplWebClient;

	/** TMDB API TV Detail API 패스 */
	@Value("${tmdb.url.tvDetailPath}")
	private String tvDetailPath;

	/** TMDB API Movie Detail API 패스 */
	@Value("${tmdb.url.movieDetailPath}")
	private String movieDetailPath;

	/** TMDB API TV Watch Providers API 패스 */
	@Value("${tmdb.url.tvWatchProvidersPath}")
	private String tvWatchProvidersPath;

	/** TMDB API Movie Watch Providers API 패스 */
	@Value("${tmdb.url.movieWatchProvidersPath}")
	private String movieWatchProvidersPath;

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

	/** 리퀘스트 파라미터 키 : TV SERIES ID */
	private static final String PARAM_TV_SERIES_ID = "series_id";

	/** 리퀘스트 파라미터 키 : MOVIE ID */
	private static final String PARAM_MOVIE_ID = "movie_id";

	/** 리퀘스트 파라미터 키 : COMICS ID */
	private static final String PARAM_COMICS_ID = "comics_id";

	/** 리퀘스트 파라미터 키 : 언어 */
	private static final String PARAM_LANGUAGE = "language";

	/** 리퀘스트 파라미터 키 : 코멘트 번호 */
	private static final String PARAM_COMMENT_NO = "commentNo";

	/** 리퀘스트 파라미터 키 : append_to_response */
	private static final String PARAM_APPEND_TO_RESPONSE = "append_to_response";

	/** 리퀘스트 파라미터 키 : Original Media Type */
	private static final String PARAM_ORIGINAL_MEDIATYPE = "originalMediaType";

	/** 리퀘스트 파라미터 키 : API ID */
	private static final String PARAM_API_ID = "apiId";

	/** 리퀘스트 파라미터 키 : 페이지 번호 */
	private static final String PARAM_PAGE = "page";

	/** 리퀘스트 파라미터 키 : 페이지당 표시 건수 */
	private static final String PARAM_PER_PAGE = "perPage";

	/** 리퀘스트 파라미터 키 : 유저ID */
	private static final String PARAM_USER_ID = "userId";

	/** 리퀘스트 파라미터 키 : 미디어 ID */
	private static final String PARAM_MEDIA_ID = "mediaId";

	/** 리퀘스트 파라미터 키 : 성인물 포함 여부 */
	private static final String PARAM_IS_ADULT = "isAdult";

	/** 언어 : 한국어 */
	private static final String LANGUAGE_KOREAN = "ko-KR";

	/** 언어 : 영어 */
	private static final String LANGUAGE_ENGLISH = "en-US";

	/** 크레딧 : credits */
	private static final String CREDITS = "credits";

	/** 첫번째 페이지 번호 */
	private static final int FIRST_PAGE_NO = 1;

	/** TMDB TV API Error */
	private static final String TMDB_TV_API_ERROR_MSG = "TMDB TV API Error";

	/** TMDB Movie API Error */
	private static final String TMDB_MOVIE_API_ERROR_MSG = "TMDB Movie API Error";

	/** AniList API Error */
	private static final String ANILIST_API_ERROR_MSG = "AniList API Error";

	/**
	 * 코멘트 등록 API
	 * 
	 * @param params 상세 코멘트 등록 요청 DTO
	 * @return ResponseEntity<Boolean> 등록 결과
	 */
	@PostMapping(value = "/saveComment")
	public ResponseEntity<Boolean> saveComent(@RequestBody @Validated DetailCommentSaveRequestDto params) {

		// 요청 파라미터를 서비스 DTO로 변환
		DetailCommentDataServiceDto commentDto = mapper.commentSaveReqToCommentService(params);

		// 등록 서비스 호출
		Boolean saveResult = service.saveComment(commentDto);

		// 등록 결과 반환
		return ResponseEntity.ok(saveResult);

	}

	/**
	 * 코멘트 갱신 API
	 * 
	 * @param params 상세 코멘트 갱신 요청 DTO
	 * @return ResponseEntity<Boolean> 갱신 결과
	 */
	@PutMapping(value = "/updateComment")
	public ResponseEntity<Boolean> updateComent(@RequestBody @Validated DetailCommentUpdateRequestDto params) {

		// 요청 파라미터를 서비스 DTO로 변환
		DetailCommentDataServiceDto commentDto = mapper.commentUpdateReqToCommentService(params);

		// 갱신 서비스 호출
		Boolean updateResult = service.updateComment(commentDto);

		// 갱신 결과 반환
		return ResponseEntity.ok(updateResult);

	}

	/**
	 * 코멘트 삭제 API
	 * 
	 * @param commentNo 코멘트 번호
	 * @return ResponseEntity<Boolean> 삭제 결과
	 */
	@DeleteMapping(value = "/deleteComment")
	public ResponseEntity<Boolean> deleteComment(@RequestParam(PARAM_COMMENT_NO) @NotNull Long commentNo) {

		// 삭제 서비스 호출
		Boolean deleteResult = service.deleteComment(commentNo);

		// 삭제 결과 반환
		return ResponseEntity.ok(deleteResult);

	}

	/**
	 * 코멘트 목록 조회 API
	 * 
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId API ID
	 * @param page 페이지 번호 (선택)
	 * @param userId 유저 ID (선택)
	 * @return ResponseEntity<DetailCommentGetResponseDto> 코멘트 목록 응답 DTO
	 */
	@GetMapping(value = "/getCommentList")
	public ResponseEntity<DetailCommentGetResponseDto> getCommentList(
			@NotEmpty @RequestParam(PARAM_ORIGINAL_MEDIATYPE)  String originalMediaType,
			@NotEmpty @RequestParam(PARAM_API_ID)  String apiId,
			@Nullable @RequestParam(PARAM_PAGE) Integer page,
			@Nullable @RequestParam(PARAM_USER_ID)  String userId
			) {

		// 응답 DTO 초기화
		DetailCommentGetResponseDto response = new DetailCommentGetResponseDto();

		// 코멘트 조회 서비스 호출
		DetailCommentServiceDto serviceResult = service.getCommentList(originalMediaType, apiId, page, userId);

		// 서비스 DTO를 응답 DTO로 변환
		List<DetailCommentGetDataDto> responseDtoList = mapper.commentServiceDtoListToCommentGetResponseDtoList(serviceResult.getDataList());

		// 응답 DTO 설정
		response.setResponseList(responseDtoList);
		response.setTotalElements(serviceResult.getTotalElements());

		// 코멘트 목록 반환
		return ResponseEntity.status(HttpStatus.OK).body(response);

	}

	/**
	 * 별점 평균 조회 API
	 * 
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId API ID
	 * @return ResponseEntity<BigDecimal> 별점 평균
	 */
	@GetMapping(value = "/getStarRatingAverage")
	public ResponseEntity<BigDecimal> getStarRatingAverage(
			@RequestParam(PARAM_ORIGINAL_MEDIATYPE) @NotEmpty String originalMediaType,
			@RequestParam(PARAM_API_ID) @NotEmpty String apiId
			) {

		// 별점 평균 조회 서비스 호출
		BigDecimal starRating = service.getStarRatingAverage(originalMediaType, apiId);

		// 별점 평균 반환
		return ResponseEntity.status(HttpStatus.OK).body(starRating);

	}

	/**
	 * TMDB TV 상세 조회 API
	 * 
	 * @param seriesId TV 시리즈 ID
	 * @param originalMediaType 원본 미디어 타입
	 * @return ResponseEntity<DetailTvResponseDto> TV 상세 응답 DTO
	 */
	@GetMapping(value = "/getTvDetail")
	public ResponseEntity<DetailTvResponseDto> getTvDetail(
			@NotNull @RequestParam(PARAM_TV_SERIES_ID) Integer seriesId
			) {

		// TMDB TV 상세 조회
		Mono<TmdbTvDetailsDto> detailMono = tmdbWebClient.get()
				.uri(builder -> builder
						.path(String.format(tvDetailPath, seriesId))
						.queryParam(PARAM_TV_SERIES_ID, seriesId)
						.queryParam(PARAM_APPEND_TO_RESPONSE, CREDITS)
						.queryParam(PARAM_LANGUAGE, LANGUAGE_KOREAN)
						.build())
				.retrieve()
				.bodyToMono(TmdbTvDetailsDto.class);
		// TMDB 시청 제공자 조회
		Mono<TmdbWatchProvidersDto> watchProvidersMono = tmdbWebClient.get()
				.uri(builder -> builder
						.path(String.format(tvWatchProvidersPath, seriesId))
						.queryParam(PARAM_TV_SERIES_ID, seriesId)
						.build())
				.retrieve()
				.bodyToMono(TmdbWatchProvidersDto.class);

		// TV 상세 조회 결과와 시청 제공자 조회 결과를 병합하여 반환
		return Mono.zip(detailMono, watchProvidersMono).map(tuple -> {
			// TMDB TV 상세 DTO
			TmdbTvDetailsDto detailResponse = tuple.getT1();
			// TMDB 시청 제공자 DTO
			TmdbWatchProvidersDto watchProvidersResponse = tuple.getT2();
			// 시청 제공자 링크 취득
			String link = Optional.ofNullable(watchProvidersResponse.getResults())
					.map(results -> results.getKr())
					.map(getKr -> getKr.getLink())
					.orElse(null);

			// 반환값 설정
			// TMDB TV 상세 DTO를 응답 DTO로 변환
			DetailTvResponseDto response = mapper.detailTvToDetailTvResponse(detailResponse);
			response.setLink(link);

			// 응답 DTO 반환
			return ResponseEntity.ok(response);
		}).block();
	}

	/**
	 * TMDB 영화 상세 조회 API
	 * 
	 * @param movieId 영화 ID
	 * @param originalMediaType 원본 미디어 타입
	 * @return ResponseEntity<DetailMovieResponseDto> 영화 상세 응답 DTO
	 */
	@GetMapping(value = "/getMovieDetail")
	public ResponseEntity<DetailMovieResponseDto> getMovieDetail(
			@NotNull @RequestParam(PARAM_MOVIE_ID) Integer movieId
			) {

		// TMDB 영화 상세 조회
		Mono<TmdbMovieDetailsDto> detailMono = tmdbWebClient.get()
				.uri(builder -> builder
						.path(String.format(movieDetailPath, movieId))
						.queryParam(PARAM_MOVIE_ID, movieId)
						.queryParam(PARAM_APPEND_TO_RESPONSE, CREDITS)
						.queryParam(PARAM_LANGUAGE, LANGUAGE_KOREAN)
						.build())
				.retrieve()
				.bodyToMono(TmdbMovieDetailsDto.class);

		// TMDB 시청 제공자 취득
		Mono<TmdbWatchProvidersDto> watchProvidersMono = tmdbWebClient.get()
				.uri(builder -> builder
						.path(String.format(movieWatchProvidersPath, movieId))
						.queryParam(PARAM_TV_SERIES_ID, movieId)
						.build())
				.retrieve()
				.bodyToMono(TmdbWatchProvidersDto.class);

		// 영화 상세 조회 결과와 시청 제공자 조회 결과를 병합하여 반환 
		return Mono.zip(detailMono, watchProvidersMono).map(tuple -> {
			// TMDB 영화 상세 DTO
			TmdbMovieDetailsDto detailResponse = tuple.getT1();
			// TMDB 시청 제공자 DTO
			TmdbWatchProvidersDto watchProvidersResponse = tuple.getT2();
			// 시청 제공자 링크 취득
			String link = Optional.ofNullable(watchProvidersResponse.getResults())
					.map(results -> results.getKr())
					.map(getKr -> getKr.getLink())
					.orElse(null);
			// 반환값 설정
			// TMDB 영화 상세 DTO를 응답 DTO로 변환
			DetailMovieResponseDto respoonse = mapper.detailMovieToDetailMovieResponse(detailResponse);
			respoonse.setLink(link);

			// 응답 DTO 반환
			return ResponseEntity.ok(respoonse);
		}).block();
	}

	/**
	 * AniList Comics 상세 조회 API
	 * 
	 * @param comicsId Comics ID
	 * @return ResponseEntity<DetailComicsResponseDto> Comics 상세 응답 DTO
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@GetMapping(value = "/getComicsDetail")
	public ResponseEntity<DetailComicsResponseDto> getComicsDetail(
			@NotNull @RequestParam(PARAM_COMICS_ID) Integer comicsId
			) throws IOException {

		// GraphQL 쿼리 파일 불러오기
		String query = GraphqlUtil.loadQuery("comics.graphql");
		// 리퀘스트 파라미터 작성
		Map<String, Object> varaiables = Map.of(
				PARAM_MEDIA_ID, comicsId
				);
		// 쿼리에 리퀘스트 파라미터 적용하여 문자열 생성
		String requestBody = GraphqlUtil.buildRequestBody(query, varaiables);

		// AniList API 조회
		return anilistWebClient.post()
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(AniListResponseDto.class)
				.map(response -> {
					// 응답 데이터 재분배
					AniListMediaDto media = response.getData().getMedia();
					// 연재 시작일
					String startDate = String.valueOf(media.getStartDate().getYear()) 
							.concat("/")
							.concat(StringUtils.leftPad(String.valueOf(media.getStartDate().getMonth()), 2, "0"))
							.concat("/")
							.concat(StringUtils.leftPad(String.valueOf(media.getStartDate().getDay()), 2, "0"));
					// 응답 DTO 생성
					DetailComicsResponseDto comicsResponse =
							DetailComicsResponseDto.builder()
							.id(media.getId())
							.overview(media.getDescription())
							.comicsGenres(media.getGenres())
							.adult(media.isAdult())
							.volumes(media.getVolumes())
							.chapters(media.getChapters())
							.status(media.getStatus())
							.homepage(media.getSiteUrl())
							.title(media.getTitle().getUserPreferred())
							.backdropPath(media.getCoverImage().getLarge())
							.posterPath(media.getCoverImage().getExtraLarge())
							.characters(media.getCharacters())
							.startDate(startDate)
							.build();

					// 응답 DTO 반환
					return ResponseEntity.ok(comicsResponse);
				}).block();
	}

	/**
	 * TMDB TV 추천 작품 조회 API
	 * 
	 * @param seriesId TV 시리즈 ID
	 * @param page 페이지 번호 (선택)
	 * @return ResponseEntity<TmdbRecommendationsTvDto> 추천 작품 응답 DTO
	 */
	@GetMapping(value = "/getTvRecommendations")
	public ResponseEntity<TmdbRecommendationsTvDto> getTvRecommendations(
			@NotNull @RequestParam(PARAM_TV_SERIES_ID) Integer seriesId,
			@Nullable @RequestParam(PARAM_PAGE) Integer page
			) {

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
								.bodyToMono(TmdbRecommendationsTvDto.class)
								.map(ResponseEntity::ok);

					} else {
						// 추천 작품이 존재하는 경우, 그대로 응답 반환
						return Mono.just(ResponseEntity.ok(response));
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
	@GetMapping(value = "/getMovieRecommendations")
	public ResponseEntity<TmdbRecommendationsMovieDto> getMovieRecommendations(
			@NotNull @RequestParam(PARAM_MOVIE_ID) Integer movieId,
			@Nullable @RequestParam(PARAM_PAGE) Integer page
			) {

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
								.bodyToMono(TmdbRecommendationsMovieDto.class)
								.map(ResponseEntity::ok);
					} else {
						// 추천 작품이 존재하는 경우, 그대로 응답 반환
						return Mono.just(ResponseEntity.ok(response));
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
	@GetMapping(value = "/getComicsRecommendations")
	public ResponseEntity<DetailComicsRecommendationsResponseDto> getComicsRecommendations(
			@NotNull @RequestParam(PARAM_MEDIA_ID) Integer mediaId,
			@Nullable @RequestParam(PARAM_PAGE) Integer page
			) throws IOException {

		// 성인콘텐츠 포함 여부
		boolean isAdult = session.getSessionBooleanValue(CommonConstants.ADULT_FLG);
		// graphql 쿼리 파일 불러오기
		String query = GraphqlUtil.loadQuery("comicsRecomendation.graphql");
		// 리퀘스트 파라미터 작성
		Map<String, Object> variables = new HashMap<>(Map.of(
				PARAM_MEDIA_ID, mediaId,
				PARAM_PAGE, Optional.ofNullable(page).orElse(1),
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
					return ResponseEntity.ok(recommendationResponse);
				}).block();
	}

}
