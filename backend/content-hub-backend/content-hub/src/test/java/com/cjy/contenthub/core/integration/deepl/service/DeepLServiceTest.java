package com.cjy.contenthub.core.integration.deepl.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cjy.contenthub.common.integration.deepl.client.DeepLApiClient;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class DeepLServiceTest {
	
	DeepLService deepLService;
	
	@Mock
	DeepLApiClient deeplApiClient;
	
	ExecutorService executorService;
	
	@BeforeEach
	void setUp() {
		this.executorService = Executors.newSingleThreadExecutor();
		deepLService = new DeepLService(deeplApiClient, executorService);
	}
	
	@AfterEach
	void tearDown() {
		this.executorService.shutdown();
	}
	
	@Test
	@DisplayName("[UT]translateText: DeepL API를 사용하여 텍스트 번역")
	void test_translateText() throws InterruptedException, ExecutionException, TimeoutException {
		
		String text = "드래곤볼";
		String targetLang = "KO";
		String sourceLang = "JP";
		
		String translatedText = "ドラゴンボール";
		when(deeplApiClient.translateText(text, targetLang, sourceLang))
		.thenReturn(Mono.just(translatedText));
		
		// 실제 메서드 호출
		String result = deepLService.translateText(text, targetLang, sourceLang).get(5, TimeUnit.SECONDS);
		
		// 검증
		assertThat(result).isEqualTo(translatedText);
		
		verify(deeplApiClient, times(1)).translateText(text, targetLang, sourceLang);

	}

}
