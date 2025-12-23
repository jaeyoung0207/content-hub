package com.cjy.contenthub.home.controller;

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
import com.cjy.contenthub.home.controller.dto.HomeRankingListResponseDto;

@Sql(
		scripts = { "/sql/init/init.sql" }, 
		config = @SqlConfig(encoding = "utf-8", transactionMode = TransactionMode.INFERRED)
		)
class HomeControllerTest extends AbstractBaseIT {
	
	@Test
	@DisplayName("[IT]getContentRankings: 콘텐츠 랭킹 정보 조회")
	void test_getContentRankings() throws Exception {

		// 파라미터 설정
		Long userId = 1L;
		
        // 테스트 대상 호출
		String url = "/api/home/rankings";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("user_id", String.valueOf(userId))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 리스트로 변환
		HomeRankingListResponseDto response = objectMapper.readValue(responseBody, HomeRankingListResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getAniRankingList()).isNotNull();
        assertThat(response.getAniRankingList().getFirst()).isNotNull();
        assertThat(response.getAniRankingList().getFirst().isWishlisted()).isTrue();
        assertThat(response.getDramaRankingList()).isNotNull();
        assertThat(response.getDramaRankingList().getFirst()).isNotNull();
        assertThat(response.getDramaRankingList().getFirst().isWishlisted()).isTrue();
        assertThat(response.getMovieRankingList()).isNotNull();
        assertThat(response.getMovieRankingList().getFirst()).isNotNull();
        assertThat(response.getMovieRankingList().getFirst().isWishlisted()).isTrue();
	}

}
