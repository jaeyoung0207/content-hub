package com.cjy.contenthub.core.integration.deepl.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cjy.contenthub.common.integration.deepl.client.DeepLApiClient;

@ExtendWith(MockitoExtension.class)
class DeepLServiceTest {
	
	DeepLService deepLService;
	
	@Mock
	DeepLApiClient deeplApiClient;
	
	@BeforeEach
	void setUp() {
		deepLService = new DeepLService(deeplApiClient);
	}
	
	@Test
	@DisplayName("[UT]translateText: DeepL API를 사용하여 텍스트 번역")
	void test_translateText() {
		
		String text = "드래곤볼";
		String targetLang = "KO";
		String sourceLang = "JP";
		
		String translatedText = "ドラゴンボール";
		when(deeplApiClient.translateText(text, targetLang, sourceLang))
		.thenReturn(translatedText);
		
		// 실제 메서드 호출
		String result = deepLService.translateText(text, targetLang, sourceLang);
		
		// 검증
		assertThat(result).isEqualTo(translatedText);
		
		verify(deeplApiClient, times(1)).translateText(text, targetLang, sourceLang);

	}

}
