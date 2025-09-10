package com.cjy.contenthub.detail.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.common.api.dto.aniist.AniListCharactersDto;
import com.cjy.contenthub.common.api.dto.aniist.AniListMediaDto;
import com.cjy.contenthub.common.api.dto.aniist.AniListResponseDto;
import com.cjy.contenthub.common.api.dto.aniist.AniListStaffDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbMovieDetailsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbTvDetailsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbWatchProvidersDto;
import com.cjy.contenthub.common.constants.AnilistParamConstants;
import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.SortEnum;
import com.cjy.contenthub.common.constants.TmdbParamConstants;
import com.cjy.contenthub.common.util.GraphqlUtil;
import com.cjy.contenthub.detail.controller.dto.DetailComicsResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailMovieResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailTvResponseDto;
import com.cjy.contenthub.detail.helper.DetailInformationHelper;
import com.cjy.contenthub.detail.mapper.DetailMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 상세 정보 서비스 구현 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DetailInformationServiceImpl implements DetailInformationService {

	/** 상세 매퍼 */
	private final DetailMapper mapper;

	/** 상세 정보 헬퍼 */
	private final DetailInformationHelper detailInformationHelper;

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

	/**
	 * TMDB TV 상세 조회
	 * 
	 * @param seriesId TV 시리즈 ID
	 * @param originalMediaType 원본 미디어 타입
	 * @param userId 유저 테이블 ID
	 * @return TV 상세 응답 DTO
	 */
	@Override
	@Cacheable(value = "tvDetailCache", key = "#seriesId + '-' + #originalMediaType + '-' + #userId", unless = "#result == null")
	public DetailTvResponseDto getTvDetail(Integer seriesId, String originalMediaType, Long userId) {

		// TMDB TV 상세 조회
		Mono<TmdbTvDetailsDto> detailMono = tmdbWebClient.get()
				.uri(builder -> builder
						.path(String.format(tvDetailPath, seriesId))
						.queryParam(TmdbParamConstants.PARAM_TV_SERIES_ID, seriesId)
						.queryParam(TmdbParamConstants.PARAM_APPEND_TO_RESPONSE, TmdbParamConstants.AGGREGATE_CREDITS)
						.queryParam(TmdbParamConstants.PARAM_LANGUAGE, TmdbParamConstants.LANGUAGE_KOREAN)
						.build())
				.retrieve()
				.bodyToMono(TmdbTvDetailsDto.class);
		// TMDB 시청 제공자 조회
		Mono<TmdbWatchProvidersDto> watchProvidersMono = tmdbWebClient.get()
				.uri(builder -> builder
						.path(String.format(tvWatchProvidersPath, seriesId))
						.queryParam(TmdbParamConstants.PARAM_TV_SERIES_ID, seriesId)
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

			// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
			if (userId != null) {
				response.setWishlisted(detailInformationHelper.setWishlisted(userId, originalMediaType, String.valueOf(seriesId)));
			}

			// 응답 DTO 반환
			return response;
		}).block();
	}

	/**
	 * TMDB 영화 상세 조회
	 * 
	 * @param movieId 영화 ID
	 * @param originalMediaType 원본 미디어 타입
	 * @param userId 유저 테이블 ID
	 * @return ResponseEntity<DetailMovieResponseDto> 영화 상세 응답 DTO
	 */
	@Override
	@Cacheable(value = "movieDetailCache", key = "#movieId + '-' + #originalMediaType + '-' + #userId", unless = "#result == null")
	public DetailMovieResponseDto getMovieDetail(Integer movieId, String originalMediaType, Long userId) {

		// TMDB 영화 상세 조회
		Mono<TmdbMovieDetailsDto> detailMono = tmdbWebClient.get()
				.uri(builder -> builder
						.path(String.format(movieDetailPath, movieId))
						.queryParam(TmdbParamConstants.PARAM_MOVIE_ID, movieId)
						.queryParam(TmdbParamConstants.PARAM_APPEND_TO_RESPONSE, TmdbParamConstants.CREDITS)
						.queryParam(TmdbParamConstants.PARAM_LANGUAGE, TmdbParamConstants.LANGUAGE_KOREAN)
						.build())
				.retrieve()
				.bodyToMono(TmdbMovieDetailsDto.class);

		// TMDB 시청 제공자 취득
		Mono<TmdbWatchProvidersDto> watchProvidersMono = tmdbWebClient.get()
				.uri(builder -> builder
						.path(String.format(movieWatchProvidersPath, movieId))
						.queryParam(TmdbParamConstants.PARAM_TV_SERIES_ID, movieId)
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
			DetailMovieResponseDto response = mapper.detailMovieToDetailMovieResponse(detailResponse);
			response.setLink(link);

			// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
			if (userId != null) {
				response.setWishlisted(detailInformationHelper.setWishlisted(userId, originalMediaType, String.valueOf(movieId)));
			}

			// 응답 DTO 반환
			return response;
		}).block();
	}

	/**
	 * AniList Comics 상세 조회
	 * 
	 * @param comicsId 만화 ID
	 * @param originalMediaType 원본 미디어 타입
	 * @param userId 유저 테이블 ID
	 * @return Comics 상세 응답 DTO
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@Override
	@Cacheable(value = "comicsDetailCache", key = "#comicsId + '-' + #originalMediaType + '-' + #userId", unless = "#result == null")
	public DetailComicsResponseDto getComicsDetail(Integer comicsId, String originalMediaType, Long userId) throws IOException {

		// GraphQL 쿼리 파일 불러오기
		String query = GraphqlUtil.loadQuery("comicsDetail.graphql");
		// 리퀘스트 파라미터 작성
		Map<String, Object> variables = new HashMap<>(Map.of(
				AnilistParamConstants.PARAM_MEDIA_ID, comicsId,
				AnilistParamConstants.PARAM_PAGE, CommonConstants.FIRST_PAGE_NO,
				AnilistParamConstants.PARAM_PER_PAGE, anilistPerCharacterPage,
				AnilistParamConstants.PARAM_STAFF_PAGE, CommonConstants.FIRST_PAGE_NO,
				AnilistParamConstants.PARAM_STAFF_PERPAGE, anilistPerCharacterPage,
				AnilistParamConstants.PARAM_SORT, List.of(SortEnum.ID),
				AnilistParamConstants.PARAM_STAFF_SORT, List.of(SortEnum.ID)
				));
		// 쿼리에 리퀘스트 파라미터 적용하여 문자열 생성
		String requestBody = GraphqlUtil.buildRequestBody(query, variables);

		// AniList 만화 정보 API 조회
		return anilistWebClient.post()
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(AniListResponseDto.class)
				.map(response -> {
					// 응답 데이터 유효성 검사
					if (response == null || response.getData() == null || response.getData().getMedia() == null) {
						log.error("만화 작품정보 조회 실패 - comicsId: {}", comicsId);
						return new DetailComicsResponseDto();
					}
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
							.staff(media.getStaff())
							.startDate(startDate)
							.build();

					// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
					if (userId != null) {
						comicsResponse.setWishlisted(detailInformationHelper.setWishlisted(userId, originalMediaType, String.valueOf(comicsId)));
					}

					// 응답 DTO 반환
					return comicsResponse;
				})
				.block();
	}

	/**
	 * AniList Comics 캐릭터 리스트 조회
	 * 
	 * @param comicsId Comics ID
	 * @param page     페이지 번호
	 * @return 캐릭터 DTO
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@Override
	@Cacheable(value = "comicsCharacterListCache", key = "'character_' + #comicsId + '-' + #page", unless = "#result == null")
	public AniListCharactersDto getComicsCharacterList(Integer comicsId, Integer page) throws IOException {

		// GraphQL 쿼리 파일 불러오기
		String query = GraphqlUtil.loadQuery("comicsCharacterList.graphql");
		// 리퀘스트 파라미터 작성
		Map<String, Object> variables = new HashMap<>(Map.of(
				AnilistParamConstants.PARAM_MEDIA_ID, comicsId,
				AnilistParamConstants.PARAM_PAGE, Optional.ofNullable(page).orElse(CommonConstants.FIRST_PAGE_NO),
				AnilistParamConstants.PARAM_PER_PAGE, anilistPerCharacterPage,
				AnilistParamConstants.PARAM_SORT, List.of(SortEnum.ID)
				));
		// 쿼리에 리퀘스트 파라미터 적용하여 문자열 생성
		String requestBody = GraphqlUtil.buildRequestBody(query, variables);

		// AniList 캐릭터 정보 API 조회
		return anilistWebClient.post()
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(AniListResponseDto.class)
				.map(reponse -> {
					// 응답 데이터 유효성 검사
					if (reponse == null || reponse.getData() == null || reponse.getData().getMedia() == null) {
						log.error("만화 캐릭터 정보 조회 실패 - comicsId: {}", comicsId);
						return new AniListCharactersDto();
					}
					// 응답 데이터 재분배
					AniListMediaDto media = reponse.getData().getMedia();

					// 캐릭터 DTO 반환
					return media.getCharacters();
				})
				.block();
	}

	/**
	 * AniList Comics 스태프 리스트 조회
	 * 
	 * @param comicsId Comics ID
	 * @param page     페이지 번호
	 * @return 스태프 DTO
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@Override
	@Cacheable(value = "comicsStaffListCache", key = "'staff_' + #comicsId + '-' + #page", unless = "#result == null")
	public AniListStaffDto getComicsStaffList(Integer comicsId, Integer page) throws IOException {

		// GraphQL 쿼리 파일 불러오기
		String query = GraphqlUtil.loadQuery("comicsStaffList.graphql");
		// 리퀘스트 파라미터 작성
		Map<String, Object> variables = new HashMap<>(Map.of(
				AnilistParamConstants.PARAM_MEDIA_ID, comicsId,
				AnilistParamConstants.PARAM_STAFF_PAGE, Optional.ofNullable(page).orElse(CommonConstants.FIRST_PAGE_NO),
				AnilistParamConstants.PARAM_STAFF_PERPAGE, anilistPerCharacterPage,
				AnilistParamConstants.PARAM_STAFF_SORT, List.of(SortEnum.ID)
				));
		// 쿼리에 리퀘스트 파라미터 적용하여 문자열 생성
		String requestBody = GraphqlUtil.buildRequestBody(query, variables);

		// AniList 스태프 정보 API 조회
		return anilistWebClient.post()
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(AniListResponseDto.class)
				.map(reponse -> {
					// 응답 데이터 유효성 검사
					if (reponse == null || reponse.getData() == null || reponse.getData().getMedia() == null) {
						log.error("만화 스태프 정보 조회 실패 - comicsId: {}", comicsId);
						return new AniListStaffDto();
					}
					// 응답 데이터 재분배
					AniListMediaDto media = reponse.getData().getMedia();

					// 캐릭터 DTO 반환
					return media.getStaff();
				})
				.block();
	}

}
