package com.cjy.contenthub.character.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.character.service.CharacterService;
import com.cjy.contenthub.common.api.dto.aniist.AniListCharactersNodesDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 캐릭터 정보 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/character")
@RequiredArgsConstructor
@Slf4j
public class CharacterController {

	/** AniList API 통신용 WebClient 클래스 */
	@Qualifier("anilistWebClient")
	private final WebClient anilistWebClient;

	/** DeepL API 통신용 WebClient 클래스 */	
	@Qualifier("deeplWebClient")
	private final WebClient deeplWebClient;
	
	/** 캐릭터 서비스 */
	private final CharacterService characterService;

	/** 리퀘스트 파라미터 키 : 캐릭터ID */
	private static final String PARAM_CHARACTER_ID = "characterId";


	/**
	 * 캐릭터 조회
	 *
	 * @param characterId 캐릭터 ID
	 * @return ResponseEntity<AniListCharactersNodesDto> 캐릭터 정보
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@GetMapping(value = "/getCharacter")
	public ResponseEntity<AniListCharactersNodesDto> getCharacter(
			@RequestParam(PARAM_CHARACTER_ID) Integer characterId) throws IOException {
		return ResponseEntity.ok(characterService.getCharacter(characterId));
	}
}
