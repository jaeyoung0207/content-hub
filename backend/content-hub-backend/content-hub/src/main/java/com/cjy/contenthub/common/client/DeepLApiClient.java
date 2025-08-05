package com.cjy.contenthub.common.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

/**
 * DeepL API 클라이언트 클래스
 * 
 * DeepL API를 사용하여 문자열을 번역하는 기능을 제공하는 클래스
 */
@Component
@RequiredArgsConstructor
public class DeepLApiClient {

	@Qualifier("deeplWebClient")
	private final WebClient deeplWebClient;

	@Value("${deepl.url.translatePath}")
	private String translatePath;

	/**
	 * 원본 언어를 번역할 언어로 변역
	 * 캐시를 사용하여 동일한 요청에 대해 반복적인 API 호출을 방지
	 * @Cacheable 어노테이션을 사용하여 캐시를 적용
	 * @Cacheable를 사용하기 위해서는 동기화 된 메소드여야 하므로, .block() 메소드를 사용하여 결과를 반환
	 * 
	 * @param text 번역할 문자열
	 * @param targetLang 번역할 언어 (예: "KO", "JA")
	 * @param sourceLang 원본 언어 (예: "KO", "JA")
	 * @return 번역된 문자열
	 */
	@Cacheable(value = CommonConstants.API_TRANSLATE_NAME)
	public String translateText(String text, String targetLang, String sourceLang) {
		
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
				.block();
	}
}
