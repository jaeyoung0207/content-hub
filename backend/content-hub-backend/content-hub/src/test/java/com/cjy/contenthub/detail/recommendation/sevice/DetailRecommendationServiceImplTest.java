package com.cjy.contenthub.detail.recommendation.sevice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.cjy.contenthub.common.integration.anilist.dto.AniListDataDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListMediaDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListResponseDto;
import com.cjy.contenthub.common.integration.tmdb.constants.TmdbParamConstants;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbRecommendationsMovieDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbRecommendationsMovieResultsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbRecommendationsTvDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbRecommendationsTvResultsDto;
import com.cjy.contenthub.common.util.GraphqlUtil;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.core.constants.DomainEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.core.constants.DomainEnum.DomainMessagesWarnEnum;
import com.cjy.contenthub.core.facade.ApiFacade;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsComicsResponseDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsComicsResultDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsMovieDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsMovieResultsDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsTvDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsTvResultsDto;
import com.cjy.contenthub.detail.recommendation.helper.DetailRecoommendationHelper;
import com.cjy.contenthub.detail.recommendation.mapper.DetailRecommendationMapper;
import com.cjy.contenthub.detail.recommendation.service.DetailRecommendationServiceImpl;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class DetailRecommendationServiceImplTest {
	
	DetailRecommendationServiceImpl service;

	@Mock
	WebClient tmdbWebClient;
	
	@Mock
	WebClient anilistWebClient;
	
	@Mock
	DetailRecoommendationHelper helper;

	@Mock
	DetailRecommendationMapper mapper;
	
	@Mock
	ApiFacade apiFacade;
	
	@Mock
	MessageUtil messageUtil;
	
	ExecutorService executorService;
	
	@BeforeEach
	void setUp() {
		executorService = Executors.newSingleThreadExecutor();
		service = new DetailRecommendationServiceImpl(
				helper,
				mapper,
				messageUtil,
				tmdbWebClient,
				anilistWebClient,
				apiFacade,
				executorService
				);
	}
	
	@AfterEach
	void tearDown() {
        executorService.shutdown();
	}
	
	@Test
	@DisplayName("[UT]getTvRecommendations: TV 추천 작품 조회 - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getTvRecommendations_existResponse() throws InterruptedException, ExecutionException, TimeoutException {
		
		Integer seriesId = 1000;
		Integer page = 1;
		Long userId = 100L;
		
		Map<String, Integer> genreMap = new HashMap<>();
		genreMap.put("Animation", 16);
		when(apiFacade.getTvGenres()).thenReturn(Mono.just(genreMap));
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec);
		when(helper.getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_KOREAN)).thenReturn("https://api.themoviedb.org/3/tv/1000/recommendations?language=ko&page=1");
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		
		TmdbRecommendationsTvDto response = new TmdbRecommendationsTvDto();
		TmdbRecommendationsTvResultsDto resultDto = new TmdbRecommendationsTvResultsDto();
		response.setResults(List.of(resultDto));
		response.setPage(page);
		response.setTotalPages(10);
		response.setTotalResults(100);
		when(responseSpec.bodyToMono(TmdbRecommendationsTvDto.class)).thenReturn(Mono.just(response));
		
		List<DetailRecommendationsTvResultsDto> filterdResultList = new ArrayList<>();
		when(mapper.tmdbRecommendationsTvListToDetailRecommendationsTvList(response.getResults())).thenReturn(filterdResultList);
		DetailRecommendationsTvResultsDto filterdResult1 = new DetailRecommendationsTvResultsDto();
		filterdResult1.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode());
		filterdResultList.add(filterdResult1);
		when(helper.setTvRecommendationResults(anyList(), anyMap())).thenReturn(filterdResultList);
		
		// 서비스 메서드 호출
		DetailRecommendationsTvDto result = service.getTvRecommendations(seriesId, page, userId).get(5, TimeUnit.SECONDS);
		
		// 결과 검증
		DetailRecommendationsTvDto expected = DetailRecommendationsTvDto.builder()
		.page(response.getPage())
		.totalPages(response.getTotalPages())
		.totalResults(response.getTotalResults())
		.results(filterdResultList)
		.build();
		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
		
		verify(tmdbWebClient, times(1)).get();
		verify(helper, times(1)).getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_KOREAN);
		verify(helper, times(1)).setTvRecommendationResults(anyList(), anyMap());
		verify(mapper, times(1)).tmdbRecommendationsTvListToDetailRecommendationsTvList(anyList());
	}
	
	@Test
	@DisplayName("[UT]getTvRecommendations: TV 추천 작품 조회 - 응답 result 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getTvRecommendations_notExistResult() throws InterruptedException, ExecutionException, TimeoutException {
		
		Integer seriesId = 1000;
		Integer page = 1;
		Long userId = 100L;
		
		Map<String, Integer> genreMap = new HashMap<>();
		genreMap.put("Animation", 16);
		when(apiFacade.getTvGenres()).thenReturn(Mono.just(genreMap));
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec);
		when(helper.getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_KOREAN)).thenReturn("https://api.themoviedb.org/3/tv/1000/recommendations?language=ko&page=1");
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		
		TmdbRecommendationsTvDto response = new TmdbRecommendationsTvDto();
		when(responseSpec.bodyToMono(TmdbRecommendationsTvDto.class)).thenReturn(Mono.just(response));
		
		// 서비스 메서드 호출
		DetailRecommendationsTvDto result = service.getTvRecommendations(seriesId, page, userId).get(5, TimeUnit.SECONDS);
		
		// 결과 검증
		DetailRecommendationsTvDto expected = new DetailRecommendationsTvDto();
		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
		
		verify(tmdbWebClient, times(1)).get();
		verify(helper, times(1)).getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_KOREAN);
	}
	
	@Test
	@DisplayName("[UT]getTvRecommendations: TV 추천 작품 조회 - 404 발생 시 재시도 성공")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getTvRecommendations_404AndRetrySuccess() throws InterruptedException, ExecutionException, TimeoutException {
		
		Integer seriesId = 1000;
		Integer page = 1;
		Long userId = 100L;
		
		Map<String, Integer> genreMap = new HashMap<>();
		genreMap.put("Animation", 16);
		when(apiFacade.getTvGenres()).thenReturn(Mono.just(genreMap));
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec2 = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1, uriSpec2);
		when(helper.getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_KOREAN)).thenReturn("https://api.themoviedb.org/3/tv/1000/recommendations?language=ko&page=1");
		when(helper.getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_ENGLISH)).thenReturn("https://api.themoviedb.org/3/tv/1000/recommendations?language=en&page=1");
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		ResponseSpec responseSpec2 = mock(ResponseSpec.class);
		when(uriSpec1.uri(anyString())).thenReturn(uriSpec1);
		when(uriSpec2.uri(anyString())).thenReturn(uriSpec2);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		when(uriSpec2.retrieve()).thenReturn(responseSpec2);
		
		// 예외 발생 설정
		when(responseSpec1.bodyToMono(TmdbRecommendationsTvDto.class))
		.thenReturn(Mono.error(new WebClientResponseException(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null, null, null)));

		// 메시지 유틸 Mock 설정		
		String apiName = "TMDB TV Recommendations";
		Object[] messageParams = { apiName, seriesId };
		when(messageUtil.getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND_THEN_RETRY.getMessageCode(), messageParams))
		.thenReturn(String.format("추천 정보를 찾을 수 없으므로 재시도합니다. (apiName: %s, apiId: %s)", apiName, seriesId));
		
		// 재시도 시 상태 코드 처리 설정
		when(responseSpec2.onStatus(any(), any())).thenReturn(responseSpec2);
		
		// 재시도 시 정상 응답 설정
		TmdbRecommendationsTvDto response = new TmdbRecommendationsTvDto();
		when(responseSpec2.bodyToMono(TmdbRecommendationsTvDto.class)).thenReturn(Mono.just(response));
		
		// 서비스 메서드 호출
		DetailRecommendationsTvDto result = service.getTvRecommendations(seriesId, page, userId).get(5, TimeUnit.SECONDS);
		
		// 결과 검증
		DetailRecommendationsTvDto expected = new DetailRecommendationsTvDto();
		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
		
		verify(tmdbWebClient, times(2)).get();
		verify(helper, times(1)).getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_KOREAN);
		verify(helper, times(1)).getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_ENGLISH);
		verify(messageUtil, times(1)).getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND_THEN_RETRY.getMessageCode(), messageParams);
	}
	
	@Test
	@DisplayName("[UT]getTvRecommendations: TV 추천 작품 조회 - 404 발생 시 재시도 404")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getTvRecommendations_404AndRetry404() throws InterruptedException, ExecutionException, TimeoutException {
		
		Integer seriesId = 1000;
		Integer page = 1;
		Long userId = 100L;
		
		Map<String, Integer> genreMap = new HashMap<>();
		genreMap.put("Animation", 16);
		when(apiFacade.getTvGenres()).thenReturn(Mono.just(genreMap));
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec2 = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1, uriSpec2);
		when(helper.getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_KOREAN)).thenReturn("https://api.themoviedb.org/3/tv/1000/recommendations?language=ko&page=1");
		when(helper.getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_ENGLISH)).thenReturn("https://api.themoviedb.org/3/tv/1000/recommendations?language=en&page=1");
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		ResponseSpec responseSpec2 = mock(ResponseSpec.class);
		when(uriSpec1.uri(anyString())).thenReturn(uriSpec1);
		when(uriSpec2.uri(anyString())).thenReturn(uriSpec2);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		when(uriSpec2.retrieve()).thenReturn(responseSpec2);
		
		// 예외 발생 설정
		when(responseSpec1.bodyToMono(TmdbRecommendationsTvDto.class))
		.thenReturn(Mono.error(new WebClientResponseException(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null, null, null)));

		// 메시지 유틸 Mock 설정		
		String apiName = "TMDB TV Recommendations";
		Object[] messageParams = { apiName, seriesId };
		when(messageUtil.getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND_THEN_RETRY.getMessageCode(), messageParams))
		.thenReturn(String.format("추천 정보를 찾을 수 없으므로 재시도합니다. (apiName: %s, apiId: %s)", apiName, seriesId));
		when(messageUtil.getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND.getMessageCode(), messageParams))
		.thenReturn(String.format("추천 정보를 찾을 수 없습니다. (apiName: %s, apiId: %s)", apiName, seriesId));
		
		// 재시도 시 상태 코드 처리 설정
		String errorBody = "error body";
		when(responseSpec2.onStatus(any(), any())).thenAnswer(invocation -> {
			Function<ClientResponse, Mono<? extends Throwable>> handler = invocation.getArgument(1);
			ClientResponse clientResponse = mock(ClientResponse.class);
			when(clientResponse.statusCode()).thenReturn(HttpStatus.NOT_FOUND);
			when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(errorBody));
			handler.apply(clientResponse).block();
			return responseSpec2;
		});
		
		when(responseSpec2.bodyToMono(TmdbRecommendationsTvDto.class)).thenReturn(Mono.empty());
		
		// 서비스 메서드 호출
		DetailRecommendationsTvDto result = service.getTvRecommendations(seriesId, page, userId).get(5, TimeUnit.SECONDS);
		
		// 결과 검증
		assertThat(result).isNull();
		
		verify(tmdbWebClient, times(2)).get();
		verify(helper, times(1)).getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_KOREAN);
		verify(helper, times(1)).getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_ENGLISH);
		verify(messageUtil, times(1)).getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND_THEN_RETRY.getMessageCode(), messageParams);
		verify(messageUtil, times(1)).getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND.getMessageCode(), messageParams);
	}
	
	@Test
	@DisplayName("[UT]getTvRecommendations: TV 추천 작품 조회 - 404 발생 시 재시도 404 이외")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getTvRecommendations_404AndRetryNot404() {
		
		Integer seriesId = 1000;
		Integer page = 1;
		Long userId = 100L;
		
		Map<String, Integer> genreMap = new HashMap<>();
		genreMap.put("Animation", 16);
		when(apiFacade.getTvGenres()).thenReturn(Mono.just(genreMap));
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec2 = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1, uriSpec2);
		when(helper.getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_KOREAN)).thenReturn("https://api.themoviedb.org/3/tv/1000/recommendations?language=ko&page=1");
		when(helper.getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_ENGLISH)).thenReturn("https://api.themoviedb.org/3/tv/1000/recommendations?language=en&page=1");
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		ResponseSpec responseSpec2 = mock(ResponseSpec.class);
		when(uriSpec1.uri(anyString())).thenReturn(uriSpec1);
		when(uriSpec2.uri(anyString())).thenReturn(uriSpec2);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		when(uriSpec2.retrieve()).thenReturn(responseSpec2);
		
		// 예외 발생 설정
		when(responseSpec1.bodyToMono(TmdbRecommendationsTvDto.class))
		.thenReturn(Mono.error(new WebClientResponseException(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null, null, null)));

		// 메시지 유틸 Mock 설정		
		String apiName = "TMDB TV Recommendations";
		Object[] messageParams = { apiName, seriesId };
		when(messageUtil.getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND_THEN_RETRY.getMessageCode(), messageParams))
		.thenReturn(String.format("추천 정보를 찾을 수 없으므로 재시도합니다. (apiName: %s, apiId: %s)", apiName, seriesId));
		
		// 재시도 시 상태 코드 처리 설정
		String errorBody = "error body";
		when(responseSpec2.onStatus(any(), any())).thenAnswer(invocation -> {
			Function<ClientResponse, Mono<? extends Throwable>> handler = invocation.getArgument(1);
			ClientResponse clientResponse = mock(ClientResponse.class);
			when(clientResponse.statusCode()).thenReturn(HttpStatus.BAD_REQUEST);
			when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(errorBody));
			handler.apply(clientResponse).block();
			return responseSpec2;
		});
		
		// 서비스 메서드 호출
		CompletableFuture<DetailRecommendationsTvDto> result = service.getTvRecommendations(seriesId, page, userId);
		
		// 결과 검증
		// CompletableFuture .get() 시 ExecutionException 발생
		ExecutionException exception = assertThrows(ExecutionException.class, () -> {
			result.get(5, TimeUnit.SECONDS);
		});
		String errorMessage = "TMDB TV API Error";
		assertThat(exception.getCause()).isInstanceOf(WebClientResponseException.class);
		assertThat(((WebClientResponseException)exception.getCause()).getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(((WebClientResponseException)exception.getCause()).getMessage()).isEqualTo(errorMessage);
		assertThat(((WebClientResponseException)exception.getCause()).getResponseBodyAsString()).isEqualTo(errorBody);
		
		verify(tmdbWebClient, times(2)).get();
		verify(helper, times(1)).getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_KOREAN);
		verify(helper, times(1)).getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_ENGLISH);
		verify(messageUtil, times(1)).getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND_THEN_RETRY.getMessageCode(), messageParams);
	}
	
	@Test
	@DisplayName("[UT]getTvRecommendations: TV 추천 작품 조회 - 404 이외 에러")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getTvRecommendations_retryNot404() {
		
		Integer seriesId = 1000;
		Integer page = 1;
		Long userId = 100L;
		
		Map<String, Integer> genreMap = new HashMap<>();
		genreMap.put("Animation", 16);
		when(apiFacade.getTvGenres()).thenReturn(Mono.just(genreMap));
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1);
		when(helper.getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_KOREAN)).thenReturn("https://api.themoviedb.org/3/tv/1000/recommendations?language=ko&page=1");
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		when(uriSpec1.uri(anyString())).thenReturn(uriSpec1);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		
		// 예외 발생 설정
		String errorMessage = "TMDB TV API Error";
		when(responseSpec1.bodyToMono(TmdbRecommendationsTvDto.class))
		.thenReturn(Mono.error(new WebClientResponseException(errorMessage, HttpStatus.BAD_REQUEST.value(), null, null, null, null)));

		// 서비스 메서드 호출
		CompletableFuture<DetailRecommendationsTvDto> result = service.getTvRecommendations(seriesId, page, userId);
		
		// 결과 검증
		// CompletableFuture .get() 시 ExecutionException 발생
		ExecutionException exception = assertThrows(ExecutionException.class, () -> {
			result.get(5, TimeUnit.SECONDS);
		});
		assertThat(exception.getCause()).isInstanceOf(WebClientResponseException.class);
		assertThat(((WebClientResponseException)exception.getCause()).getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(((WebClientResponseException)exception.getCause()).getMessage()).isEqualTo(errorMessage);
		
		verify(tmdbWebClient, times(1)).get();
		verify(helper, times(1)).getTVRecommendationUri(seriesId, page, TmdbParamConstants.LANGUAGE_KOREAN);
	}
	
	
	@Test
	@DisplayName("[UT]getMovieRecommendations: 영화 추천 작품 조회 - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getMovieRecommendations_existResponse() throws InterruptedException, ExecutionException, TimeoutException {
		
		Integer movieId = 2000;
		Integer page = 1;
		Long userId = 100L;
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec);
		when(helper.getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_KOREAN)).thenReturn("https://api.themoviedb.org/3/movie/2000/recommendations?language=ko&page=1");
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		
		TmdbRecommendationsMovieDto response = new TmdbRecommendationsMovieDto();
		TmdbRecommendationsMovieResultsDto resultDto = new TmdbRecommendationsMovieResultsDto();
		response.setResults(List.of(resultDto));
		response.setPage(page);
		response.setTotalPages(10);
		response.setTotalResults(100);
		when(responseSpec.bodyToMono(TmdbRecommendationsMovieDto.class)).thenReturn(Mono.just(response));
		
		List<DetailRecommendationsMovieResultsDto> filterdResultList = new ArrayList<>();
		when(mapper.tmdbRecommendationsMovieListToDetailRecommendationsMovieList(response.getResults())).thenReturn(filterdResultList);
		DetailRecommendationsMovieResultsDto filterdResult1 = new DetailRecommendationsMovieResultsDto();
		filterdResult1.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode());
		filterdResultList.add(filterdResult1);
		
		// 서비스 메서드 호출
		DetailRecommendationsMovieDto result = service.getMovieRecommendations(movieId, page, userId).get(5, TimeUnit.SECONDS);
		
		// 결과 검증
		DetailRecommendationsMovieDto expected = DetailRecommendationsMovieDto.builder()
		.page(response.getPage())
		.totalPages(response.getTotalPages())
		.totalResults(response.getTotalResults())
		.results(filterdResultList)
		.build();
		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
		
		verify(tmdbWebClient, times(1)).get();
		verify(helper, times(1)).getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_KOREAN);
		verify(mapper, times(1)).tmdbRecommendationsMovieListToDetailRecommendationsMovieList(anyList());
	}
	
	@Test
	@DisplayName("[UT]getMovieRecommendations: 영화 추천 작품 조회 - 응답 result 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getMovieRecommendations_notExistResult() throws InterruptedException, ExecutionException, TimeoutException {
		
		Integer movieId = 2000;
		Integer page = 1;
		Long userId = 100L;
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec);
		when(helper.getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_KOREAN)).thenReturn("https://api.themoviedb.org/3/movie/2000/recommendations?language=ko&page=1");
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		
		TmdbRecommendationsMovieDto response = new TmdbRecommendationsMovieDto();
		when(responseSpec.bodyToMono(TmdbRecommendationsMovieDto.class)).thenReturn(Mono.just(response));
		
		// 서비스 메서드 호출
		DetailRecommendationsMovieDto result = service.getMovieRecommendations(movieId, page, userId).get(5, TimeUnit.SECONDS);
		
		// 결과 검증
		DetailRecommendationsMovieDto expected = new DetailRecommendationsMovieDto();
		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
		
		verify(tmdbWebClient, times(1)).get();
		verify(helper, times(1)).getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_KOREAN);
	}
	
	@Test
	@DisplayName("[UT]getMovieRecommendations: 영화 추천 작품 조회 - 404 발생 시 재시도 성공")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getMovieRecommendations_404AndRetrySuccess() throws InterruptedException, ExecutionException, TimeoutException {
		
		Integer movieId = 2000;
		Integer page = 1;
		Long userId = 100L;
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec2 = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1, uriSpec2);
		when(helper.getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_KOREAN)).thenReturn("https://api.themoviedb.org/3/movie/2000/recommendations?language=ko&page=1");
		when(helper.getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_ENGLISH)).thenReturn("https://api.themoviedb.org/3/movie/2000/recommendations?language=en&page=1");
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		ResponseSpec responseSpec2 = mock(ResponseSpec.class);
		when(uriSpec1.uri(anyString())).thenReturn(uriSpec1);
		when(uriSpec2.uri(anyString())).thenReturn(uriSpec2);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		when(uriSpec2.retrieve()).thenReturn(responseSpec2);
		
		// 예외 발생 설정
		when(responseSpec1.bodyToMono(TmdbRecommendationsMovieDto.class))
		.thenReturn(Mono.error(new WebClientResponseException(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null, null, null)));

		// 메시지 유틸 Mock 설정		
		String apiName = "TMDB Movie Recommendations";
		Object[] messageParams = { apiName, movieId };
		when(messageUtil.getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND_THEN_RETRY.getMessageCode(), messageParams))
		.thenReturn(String.format("추천 정보를 찾을 수 없으므로 재시도합니다. (apiName: %s, apiId: %s)", apiName, movieId));
		
		// 재시도 시 상태 코드 처리 설정
		when(responseSpec2.onStatus(any(), any())).thenReturn(responseSpec2);
		
		// 재시도 시 정상 응답 설정
		TmdbRecommendationsMovieDto response = new TmdbRecommendationsMovieDto();
		when(responseSpec2.bodyToMono(TmdbRecommendationsMovieDto.class)).thenReturn(Mono.just(response));
		
		// 서비스 메서드 호출
		DetailRecommendationsMovieDto result = service.getMovieRecommendations(movieId, page, userId).get(5, TimeUnit.SECONDS);
		
		// 결과 검증
		DetailRecommendationsMovieDto expected = new DetailRecommendationsMovieDto();
		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
		
		verify(tmdbWebClient, times(2)).get();
		verify(helper, times(1)).getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_KOREAN);
		verify(helper, times(1)).getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_ENGLISH);
		verify(messageUtil, times(1)).getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND_THEN_RETRY.getMessageCode(), messageParams);
	}
	
	@Test
	@DisplayName("[UT]getMovieRecommendations: 영화 추천 작품 조회 - 404 발생 시 재시도 404")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getMovieRecommendations_404AndRetry404() throws InterruptedException, ExecutionException, TimeoutException {
		
		Integer movieId = 2000;
		Integer page = 1;
		Long userId = 100L;
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec2 = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1, uriSpec2);
		when(helper.getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_KOREAN)).thenReturn("https://api.themoviedb.org/3/movie/2000/recommendations?language=ko&page=1");
		when(helper.getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_ENGLISH)).thenReturn("https://api.themoviedb.org/3/movie/2000/recommendations?language=en&page=1");
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		ResponseSpec responseSpec2 = mock(ResponseSpec.class);
		when(uriSpec1.uri(anyString())).thenReturn(uriSpec1);
		when(uriSpec2.uri(anyString())).thenReturn(uriSpec2);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		when(uriSpec2.retrieve()).thenReturn(responseSpec2);
		
		// 예외 발생 설정
		when(responseSpec1.bodyToMono(TmdbRecommendationsMovieDto.class))
		.thenReturn(Mono.error(new WebClientResponseException(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null, null, null)));

		// 메시지 유틸 Mock 설정		
		String apiName = "TMDB Movie Recommendations";
		Object[] messageParams = { apiName, movieId };
		when(messageUtil.getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND_THEN_RETRY.getMessageCode(), messageParams))
		.thenReturn(String.format("추천 정보를 찾을 수 없으므로 재시도합니다. (apiName: %s, apiId: %s)", apiName, movieId));
		when(messageUtil.getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND.getMessageCode(), messageParams))
		.thenReturn(String.format("추천 정보를 찾을 수 없습니다. (apiName: %s, apiId: %s)", apiName, movieId));
		
		// 재시도 시 상태 코드 처리 설정
		String errorBody = "error body";
		when(responseSpec2.onStatus(any(), any())).thenAnswer(invocation -> {
			Function<ClientResponse, Mono<? extends Throwable>> handler = invocation.getArgument(1);
			ClientResponse clientResponse = mock(ClientResponse.class);
			when(clientResponse.statusCode()).thenReturn(HttpStatus.NOT_FOUND);
			when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(errorBody));
			handler.apply(clientResponse).block();
			return responseSpec2;
		});
		
		when(responseSpec2.bodyToMono(TmdbRecommendationsMovieDto.class)).thenReturn(Mono.empty());
		
		// 서비스 메서드 호출
		DetailRecommendationsMovieDto result = service.getMovieRecommendations(movieId, page, userId).get(5, TimeUnit.SECONDS);
		
		// 결과 검증
		assertThat(result).isNull();
		
		verify(tmdbWebClient, times(2)).get();
		verify(helper, times(1)).getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_KOREAN);
		verify(helper, times(1)).getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_ENGLISH);
		verify(messageUtil, times(1)).getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND_THEN_RETRY.getMessageCode(), messageParams);
		verify(messageUtil, times(1)).getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND.getMessageCode(), messageParams);
	}
	
	@Test
	@DisplayName("[UT]getMovieRecommendations: 영화 추천 작품 조회 - 404 발생 시 재시도 404 이외")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getMovieRecommendations_404AndRetryNot404() {
		
		Integer movieId = 2000;
		Integer page = 1;
		Long userId = 100L;
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec2 = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1, uriSpec2);
		when(helper.getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_KOREAN)).thenReturn("https://api.themoviedb.org/3/movie/2000/recommendations?language=ko&page=1");
		when(helper.getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_ENGLISH)).thenReturn("https://api.themoviedb.org/3/movie/2000/recommendations?language=en&page=1");
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		ResponseSpec responseSpec2 = mock(ResponseSpec.class);
		when(uriSpec1.uri(anyString())).thenReturn(uriSpec1);
		when(uriSpec2.uri(anyString())).thenReturn(uriSpec2);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		when(uriSpec2.retrieve()).thenReturn(responseSpec2);
		
		// 예외 발생 설정
		when(responseSpec1.bodyToMono(TmdbRecommendationsMovieDto.class))
		.thenReturn(Mono.error(new WebClientResponseException(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null, null, null)));

		// 메시지 유틸 Mock 설정		
		String apiName = "TMDB Movie Recommendations";
		Object[] messageParams = { apiName, movieId };
		when(messageUtil.getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND_THEN_RETRY.getMessageCode(), messageParams))
		.thenReturn(String.format("추천 정보를 찾을 수 없으므로 재시도합니다. (apiName: %s, apiId: %s)", apiName, movieId));
		
		// 재시도 시 상태 코드 처리 설정
		String errorBody = "error body";
		when(responseSpec2.onStatus(any(), any())).thenAnswer(invocation -> {
			Function<ClientResponse, Mono<? extends Throwable>> handler = invocation.getArgument(1);
			ClientResponse clientResponse = mock(ClientResponse.class);
			when(clientResponse.statusCode()).thenReturn(HttpStatus.BAD_REQUEST);
			when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(errorBody));
			handler.apply(clientResponse).block();
			return responseSpec2;
		});
		
		// 서비스 메서드 호출
		CompletableFuture<DetailRecommendationsMovieDto> result = service.getMovieRecommendations(movieId, page, userId);
		
		// 결과 검증
		// CompletableFuture .get() 시 ExecutionException 발생
		ExecutionException exception = assertThrows(ExecutionException.class, () -> {
			result.get(5, TimeUnit.SECONDS);
		});
		String errorMessage = "TMDB Movie API Error";
		assertThat(exception.getCause()).isInstanceOf(WebClientResponseException.class);
		assertThat(((WebClientResponseException)exception.getCause()).getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(((WebClientResponseException)exception.getCause()).getMessage()).isEqualTo(errorMessage);
		assertThat(((WebClientResponseException)exception.getCause()).getResponseBodyAsString()).isEqualTo(errorBody);
		
		verify(tmdbWebClient, times(2)).get();
		verify(helper, times(1)).getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_KOREAN);
		verify(helper, times(1)).getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_ENGLISH);
		verify(messageUtil, times(1)).getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND_THEN_RETRY.getMessageCode(), messageParams);
	}
	
	@Test
	@DisplayName("[UT]getMovieRecommendations: 영화 추천 작품 조회 - 404 이외 에러")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getMovieRecommendations_retryNot404() {
		
		Integer movieId = 2000;
		Integer page = 1;
		Long userId = 100L;
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1);
		when(helper.getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_KOREAN)).thenReturn("https://api.themoviedb.org/3/movie/2000/recommendations?language=ko&page=1");
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		when(uriSpec1.uri(anyString())).thenReturn(uriSpec1);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		
		// 예외 발생 설정
		String errorMessage = "TMDB Movie API Error";
		when(responseSpec1.bodyToMono(TmdbRecommendationsMovieDto.class))
		.thenReturn(Mono.error(new WebClientResponseException(errorMessage, HttpStatus.BAD_REQUEST.value(), null, null, null, null)));

		// 서비스 메서드 호출
		CompletableFuture<DetailRecommendationsMovieDto> result = service.getMovieRecommendations(movieId, page, userId);
		
		// 결과 검증
		// CompletableFuture .get() 시 ExecutionException 발생
		ExecutionException exception = assertThrows(ExecutionException.class, () -> {
			result.get(5, TimeUnit.SECONDS);
		});
		assertThat(exception.getCause()).isInstanceOf(WebClientResponseException.class);
		assertThat(((WebClientResponseException)exception.getCause()).getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(((WebClientResponseException)exception.getCause()).getMessage()).isEqualTo(errorMessage);
		
		verify(tmdbWebClient, times(1)).get();
		verify(helper, times(1)).getMovieRecommendationUri(movieId, page, TmdbParamConstants.LANGUAGE_KOREAN);
	}
	
	@Test
	@DisplayName("[UT]getComicsRecommendations: 만화 추천 작품 조회 - 응답 데이터 존재 및 첫번째 페이지")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getComicsRecommendations_existResponseAndFirstPage() throws InterruptedException, ExecutionException, TimeoutException, IOException {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer mediaId = 3000;
			Integer page = 1;
			Long userId = 100L;
			String query = "graphql query";

			mocked.when(() -> GraphqlUtil.loadQuery("comicsRecomendationList.graphql"))
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
			
			// 재시도 시 상태 코드 처리 설정
			when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
			
			// 응답 데이터 설정
			AniListResponseDto aniListResponseDto = new AniListResponseDto();
			AniListDataDto data = new AniListDataDto();
			AniListMediaDto media = new AniListMediaDto();
			media.setId(mediaId);
			data.setMedia(media);
			aniListResponseDto.setData(data);
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(aniListResponseDto));
			
			List<DetailRecommendationsComicsResultDto> comicsResults = new ArrayList<>();
			doNothing().when(helper).getComicsRelations(media, comicsResults);
			doNothing().when(helper).getComicsRecommendations(media, comicsResults);
			
			// 서비스 메서드 호출
			DetailRecommendationsComicsResponseDto result = service.getComicsRecommendations(mediaId, page, userId).get(5, TimeUnit.SECONDS);
			
			// 결과 검증
			DetailRecommendationsComicsResponseDto expected = DetailRecommendationsComicsResponseDto.builder()
					.results(comicsResults)
					.build();
			assertThat(result).usingRecursiveComparison().isEqualTo(expected);
			
			verify(anilistWebClient, times(1)).post();
			verify(helper, times(1)).getComicsRelations(any(AniListMediaDto.class), anyList());
			verify(helper, times(1)).getComicsRecommendations(any(AniListMediaDto.class), anyList());
		}
	}
	
	@Test
	@DisplayName("[UT]getComicsRecommendations: 만화 추천 작품 조회 - 응답 데이터 존재 및 두번째 페이지")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getComicsRecommendations_existResponseAndSecondPage() throws InterruptedException, ExecutionException, TimeoutException, IOException {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer mediaId = 3000;
			Integer page = 2;
			Long userId = 100L;
			String query = "graphql query";

			mocked.when(() -> GraphqlUtil.loadQuery("comicsRecomendationList.graphql"))
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
			
			// 재시도 시 상태 코드 처리 설정
			when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
			
			// 응답 데이터 설정
			AniListResponseDto aniListResponseDto = new AniListResponseDto();
			AniListDataDto data = new AniListDataDto();
			AniListMediaDto media = new AniListMediaDto();
			media.setId(mediaId);
			data.setMedia(media);
			aniListResponseDto.setData(data);
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(aniListResponseDto));
			
			List<DetailRecommendationsComicsResultDto> comicsResults = new ArrayList<>();
			doNothing().when(helper).getComicsRecommendations(media, comicsResults);
			
			// 서비스 메서드 호출
			DetailRecommendationsComicsResponseDto result = service.getComicsRecommendations(mediaId, page, userId).get(5, TimeUnit.SECONDS);
			
			// 결과 검증
			DetailRecommendationsComicsResponseDto expected = DetailRecommendationsComicsResponseDto.builder()
					.results(comicsResults)
					.build();
			assertThat(result).usingRecursiveComparison().isEqualTo(expected);
			
			verify(anilistWebClient, times(1)).post();
			verify(helper, times(1)).getComicsRecommendations(any(AniListMediaDto.class), anyList());
		}
	}
	
	@Test
	@DisplayName("[UT]getComicsRecommendations: 만화 추천 작품 조회 - 응답 데이터 Data 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getComicsRecommendations_notExistData() throws InterruptedException, ExecutionException, TimeoutException, IOException {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer mediaId = 3000;
			Integer page = 0;
			Long userId = 100L;
			String query = "graphql query";

			mocked.when(() -> GraphqlUtil.loadQuery("comicsRecomendationList.graphql"))
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
			
			// 재시도 시 상태 코드 처리 설정
			when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
			
			// 응답 데이터 설정
			AniListResponseDto aniListResponseDto = new AniListResponseDto();
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(aniListResponseDto));
			
			// 서비스 메서드 호출
			DetailRecommendationsComicsResponseDto result = service.getComicsRecommendations(mediaId, page, userId).get(5, TimeUnit.SECONDS);
			
			// 결과 검증
			DetailRecommendationsComicsResponseDto expected = new DetailRecommendationsComicsResponseDto();
			assertThat(result).usingRecursiveComparison().isEqualTo(expected);
			
			verify(anilistWebClient, times(1)).post();
		}
	}
	
	@Test
	@DisplayName("[UT]getComicsRecommendations: 만화 추천 작품 조회 - 응답 데이터 Media 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getComicsRecommendations_notExistMedia() throws InterruptedException, ExecutionException, TimeoutException, IOException {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer mediaId = 3000;
			Integer page = 0;
			Long userId = 100L;
			String query = "graphql query";

			mocked.when(() -> GraphqlUtil.loadQuery("comicsRecomendationList.graphql"))
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
			
			// 재시도 시 상태 코드 처리 설정
			when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
			
			// 응답 데이터 설정
			AniListResponseDto aniListResponseDto = new AniListResponseDto();
			AniListDataDto data = new AniListDataDto();
			aniListResponseDto.setData(data);
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(aniListResponseDto));
			
			// 서비스 메서드 호출
			DetailRecommendationsComicsResponseDto result = service.getComicsRecommendations(mediaId, page, userId).get(5, TimeUnit.SECONDS);
			
			// 결과 검증
			DetailRecommendationsComicsResponseDto expected = new DetailRecommendationsComicsResponseDto();
			assertThat(result).usingRecursiveComparison().isEqualTo(expected);
			
			verify(anilistWebClient, times(1)).post();
		}
	}
	
	@Test
	@DisplayName("[UT]getComicsRecommendations: 만화 추천 작품 조회 - 404 에러")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getComicsRecommendations_404Error() throws InterruptedException, ExecutionException, TimeoutException, IOException {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer mediaId = 3000;
			Integer page = 2;
			Long userId = 100L;
			String query = "graphql query";

			mocked.when(() -> GraphqlUtil.loadQuery("comicsRecomendationList.graphql"))
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
			
			// 재시도 시 상태 코드 처리 설정
			String errorBody = "error body";
			when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> {
				Function<ClientResponse, Mono<? extends Throwable>> handler = invocation.getArgument(1);
				ClientResponse clientResponse = mock(ClientResponse.class);
				when(clientResponse.statusCode()).thenReturn(HttpStatus.NOT_FOUND);
				when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(errorBody));
				handler.apply(clientResponse).block();
				return responseSpec;
			});
			
			// 메시지 유틸 Mock 설정		
			String apiName = "AniList Comics Recommendations";
			Object[] messageParams = { apiName, mediaId };
			when(messageUtil.getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND.getMessageCode(), messageParams))
			.thenReturn(String.format("추천 정보를 찾을 수 없습니다. (apiName: %s, apiId: %s)", apiName, mediaId));
			
			// 응답 데이터 설정
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.empty());
			
			// 서비스 메서드 호출
			DetailRecommendationsComicsResponseDto result = service.getComicsRecommendations(mediaId, page, userId).get(5, TimeUnit.SECONDS);
			
			// 결과 검증
			assertThat(result).isNull();
			
			verify(anilistWebClient, times(1)).post();
			verify(messageUtil, times(1)).getMessageKO(DomainMessagesWarnEnum.WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND.getMessageCode(), messageParams);
		}
	}
	
	@Test
	@DisplayName("[UT]getComicsRecommendations: 만화 추천 작품 조회 - 404 이외 에러")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getComicsRecommendations_Not404Error() {
		
		// MockedStatic 사용 -> try with resources 구문으로 자동 close 처리
		try (MockedStatic<GraphqlUtil> mocked = mockStatic(GraphqlUtil.class)) {
			Integer mediaId = 3000;
			Integer page = 2;
			Long userId = 100L;
			String query = "graphql query";

			mocked.when(() -> GraphqlUtil.loadQuery("comicsRecomendationList.graphql"))
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
			
			// 재시도 시 상태 코드 처리 설정
			String errorBody = "error body";
			when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> {
				Function<ClientResponse, Mono<? extends Throwable>> handler = invocation.getArgument(1);
				ClientResponse clientResponse = mock(ClientResponse.class);
				when(clientResponse.statusCode()).thenReturn(HttpStatus.BAD_REQUEST);
				when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(errorBody));
				handler.apply(clientResponse).block();
				return responseSpec;
			});
			
			// 응답 데이터 설정
			String errorMessage = "AniList API Error";

			// 결과 검증
			WebClientResponseException exception = assertThrows(WebClientResponseException.class, () -> {
				service.getComicsRecommendations(mediaId, page, userId).get(5, TimeUnit.SECONDS);
			});
			assertThat(exception.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(exception.getMessage()).isEqualTo(errorMessage);
			assertThat(exception.getResponseBodyAsString()).isEqualTo(errorBody);
			
			verify(anilistWebClient, times(1)).post();
		}
	}


}
