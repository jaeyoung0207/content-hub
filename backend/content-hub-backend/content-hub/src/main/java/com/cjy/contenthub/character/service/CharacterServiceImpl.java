package com.cjy.contenthub.character.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.common.integration.anilist.constants.AniListParamConstants;
import com.cjy.contenthub.common.integration.anilist.dto.AniListCharactersNodeDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListResponseDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListStaffNodeDto;
import com.cjy.contenthub.common.util.GraphqlUtil;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.core.constants.CacheNames;
import com.cjy.contenthub.core.constants.DomainEnum.DomainMessagesWarnEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 캐릭터 정보 API 컨트롤러 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CharacterServiceImpl implements CharacterService {
	
	/** 메시지 유틸리티 */
	private final MessageUtil messageUtil;

	/** AniList API 통신용 WebClient 클래스 */
	@Qualifier("anilistWebClient")
	private final WebClient anilistWebClient;

	/** 비동기 처리용 Executor */
	private final Executor apiTaskExecutor;

	/**
	 * 캐릭터 조회
	 *
	 * @param characterId 캐릭터 ID
	 * @return 캐릭터 정보
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@Override
	@Cacheable(value = CacheNames.CHARACTER, unless = "#result == null")
	public CompletableFuture<AniListCharactersNodeDto> getCharacter(Integer characterId) throws IOException {

		// GraphQL 쿼리 파일 불러오기
		String query = GraphqlUtil.loadQuery("comicsCharacter.graphql");
		// 리퀘스트 파라미터 작성
		Map<String, Object> variables = Map.of(
				AniListParamConstants.PARAM_CHARACTER_ID, characterId
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
					if (response == null || response.getData() == null
							|| response.getData().getCharacter() == null) {
						Object[] messageParams = { characterId };
						log.warn(messageUtil.getMessageKO(
								DomainMessagesWarnEnum.WARN_CHARACTER_CHARACTER_NOT_FOUND.getMessageCode(), messageParams));
						return new AniListCharactersNodeDto();
					}
					// 응답 데이터가 있는 경우 캐릭터 정보 반환
					return response.getData().getCharacter();
				})
				// 결과를 CompletableFuture로 변환하여 반환 및 스레드 위임
				.toFuture().thenApplyAsync(character -> character, apiTaskExecutor);
	}


	/**
	 * 스태프 조회
	 *
	 * @param staffId 스태프 ID
	 * @return 스태프 정보
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@Override
	@Cacheable(value = CacheNames.STAFF, unless = "#result == null")
	public CompletableFuture<AniListStaffNodeDto> getStaff(Integer staffId) throws IOException {

		// GraphQL 쿼리 파일 불러오기
		String query = GraphqlUtil.loadQuery("comicsStaff.graphql");
		// 리퀘스트 파라미터 작성
		Map<String, Object> variables = Map.of(
				AniListParamConstants.PARAM_STAFF_ID, staffId
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
					if (response == null || response.getData() == null
							|| response.getData().getStaff() == null) {
						Object[] messageParams = { staffId };
						log.warn(messageUtil.getMessageKO(
								DomainMessagesWarnEnum.WARN_CHARACTER_STAFF_NOT_FOUND.getMessageCode(), messageParams));
						return new AniListStaffNodeDto();
					}
					// 응답 데이터가 있는 경우 스태프 정보 반환
					return response.getData().getStaff();
				})
				// 결과를 CompletableFuture로 변환하여 반환 및 스레드 위임
				.toFuture().thenApplyAsync(staff -> staff, apiTaskExecutor);
	}
}
