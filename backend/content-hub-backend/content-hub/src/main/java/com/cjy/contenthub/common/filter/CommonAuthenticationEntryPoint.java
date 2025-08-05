package com.cjy.contenthub.common.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.cjy.contenthub.common.constants.CommonConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 공통 인증 엔트리 포인트 클래스
 * Spring Security에서 인증 예외가 발생했을 때 호출되는 엔트리 포인트
 * Spring Security의 AuthenticationEntryPoint 인터페이스를 구현하며, 예외 처리 리졸버를 사용하여 인증 예외를 처리
 */
@Component
public class CommonAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	/** 예외 처리 리졸버 */
	private final HandlerExceptionResolver resolver;
	
	/**
	 * 생성자
	 * 예외 처리 리졸버를 주입받아 CommonAuthenticationEntryPoint를 초기화
	 * @Qualifier를 사용하여 특정 빈을 주입받음
	 * 
	 * @param resolver 예외 처리 리졸버
	 */
	public CommonAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

	/**
	 * 인증되지 않은 유저가 보호된 리소스에 접근하려고 할 때 호출되는 메소드
	 * 이 메소드에서는 CORS 헤더를 수동으로 추가하여 CORS 정책을 준수하고,
	 * 예외 처리 리졸버를 사용하여 인증 예외를 처리
	 * 
	 * @param request HTTP 요청 객체
	 * @param response HTTP 응답 객체
	 * @param authException 인증 예외 객체
	 * @throws IOException I/O 예외
	 * @throws ServletException 서블릿 예외
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		// AuthenticationEntryPoint 가 필터 체인의 바깥에서 실행되기 때문에, 
	    // 미리 설정해 놓은 WebMvcConfigurer의 addCorsMappings()의 CORS 설정이 적용되지 않으므로, CORS 헤더 직접 수동 추가
	    response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader(HttpHeaders.ORIGIN)); // 요청의 Origin 헤더를 사용하여 CORS 허용
	    response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.toString(true)); // 쿠키, 인증 헤더 등을 포함할 수 있도록 허용
	    response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.join(CommonConstants.COMMA, 
	    		HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), 
	    		HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name())); // 허용할 HTTP 메서드 설정
	    response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, String.join(CommonConstants.COMMA, 
	    		HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE)); // 허용할 헤더 설정
	    
	    // 예외 처리 리졸버를 사용하여 인증 예외를 처리
		resolver.resolveException(request, response, null, authException);
	}
}
