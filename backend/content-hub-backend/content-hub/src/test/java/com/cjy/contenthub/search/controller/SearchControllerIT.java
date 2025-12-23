package com.cjy.contenthub.search.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.test.web.servlet.MvcResult;

import com.cjy.contenthub.AbstractBaseIT;
import com.cjy.contenthub.search.controller.dto.SearchComicsResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;

@Sql(
		scripts = { "/sql/init/init.sql" }, 
		config = @SqlConfig(encoding = "utf-8", transactionMode = TransactionMode.INFERRED)
		)
class SearchControllerIT extends AbstractBaseIT {

	@Test
	@DisplayName("[IT]searchKeyword: 검색어 리스트 조회 API")
	void test_searchKeyword() throws Exception {

		// 파라미터 설정
		String keyword = "드래곤볼";
		
        // 테스트 대상 호출
		String url = "/api/search/searchKeyword";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("keyword", keyword)
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 리스트로 변환
		List<String> response = objectMapper.readValue(responseBody, new TypeReference<List<String>>() {});
		
		// 검증
		assertThat(response).isNotNull();
		boolean isSimilarKeywords = response.stream().anyMatch(k -> k.contains("드래곤볼 Z"));
		assertThat(isSimilarKeywords).isTrue();
	}
	
	@Test
	@DisplayName("[IT]searchVideo: 검색어 리스트 조회 API - userId 없음")
	void test_searchVideo_notExistUserId() throws Exception {

		// 파라미터 설정
		String keyword = "드래곤볼 Z";
		Integer seriesId = 12971;
		
        // 테스트 대상 호출
		String url = "/api/search/searchVideo";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("keyword", keyword)
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		SearchVideoResponseDto response = objectMapper.readValue(responseBody, SearchVideoResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		boolean isAniResults = response.getAniResults().stream()
                .anyMatch(content -> content.getId() == seriesId);
		assertThat(isAniResults).isTrue();
	}
	
	@Test
	@DisplayName("[IT]searchVideo: 검색어 리스트 조회 API - userId 존재")
	void test_searchVideo_existUserId() throws Exception {

		// 파라미터 설정
		String keyword = "드래곤볼 Z";
		Integer seriesId = 12971;
		Long userId = 1L; // 존재하는 유저 ID
		
        // 테스트 대상 호출
		String url = "/api/search/searchVideo";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("keyword", keyword)
				.param("user_id", String.valueOf(userId))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		SearchVideoResponseDto response = objectMapper.readValue(responseBody, SearchVideoResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		boolean isAniResults = response.getAniResults().stream()
                .anyMatch(ani -> ani.getId() == seriesId);
		assertThat(isAniResults).isTrue();
		boolean isWishlisted = response.getAniResults().stream()
				.anyMatch(content -> content.getId() == seriesId && content.isWishlisted());
		assertThat(isWishlisted).isTrue();
	}
	
	@Test
	@DisplayName("[IT]searchAni: 애니 정보 검색 API - userId 없음")
	void test_searchAni_notExistUserId() throws Exception {

		// 파라미터 설정
		String keyword = "드래곤볼 Z";
		Integer page = 1;
		Integer seriesId = 12971;
		
        // 테스트 대상 호출
		String url = "/api/search/searchAni";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("keyword", keyword)
				.param("page", String.valueOf(page))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		SearchTvResponseDto response = objectMapper.readValue(responseBody, SearchTvResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		boolean isAniResults = response.getAniResults().stream()
                .anyMatch(content -> content.getId() == seriesId);
		assertThat(isAniResults).isTrue();
	}
	
	@Test
	@DisplayName("[IT]searchAni: 애니 정보 검색 API - userId 존재")
	void test_searchAni_existUserId() throws Exception {

		// 파라미터 설정
		String keyword = "드래곤볼 Z";
		Integer seriesId = 12971;
		Integer page = 1;
		Long userId = 1L; // 존재하는 유저 ID
		
        // 테스트 대상 호출
		String url = "/api/search/searchAni";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("keyword", keyword)
				.param("page", String.valueOf(page))
				.param("user_id", String.valueOf(userId))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		SearchTvResponseDto response = objectMapper.readValue(responseBody, SearchTvResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		boolean isAniResults = response.getAniResults().stream()
                .anyMatch(content -> content.getId() == seriesId);
		assertThat(isAniResults).isTrue();
		boolean isWishlisted = response.getAniResults().stream()
				.anyMatch(content -> content.getId() == seriesId && content.isWishlisted());
		assertThat(isWishlisted).isTrue();
	}
	
	@Test
	@DisplayName("[IT]searchTvExceptAni: 애니 제외한 TV 시리즈 검색 API - userId 없음")
	void test_searchTvExceptAni_notExistUserId() throws Exception {

		// 파라미터 설정
		String keyword = "왕좌의 게임";
		String contentMediaType = "1102";
		Integer page = 1;
		Integer seriesId = 1399;
		
        // 테스트 대상 호출
		String url = "/api/search/searchTvExceptAni";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("keyword", keyword)
				.param("content_media_type", contentMediaType)
				.param("page", String.valueOf(page))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		SearchTvResponseDto response = objectMapper.readValue(responseBody, SearchTvResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		boolean isAniResults = response.getDramaResults().stream()
                .anyMatch(content -> content.getId() == seriesId);
		assertThat(isAniResults).isTrue();
	}
	
	@Test
	@DisplayName("[IT]searchTvExceptAni: 애니 제외한 TV 시리즈 검색 API - userId 존재")
	void test_searchTvExceptAni_existUserId() throws Exception {

		// 파라미터 설정
		String keyword = "왕좌의 게임";
		String contentMediaType = "1102";
		Integer page = 1;
		Integer seriesId = 1399;
		Long userId = 1L; // 존재하는 유저 ID
		
        // 테스트 대상 호출
		String url = "/api/search/searchTvExceptAni";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("keyword", keyword)
				.param("content_media_type", contentMediaType)
				.param("page", String.valueOf(page))
				.param("user_id", String.valueOf(userId))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		SearchTvResponseDto response = objectMapper.readValue(responseBody, SearchTvResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		boolean isDramaResults = response.getDramaResults().stream()
                .anyMatch(content -> content.getId() == seriesId);
		assertThat(isDramaResults).isTrue();
		boolean isWishlisted = response.getDramaResults().stream()
				.anyMatch(content -> content.getId() == seriesId && content.isWishlisted());
		assertThat(isWishlisted).isTrue();
	}
	
	@Test
	@DisplayName("[IT]searchMovie: 영화 정보 검색 API - userId 없음")
	void test_searchMovie_notExistUserId() throws Exception {

		// 파라미터 설정
		String keyword = "어벤져스";
		Integer page = 1;
		Integer movieId = 24428;
		
        // 테스트 대상 호출
		String url = "/api/search/searchMovie";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("keyword", keyword)
				.param("page", String.valueOf(page))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		SearchMovieResponseDto response = objectMapper.readValue(responseBody, SearchMovieResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		boolean isMovieResults = response.getMovieResults().stream()
                .anyMatch(content -> content.getId() == movieId);
		assertThat(isMovieResults).isTrue();
	}
	
	@Test
	@DisplayName("[IT]searchMovie: 영화 정보 검색 API - userId 존재")
	void test_searchMovie_existUserId() throws Exception {

		// 파라미터 설정
		String keyword = "어벤져스";
		Integer page = 1;
		Integer movieId = 24428;
		Long userId = 1L; // 존재하는 유저 ID
		
        // 테스트 대상 호출
		String url = "/api/search/searchMovie";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("keyword", keyword)
				.param("page", String.valueOf(page))
				.param("user_id", String.valueOf(userId))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		SearchMovieResponseDto response = objectMapper.readValue(responseBody, SearchMovieResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		boolean isMovieResults = response.getMovieResults().stream()
                .anyMatch(content -> content.getId() == movieId);
		assertThat(isMovieResults).isTrue();
		boolean isWishlisted = response.getMovieResults().stream()
				.anyMatch(content -> content.getId() == movieId && content.isWishlisted());
		assertThat(isWishlisted).isTrue();
	}
	
	@Test
	@DisplayName("[IT]searchComics: 만화 정보 검색 API - userId 없음")
	void test_searchComics_notExistUserId() throws Exception {

		// 파라미터 설정
		String keyword = "귀멸의 칼날";
		boolean isMainPage = true;
		Integer comicsId = 87216;
		
        // 테스트 대상 호출
		String url = "/api/search/searchComics";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("keyword", keyword)
				.param("is_main_page", String.valueOf(isMainPage))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		SearchComicsResponseDto response = objectMapper.readValue(responseBody, SearchComicsResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		boolean isComicsResults = response.getComicsResults().stream()
                .anyMatch(content -> content.getId() == comicsId);
		assertThat(isComicsResults).isTrue();
	}
	
	@Test
	@DisplayName("[IT]searchComics: 만화 정보 검색 API - userId 존재")
	void test_searchComics_existUserId() throws Exception {

		// 파라미터 설정
		String keyword = "귀멸의 칼날";
		boolean isMainPage = false;
		Integer page = 1;
		Long userId = 1L; // 존재하는 유저 ID
		Integer comicsId = 87216;
		
        // 테스트 대상 호출
		String url = "/api/search/searchComics";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("keyword", keyword)
				.param("page", String.valueOf(page))
				.param("is_main_page", String.valueOf(isMainPage))
				.param("user_id", String.valueOf(userId))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		SearchComicsResponseDto response = objectMapper.readValue(responseBody, SearchComicsResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		boolean isComicsResults = response.getComicsResults().stream()
                .anyMatch(content -> content.getId() == comicsId);
		assertThat(isComicsResults).isTrue();
		boolean isWishlisted = response.getComicsResults().stream()
				.anyMatch(content -> content.getId() == comicsId && content.isWishlisted());
		assertThat(isWishlisted).isTrue();
	}
	
}
