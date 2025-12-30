package com.cjy.contenthub.person.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.cjy.contenthub.common.integration.tmdb.dto.TmdbPersonMovieCreditsCastDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbPersonMovieCreditsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbPersonTvCreditsCastDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbPersonTvCreditsDto;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.core.constants.DomainEnum.TmdbGenderEnum;
import com.cjy.contenthub.core.facade.ApiFacade;
import com.cjy.contenthub.person.controller.dto.PersonCreditsCastDto;
import com.cjy.contenthub.person.controller.dto.PersonCreditsCrewDto;
import com.cjy.contenthub.person.controller.dto.PersonDto;
import com.cjy.contenthub.person.controller.dto.PersonResponseDto;
import com.cjy.contenthub.person.helper.PersonHelper;
import com.cjy.contenthub.person.mapper.PersonMapper;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class PersonServiceImplTest {
	
	@InjectMocks
	PersonServiceImpl service;
	
	@Mock
	WebClient tmdbWebClient;
	
	@Mock
	ApiFacade apiFacade;
	
	@Mock
	MessageUtil messageUtil;

	@Mock
	PersonMapper mapper;

	@Mock
	PersonHelper helper;

	static final String PERSON_DETAIL_PATH = "/3/person/%s";
	
	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(service, "personDetailPath", PERSON_DETAIL_PATH);	
	}
	
	@Test
	@DisplayName("[UT]getPersonDetails: 인물 상세 정보 조회 - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getPersonDetails_existResponse() {
		
		int personId = 12345;
		
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
		
		// 응답 데이터 설정
		TmdbPersonTvCreditsDto tvCreditsDto = new TmdbPersonTvCreditsDto();
		List<TmdbPersonTvCreditsCastDto> castList = new ArrayList<>();
		TmdbPersonTvCreditsCastDto tvCast1 = new TmdbPersonTvCreditsCastDto();
		castList.add(tvCast1);
		tvCreditsDto.setCast(castList);
		TmdbPersonMovieCreditsDto movieCreditsDto = new TmdbPersonMovieCreditsDto();
		List<TmdbPersonMovieCreditsCastDto> movieCastList = new ArrayList<>();
		TmdbPersonMovieCreditsCastDto movieCast1 = new TmdbPersonMovieCreditsCastDto();
		movieCastList.add(movieCast1);
		movieCreditsDto.setCast(movieCastList);
		
		PersonDto response = PersonDto.builder()
				.id(personId)
				.gender(TmdbGenderEnum.MALE.getGenderCode())
				.tvCredits(tvCreditsDto)
				.movieCredits(movieCreditsDto)
				.build();
		when(responseSpec.bodyToMono(PersonDto.class)).thenReturn(Mono.just(response));
		
		// TV Cast
		List<PersonCreditsCastDto> personCastList = new ArrayList<>();
		PersonCreditsCastDto tvPersonCast1 = new PersonCreditsCastDto();
		tvPersonCast1.setTitle("왕좌의 게임");
		tvPersonCast1.setReleaseYear("2015");
		PersonCreditsCastDto tvPersonCast2 = new PersonCreditsCastDto();
		tvPersonCast2.setTitle("기묘한 이야기");
		tvPersonCast2.setReleaseYear("2019");
		personCastList.add(tvPersonCast1);
		personCastList.add(tvPersonCast2);
		// Movie Cast
		PersonCreditsCastDto moviePersonCast1 = new PersonCreditsCastDto();
		moviePersonCast1.setTitle("인셉션");
		moviePersonCast1.setReleaseYear("2010");
		personCastList.add(moviePersonCast1);
		// TV Crew
		List<PersonCreditsCrewDto> personCrewList = new ArrayList<>();
		PersonCreditsCrewDto tvPersonCrew1 = new PersonCreditsCrewDto();
		tvPersonCrew1.setTitle("왕좌의 게임");
		tvPersonCrew1.setReleaseYear("2015");
		PersonCreditsCrewDto tvPersonCrew2 = new PersonCreditsCrewDto();
		tvPersonCrew2.setTitle("기묘한 이야기");
		tvPersonCrew2.setReleaseYear("2019");
		personCrewList.add(tvPersonCrew1);
		personCrewList.add(tvPersonCrew2);
		// Movie Crew
		PersonCreditsCrewDto moviePersonCrew1 = new PersonCreditsCrewDto();
		moviePersonCrew1.setTitle("인셉션");
		moviePersonCrew1.setReleaseYear("2010");
		personCrewList.add(moviePersonCrew1);
		
		PersonResponseDto personResponse = PersonResponseDto.builder()
				.id(response.getId())
				.gender(response.getGender())
				.cast(personCastList)
				.crew(personCrewList)
				.build();
		when(mapper.personToPersonResponse(response)).thenReturn(personResponse);
		doAnswer(invocation -> {
			PersonResponseDto argPersonResponse = invocation.getArgument(0);
			argPersonResponse.getCast().addAll(personCastList);
			return null;
		}).when(helper).setCreditsCast(personResponse, response.getTvCredits().getCast(), tvGenreMap);
		doAnswer(invocation -> {
            PersonResponseDto argPersonResponse = invocation.getArgument(0);
            argPersonResponse.getCrew().addAll(personCrewList);
            return null;
        }).when(helper).setCreditsCrew(personResponse, response.getTvCredits().getCrew(), tvGenreMap);
		doAnswer(invocation -> {
            PersonResponseDto argPersonResponse = invocation.getArgument(0);
            argPersonResponse.getCast().addAll(personCastList);
            return null;
        }).when(helper).setCreditsCast(personResponse, response.getMovieCredits().getCast(), movieGenreMap);
		doAnswer(invocation -> {
			PersonResponseDto argPersonResponse = invocation.getArgument(0);
			argPersonResponse.getCrew().addAll(personCrewList);
			return null;
		}).when(helper).setCreditsCrew(personResponse, response.getMovieCredits().getCrew(), movieGenreMap);
		
		// 실제 메서드 호출
		PersonResponseDto result = service.getPersonDetails(personId);
		
		// 결과 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(personResponse);
		
		verify(apiFacade, times(1)).getTvGenres();
		verify(apiFacade, times(1)).getMovieGenres();
		verify(tmdbWebClient, times(1)).get();
		verify(mapper, times(1)).personToPersonResponse(response);
        verify(helper, times(1)).setCreditsCast(personResponse, response.getTvCredits().
        		getCast(), tvGenreMap);
        verify(helper, times(1)).setCreditsCrew(personResponse, response.getTvCredits().
        		getCrew(), tvGenreMap);
        verify(helper, times(1)).setCreditsCast(personResponse, response.getMovieCredits().
        		getCast(), movieGenreMap);
        verify(helper, times(1)).setCreditsCrew(personResponse, response.getMovieCredits().
        		getCrew(), movieGenreMap);
	}
	
	@Test
	@DisplayName("[UT]getPersonDetails: 인물 상세 정보 조회 - 출연작 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getPersonDetails_notExistCast() {
		
		int personId = 12345;
		
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
		
		// 응답 데이터 설정
		TmdbPersonMovieCreditsDto movieCreditsDto = new TmdbPersonMovieCreditsDto();
		PersonDto response = PersonDto.builder()
				.id(personId)
				.gender(TmdbGenderEnum.MALE.getGenderCode())
				.tvCredits(null)
				.movieCredits(movieCreditsDto)
				.build();
		when(responseSpec.bodyToMono(PersonDto.class)).thenReturn(Mono.just(response));
		
		// Movie Cast
		List<PersonCreditsCastDto> personCastList = new ArrayList<>();
		PersonCreditsCastDto moviePersonCast1 = new PersonCreditsCastDto();
		moviePersonCast1.setTitle("인셉션");
		moviePersonCast1.setReleaseYear("2010");
		personCastList.add(moviePersonCast1);
		// Movie Crew
		List<PersonCreditsCrewDto> personCrewList = new ArrayList<>();
		PersonCreditsCrewDto moviePersonCrew1 = new PersonCreditsCrewDto();
		moviePersonCrew1.setTitle("인셉션");
		moviePersonCrew1.setReleaseYear("2010");
		personCrewList.add(moviePersonCrew1);
		PersonResponseDto personResponse = PersonResponseDto.builder()
				.id(response.getId())
				.gender(response.getGender())
				.cast(new ArrayList<>())
				.crew(new ArrayList<>())
				.build();
		when(mapper.personToPersonResponse(response)).thenReturn(personResponse);
		doAnswer(invocation -> {
            PersonResponseDto argPersonResponse = invocation.getArgument(0);
            argPersonResponse.getCast().addAll(personCastList);
            return null;
        }).when(helper).setCreditsCast(personResponse, response.getMovieCredits().getCast(), movieGenreMap);
		doAnswer(invocation -> {
			PersonResponseDto argPersonResponse = invocation.getArgument(0);
			argPersonResponse.getCrew().addAll(personCrewList);
			return null;
		}).when(helper).setCreditsCrew(personResponse, response.getMovieCredits().getCrew(), movieGenreMap);
		
		// 실제 메서드 호출
		PersonResponseDto result = service.getPersonDetails(personId);
		
		// 결과 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(personResponse);
		
		verify(apiFacade, times(1)).getTvGenres();
		verify(apiFacade, times(1)).getMovieGenres();
		verify(tmdbWebClient, times(1)).get();
		verify(mapper, times(1)).personToPersonResponse(response);
        verify(helper, times(1)).setCreditsCast(personResponse, response.getMovieCredits().
        		getCast(), movieGenreMap);
        verify(helper, times(1)).setCreditsCrew(personResponse, response.getMovieCredits().
        		getCrew(), movieGenreMap);
	}
	
	@Test
	@DisplayName("[UT]getPersonDetails: 인물 상세 정보 조회 - 제작진 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getPersonDetails_notExistCrew() {
		
		int personId = 12345;
		
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
		
		// 응답 데이터 설정
		TmdbPersonTvCreditsDto tvCreditsDto = new TmdbPersonTvCreditsDto();
		PersonDto response = PersonDto.builder()
				.id(personId)
				.gender(TmdbGenderEnum.MALE.getGenderCode())
				.tvCredits(tvCreditsDto)
				.movieCredits(null)
				.build();
		when(responseSpec.bodyToMono(PersonDto.class)).thenReturn(Mono.just(response));
		
		PersonResponseDto personResponse = PersonResponseDto.builder()
				.id(response.getId())
				.gender(response.getGender())
				.cast(new ArrayList<>())
				.crew(new ArrayList<>())
				.build();
		when(mapper.personToPersonResponse(response)).thenReturn(personResponse);
		doNothing().when(helper).setCreditsCast(personResponse, response.getTvCredits().getCast(), tvGenreMap);
		doNothing().when(helper).setCreditsCrew(personResponse, response.getTvCredits().getCrew(), tvGenreMap);
		
		// 실제 메서드 호출
		PersonResponseDto result = service.getPersonDetails(personId);
		
		// 결과 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(personResponse);
		
		verify(apiFacade, times(1)).getTvGenres();
		verify(apiFacade, times(1)).getMovieGenres();
		verify(tmdbWebClient, times(1)).get();
		verify(mapper, times(1)).personToPersonResponse(response);
		verify(helper, times(1)).setCreditsCast(personResponse, response.getTvCredits().
        		getCast(), tvGenreMap);
        verify(helper, times(1)).setCreditsCrew(personResponse, response.getTvCredits().
        		getCrew(), tvGenreMap);
	}
	@Test
	@DisplayName("[UT]getPersonDetails: 인물 상세 정보 조회 - 출연작 및 제작진 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getPersonDetails_notExistCastAndCrew() {
		
		int personId = 12345;
		
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
		
		// 응답 데이터 설정
		PersonDto response = PersonDto.builder()
				.id(personId)
				.gender(TmdbGenderEnum.MALE.getGenderCode())
				.tvCredits(null)
				.movieCredits(null)
				.build();
		when(responseSpec.bodyToMono(PersonDto.class)).thenReturn(Mono.just(response));
		
		PersonResponseDto personResponse = PersonResponseDto.builder()
				.id(response.getId())
				.gender(response.getGender())
				.cast(new ArrayList<>())
				.crew(new ArrayList<>())
				.build();
		when(mapper.personToPersonResponse(response)).thenReturn(personResponse);
		when(messageUtil.getMessageKO(anyString(), eq(new Object[] { personId })))
				.thenReturn("인물 정보를 찾을 수 없습니다. (personId: " + personId + ")");
		
		// 실제 메서드 호출
		PersonResponseDto result = service.getPersonDetails(personId);
		
		// 결과 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(personResponse);
		
		verify(apiFacade, times(1)).getTvGenres();
		verify(apiFacade, times(1)).getMovieGenres();
		verify(tmdbWebClient, times(1)).get();
		verify(mapper, times(1)).personToPersonResponse(response);
		verify(messageUtil, times(1)).getMessageKO(anyString(), eq(new Object[] { personId }));
	}

}
