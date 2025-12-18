package com.cjy.contenthub.login.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.server.ResponseStatusException;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.integration.kakao.dto.KakaoAccountDto;
import com.cjy.contenthub.common.integration.kakao.dto.KakaoProfileDto;
import com.cjy.contenthub.common.integration.kakao.dto.KakaoUserInfoDto;
import com.cjy.contenthub.common.integration.naver.dto.NaverProfileDataDto;
import com.cjy.contenthub.common.integration.naver.dto.NaverProfileResultDto;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.core.constants.DomainEnum.LoginProviderEnum;
import com.cjy.contenthub.login.controller.dto.LoginUserInfoDto;
import com.cjy.contenthub.login.controller.dto.LoginUserResponseDto;
import com.cjy.contenthub.login.helper.LoginHelper;
import com.cjy.contenthub.login.mapper.LoginMapper;
import com.cjy.contenthub.login.service.LoginService;
import com.cjy.contenthub.login.service.dto.LoginUserServiceDto;

import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class LoginClientTest {
	
	LoginClient loginClient;
	
	@Mock
	LoginMapper mapper;

	@Mock
	MessageUtil messageUtil;
	
	@Mock
	LoginService service;

	@Mock
	LoginHelper helper;
	
	@Mock
	WebClient naverWebClient;
	
	@Mock
	WebClient kakaoWebClient;
	
	ExecutorService executorService;
	
	@Mock
	HttpServletRequest request;
	
	static final long NAVER_EXPIRES_IN = 365L;
	
	static final int KAKAO_EXPIRES_IN = 60;
	
	@BeforeEach
	void setUp() {
		executorService = Executors.newSingleThreadExecutor();
		loginClient = new LoginClient(
				mapper, 
				messageUtil, 
				service, 
				helper,
				naverWebClient,
				kakaoWebClient,
				executorService);
		ReflectionTestUtils.setField(loginClient, "naverUserInfoUrl", "https://openapi.naver.com/v1/nid/me");
		ReflectionTestUtils.setField(loginClient, "kakaoUserInfoUrl", "https://kapi.kakao.com/v2/user/me");
		ReflectionTestUtils.setField(loginClient, "naverExpiresIn", NAVER_EXPIRES_IN);
		ReflectionTestUtils.setField(loginClient, "kakaoExpiresIn", KAKAO_EXPIRES_IN);
	}
	
	@AfterEach
	void tearDown() {
		executorService.shutdown();
	}
	
	@Test
	@DisplayName("[UT]getNaverUserInfo: 네이버 유저 정보 조회 - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getNaverUserInfo_existResponse() throws InterruptedException, ExecutionException, TimeoutException {
		
		String accessToken = "accessToken";
		int expiresIn = 3600;
		String refreshToken = "refreshToken";
		String provider = LoginProviderEnum.NAVER.getProvider();
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(naverWebClient.get()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.header(HttpHeaders.AUTHORIZATION, CommonConstants.AUTHORIZATION_HEADER_PREFIX.concat(accessToken))).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		
		// Mock response 데이터 설정
		NaverProfileResultDto response = new NaverProfileResultDto();
		NaverProfileDataDto profile = new NaverProfileDataDto();
		profile.setId("naverId");
		profile.setNickname("nickname");
		profile.setName("name");
		response.setResponse(profile);
		response.setResultcode("00");
		response.setMessage("success");
        when(responseSpec.bodyToMono(NaverProfileResultDto.class)).thenReturn(Mono.just(response));
        
        LoginUserServiceDto userServiceDto = LoginUserServiceDto.builder()
        		                .provider(provider)
        		                .providerId(profile.getId())
        		                .nickname(profile.getNickname())
        		                .name(profile.getName())
        		                .build();
        when(mapper.profileDataDtoToUserServiceDto(profile)).thenReturn(userServiceDto);
        when(service.saveUser(userServiceDto)).thenReturn(userServiceDto);
		LoginUserInfoDto userInfo = LoginUserInfoDto.builder()
				.provider(provider)
				.id(profile.getId())
				.nickname(profile.getNickname())
				.name(profile.getName())
				.build();
		String jwt = "jwt";
		String expireDateStr = "2024-12-31T23:59:59Z";
		when(mapper.profileDataDtoToProfileDataDto(profile)).thenReturn(userInfo);
		when(helper.createJwt(expiresIn, profile.getId(), provider, userServiceDto))
		.thenReturn(new LoginHelper.JwtCreationRecord(jwt, expireDateStr));
		when(helper.setLoginCookies(request, refreshToken, provider, profile.getId(), expiresIn))
		.thenReturn(new String[] { "refreshTokenCookie", "providerCookie" });
		
		// 실제 테스트 실행
		ResponseEntity<LoginUserResponseDto> result = 
				loginClient.getNaverUserInfo(request, accessToken, expiresIn, refreshToken).get(5, TimeUnit.SECONDS);
		
		// response body 검증
		LoginUserResponseDto expectedResult = LoginUserResponseDto.builder()
				.resultcode(response.getResultcode())
				.message(response.getMessage())
				.userInfo(userInfo)
				.accessToken(accessToken)
				.jwt(jwt)
				.expireDate(expireDateStr)
				.build();
		assertThat(result.getBody()).usingRecursiveComparison().isEqualTo(expectedResult);
		
		// response header 검증(리프레시 토큰 쿠키 존재)
		result.getHeaders().forEach((key, value) -> {
			if (key.equals(HttpHeaders.SET_COOKIE)) {
				assertThat(value).containsExactlyInAnyOrder("refreshTokenCookie", "providerCookie");
			}
		});
		
		verify(naverWebClient, times(1)).get();
		verify(mapper, times(1)).profileDataDtoToUserServiceDto(profile);
		verify(service, times(1)).saveUser(userServiceDto);
		verify(mapper, times(1)).profileDataDtoToProfileDataDto(profile);
	}
	
	@Test
	@DisplayName("[UT]getNaverUserInfo: 네이버 유저 정보 조회 - 응답 데이터 에러코드 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getNaverUserInfo_errorAndExistErrorCode() {
		
		String accessToken = "accessToken";
		int expiresIn = 3600;
		String refreshToken = "refreshToken";
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(naverWebClient.get()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.header(HttpHeaders.AUTHORIZATION, CommonConstants.AUTHORIZATION_HEADER_PREFIX.concat(accessToken))).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		
		// Mock response 데이터 설정
		String resultCode = "024";
		String massage = "Authentication failure / 인증에 실패했습니다.";
		NaverProfileResultDto response = new NaverProfileResultDto();
		NaverProfileDataDto profile = new NaverProfileDataDto();
		profile.setId("naverId");
		profile.setNickname("nickname");
		profile.setName("name");
		response.setResponse(profile);
		response.setResultcode(resultCode);
		response.setMessage(massage);
        when(responseSpec.bodyToMono(NaverProfileResultDto.class)).thenReturn(Mono.just(response));
        
		// 실제 테스트 실행 및 예외 검증
        assertThatThrownBy(() -> loginClient.getNaverUserInfo(request, accessToken, expiresIn, refreshToken)
        		.get(5, TimeUnit.SECONDS))
        .isInstanceOf(ExecutionException.class)
        .hasCauseInstanceOf(ResponseStatusException.class)
        .hasMessageContaining(massage)
        .satisfies(ex -> {
        	ResponseStatusException realEx = (ResponseStatusException) ex.getCause();
        	assertThat(realEx.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        });

		verify(naverWebClient, times(1)).get();
	}
	
	@Test
	@DisplayName("[UT]getNaverUserInfo: 네이버 유저 정보 조회 - 응답 데이터 존재(리프레시 토큰 없음)")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getNaverUserInfo_existResponseAndNoRefreshToken() throws InterruptedException, ExecutionException, TimeoutException {
		
		String accessToken = "accessToken";
		int expiresIn = 3600;
		String refreshToken = "";
		String provider = LoginProviderEnum.NAVER.getProvider();
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(naverWebClient.get()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.header(HttpHeaders.AUTHORIZATION, CommonConstants.AUTHORIZATION_HEADER_PREFIX.concat(accessToken))).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		
		// Mock response 데이터 설정
		NaverProfileResultDto response = new NaverProfileResultDto();
		NaverProfileDataDto profile = new NaverProfileDataDto();
		profile.setId("naverId");
		profile.setNickname("nickname");
		profile.setName("name");
		response.setResponse(profile);
		response.setResultcode("00");
		response.setMessage("success");
        when(responseSpec.bodyToMono(NaverProfileResultDto.class)).thenReturn(Mono.just(response));
        
        LoginUserServiceDto userServiceDto = LoginUserServiceDto.builder()
        		                .provider(provider)
        		                .providerId(profile.getId())
        		                .nickname(profile.getNickname())
        		                .name(profile.getName())
        		                .build();
        when(mapper.profileDataDtoToUserServiceDto(profile)).thenReturn(userServiceDto);
        when(service.saveUser(userServiceDto)).thenReturn(userServiceDto);
        String jwt = "jwt";
        String expireDateStr = "2024-12-31T23:59:59Z";
        when(helper.createJwt(expiresIn, profile.getId(), provider, userServiceDto))
                .thenReturn(new LoginHelper.JwtCreationRecord(jwt, expireDateStr));
		LoginUserInfoDto userInfo = LoginUserInfoDto.builder()
				.provider(provider)
				.id(profile.getId())
				.nickname(profile.getNickname())
				.name(profile.getName())
				.build();
		when(mapper.profileDataDtoToProfileDataDto(profile)).thenReturn(userInfo);
		
		// 실제 테스트 실행
		ResponseEntity<LoginUserResponseDto> result = 
				loginClient.getNaverUserInfo(request, accessToken, expiresIn, refreshToken).get(5, TimeUnit.SECONDS);
		
		// response body 검증
		LoginUserResponseDto expectedResult = LoginUserResponseDto.builder()
				.resultcode(response.getResultcode())
				.message(response.getMessage())
				.userInfo(userInfo)
				.accessToken(accessToken)
				.jwt(jwt)
				.expireDate(expireDateStr) // 밀리 초 단위이므로 직접 비교 불가
				.build();
		assertThat(result.getBody()).usingRecursiveComparison().isEqualTo(expectedResult);
		
		// response header 검증(리프레시 토큰 쿠키 없음)
		result.getHeaders().forEach((key, value) -> {
			if (key.equals(HttpHeaders.SET_COOKIE)) {
				assertThat(value).isNullOrEmpty();
			}
		});
		
		verify(naverWebClient, times(1)).get();
		verify(mapper, times(1)).profileDataDtoToUserServiceDto(profile);
		verify(service, times(1)).saveUser(userServiceDto);
		verify(mapper, times(1)).profileDataDtoToProfileDataDto(profile);
	}
	
	@Test
	@DisplayName("[UT]getKakaoUserInfo: 카카오 유저 정보 조회 - 응답 데이터 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getKakaoUserInfo_existResponse() throws InterruptedException, ExecutionException, TimeoutException {
		
		String accessToken = "accessToken";
		int expiresIn = 3600;
		String refreshToken = "refreshToken";
		String provider = LoginProviderEnum.KAKAO.getProvider();
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(kakaoWebClient.get()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.header(HttpHeaders.AUTHORIZATION, CommonConstants.AUTHORIZATION_HEADER_PREFIX.concat(accessToken))).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		
		// Mock response 데이터 설정
		long providerId = 123456789L;
		KakaoUserInfoDto response = new KakaoUserInfoDto();
		response.setId(providerId);
		KakaoAccountDto account = new KakaoAccountDto();
		account.setName("name");
		KakaoProfileDto profile = new KakaoProfileDto();
		profile.setNickname("nickname");
		account.setProfile(profile);
		response.setKakaoAccount(account);
        when(responseSpec.bodyToMono(KakaoUserInfoDto.class)).thenReturn(Mono.just(response));
        
		LoginUserServiceDto userServiceDto = LoginUserServiceDto.builder()
				.provider(provider)
				.providerId(response.getId().toString())
				.nickname(profile.getNickname())
				.build();
		LoginUserServiceDto userServiceResultDto = 
				LoginUserServiceDto.builder()
				.userId(1L)
				.provider(userServiceDto.getProvider())
				.providerId(userServiceDto.getProviderId())
				.nickname(userServiceDto.getNickname())
				.build();
		when(service.saveUser(userServiceDto)).thenReturn(userServiceResultDto);
		String jwt = "jwt";
		String expireDateStr = "2024-12-31T23:59:59Z";
		when(helper.createJwt(expiresIn, String.valueOf(response.getId()), provider, userServiceDto))
		.thenReturn(new LoginHelper.JwtCreationRecord(jwt, expireDateStr));
		when(helper.setLoginCookies(request, refreshToken, provider, String.valueOf(response.getId()), expiresIn))
		.thenReturn(new String[] { "refreshTokenCookie", "providerCookie" });
		
		// 실제 테스트 실행
		ResponseEntity<LoginUserResponseDto> result = loginClient.getKakaoUserInfo(request, accessToken, expiresIn, refreshToken)
				.get(5, TimeUnit.SECONDS);
		
		// response body 검증
		LoginUserInfoDto userInfo = LoginUserInfoDto.builder()
				.userId(userServiceResultDto.getUserId())
				.provider(provider)
				.id(String.valueOf(providerId))
				.nickname(profile.getNickname())
				.build();
		LoginUserResponseDto userResponse = LoginUserResponseDto.builder()
				.resultcode("00")
				.message("")
				.userInfo(userInfo)
				.accessToken(accessToken)
				.jwt(jwt)
				.expireDate(expireDateStr)
				.build();
		assertThat(result.getBody()).usingRecursiveComparison().isEqualTo(userResponse);
		
		// response header 검증(리프레시 토큰 쿠키 존재)
		result.getHeaders().forEach((key, value) -> {
			if (key.equals(HttpHeaders.SET_COOKIE)) {
				assertThat(value).containsExactlyInAnyOrder("refreshTokenCookie", "providerCookie");
			}
		});
		
		verify(kakaoWebClient, times(1)).get();
		verify(service, times(1)).saveUser(userServiceDto);
		verify(helper, times(1)).createJwt(expiresIn, String.valueOf(response.getId()), provider, userServiceDto);
		verify(helper, times(1)).setLoginCookies(request, refreshToken, provider, String.valueOf(response.getId()), expiresIn);
	}
	
	@Test
	@DisplayName("[UT]getKakaoUserInfo: 카카오 유저 정보 조회 - 계정 정보 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getKakaoUserInfo_notExistAccount() {
		
		String accessToken = "accessToken";
		int expiresIn = 3600;
		String refreshToken = "refreshToken";
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(kakaoWebClient.get()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.header(HttpHeaders.AUTHORIZATION, CommonConstants.AUTHORIZATION_HEADER_PREFIX.concat(accessToken))).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		
		KakaoUserInfoDto response = new KakaoUserInfoDto();
        when(responseSpec.bodyToMono(KakaoUserInfoDto.class)).thenReturn(Mono.just(response));
		
        String errorMessage = "프로필 정보를 찾을 수 없습니다.";	
        when(messageUtil.getMessageKO(anyString())).thenReturn(errorMessage);
        
        // 실제 테스트 실행 및 예외 검증
        assertThatThrownBy(() -> loginClient.getKakaoUserInfo(request, accessToken, expiresIn, refreshToken)
        		.get(5, TimeUnit.SECONDS))
        .isInstanceOf(ExecutionException.class)
        .hasCauseInstanceOf(ResponseStatusException.class)
        .hasMessageContaining(errorMessage)
				.satisfies(ex -> {
					ResponseStatusException realEx = (ResponseStatusException) ex.getCause();
					assertThat(realEx.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
				});
        
        verify(kakaoWebClient, times(1)).get();
        verify(messageUtil, times(1)).getMessageKO(anyString());
	}
	
	@Test
	@DisplayName("[UT]getKakaoUserInfo: 카카오 유저 정보 조회 - 프로필 정보 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getKakaoUserInfo_notExistProfile() {
		
		String accessToken = "accessToken";
		int expiresIn = 3600;
		String refreshToken = "refreshToken";
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(kakaoWebClient.get()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.header(HttpHeaders.AUTHORIZATION, CommonConstants.AUTHORIZATION_HEADER_PREFIX.concat(accessToken))).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		
		KakaoUserInfoDto response = new KakaoUserInfoDto();
		KakaoAccountDto account = new KakaoAccountDto();
		response.setKakaoAccount(account);
        when(responseSpec.bodyToMono(KakaoUserInfoDto.class)).thenReturn(Mono.just(response));
		
        String errorMessage = "프로필 정보를 찾을 수 없습니다.";	
        when(messageUtil.getMessageKO(anyString())).thenReturn(errorMessage);
        
        // 실제 테스트 실행 및 예외 검증
        assertThatThrownBy(() -> loginClient.getKakaoUserInfo(request, accessToken, expiresIn, refreshToken)
        		.get(5, TimeUnit.SECONDS))
        .isInstanceOf(ExecutionException.class)
        .hasCauseInstanceOf(ResponseStatusException.class)
        .hasMessageContaining(errorMessage)
				.satisfies(ex -> {
					ResponseStatusException realEx = (ResponseStatusException) ex.getCause();
					assertThat(realEx.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
				});
        
        verify(kakaoWebClient, times(1)).get();
        verify(messageUtil, times(1)).getMessageKO(anyString());
	}
	
	@Test
	@DisplayName("[UT]getKakaoUserInfo: 카카오 유저 정보 조회 - 응답 데이터 존재(리프레시 토큰 없음)")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getKakaoUserInfo_existResponseAndNoRefreshToken() throws InterruptedException, ExecutionException, TimeoutException {
		
		String accessToken = "accessToken";
		int expiresIn = 3600;
		String refreshToken = "";
		String provider = LoginProviderEnum.KAKAO.getProvider();
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(kakaoWebClient.get()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.header(HttpHeaders.AUTHORIZATION, CommonConstants.AUTHORIZATION_HEADER_PREFIX.concat(accessToken))).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		
		// Mock response 데이터 설정
		long providerId = 123456789L;
		KakaoUserInfoDto response = new KakaoUserInfoDto();
		response.setId(providerId);
		KakaoAccountDto account = new KakaoAccountDto();
		account.setName("name");
		KakaoProfileDto profile = new KakaoProfileDto();
		profile.setNickname("nickname");
		account.setProfile(profile);
		response.setKakaoAccount(account);
        when(responseSpec.bodyToMono(KakaoUserInfoDto.class)).thenReturn(Mono.just(response));
        
		LoginUserServiceDto userServiceDto = LoginUserServiceDto.builder()
				.provider(provider)
				.providerId(response.getId().toString())
				.nickname(profile.getNickname())
				.build();
		LoginUserServiceDto userServiceResultDto = 
				LoginUserServiceDto.builder()
				.userId(1L)
				.provider(userServiceDto.getProvider())
				.providerId(userServiceDto.getProviderId())
				.nickname(userServiceDto.getNickname())
				.build();
		when(service.saveUser(userServiceDto)).thenReturn(userServiceResultDto);
		String jwt = "jwt";
		String expireDateStr = "2024-12-31T23:59:59Z";
		when(helper.createJwt(expiresIn, String.valueOf(response.getId()), provider, userServiceDto))
		.thenReturn(new LoginHelper.JwtCreationRecord(jwt, expireDateStr));
		
		// 실제 테스트 실행
		ResponseEntity<LoginUserResponseDto> result = loginClient.getKakaoUserInfo(request, accessToken, expiresIn, refreshToken)
				.get(5, TimeUnit.SECONDS);
		
		// response body 검증
		LoginUserInfoDto userInfo = LoginUserInfoDto.builder()
				.userId(userServiceResultDto.getUserId())
				.provider(provider)
				.id(String.valueOf(providerId))
				.nickname(profile.getNickname())
				.build();
		LoginUserResponseDto userResponse = LoginUserResponseDto.builder()
				.resultcode("00")
				.message("")
				.userInfo(userInfo)
				.accessToken(accessToken)
				.jwt(jwt)
				.expireDate(expireDateStr)
				.build();
		assertThat(result.getBody()).usingRecursiveComparison().isEqualTo(userResponse);
		
		// response header 검증(리프레시 토큰 쿠키 없음)
		result.getHeaders().forEach((key, value) -> {
			if (key.equals(HttpHeaders.SET_COOKIE)) {
				assertThat(value).isNullOrEmpty();
			}
		});
		
		verify(kakaoWebClient, times(1)).get();
		verify(service, times(1)).saveUser(userServiceDto);
		verify(helper, times(1)).createJwt(expiresIn, String.valueOf(response.getId()), provider, userServiceDto);
	}

}
