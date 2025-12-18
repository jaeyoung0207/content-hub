package com.cjy.contenthub.common.integration.deepl.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.cjy.contenthub.common.util.MessageUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class DeepLApiClientTest {

	@InjectMocks
	DeepLApiClient deepLApiClient;

	@Mock
	MessageUtil messageUtil;

	@Mock
	WebClient deeplWebClient;

	static final String TRANSLATE_PATH = "/v2/translate";

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(deepLApiClient, "translatePath", TRANSLATE_PATH);
	}

	@Test
	@DisplayName("[UT]translateText: 원본 언어를 번역할 언어로 변역 - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_translateText_existResponse() {

		String text = "드래곤볼";
		String targetLang = "KO";
		String sourceLang = "JP";

		// WebClient Mock 설정
		RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
		RequestHeadersSpec headerSpec = mock(RequestHeadersSpec.class);
		when(deeplWebClient.post()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(TRANSLATE_PATH)).thenReturn(uriSpec);
		when(uriSpec.bodyValue(anyMap())).thenReturn(headerSpec);
		when(headerSpec.retrieve()).thenReturn(responseSpec);

		ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
		ObjectNode translations = new ObjectNode(JsonNodeFactory.instance);
		translations.put("text", "ドラゴンボール");
		response.putArray("translations").add(translations);
		when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(Mono.just(response));

		// 실제 메소드 호출
		String result = deepLApiClient.translateText(text, targetLang, sourceLang).block();

		// 결과 검증
		assertThat(result).isEqualTo(response.get("translations").get(0).get("text").asText());

		verify(deeplWebClient, times(1)).post();
	}

	@Test
	@DisplayName("[UT]translateText: 원본 언어를 번역할 언어로 변역 - 응답 데이터 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_translateText_notExistResponse() {

		String text = "드래곤볼";
		String targetLang = "KO";
		String sourceLang = "JP";

		// WebClient Mock 설정
		RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
		RequestHeadersSpec headerSpec = mock(RequestHeadersSpec.class);
		when(deeplWebClient.post()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(TRANSLATE_PATH)).thenReturn(uriSpec);
		when(uriSpec.bodyValue(anyMap())).thenReturn(headerSpec);
		when(headerSpec.retrieve()).thenReturn(responseSpec);

		String errorMessage = "번역 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.";
		RuntimeException causeException = new WebClientResponseException(HttpStatus.BAD_REQUEST.value(),
				errorMessage, null, null, null);
		when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(Mono.error(causeException));
		when(messageUtil.getMessageKO(anyString(), any(Object[].class)))
		.thenReturn("DeepL API 호출 에러 (text={0}, targetLang={1}, sourceLang={2}, error={3})");
        when(messageUtil.getMessageKO(anyString())).thenReturn(errorMessage);
        
        Mono<String> resultMono = deepLApiClient.translateText(text, targetLang, sourceLang);

		// 실제 메소드 호출 및 예외 검증
		assertThatThrownBy( 
			resultMono::block
		).isInstanceOf(ResponseStatusException.class)
		.satisfies(ex -> {
            ResponseStatusException statusEx = (ResponseStatusException) ex;
            // HTTP Status 검증
            assertThat(statusEx.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            // 메시지 검증
            // getReason()은 ResponseStatusException 생성자의 두 번째 인자 메시지를 반환
            assertThat(statusEx.getReason()).isEqualTo(errorMessage); 
            // 원인 예외 검증
            assertThat(statusEx.getCause()).isEqualTo(causeException);
        });

		verify(deeplWebClient, times(1)).post();
		verify(messageUtil, times(1)).getMessageKO(anyString(), any(Object[].class));
		verify(messageUtil, times(1)).getMessageKO(anyString());
	}


}
