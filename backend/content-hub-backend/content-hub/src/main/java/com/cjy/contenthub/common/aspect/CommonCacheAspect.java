package com.cjy.contenthub.common.aspect;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.cjy.contenthub.common.constants.CommonEnum.CommonMessagesErrorEnum;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMessagesInfoEnum;
import com.cjy.contenthub.common.util.MessageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 공통 캐시 Aspect 클래스
 * 
 * @AfterThrowing 어노테이션을 사용하여 메소드 실행 중 예외가 발생했을 때 캐시를 삭제
 */
@Aspect
@Order(10) // 로깅 Aspect(1) 이후에 실행되도록 설정
@Component
@RequiredArgsConstructor
@Slf4j
public class CommonCacheAspect {

	/** 캐시 관리자 */
	private final CacheManager cacheManager;

	/** 캐시 키 생성기 */
	private final KeyGenerator keyGenerator;
	
	/** 메시지 유틸 */
	private final MessageUtil messageUtil;

	/**
	 * 메소드 실행 중 예외가 발생했을 때 캐시 삭제(동기식)
	 * @Cacheable이 붙은 메소드의 객체가 반환되기 전까지의 동기식 처리에만 적용
	 * 예: 인자 유효성 검사 실패 등 동기적인 예외
	 * 
	 * @param joinPoint JoinPoint
	 * @param cacheable Cacheable 어노테이션
	 * @param ex        발생한 예외
	 */
	@AfterThrowing(pointcut = "@annotation(cacheable)", throwing = "ex")
	public void afterThrowing(JoinPoint joinPoint, Cacheable cacheable, Throwable ex) {
		prepareDeleteCache((ProceedingJoinPoint) joinPoint, cacheable);
	}
	
	/**
	 * 메소드 실행 중 예외가 발생했을 때 캐시 삭제(비동기식)
	 * @Cacheable이 붙은 메소드의 CompletableFuture 객체가 완료되는 시점까지의 비동기식 처리에 적용
	 * 예: WebClient 호출 실패, DB 처리 실패 등 비동기 작업 중 발생하는 예외
	 * 
	 * @param joinPoint ProceedingJoinPoint
	 * @param cacheable Cacheable 어노테이션
	 * @return Object 원래 메소드의 반환값 또는 수정된 CompletableFuture
	 * @throws Throwable 예외 발생 시
	 */
	@Around("@annotation(cacheable) && execution(java.util.concurrent.CompletableFuture *(..))")
	public Object aroundAsync(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {
	    
	    // 실제 비동기 메서드 실행 (CompletableFuture 반환)
	    Object result = joinPoint.proceed();

	    // CompletableFuture인지 확인
	    if (result instanceof CompletableFuture) {
	        CompletableFuture<?> resultFuture = (CompletableFuture<?>) result;

	        // CompletableFuture가 성공 또는 실패로 완료될 때 콜백 등록
	        return resultFuture.whenComplete((res, ex) -> {
	            // 예외 발생 시 (비동기 작업이 실패한 경우)
	            if (ex != null) {
	            	prepareDeleteCache(joinPoint, cacheable);
	            }
	        });
	    }
	    // CompletableFuture가 아니면 원래 결과 반환
	    return result;
	}
	
	/**
	 * 캐시 삭제 준비
	 * 
	 * @param joinPoint AOP 조인 포인트
	 * @param cacheable Cacheable 어노테이션
	 */
	private void prepareDeleteCache(ProceedingJoinPoint joinPoint, Cacheable cacheable) {
        try {
            // AOP 대상 메서드 정보
            String methodName = joinPoint.getSignature().getName();
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            Method implMethod = findImplementationMethod(joinPoint.getTarget(), method);
            // 캐시 이름 배열
            String[] cacheNameArr = cacheable.value();
            // 캐시 키 생성
            Object key = keyGenerator.generate(joinPoint.getTarget(), implMethod, joinPoint.getArgs());
            // 각 캐시에서 해당 키 삭제
            for (String cacheName : cacheNameArr) {
                deleteCache(cacheName, key, methodName);
            }
        } catch(Exception e) {
            // 캐시 삭제 중 발생한 예외 로깅
            Object[] messageParams = {joinPoint.getSignature().getName(), e.getMessage()};
            log.error(messageUtil.getMessageKO(
                CommonMessagesErrorEnum.ERROR_COMMON_FAILED_CACHE_DELETE_IN_AFTER_THROWING.getMessageCode(), 
                messageParams));
        }
	}
	
	/**
	 * 인터페이스 메소드에 대한 실제 구현 메소드 반환
	 * 
	 * @param target          대상 객체
	 * @param interfaceMethod 인터페이스 메소드
	 * @return 실제 구현 메소드
	 */
	private Method findImplementationMethod(Object target, Method interfaceMethod) {
	    if (target == null) {
	        return interfaceMethod;
	    }
	    try {
	        return target.getClass().getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());
	    } catch (NoSuchMethodException e) {
	        return interfaceMethod;
	    }
	}

	/**
	 * 캐시에서 특정 키 삭제
	 * 
	 * @param cacheName  캐시 이름
	 * @param key        캐시 키
	 * @param methodName 메소드 이름
	 */
	private void deleteCache(String cacheName, Object key, String methodName) {
		try {
			Cache cache = cacheManager.getCache(cacheName);
			if (cache != null) {
				Object[] messageParams = {cacheName, key};
				log.info(messageUtil.getMessageKO(CommonMessagesInfoEnum.INFO_COMMON_CACHEDELETE.getMessageCode(), messageParams));
				cache.evict(key);
			}
		} catch (Exception e) {
			Object[] messageParams = {methodName, cacheName, key, e.getMessage()};
			log.error(messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_FAILED_CACHE_DELETE.getMessageCode(), messageParams));
		}
	}

}
