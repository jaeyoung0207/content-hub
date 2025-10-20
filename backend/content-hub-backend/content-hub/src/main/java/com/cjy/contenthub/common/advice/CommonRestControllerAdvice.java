package com.cjy.contenthub.common.advice;

import java.nio.file.AccessDeniedException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.cjy.contenthub.common.advice.response.CommonErrorResponse;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMessagesErrorEnum;
import com.cjy.contenthub.common.exception.CommonBusinessException;
import com.cjy.contenthub.common.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Rest API 통신중 발생한 에러를 잡아서 처리하는 클래스
 * 각 메소드에서 예외를 잡아 적절한 에러 메시지와 상태 코드를 JSON 형태의 오브젝트로 반환
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class CommonRestControllerAdvice {
	
	/** 메시지 유틸 */
	private final MessageUtil messageUtil;

	/** 인증 에러 */
	private static final String AUTHENTICATION_AUTHORIZATION_ERROR = "Authentication/Authorization Error";

	/** 입력값 검사 에러 */
	private static final String VALIDATION_ERROR = "Validation Error";

	/** API 요청 에러 */
	private static final String API_REQUEST_ERROR = "API Request Error";
	
	/** API 응답 에러 */
	private static final String API_RESPONSE_ERROR = "API Response Error";

	/** 업무 에러 */
	private static final String BUSINESS_ERROR = "Business Error";

	/** 시스템 에러 */
	private static final String SERVER_ERROR = "Server Error";
	
	/** 타임아웃 에러 */
	private static final String TIMEOUT_ERROR = "Timeout Error";

	/**
	 * 인증/인가 관련 예외 처리
	 * 
	 * @param ex AuthenticationException, AccessDeniedException
	 * @param request HttpServletRequest
	 * @return 공통 에러 응답 오브젝트
	 */
	@ExceptionHandler({
		AuthenticationException.class, // 인증 실패
		AccessDeniedException.class // 인가 실패
	})
	public ResponseEntity<CommonErrorResponse> handleAuthException(Exception ex, HttpServletRequest request) {
		String path = request.getRequestURI();
		int statusCode = ex instanceof AccessDeniedException
				? HttpStatus.FORBIDDEN.value() : HttpStatus.UNAUTHORIZED.value();
		String message = ex.getMessage();
		CommonErrorResponse errorResponse = CommonErrorResponse.builder()
				.path(path)
				.status(statusCode)
				.message(message)
				.name(AUTHENTICATION_AUTHORIZATION_ERROR)
				.build();
		Object[] messageParams = {AUTHENTICATION_AUTHORIZATION_ERROR, path, statusCode, 
				ObjectUtils.isNotEmpty(ex.getCause()) ? ex.getCause().getMessage() : message};
		log.error(messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_CONTROLLER_ADVICE_1.getMessageCode(), messageParams), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(statusCode));
	}

	/**
	 * 입력값 검사 실패 시 예외 처리
	 * 
	 * @param ex BindException, MissingServletRequestParameterException, MethodArgumentTypeMismatchException, 
	 *           HttpMessageNotReadableException, ConstraintViolationException
	 * @param request HttpServletRequest
	 * @return 공통 에러 응답 오브젝트
	 */
	@ExceptionHandler({
		HttpMessageNotReadableException.class, // JSON 파싱 에러
		MissingServletRequestParameterException.class, // 필수 파라미터 누락
		MethodArgumentTypeMismatchException.class, // 타입 불일치
		BindException.class, // 바인딩 에러(유효성 검사 에러(MethodArgumentNotValidException)포함)
		ConstraintViolationException.class // JSR-303/JSR-380 유효성 검사 실패
	})
	public ResponseEntity<CommonErrorResponse> handleValidationException(Exception ex, HttpServletRequest request) {
		String path = request.getRequestURI();
		int statusCode = HttpStatus.BAD_REQUEST.value();
		String message = "";

		// 예외 타입에 따라 메시지를 다르게 설정
		switch (ex) {
			// JSON 파싱 에러 처리
			case HttpMessageNotReadableException jsonParseEx -> message = "JSON parsing error: " + jsonParseEx.getMessage();
	
			// 필수 파라미터가 누락된 경우
			case MissingServletRequestParameterException missingReqParamEx -> message = "Missing parameter: " + missingReqParamEx.getParameterName();
	
			// 타입 불일치 예외 처리
			case MethodArgumentTypeMismatchException typeMismatchEx -> 
				message = "Type mismatch for parameter: " + typeMismatchEx.getName() + ", expected: "
						+ typeMismatchEx.getRequiredType().getSimpleName();
	
			// 바인딩 에러 처리
			// DTO 필드에 대한 유효성 검사(@Valid, @NotEmpty 등)에서 실패할 때 발생
			case BindException bindEx -> message = bindEx.getBindingResult().getFieldErrors().stream()
					.map(error -> error.getField() + ": " + error.getDefaultMessage())
					.collect(Collectors.joining(", "));
	
			// JSR-303/JSR-380 유효성 검사 실패 처리
			// 메서드 파라미터에 직접 @Validated를 붙여 유효성 검사를 할 때 실패하면 발생
			case ConstraintViolationException constraintViolationEx -> 
				message = constraintViolationEx.getConstraintViolations().stream()
					.map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
					.collect(Collectors.joining(", "));
	
			default -> message = ex.getMessage();
		}

		CommonErrorResponse errorResponse = CommonErrorResponse.builder()
				.path(path)
				.status(statusCode)
				.message(message)
				.name(VALIDATION_ERROR)
				.build();
		Object[] messageParams = {VALIDATION_ERROR, path, statusCode, message};
		log.error(messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_CONTROLLER_ADVICE_1.getMessageCode(), messageParams), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	/**
	 * WebClient의 .retrieve() 사용 시 발생하는 예외 처리
	 * 
	 * @param ex WebClientException
	 * @param request HttpServletRequest
	 * @return 공통 에러 응답 오브젝트
	 */
	@ExceptionHandler(WebClientException.class)
	public ResponseEntity<CommonErrorResponse> handleWebClientException(WebClientException ex, HttpServletRequest request) {
		// response Error 정보가 있는 경우에만 추출
		WebClientResponseException responseEx = ex instanceof WebClientResponseException responseError ? responseError : null;
		// WebClient 통신중 발생한 4XX/5XX 이외의 예외는 시스템 에러로 처리
		if (responseEx != null && responseEx.getStatusCode() != null && !responseEx.getStatusCode().isError()) {
			return handleException(ex, request);
		}
		String path = request.getRequestURI();
		int statusCode = responseEx != null ? responseEx.getStatusCode().value() : HttpStatus.BAD_REQUEST.value();
		String message = ex.getMessage();
		String body = responseEx != null ? responseEx.getResponseBodyAsString() : "";
		String errorName = responseEx ==  null ? API_REQUEST_ERROR : API_RESPONSE_ERROR;
		CommonErrorResponse errorResponse = CommonErrorResponse.builder()
				.path(path)
				.status(statusCode)
				.message(message)
				.body(body)
				.name(errorName)
				.build();
		Object[] messageParams = {errorName, path, statusCode, message, body};
		log.error(messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_CONTROLLER_ADVICE_2.getMessageCode(), messageParams), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(statusCode));
	}

	/**
	 * 비즈니스 로직에서 발생한 예외 처리
	 * 
	 * @param ex CommonBusinessException
	 * @param request HttpServletRequest
	 * @return 공통 에러 응답 오브젝트
	 */
	@ExceptionHandler(CommonBusinessException.class)
	public ResponseEntity<CommonErrorResponse> handleBusinessException(CommonBusinessException ex, HttpServletRequest request) {
		String path = request.getRequestURI();
		int statusCode = ObjectUtils.isEmpty(ex.getStatusCode()) ? HttpStatus.BAD_REQUEST.value() : ex.getStatusCode();
		String message = ex.getMessage();
		CommonErrorResponse errorResponse = CommonErrorResponse.builder()
				.path(path)
				.status(statusCode)
				.message(message)
				.name(BUSINESS_ERROR)
				.build();
		Object[] messageParams = {BUSINESS_ERROR, path, statusCode, message};
		log.error(messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_CONTROLLER_ADVICE_1.getMessageCode(), messageParams), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(statusCode));
	}

	/**
	 * ResponseStatusException 예외 처리
	 * 
	 * @param ex ResponseStatusException
	 * @param request HttpServletRequest
	 * @return 공통 에러 응답 오브젝트
	 */
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<CommonErrorResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
		String path = request.getRequestURI();
		int statusCode = ObjectUtils.isEmpty(ex.getStatusCode()) ? HttpStatus.BAD_REQUEST.value() : ex.getStatusCode().value();
		String message = ex.getReason();
		CommonErrorResponse errorResponse = CommonErrorResponse.builder()
				.path(path)
				.status(statusCode)
				.message(message)
				.name(BUSINESS_ERROR)
				.build();
		Object[] messageParams = {BUSINESS_ERROR.concat(" (StatusException)"), path, statusCode, message};
		log.error(messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_CONTROLLER_ADVICE_1.getMessageCode(), messageParams), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(statusCode));
	}
	
	/**
	 * 타임아웃 예외 처리
	 * 
	 * @param ex      TimeoutException
	 * @param request HttpServletRequest
	 * @return 공통 에러 응답 오브젝트
	 */
	@ExceptionHandler(TimeoutException.class)
	public ResponseEntity<CommonErrorResponse> handleTimeoutException(TimeoutException ex, HttpServletRequest request) {
	    String path = request.getRequestURI();
	    int statusCode = HttpStatus.GATEWAY_TIMEOUT.value();
	    String message = "API Request Timeout: " + ex.getMessage();
	    CommonErrorResponse errorResponse = CommonErrorResponse.builder()
	        .path(path)
	        .status(statusCode)
	        .message(message)
	        .name(TIMEOUT_ERROR)
	        .build();
	    Object[] messageParams = {TIMEOUT_ERROR, path, statusCode, message};
		log.error(messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_CONTROLLER_ADVICE_1.getMessageCode(), messageParams), ex);
	    return new ResponseEntity<>(errorResponse, HttpStatus.GATEWAY_TIMEOUT);
	}

	/**
	 * 그 밖에 모든 예외 처리
	 * 
	 * @param ex Exception
	 * @param request HttpServletRequest
	 * @return 공통 에러 응답 오브젝트
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<CommonErrorResponse> handleException(Exception ex, HttpServletRequest request) {
		String path = request.getRequestURI();
		int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
		String message = ex.getMessage();
		CommonErrorResponse errorResponse = CommonErrorResponse.builder()
				.path(path)
				.status(statusCode)
				.message(message)
				.name(SERVER_ERROR)
				.build();
		Object[] messageParams = {SERVER_ERROR, path, statusCode, message};
		log.error(messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_CONTROLLER_ADVICE_1.getMessageCode(), messageParams), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
