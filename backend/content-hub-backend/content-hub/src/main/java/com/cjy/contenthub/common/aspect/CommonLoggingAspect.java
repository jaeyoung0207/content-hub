package com.cjy.contenthub.common.aspect;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 공통 로그 출력 Aspect 클래스
 */
@Aspect
@Component
@Slf4j
public class CommonLoggingAspect {

	/**
	 * 컨트롤러 메소드 실행 시 로그 출력
	 * 
	 * @param joinPoint ProceedingJoinPoint
	 * @return 공통 로그 출력 메소드
	 * @throws Throwable 예외 발생 시
	 */
	@Around("execution(* com.cjy.contenthub..*Controller.*(..))")
	public Object controllerLogAround(ProceedingJoinPoint joinPoint) throws Throwable {
	    return logAround(joinPoint, "Controller");
	}
	
	/**
	 * 서비스 메소드 실행 시 로그 출력
	 * 
	 * @param joinPoint ProceedingJoinPoint
	 * @return 공통 로그 출력 메소드
	 * @throws Throwable 예외 발생 시
	 */
	@Around("execution(* com.cjy.contenthub..*Service.*(..))")
	public Object serviceLogAround(ProceedingJoinPoint joinPoint) throws Throwable {
	    return logAround(joinPoint, "Service");
	}


	/**
	 * 클라이언트 메소드 실행 시 로그 출력
	 * 
	 * @param joinPoint ProceedingJoinPoint
	 * @return 공통 로그 출력 메소드
	 * @throws Throwable 예외 발생 시
	 */
	@Around("execution(* com.cjy.contenthub.common.client.*.*(..))")
	public Object clientLogAround(ProceedingJoinPoint joinPoint) throws Throwable {
	    return logAround(joinPoint, "Client");
	}
	
	/**
	 * 공통 로그 출력 메소드
	 * 
	 * @param joinPoint ProceedingJoinPoint
	 * @param type 로그 타입
	 * @return Object
	 * @throws Throwable 예외 발생 시
	 */
	private Object logAround(ProceedingJoinPoint joinPoint, String type) throws Throwable {
	    String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
	    String methodName = joinPoint.getSignature().getName();
	    String args = Arrays.toString(joinPoint.getArgs());
	    try {
	    	log.info("{}_START: {}.{}({})", type, declaringTypeName, methodName, args);
	        Object result = joinPoint.proceed();
	        log.info("{}_END: {}.{}({})", type, declaringTypeName, methodName, args);
	        return result;
	    } catch (Throwable ex) {
	        log.error("{}_Error: {}.{} - {}", type, declaringTypeName, methodName, ex.getMessage(), ex);
	        throw ex;
	    }
	}

}
