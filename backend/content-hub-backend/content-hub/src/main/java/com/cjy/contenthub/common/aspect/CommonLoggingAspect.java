package com.cjy.contenthub.common.aspect;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.cjy.contenthub.common.annotation.MaskingTarget;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 공통 로그 출력 Aspect 클래스
 */
@Aspect
@Order(1) // 가장 먼저 실행되도록 설정
@Component
@RequiredArgsConstructor
@Slf4j
public class CommonLoggingAspect {

	/** 컨트롤러 타입 상수 */
	private static final String CONTROLLER_TYPE = "Controller";

	/** Java 기본 타입 및 자주 사용하는 타입 Set */
	private static final Set<String> JAVA_DEFAULT_TYPES = Set.of(
			byte.class.getName(),
			short.class.getName(),
			int.class.getName(),
			long.class.getName(),
			float.class.getName(),
			double.class.getName(),
			boolean.class.getName(),
			char.class.getName(),
			String.class.getName(), 
			Integer.class.getName(), 
			Long.class.getName(), 
			Boolean.class.getName(),
			Double.class.getName(), 
			Float.class.getName(), 
			Byte.class.getName(), 
			Short.class.getName(), 
			Character.class.getName(),
			BigDecimal.class.getName(),
			BigInteger.class.getName(),
			java.util.Date.class.getName(),
			java.sql.Date.class.getName(),
			java.sql.Timestamp.class.getName(),
			java.time.LocalDate.class.getName(),
			java.time.LocalDateTime.class.getName(),
			java.time.Instant.class.getName()
			);

	/**
	 * 컨트롤러 메소드 실행 시 로그 출력
	 * 
	 * @param joinPoint ProceedingJoinPoint
	 * @return 공통 로그 출력 메소드
	 * @throws Throwable 예외 발생 시
	 */
	@Around("execution(* com.cjy.contenthub..*Controller.*(..))")
	public Object controllerLogAround(ProceedingJoinPoint joinPoint) throws Throwable {
		return logAround(joinPoint, CONTROLLER_TYPE);
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
	 * 헬퍼 메소드 실행 시 로그 출력
	 * 
	 * @param joinPoint ProceedingJoinPoint
	 * @return 공통 로그 출력 메소드
	 * @throws Throwable 예외 발생 시
	 */
	@Around("execution(* com.cjy.contenthub..*Helper.*(..))")
	public Object helperLogAround(ProceedingJoinPoint joinPoint) throws Throwable {
		return logAround(joinPoint, "Helper");
	}

	/**
	 * 레포지토리 메소드 실행 시 로그 출력
	 * 
	 * @param joinPoint ProceedingJoinPoint
	 * @return 공통 로그 출력 메소드
	 * @throws Throwable 예외 발생 시
	 */
	@Around("execution(* com.cjy.contenthub..*Repository.*(..))")
	public Object repositoryLogAround(ProceedingJoinPoint joinPoint) throws Throwable {
		return logAround(joinPoint, "Repository");
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
		// 클래스 이름
		String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
		// 메소드 이름
		String methodName = joinPoint.getSignature().getName();
		//  메소드 정보
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		String args;
		// 메소드 인자 가공
		args = formatArgsForLog(method, joinPoint.getArgs());
		// 실행 시작 시간
		long startTime = System.currentTimeMillis();
		try {
			if (CONTROLLER_TYPE.equals(type)) {
				log.info("{}_START: {}.{}({})", type, declaringTypeName, methodName, args);
			} else if (log.isDebugEnabled()) {
				log.debug("{}_START: {}.{}({})", type, declaringTypeName, methodName, args);
			}
			// 메소드 실행
			Object result = joinPoint.proceed();

			// 비동기(CompletableFuture) 처리인 경우
			if (result instanceof CompletableFuture) {
				CompletableFuture<?> future = (CompletableFuture<?>) result;
				// CompletableFuture가 완료될 때 로그 출력
				return future.whenComplete((res, ex) -> {
					// 경과 시간 계산
					long duration = System.currentTimeMillis() - startTime;
					if (ex != null) {
						// 실패 로그
						log.error("{}_END(ASYNC)_FAILED: {}.{}({}) - {}ms (Exception: {})", 
								type, declaringTypeName, methodName, args, duration, ex.getMessage());
					} else {
						// 성공 로그
						if (CONTROLLER_TYPE.equals(type)) {
							log.info("{}_END(ASYNC): {}.{}({}) - {}ms", 
									type, declaringTypeName, methodName, args, duration);
						} else if (log.isDebugEnabled()) {
							log.debug("{}_END(ASYNC): {}.{}({}) - {}ms", 
									type, declaringTypeName, methodName, args, duration);
						}
					}
				});
			} 

			// 경과 시간 계산
			long elapsedTime = System.currentTimeMillis() - startTime;
			if (CONTROLLER_TYPE.equals(type)) {
				log.info("{}_END: {}.{}({}) - {}ms", type, declaringTypeName, methodName, args, elapsedTime);
			} else if (log.isDebugEnabled()) {
				log.debug("{}_END: {}.{}({}) - {}ms", type, declaringTypeName, methodName, args, elapsedTime);
			}
			return result;
		} catch (Throwable ex) {
			log.error("{}_Error: {}.{} - {}", type, declaringTypeName, methodName, ex.getMessage());
			throw ex;
		}
	}

	/**
	 * 메소드 인자를 로그출력용으로 가공
	 * 
	 * @param method 메소드
	 * @param args   메소드 인자 배열
	 * @return 가공된 메소드 인자 문자열
	 */
	private String formatArgsForLog(Method method, Object[] args) {

		// 메소드 파라미터 정보
		Parameter[] parameters = method.getParameters();
		// 파라미터 개수와 인자 개수가 다르면 원본 args 반환
		if (args.length != parameters.length) {
			return Arrays.toString(args);
		}

		// 반환할 파라미터 배열
		String[] argArray = new String[args.length];
		try {
			// 각 파라미터 가공
			for (int i = 0; i < args.length; i++) {
				// 파라미터 정보
				Parameter param = parameters[i];
				// 파라미터 타입
				Class<?> clazz = param.getType();
				// 로그용 파라미터 문자열
				String logArg;
				// 각 파라미터별로 마스킹 처리(마스킹용 어노테이션이 붙은 경우)
				if (param.isAnnotationPresent(MaskingTarget.class)) {
					String paramName = param.getName();
					logArg = String.format("[REDACTED: %s]", paramName);
				}
				// 자바 기본 타입 및 자주 사용하는 타입이 아닌 경우 객체 타입으로 표시
				else if (!JAVA_DEFAULT_TYPES.contains(clazz.getName())) {
					logArg = String.format("[OBJECT: %s]", clazz.getSimpleName());
				}
				// 그 외에는 원본 값 사용
				else {
					logArg = args[i] != null ? args[i].toString() : "null";
				}
				argArray[i] = logArg;
			}
		} catch (Exception ex) {
			log.error("메소드 파라미터 가공 실패: {}", ex.getMessage(), ex);
			return Arrays.toString(args);
		}
		// 가공된 파라미터 배열을 문자열로 변환하여 반환
		return Arrays.toString(argArray);
	}

}
