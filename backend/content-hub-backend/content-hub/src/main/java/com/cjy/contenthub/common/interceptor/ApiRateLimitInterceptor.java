package com.cjy.contenthub.common.interceptor;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.cjy.contenthub.common.advice.response.CommonErrorResponse;
import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.MessagesDebugEnum;
import com.cjy.contenthub.common.constants.CommonEnum.MessagesWarnEnum;
import com.cjy.contenthub.common.ratelimit.service.ApiRateLimitService;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.common.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * API Rate Limit 인터셉터 
 * 특정 IP 주소와 요청 URI에 대해 일정 시간 내에 허용된 요청 횟수를 초과하는 경우 요청을 차단
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiRateLimitInterceptor implements HandlerInterceptor {

	/** Redis 유틸 */
	private final RedisUtil redisUtil;
	
	/** 메시지 유틸리티 */
	private final MessageUtil messageUtil;
	
	/** API Rate Limit 서비스 */
	private final ApiRateLimitService apiRateLimitService;
	
	/**
	 * API 요청 전 처리 메소드
	 * 
	 * @param request  HTTP 요청 객체
	 * @param response HTTP 응답 객체
	 * @param handler  핸들러 객체
	 * @return boolean 요청 허용 여부 (true: 허용, false: 차단)
	 * @throws Exception 예외 발생 시
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		// OPTIONS 요청은 Rate Limit 적용하지 않음
		if (HttpMethod.OPTIONS.matches(request.getMethod())) {
			return true;
		}
		
		// 클라이언트 IP 주소와 요청 URI를 기반으로 고유 키 생성
		String ip = request.getRemoteAddr();
		String uri = request.getRequestURI();
		String key = "rate_limit:".concat(ip).concat(CommonConstants.COLON).concat(uri);
		
		// 해당 URI에 대한 최대 요청 횟수와 시간(초) 조회
		int maxRequestCount = apiRateLimitService.getMaxRequestCount(uri);
		int seconds = apiRateLimitService.getSeconds(uri);
		
		// Lua 스크립트를 사용하여 원자적으로 요청 횟수를 증가시키고 만료 시간을 설정
		String script = "local current=redis.call('INCR', KEYS[1]) " +
                "if current==1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end " +
                "return current";
		// KEYS[1]: 요청을 추적할 고유 키
		List<String> keyList = Collections.singletonList(key);
		// ARGV[1]: 요청 제한 기간(초 단위)
		List<String> args = Collections.singletonList(String.valueOf(seconds));
		// 스크립트 실행
		Long count = redisUtil.executeScript(script, keyList, args, Long.class);
		
		Object[] logParams = { ip, uri, key, count, maxRequestCount };
		log.debug(messageUtil.getMessageKO(
				MessagesDebugEnum.DEBUG_COMMON_API_RATE_LIMIT_CHECK.getMessageCode(), logParams));
		
		// 요청 횟수가 허용된 최대치를 초과하는 경우
		if (count > maxRequestCount) {
			// 남은 TTL(Time To Live) 값을 가져와서 응답 헤더에 설정
			Long ttl = redisUtil.getExpire(key, TimeUnit.SECONDS);
			// 429 Too Many Requests 상태 코드와 함께 응답 설정
			response.setStatus(HttpStatus.SC_TOO_MANY_REQUESTS);
			// JSON 응답으로 설정
			response.setContentType("application/json; charset=UTF-8");
			// Retry-After 헤더 설정 (클라이언트가 재시도하기 전에 기다려야 하는 시간)
			response.setHeader(HttpHeaders.RETRY_AFTER, 
					ttl != null && ttl > 0 ? String.valueOf(ttl) : String.valueOf(seconds));
			// 응답 본문에 공통 에러 응답 작성
			CommonErrorResponse errorResponse = CommonErrorResponse.builder()
					.path(uri)
					.status(HttpStatus.SC_TOO_MANY_REQUESTS)
					.message("Too many requests - try again after " + ttl + " seconds")
					.name("API Rate Limit Exceeded Error")
					.build();
			// 응답 본문을 JSON 형식으로 작성
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonResponse = objectMapper.writeValueAsString(errorResponse);
			// 응답 본문에 제한 초과 메시지 작성
			response.getWriter().write(jsonResponse);
			Object[] messageParams = { ip, uri, count, ttl };
			log.warn(messageUtil.getMessageKO(
					MessagesWarnEnum.WARN_COMMON_API_RATE_LIMIT_EXCEEDED.getMessageCode(), messageParams));
			return false;
		}
		// 요청이 허용된 경우 true 반환
		return true;
	}
}
