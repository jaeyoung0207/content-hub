package com.cjy.contenthub.common.aspect;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.character.service.CharacterServiceImpl;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMessagesErrorEnum;
import com.cjy.contenthub.common.util.MessageUtil;

import jakarta.validation.ValidationException;

@ExtendWith(MockitoExtension.class)
class CommonCacheAspectTest {
	
	CommonCacheAspect commonCacheAspect;

	@Mock
	CacheManager cacheManager;

	@Mock
	KeyGenerator keyGenerator;
	
	@Mock
	MessageUtil messageUtil;
	
	CharacterServiceImpl characterServiceImpl;
	
	@BeforeEach
	void setUp() {
		commonCacheAspect = new CommonCacheAspect(cacheManager, keyGenerator, messageUtil);
		MessageUtil characterMessageUtil = Mockito.mock(MessageUtil.class);
		WebClient anilistWebClient = Mockito.mock(WebClient.class);
		characterServiceImpl = new CharacterServiceImpl(characterMessageUtil, anilistWebClient);
	}
	
	@Test
	@DisplayName("[UT]afterThrowing: 메소드 실행 중 예외가 발생했을 때 캐시 삭제(동기식) - 대상 객체와 캐시가 존재하는 경우")
	void test_afterThrowing_existTargetAndCacheIsNotNull() {

		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		Cacheable cacheable = Mockito.mock(Cacheable.class);
		
		MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
		Method method = Mockito.mock(Method.class);
		String methodName = "getCharacter";
		when(joinPoint.getSignature()).thenReturn(methodSignature);
		when(methodSignature.getName()).thenReturn(methodName);
		when(methodSignature.getMethod()).thenReturn(method);
		when(method.getName()).thenReturn(methodName);
		when(method.getParameterTypes()).thenReturn(new Class[] {Integer.class});
		when(joinPoint.getTarget()).thenReturn(characterServiceImpl);
		when(joinPoint.getArgs()).thenReturn(new Object[] {1});
		String cacheName = "character";
		String[] cacheNames = {cacheName};
		when(cacheable.value()).thenReturn(cacheNames);
		
		String cacheKey = "cacheKey";
		when(keyGenerator.generate(any(Object.class), any(Method.class), any(Object[].class)))
		.thenReturn(cacheKey);
		
		Cache cache1 = Mockito.mock(Cache.class);
		when(cacheManager.getCache(cacheNames[0])).thenReturn(cache1);
		String message = String.format("캐시 삭제 - Cache Name: %s, Key: %s", cacheName, cacheKey);
		when(messageUtil.getMessageKO(any(String.class), any(Object[].class)))
		.thenReturn(message);
		
		// 실제 메소드 호출
		ValidationException exception = new ValidationException("Validation Exception");
		commonCacheAspect.afterThrowing(joinPoint, cacheable, exception);
		
		// 검증
		verify(cache1, Mockito.times(1)).evict(cacheKey);
		verify(keyGenerator, Mockito.times(1)).generate(any(Object.class), any(Method.class), any(Object[].class));
		verify(cacheManager, Mockito.times(1)).getCache(cacheNames[0]);
		verify(messageUtil, Mockito.times(1)).getMessageKO(any(String.class), any(Object[].class));
	}
	
	@Test
	@DisplayName("[UT]afterThrowing: 메소드 실행 중 예외가 발생했을 때 캐시 삭제(동기식) - 대상 객체는 존재하지만 캐시가 없는 경우")
	void test_afterThrowing_notExistTargetAndCacheIsNull() {

		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		Cacheable cacheable = Mockito.mock(Cacheable.class);
		
		MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
		Method method = Mockito.mock(Method.class);
		String methodName = "getCharacter";
		when(joinPoint.getSignature()).thenReturn(methodSignature);
		when(methodSignature.getName()).thenReturn(methodName);
		when(methodSignature.getMethod()).thenReturn(method);
		when(joinPoint.getTarget()).thenReturn(null);
		when(joinPoint.getArgs()).thenReturn(new Object[] {1});
		String[] cacheNames = {"character"};
		when(cacheable.value()).thenReturn(cacheNames);
		
		String cacheKey = "cacheKey";
		when(keyGenerator.generate(eq(joinPoint.getTarget()), any(Method.class), any(Object[].class)))
		.thenReturn(cacheKey);
		
		Cache cache1 = Mockito.mock(Cache.class);
		when(cacheManager.getCache(cacheNames[0])).thenReturn(null);
		
		// 실제 메소드 호출
		ValidationException exception = new ValidationException("Validation Exception");
		commonCacheAspect.afterThrowing(joinPoint, cacheable, exception);
		
		// 검증
		verify(cache1, Mockito.times(0)).evict(cacheKey);
		verify(keyGenerator, Mockito.times(1)).generate(eq(joinPoint.getTarget()), any(Method.class), any(Object[].class));
		verify(cacheManager, Mockito.times(1)).getCache(cacheNames[0]);
	}
	
	@Test
	@DisplayName("[UT]afterThrowing: 메소드 실행 중 예외가 발생했을 때 캐시 삭제(동기식) - 캐시 삭제 준비 처리 중 예외가 발생한 경우")
	void test_afterThrowing_prepareDeleteCacheError() {

		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		Cacheable cacheable = Mockito.mock(Cacheable.class);
		
		CharacterServiceImpl dummyTarget = Mockito.mock(CharacterServiceImpl.class);
		MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
		Method method = Mockito.mock(Method.class);
		String methodName = "getCharacter";
		when(joinPoint.getSignature()).thenReturn(methodSignature);
		when(methodSignature.getName()).thenReturn(methodName);
		when(methodSignature.getMethod()).thenReturn(method);
		when(method.getName()).thenReturn(methodName);
		when(joinPoint.getTarget()).thenReturn(dummyTarget);
		when(joinPoint.getArgs()).thenReturn(new Object[] {1});
		String[] cacheNames = {"character"};
		when(cacheable.value()).thenReturn(cacheNames);

		// 예외 발생하도록 설정
		String causeMessage = "NullPointerException";
		when(keyGenerator.generate(any(Object.class), any(Method.class), any(Object[].class)))
		.thenThrow(new NullPointerException(causeMessage));
		
		String errorMessage = String.format("캐시 삭제 실패 (After Throwing) - Method: %s, Error: %s", methodName, causeMessage);
		when(messageUtil.getMessageKO(any(String.class), any(Object[].class)))
		.thenReturn(errorMessage);
		
		// 실제 메소드 호출
		ValidationException exception = new ValidationException("Validation Exception");
		commonCacheAspect.afterThrowing(joinPoint, cacheable, exception);
		
		// 검증
		verify(keyGenerator, Mockito.times(1)).generate(any(Object.class), any(Method.class), any(Object[].class));
		verify(messageUtil, Mockito.times(1)).getMessageKO(any(String.class), any(Object[].class));
	}
	
	@Test
	@DisplayName("[UT]afterThrowing: 메소드 실행 중 예외가 발생했을 때 캐시 삭제(동기식) - 캐시 삭제 중 예외가 발생한 경우")
	void test_afterThrowing_deleteCacheError() {

		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		Cacheable cacheable = Mockito.mock(Cacheable.class);
		
		MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
		Method method = Mockito.mock(Method.class);
		String methodName = "getCharacter";
		when(joinPoint.getSignature()).thenReturn(methodSignature);
		when(methodSignature.getName()).thenReturn(methodName);
		when(methodSignature.getMethod()).thenReturn(method);
		when(joinPoint.getTarget()).thenReturn(null);
		when(joinPoint.getArgs()).thenReturn(new Object[] {1});
		String cacheName = "character";
		String[] cacheNames = {cacheName};
		when(cacheable.value()).thenReturn(cacheNames);

		String cacheKey = "cacheKey";
		when(keyGenerator.generate(eq(joinPoint.getTarget()), any(Method.class), any(Object[].class)))
		.thenReturn(cacheKey);
		
		// 예외 발생하도록 설정
		String causeMessage = "Cache retrieval error";
		when(cacheManager.getCache(cacheNames[0])).thenThrow(new RuntimeException(causeMessage));
		
		Object[] messageParams = {methodName, cacheName, cacheKey, causeMessage};
		String errorMessage = String.format("캐시 삭제 실패 - Cache Name: %s, Key: %s, error: %s", cacheName, methodName, causeMessage);
		when(messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_FAILED_CACHE_DELETE.getMessageCode(), messageParams))
		.thenReturn(errorMessage);
		
		// 실제 메소드 호출
		ValidationException exception = new ValidationException("Validation Exception");
		commonCacheAspect.afterThrowing(joinPoint, cacheable, exception);
		
		// 검증
		verify(keyGenerator, Mockito.times(1)).generate(eq(joinPoint.getTarget()), any(Method.class), any(Object[].class));
		verify(messageUtil, Mockito.times(1)).getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_FAILED_CACHE_DELETE.getMessageCode(), messageParams);
	}

}
