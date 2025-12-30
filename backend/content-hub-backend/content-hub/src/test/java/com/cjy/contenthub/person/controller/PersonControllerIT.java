package com.cjy.contenthub.person.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import com.cjy.contenthub.AbstractBaseIT;
import com.cjy.contenthub.person.controller.dto.PersonResponseDto;

class PersonControllerIT extends AbstractBaseIT {

	@Test
	@DisplayName("[IT]getPersonDetails: 인물 상세 정보 조회 API")
	void test_getPersonDetails() throws Exception {
		
		// 파라미터 설정
		Integer personId = 1256603;
		
        // 테스트 대상 호출
		String url = "/api/person/getPersonDetails";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("person_id", String.valueOf(personId))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		PersonResponseDto response = objectMapper.readValue(responseBody, PersonResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(personId);
	}
	
}
