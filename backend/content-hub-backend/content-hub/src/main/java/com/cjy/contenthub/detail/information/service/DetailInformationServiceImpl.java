package com.cjy.contenthub.detail.information.service;

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

import com.cjy.contenthub.common.integration.anilist.constants.AniListParamConstants;
import com.cjy.contenthub.common.integration.anilist.dto.AniListCharactersDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListMediaDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListResponseDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListStaffDto;
import com.cjy.contenthub.common.integration.tmdb.constants.TmdbParamConstants;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbGenreDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbMovieDetailsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbTvDetailsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbVideoCreditsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbWatchProvidersDto;
import com.cjy.contenthub.common.util.GraphqlUtil;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.core.constants.CacheNames;
import com.cjy.contenthub.core.constants.DomainConstants;
import com.cjy.contenthub.core.constants.DomainEnum.DomainMessagesWarnEnum;
import com.cjy.contenthub.core.constants.DomainEnum.SortEnum;
import com.cjy.contenthub.core.shared.service.GenreSharedService;
import com.cjy.contenthub.detail.information.controller.dto.DetailComicsResponseDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailMovieResponseDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailTvResponseDto;
import com.cjy.contenthub.detail.information.mapper.DetailInformationMapper;

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
	private final DetailInformationMapper detailInformationMapper;
	
	/** 장르 공유 서비스 */
	private final GenreSharedService genreSharedService;
	
	/** 메시지 유틸리티 */
	private final MessageUtil messageUtil;

	/** TMDB API 통신용 WebClient 클래스 */
	@Qualifier("tmdbWebClient")
	private final WebClient tmdbWebClient;

	/** AniList API 통신용 WebClient 클래스 */
	@Qualifier("anilistWebClient")
	private final WebClient anilistWebClient;

	/** TMDB API TV Detail API 패스 */
	@Value("${tmdb.url.tv-detail-path}")
	private String tvDetailPath;
	
	/** TMDB API TV Credits API 패스 */
	@Value("${tmdb.url.tv-credits-path}")
	private String tvCreditsPath;

	/** TMDB API Movie Detail API 패스 */
	@Value("${tmdb.url.movie-detail-path}")
	private String movieDetailPath;
	
	/** TMDB API Movie Credits API 패스 */
	@Value("${tmdb.url.movie-credits-path}")
	private String movieCreditsPath;

	/** TMDB API TV Watch Providers API 패스 */
	@Value("${tmdb.url.tv-watch-providers-path}")
	private String tvWatchProvidersPath;

	/** TMDB API Movie Watch Providers API 패스 */
	@Value("${tmdb.url.movie-watch-providers-path}")
	private String movieWatchProvidersPath;

	/** TMDB API TV 추천 작품 API 패스 */
	@Value("${tmdb.url.tv-recommendations-path}")
	private String tvRecommendationsPath;

	/** TMDB API Movie 추천 작품 API 패스 */
	@Value("${tmdb.url.movie-recommendations-path}")
	private String movieRecommendationsPath;

	/** TMDB API TV 비슷한 작품 API 패스 */
	@Value("${tmdb.url.tv-similar-path}")
	private String tvSimilarPath;

	/** TMDB API Movie 비슷한 작품 API 패스 */
	@Value("${tmdb.url.movie-similar-path}")
	private String movieSimilarPath;

	/** AniList API 전체보기화면 작품 표시 개수 */
	@Value("${anilist.custom.per-more-page}")
	private int anilistPerMorePage;

	/** AniList API 상세화면 캐릭터 표시 개수 */
	@Value("${anilist.custom.per-character-page}")
	private int anilistPerCharacterPage;

	/**
	 * TMDB TV 상세 조회
	 * 
	 * @param seriesId TV 시리즈 ID
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param userId 유저 테이블 ID
	 * @return TV 상세 응답 DTO
	 */
	@Override
	@Cacheable(value = CacheNames.TV_DETAIL, unless = "#result == null")
	public DetailTvResponseDto getTvDetail(Integer seriesId, String contentMediaType) {

		// TMDB TV 상세 조회
		Mono<TmdbTvDetailsDto> detailMono = tmdbWebClient.get()
				.uri(builder -> builder
						.path(String.format(tvDetailPath, seriesId))
						.queryParam(TmdbParamConstants.PARAM_TV_SERIES_ID, seriesId)
						.queryParam(TmdbParamConstants.PARAM_LANGUAGE, TmdbParamConstants.LANGUAGE_KOREAN)
						.build())
				.retrieve()
				.bodyToMono(TmdbTvDetailsDto.class);
		
		// TMDB TV 크레딧 조회
		Mono<TmdbVideoCreditsDto> creditsMono = tmdbWebClient.get()
				.uri(builder -> builder
						.path(String.format(tvCreditsPath, seriesId))
						.queryParam(TmdbParamConstants.PARAM_TV_SERIES_ID, seriesId)
						.queryParam(TmdbParamConstants.PARAM_LANGUAGE, TmdbParamConstants.LANGUAGE_KOREAN)
						.build())
				.retrieve()
				.bodyToMono(TmdbVideoCreditsDto.class);
		
		// TMDB 시청 제공자 조회
		Mono<TmdbWatchProvidersDto> watchProvidersMono = tmdbWebClient.get()
				.uri(builder -> builder
						.path(String.format(tvWatchProvidersPath, seriesId))
						.queryParam(TmdbParamConstants.PARAM_TV_SERIES_ID, seriesId)
						.build())
				.retrieve()
				.bodyToMono(TmdbWatchProvidersDto.class);

		// TV 상세 조회 결과와 시청 제공자 조회 결과를 병합하여 반환
		return Mono.zip(detailMono, creditsMono, watchProvidersMono).map(tuple -> {
			// TMDB TV 상세 DTO
			TmdbTvDetailsDto detailResponse = tuple.getT1();
			// TMDB TV 크레딧 DTO
			TmdbVideoCreditsDto creditsResponse = tuple.getT2();
			// TMDB 시청 제공자 DTO
			TmdbWatchProvidersDto watchProvidersResponse = tuple.getT3();
			// 시청 제공자 링크 취득
			String link = Optional.ofNullable(watchProvidersResponse.getResults())
					.map(results -> results.getKr())
					.map(getKr -> getKr.getLink())
					.orElse(null);

			// 상세 DTO에 크레딧 설정
			detailResponse.setCredits(creditsResponse);
			// 반환값 설정
			DetailTvResponseDto response = detailInformationMapper.detailTvToDetailTvResponse(detailResponse);
			response.setLink(link);
			response.setGenreIds(detailResponse.getGenres().stream().map(TmdbGenreDto::getId).toList());

			// 응답 DTO 반환
			return response;
		}).block();
	}

	/**
	 * TMDB 영화 상세 조회
	 * 
	 * @param movieId 영화 ID
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @return ResponseEntity<DetailMovieResponseDto> 영화 상세 응답 DTO
	 */
	@Override
	@Cacheable(value = CacheNames.MOVIE_DETAIL, unless = "#result == null")
	public DetailMovieResponseDto getMovieDetail(Integer movieId, String contentMediaType) {

		// TMDB 영화 상세 조회
		Mono<TmdbMovieDetailsDto> detailMono = tmdbWebClient.get()
				.uri(builder -> builder
						.path(String.format(movieDetailPath, movieId))
						.queryParam(TmdbParamConstants.PARAM_MOVIE_ID, movieId)
						.queryParam(TmdbParamConstants.PARAM_LANGUAGE, TmdbParamConstants.LANGUAGE_KOREAN)
						.build())
				.retrieve()
				.bodyToMono(TmdbMovieDetailsDto.class);
		
		// TMDB 영화 크레딧 조회
		Mono<TmdbVideoCreditsDto> creditsMono = tmdbWebClient.get()
				.uri(builder -> builder
						.path(String.format(movieCreditsPath, movieId))
						.queryParam(TmdbParamConstants.PARAM_MOVIE_ID, movieId)
						.queryParam(TmdbParamConstants.PARAM_LANGUAGE, TmdbParamConstants.LANGUAGE_KOREAN)
						.build())
				.retrieve()
				.bodyToMono(TmdbVideoCreditsDto.class);

		// TMDB 시청 제공자 취득
		Mono<TmdbWatchProvidersDto> watchProvidersMono = tmdbWebClient.get()
				.uri(builder -> builder
						.path(String.format(movieWatchProvidersPath, movieId))
						.queryParam(TmdbParamConstants.PARAM_MOVIE_ID, movieId)
						.build())
				.retrieve()
				.bodyToMono(TmdbWatchProvidersDto.class);

		// 영화 상세 조회 결과와 시청 제공자 조회 결과를 병합하여 반환 
		return Mono.zip(detailMono, creditsMono, watchProvidersMono).map(tuple -> {
			// TMDB 영화 상세 DTO
			TmdbMovieDetailsDto detailResponse = tuple.getT1();
			// TMDB 영화 크레딧 DTO
			TmdbVideoCreditsDto creditsResponse = tuple.getT2();
			// TMDB 시청 제공자 DTO
			TmdbWatchProvidersDto watchProvidersResponse = tuple.getT3();
			// 시청 제공자 링크 취득
			String link = Optional.ofNullable(watchProvidersResponse.getResults())
					.map(results -> results.getKr())
					.map(getKr -> getKr.getLink())
					.orElse(null);
			
			// 상세 DTO에 크레딧 설정
			detailResponse.setCredits(creditsResponse);
			// 반환값 설정
			DetailMovieResponseDto response = detailInformationMapper.detailMovieToDetailMovieResponse(detailResponse);
			response.setLink(link);
			response.setGenreIds(detailResponse.getGenres().stream().map(TmdbGenreDto::getId).toList());

			// 응답 DTO 반환
			return response;
		}).block();
	}

	/**
	 * AniList Comics 상세 조회
	 * 
	 * @param comicsId 만화 ID
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @return Comics 상세 응답 DTO
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@Override
	@Cacheable(value = CacheNames.COMICS_DETAIL, unless = "#result == null")
	public DetailComicsResponseDto getComicsDetail(Integer comicsId, String contentMediaType) throws IOException {

		// GraphQL 쿼리 파일 불러오기
		String query = GraphqlUtil.loadQuery("comicsDetail.graphql");
		// 리퀘스트 파라미터 작성
		Map<String, Object> variables = new HashMap<>(Map.of(
				AniListParamConstants.PARAM_MEDIA_ID, comicsId,
				AniListParamConstants.PARAM_PAGE, DomainConstants.FIRST_PAGE_NO,
				AniListParamConstants.PARAM_PER_PAGE, anilistPerCharacterPage,
				AniListParamConstants.PARAM_STAFF_PAGE, DomainConstants.FIRST_PAGE_NO,
				AniListParamConstants.PARAM_STAFF_PERPAGE, anilistPerCharacterPage,
				AniListParamConstants.PARAM_SORT, List.of(SortEnum.ID),
				AniListParamConstants.PARAM_STAFF_SORT, List.of(SortEnum.ID)
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
						Object[] messageParams = { comicsId };
						log.warn(messageUtil.getMessageKO(
								DomainMessagesWarnEnum.WARN_DETAIL_INFORMATION_COMICS_NOT_FOUND.getMessageCode(), messageParams));
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
							.genreIds(genreSharedService.genreMappingFromAniListToTmdb(media.getGenres()))
							.adult(media.isAdult())
							.volumes(media.getVolumes())
							.chapters(media.getChapters())
							.status(media.getStatus())
							.homepage(media.getSiteUrl())
							.title(media.getTitle().getUserPreferred())
							.synonyms(media.getSynonyms())
							.backdropPath(media.getCoverImage().getLarge())
							.posterPath(media.getCoverImage().getExtraLarge())
							.characters(media.getCharacters())
							.staff(media.getStaff())
							.startDate(startDate)
							.build();

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
	@Cacheable(value = CacheNames.COMICS_CHARACTER_LIST, unless = "#result == null")
	public AniListCharactersDto getComicsCharacterList(Integer comicsId, Integer page) throws IOException {

		// GraphQL 쿼리 파일 불러오기
		String query = GraphqlUtil.loadQuery("comicsCharacterList.graphql");
		// 리퀘스트 파라미터 작성
		Map<String, Object> variables = new HashMap<>(Map.of(
				AniListParamConstants.PARAM_MEDIA_ID, comicsId,
				AniListParamConstants.PARAM_PAGE, Optional.ofNullable(page).orElse(DomainConstants.FIRST_PAGE_NO),
				AniListParamConstants.PARAM_PER_PAGE, anilistPerCharacterPage,
				AniListParamConstants.PARAM_SORT, List.of(SortEnum.ID)
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
						Object[] messageParams = { comicsId };
						log.warn(messageUtil.getMessageKO(
								DomainMessagesWarnEnum.WARN_DETAIL_INFORMATION_CHARACTERS_NOT_FOUND.getMessageCode(), messageParams));
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
	@Cacheable(value = CacheNames.COMICS_STAFF_LIST, unless = "#result == null")
	public AniListStaffDto getComicsStaffList(Integer comicsId, Integer page) throws IOException {

		// GraphQL 쿼리 파일 불러오기
		String query = GraphqlUtil.loadQuery("comicsStaffList.graphql");
		// 리퀘스트 파라미터 작성
		Map<String, Object> variables = new HashMap<>(Map.of(
				AniListParamConstants.PARAM_MEDIA_ID, comicsId,
				AniListParamConstants.PARAM_STAFF_PAGE, Optional.ofNullable(page).orElse(DomainConstants.FIRST_PAGE_NO),
				AniListParamConstants.PARAM_STAFF_PERPAGE, anilistPerCharacterPage,
				AniListParamConstants.PARAM_STAFF_SORT, List.of(SortEnum.ID)
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
						Object[] messageParams = { comicsId };
						log.warn(messageUtil.getMessageKO(
								DomainMessagesWarnEnum.WARN_DETAIL_INFORMATION_STAFF_NOT_FOUND.getMessageCode(), messageParams));
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
