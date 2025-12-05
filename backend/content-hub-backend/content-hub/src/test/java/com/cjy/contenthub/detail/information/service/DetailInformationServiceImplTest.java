package com.cjy.contenthub.detail.information.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;

import com.cjy.contenthub.common.integration.tmdb.dto.TmdbGenreDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbGenreListDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbMovieDetailsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbTvDetailsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbVideoCreditsCastDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbVideoCreditsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbWatchProvidersDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbWatchProvidersResultsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbWatchProvidersTypeDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailMovieResponseDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailTvResponseDto;
import com.cjy.contenthub.detail.information.mapper.DetailInformationMapper;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class DetailInformationServiceImplTest {

	@InjectMocks
	DetailInformationServiceImpl service;

	@Mock
	WebClient tmdbWebClient;

	@Mock
	DetailInformationMapper detailInformationMapper;
	
	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(service, "tvDetailPath", "/3/tv/%s");
		ReflectionTestUtils.setField(service, "movieDetailPath", "/3/movie/%s");
	}

	@Test
	@DisplayName("getTvDetail: TV 상세 정보 조회")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getTvDetail() {
		// 파라미터 설정
		Integer seriesId = 1000;
		String contentMediaType = "1101";
		// WenClient Mock 설정
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
		when(detailInformationMapper.detailTvToDetailTvResponse(tmdbTvDetailsDto))
		.thenReturn(response);

		// 서비스 메서드 호출
		DetailTvResponseDto result = service.getTvDetail(seriesId, contentMediaType);
		
		// 결과 검증
		DetailTvResponseDto expectedDto = new DetailTvResponseDto();
		expectedDto.setCredits(tmdbVideoCreditsDto);
		expectedDto.setLink(link);
		expectedDto.setGenreIds(tmdbTvDetailsDto.getGenres().stream().map(TmdbGenreDto::getId).toList());
		assertThat(result).usingRecursiveComparison().isEqualTo(expectedDto);
		
		// Mock 호출 검증
		verify(tmdbWebClient, times(3)).get();
		verify(detailInformationMapper, times(1)).detailTvToDetailTvResponse(tmdbTvDetailsDto);
	}
	
	@Test
	@DisplayName("getMovieDetail: 영화 상세 정보 조회")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getMovieDetail() {
		// 파라미터 설정
		Integer movieId = 2000;
		String contentMediaType = "1201";
		// WenClient Mock 설정
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
		when(detailInformationMapper.detailMovieToDetailMovieResponse(tmdbMovieDetailsDto))
		.thenReturn(response);

		// 서비스 메서드 호출
		DetailMovieResponseDto result = service.getMovieDetail(movieId, contentMediaType);
		
		// 결과 검증
		DetailMovieResponseDto expectedDto = new DetailMovieResponseDto();
		expectedDto.setCredits(tmdbVideoCreditsDto);
		expectedDto.setLink(link);
		expectedDto.setGenreIds(tmdbMovieDetailsDto.getGenres().stream().map(TmdbGenreDto::getId).toList());
		assertThat(result).usingRecursiveComparison().isEqualTo(expectedDto);
		
		// Mock 호출 검증
		verify(tmdbWebClient, times(3)).get();
		verify(detailInformationMapper, times(1)).detailMovieToDetailMovieResponse(tmdbMovieDetailsDto);
	}

}
