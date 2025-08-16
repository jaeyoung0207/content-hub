package com.cjy.contenthub.person.service;

import com.cjy.contenthub.person.controller.dto.PersonResponseDto;

/**
 * 인물 정보 서비스 인터페이스
 */
public interface PersonService {
	
	/**
	 * 인물 상세 정보 조회
	 *
	 * @param personId 인물 ID
	 * @return PersonResponseDto 인물 상세 정보 DTO
	 */
	PersonResponseDto getPersonDetails(int personId);

}
