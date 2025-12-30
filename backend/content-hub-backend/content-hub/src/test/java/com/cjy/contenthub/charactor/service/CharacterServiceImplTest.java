package com.cjy.contenthub.charactor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.cjy.contenthub.character.service.CharacterServiceImpl;
import com.cjy.contenthub.common.integration.anilist.dto.AniListCharactersNodeDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListDataDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListResponseDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListStaffNodeDto;
import com.cjy.contenthub.common.util.GraphqlUtil;
import com.cjy.contenthub.common.util.MessageUtil;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class CharacterServiceImplTest {
	
	CharacterServiceImpl service;
	
	@Mock
	MessageUtil messageUtil;

	@Mock
	WebClient anilistWebClient;
	
	@BeforeEach
	void setUp() {
		service = new CharacterServiceImpl(
				messageUtil,
				anilistWebClient
				);
	}
	
	@Test
	@DisplayName("[UT]getCharacter: 캐릭터 조회 - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getCharacter_existResponse() throws IOException {
		
		try (MockedStatic<GraphqlUtil> mocked = Mockito.mockStatic(GraphqlUtil.class)) {
			
			Integer characterId = 1;
			
			String query = "graphql query string";
			String requestBody = "{ requestBody }";
			mocked.when(() -> GraphqlUtil.loadQuery("comicsCharacter.graphql")).thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(eq(query), anyMap())).thenReturn(requestBody);
			
			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(requestBody)).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);
			
			AniListResponseDto response = new AniListResponseDto();
			AniListDataDto data = new AniListDataDto();
			AniListCharactersNodeDto character = new AniListCharactersNodeDto();
			data.setCharacter(character);
			response.setData(data);
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(response));
			
			// 실제 서비스 호출
			AniListCharactersNodeDto result = service.getCharacter(characterId);
			
			// 검증
			assertThat(result).usingRecursiveComparison().isEqualTo(character);
			
			verify(anilistWebClient, times(1)).post();
			
		}
	}
	
	@Test
	@DisplayName("[UT]getCharacter: 캐릭터 조회 - 응답 데이터 없음(Data)")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getCharacter_notExistData() throws IOException {
		
		try (MockedStatic<GraphqlUtil> mocked = Mockito.mockStatic(GraphqlUtil.class)) {
			
			Integer characterId = 1;
			
			String query = "graphql query string";
			String requestBody = "{ requestBody }";
			mocked.when(() -> GraphqlUtil.loadQuery("comicsCharacter.graphql")).thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(eq(query), anyMap())).thenReturn(requestBody);
			
			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(requestBody)).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);
			
			AniListResponseDto response = new AniListResponseDto();
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(response));
			
			// 실제 서비스 호출
			AniListCharactersNodeDto result = service.getCharacter(characterId);
			
			// 검증
			assertThat(result).usingRecursiveComparison().isEqualTo(new AniListCharactersNodeDto());
			
			verify(anilistWebClient, times(1)).post();
			
		}
	}
	
	@Test
	@DisplayName("[UT]getCharacter: 캐릭터 조회 - 응답 데이터 없음(Character)")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getCharacter_notExistCharacter() throws IOException {
		
		try (MockedStatic<GraphqlUtil> mocked = Mockito.mockStatic(GraphqlUtil.class)) {
			
			Integer characterId = 1;
			
			String query = "graphql query string";
			String requestBody = "{ requestBody }";
			mocked.when(() -> GraphqlUtil.loadQuery("comicsCharacter.graphql")).thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(eq(query), anyMap())).thenReturn(requestBody);
			
			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(requestBody)).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);
			
			AniListResponseDto response = new AniListResponseDto();
			AniListDataDto data = new AniListDataDto();
			response.setData(data);
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(response));
			String errorMessage = "캐릭터 정보를 찾을 수 없습니다. (characterId:" + characterId + ")";
			when(messageUtil.getMessageKO(anyString(), eq(new Object[] { characterId })))
			.thenReturn(errorMessage);
			
			// 실제 서비스 호출
			AniListCharactersNodeDto result = service.getCharacter(characterId);
			
			// 검증
			assertThat(result).usingRecursiveComparison().isEqualTo(new AniListCharactersNodeDto());
			
			verify(anilistWebClient, times(1)).post();
			verify(messageUtil, times(1)).getMessageKO(anyString(), eq(new Object[] { characterId }));
			
		}
	}
	
	@Test
	@DisplayName("[UT]getStaff: 스태프 조회 - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getStaff_existResponse() throws IOException {
		
		try (MockedStatic<GraphqlUtil> mocked = Mockito.mockStatic(GraphqlUtil.class)) {
			
			Integer characterId = 1;
			
			String query = "graphql query string";
			String requestBody = "{ requestBody }";
			mocked.when(() -> GraphqlUtil.loadQuery("comicsStaff.graphql")).thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(eq(query), anyMap())).thenReturn(requestBody);
			
			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(requestBody)).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);
			
			AniListResponseDto response = new AniListResponseDto();
			AniListDataDto data = new AniListDataDto();
			AniListStaffNodeDto staff = new AniListStaffNodeDto();
			data.setStaff(staff);
			response.setData(data);
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(response));
			
			// 실제 서비스 호출
			AniListStaffNodeDto result = service.getStaff(characterId);
			
			// 검증
			assertThat(result).usingRecursiveComparison().isEqualTo(staff);
			
			verify(anilistWebClient, times(1)).post();
			
		}
	}
	
	@Test
	@DisplayName("[UT]getStaff: 스태프 조회 - 응답 데이터 없음(Data)")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getStaff_notExistData() throws IOException {
		
		try (MockedStatic<GraphqlUtil> mocked = Mockito.mockStatic(GraphqlUtil.class)) {
			
			Integer staffId = 1;
			
			String query = "graphql query string";
			String requestBody = "{ requestBody }";
			mocked.when(() -> GraphqlUtil.loadQuery("comicsStaff.graphql")).thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(eq(query), anyMap())).thenReturn(requestBody);
			
			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(requestBody)).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);
			
			AniListResponseDto response = new AniListResponseDto();
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(response));
			
			// 실제 서비스 호출
			AniListStaffNodeDto result = service.getStaff(staffId);
			
			// 검증
			assertThat(result).usingRecursiveComparison().isEqualTo(new AniListStaffNodeDto());
			
			verify(anilistWebClient, times(1)).post();
			
		}
	}
	
	@Test
	@DisplayName("[UT]getCharacter: 캐릭터 조회 - 응답 데이터 없음(staff)")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getStaff_notExistCharacter() throws IOException {
		
		try (MockedStatic<GraphqlUtil> mocked = Mockito.mockStatic(GraphqlUtil.class)) {
			
			Integer staffId = 1;
			
			String query = "graphql query string";
			String requestBody = "{ requestBody }";
			mocked.when(() -> GraphqlUtil.loadQuery("comicsStaff.graphql")).thenReturn(query);
			mocked.when(() -> GraphqlUtil.buildRequestBody(eq(query), anyMap())).thenReturn(requestBody);
			
			// WebClient Mock 설정
			RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
			RequestHeadersUriSpec headersSpec = mock(RequestHeadersUriSpec.class);
			ResponseSpec responseSpec = mock(ResponseSpec.class);
			when(anilistWebClient.post()).thenReturn(uriSpec);
			when(uriSpec.bodyValue(requestBody)).thenReturn(headersSpec);
			when(headersSpec.retrieve()).thenReturn(responseSpec);
			
			AniListResponseDto response = new AniListResponseDto();
			AniListDataDto data = new AniListDataDto();
			response.setData(data);
			when(responseSpec.bodyToMono(AniListResponseDto.class)).thenReturn(Mono.just(response));
			String errorMessage = "캐릭터 정보를 찾을 수 없습니다. (characterId:" + staffId + ")";
			when(messageUtil.getMessageKO(anyString(), eq(new Object[] { staffId })))
			.thenReturn(errorMessage);
			
			// 실제 서비스 호출
			AniListStaffNodeDto result = service.getStaff(staffId);
			
			// 검증
			assertThat(result).usingRecursiveComparison().isEqualTo(new AniListStaffNodeDto());
			
			verify(anilistWebClient, times(1)).post();
			verify(messageUtil, times(1)).getMessageKO(anyString(), eq(new Object[] { staffId }));
		}
	}
	
	

}
