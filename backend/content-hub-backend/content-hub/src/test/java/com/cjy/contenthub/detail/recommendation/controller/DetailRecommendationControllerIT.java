package com.cjy.contenthub.detail.recommendation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.test.web.servlet.MvcResult;

import com.cjy.contenthub.AbstractBaseIT;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsComicsResponseDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsMovieDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsTvDto;

@Sql(
		scripts = { "/sql/init/init.sql" }, 
		config = @SqlConfig(encoding = "utf-8", transactionMode = TransactionMode.INFERRED)
		)
class DetailRecommendationControllerIT extends AbstractBaseIT {
	
	@Test
	@DisplayName("[IT]getTvRecommendations: TMDB TV 추천 작품 조회 API - userId 없음")
	void test_getTvRecommendations_notExistUserId() throws Exception {

		// 파라미터 설정
		Integer seriesId = 1399; // 왕좌의 게임
		Integer page = 1; // 드라마
		
        // 테스트 대상 호출
		String url = "/api/detail/recommendation/getTvRecommendations";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("series_id", String.valueOf(seriesId))
				.param("page", String.valueOf(page))
				)
				.andExpect(request().asyncStarted()) // 비동기 시작 검증
				.andExpect(status().isOk())
				.andReturn();
		
		// 비동기 결과를 다시 디스패치
		MvcResult asyncResult = mockMvc.perform(asyncDispatch(mvcResult))
		    .andExpect(status().isOk())
		    .andDo(print())
		    .andReturn();
		
		// 응답 본문 추출
		String responseBody = asyncResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		DetailRecommendationsTvDto response = objectMapper.readValue(responseBody, DetailRecommendationsTvDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getResults().getFirst().getContentMediaType()).isNotNull();
	}
	
	@Test
	@DisplayName("[IT]getTvRecommendations: TMDB TV 추천 작품 조회 API - userId 존재")
	void test_getTvRecommendations_existUserId() throws Exception {

		// 파라미터 설정
		Integer seriesId = 1399; // 왕좌의 게임
		Integer page = 1;
		Long userId = 1L; // 존재하는 유저 ID
		Integer wishlistedSeriesId = 44217; // 위시리스트에 추가된 TV 시리즈 ID
		
        // 테스트 대상 호출
		String url = "/api/detail/recommendation/getTvRecommendations";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("series_id", String.valueOf(seriesId))
				.param("page", String.valueOf(page))
				.param("user_id", String.valueOf(userId))
				)
				.andExpect(request().asyncStarted()) // 비동기 시작 검증
				.andExpect(status().isOk())
				.andReturn();
		
		// 비동기 결과를 다시 디스패치
		MvcResult asyncResult = mockMvc.perform(asyncDispatch(mvcResult))
		    .andExpect(status().isOk())
		    .andDo(print())
		    .andReturn();
		
		// 응답 본문 추출
		String responseBody = asyncResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		DetailRecommendationsTvDto response = objectMapper.readValue(responseBody, DetailRecommendationsTvDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getResults().getFirst().getContentMediaType()).isNotNull();
		boolean isWishlisted = response.getResults().stream()
				.anyMatch(tv -> tv.getId() == wishlistedSeriesId && tv.isWishlisted());
		assertThat(isWishlisted).isTrue();
	}
	
	@Test
	@DisplayName("[IT]getMovieRecommendations: TMDB 영화 추천 작품 조회 API - userId 없음")
	void test_getMovieRecommendations_notExistUserId() throws Exception {

		// 파라미터 설정
		Integer movieId = 24428; // 어벤져스
		Integer page = 1;
		
        // 테스트 대상 호출
		String url = "/api/detail/recommendation/getMovieRecommendations";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("movie_id", String.valueOf(movieId))
				.param("page", String.valueOf(page))
				)
				.andExpect(request().asyncStarted()) // 비동기 시작 검증
				.andExpect(status().isOk())
				.andReturn();
		
		// 비동기 결과를 다시 디스패치
		MvcResult asyncResult = mockMvc.perform(asyncDispatch(mvcResult))
		    .andExpect(status().isOk())
		    .andDo(print())
		    .andReturn();
		
		// 응답 본문 추출
		String responseBody = asyncResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		DetailRecommendationsMovieDto response = objectMapper.readValue(responseBody, DetailRecommendationsMovieDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getResults().getFirst().getContentMediaType()).isNotNull();
	}
	
	@Test
	@DisplayName("[IT]getMovieRecommendations: TMDB 영화 추천 작품 조회 API - userId 없음")
	void test_getMovieRecommendations_existUserId() throws Exception {

		// 파라미터 설정
		Integer movieId = 24428; // 어벤져스
		Integer page = 1;
		Long userId = 1L; // 존재하는 유저 ID
		Integer wishlistedSeriesId = 299536; // 위시리스트에 추가된 TV 시리즈 ID
		
        // 테스트 대상 호출
		String url = "/api/detail/recommendation/getMovieRecommendations";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("movie_id", String.valueOf(movieId))
				.param("page", String.valueOf(page))
				.param("user_id", String.valueOf(userId))
				)
				.andExpect(request().asyncStarted()) // 비동기 시작 검증
				.andExpect(status().isOk())
				.andReturn();
		
		// 비동기 결과를 다시 디스패치
		MvcResult asyncResult = mockMvc.perform(asyncDispatch(mvcResult))
		    .andExpect(status().isOk())
		    .andDo(print())
		    .andReturn();
		
		// 응답 본문 추출
		String responseBody = asyncResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		DetailRecommendationsMovieDto response = objectMapper.readValue(responseBody, DetailRecommendationsMovieDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getResults().getFirst().getContentMediaType()).isNotNull();
		boolean isWishlisted = response.getResults().stream()
				.anyMatch(tv -> tv.getId() == wishlistedSeriesId && tv.isWishlisted());
		assertThat(isWishlisted).isTrue();
	}
	
	@Test
	@DisplayName("[IT]getComicsRecommendations: AniList Comics 추천 작품 조회 API - userId 없음")
	void test_getComicsRecommendations_notExistUserId() throws Exception {

		// 파라미터 설정
		Integer mediaId = 87216; // 귀멸의 칼날
		Integer page = 1;
		
        // 테스트 대상 호출
		String url = "/api/detail/recommendation/getComicsRecommendations";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("media_id", String.valueOf(mediaId))
				.param("page", String.valueOf(page))
				)
				.andExpect(request().asyncStarted()) // 비동기 시작 검증
				.andExpect(status().isOk())
				.andReturn();
		
		// 비동기 결과를 다시 디스패치
		MvcResult asyncResult = mockMvc.perform(asyncDispatch(mvcResult))
		    .andExpect(status().isOk())
		    .andDo(print())
		    .andReturn();
		
		String responseBody = asyncResult.getResponse().getContentAsString();
		
		DetailRecommendationsComicsResponseDto response = objectMapper.readValue(responseBody, DetailRecommendationsComicsResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getResults().getFirst().getContentMediaType()).isNotNull();
	}
	
	@Test
	@DisplayName("[IT]getComicsRecommendations: AniList Comics 추천 작품 조회 API - userId 존재")
	void test_getComicsRecommendations_existUserId() throws Exception {

		// 파라미터 설정
		Integer mediaId = 87216; // 귀멸의 칼날
		Integer page = 1;
		Long userId = 1L; // 존재하는 유저 ID
		Integer wishlistedSeriesId = 30053; // 위시리스트에 추가된 TV 시리즈 ID
		
        // 테스트 대상 호출
		String url = "/api/detail/recommendation/getComicsRecommendations";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("media_id", String.valueOf(mediaId))
				.param("page", String.valueOf(page))
				.param("user_id", String.valueOf(userId))
				)
				.andExpect(request().asyncStarted()) // 비동기 시작 검증
				.andExpect(status().isOk())
				.andReturn();
		
		// 비동기 결과를 다시 디스패치
		MvcResult asyncResult = mockMvc.perform(asyncDispatch(mvcResult))
		    .andExpect(status().isOk())
		    .andDo(print())
		    .andReturn();
		
		// 응답 본문 추출
		String responseBody = asyncResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		DetailRecommendationsComicsResponseDto response = objectMapper.readValue(responseBody, DetailRecommendationsComicsResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getResults().getFirst().getContentMediaType()).isNotNull();
		boolean isWishlisted = response.getResults().stream()
				.anyMatch(tv -> tv.getId() == wishlistedSeriesId && tv.isWishlisted());
		assertThat(isWishlisted).isTrue();
	}

}
