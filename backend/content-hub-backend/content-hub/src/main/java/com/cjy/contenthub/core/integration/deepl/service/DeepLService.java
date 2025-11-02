package com.cjy.contenthub.core.integration.deepl.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cjy.contenthub.common.integration.deepl.client.DeepLApiClient;
import com.cjy.contenthub.core.constants.CacheNames;

import lombok.RequiredArgsConstructor;

/**
 * DeepL API 번역 서비스 클래스
 * DeepL API를 사용하여 텍스트 번역 기능을 제공하는 서비스
 */
@Service
@RequiredArgsConstructor
public class DeepLService {
	
	/** DeepL API 번역 WebClient 클래스 */
	private final DeepLApiClient deeplApiClient;

	/**
	 * DeepL API를 사용하여 텍스트 번역
	 * 
	 * @param keyword    번역할 텍스트
	 * @param targetLang 대상 언어 코드
	 * @param sourceLang 원본 언어 코드
	 * @return 번역된 텍스트
	 */
	@Cacheable(value = CacheNames.TRANSLATE, key = "#keyword + '-' + #targetLang + '-' + #sourceLang", unless = "#result == null || #result.isEmpty()")
	public String translateText(String keyword, String targetLang, String sourceLang) {
		return deeplApiClient.translateText(keyword, targetLang, sourceLang);
	}

}
