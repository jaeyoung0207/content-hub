package com.cjy.contenthub.common.aspect;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import org.apache.coyote.BadRequestException;
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
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.cjy.contenthub.character.service.CharacterServiceImpl;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMessagesErrorEnum;
import com.cjy.contenthub.common.util.MessageUtil;

import jakarta.validation.ValidationException;
import reactor.core.publisher.Mono;

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
		ExecutorService apiTaskExecutor = Executors.newSingleThreadExecutor();
		characterServiceImpl = new CharacterServiceImpl(characterMessageUtil, anilistWebClient, apiTaskExecutor);
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
	
	@Test
	@DisplayName("[UT]aroundAsync: 메소드 실행 중 예외가 발생했을 때 캐시 삭제(비동기식) - 에러가 존재하는 경우")
	void test_aroundAsync_existError() throws Throwable {
		
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		Cacheable cacheable = Mockito.mock(Cacheable.class);
		
		// 예외 설정
		CompletableFuture<?> resultFuture = Mockito.mock(CompletableFuture.class);
		BadRequestException causeException = new BadRequestException("WebClient Exception");
		WebClientRequestException webClientRequestException = 
				new WebClientRequestException(causeException, null, null, HttpHeaders.EMPTY);
		when(joinPoint.proceed()).thenReturn(resultFuture);

		// whenComplete 호출 시 BiConsumer의 accept 메서드가 호출되도록 설정
		doAnswer(invocation -> {
			BiConsumer<Object, Throwable> consumer = invocation.getArgument(0);
			// 예외를 전달하여 accept 메서드 호출
			consumer.accept(null, webClientRequestException);
			return null;
		}).when(resultFuture).whenComplete(Mockito.any());
		
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
		commonCacheAspect.aroundAsync(joinPoint, cacheable);
		
		// 검증
		verify(cache1, Mockito.times(1)).evict(cacheKey);
		verify(keyGenerator, Mockito.times(1)).generate(any(Object.class), any(Method.class), any(Object[].class));
		verify(cacheManager, Mockito.times(1)).getCache(cacheNames[0]);
		verify(messageUtil, Mockito.times(1)).getMessageKO(any(String.class), any(Object[].class));
	}
	
	@Test
	@DisplayName("[UT]aroundAsync: 메소드 실행 중 예외가 발생했을 때 캐시 삭제(비동기식) - 에러가 존재하지 않는 경우")
	void test_aroundAsync_notExistError() throws Throwable {
		
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		Cacheable cacheable = Mockito.mock(Cacheable.class);
		
		CompletableFuture<?> resultFuture = Mockito.mock(CompletableFuture.class);
		when(joinPoint.proceed()).thenReturn(resultFuture);

		// whenComplete 호출 시 BiConsumer의 accept 메서드가 호출되도록 설정
		doAnswer(invocation -> {
			BiConsumer<Object, Throwable> consumer = invocation.getArgument(0);
			// 예외를 전달하여 accept 메서드 호출
			consumer.accept(null, null);
			return null;
		}).when(resultFuture).whenComplete(Mockito.any());
		
		// 실제 메소드 호출
		commonCacheAspect.aroundAsync(joinPoint, cacheable);
		
		// 검증
		verify(resultFuture, Mockito.times(1)).whenComplete(Mockito.any());
		verify(keyGenerator, Mockito.times(0)).generate(any(Object.class), any(Method.class), any(Object[].class));
	}
	
	@Test
	@DisplayName("[UT]aroundAsync: 메소드 실행 중 예외가 발생했을 때 캐시 삭제(비동기식) - CompletableFuture가 아닌 경우")
	void test_aroundAsync_notCompletableFuture() throws Throwable {
		
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		Cacheable cacheable = Mockito.mock(Cacheable.class);
		
		Mono<?> mono = Mockito.mock(Mono.class);
		when(joinPoint.proceed()).thenReturn(mono);

		// 실제 메소드 호출
		commonCacheAspect.aroundAsync(joinPoint, cacheable);
		
		// 검증
		verify(joinPoint, Mockito.times(1)).proceed();
		verify(keyGenerator, Mockito.times(0)).generate(any(Object.class), any(Method.class), any(Object[].class));
	}

}
