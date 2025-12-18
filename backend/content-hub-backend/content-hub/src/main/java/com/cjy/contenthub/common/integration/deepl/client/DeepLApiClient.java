package com.cjy.contenthub.common.integration.deepl.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.cjy.contenthub.common.constants.CommonEnum.CommonMessagesErrorEnum;
import com.cjy.contenthub.common.util.MessageUtil;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * DeepL API 클라이언트 클래스
 * 
 * DeepL API를 사용하여 문자열을 번역하는 기능을 제공하는 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeepLApiClient {

	/** 메시지 유틸리티 */
	private final MessageUtil messageUtil;

	/** DeepL WebClient */
	@Qualifier("deeplWebClient")
	private final WebClient deeplWebClient;

	/** DeepL 번역 API 경로 */
	@Value("${deepl.url.translate-path}")
	private String translatePath;

	/**
	 * 원본 언어를 번역할 언어로 변역
	 * 캐시를 사용하여 동일한 요청에 대해 반복적인 API 호출을 방지
	 * @Cacheable 어노테이션을 사용하여 캐시를 적용
	 * 
	 * @param text 번역할 문자열
	 * @param targetLang 번역할 언어 (예: "KO", "JA")
	 * @param sourceLang 원본 언어 (예: "KO", "JA")
	 * @return 번역된 문자열
	 */
	public Mono<String> translateText(String text, String targetLang, String sourceLang) {
		// 파라미터 맵 생성
		MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
		paramsMap.add("text", text);
		paramsMap.add("target_lang", targetLang);
		paramsMap.add("source_lang", sourceLang);

		// DeepL API를 호출하여 번역 요청
		return deeplWebClient.post()
				.uri(translatePath)
				.bodyValue(paramsMap)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.map(json -> json.get("translations").get(0).get("text").asText())
				.onErrorResume(ex -> {
					Object[] logParams = { text, targetLang, sourceLang, ex.getMessage() };
					log.error(messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_DEEPL_DETAIL.getMessageCode(), logParams), ex);
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
							messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_DEEPL.getMessageCode()), ex);
				});
	}
}
