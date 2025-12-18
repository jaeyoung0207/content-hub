package com.cjy.contenthub.charactor.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import com.cjy.contenthub.AbstractBaseIT;
import com.cjy.contenthub.common.integration.anilist.dto.AniListCharactersNodeDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListStaffNodeDto;

class CharacterControllerIT extends AbstractBaseIT {
	
	@Test
	@DisplayName("[IT]getCharacter: 캐릭터 조회")
	void test_getCharacter() throws Exception {
		
		// 파라미터 설정
		Integer characterId = 126071;
		
        // 테스트 대상 호출
		String url = "/api/character/getCharacter";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("character_id", String.valueOf(characterId))
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
		
		AniListCharactersNodeDto response = objectMapper.readValue(responseBody, AniListCharactersNodeDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(characterId);
	}
	
	@Test
	@DisplayName("[IT]getStaff: 스태프 조회")
	void test_getStaff() throws Exception {
		
		// 파라미터 설정
		Integer staffId = 119973;
		
        // 테스트 대상 호출
		String url = "/api/character/getStaff";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("staff_id", String.valueOf(staffId))
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
		
		AniListStaffNodeDto response = objectMapper.readValue(responseBody, AniListStaffNodeDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(staffId);
	}
	
}
