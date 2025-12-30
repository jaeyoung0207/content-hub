package com.cjy.contenthub.detail.information.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.test.web.servlet.MvcResult;

import com.cjy.contenthub.AbstractBaseIT;
import com.cjy.contenthub.common.integration.anilist.dto.AniListCharactersDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListStaffDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailComicsResponseDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailMovieResponseDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailTvResponseDto;

@Sql(
		scripts = { "/sql/init/init.sql" }, 
		config = @SqlConfig(encoding = "utf-8", transactionMode = TransactionMode.INFERRED)
		)
class DetailInformationControllerIT extends AbstractBaseIT {
	
	@Test
	@DisplayName("[IT]getTvDetail: TMDB TV 상세 조회 API - userId 없음")
	void test_getTvDetail_notExistUserId() throws Exception {

		// 파라미터 설정
		Integer seriesId = 1399; // 왕좌의 게임
		String contentMediaType = "1102"; // 드라마
		
        // 테스트 대상 호출
		String url = "/api/detail/information/getTvDetail";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("series_id", String.valueOf(seriesId))
				.param("content_media_type", contentMediaType)
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		DetailTvResponseDto response = objectMapper.readValue(responseBody, DetailTvResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(seriesId);
		assertThat(response.isWishlisted()).isFalse();
	}
	
	@Test
	@DisplayName("[IT]getTvDetail: TMDB TV 상세 조회 API - userId 존재")
	void test_getTvDetail_existUserId() throws Exception {

		// 파라미터 설정
		Integer seriesId = 1399; // 왕좌의 게임
		String contentMediaType = "1102"; // 드라마
		Long userId = 1L; // 존재하는 유저 ID
		
        // 테스트 대상 호출
		String url = "/api/detail/information/getTvDetail";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("series_id", String.valueOf(seriesId))
				.param("content_media_type", contentMediaType)
				.param("user_id", String.valueOf(userId))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		DetailTvResponseDto response = objectMapper.readValue(responseBody, DetailTvResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(seriesId);
		assertThat(response.isWishlisted()).isTrue();
	}
	
	@Test
	@DisplayName("[IT]getMovieDetail: TMDB 영화 상세 조회 API - userId 없음")
	void test_getMovieDetail_notExistUserId() throws Exception {

		// 파라미터 설정
		Integer movieId = 24428; // 어벤져스
		String contentMediaType = "1201"; // 영화
		
        // 테스트 대상 호출
		String url = "/api/detail/information/getMovieDetail";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("movie_id", String.valueOf(movieId))
				.param("content_media_type", contentMediaType)
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		DetailMovieResponseDto response = objectMapper.readValue(responseBody, DetailMovieResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(movieId);
		assertThat(response.isWishlisted()).isFalse();
	}
	
	@Test
	@DisplayName("[IT]getMovieDetail: TMDB 영화 상세 조회 API - userId 존재")
	void test_getMovieDetail_existUserId() throws Exception {

		// 파라미터 설정
		Integer movieId = 24428; // 어벤져스
		String contentMediaType = "1201"; // 영화
		Long userId = 1L; // 존재하는 유저 ID
		
        // 테스트 대상 호출
		String url = "/api/detail/information/getMovieDetail";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("movie_id", String.valueOf(movieId))
				.param("content_media_type", contentMediaType)
				.param("user_id", String.valueOf(userId))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		DetailMovieResponseDto response = objectMapper.readValue(responseBody, DetailMovieResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(movieId);
		assertThat(response.isWishlisted()).isTrue();
	}
	
	@Test
	@DisplayName("[IT]getComicsDetail: AniList Comics 상세 조회 API - userId 없음")
	void test_getComicsDetail_notExistUserId() throws Exception {

		// 파라미터 설정
		Integer comicsId = 87216; // 귀멸의 칼날
		String contentMediaType = "2101"; // 만화
		
        // 테스트 대상 호출
		String url = "/api/detail/information/getComicsDetail";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("comics_id", String.valueOf(comicsId))
				.param("content_media_type", contentMediaType)
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		DetailComicsResponseDto response = objectMapper.readValue(responseBody, DetailComicsResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(comicsId);
		assertThat(response.isWishlisted()).isFalse();
	}
	
	@Test
	@DisplayName("[IT]getComicsDetail: AniList Comics 상세 조회 API - userId 존재")
	void test_getComicsDetail_existUserId() throws Exception {

		// 파라미터 설정
		Integer comicsId = 87216; // 귀멸의 칼날
		String contentMediaType = "2101"; // 만화
		Long userId = 1L; // 존재하는 유저 ID
		
        // 테스트 대상 호출
		String url = "/api/detail/information/getComicsDetail";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("comics_id", String.valueOf(comicsId))
				.param("content_media_type", contentMediaType)
				.param("user_id", String.valueOf(userId))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		DetailComicsResponseDto response = objectMapper.readValue(responseBody, DetailComicsResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(comicsId);
		assertThat(response.isWishlisted()).isTrue();
	}
	
	@Test
	@DisplayName("[IT]getComicsCharacterList: AniList Comics 캐릭터 리스트 조회 API")
	void test_getComicsCharacterList() throws Exception {

		// 파라미터 설정
		Integer comicsId = 87216; // 귀멸의 칼날
		Integer page = 1; // 만화
		
        // 테스트 대상 호출
		String url = "/api/detail/information/getComicsCharacterList";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("comics_id", String.valueOf(comicsId))
				.param("page", String.valueOf(page))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		AniListCharactersDto response = objectMapper.readValue(responseBody, AniListCharactersDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getEdges()).isNotEmpty();
		assertThat(response.getPageInfo()).isNotNull();
		assertThat(response.getPageInfo().getCurrentPage()).isEqualTo(page);
	}
	
	@Test
	@DisplayName("[IT]AniListStaffDto: AniList Comics 스태프 리스트 조회 API")
	void test_AniListStaffDto() throws Exception {

		// 파라미터 설정
		Integer comicsId = 87216; // 귀멸의 칼날
		Integer page = 1; // 만화
		
        // 테스트 대상 호출
		String url = "/api/detail/information/getComicsStaffList";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("comics_id", String.valueOf(comicsId))
				.param("page", String.valueOf(page))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		AniListStaffDto response = objectMapper.readValue(responseBody, AniListStaffDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getEdges()).isNotEmpty();
		assertThat(response.getPageInfo()).isNotNull();
		assertThat(response.getPageInfo().getCurrentPage()).isEqualTo(page);
	}

}
