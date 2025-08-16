package com.cjy.contenthub.character.service;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.common.api.dto.aniist.AniListCharactersNodesDto;
import com.cjy.contenthub.common.api.dto.aniist.AniListResponseDto;
import com.cjy.contenthub.common.util.GraphqlUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 캐릭터 정보 API 컨트롤러 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CharacterServiceImpl implements CharacterService {

	/** AniList API 통신용 WebClient 클래스 */
	@Qualifier("anilistWebClient")
	private final WebClient anilistWebClient;

	/** DeepL API 통신용 WebClient 클래스 */
	@Qualifier("deeplWebClient")
	private final WebClient deeplWebClient;

	/** 리퀘스트 파라미터 키 : 캐릭터ID */
	private static final String PARAM_CHARACTER_ID = "characterId";


	/**
	 * 캐릭터 조회
	 *
	 * @param characterId 캐릭터 ID
	 * @return 캐릭터 정보
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@Override
	@Cacheable(value = "character", key = "#characterId", unless = "#result == null")
	public AniListCharactersNodesDto getCharacter(Integer characterId) throws IOException {

		// GraphQL 쿼리 파일 불러오기
		String query = GraphqlUtil.loadQuery("comicsCharacter.graphql");
		// 리퀘스트 파라미터 작성
		Map<String, Object> variables = Map.of(
				PARAM_CHARACTER_ID, characterId
				);
		// 쿼리에 리퀘스트 파라미터 적용하여 문자열 생성
		String requestBody = GraphqlUtil.buildRequestBody(query, variables);

		// AniList API 조회
		return anilistWebClient.post()
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(AniListResponseDto.class)
				.map(response -> {
					// 응답 데이터가 없는 경우 빈 ResponseEntity 반환
					if (ObjectUtils.isEmpty(response.getData())
							|| ObjectUtils.isEmpty(response.getData().getCharacter())) {
						log.warn("Character not found for ID: {}", characterId);
						return new AniListCharactersNodesDto();
					}
					// 응답 데이터가 있는 경우 캐릭터 정보 반환
					return response.getData().getCharacter();
				})
				.block();
	}
}
