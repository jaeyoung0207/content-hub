package com.cjy.contenthub.detail.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.common.api.dto.aniist.AniListMediaDto;
import com.cjy.contenthub.common.api.dto.aniist.AniListResponseDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbMovieDetailsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbTvDetailsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbWatchProvidersDto;
import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.util.GraphqlUtil;
import com.cjy.contenthub.common.util.SessionUtil;
import com.cjy.contenthub.detail.controller.dto.DetailComicsResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailMovieResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailTvResponseDto;
import com.cjy.contenthub.detail.mapper.DetailMapper;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 상세 화면 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/detail/information")
@RequiredArgsConstructor
@Slf4j
public class DetailInformationController {

	/** 상세 매퍼 */
	private final DetailMapper mapper;

	/** 공통 세션 유팅 */
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
	
	/** AniList API 상세화면 캐릭터 표시 개수 */
	@Value("${anilist.custom.perCharacterPage}")
	private int anilistPerCharacterPage;

	/** 리퀘스트 파라미터 키 : TV SERIES ID */
	private static final String PARAM_TV_SERIES_ID = "series_id";

	/** 리퀘스트 파라미터 키 : MOVIE ID */
	private static final String PARAM_MOVIE_ID = "movie_id";

	/** 리퀘스트 파라미터 키 : COMICS ID */
	private static final String PARAM_COMICS_ID = "comics_id";

	/** 리퀘스트 파라미터 키 : 언어 */
	private static final String PARAM_LANGUAGE = "language";

	/** 리퀘스트 파라미터 키 : append_to_response */
	private static final String PARAM_APPEND_TO_RESPONSE = "append_to_response";

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

	/** 크레딧 : credits */
	private static final String CREDITS = "credits";
	
	/** 종합 크레딧 : aggregate credits */
	private static final String AGGREGATE_CREDITS = "aggregate_credits";

	/** 첫번째 페이지 번호 */
	private static final int FIRST_PAGE_NO = 1;

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
						.queryParam(PARAM_APPEND_TO_RESPONSE, AGGREGATE_CREDITS)
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
	 * @param page 페이지 번호
	 * @return Comics 상세 응답 DTO
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@GetMapping(value = "/getComicsDetail")
	public ResponseEntity<DetailComicsResponseDto> getComicsDetail(
			@NotNull @RequestParam(PARAM_COMICS_ID) Integer comicsId,
			@Nullable @RequestParam(PARAM_PAGE) Integer page
			) throws IOException {

		// GraphQL 쿼리 파일 불러오기
		String query = GraphqlUtil.loadQuery("comics.graphql");
		// 리퀘스트 파라미터 작성
		Map<String, Object> variables = new HashMap<>(Map.of(
				PARAM_MEDIA_ID, comicsId,
				PARAM_PAGE, Optional.ofNullable(page).orElse(FIRST_PAGE_NO),
				PARAM_PER_PAGE, anilistPerCharacterPage
				));
		// 성인물 포함 플래그
		boolean isAdult = session.getSessionBooleanValue(CommonConstants.ADULT_FLG);
		// 성인물 플래그가 false인 경우, 파라미터 추가
		if (!isAdult) {
			variables.put(PARAM_IS_ADULT, isAdult);
		}
		// 쿼리에 리퀘스트 파라미터 적용하여 문자열 생성
		String requestBody = GraphqlUtil.buildRequestBody(query, variables);

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
}
