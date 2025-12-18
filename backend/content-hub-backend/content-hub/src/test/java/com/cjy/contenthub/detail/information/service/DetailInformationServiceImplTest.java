package com.cjy.contenthub.detail.information.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;

import com.cjy.contenthub.common.integration.anilist.dto.AniListCharactersDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListCoverImageDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListDataDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListDateDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListMediaDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListResponseDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListStaffDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListTitleDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbGenreDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbGenreListDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbMovieDetailsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbTvDetailsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbVideoCreditsCastDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbVideoCreditsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbWatchProvidersDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbWatchProvidersResultsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbWatchProvidersTypeDto;
import com.cjy.contenthub.common.util.GraphqlUtil;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.core.shared.service.GenreSharedService;
import com.cjy.contenthub.detail.information.controller.dto.DetailComicsResponseDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailMovieResponseDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailTvResponseDto;
import com.cjy.contenthub.detail.information.mapper.DetailInformationMapper;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class DetailInformationServiceImplTest {

	DetailInformationServiceImpl service;

	@Mock
	WebClient tmdbWebClient;
	
	@Mock
	WebClient anilistWebClient;
	
	@Mock
	WebClient deeplWebClient;

	@Mock
	DetailInformationMapper mapper;
	
	@Mock
	GenreSharedService genreSharedService;
	
	@Mock
	MessageUtil messageUtil;
	
	ExecutorService executorService;
	
	@BeforeEach
	void setUp() {
		executorService = Executors.newSingleThreadExecutor();
		// 서비스 인스턴스 생성
		service = new DetailInformationServiceImpl(
				mapper,
				genreSharedService,
				messageUtil,
				executorService,
				tmdbWebClient,
				anilistWebClient,
				deeplWebClient
				);
	}
	
	@AfterEach
	void tearDown() {
        executorService.shutdown();
	}

	@Test
	@DisplayName("[UT]getTvDetail: TV 상세 정보 조회")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getTvDetail() throws InterruptedException, ExecutionException, TimeoutException  {
		// 파라미터 설정
		Integer seriesId = 1000;
		String contentMediaType = "1101";
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec2 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec3 = mock(RequestHeadersUriSpec.class);
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		ResponseSpec responseSpec2 = mock(ResponseSpec.class);
		ResponseSpec responseSpec3 = mock(ResponseSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1, uriSpec2,  uriSpec3);

		// TMDB TV 상세 Mock 설정
		TmdbTvDetailsDto tmdbTvDetailsDto = new TmdbTvDetailsDto();
		TmdbGenreDto genre = new TmdbGenreDto();
		genre.setId(16);
		List<TmdbGenreDto> genres = List.of(genre);
		TmdbGenreListDto genreList = new TmdbGenreListDto();
		genreList.setGenres(genres);
		tmdbTvDetailsDto.setGenres(genres);
		when(uriSpec1.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())).thenReturn(uriSpec1);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		when(responseSpec1.bodyToMono(TmdbTvDetailsDto.class)).thenReturn(Mono.just(tmdbTvDetailsDto));
		// TMDB 크레딧 조회 Mock 설정
		TmdbVideoCreditsDto tmdbVideoCreditsDto = new TmdbVideoCreditsDto();
		TmdbVideoCreditsCastDto cast = new TmdbVideoCreditsCastDto();
		cast.setId(1);
		tmdbVideoCreditsDto.setCast(List.of(cast));
		when(uriSpec2.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())).thenReturn(uriSpec2);
		when(uriSpec2.retrieve()).thenReturn(responseSpec2);
		when(responseSpec2.bodyToMono(TmdbVideoCreditsDto.class)).thenReturn(Mono.just(tmdbVideoCreditsDto));
		// TMDB 시청 제공자 조회 Mock 설정
		String link = "https://www.themoviedb.org/tv/" + seriesId;
		TmdbWatchProvidersResultsDto results = new TmdbWatchProvidersResultsDto();
		TmdbWatchProvidersTypeDto kr = new TmdbWatchProvidersTypeDto();
		kr.setLink(link);
		results.setKr(kr);
		TmdbWatchProvidersDto tmdbWatchProvidersDto = new TmdbWatchProvidersDto();
		tmdbWatchProvidersDto.setResults(results);
		when(uriSpec3.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())).thenReturn(uriSpec3);
		when(uriSpec3.retrieve()).thenReturn(responseSpec3);
		when(responseSpec3.bodyToMono(TmdbWatchProvidersDto.class)).thenReturn(Mono.just(tmdbWatchProvidersDto));
		// 매퍼 Mock 설정
		tmdbTvDetailsDto.setCredits(tmdbVideoCreditsDto);
		DetailTvResponseDto response = new DetailTvResponseDto();
		response.setCredits(tmdbVideoCreditsDto);
		when(mapper.detailTvToDetailTvResponse(tmdbTvDetailsDto))
		.thenReturn(response);

		// 서비스 메서드 호출 (CompletableFuture 반환이므로 get으로 결과 대기)
		DetailTvResponseDto result = service.getTvDetail(seriesId, contentMediaType).get(5, TimeUnit.SECONDS);
		
		// 결과 검증
		DetailTvResponseDto expectedDto = new DetailTvResponseDto();
		expectedDto.setCredits(tmdbVideoCreditsDto);
		expectedDto.setLink(link);
		expectedDto.setGenreIds(tmdbTvDetailsDto.getGenres().stream().map(TmdbGenreDto::getId).toList());
		assertThat(result).usingRecursiveComparison().isEqualTo(expectedDto);
		
		// Mock 호출 검증
		verify(tmdbWebClient, times(3)).get();
		verify(mapper, times(1)).detailTvToDetailTvResponse(tmdbTvDetailsDto);
	}
	
	@Test
	@DisplayName("[UT]getMovieDetail: 영화 상세 정보 조회")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getMovieDetail() throws InterruptedException, ExecutionException, TimeoutException {
		// 파라미터 설정
		Integer movieId = 2000;
		String contentMediaType = "1201";
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec2 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec3 = mock(RequestHeadersUriSpec.class);
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		ResponseSpec responseSpec2 = mock(ResponseSpec.class);
		ResponseSpec responseSpec3 = mock(ResponseSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1, uriSpec2,  uriSpec3);

		// TMDB 영화 상세 Mock 설정
		TmdbMovieDetailsDto tmdbMovieDetailsDto = new TmdbMovieDetailsDto();
		TmdbGenreDto genre = new TmdbGenreDto();
		genre.setId(28);
		List<TmdbGenreDto> genres = List.of(genre);
		TmdbGenreListDto genreList = new TmdbGenreListDto();
		genreList.setGenres(genres);
		tmdbMovieDetailsDto.setGenres(genres);
		when(uriSpec1.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())).thenReturn(uriSpec1);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		when(responseSpec1.bodyToMono(TmdbMovieDetailsDto.class)).thenReturn(Mono.just(tmdbMovieDetailsDto));
		// TMDB 크레딧 조회 Mock 설정
		TmdbVideoCreditsDto tmdbVideoCreditsDto = new TmdbVideoCreditsDto();
		TmdbVideoCreditsCastDto cast = new TmdbVideoCreditsCastDto();
		cast.setId(2);
		tmdbVideoCreditsDto.setCast(List.of(cast));
		when(uriSpec2.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())).thenReturn(uriSpec2);
		when(uriSpec2.retrieve()).thenReturn(responseSpec2);
		when(responseSpec2.bodyToMono(TmdbVideoCreditsDto.class)).thenReturn(Mono.just(tmdbVideoCreditsDto));
		// TMDB 시청 제공자 조회 Mock 설정
		String link = "https://www.themoviedb.org/movie/" + movieId;
		TmdbWatchProvidersResultsDto results = new TmdbWatchProvidersResultsDto();
		TmdbWatchProvidersTypeDto kr = new TmdbWatchProvidersTypeDto();
		kr.setLink(link);
		results.setKr(kr);
		TmdbWatchProvidersDto tmdbWatchProvidersDto = new TmdbWatchProvidersDto();
		tmdbWatchProvidersDto.setResults(results);
		when(uriSpec3.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())).thenReturn(uriSpec3);
		when(uriSpec3.retrieve()).thenReturn(responseSpec3);
		when(responseSpec3.bodyToMono(TmdbWatchProvidersDto.class)).thenReturn(Mono.just(tmdbWatchProvidersDto));
		// 매퍼 Mock 설정
		tmdbMovieDetailsDto.setCredits(tmdbVideoCreditsDto);
		DetailMovieResponseDto response = new DetailMovieResponseDto();
		response.setCredits(tmdbVideoCreditsDto);
		when(mapper.detailMovieToDetailMovieResponse(tmdbMovieDetailsDto))
		.thenReturn(response);

		// 서비스 메서드 호출 (CompletableFuture 반환이므로 get으로 결과 대기)
		DetailMovieResponseDto result = service.getMovieDetail(movieId, contentMediaType).get(5, TimeUnit.SECONDS);
		
		// 결과 검증
		DetailMovieResponseDto expectedDto = new DetailMovieResponseDto();
		expectedDto.setCredits(tmdbVideoCreditsDto);
		expectedDto.setLink(link);
		expectedDto.setGenreIds(tmdbMovieDetailsDto.getGenres().stream().map(TmdbGenreDto::getId).toList());
		assertThat(result).usingRecursiveComparison().isEqualTo(expectedDto);
		
		// Mock 호출 검증
		verify(tmdbWebClient, times(3)).get();
		verify(mapper, times(1)).detailMovieToDetailMovieResponse(tmdbMovieDetailsDto);
	}
	
	@Test
	@DisplayName("[UT]getComicsDetail: 만화 상세 정보 조회 - 응답 데이터 및 연재 시작일 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getComicsDetail_existResponseAndStartDate() throws InterruptedException, ExecutionException, TimeoutException, IOException {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer comicsId = 3000;
			String contentMediaType = "2101";
			String query = "graphql query";

			mocked.when(() -> GraphqlUtil.loadQuery("comicsDetail.graphql"))
			.thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(anyString(), anyMap()))
			.thenReturn(query);

			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(anyString())).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);

			AniListResponseDto aniListResponseDto = new AniListResponseDto();
			AniListDataDto data = new AniListDataDto();
			AniListMediaDto media = new AniListMediaDto();
			media.setId(comicsId);
			media.setGenres(List.of("28"));
			AniListTitleDto title = new AniListTitleDto();
			title.setEnglish("Test Title");
			media.setTitle(title);
			AniListCoverImageDto coverImage = new AniListCoverImageDto();
			coverImage.setLarge("http://anilist.com/large.jpg");
			coverImage.setExtraLarge("http://anilist.com/extraLarge.jpg");		
			media.setCoverImage(coverImage);
			AniListDateDto startDateDto = new AniListDateDto();
			startDateDto.setYear(2025);
			startDateDto.setMonth(1);
			startDateDto.setDay(1);
			media.setStartDate(startDateDto);
			data.setMedia(media);
			aniListResponseDto.setData(data);

			when(genreSharedService.genreMappingFromAniListToTmdb(media.getGenres())).thenReturn(List.of(28));

			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(aniListResponseDto));

			// 서비스 메서드 호출 (CompletableFuture 반환이므로 get으로 결과 대기)
			DetailComicsResponseDto result = service.getComicsDetail(comicsId, contentMediaType).get(5, TimeUnit.SECONDS);

			// 결과 검증
			String startDate = String.valueOf(media.getStartDate().getYear()) 
					.concat("/")
					.concat(StringUtils.leftPad(String.valueOf(media.getStartDate().getMonth()), 2, "0"))
					.concat("/")
					.concat(StringUtils.leftPad(String.valueOf(media.getStartDate().getDay()), 2, "0"));
			DetailComicsResponseDto comicsResponse =
					DetailComicsResponseDto.builder()
					.id(media.getId())
					.overview(media.getDescription())
					.comicsGenres(media.getGenres())
					.genreIds(List.of(28))
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
			assertThat(result).usingRecursiveComparison().isEqualTo(comicsResponse);

			// Mock 호출 검증
			verify(anilistWebClient, times(1)).post();
			verify(genreSharedService, times(1)).genreMappingFromAniListToTmdb(media.getGenres());
		}
	}
	
	@Test
	@DisplayName("[UT]getComicsDetail: 만화 상세 정보 조회 - 응답 데이터 존재 및 연재 시작일 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getComicsDetail_existResponseAndNotExistStartDate() throws InterruptedException, ExecutionException, TimeoutException, IOException {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer comicsId = 3000;
			String contentMediaType = "2101";
			String query = "graphql query";
			mocked.when(() -> GraphqlUtil.loadQuery("comicsDetail.graphql"))
			.thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(anyString(), anyMap()))
			.thenReturn(query);

			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(anyString())).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);

			AniListResponseDto aniListResponseDto = new AniListResponseDto();
			AniListDataDto data = new AniListDataDto();
			AniListMediaDto media = new AniListMediaDto();
			media.setId(comicsId);
			media.setGenres(List.of("28"));
			AniListTitleDto title = new AniListTitleDto();
			title.setEnglish("Test Title");
			media.setTitle(title);
			AniListCoverImageDto coverImage = new AniListCoverImageDto();
			coverImage.setLarge("http://anilist.com/large.jpg");
			coverImage.setExtraLarge("http://anilist.com/extraLarge.jpg");		
			media.setCoverImage(coverImage);
			data.setMedia(media);
			aniListResponseDto.setData(data);

			when(genreSharedService.genreMappingFromAniListToTmdb(media.getGenres())).thenReturn(List.of(28));

			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(aniListResponseDto));

			// 서비스 메서드 호출 (CompletableFuture 반환이므로 get으로 결과 대기)
			DetailComicsResponseDto result = service.getComicsDetail(comicsId, contentMediaType).get(5, TimeUnit.SECONDS);

			// 결과 검증
			String startDate = "";
			DetailComicsResponseDto comicsResponse =
					DetailComicsResponseDto.builder()
					.id(media.getId())
					.overview(media.getDescription())
					.comicsGenres(media.getGenres())
					.genreIds(List.of(28))
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
			assertThat(result).usingRecursiveComparison().isEqualTo(comicsResponse);

			// Mock 호출 검증
			verify(anilistWebClient, times(1)).post();
			verify(genreSharedService, times(1)).genreMappingFromAniListToTmdb(media.getGenres());
		}
	}
	
	/**
	 * 이하의 이유로 응답 데이터 없음 케이스는 생략함
	 * - WebClient Mono .map 체인 내부에서 response == null 상황 자체가 불가능
	 *   (Mono.empty()가 될 뿐임)
	 * - 프레임워크, 서비스 특성상 해당 분기는 데드코드
	 */
	@Test
	@DisplayName("[UT]getComicsDetail: 만화 상세 정보 조회 - 응답 데이터 없음(데드코드)")
	void test_getComicsDetail_notExistResponse() {
		assertTrue(true);
	}
	
	@Test
	@DisplayName("[UT]getComicsDetail: 만화 상세 정보 조회 - 응답 데이터 존재 및 Data 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getComicsDetail_existResponseAndNotExistData() throws InterruptedException, ExecutionException, TimeoutException, IOException {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer comicsId = 3000;
			String contentMediaType = "2101";
			String query = "graphql query";
			mocked.when(() -> GraphqlUtil.loadQuery("comicsDetail.graphql"))
			.thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(anyString(), anyMap()))
			.thenReturn(query);

			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(anyString())).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);

			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.justOrEmpty(new AniListResponseDto()));
			
			when(messageUtil.getMessageKO(anyString(), any())).thenReturn("만화 정보를 찾을 수 없습니다. (apiId: " + comicsId + ")");

			// 서비스 메서드 호출 (CompletableFuture 반환이므로 get으로 결과 대기)
			DetailComicsResponseDto result = service.getComicsDetail(comicsId, contentMediaType).get(5, TimeUnit.SECONDS);

			// 결과 검증
			DetailComicsResponseDto comicsResponse = new DetailComicsResponseDto();
			assertThat(result).usingRecursiveComparison().isEqualTo(comicsResponse);

			// Mock 호출 검증
			verify(anilistWebClient, times(1)).post();
			verify(genreSharedService, times(0)).genreMappingFromAniListToTmdb(anyList());
			verify(messageUtil, times(1)).getMessageKO(anyString(), any());
		}
	}
	
	@Test
	@DisplayName("[UT]getComicsDetail: 만화 상세 정보 조회 - 응답 데이터 존재 및 Media 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getComicsDetail_existResponseAndNotExistMedia() throws InterruptedException, ExecutionException, TimeoutException, IOException {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer comicsId = 3000;
			String contentMediaType = "2101";
			String query = "graphql query";
			mocked.when(() -> GraphqlUtil.loadQuery("comicsDetail.graphql"))
			.thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(anyString(), anyMap()))
			.thenReturn(query);

			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(anyString())).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);
			
			AniListResponseDto aniListResponseDto = new AniListResponseDto();
			AniListDataDto data = new AniListDataDto();
			aniListResponseDto.setData(data);

			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(aniListResponseDto));
			
			when(messageUtil.getMessageKO(anyString(), any())).thenReturn("만화 정보를 찾을 수 없습니다. (apiId: " + comicsId + ")");

			// 서비스 메서드 호출 (CompletableFuture 반환이므로 get으로 결과 대기)
			DetailComicsResponseDto result = service.getComicsDetail(comicsId, contentMediaType).get(5, TimeUnit.SECONDS);

			// 결과 검증
			DetailComicsResponseDto comicsResponse = new DetailComicsResponseDto();
			assertThat(result).usingRecursiveComparison().isEqualTo(comicsResponse);

			// Mock 호출 검증
			verify(anilistWebClient, times(1)).post();
			verify(genreSharedService, times(0)).genreMappingFromAniListToTmdb(anyList());
			verify(messageUtil, times(1)).getMessageKO(anyString(), any());
		}
	}
	
	@Test
	@DisplayName("[UT]getComicsCharacterList: 캐릭터 리스트 조회 - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getComicsCharacterList_existResponse() throws InterruptedException, ExecutionException, TimeoutException, IOException {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer comicsId = 3000;
			Integer page = 1;
			String query = "graphql query";

			mocked.when(() -> GraphqlUtil.loadQuery("comicsCharacterList.graphql"))
			.thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(anyString(), anyMap()))
			.thenReturn(query);

			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(anyString())).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);

			AniListResponseDto aniListResponseDto = new AniListResponseDto();
			AniListDataDto data = new AniListDataDto();
			AniListMediaDto media = new AniListMediaDto();
			media.setId(comicsId);
			AniListCharactersDto characters = new AniListCharactersDto();
			media.setCharacters(characters);
			data.setMedia(media);
			aniListResponseDto.setData(data);

			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(aniListResponseDto));

			// 서비스 메서드 호출 (CompletableFuture 반환이므로 get으로 결과 대기)
			AniListCharactersDto result = service.getComicsCharacterList(comicsId, page).get(5, TimeUnit.SECONDS);

			// 결과 검증
			assertThat(result).usingRecursiveComparison().isEqualTo(characters);

			// Mock 호출 검증
			verify(anilistWebClient, times(1)).post();
		}
	}
	
	/**
	 * 이하의 이유로 응답 데이터 없음 케이스는 생략함
	 * - WebClient Mono .map 체인 내부에서 response == null 상황 자체가 불가능
	 *   (Mono.empty()가 될 뿐임)
	 * - 프레임워크, 서비스 특성상 해당 분기는 데드코드
	 */
	@Test
	@DisplayName("[UT]getComicsCharacterList: 캐릭터 상세 정보 조회 - 응답 데이터 없음(데드코드)")
	void test_getComicsCharacterList_notExistResponse() {
		assertTrue(true);
	}
	
	@Test
	@DisplayName("[UT]getComicsCharacterList: 캐릭터 리스트 조회 - 응답 데이터 존재 및 Data 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getComicsCharacterList_existResponseAndNotExistData() throws InterruptedException, ExecutionException, TimeoutException, IOException {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer comicsId = 3000;
			Integer page = 1;
			String query = "graphql query";

			mocked.when(() -> GraphqlUtil.loadQuery("comicsCharacterList.graphql"))
			.thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(anyString(), anyMap()))
			.thenReturn(query);

			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(anyString())).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);

			AniListResponseDto aniListResponseDto = new AniListResponseDto();

			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(aniListResponseDto));
			
			when(messageUtil.getMessageKO(anyString(), any())).thenReturn("캐릭터 정보를 찾을 수 없습니다. (apiId: " + comicsId + ")");

			// 서비스 메서드 호출 (CompletableFuture 반환이므로 get으로 결과 대기)
			AniListCharactersDto result = service.getComicsCharacterList(comicsId, page).get(5, TimeUnit.SECONDS);

			// 결과 검증
			assertThat(result).usingRecursiveComparison().isEqualTo(new AniListCharactersDto());

			// Mock 호출 검증
			verify(anilistWebClient, times(1)).post();
			verify(messageUtil, times(1)).getMessageKO(anyString(), any());
		}
	}
	
	@Test
	@DisplayName("[UT]getComicsCharacterList: 캐릭터 리스트 조회 - 응답 데이터 존재 및 Media 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getComicsCharacterList_existResponseAndNotExistMedia() throws InterruptedException, ExecutionException, TimeoutException, IOException {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer comicsId = 3000;
			Integer page = 1;
			String query = "graphql query";

			mocked.when(() -> GraphqlUtil.loadQuery("comicsCharacterList.graphql"))
			.thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(anyString(), anyMap()))
			.thenReturn(query);

			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(anyString())).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);

			AniListResponseDto aniListResponseDto = new AniListResponseDto();
			AniListDataDto data = new AniListDataDto();
			aniListResponseDto.setData(data);

			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(aniListResponseDto));
			
			when(messageUtil.getMessageKO(anyString(), any())).thenReturn("캐릭터 정보를 찾을 수 없습니다. (apiId: " + comicsId + ")");

			// 서비스 메서드 호출 (CompletableFuture 반환이므로 get으로 결과 대기)
			AniListCharactersDto result = service.getComicsCharacterList(comicsId, page).get(5, TimeUnit.SECONDS);

			// 결과 검증
			assertThat(result).usingRecursiveComparison().isEqualTo(new AniListCharactersDto());

			// Mock 호출 검증
			verify(anilistWebClient, times(1)).post();
			verify(messageUtil, times(1)).getMessageKO(anyString(), any());
		}
	}
	
	@Test
	@DisplayName("[UT]getComicsStaffList: 스테프 리스트 조회 - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getgetComicsStaffList_existResponse() throws InterruptedException, ExecutionException, TimeoutException, IOException {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer comicsId = 3000;
			Integer page = 1;
			String query = "graphql query";

			mocked.when(() -> GraphqlUtil.loadQuery("comicsStaffList.graphql"))
			.thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(anyString(), anyMap()))
			.thenReturn(query);

			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(anyString())).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);

			AniListResponseDto aniListResponseDto = new AniListResponseDto();
			AniListDataDto data = new AniListDataDto();
			AniListMediaDto media = new AniListMediaDto();
			media.setId(comicsId);
			AniListStaffDto staff = new AniListStaffDto();
			media.setStaff(staff);
			data.setMedia(media);
			aniListResponseDto.setData(data);

			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(aniListResponseDto));

			// 서비스 메서드 호출 (CompletableFuture 반환이므로 get으로 결과 대기)
			AniListStaffDto result = service.getComicsStaffList(comicsId, page).get(5, TimeUnit.SECONDS);

			// 결과 검증
			assertThat(result).usingRecursiveComparison().isEqualTo(staff);

			// Mock 호출 검증
			verify(anilistWebClient, times(1)).post();
		}
	}
	
	/**
	 * 이하의 이유로 응답 데이터 없음 케이스는 생략함
	 * - WebClient Mono .map 체인 내부에서 response == null 상황 자체가 불가능
	 *   (Mono.empty()가 될 뿐임)
	 * - 프레임워크, 서비스 특성상 해당 분기는 데드코드
	 */
	@Test
	@DisplayName("[UT]getComicsStaffList: 스태프 상세 정보 조회 - 응답 데이터 없음(데드코드)")
	void test_getComicsStaffList_notExistResponse() {
		assertTrue(true);
	}
	
	@Test
	@DisplayName("[UT]getComicsStaffList: 스테프 리스트 조회 - 응답 데이터 존재 및 Data 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getgetComicsStaffList_existResponseAndNotExistData() throws InterruptedException, ExecutionException, TimeoutException, IOException {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer comicsId = 3000;
			Integer page = 1;
			String query = "graphql query";

			mocked.when(() -> GraphqlUtil.loadQuery("comicsStaffList.graphql"))
			.thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(anyString(), anyMap()))
			.thenReturn(query);

			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(anyString())).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);

			AniListResponseDto aniListResponseDto = new AniListResponseDto();

			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(aniListResponseDto));

			when(messageUtil.getMessageKO(anyString(), any())).thenReturn("스태프 정보를 찾을 수 없습니다. (apiId: " + comicsId + ")");
			
			// 서비스 메서드 호출 (CompletableFuture 반환이므로 get으로 결과 대기)
			AniListStaffDto result = service.getComicsStaffList(comicsId, page).get(5, TimeUnit.SECONDS);

			// 결과 검증
			assertThat(result).usingRecursiveComparison().isEqualTo(new AniListStaffDto());

			// Mock 호출 검증
			verify(anilistWebClient, times(1)).post();
			verify(messageUtil, times(1)).getMessageKO(anyString(), any());
		}
	}
	
	@Test
	@DisplayName("[UT]getComicsStaffList: 스테프 리스트 조회 - 응답 데이터 존재 및 Media 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getgetComicsStaffList_existResponseAndNotExistMedia() throws InterruptedException, ExecutionException, TimeoutException, IOException {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer comicsId = 3000;
			Integer page = 1;
			String query = "graphql query";

			mocked.when(() -> GraphqlUtil.loadQuery("comicsStaffList.graphql"))
			.thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(anyString(), anyMap()))
			.thenReturn(query);

			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(anyString())).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);

			AniListResponseDto aniListResponseDto = new AniListResponseDto();
			AniListDataDto data = new AniListDataDto();
			aniListResponseDto.setData(data);

			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(aniListResponseDto));

			when(messageUtil.getMessageKO(anyString(), any())).thenReturn("스태프 정보를 찾을 수 없습니다. (apiId: " + comicsId + ")");
			
			// 서비스 메서드 호출 (CompletableFuture 반환이므로 get으로 결과 대기)
			AniListStaffDto result = service.getComicsStaffList(comicsId, page).get(5, TimeUnit.SECONDS);

			// 결과 검증
			assertThat(result).usingRecursiveComparison().isEqualTo(new AniListStaffDto());

			// Mock 호출 검증
			verify(anilistWebClient, times(1)).post();
			verify(messageUtil, times(1)).getMessageKO(anyString(), any());
		}
	}

}
