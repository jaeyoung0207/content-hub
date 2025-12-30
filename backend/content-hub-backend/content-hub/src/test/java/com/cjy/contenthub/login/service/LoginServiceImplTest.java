package com.cjy.contenthub.login.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.integration.kakao.dto.KakaoIssueTokenDto;
import com.cjy.contenthub.common.integration.kakao.dto.KakaoUserInfoDto;
import com.cjy.contenthub.common.integration.naver.dto.NaverDeleteTokenDto;
import com.cjy.contenthub.common.integration.naver.dto.NaverIssueTokenDto;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.common.util.RedisUtil;
import com.cjy.contenthub.common.util.RedisUtil.ProviderInfo;
import com.cjy.contenthub.core.constants.DomainEnum.LoginProviderEnum;
import com.cjy.contenthub.core.constants.DomainEnum.LoginStatusEnum;
import com.cjy.contenthub.core.repository.UserRepository;
import com.cjy.contenthub.core.repository.entity.UserEntity;
import com.cjy.contenthub.login.helper.LoginHelper;
import com.cjy.contenthub.login.mapper.LoginMapper;
import com.cjy.contenthub.login.service.dto.LoginUserServiceDto;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {
	
	LoginServiceImpl service;
	
	@Mock
	UserRepository userRepository;
	
	@Mock
	LoginHelper loginHelper;
	
	@Mock
	LoginMapper loginMapper;
	
	@Mock
	RedisUtil redisUtil;
	
	@Mock
	MessageUtil messageUtil;
	
	@Mock
	WebClient naverWebClient;

	@Mock
	WebClient kakaoWebClient;

	String naverClientId;

	String naverClientSecret;
	
	String kakaoClientSecret;

	String naverTokenIssueUrl;

	String kakaoOidcUserInfoUrl;

	String kakaoTokenIssueUrl;

	String kakaoUserInfoUrl;

	String kakaoLogoutUrl;
	
	@BeforeEach
	void setUp() {
		service = new LoginServiceImpl(
				userRepository, 
				loginHelper, 
				loginMapper, 
				redisUtil, 
				messageUtil, 
				naverWebClient,
				kakaoWebClient);
		ReflectionTestUtils.setField(service, "naverClientId", "testNaverClientId");
		ReflectionTestUtils.setField(service, "naverClientSecret", "testNaverClientSecret");
		ReflectionTestUtils.setField(service, "kakaoClientSecret", "testKakaoClientSecret");
		ReflectionTestUtils.setField(service, "naverTokenIssueUrl", "https://nid.naver.com/oauth2.0/token");
		ReflectionTestUtils.setField(service, "kakaoOidcUserInfoUrl", "https://kapi.kakao.com/v2/user/me");
		ReflectionTestUtils.setField(service, "kakaoTokenIssueUrl", "https://kauth.kakao.com/oauth/token");
		ReflectionTestUtils.setField(service, "kakaoUserInfoUrl", "https://kapi.kakao.com/v2/user/me");
		ReflectionTestUtils.setField(service, "kakaoLogoutUrl", "https://kapi.kakao.com/v1/user/logout");
	}
	
	@Test
	@DisplayName("[UT]saveUser: 로그인 유저 정보 저장 - 유저 정보 없음")
	void test_saveUser_notExistUserInfo() {
		
		String provider = "NAVER";
		String providerId = "providerId";
		long userId = 1L;
		LoginUserServiceDto loginUserServiceDto = LoginUserServiceDto.builder()
				.provider(provider)
				.providerId(providerId)
                .userId(userId)
                .build();
		UserEntity userEntity = UserEntity.builder()
				.userId(userId)
				.provider(provider)
				.providerId(providerId)
				.build();

		when(userRepository.findByProviderAndProviderId(anyString(), anyString()))
				.thenReturn(null);
		when(loginMapper.userServiceDtoToUserEntity(any(LoginUserServiceDto.class))).thenReturn(userEntity);
		when(userRepository.save(any())).thenReturn(userEntity);
		when(loginMapper.userEntityToUserServiceDto(any(UserEntity.class)))
		.thenReturn(loginUserServiceDto);
		
		// 실제 메서드 호출
		LoginUserServiceDto result = service.saveUser(loginUserServiceDto);
		
		// 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(loginUserServiceDto);
		
		verify(userRepository, times(1)).findByProviderAndProviderId(provider, providerId);
		verify(loginMapper, times(1)).userServiceDtoToUserEntity(loginUserServiceDto);
		verify(userRepository, times(1)).save(userEntity);
		verify(loginMapper, times(1)).userEntityToUserServiceDto(userEntity);
	}
	
	@Test
	@DisplayName("[UT]saveUser: 로그인 유저 정보 저장 - 유저 정보 존재")
	void test_saveUser_existUserInfo() {
		
		String provider = "NAVER";
		String providerId = "providerId";
		long userId = 1L;
		LoginUserServiceDto loginUserServiceDto = LoginUserServiceDto.builder()
				.provider(provider)
				.providerId(providerId)
                .userId(userId)
                .build();
		UserEntity userinfo = UserEntity.builder()
				.userId(userId)
				.provider(provider)
				.providerId(providerId)
				.build();

		when(userRepository.findByProviderAndProviderId(anyString(), anyString()))
				.thenReturn(userinfo);
		doNothing().when(loginHelper).updateUserStatus(loginUserServiceDto.getUserId(), LoginStatusEnum.LOGIN.getLoginStatus());
		when(loginMapper.userEntityToUserServiceDto(any(UserEntity.class)))
		.thenReturn(loginUserServiceDto);
		
		// 실제 메서드 호출
		LoginUserServiceDto result = service.saveUser(loginUserServiceDto);
		
		// 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(loginUserServiceDto);
		
		verify(userRepository, times(1)).findByProviderAndProviderId(provider, providerId);
		verify(loginHelper, times(1)).updateUserStatus(loginUserServiceDto.getUserId(), LoginStatusEnum.LOGIN.getLoginStatus());
		verify(loginMapper, times(1)).userEntityToUserServiceDto(userinfo);
	}
	
	@Test
	@DisplayName("[UT]getNaverIssueToken: 네이버 로그인 토큰 발행")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getNaverIssueToken() {
		
		String code = "code";
		String state = "state";
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(naverWebClient.get()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		NaverIssueTokenDto issueTokenDto = new NaverIssueTokenDto();
		issueTokenDto.setAccessToken("accessToken");
		when(responseSpec.bodyToMono(NaverIssueTokenDto.class)).thenReturn(Mono.just(issueTokenDto));
		
		// 실제 메서드 호출
		NaverIssueTokenDto result = service.getNaverIssueToken(code, state);
		
		// 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(issueTokenDto);
		
		verify(naverWebClient, times(1)).get();
	}
	
	@Test
	@DisplayName("[UT]getNaverUpdateToken: 네이버 로그인 토큰 갱신 - 제공자 정보 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_getNaverUpdateToken_existProviderInfo() {
		
		String refreshToken = "refreshToken";
		String deviceId = "deviceId";
		String provider = "NAVER";
		String providerId = "providerId";
		
		when(redisUtil.getProviderInfo(refreshToken, deviceId)).thenReturn(new ProviderInfo(provider, providerId));
		when(redisUtil.validateRefreshToken(provider, providerId, refreshToken, deviceId)).thenReturn(true);
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(naverWebClient.get()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		NaverIssueTokenDto issueTokenDto = new NaverIssueTokenDto();
		issueTokenDto.setAccessToken("accessToken");
		when(responseSpec.bodyToMono(NaverIssueTokenDto.class)).thenReturn(Mono.just(issueTokenDto));
		
		// 실제 메서드 호출
		NaverIssueTokenDto result = service.getNaverUpdateToken(refreshToken, deviceId);
		
		// 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(issueTokenDto);
		
		verify(redisUtil, times(1)).getProviderInfo(refreshToken, deviceId);
		verify(redisUtil, times(1)).validateRefreshToken(provider, providerId, refreshToken, deviceId);
		verify(naverWebClient, times(1)).get();
	}
	
	@Test
	@DisplayName("[UT]getNaverUpdateToken: 네이버 로그인 토큰 갱신 - 제공자 정보 없음")
	void test_getNaverUpdateToken_notExistProviderInfo() {
		
		String refreshToken = "refreshToken";
		String deviceId = "deviceId";
		
		when(redisUtil.getProviderInfo(refreshToken, deviceId)).thenReturn(null);
		String errorMessage = "리프레시 토큰 검증 에러";
		when(messageUtil.getMessageKO(anyString())).thenReturn(errorMessage);
		
		// 실제 메서드 호출 및 예외 검증
		assertThatThrownBy(() -> 
            service.getNaverUpdateToken(refreshToken, deviceId))
		.isInstanceOf(AccountExpiredException.class)
		.hasMessageContaining(errorMessage);
		
		verify(redisUtil, times(1)).getProviderInfo(refreshToken, deviceId);
	}
	
	@Test
	@DisplayName("[UT]getNaverUpdateToken: 네이버 로그인 토큰 갱신 - 리프레시 토큰 검증 에러")
	void test_getNaverUpdateToken_validateError() {
		
		String refreshToken = "refreshToken";
		String deviceId = "deviceId";
		String provider = "NAVER";
		String providerId = "providerId";
		
		when(redisUtil.getProviderInfo(refreshToken, deviceId)).thenReturn(new ProviderInfo(provider, providerId));
		when(redisUtil.validateRefreshToken(provider, providerId, refreshToken, deviceId)).thenReturn(false);
		String errorMessage = "리프레시 토큰 검증 에러";
		when(messageUtil.getMessageKO(anyString())).thenReturn(errorMessage);
		
		// 실제 메서드 호출 및 예외 검증
		assertThatThrownBy(() -> 
            service.getNaverUpdateToken(refreshToken, deviceId))
		.isInstanceOf(AccountExpiredException.class)
		.hasMessageContaining(errorMessage);
		
		verify(redisUtil, times(1)).getProviderInfo(refreshToken, deviceId);
		verify(redisUtil, times(1)).validateRefreshToken(provider, providerId, refreshToken, deviceId);
	}
	
	@Test
	@DisplayName("[UT]deleteNaverToken: 네이버 로그인 토큰 삭제 - 리프레시 토큰 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_deleteNaverToken_existRefreshToken() {
		
		String accessToken = "accessToken";
		String targetId = "targetId";
		Long userId = 1L;
		String refreshToken = "refreshToken";
		String deviceId = "deviceId";
		
		doNothing().when(redisUtil).deleteRefreshToken(LoginProviderEnum.NAVER.getProvider(), targetId, deviceId);
		doNothing().when(redisUtil).deleteProviderInfo(refreshToken, deviceId);
		
		doNothing().when(loginHelper).updateUserStatus(userId, LoginStatusEnum.LOGOUT.getLoginStatus());
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(naverWebClient.get()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		NaverDeleteTokenDto deleteTokenDto = new NaverDeleteTokenDto();
		deleteTokenDto.setAccessToken("accessToken");
		when(responseSpec.bodyToMono(NaverDeleteTokenDto.class)).thenReturn(Mono.just(deleteTokenDto));
		
		// 실제 메서드 호출
		NaverDeleteTokenDto result = service.deleteNaverToken(accessToken, targetId, userId, refreshToken, deviceId)
				;
		
		// 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(deleteTokenDto);
		
		verify(redisUtil, times(1)).deleteRefreshToken(LoginProviderEnum.NAVER.getProvider(), targetId, deviceId);
		verify(redisUtil, times(1)).deleteProviderInfo(refreshToken, deviceId);
		verify(naverWebClient, times(1)).get();
	}
	
	@Test
	@DisplayName("[UT]deleteNaverToken: 네이버 로그인 토큰 삭제 - 리프레시 토큰 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_deleteNaverTokDen_notExistRefreshToken() {
		
		String accessToken = "accessToken";
		String targetId = "targetId";
		Long userId = 1L;
		String refreshToken = "";
		String deviceId = "deviceId";
		
		doNothing().when(loginHelper).updateUserStatus(userId, LoginStatusEnum.LOGOUT.getLoginStatus());
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(naverWebClient.get()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		NaverDeleteTokenDto deleteTokenDto = new NaverDeleteTokenDto();
		deleteTokenDto.setAccessToken("accessToken");
		when(responseSpec.bodyToMono(NaverDeleteTokenDto.class)).thenReturn(Mono.just(deleteTokenDto));
		
		// 실제 메서드 호출
		NaverDeleteTokenDto result = service.deleteNaverToken(accessToken, targetId, userId, refreshToken, deviceId)
				;
		
		// 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(deleteTokenDto);
		
		verify(naverWebClient, times(1)).get();
	}
	
	
	@Test
	@DisplayName("[UT]getKakaoIssueToken: 카카오 로그인 토큰 발행")
	void test_getKakaoIssueToken() {
		
		String clientId = "clientId";
		String redirectUri = "http://localhost:3000/login/kakao";
		String code = "code";
		
		// WebClient Mock 설정
		RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
		when(kakaoWebClient.post()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		KakaoIssueTokenDto issueTokenDto = new KakaoIssueTokenDto();
		issueTokenDto.setAccessToken("accessToken");
		when(responseSpec.bodyToMono(KakaoIssueTokenDto.class)).thenReturn(Mono.just(issueTokenDto));
		
		// 실제 메서드 호출
		KakaoIssueTokenDto result = service.getKakaoIssueToken(clientId, redirectUri, code);
		
		// 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(issueTokenDto);
		
		verify(kakaoWebClient, times(1)).post();
	}
	
	@Test
	@DisplayName("[UT]updateKakaoLoginInfo: 카카오 로그인 토큰 갱신 - 제공자 정보 존재")
	void test_updateKakaoLoginInfo_existProviderInfo() {
		
		String clientId = "clientId";
		String refreshToken = "refreshToken";
		String deviceId = "deviceId";
		String provider = "KAKAO";
		String providerId = "providerId";
		
		when(redisUtil.getProviderInfo(refreshToken, deviceId)).thenReturn(new ProviderInfo(provider, providerId));
		when(redisUtil.validateRefreshToken(provider, providerId, refreshToken, deviceId)).thenReturn(true);
		
		// WebClient Mock 설정
		RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
		when(kakaoWebClient.post()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		KakaoIssueTokenDto issueTokenDto = new KakaoIssueTokenDto();
		issueTokenDto.setAccessToken("accessToken");
		when(responseSpec.bodyToMono(KakaoIssueTokenDto.class)).thenReturn(Mono.just(issueTokenDto));
		
		// 실제 메서드 호출
		KakaoIssueTokenDto result = service.updateKakaoLoginInfo(clientId, refreshToken, deviceId);
		
		// 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(issueTokenDto);
		
		verify(redisUtil, times(1)).getProviderInfo(refreshToken, deviceId);
		verify(redisUtil, times(1)).validateRefreshToken(provider, providerId, refreshToken, deviceId);
		verify(kakaoWebClient, times(1)).post();
	}
	
	@Test
	@DisplayName("[UT]updateKakaoLoginInfo: 카카오 로그인 토큰 갱신 - 제공자 정보 없음")
	void test_updateKakaoLoginInfo_notExistProviderInfo() {
		
		String clientId = "clientId";
		String refreshToken = "refreshToken";
		String deviceId = "deviceId";
		
		when(redisUtil.getProviderInfo(refreshToken, deviceId)).thenReturn(null);
		String errorMessage = "리프레시 토큰 검증 에러";
		when(messageUtil.getMessageKO(anyString())).thenReturn(errorMessage);
		
		// 실제 메서드 호출 및 예외 검증
		assertThatThrownBy(() -> 
            service.updateKakaoLoginInfo(clientId, refreshToken, deviceId))
		.isInstanceOf(AccountExpiredException.class)
		.hasMessageContaining(errorMessage);
		
		verify(redisUtil, times(1)).getProviderInfo(refreshToken, deviceId);
	}
	
	@Test
	@DisplayName("[UT]updateKakaoLoginInfo: 네이버 로그인 토큰 갱신 - 리프레시 토큰 검증 에러")
	void test_updateKakaoLoginInfo_validateError() {
		
		String clientId = "clientId";
		String refreshToken = "refreshToken";
		String deviceId = "deviceId";
		String provider = "NAVER";
		String providerId = "providerId";
		
		when(redisUtil.getProviderInfo(refreshToken, deviceId)).thenReturn(new ProviderInfo(provider, providerId));
		when(redisUtil.validateRefreshToken(provider, providerId, refreshToken, deviceId)).thenReturn(false);
		String errorMessage = "리프레시 토큰 검증 에러";
		when(messageUtil.getMessageKO(anyString())).thenReturn(errorMessage);
		
		// 실제 메서드 호출 및 예외 검증
		assertThatThrownBy(() -> 
            service.updateKakaoLoginInfo(clientId, refreshToken, deviceId))
		.isInstanceOf(AccountExpiredException.class)
		.hasMessageContaining(errorMessage);
		
		verify(redisUtil, times(1)).getProviderInfo(refreshToken, deviceId);
		verify(redisUtil, times(1)).validateRefreshToken(provider, providerId, refreshToken, deviceId);
	}
	
	@Test
	@DisplayName("[UT]deleteKakaoToken: 카카오 로그인 토큰 삭제 - 리프레시 토큰 존재")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_deleteKakaoToken_existRefreshToken() {
		
		String accessToken = "accessToken";
		String targetId = "12345";
		Long userId = 1L;
		String refreshToken = "refreshToken";
		String deviceId = "deviceId";
		
		doNothing().when(redisUtil).deleteRefreshToken(LoginProviderEnum.KAKAO.getProvider(), targetId, deviceId);
		doNothing().when(redisUtil).deleteProviderInfo(refreshToken, deviceId);
		
		doNothing().when(loginHelper).updateUserStatus(userId, LoginStatusEnum.LOGOUT.getLoginStatus());
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(kakaoWebClient.get()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.header(HttpHeaders.AUTHORIZATION, 
						CommonConstants.AUTHORIZATION_HEADER_PREFIX.concat(accessToken))).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		KakaoUserInfoDto userInfo = new KakaoUserInfoDto();
		userInfo.setId(1234567890L);
		when(responseSpec.bodyToMono(KakaoUserInfoDto.class)).thenReturn(Mono.just(userInfo));
		
		// 실제 메서드 호출
		KakaoUserInfoDto result = service.deleteKakaoToken(accessToken, targetId, userId, refreshToken, deviceId);
		
		// 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(userInfo);
		
		verify(redisUtil, times(1)).deleteRefreshToken(LoginProviderEnum.KAKAO.getProvider(), targetId, deviceId);
		verify(redisUtil, times(1)).deleteProviderInfo(refreshToken, deviceId);
		verify(kakaoWebClient, times(1)).get();
	}
	
	@Test
	@DisplayName("[UT]deleteNaverToken: 카카오 로그인 토큰 삭제 - 리프레시 토큰 없음")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void test_deleteKakaoToken_notExistRefreshToken() {
		
		String accessToken = "accessToken";
		String targetId = "12345";
		Long userId = 1L;
		String refreshToken = "";
		String deviceId = "deviceId";
		
		doNothing().when(loginHelper).updateUserStatus(userId, LoginStatusEnum.LOGOUT.getLoginStatus());
		
		// WebClient Mock 설정
		RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
		when(kakaoWebClient.get()).thenReturn(uriSpec);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		when(uriSpec.uri(anyString())).thenReturn(uriSpec);
		when(uriSpec.header(HttpHeaders.AUTHORIZATION, 
				CommonConstants.AUTHORIZATION_HEADER_PREFIX.concat(accessToken))).thenReturn(uriSpec);
		when(uriSpec.retrieve()).thenReturn(responseSpec);
		KakaoUserInfoDto userInfo = new KakaoUserInfoDto();
		userInfo.setId(1234567890L);
		when(responseSpec.bodyToMono(KakaoUserInfoDto.class)).thenReturn(Mono.just(userInfo));
		
		// 실제 메서드 호출
		KakaoUserInfoDto result = service.deleteKakaoToken(accessToken, targetId, userId, refreshToken, deviceId)
				;
		
		// 검증
		assertThat(result).usingRecursiveComparison().isEqualTo(userInfo);
		
		verify(kakaoWebClient, times(1)).get();
	}


}
