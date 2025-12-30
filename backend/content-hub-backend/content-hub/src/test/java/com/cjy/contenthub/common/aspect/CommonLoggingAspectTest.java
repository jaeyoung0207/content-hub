package com.cjy.contenthub.common.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import com.cjy.contenthub.common.annotation.MaskingTarget;
import com.cjy.contenthub.core.repository.entity.UserEntity;
import com.cjy.contenthub.detail.information.controller.dto.DetailTvResponseDto;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class CommonLoggingAspectTest {
	
	private Logger logger = (Logger) LoggerFactory.getLogger(CommonLoggingAspect.class); 
    private ListAppender<ILoggingEvent> listAppender;
	
	CommonLoggingAspect commonLoggingAspect = new CommonLoggingAspect();
	
	@BeforeEach
	void setUp() {
		// ListAppender 생성
        listAppender = new ListAppender<>();
        listAppender.start();
        // Logger에 ListAppender 추가
        logger.addAppender(listAppender);
	}
	
	@Test
	@DisplayName("[UT]controllerLogAround: 컨트롤 메소드 실행 시 로그 출력 - formatArgsForLog 메소드 성공")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void test_controllerLogAround_formatArgsForLogMethodSuccess() throws Throwable {
		
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
		Method method = Mockito.mock(Method.class);
		String methodName = "getTvDetail";
		when(joinPoint.getSignature()).thenReturn(methodSignature);
		String declaringTypeName = "com.cjy.contenthub.detail.information.controller.DetailInformationController";
		when(methodSignature.getDeclaringTypeName())
		.thenReturn(declaringTypeName);
		when(methodSignature.getName()).thenReturn(methodName);
		when(methodSignature.getMethod()).thenReturn(method);
		
		Parameter[] parameters = new Parameter[4];
		parameters[0] = Mockito.mock(Parameter.class);
		when(parameters[0].getType()).thenReturn((Class)Long.class);
		when(parameters[0].isAnnotationPresent(MaskingTarget.class)).thenReturn(true);
		String paramName1 = "userId";
		when(parameters[0].getName()).thenReturn(paramName1);
		String className = DetailTvResponseDto.class.getSimpleName();
		parameters[1] = Mockito.mock(Parameter.class);
		when(parameters[1].getType()).thenReturn((Class)DetailTvResponseDto.class);
		when(parameters[1].isAnnotationPresent(MaskingTarget.class)).thenReturn(false);
		parameters[2] = Mockito.mock(Parameter.class);
		when(parameters[2].getType()).thenReturn((Class)String.class);
		when(parameters[2].isAnnotationPresent(MaskingTarget.class)).thenReturn(false);
		parameters[3] = Mockito.mock(Parameter.class);
		when(parameters[3].getType()).thenReturn((Class)String.class);
		when(parameters[3].isAnnotationPresent(MaskingTarget.class)).thenReturn(false);
		
		when(method.getParameters()).thenReturn(parameters);
		when(joinPoint.getArgs()).thenReturn(new Object[] {1, 2, 3, null});
		
		CompletableFuture<?> resultFuture = Mockito.mock(CompletableFuture.class);
		when(joinPoint.proceed()).thenReturn(resultFuture);

		// 실제 메소드 호출
		commonLoggingAspect.controllerLogAround(joinPoint);
		
		// 로그 메시지 검증
		List<ILoggingEvent> logsList = listAppender.list;
		String type = "Controller";
		String redactedFormat = "[REDACTED: %s]";
		String objectFormat = "[OBJECT: %s]";
		String[] arrays = {
				String.format(redactedFormat, paramName1), 
				String.format(objectFormat, className), 
				"3",
				"null"
		};
		String args = String.join(", ", arrays);
		String startLogFormat = "%s_START: %s.%s([%s])";
		String endAsyncLogFormat = "%s_END: %s.%s([%s])";
		String expectedStartMessage = String.format(startLogFormat, type, declaringTypeName, methodName, args);
		String expectedEndMessage = String.format(endAsyncLogFormat, type, declaringTypeName, methodName, args);
		boolean startLogFound = logsList.stream()
				.filter(event -> event.getLevel().equals(Level.INFO))
				.map(ILoggingEvent::getFormattedMessage)
				.anyMatch(event -> event.contains(expectedStartMessage));
		boolean endLogFound = logsList.stream()
				.filter(event -> event.getLevel().equals(Level.INFO))
				.map(ILoggingEvent::getFormattedMessage)
				.anyMatch(event -> event.contains(expectedEndMessage));
		assertThat(startLogFound).isTrue();
		assertThat(endLogFound).isTrue();
		System.out.println(expectedStartMessage);
		System.out.println(expectedEndMessage);
	}
	
	@Test
	@DisplayName("[UT]controllerLogAround: 컨트롤러 메소드 실행 시 로그 출력 - formatArgsForLog 메소드 에러")
	@SuppressWarnings({ "unchecked" })
	void test_controllerLogAround_formatArgsForLogMethodError() throws Throwable {
		
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
		Method method = Mockito.mock(Method.class);
		String methodName = "findByProviderAndProviderId";
		when(joinPoint.getSignature()).thenReturn(methodSignature);
		String declaringTypeName = "com.cjy.contenthub.app.controller.AppController";
		when(methodSignature.getDeclaringTypeName())
		.thenReturn(declaringTypeName);
		when(methodSignature.getName()).thenReturn(methodName);
		when(methodSignature.getMethod()).thenReturn(method);
		
		// 예외 발생하는 파라미터 설정
		Parameter[] parameters = new Parameter[1];
		parameters[0] = Mockito.mock(Parameter.class);
		String errorMessage = "Parameter type error";
		when(parameters[0].getType()).thenThrow(new IllegalArgumentException(errorMessage));
		when(method.getParameters()).thenReturn(parameters);
		
		boolean arg1 = false;
		when(joinPoint.getArgs()).thenReturn(new Object[] {arg1});
		
		ResponseEntity<Void> setAdultFlg = Mockito.mock(ResponseEntity.class);
		when(joinPoint.proceed()).thenReturn(setAdultFlg);

		// 실제 메소드 호출
		commonLoggingAspect.controllerLogAround(joinPoint);
		
		// 로그 메시지 검증
		List<ILoggingEvent> logsList = listAppender.list;
		String type = "Controller";
		String[] arrays = {
				String.valueOf(arg1)
		};
		String args = String.join(", ", arrays);
		String startLogFormat = "%s_START: %s.%s([%s])";
		String endAsyncLogFormat = "%s_END: %s.%s([%s])";
		String expectedStartMessage = String.format(startLogFormat, type, declaringTypeName, methodName, args);
		String expectedEndMessage = String.format(endAsyncLogFormat, type, declaringTypeName, methodName, args);
		String expectedErrorLogMessage = "메소드 파라미터 가공 실패: " + errorMessage;
		boolean startLogFound = logsList.stream()
				.map(ILoggingEvent::getFormattedMessage)
				.anyMatch(event -> event.contains(expectedStartMessage));
		boolean endLogFound = logsList.stream()
				.map(ILoggingEvent::getFormattedMessage)
				.anyMatch(event -> event.contains(expectedEndMessage));
		boolean errorLogFound = logsList.stream()
				.map(ILoggingEvent::getFormattedMessage)
				.anyMatch(event -> event.contains(expectedErrorLogMessage));
		assertThat(startLogFound).isTrue();
		assertThat(endLogFound).isTrue();
		assertThat(errorLogFound).isTrue();
		System.out.println(expectedStartMessage);
		System.out.println(expectedEndMessage);
		System.out.println(expectedErrorLogMessage);
	}
	
	@Test
	@DisplayName("[UT]controllerLogAround: 컨트롤러 메소드 실행 시 로그 출력 - logAround 메소드 에러")
	void test_controllerLogAround_logAroundMethodError() throws Throwable {
		
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
		Method method = Mockito.mock(Method.class);
		String methodName = "findByProviderAndProviderId";
		when(joinPoint.getSignature()).thenReturn(methodSignature);
		String declaringTypeName = "com.cjy.contenthub.app.controller.AppController";
		when(methodSignature.getDeclaringTypeName())
		.thenReturn(declaringTypeName);
		when(methodSignature.getName()).thenReturn(methodName);
		when(methodSignature.getMethod()).thenReturn(method);
		
		Parameter[] parameters = new Parameter[0];
		when(method.getParameters()).thenReturn(parameters);
		boolean arg1 = false;
		when(joinPoint.getArgs()).thenReturn(new Object[] {arg1});
		
		// joinPoint.proceed() 에러 설정
		String errorMessage = "Proceed method error";
		when(joinPoint.proceed()).thenThrow(new RuntimeException(errorMessage));

		// 실제 메소드 호출
		assertThatThrownBy(() -> commonLoggingAspect.controllerLogAround(joinPoint))
		.isInstanceOf(RuntimeException.class)
		.hasMessage(errorMessage);
		
		// 로그 메시지 검증
		List<ILoggingEvent> logsList = listAppender.list;
		String type = "Controller";
		String[] arrays = {
				String.valueOf(arg1)
		};
		String args = String.join(", ", arrays);
		String startLogFormat = "%s_START: %s.%s([%s])";
		String errorLogFormat = "%s_Error: %s.%s - %s";
		String expectedStartMessage = String.format(startLogFormat, type, declaringTypeName, methodName, args);
		String expectedErrorLogMessage = String.format(errorLogFormat, type, declaringTypeName, methodName, errorMessage);
		boolean startLogFound = logsList.stream()
				.map(ILoggingEvent::getFormattedMessage)
				.anyMatch(event -> event.contains(expectedStartMessage));
		boolean errorLogFound = logsList.stream()
				.map(ILoggingEvent::getFormattedMessage)
				.anyMatch(event -> event.contains(expectedErrorLogMessage));
		assertThat(startLogFound).isTrue();
		assertThat(errorLogFound).isTrue();
		System.out.println(expectedStartMessage);
		System.out.println(errorLogFound);
	}
	
	@Test
	@DisplayName("[UT]serviceLogAround: 서비스 메소드 실행 시 로그 출력 - 성공 로그 출력")
	void test_serviceLogAround_successLog() throws Throwable {
		
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		
		MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
		Method method = Mockito.mock(Method.class);
		String methodName = "setAdultFlg";
		when(joinPoint.getSignature()).thenReturn(methodSignature);
		String declaringTypeName = "com.cjy.contenthub.detail.information.service.DetailInformationServiceImpl";
		when(methodSignature.getDeclaringTypeName())
		.thenReturn(declaringTypeName);
		when(methodSignature.getName()).thenReturn(methodName);
		when(methodSignature.getMethod()).thenReturn(method);
		
		Parameter[] parameters = new Parameter[0];
		when(method.getParameters()).thenReturn(parameters);
		Integer arg1 = 1000;
		String arg2 = "1101";
		when(joinPoint.getArgs()).thenReturn(new Object[] {arg1, arg2});
		
		DetailTvResponseDto resultFuture = Mockito.mock(DetailTvResponseDto.class);
		when(joinPoint.proceed()).thenReturn(resultFuture);

		// 로그 레벨 설정
		logger.setLevel(Level.DEBUG);
		
		// 실제 메소드 호출
		commonLoggingAspect.serviceLogAround(joinPoint);
		
		// 로그 메시지 검증
		List<ILoggingEvent> logsList = listAppender.list;
		String type = "Service";
		String[] arrays = {
				arg1.toString(),
				arg2
		};
		String args = String.join(", ", arrays);
		String startLogFormat = "%s_START: %s.%s([%s])";
		String endAsyncLogFormat = "%s_END: %s.%s([%s])";
		String expectedStartMessage = String.format(startLogFormat, type, declaringTypeName, methodName, args);
		String expectedEndMessage = String.format(endAsyncLogFormat, type, declaringTypeName, methodName, args);
		boolean startLogFound = logsList.stream()
				.filter(event -> event.getLevel().equals(Level.DEBUG))
				.map(ILoggingEvent::getFormattedMessage)
				.anyMatch(event -> event.contains(expectedStartMessage));
		boolean endLogFound = logsList.stream()
				.filter(event -> event.getLevel().equals(Level.DEBUG))
				.map(ILoggingEvent::getFormattedMessage)
				.anyMatch(event -> event.contains(expectedEndMessage));
		assertThat(startLogFound).isTrue();
		assertThat(endLogFound).isTrue();
		System.out.println(expectedStartMessage);
		System.out.println(expectedEndMessage);
	}
	
	@Test
	@DisplayName("[UT]serviceLogAround: 서비스 메소드 실행 시 로그 출력 - 로그 레벨이 DEBUG가 아닐 때")
	void test_serviceLogAround_notDebugLog() throws Throwable {
		
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);

		MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
		Method method = Mockito.mock(Method.class);
		String methodName = "getTvDetail";
		when(joinPoint.getSignature()).thenReturn(methodSignature);
		String declaringTypeName = "com.cjy.contenthub.detail.information.service.DetailInformationServiceImpl";
		when(methodSignature.getDeclaringTypeName()).thenReturn(declaringTypeName);
		when(methodSignature.getName()).thenReturn(methodName);
		when(methodSignature.getMethod()).thenReturn(method);

		Parameter[] parameters = new Parameter[0];
		when(method.getParameters()).thenReturn(parameters);
		Integer arg1 = 1000;
		String arg2 = "1101";
		when(joinPoint.getArgs()).thenReturn(new Object[] { arg1, arg2 });

		DetailTvResponseDto resultFuture = Mockito.mock(DetailTvResponseDto.class);
		when(joinPoint.proceed()).thenReturn(resultFuture);

		// 로그 레벨 설정
		logger.setLevel(Level.INFO);

		// 실제 메소드 호출
		commonLoggingAspect.serviceLogAround(joinPoint);

		// 로그 메시지 검증
		List<ILoggingEvent> logsList = listAppender.list;
		assertThat(logsList).isEmpty();
	}
	
	@Test
	@DisplayName("[UT]clientLogAround: 클라이언트 메소드 실행 시 로그 출력")
	void test_clientLogAround() throws Throwable {
		
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		
		MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
		Method method = Mockito.mock(Method.class);
		String methodName = "getNaverUserInfo";
		when(joinPoint.getSignature()).thenReturn(methodSignature);
		String declaringTypeName = "com.cjy.contenthub.login.client.LoginClient";
		when(methodSignature.getDeclaringTypeName())
		.thenReturn(declaringTypeName);
		when(methodSignature.getName()).thenReturn(methodName);
		when(methodSignature.getMethod()).thenReturn(method);
		
		Parameter[] parameters = new Parameter[0];
		when(method.getParameters()).thenReturn(parameters);
		String arg1 = "accessToken";
		String arg2 = "3600";
		when(joinPoint.getArgs()).thenReturn(new Object[] {arg1, arg2});
		
		Mono<?> resultFuture = Mockito.mock(Mono.class);
		when(joinPoint.proceed()).thenReturn(resultFuture);

		// 로그 레벨 설정
		logger.setLevel(Level.DEBUG);
		
		// 실제 메소드 호출
		commonLoggingAspect.clientLogAround(joinPoint);
		
		// 로그 메시지 검증
		List<ILoggingEvent> logsList = listAppender.list;
		String type = "Client";
		String[] arrays = {
				arg1,
				arg2
		};
		String args = String.join(", ", arrays);
		String startLogFormat = "%s_START: %s.%s([%s])";
		String endAsyncLogFormat = "%s_END: %s.%s([%s])";
		String expectedStartMessage = String.format(startLogFormat, type, declaringTypeName, methodName, args);
		String expectedEndMessage = String.format(endAsyncLogFormat, type, declaringTypeName, methodName, args);
		boolean startLogFound = logsList.stream()
				.map(ILoggingEvent::getFormattedMessage)
				.anyMatch(event -> event.contains(expectedStartMessage));
		boolean endLogFound = logsList.stream()
				.map(ILoggingEvent::getFormattedMessage)
				.anyMatch(event -> event.contains(expectedEndMessage));
		assertThat(startLogFound).isTrue();
		assertThat(endLogFound).isTrue();
		System.out.println(expectedStartMessage);
		System.out.println(expectedEndMessage);
	}
	
	@Test
	@DisplayName("[UT]helperLogAround: 헬퍼 메소드 실행 시 로그 출력")
	void test_helperLogAround() throws Throwable {
		
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		
		MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
		Method method = Mockito.mock(Method.class);
		String methodName = "updateUserStatus";
		when(joinPoint.getSignature()).thenReturn(methodSignature);
		String declaringTypeName = "com.cjy.contenthub.login.helper.LoginHelper";
		when(methodSignature.getDeclaringTypeName())
		.thenReturn(declaringTypeName);
		when(methodSignature.getName()).thenReturn(methodName);
		when(methodSignature.getMethod()).thenReturn(method);
		
		Parameter[] parameters = new Parameter[0];
		when(method.getParameters()).thenReturn(parameters);
		String arg1 = "1";
		String arg2 = "0";
		when(joinPoint.getArgs()).thenReturn(new Object[] {arg1, arg2});
		
		when(joinPoint.proceed()).thenReturn(null);
		
		// 로그 레벨 설정
		logger.setLevel(Level.DEBUG);
		
		// 실제 메소드 호출
		commonLoggingAspect.helperLogAround(joinPoint);
		
		// 로그 메시지 검증
		List<ILoggingEvent> logsList = listAppender.list;
		String type = "Helper";
		String[] arrays = {
				arg1,
				arg2
		};
		String args = String.join(", ", arrays);
		String startLogFormat = "%s_START: %s.%s([%s])";
		String endAsyncLogFormat = "%s_END: %s.%s([%s])";
		String expectedStartMessage = String.format(startLogFormat, type, declaringTypeName, methodName, args);
		String expectedEndMessage = String.format(endAsyncLogFormat, type, declaringTypeName, methodName, args);
		boolean startLogFound = logsList.stream()
				.map(ILoggingEvent::getFormattedMessage)
				.anyMatch(event -> event.contains(expectedStartMessage));
		boolean endLogFound = logsList.stream()
				.map(ILoggingEvent::getFormattedMessage)
				.anyMatch(event -> event.contains(expectedEndMessage));
		assertThat(startLogFound).isTrue();
		assertThat(endLogFound).isTrue();
		System.out.println(expectedStartMessage);
		System.out.println(expectedEndMessage);
	}
	
	@Test
	@DisplayName("[UT]repositoryLogAround: 레포지토리 메소드 실행 시 로그 출력")
	void test_repositoryLogAround() throws Throwable {
		
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		
		MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
		Method method = Mockito.mock(Method.class);
		String methodName = "findByProviderAndProviderId";
		when(joinPoint.getSignature()).thenReturn(methodSignature);
		String declaringTypeName = "com.cjy.contenthub.core.repository.UserRepository";
		when(methodSignature.getDeclaringTypeName())
		.thenReturn(declaringTypeName);
		when(methodSignature.getName()).thenReturn(methodName);
		when(methodSignature.getMethod()).thenReturn(method);
		
		Parameter[] parameters = new Parameter[0];
		when(method.getParameters()).thenReturn(parameters);
		String arg1 = "NAVER";
		String arg2 = "providerId";
		when(joinPoint.getArgs()).thenReturn(new Object[] {arg1, arg2});
		
		UserEntity userEntity = Mockito.mock(UserEntity.class);
		when(joinPoint.proceed()).thenReturn(userEntity);
		
		// 로그 레벨 설정
		logger.setLevel(Level.DEBUG);
		
		// 실제 메소드 호출
		commonLoggingAspect.repositoryLogAround(joinPoint);
		
		// 로그 메시지 검증
		List<ILoggingEvent> logsList = listAppender.list;
		String type = "Repository";
		String[] arrays = {
				arg1,
				arg2
		};
		String args = String.join(", ", arrays);
		String startLogFormat = "%s_START: %s.%s([%s])";
		String endAsyncLogFormat = "%s_END: %s.%s([%s])";
		String expectedStartMessage = String.format(startLogFormat, type, declaringTypeName, methodName, args);
		String expectedEndMessage = String.format(endAsyncLogFormat, type, declaringTypeName, methodName, args);
		boolean startLogFound = logsList.stream()
				.map(ILoggingEvent::getFormattedMessage)
				.anyMatch(event -> event.contains(expectedStartMessage));
		boolean endLogFound = logsList.stream()
				.map(ILoggingEvent::getFormattedMessage)
				.anyMatch(event -> event.contains(expectedEndMessage));
		assertThat(startLogFound).isTrue();
		assertThat(endLogFound).isTrue();
		System.out.println(expectedStartMessage);
		System.out.println(expectedEndMessage);
	}

}
