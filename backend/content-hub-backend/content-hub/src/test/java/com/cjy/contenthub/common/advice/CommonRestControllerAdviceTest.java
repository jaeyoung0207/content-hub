package com.cjy.contenthub.common.advice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cjy.contenthub.common.advice.response.CommonErrorResponse;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMessagesErrorEnum;
import com.cjy.contenthub.common.exception.CommonBusinessException;
import com.cjy.contenthub.common.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class CommonRestControllerAdviceTest {
	
	@InjectMocks
	CommonRestControllerAdvice commonRestControllerAdvice;
	
	@Mock
	CommonBusinessException businessException;
	
	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;
	
	@Mock
	private MessageUtil messageUtil;
	
	private static final String BUSINESS_ERROR = "Business Error";
	
	@Test
	@DisplayName("[UT]handleCommonBusinessException: CommonBusinessException 처리 - statusCode가 null인 경우")
	void test_handleCommonBusinessException_statusCodeIsNull() {
		// Mock 설정
		String path = "/api/detail/comments/updateComment";
		String message = "댓글 정보를 찾을 수 없습니다.";
		int statusCode = HttpStatus.BAD_REQUEST.value();
		String errorName = BUSINESS_ERROR;
		Object[] messageParams = {errorName, path, statusCode, message};
		when(request.getRequestURI()).thenReturn(path);
		when(businessException.getStatusCode()).thenReturn(null);
		when(businessException.getMessage()).thenReturn(message);
		when(messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_CONTROLLER_ADVICE_1.getMessageCode(),messageParams))
				.thenReturn(MessageFormat.format("{0} : path={1}, status={2}, message={3}", errorName, path, statusCode, message));
		
		// 테스트 대상 메서드 호출
		ResponseEntity<CommonErrorResponse> resultResponse = commonRestControllerAdvice.handleBusinessException(businessException, request, response);
		
		// 기대 결과
		CommonErrorResponse expectedResponse = CommonErrorResponse.builder()
				.path(path)
				.status(statusCode)
				.message(message)
				.name(errorName)
				.build();
		// 응답 상태 코드 검증
		assertThat(resultResponse.getBody()).usingRecursiveComparison().isEqualTo(expectedResponse);
		assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.valueOf(statusCode));
		// 메시지 유틸 호출 검증
		verify(messageUtil, times(1)).getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_CONTROLLER_ADVICE_1.getMessageCode(), messageParams);
	}
	
	@Test
	@DisplayName("[UT]handleCommonBusinessException: CommonBusinessException 처리 - statusCode가 null이 아닌 경우")
	void test_handleCommonBusinessException_statusCodeIsNotNull() {
		// Mock 설정
		String path = "/api/home/rankings";
		String message = "서비스 점검 중입니다. 잠시 후 다시 시도해 주세요.";
		int statusCode = HttpStatus.SERVICE_UNAVAILABLE.value();
		String errorName = BUSINESS_ERROR;
		Object[] messageParams = {errorName, path, statusCode, message};
		when(request.getRequestURI()).thenReturn(path);
		when(businessException.getStatusCode()).thenReturn(statusCode);
		when(businessException.getMessage()).thenReturn(message);
		when(messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_CONTROLLER_ADVICE_1.getMessageCode(),messageParams))
				.thenReturn(MessageFormat.format("{0} : path={1}, status={2}, message={3}", errorName, path, statusCode, message));
		
		// 테스트 대상 메서드 호출
		ResponseEntity<CommonErrorResponse> resultResponse = commonRestControllerAdvice.handleBusinessException(businessException, request, response);
		
		// 기대 결과
		CommonErrorResponse expectedResponse = CommonErrorResponse.builder()
				.path(path)
				.status(statusCode)
				.message(message)
				.name(errorName)
				.build();
		// 응답 상태 코드 검증
		assertThat(resultResponse.getBody()).usingRecursiveComparison().isEqualTo(expectedResponse);
		assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.valueOf(statusCode));
		// 메시지 유틸 호출 검증
		verify(messageUtil, times(1)).getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_CONTROLLER_ADVICE_1.getMessageCode(), messageParams);
	}
	
}
