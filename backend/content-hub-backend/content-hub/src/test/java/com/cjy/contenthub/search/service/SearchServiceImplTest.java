package com.cjy.contenthub.search.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
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
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;

import com.cjy.contenthub.common.integration.anilist.dto.AniListDataDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListMediaDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListPageDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListPageInfoDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListResponseDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListTitleDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbSearchMovieDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbSearchMovieResultsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbSearchMultiDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbSearchMultiResultsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbSearchTvDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbSearchTvResultsDto;
import com.cjy.contenthub.common.util.GraphqlUtil;
import com.cjy.contenthub.core.constants.DomainConstants;
import com.cjy.contenthub.core.constants.DomainEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.core.facade.ApiFacade;
import com.cjy.contenthub.search.controller.dto.SearchComicsResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchComicsResultDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;
import com.cjy.contenthub.search.helper.SearchHelper;
import com.cjy.contenthub.search.mapper.SearchMapper;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {
	
	SearchServiceImpl service;

	@Mock
	WebClient tmdbWebClient;
	
	@Mock
	WebClient anilistWebClient;
	
	@Mock
	SearchHelper helper;

	@Mock
	SearchMapper mapper;
	
	@Mock
	ApiFacade apiFacade;
	
	ExecutorService executorService;
	
	static final String TV_SEARCH_PATH = "/3/search/tv";
	
	static final String MOVIE_SEARCH_PATH = "/3/search/movie";
	
	static final String MULTI_SEARCH_PATH = "/3/search/multi";
	
	@BeforeEach
	void setUp() {
		executorService = Executors.newSingleThreadExecutor();
		service = new SearchServiceImpl(
				tmdbWebClient,
				anilistWebClient,
				apiFacade,
				helper,
				mapper,
				executorService
				);
		ReflectionTestUtils.setField(service, "tvSearchPath", TV_SEARCH_PATH);
		ReflectionTestUtils.setField(service, "movieSearchPath", MOVIE_SEARCH_PATH);
		ReflectionTestUtils.setField(service, "multiSearchPath", MULTI_SEARCH_PATH);
		ReflectionTestUtils.setField(service, "autoCompleteCount", 10);
		ReflectionTestUtils.setField(service, "tmdbParallelPages", 2);
		ReflectionTestUtils.setField(service, "anilistPerMainPage", 12);
		ReflectionTestUtils.setField(service, "anilistPerMorePage", 18);
	}
	
	@AfterEach
	void tearDown() {
        executorService.shutdown();
	}
	
	@Test
	@DisplayName("[UT]searchKeyword: 검색어 리스트 조회 - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_searchKeyword_existResponse() throws InterruptedException, ExecutionException, TimeoutException {
		
		String keyword = "블랙";
		boolean isAdult = false;
		
		Map<String, Integer> tvGenreMap = new HashMap<>();
		tvGenreMap.put("Animation", 16);
		tvGenreMap.put("Drama", 18);
		Map<String, Integer> movieGenreMap = new HashMap<>();
		movieGenreMap.put("Action", 28);
		
		when(apiFacade.getTvGenres()).thenReturn(Mono.just(tvGenreMap));
		when(apiFacade.getMovieGenres()).thenReturn(Mono.just(movieGenreMap));
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		
		TmdbSearchMultiDto response = new TmdbSearchMultiDto();
		TmdbSearchMultiResultsDto multiResults1 = new TmdbSearchMultiResultsDto();
		multiResults1.setGenreIds(List.of(18));
		multiResults1.setName("블랙리스트");
		multiResults1.setMediaType(ContentMediaTypeEnum.TMDB_MEDIA_TYPE_TV.getContentMediaTypeValue());
		TmdbSearchMultiResultsDto multiResults2 = new TmdbSearchMultiResultsDto();
		multiResults2.setGenreIds(List.of(28));
		multiResults2.setTitle("맨 인 블랙");
		multiResults2.setMediaType(ContentMediaTypeEnum.TMDB_MEDIA_TYPE_MOVIE.getContentMediaTypeValue());
		TmdbSearchMultiResultsDto multiResults3 = new TmdbSearchMultiResultsDto();
		multiResults3.setTitle("잭 블랙");
		multiResults3.setMediaType(ContentMediaTypeEnum.TMDB_MEDIA_TYPE_PERSON.getContentMediaTypeValue());
		TmdbSearchMultiResultsDto multiResults4 = new TmdbSearchMultiResultsDto();
		multiResults4.setName("블랙홀");
		multiResults4.setMediaType(ContentMediaTypeEnum.TMDB_MEDIA_TYPE_TV.getContentMediaTypeValue());
		List<TmdbSearchMultiResultsDto> multiResultsList = new ArrayList<>();
		multiResultsList.add(multiResults1);
		multiResultsList.add(multiResults2);
		multiResultsList.add(multiResults3);
		multiResultsList.add(multiResults4);
		response.setResults(multiResultsList);
		when(responseSpec.bodyToMono(TmdbSearchMultiDto.class)).thenReturn(Mono.just(response));
		
		List<String> sortedList = List.of("블랙리스트", "맨 인 블랙");
		when(helper.sortKeywordList(anyList(), anyString())).thenReturn(sortedList);
		
		// 서비스 메서드 호출
		List<String> resultList = service.searchKeyword(keyword, isAdult).get(5, TimeUnit.SECONDS);
		
		// 검증
		List <String> expectedList = sortedList;
		assertThat(resultList).isEqualTo(expectedList);
		
		verify(apiFacade, times(1)).getTvGenres();
		verify(apiFacade, times(1)).getMovieGenres();
		verify(tmdbWebClient, times(1)).get();
		verify(helper, times(1)).sortKeywordList(anyList(), anyString());
	}
	
	@Test
	@DisplayName("[UT]searchKeyword: 검색어 리스트 조회 - 검색 결과 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_searchKeyword_notExistResults() throws InterruptedException, ExecutionException, TimeoutException {
		
		String keyword = "드래곤볼";
		boolean isAdult = false;
		
		Map<String, Integer> tvGenreMap = new HashMap<>();
		tvGenreMap.put("Animation", 16);
		tvGenreMap.put("Drama", 18);
		Map<String, Integer> movieGenreMap = new HashMap<>();
		movieGenreMap.put("Action", 28);
		
		when(apiFacade.getTvGenres()).thenReturn(Mono.just(tvGenreMap));
		when(apiFacade.getMovieGenres()).thenReturn(Mono.just(movieGenreMap));
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		
		TmdbSearchMultiDto response = new TmdbSearchMultiDto();
		when(responseSpec.bodyToMono(TmdbSearchMultiDto.class)).thenReturn(Mono.just(response));
		
		// 서비스 메서드 호출
		List<String> resultList = service.searchKeyword(keyword, isAdult).get(5, TimeUnit.SECONDS);
		
		// 검증
		List <String> expectedList = Collections.emptyList();
		assertThat(resultList).isEqualTo(expectedList);
		
		verify(apiFacade, times(1)).getTvGenres();
		verify(apiFacade, times(1)).getMovieGenres();
		verify(tmdbWebClient, times(1)).get();
	}
	
	@Test
	@DisplayName("[UT]searchVideo: TV 시리즈 및 영화 검색 - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_searchVideo_existTvAndMovie() throws InterruptedException, ExecutionException, TimeoutException {
		
		String keyword = "블랙";
		boolean isAdult = false;
		
		Map<String, Integer> tvGenreMap = new HashMap<>();
		tvGenreMap.put("Animation", 16);
		tvGenreMap.put("Drama", 18);
		Map<String, Integer> movieGenreMap = new HashMap<>();
		movieGenreMap.put("Action", 28);
		
		when(apiFacade.getTvGenres()).thenReturn(Mono.just(tvGenreMap));
		when(apiFacade.getMovieGenres()).thenReturn(Mono.just(movieGenreMap));
		
		/* 애니, 드라마 정보 조회 */
		// TMDB API TV 시리즈 검색 URI 생성
		when(helper.getSearchUri(TV_SEARCH_PATH, keyword, isAdult, 1))
		.thenReturn(TV_SEARCH_PATH + "?query=블랙&isAdult=false&page=1");
		when(helper.getSearchUri(TV_SEARCH_PATH, keyword, isAdult, 2))
		.thenReturn(TV_SEARCH_PATH + "?query=블랙&isAdult=false&page=2");
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec2 = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1, uriSpec1, uriSpec2, uriSpec2);
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		when(uriSpec1.uri(anyString())).thenReturn(uriSpec1);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		ResponseSpec responseSpec2 = mock(ResponseSpec.class);

		// 첫 번째 페이지 응답 설정
		TmdbSearchTvDto response1 = new TmdbSearchTvDto();
		TmdbSearchTvResultsDto multiResults1 = new TmdbSearchTvResultsDto();
		multiResults1.setGenreIds(List.of(16));
		multiResults1.setName("블랙리스트");
		List<TmdbSearchTvResultsDto> multiResultsList1 = new ArrayList<>();
		response1.setResults(multiResultsList1);
		response1.setPage(1);
		response1.setTotalPages(2);
		response1.setTotalResults(2);
		// 두 번째 페이지 응답 설정
		TmdbSearchTvDto response2 = new TmdbSearchTvDto();
		TmdbSearchTvResultsDto multiResults2 = new TmdbSearchTvResultsDto();
		multiResults2.setGenreIds(List.of(18));
		multiResults2.setName("블랙클로버");
		List<TmdbSearchTvResultsDto> multiResultsList2 = new ArrayList<>();
		response2.setResults(multiResultsList2);
		response2.setPage(2);
		response2.setTotalPages(2);
		response2.setTotalResults(2);
		// 응답 데이터 설정
		when(responseSpec1.bodyToMono(TmdbSearchTvDto.class)).thenReturn(Mono.just(response1), Mono.just(response2));
		
		// TV 시리즈 검색 결과 매핑
		SearchTvResultsDto tvResult1 = new SearchTvResultsDto();
		tvResult1.setContentMediaType("1101");
		tvResult1.setGenreIds(List.of(16));
		List<SearchTvResultsDto> tvResultsList1 = new ArrayList<>();
		tvResultsList1.add(tvResult1);
		SearchTvResultsDto tvResult2 = new SearchTvResultsDto();
		tvResult2.setContentMediaType("1102");
		tvResult2.setGenreIds(List.of(18));
		List<SearchTvResultsDto> tvResultsList2 = new ArrayList<>();
		tvResultsList2.add(tvResult2);
		List<SearchTvResultsDto> aniList = new ArrayList<>();
		aniList.addAll(tvResultsList1);
		List<SearchTvResultsDto> dramaList = new ArrayList<>(); 
		dramaList.addAll(tvResultsList2);
		when(mapper.tvResultsListToTmdbTvResultsList(anyList())).thenReturn(tvResultsList1, tvResultsList2);
		when(helper.getAniList(anyList(), eq(tvGenreMap))).thenReturn(aniList, Collections.emptyList());
		when(helper.getDramaList(anyList(), eq(tvGenreMap))).thenReturn(Collections.emptyList(), dramaList);
		when(helper.getDocumentaryList(anyList(), eq(tvGenreMap))).thenReturn(Collections.emptyList());
		when(helper.getKidsList(anyList(), eq(tvGenreMap))).thenReturn(Collections.emptyList());
		when(helper.getNewsList(anyList(), eq(tvGenreMap))).thenReturn(Collections.emptyList());
		when(helper.getVarietyList(anyList(), eq(tvGenreMap))).thenReturn(Collections.emptyList());
		
		/* 영화 정보 조회 */
		// TMDB API 영화 검색 URI 생성
        when(helper.getSearchUri(MOVIE_SEARCH_PATH, keyword, isAdult, 1))
        .thenReturn(MOVIE_SEARCH_PATH + "?query=블랙&isAdult=false&page=1");
        when(helper.getSearchUri(MOVIE_SEARCH_PATH, keyword, isAdult, 2))
        .thenReturn(MOVIE_SEARCH_PATH + "?query=블랙&isAdult=false&page=2");
		
		// WebClient Mock 설정
		when(uriSpec2.uri(anyString())).thenReturn(uriSpec2);
		when(uriSpec2.retrieve()).thenReturn(responseSpec2);
        
		// 첫번째 페이지 응답 설정
		TmdbSearchMovieDto movieResponse1 = new TmdbSearchMovieDto();
		TmdbSearchMovieResultsDto movieResults1 = new TmdbSearchMovieResultsDto();
		movieResults1.setGenreIds(List.of(28));
		movieResults1.setTitle("맨 인 블랙");
		List<TmdbSearchMovieResultsDto> movieResultsList1 = new ArrayList<>();
		movieResultsList1.add(movieResults1);
		movieResponse1.setResults(movieResultsList1);
		movieResponse1.setPage(1);
		movieResponse1.setTotalPages(1);
		movieResponse1.setTotalResults(1);
		// 두번째 페이지 응답 설정
		TmdbSearchMovieDto movieResponse2 = new TmdbSearchMovieDto();
		List<TmdbSearchMovieResultsDto> movieResultsList2 = new ArrayList<>();
		movieResponse2.setResults(movieResultsList2);
		// 응답 데이터 설정
		when(responseSpec2.bodyToMono(TmdbSearchMovieDto.class)).thenReturn(Mono.just(movieResponse1), Mono.just(movieResponse2));

		// 영화 검색 결과 매핑
		SearchMovieResultsDto movieResultDto1 = new SearchMovieResultsDto();
		movieResultDto1.setContentMediaType("2101");
		movieResultDto1.setGenreIds(List.of(28));
		List<SearchMovieResultsDto> movieResultsDtoList1 = new ArrayList<>();
		movieResultsDtoList1.add(movieResultDto1);
		when(mapper.movieResultsListToTmdbMovieResultsList(anyList())).thenReturn(movieResultsDtoList1);
		
		SearchVideoResponseDto responseDto = SearchVideoResponseDto.builder()
                .aniResults(aniList)
                .dramaResults(dramaList)
                .movieResults(movieResultsDtoList1)
                .build();
		when(helper.setVideoResponse(any(), any(), eq(movieGenreMap))).thenReturn(responseDto);
		
		// 서비스 메서드 호출
		SearchVideoResponseDto result = service.searchVideo(keyword, isAdult).get(5, TimeUnit.SECONDS);
		
		// 결과 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(responseDto);
		
		verify(apiFacade, times(1)).getTvGenres();
		verify(apiFacade, times(1)).getMovieGenres();
		verify(tmdbWebClient, times(4)).get();
		verify(helper, times(4)).getSearchUri(anyString(), anyString(), anyBoolean(), anyInt());
		verify(mapper, times(2)).tvResultsListToTmdbTvResultsList(anyList());
		verify(mapper, times(2)).movieResultsListToTmdbMovieResultsList(anyList());
		verify(helper, times(2)).getAniList(anyList(), eq(tvGenreMap));
		verify(helper, times(2)).getDramaList(anyList(), eq(tvGenreMap));
		verify(helper, times(2)).getDocumentaryList(anyList(), eq(tvGenreMap));
		verify(helper, times(2)).getKidsList(anyList(), eq(tvGenreMap));
		verify(helper, times(2)).getNewsList(anyList(), eq(tvGenreMap));
		verify(helper, times(2)).getVarietyList(anyList(), eq(tvGenreMap));
		verify(helper, times(1)).setVideoResponse(any(), any(), eq(movieGenreMap));
	}
	
	@Test
	@DisplayName("[UT]searchVideo: TV 시리즈 및 영화 검색 - 응답 데이터 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_searchVideo_notExistTvAndMovie() throws InterruptedException, ExecutionException, TimeoutException {
		
		String keyword = "블랙";
		boolean isAdult = false;
		
		Map<String, Integer> tvGenreMap = new HashMap<>();
		tvGenreMap.put("Animation", 16);
		tvGenreMap.put("Drama", 18);
		Map<String, Integer> movieGenreMap = new HashMap<>();
		movieGenreMap.put("Action", 28);
		
		when(apiFacade.getTvGenres()).thenReturn(Mono.just(tvGenreMap));
		when(apiFacade.getMovieGenres()).thenReturn(Mono.just(movieGenreMap));
		
		/* 애니, 드라마 정보 조회 */
		// TMDB API TV 시리즈 검색 URI 생성
		when(helper.getSearchUri(TV_SEARCH_PATH, keyword, isAdult, 1))
		.thenReturn(TV_SEARCH_PATH + "?query=블랙&isAdult=false&page=1");
		when(helper.getSearchUri(TV_SEARCH_PATH, keyword, isAdult, 2))
		.thenReturn(TV_SEARCH_PATH + "?query=블랙&isAdult=false&page=2");
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec2 = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1, uriSpec1, uriSpec2, uriSpec2);
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		when(uriSpec1.uri(anyString())).thenReturn(uriSpec1);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		ResponseSpec responseSpec2 = mock(ResponseSpec.class);

		// 응답 데이터 설정
		when(responseSpec1.bodyToMono(TmdbSearchTvDto.class)).thenReturn(Mono.empty());
		
		// TV 시리즈 검색 결과 매핑
		List<SearchTvResultsDto> tvResultsList1 = new ArrayList<>();
		List<SearchTvResultsDto> tvResultsList2 = new ArrayList<>();
		
		/* 영화 정보 조회 */
		// TMDB API 영화 검색 URI 생성
        when(helper.getSearchUri(MOVIE_SEARCH_PATH, keyword, isAdult, 1))
        .thenReturn(MOVIE_SEARCH_PATH + "?query=블랙&isAdult=false&page=1");
        when(helper.getSearchUri(MOVIE_SEARCH_PATH, keyword, isAdult, 2))
        .thenReturn(MOVIE_SEARCH_PATH + "?query=블랙&isAdult=false&page=2");
		
		// WebClient Mock 설정
		when(uriSpec2.uri(anyString())).thenReturn(uriSpec2);
		when(uriSpec2.retrieve()).thenReturn(responseSpec2);
        
		// 응답 데이터 설정
		when(responseSpec2.bodyToMono(TmdbSearchMovieDto.class)).thenReturn(Mono.empty());

		// 영화 검색 결과 매핑
		List<SearchMovieResultsDto> movieResultsDtoList1 = new ArrayList<>();
		
		SearchVideoResponseDto responseDto = SearchVideoResponseDto.builder()
                .aniResults(tvResultsList1)
                .dramaResults(tvResultsList2)
                .movieResults(movieResultsDtoList1)
                .build();
		when(helper.setVideoResponse(any(), any(), eq(movieGenreMap))).thenReturn(responseDto);
		
		// 서비스 메서드 호출
		SearchVideoResponseDto result = service.searchVideo(keyword, isAdult).get(5, TimeUnit.SECONDS);
		
		// 결과 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(responseDto);
		
		verify(apiFacade, times(1)).getTvGenres();
		verify(apiFacade, times(1)).getMovieGenres();
		verify(tmdbWebClient, times(4)).get();
		verify(helper, times(4)).getSearchUri(anyString(), anyString(), anyBoolean(), anyInt());
		verify(helper, times(1)).setVideoResponse(any(), any(), eq(movieGenreMap));
	}
	
	@Test
	@DisplayName("[UT]searchAni: 애니 검색 데이터 조회 - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_searchAni_existResponse() throws InterruptedException, ExecutionException, TimeoutException {
		
		String keyword = "블랙";
		boolean isAdult = false;
		Integer page = 1;
		
		Map<String, Integer> tvGenreMap = new HashMap<>();
		tvGenreMap.put("Animation", 16);
		tvGenreMap.put("Drama", 18);
		Map<String, Integer> movieGenreMap = new HashMap<>();
		movieGenreMap.put("Action", 28);
		
		when(apiFacade.getTvGenres()).thenReturn(Mono.just(tvGenreMap));
		when(apiFacade.getMovieGenres()).thenReturn(Mono.just(movieGenreMap));
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec2 = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1, uriSpec2);
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		ResponseSpec responseSpec2 = mock(ResponseSpec.class);
		when(uriSpec1.uri(anyString())).thenReturn(uriSpec1);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		when(uriSpec2.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())).thenReturn(uriSpec2);
		when(uriSpec2.retrieve()).thenReturn(responseSpec2);
		
		// TMDB API TV 시리즈 검색 URI 생성
		when(helper.getSearchUri(TV_SEARCH_PATH, keyword, isAdult, 1))
		.thenReturn(TV_SEARCH_PATH + "?query=블랙&isAdult=false&page=1");
		
		// TV 시리즈 응답 설정	
		TmdbSearchTvDto tvResponse = new TmdbSearchTvDto();
		TmdbSearchTvResultsDto tmdbTvResults = new TmdbSearchTvResultsDto();
		tmdbTvResults.setGenreIds(List.of(16));
		tmdbTvResults.setName("블랙클로버");
		List<TmdbSearchTvResultsDto> tmdbTvResultsList = new ArrayList<>();
		tmdbTvResultsList.add(tmdbTvResults);
		tvResponse.setResults(tmdbTvResultsList);
		tvResponse.setPage(1);
		tvResponse.setTotalPages(1);
		tvResponse.setTotalResults(1);
		when(responseSpec1.bodyToMono(TmdbSearchTvDto.class)).thenReturn(Mono.just(tvResponse));
		
		// 영화 응답 설정
		TmdbSearchMovieDto movieResponse = new TmdbSearchMovieDto();
		List<TmdbSearchMovieResultsDto> tmdbMovieResultsList = new ArrayList<>();
		TmdbSearchMovieResultsDto tmdbMovieResults = new TmdbSearchMovieResultsDto();
		tmdbMovieResults.setGenreIds(List.of(28));
		tmdbMovieResults.setTitle("맨 인 블랙");
		tmdbMovieResultsList.add(tmdbMovieResults);
		movieResponse.setResults(tmdbMovieResultsList);
		movieResponse.setPage(1);
		movieResponse.setTotalPages(1);
		movieResponse.setTotalResults(1);
		when(responseSpec2.bodyToMono(TmdbSearchMovieDto.class)).thenReturn(Mono.just(movieResponse));
		
		// TV 시리즈 검색 결과 매핑
		SearchTvResultsDto tvResult = new SearchTvResultsDto();
		tvResult.setContentMediaType("1101");
		tvResult.setGenreIds(List.of(16));
		tvResult.setName("블랙클로버");
		List<SearchTvResultsDto> tvResultsList = new ArrayList<>();
		tvResultsList.add(tvResult);
		when(mapper.tvResultsListToTmdbTvResultsList(anyList())).thenReturn(tvResultsList);
		when(helper.getAniList(anyList(), eq(tvGenreMap))).thenReturn(tvResultsList);
		
		// 영화 검색 결과 매핑
		SearchMovieResultsDto movieResultDto = new SearchMovieResultsDto();
		movieResultDto.setContentMediaType("2101");
		movieResultDto.setGenreIds(List.of(28));
		movieResultDto.setTitle("맨 인 블랙");
		List<SearchMovieResultsDto> movieResultsDtoList = new ArrayList<>();
		movieResultsDtoList.add(movieResultDto);
		when(mapper.movieResultsListToTmdbMovieResultsList(anyList()))
		.thenReturn(movieResultsDtoList);
		when(helper.getAniMovieList(anyList(), eq(movieGenreMap)))
		.thenReturn(new ArrayList<>());
		
		// 서비스 메서드 호출
		SearchTvResponseDto result = service.searchAni(keyword, isAdult, page).get(5, TimeUnit.SECONDS);

		// 결과 검증
		SearchTvResponseDto expectedResult = SearchTvResponseDto.builder()
				.aniResults(tvResultsList)
				.page(1)
				.totalPages(1)
				.build();
		assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
		verify(apiFacade, times(1)).getTvGenres();
		verify(apiFacade, times(1)).getMovieGenres();
		verify(tmdbWebClient, times(2)).get();
		verify(helper, times(1)).getSearchUri(anyString(), anyString(), anyBoolean(), anyInt());
		verify(mapper, times(1)).tvResultsListToTmdbTvResultsList(anyList());
		verify(mapper, times(1)).movieResultsListToTmdbMovieResultsList(anyList());
		verify(helper, times(1)).getAniList(anyList(), eq(tvGenreMap));
		verify(helper, times(1)).getAniMovieList(anyList(), eq(movieGenreMap));
	}
	
	@Test
	@DisplayName("[UT]searchAni: 애니 검색 데이터 조회 - 응답 데이터 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_searchAni_notExistResponse() throws InterruptedException, ExecutionException, TimeoutException {
		
		String keyword = "블랙";
		boolean isAdult = false;
		Integer page = 1;
		
		Map<String, Integer> tvGenreMap = new HashMap<>();
		tvGenreMap.put("Animation", 16);
		tvGenreMap.put("Drama", 18);
		Map<String, Integer> movieGenreMap = new HashMap<>();
		movieGenreMap.put("Action", 28);
		
		when(apiFacade.getTvGenres()).thenReturn(Mono.just(tvGenreMap));
		when(apiFacade.getMovieGenres()).thenReturn(Mono.just(movieGenreMap));
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec2 = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1, uriSpec2);
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		ResponseSpec responseSpec2 = mock(ResponseSpec.class);
		when(uriSpec1.uri(anyString())).thenReturn(uriSpec1);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		when(uriSpec2.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())).thenReturn(uriSpec2);
		when(uriSpec2.retrieve()).thenReturn(responseSpec2);
		
		// TMDB API TV 시리즈 검색 URI 생성
		when(helper.getSearchUri(TV_SEARCH_PATH, keyword, isAdult, 1))
		.thenReturn(TV_SEARCH_PATH + "?query=블랙&isAdult=false&page=1");
		
		// TV 시리즈 응답 설정
		TmdbSearchTvDto tvResponse = new TmdbSearchTvDto();
		when(responseSpec1.bodyToMono(TmdbSearchTvDto.class)).thenReturn(Mono.just(tvResponse));
		
		// 영화 응답 설정
		TmdbSearchMovieDto movieResponse = new TmdbSearchMovieDto();
		when(responseSpec2.bodyToMono(TmdbSearchMovieDto.class)).thenReturn(Mono.just(movieResponse));

		// 서비스 메서드 호출
		SearchTvResponseDto result = service.searchAni(keyword, isAdult, page).get(5, TimeUnit.SECONDS);

		// 결과 검증
		SearchTvResponseDto expectedResult = new SearchTvResponseDto();
		assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
		verify(apiFacade, times(1)).getTvGenres();
		verify(apiFacade, times(1)).getMovieGenres();
		verify(tmdbWebClient, times(2)).get();
		verify(helper, times(1)).getSearchUri(anyString(), anyString(), anyBoolean(), anyInt());
	}
	
	@Test
	@DisplayName("[UT]searchAni: 애니 검색 데이터 조회 - TV 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_searchAni_existTvResponse() throws InterruptedException, ExecutionException, TimeoutException {
		
		String keyword = "블랙";
		boolean isAdult = false;
		Integer page = 1;
		
		Map<String, Integer> tvGenreMap = new HashMap<>();
		tvGenreMap.put("Animation", 16);
		tvGenreMap.put("Drama", 18);
		Map<String, Integer> movieGenreMap = new HashMap<>();
		movieGenreMap.put("Action", 28);
		
		when(apiFacade.getTvGenres()).thenReturn(Mono.just(tvGenreMap));
		when(apiFacade.getMovieGenres()).thenReturn(Mono.just(movieGenreMap));
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec2 = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1, uriSpec2);
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		ResponseSpec responseSpec2 = mock(ResponseSpec.class);
		when(uriSpec1.uri(anyString())).thenReturn(uriSpec1);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		when(uriSpec2.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())).thenReturn(uriSpec2);
		when(uriSpec2.retrieve()).thenReturn(responseSpec2);
		
		// TMDB API TV 시리즈 검색 URI 생성
		when(helper.getSearchUri(TV_SEARCH_PATH, keyword, isAdult, 1))
		.thenReturn(TV_SEARCH_PATH + "?query=블랙&isAdult=false&page=1");
		
		// TV 시리즈 응답 설정
		TmdbSearchTvDto tvResponse = new TmdbSearchTvDto();
		TmdbSearchTvResultsDto tmdbTvResults = new TmdbSearchTvResultsDto();
		tmdbTvResults.setGenreIds(List.of(16));
		tmdbTvResults.setName("블랙클로버");
		List<TmdbSearchTvResultsDto> tmdbTvResultsList = new ArrayList<>();
		tmdbTvResultsList.add(tmdbTvResults);
		tvResponse.setResults(tmdbTvResultsList);
		tvResponse.setPage(1);
		tvResponse.setTotalPages(1);
		tvResponse.setTotalResults(1);
		when(responseSpec1.bodyToMono(TmdbSearchTvDto.class)).thenReturn(Mono.just(tvResponse));
		
		// 영화 응답 설정
		TmdbSearchMovieDto movieResponse = new TmdbSearchMovieDto();
		when(responseSpec2.bodyToMono(TmdbSearchMovieDto.class)).thenReturn(Mono.just(movieResponse));
		
		// TV 시리즈 검색 결과 매핑
		SearchTvResultsDto tvResult = new SearchTvResultsDto();
		tvResult.setContentMediaType("1101");
		tvResult.setGenreIds(List.of(16));
		tvResult.setName("블랙클로버");
		List<SearchTvResultsDto> tvResultsList = new ArrayList<>();
		tvResultsList.add(tvResult);
		when(mapper.tvResultsListToTmdbTvResultsList(anyList())).thenReturn(tvResultsList);
		when(helper.getAniList(anyList(), eq(tvGenreMap))).thenReturn(tvResultsList);

		// 서비스 메서드 호출
		SearchTvResponseDto result = service.searchAni(keyword, isAdult, page).get(5, TimeUnit.SECONDS);

		// 결과 검증
		SearchTvResponseDto expectedResult = SearchTvResponseDto.builder()
				.aniResults(tvResultsList)
				.page(1)
				.totalPages(1)
				.build();
		assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
		verify(apiFacade, times(1)).getTvGenres();
		verify(apiFacade, times(1)).getMovieGenres();
		verify(tmdbWebClient, times(2)).get();
		verify(helper, times(1)).getSearchUri(anyString(), anyString(), anyBoolean(), anyInt());
		verify(mapper, times(1)).tvResultsListToTmdbTvResultsList(anyList());
		verify(helper, times(1)).getAniList(anyList(), eq(tvGenreMap));
	}
	
	@Test
	@DisplayName("[UT]searchAni: 애니 검색 데이터 조회 - Movie 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_searchAni_existMovieResponse() throws InterruptedException, ExecutionException, TimeoutException {
		
		String keyword = "귀멸의 칼날";
		boolean isAdult = false;
		Integer page = 1;
		
		Map<String, Integer> tvGenreMap = new HashMap<>();
		tvGenreMap.put("Animation", 16);
		tvGenreMap.put("Drama", 18);
		Map<String, Integer> movieGenreMap = new HashMap<>();
		movieGenreMap.put("Action", 28);
		
		when(apiFacade.getTvGenres()).thenReturn(Mono.just(tvGenreMap));
		when(apiFacade.getMovieGenres()).thenReturn(Mono.just(movieGenreMap));
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec1 = mock(RequestHeadersUriSpec.class);
		RequestHeadersUriSpec uriSpec2 = mock(RequestHeadersUriSpec.class);
		when(tmdbWebClient.get()).thenReturn(uriSpec1, uriSpec2);
		ResponseSpec responseSpec1 = mock(ResponseSpec.class);
		ResponseSpec responseSpec2 = mock(ResponseSpec.class);
		when(uriSpec1.uri(anyString())).thenReturn(uriSpec1);
		when(uriSpec1.retrieve()).thenReturn(responseSpec1);
		when(uriSpec2.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())).thenReturn(uriSpec2);
		when(uriSpec2.retrieve()).thenReturn(responseSpec2);
		
		// TMDB API TV 시리즈 검색 URI 생성
		when(helper.getSearchUri(TV_SEARCH_PATH, keyword, isAdult, 1))
		.thenReturn(TV_SEARCH_PATH + "?query=블랙&isAdult=false&page=1");
		
		// TV 시리즈 응답 설정
		TmdbSearchTvDto tvResponse = new TmdbSearchTvDto();
		when(responseSpec1.bodyToMono(TmdbSearchTvDto.class)).thenReturn(Mono.just(tvResponse));
		
		// 영화 응답 설정
		TmdbSearchMovieDto movieResponse = new TmdbSearchMovieDto();
		List<TmdbSearchMovieResultsDto> tmdbMovieResultsList = new ArrayList<>();
		TmdbSearchMovieResultsDto tmdbMovieResults = new TmdbSearchMovieResultsDto();
		tmdbMovieResults.setGenreIds(List.of(16));
		tmdbMovieResults.setTitle("극장판 귀멸의 칼날: 무한성편");
		tmdbMovieResultsList.add(tmdbMovieResults);
		movieResponse.setResults(tmdbMovieResultsList);
		movieResponse.setPage(1);
		movieResponse.setTotalPages(1);
		movieResponse.setTotalResults(1);
		when(responseSpec2.bodyToMono(TmdbSearchMovieDto.class)).thenReturn(Mono.just(movieResponse));
		
		// 영화 검색 결과 매핑
		SearchMovieResultsDto movieResultsDto = new SearchMovieResultsDto();
		movieResultsDto.setContentMediaType("2101");
		movieResultsDto.setGenreIds(List.of(16));
		movieResultsDto.setTitle("극장판 귀멸의 칼날: 무한성편");
		List<SearchMovieResultsDto> movieResultsDtoList = new ArrayList<>();
		movieResultsDtoList.add(movieResultsDto);
        when(mapper.movieResultsListToTmdbMovieResultsList(anyList()))
        .thenReturn(movieResultsDtoList);
        SearchTvResultsDto tvResult = new SearchTvResultsDto();
        tvResult.setContentMediaType(movieResultsDto.getContentMediaType());
        tvResult.setGenreIds(movieResultsDto.getGenreIds());
        tvResult.setName(movieResultsDto.getTitle());
        List<SearchTvResultsDto> tvResultsList = new ArrayList<>();
        tvResultsList.add(tvResult);
        when(helper.getAniMovieList(anyList(), eq(movieGenreMap)))
        .thenReturn(tvResultsList);

		// 서비스 메서드 호출
		SearchTvResponseDto result = service.searchAni(keyword, isAdult, page).get(5, TimeUnit.SECONDS);

		// 결과 검증
		SearchTvResponseDto expectedResult = SearchTvResponseDto.builder()
				.aniResults(tvResultsList)
				.page(1)
				.totalPages(1)
				.build();
		assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
		verify(apiFacade, times(1)).getTvGenres();
		verify(apiFacade, times(1)).getMovieGenres();
		verify(tmdbWebClient, times(2)).get();
		verify(helper, times(1)).getSearchUri(anyString(), anyString(), anyBoolean(), anyInt());
		verify(mapper, times(1)).movieResultsListToTmdbMovieResultsList(anyList());
		verify(helper, times(1)).getAniMovieList(anyList(), eq(movieGenreMap));
	}
	
	@Test
	@DisplayName("[UT]searchTvExceptAni: TV 시리즈 검색 데이터 조회(애니 제외) - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_searchTvExceptAni_existResponse() throws InterruptedException, ExecutionException, TimeoutException {
		
		String keyword = "뉴스";
        boolean isAdult = false;
        Integer page = 1;
        String contentMediaType = "1101";
        
        Map<String, Integer> tvGenreMap = new HashMap<>();
        tvGenreMap.put("Animation", 16);
        tvGenreMap.put("Drama", 18);
        tvGenreMap.put("News", 10763);
        when(apiFacade.getTvGenres()).thenReturn(Mono.just(tvGenreMap));
        
        // WebClient Mock 설정
        RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
        when(tmdbWebClient.get()).thenReturn(uriSpec);
        ResponseSpec responseSpec = mock(ResponseSpec.class);
        when(uriSpec.uri(anyString())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        
        // TMDB API TV 시리즈 검색 URI 생성
        when(helper.getSearchUri(TV_SEARCH_PATH, keyword, isAdult, 1))
        .thenReturn(TV_SEARCH_PATH + "?query=뉴스&isAdult=false&page=1");
        
        // TV 시리즈 응답 설정	
        TmdbSearchTvDto tvResponse = new TmdbSearchTvDto();
        TmdbSearchTvResultsDto tmdbTvResults = new TmdbSearchTvResultsDto();
        tmdbTvResults.setGenreIds(List.of(10763));
        tmdbTvResults.setName("MBC 뉴스데스크");
        List<TmdbSearchTvResultsDto> tmdbTvResultsList = new ArrayList<>();
        tmdbTvResultsList.add(tmdbTvResults);
        tvResponse.setResults(tmdbTvResultsList);
        tvResponse.setPage(1);
        tvResponse.setTotalPages(1);
        tvResponse.setTotalResults(1);
        when(responseSpec.bodyToMono(TmdbSearchTvDto.class)).thenReturn(Mono.just(tvResponse));
        
        // TV 시리즈 검색 결과 매핑
        SearchTvResultsDto tvResult = new SearchTvResultsDto();
        tvResult.setContentMediaType(contentMediaType);
        tvResult.setGenreIds(tmdbTvResults.getGenreIds());
        tvResult.setName(tmdbTvResults.getName());
        List<SearchTvResultsDto> tvResultsList = new ArrayList<>();
		tvResultsList.add(tvResult);
		when(mapper.tvResultsListToTmdbTvResultsList(anyList())).thenReturn(tvResultsList);
		when(helper.getTvListOfMediaType(anyList(), eq(tvGenreMap), eq(contentMediaType))).thenReturn(tvResultsList);

		// 서비스 메서드 호출
		SearchTvResponseDto result = service.searchTvExceptAni(keyword, isAdult, contentMediaType, page).get(5, TimeUnit.SECONDS);

		// 결과 검증
		SearchTvResponseDto expectedResult = SearchTvResponseDto.builder()
				.dramaResults(tvResultsList)
				.page(tvResponse.getPage())
				.totalPages(tvResponse.getTotalPages())
				.totalResults(tvResponse.getTotalResults())
				.build();
		assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
		verify(apiFacade, times(1)).getTvGenres();
		verify(tmdbWebClient, times(1)).get();
		verify(helper, times(1)).getSearchUri(anyString(), anyString(), anyBoolean(), anyInt());
		verify(mapper, times(1)).tvResultsListToTmdbTvResultsList(anyList());
		verify(helper, times(1)).getTvListOfMediaType(anyList(), eq(tvGenreMap), eq(contentMediaType));
	}
	
	@Test
	@DisplayName("[UT]searchMovie: 영화 검색 데이터 조회 - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_searchMovie_existResponse() throws InterruptedException, ExecutionException, TimeoutException {
		
		String keyword = "어벤져스";
        boolean isAdult = false;
        Integer page = 1;
        String contentMediaType = "2101";
        
        Map<String, Integer> movieGenreMap = new HashMap<>();
		movieGenreMap.put("Action", 28);
		when(apiFacade.getMovieGenres()).thenReturn(Mono.just(movieGenreMap));
        
        // WebClient Mock 설정
        RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
        when(tmdbWebClient.get()).thenReturn(uriSpec);
        ResponseSpec responseSpec = mock(ResponseSpec.class);
        when(uriSpec.uri(anyString())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        
        // TMDB API 영화 검색 URI 생성
        when(helper.getSearchUri(MOVIE_SEARCH_PATH, keyword, isAdult, 1))
        .thenReturn(MOVIE_SEARCH_PATH + "?query=어벤져스&isAdult=false&page=1");
        
        // 영화 응답 설정
        TmdbSearchMovieDto movieResponse = new TmdbSearchMovieDto();
        TmdbSearchMovieResultsDto tmdbMovieResults = new TmdbSearchMovieResultsDto();
        tmdbMovieResults.setGenreIds(List.of(28));
        tmdbMovieResults.setTitle("어벤져스: 엔드게임");
        List<TmdbSearchMovieResultsDto> tmdbMovieResultsList = new ArrayList<>();
        tmdbMovieResultsList.add(tmdbMovieResults);
        movieResponse.setResults(tmdbMovieResultsList);
        movieResponse.setPage(1);
        movieResponse.setTotalPages(1);
		movieResponse.setTotalResults(1);
		when(responseSpec.bodyToMono(TmdbSearchMovieDto.class)).thenReturn(Mono.just(movieResponse));

		// 영화 검색 결과 매핑
		SearchMovieResultsDto movieResult = new SearchMovieResultsDto();
		movieResult.setContentMediaType(contentMediaType);
		movieResult.setGenreIds(tmdbMovieResults.getGenreIds());
		movieResult.setTitle(tmdbMovieResults.getTitle());
		List<SearchMovieResultsDto> movieResultsList = new ArrayList<>();
		movieResultsList.add(movieResult);
		when(mapper.movieResultsListToTmdbMovieResultsList(anyList())).thenReturn(movieResultsList);
		when(helper.getMovieList(anyList(), eq(movieGenreMap)))
				.thenReturn(movieResultsList);

		// 서비스 메서드 호출
		SearchMovieResponseDto result = service.searchMovie(keyword, isAdult, page).get(5, TimeUnit.SECONDS);

		// 결과 검증
		SearchMovieResponseDto expectedResult = SearchMovieResponseDto.builder()
				.movieResults(movieResultsList)
				.page(movieResponse.getPage())
				.totalPages(movieResponse.getTotalPages())
				.totalResults(movieResponse.getTotalResults())
				.build();
		assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
		verify(apiFacade, times(1)).getMovieGenres();
		verify(tmdbWebClient, times(1)).get();
		verify(helper, times(1)).getSearchUri(anyString(), anyString(), anyBoolean(), anyInt());
		verify(mapper, times(1)).movieResultsListToTmdbMovieResultsList(anyList());
		verify(helper, times(1)).getMovieList(anyList(), eq(movieGenreMap));
	}
	
	@Test
	@DisplayName("[UT]searchComics: 만화 검색 데이터 조회 - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_searchComics_existResponse() throws InterruptedException, ExecutionException, TimeoutException {
		
		try (MockedStatic<GraphqlUtil> mocked = Mockito.mockStatic(GraphqlUtil.class)) {
			String keyword = "원피스";
			boolean isAdult = false;
			Integer page = 1;
			boolean isMainPage = true;

			when(apiFacade.getTranslationText(keyword, DomainConstants.API_LANGUAGE_JAPANESE, DomainConstants.API_LANGUAGE_KOREAN))
			.thenReturn(CompletableFuture.completedFuture("ワンピース"));

			String query = "graphql query string";
			String requestBody = "{ requestBody }";
			mocked.when(() -> GraphqlUtil.loadQuery("comicsList.graphql")).thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(eq(query), anyMap())).thenReturn(requestBody);
			
			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(anyString())).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);
			
			// 만화 응답 설정
			AniListResponseDto anilistResponse = new AniListResponseDto();
			AniListDataDto dataDto = new AniListDataDto();
			AniListMediaDto mediaDto = new AniListMediaDto();
			AniListPageDto pageDto = new AniListPageDto();
			AniListPageInfoDto pageInfoDto = new AniListPageInfoDto();
			pageInfoDto.setTotal(1);
			pageInfoDto.setCurrentPage(page);
			pageInfoDto.setLastPage(1);
			pageDto.setPageInfo(pageInfoDto);
			dataDto.setPage(pageDto);
			List<AniListMediaDto> mediaList = new ArrayList<>();
			AniListMediaDto comicsMedia = new AniListMediaDto();
			AniListTitleDto titleDto = new AniListTitleDto();
			titleDto.setUserPreferred("원피스");
			comicsMedia.setTitle(titleDto);
			mediaList.add(new AniListMediaDto());
			pageDto.setMedia(mediaList);
			dataDto.setMedia(mediaDto);
			anilistResponse.setData(dataDto);
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(anilistResponse));
			
			// 만화 검색 결과 매핑
			List<SearchComicsResultDto> comicsResultsList = new ArrayList<>();
			SearchComicsResultDto comicsResult = SearchComicsResultDto.builder().title(titleDto.getUserPreferred()).build();
			comicsResultsList.add(comicsResult);
			when(helper.setComicsResponse(mediaList)).thenReturn(comicsResultsList);
			
			// 서비스 메서드 호출
			SearchComicsResponseDto result = service.searchComics(keyword, isAdult, page, isMainPage).get(5, TimeUnit.SECONDS);

			// 결과 검증
			SearchComicsResponseDto expectedResult = SearchComicsResponseDto.builder()
					.page(pageInfoDto.getCurrentPage())
					.totalPages(pageInfoDto.getLastPage())
					.isComicsViewMore(pageInfoDto.getCurrentPage() < pageInfoDto.getLastPage())
					.comicsResults(comicsResultsList)
					.build();
			assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);

			verify(apiFacade, times(1)).getTranslationText(keyword, DomainConstants.API_LANGUAGE_JAPANESE, DomainConstants.API_LANGUAGE_KOREAN);
			verify(anilistWebClient, times(1)).post();
			verify(helper, times(1)).setComicsResponse(any());
		}
	}
	
	@Test
	@DisplayName("[UT]searchComics: 만화 검색 데이터 조회 - 응답 데이터(Data) 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_searchComics_notExistData() throws InterruptedException, ExecutionException, TimeoutException {
		
		try (MockedStatic<GraphqlUtil> mocked = Mockito.mockStatic(GraphqlUtil.class)) {
			String keyword = "원피스";
			boolean isAdult = true;
			Integer page = 1;
			boolean isMainPage = false;

			when(apiFacade.getTranslationText(keyword, DomainConstants.API_LANGUAGE_JAPANESE, DomainConstants.API_LANGUAGE_KOREAN))
			.thenReturn(CompletableFuture.completedFuture("ワンピース"));
			String query = "graphql query string";
			String requestBody = "{ requestBody }";
			mocked.when(() -> GraphqlUtil.loadQuery("comicsList.graphql")).thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(eq(query), anyMap())).thenReturn(requestBody);
			
			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(anyString())).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);
			
			// 만화 응답 설정
			AniListResponseDto anilistResponse = new AniListResponseDto();
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(anilistResponse));
			
			// 서비스 메서드 호출
			SearchComicsResponseDto result = service.searchComics(keyword, isAdult, page, isMainPage).get(5, TimeUnit.SECONDS);

			// 결과 검증
			SearchComicsResponseDto expectedResult = new SearchComicsResponseDto();
			assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);

			verify(apiFacade, times(1)).getTranslationText(keyword, DomainConstants.API_LANGUAGE_JAPANESE, DomainConstants.API_LANGUAGE_KOREAN);
			verify(anilistWebClient, times(1)).post();
		}
	}
	
	@Test
	@DisplayName("[UT]searchComics: 만화 검색 데이터 조회 - 응답 데이터(Page) 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_searchComics_notExistPage() throws InterruptedException, ExecutionException, TimeoutException {
		
		try (MockedStatic<GraphqlUtil> mocked = Mockito.mockStatic(GraphqlUtil.class)) {
			String keyword = "원피스";
			boolean isAdult = false;
			Integer page = 1;
			boolean isMainPage = true;

			when(apiFacade.getTranslationText(keyword, DomainConstants.API_LANGUAGE_JAPANESE, DomainConstants.API_LANGUAGE_KOREAN))
			.thenReturn(CompletableFuture.completedFuture("ワンピース"));
			String query = "graphql query string";
			String requestBody = "{ requestBody }";
			mocked.when(() -> GraphqlUtil.loadQuery("comicsList.graphql")).thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(eq(query), anyMap())).thenReturn(requestBody);
			
			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(anyString())).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);
			
			// 만화 응답 설정
			AniListResponseDto anilistResponse = new AniListResponseDto();
			AniListDataDto dataDto = new AniListDataDto();
			anilistResponse.setData(dataDto);
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(anilistResponse));
			
			// 서비스 메서드 호출
			SearchComicsResponseDto result = service.searchComics(keyword, isAdult, page, isMainPage).get(5, TimeUnit.SECONDS);

			// 결과 검증
			SearchComicsResponseDto expectedResult = new SearchComicsResponseDto();
			assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);

			verify(apiFacade, times(1)).getTranslationText(keyword, DomainConstants.API_LANGUAGE_JAPANESE, DomainConstants.API_LANGUAGE_KOREAN);
			verify(anilistWebClient, times(1)).post();
		}
	}
	
	@Test
	@DisplayName("[UT]searchComics: 만화 검색 데이터 조회 - 응답 데이터(Media) 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_searchComics_notExistMedia() throws InterruptedException, ExecutionException, TimeoutException {
		
		try (MockedStatic<GraphqlUtil> mocked = Mockito.mockStatic(GraphqlUtil.class)) {
			String keyword = "원피스";
			boolean isAdult = false;
			Integer page = 1;
			boolean isMainPage = true;

			when(apiFacade.getTranslationText(keyword, DomainConstants.API_LANGUAGE_JAPANESE, DomainConstants.API_LANGUAGE_KOREAN))
			.thenReturn(CompletableFuture.completedFuture("ワンピース"));
			String query = "graphql query string";
			String requestBody = "{ requestBody }";
			mocked.when(() -> GraphqlUtil.loadQuery("comicsList.graphql")).thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(eq(query), anyMap())).thenReturn(requestBody);
			
			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(anyString())).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);
			
			// 만화 응답 설정
			AniListResponseDto anilistResponse = new AniListResponseDto();
			AniListDataDto dataDto = new AniListDataDto();
			AniListPageDto pageDto = new AniListPageDto();
			dataDto.setPage(pageDto);
			anilistResponse.setData(dataDto);
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(anilistResponse));
			
			// 서비스 메서드 호출
			SearchComicsResponseDto result = service.searchComics(keyword, isAdult, page, isMainPage).get(5, TimeUnit.SECONDS);

			// 결과 검증
			SearchComicsResponseDto expectedResult = new SearchComicsResponseDto();
			assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);

			verify(apiFacade, times(1)).getTranslationText(keyword, DomainConstants.API_LANGUAGE_JAPANESE, DomainConstants.API_LANGUAGE_KOREAN);
			verify(anilistWebClient, times(1)).post();
		}
	}
	
	@Test
	@DisplayName("[UT]searchComics: 만화 검색 데이터 조회 - 응답 데이터(PageInfo) 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_searchComics_notExistPageInfo() throws InterruptedException, ExecutionException, TimeoutException {
		
		try (MockedStatic<GraphqlUtil> mocked = Mockito.mockStatic(GraphqlUtil.class)) {
			String keyword = "원피스";
			boolean isAdult = false;
			Integer page = 1;
			boolean isMainPage = true;

			when(apiFacade.getTranslationText(keyword, DomainConstants.API_LANGUAGE_JAPANESE, DomainConstants.API_LANGUAGE_KOREAN))
			.thenReturn(CompletableFuture.completedFuture("ワンピース"));
			String query = "graphql query string";
			String requestBody = "{ requestBody }";
			mocked.when(() -> GraphqlUtil.loadQuery("comicsList.graphql")).thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(eq(query), anyMap())).thenReturn(requestBody);
			
			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(anyString())).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);
			
			// 만화 응답 설정
			AniListResponseDto anilistResponse = new AniListResponseDto();
			AniListDataDto dataDto = new AniListDataDto();
			AniListPageDto pageDto = new AniListPageDto();
			AniListMediaDto mediaDto = new AniListMediaDto();
			pageDto.setMedia(List.of(mediaDto));
			dataDto.setPage(pageDto);
			anilistResponse.setData(dataDto);
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(anilistResponse));
			
			// 서비스 메서드 호출
			SearchComicsResponseDto result = service.searchComics(keyword, isAdult, page, isMainPage).get(5, TimeUnit.SECONDS);

			// 결과 검증
			SearchComicsResponseDto expectedResult = new SearchComicsResponseDto();
			assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);

			verify(apiFacade, times(1)).getTranslationText(keyword, DomainConstants.API_LANGUAGE_JAPANESE, DomainConstants.API_LANGUAGE_KOREAN);
			verify(anilistWebClient, times(1)).post();
		}
	}
	
	@Test
	@DisplayName("[UT]searchComics: 만화 검색 데이터 조회 - graphql 쿼리 파일 없음")
	void test_searchComics_graphqlFileError() {
		
		try (MockedStatic<GraphqlUtil> mocked = Mockito.mockStatic(GraphqlUtil.class)) {
			String keyword = "원피스";
			boolean isAdult = false;
			Integer page = 1;
			boolean isMainPage = true;
			String errorMessage = "File not found";

			when(apiFacade.getTranslationText(keyword, DomainConstants.API_LANGUAGE_JAPANESE, DomainConstants.API_LANGUAGE_KOREAN))
			.thenReturn(CompletableFuture.completedFuture("ワンピース"));
			mocked.when(() -> GraphqlUtil.loadQuery("comicsList.graphql")).thenThrow(new IOException(errorMessage));
			
			// 서비스 메서드 호출 및 예외 검증
			assertThatThrownBy(() -> service.searchComics(keyword, isAdult, page, isMainPage).get(5, TimeUnit.SECONDS))
			.isInstanceOf(ExecutionException.class)
			.hasCauseInstanceOf(IOException.class)
			.hasMessageContaining(errorMessage);
			
			verify(apiFacade, times(1)).getTranslationText(keyword, DomainConstants.API_LANGUAGE_JAPANESE, DomainConstants.API_LANGUAGE_KOREAN);
		}
	}

}
