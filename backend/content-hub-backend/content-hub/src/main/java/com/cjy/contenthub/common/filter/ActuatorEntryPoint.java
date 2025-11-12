package com.cjy.contenthub.common.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Actuator 인증 예외 처리 진입점
 */
@Component
public class ActuatorEntryPoint implements AuthenticationEntryPoint {
	
	// 개발자/디버깅용 원시 응답 파라미터/헤더 이름
    private static final String RAW_PARAM = "raw";
    
    // 개발자/디버깅용 원시 응답 헤더 이름
    private static final String RAW_HEADER = "X-Actuator-Raw";
    
    /** Actuator 개발자 모드 활성화 여부 */
    @Value("${app.actuator.developer-mode:false}")
    private boolean actuatorDeveloperMode;
	
	/**
	 * Actuator 인증 시 호출되는 메서드
	 * 
	 * @param request       HTTP 요청
	 * @param response      HTTP 응답
	 * @param authException 인증 예외
	 * @throws IOException 입출력 예외
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		
        String accept = request.getHeader("Accept");
        String rawHeader = request.getHeader(RAW_HEADER);
        String rawParam = request.getParameter(RAW_PARAM);

        boolean developerRequestedRaw = "1".equals(rawParam)
                || "true".equalsIgnoreCase(rawParam)
                || "1".equals(rawHeader)
                || "true".equalsIgnoreCase(rawHeader);

        // 개발자/디버깅 요청
        if (actuatorDeveloperMode && developerRequestedRaw) {
            response.addHeader("WWW-Authenticate", "Basic realm=\"Actuator\""); // 인증 요구 헤더 포함(Http Basic 인증에서 인증영역(realm) 지정)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드
            response.setContentType("text/plain;charset=UTF-8"); // 텍스트 응답
            response.getWriter().write("Unauthorized - actuator raw (developer override)"); // 텍스트 메시지
            response.flushBuffer(); // 응답 버퍼 플러시
            return;
        }

        // 브라우저(HTML) 요청(팝업을 막기 위해 WWW-Authenticate 헤더 없이 커스텀 HTML 반환)
        if (accept != null && accept.contains("text/html")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/html;charset=UTF-8"); // HTML 응답
            response.getWriter().write("<!doctype html><html><head><meta charset=\"utf-8\"><title>Unauthorized</title></head>"
                    + "<body style=\"padding:24px;\">"
                    + "<h1>Unauthorized</h1>"
                    + "<p>인증이 필요합니다. 관리자에게 문의하세요.</p>"
                    + "</body></html>"); // 커스텀 HTML 메시지
            response.flushBuffer();
            return;
        }

        // 모니터링/스크립트/비브라우저 클라이언트 요청
        response.addHeader("WWW-Authenticate", "Basic realm=\"Actuator\""); // 인증 요구 헤더 포함(Http Basic 인증에서 인증영역(realm) 지정)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8"); // JSON 응답
        response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\"}"); // JSON 메시지
        response.flushBuffer();
    }

}
