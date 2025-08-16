package com.cjy.contenthub.person.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.person.controller.dto.PersonResponseDto;
import com.cjy.contenthub.person.service.PersonService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인물 정보 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/person")
@RequiredArgsConstructor
@Slf4j
public class PersonController {
	
	/** 인물 상세 서비스 */
	private final PersonService personService;
	
	/** TMDB API 통신용 WebClient 클래스 */
	@Qualifier("tmdbWebClient")
	private final WebClient tmdbWebClient;
	
	/** TMDB API Person Detail API 패스 */
	@Value("${tmdb.url.personDetailPath}")
	private String personDetailPath;
	
	/** 리퀘스트 파라미터 키 : 인물 ID */
	private static final String PARAM_PERSON_ID = "personId";
	
	/**
	 * 인물 상세 정보 조회 API
	 * 
	 * @param personId 인물 ID
	 * @return 인물 상세 정보 DTO
	 */
	@GetMapping("/details")
	public ResponseEntity<PersonResponseDto> getPersonDetails(
			@RequestParam(PARAM_PERSON_ID) int personId) {
		return ResponseEntity.ok(personService.getPersonDetails(personId));
	}

}
