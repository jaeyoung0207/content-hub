package com.cjy.contenthub.login.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import com.cjy.contenthub.AbstractBaseIT;
import com.cjy.contenthub.common.advice.response.CommonErrorResponse;
import com.cjy.contenthub.common.integration.kakao.dto.KakaoIssueTokenDto;
import com.cjy.contenthub.common.integration.kakao.dto.KakaoUserInfoDto;
import com.cjy.contenthub.common.integration.naver.dto.NaverDeleteTokenDto;
import com.cjy.contenthub.common.integration.naver.dto.NaverIssueTokenDto;
import com.cjy.contenthub.common.record.CommonRecords.LoginCookiesRecord;
import com.cjy.contenthub.common.util.CookieUtil;
import com.cjy.contenthub.core.constants.DomainEnum.NaverProfileErrorEnum;
import com.cjy.contenthub.login.client.LoginClient;
import com.cjy.contenthub.login.controller.dto.LoginUserInfoDto;
import com.cjy.contenthub.login.controller.dto.LoginUserResponseDto;
import com.cjy.contenthub.login.service.LoginService;

@Sql(
		scripts = { "/sql/init/init.sql" }, 
		config = @SqlConfig(encoding = "utf-8", transactionMode = TransactionMode.INFERRED)
		)
class LoginControllerIT extends AbstractBaseIT {
	
	@MockitoBean
    LoginService loginService;

	@MockitoBean
    LoginClient loginClient;
	
	@MockitoBean
	CookieUtil cookieUtil;
	
	@Test
	@DisplayName("[IT]getNaverLoginInfo: 네이버 로그인 정보 조회 API - 성공")
	void test_getNaverLoginInfo_success() throws Exception {

		// 파라미터 설정
		String code = "code";
		String state = "state";
		String accessToken = "access_token";
		Integer expiresIn = 3600;
		
		// 서비스 mock 응답 설정
        NaverIssueTokenDto issueToken = new NaverIssueTokenDto();
        issueToken.setAccessToken(accessToken);
        issueToken.setExpiresIn(expiresIn);
        issueToken.setRefreshToken("refresh_token");
        when(loginService.getNaverIssueToken(anyString(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(issueToken));
        
        // 클라이언트 mock 응답 설정
        LoginUserResponseDto userResponse = new LoginUserResponseDto();
        userResponse.setAccessToken(accessToken);
        userResponse.setExpiresIn(expiresIn);
        when(loginClient.getNaverUserInfo(any(), anyString(), anyInt(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(userResponse)));
		
        // 테스트 대상 호출
		String url = "/api/login/getNaverLoginInfo";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("code", code)
				.param("state", state)
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		LoginUserResponseDto response = objectMapper.readValue(responseBody, LoginUserResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getAccessToken()).isEqualTo(accessToken);
		assertThat(response.getExpiresIn()).isEqualTo(expiresIn);
	}
	
	@Test
	@DisplayName("[IT]getNaverLoginInfo: 네이버 로그인 정보 조회 API - 실패")
	void test_getNaverLoginInfo_failure() throws Exception {

		// 파라미터 설정
		String code = "code";
		String state = "state";
		String resultErrorCode = "024";
		Integer httpErrorCode = NaverProfileErrorEnum.getNaverProfileError(resultErrorCode).getHttpErrorCode();
		String errorDescription = "Authentication failed / 인증에 실패했습니다.";
		
		// 서비스 mock 응답 설정
        NaverIssueTokenDto issueToken = new NaverIssueTokenDto();
        issueToken.setError(resultErrorCode);
        issueToken.setErrorDescription(errorDescription);
        when(loginService.getNaverIssueToken(anyString(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(issueToken));
        
        // 테스트 대상 호출
		String url = "/api/login/getNaverLoginInfo";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("code", code)
				.param("state", state)
				)
				.andExpect(status().is(httpErrorCode)) // 상태 코드 검증
				// 예외 검증
				.andExpect(ex -> assertThat(ex.getResolvedException())
						.isInstanceOf(ResponseStatusException.class)
						.hasMessageContaining(errorDescription))
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		CommonErrorResponse response = objectMapper.readValue(responseBody, CommonErrorResponse.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getPath()).isEqualTo(url);
		assertThat(response.getStatus()).isEqualTo(httpErrorCode);
		assertThat(response.getMessage()).isEqualTo(errorDescription);
	}
	
	@Test
	@DisplayName("[IT]updateNaverLoginInfo: 네이버 로그인 정보 갱신 API - 성공")
	void test_updateNaverLoginInfo_success() throws Exception {
		
		String accessToken = "access_token";
		Integer expiresIn = 3600;

		// 쿠키 mock 응답 설정
		String refreshToken = "refresh_token";
        when(cookieUtil.getRefreshToken(any(), anyString())).thenReturn(refreshToken);
        
        // 클라이언트 mock 응답 설정
        String deviceId = "device_id";
        when(cookieUtil.getCookieValue(any(), anyString())).thenReturn(deviceId);
        
		// 서비스 mock 응답 설정
        NaverIssueTokenDto issueToken = new NaverIssueTokenDto();
        issueToken.setAccessToken(accessToken);
        issueToken.setExpiresIn(expiresIn);
        issueToken.setRefreshToken("refresh_token");
        when(loginService.getNaverUpdateToken(anyString(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(issueToken));
        
        // 클라이언트 mock 응답 설정
        LoginUserResponseDto userResponse = new LoginUserResponseDto();
        userResponse.setAccessToken(accessToken);
        userResponse.setExpiresIn(expiresIn);
        when(loginClient.getNaverUserInfo(any(), anyString(), anyInt(), eq(null)))
        .thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(userResponse)));
		
        // 테스트 대상 호출
		String url = "/api/login/updateNaverLoginInfo";
		MvcResult mvcResult = mockMvc.perform(get(url))
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		LoginUserResponseDto response = objectMapper.readValue(responseBody, LoginUserResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getAccessToken()).isEqualTo(accessToken);
		assertThat(response.getExpiresIn()).isEqualTo(expiresIn);
	}
	
	@Test
	@DisplayName("[IT]updateNaverLoginInfo: 네이버 로그인 정보 갱신 API - 실패")
	void test_updateNaverLoginInfo_failure() throws Exception {
		
        // 테스트 대상 호출
		String url = "/api/login/updateNaverLoginInfo";
		MvcResult mvcResult = mockMvc.perform(get(url))
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 리스트로 변환
		LoginUserResponseDto response = objectMapper.readValue(responseBody, LoginUserResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getAccessToken()).isNull();
		assertThat(response.getExpiresIn()).isNull();
	}
	
	@Test
	@DisplayName("[IT]getNaverUserInfo: 네이버 유저 정보 조회 API")
	void test_getNaverUserInfo() throws Exception {
		
		String accessToken = "access_token";
		Integer expiresIn = 3600;
        
        // 클라이언트 mock 응답 설정
        LoginUserResponseDto userResponse = new LoginUserResponseDto();
        LoginUserInfoDto userInfo = LoginUserInfoDto.builder()
        		.userId(1L)
        		.provider("NAVER")
        		.id("id")
        		.nickname("nickname")
        		.name("name")
        		.build();
        userResponse.setUserInfo(userInfo);
        userResponse.setAccessToken(accessToken);
        userResponse.setExpiresIn(expiresIn);
        when(loginClient.getNaverUserInfo(any(), anyString(), anyInt(), eq(null)))
        .thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(userResponse)));
		
        // 테스트 대상 호출
		String url = "/api/login/getNaverUserInfo";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("access_token", accessToken)
				.param("expires_in", String.valueOf(expiresIn))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		LoginUserResponseDto response = objectMapper.readValue(responseBody, LoginUserResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getAccessToken()).isEqualTo(accessToken);
		assertThat(response.getExpiresIn()).isEqualTo(expiresIn);
		assertThat(response.getUserInfo()).usingRecursiveComparison().isEqualTo(userInfo);
	}
	
	@Test
	@DisplayName("[IT]deleteNaverToken: 네이버 로그인 정보 삭제 API")
	void test_deleteNaverToken() throws Exception {
		
		String accessToken = "access_token";
		String targetId = "targetId";
		Long userId = 1L;

		// 쿠키 mock 응답 설정
		String refreshToken = "refresh_token";
        when(cookieUtil.getRefreshToken(any(), anyString())).thenReturn(refreshToken);
        
        // 클라이언트 mock 응답 설정
        String deviceId = "device_id";
        when(cookieUtil.getCookieValue(any(), anyString())).thenReturn(deviceId);
        
		// 서비스 mock 응답 설정
        NaverDeleteTokenDto deleteToken = new NaverDeleteTokenDto();
        deleteToken.setAccessToken(accessToken);
        when(loginService.deleteNaverToken(anyString(), anyString(), anyLong(), anyString(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(deleteToken));
        
        String providerCookie = "PROVIDER=NAVER; Max-Age=0; Path=/; Domain=''; Secure; HttpOnly; SameSite=Lax";
        String refreshTokenCookie = "REFRESH_TOKEN=abcd1234; Max-Age=0; Path=/; Domain=''; Secure; HttpOnly; SameSite=Lax";
        LoginCookiesRecord cookiesInfo = new LoginCookiesRecord(providerCookie, refreshTokenCookie);
        when(cookieUtil.getLoginCookiesForDelete()).thenReturn(cookiesInfo);
		
        // 테스트 대상 호출
		String url = "/api/login/deleteNaverToken";
		MvcResult mvcResult = mockMvc.perform(delete(url)
				.param("access_token", accessToken)
				.param("target_id", targetId)
				.param("user_id", String.valueOf(userId))
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn();
		
		// 쿠키 헤더 값 추출
		List<String> headerValue = mvcResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		LoginUserResponseDto response = objectMapper.readValue(responseBody, LoginUserResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getAccessToken()).isEqualTo(accessToken);
		boolean isIncludeCookie = headerValue.stream()
                .anyMatch(header -> header.contains("Max-Age=0") && header.contains("PROVIDER=NAVER"))
                && headerValue.stream()
                .anyMatch(header -> header.contains("Max-Age=0") && header.contains("REFRESH_TOKEN"));
		assertThat(isIncludeCookie).isTrue();
	}
	
	@Test
	@DisplayName("[IT]getKakaoLoginInfo: 카카오 로그인 정보 조회 API - 성공")
	void test_getKakaoLoginInfo_success() throws Exception {

		// 파라미터 설정
		String clientId = "clientId";
		String redirectUri = "http://localhost:3000/login/kakao";
		String code = "code";
		String accessToken = "access_token";
		Integer expiresIn = 3600;
		String refreshToken = "refresh_token";
		
		// 서비스 mock 응답 설정
		String idToken = "header.payload.signature";
		KakaoIssueTokenDto issueToken = new KakaoIssueTokenDto();
        issueToken.setAccessToken(accessToken);
        issueToken.setExpiresIn(expiresIn);
        issueToken.setRefreshToken(refreshToken);
        issueToken.setIdToken(idToken);
        when(loginService.getKakaoIssueToken(anyString(), anyString(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(issueToken));
        
        // 클라이언트 mock 응답 설정
        LoginUserResponseDto userResponse = new LoginUserResponseDto();
        userResponse.setAccessToken(accessToken);
        userResponse.setExpiresIn(expiresIn);
        when(loginClient.getKakaoUserInfo(any(), anyString(), anyInt(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(userResponse)));
		
        // 테스트 대상 호출
		String url = "/api/login/getKakaoLoginInfo";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("client_id", clientId)
				.param("redirect_uri", redirectUri)
				.param("code", code)
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		LoginUserResponseDto response = objectMapper.readValue(responseBody, LoginUserResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getAccessToken()).isEqualTo(accessToken);
		assertThat(response.getExpiresIn()).isEqualTo(expiresIn);
	}
	
	@Test
	@DisplayName("[IT]getKakaoLoginInfo: 카카오 로그인 정보 조회 API - ID 토큰 없음")
	void test_getKakaoLoginInfo_notExistIdToken() throws Exception {

		// 파라미터 설정
		String clientId = "clientId";
		String redirectUri = "http://localhost:3000/login/kakao";
		String code = "code";
		String errorMessage = "로그인 페이로드가 비어있습니다.";
		
		// 서비스 mock 응답 설정
		String idToken = ""; // ID 토큰이 없는 경우
		KakaoIssueTokenDto issueToken = new KakaoIssueTokenDto();
        issueToken.setIdToken(idToken);
        when(loginService.getKakaoIssueToken(anyString(), anyString(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(issueToken));
        
        // 클라이언트 mock 응답 설정
        LoginUserResponseDto userResponse = new LoginUserResponseDto();
        when(loginClient.getKakaoUserInfo(any(), anyString(), anyInt(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(userResponse)));
		
        // 테스트 대상 호출
		String url = "/api/login/getKakaoLoginInfo";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("client_id", clientId)
				.param("redirect_uri", redirectUri)
				.param("code", code)
				)
				.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
				// 예외 검증
				.andExpect(ex -> assertThat(ex.getResolvedException())
						.isInstanceOf(ResponseStatusException.class)
						.hasMessageContaining(errorMessage))
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		CommonErrorResponse response = objectMapper.readValue(responseBody, CommonErrorResponse.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getPath()).isEqualTo(url);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(response.getMessage()).isEqualTo("로그인 페이로드가 비어있습니다.");
	}
	
	@Test
	@DisplayName("[IT]updateKakaoLoginInfo: 카카오 로그인 정보 갱신 API - 성공")
	void test_updateKakaoLoginInfo_success() throws Exception {
		
		String clientId = "clientId";
		String accessToken = "access_token";
		Integer expiresIn = 3600;

		// 쿠키 mock 응답 설정
		String refreshToken = "refresh_token";
        when(cookieUtil.getRefreshToken(any(), anyString())).thenReturn(refreshToken);
        
        // 클라이언트 mock 응답 설정
        String deviceId = "device_id";
        when(cookieUtil.getCookieValue(any(), anyString())).thenReturn(deviceId);
        
		// 서비스 mock 응답 설정
        String idToken = "header.payload.signature";
        KakaoIssueTokenDto issueToken = new KakaoIssueTokenDto();
        issueToken.setAccessToken(accessToken);
        issueToken.setExpiresIn(expiresIn);
        issueToken.setRefreshToken(refreshToken);
        issueToken.setIdToken(idToken);
        when(loginService.updateKakaoLoginInfo(anyString(), anyString(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(issueToken));
        
        // 클라이언트 mock 응답 설정
        LoginUserResponseDto userResponse = new LoginUserResponseDto();
        userResponse.setAccessToken(accessToken);
        userResponse.setExpiresIn(expiresIn);
        when(loginClient.getKakaoUserInfo(any(), anyString(), anyInt(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(userResponse)));
		
        // 테스트 대상 호출
		String url = "/api/login/updateKakaoLoginInfo";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("client_id", clientId)
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		LoginUserResponseDto response = objectMapper.readValue(responseBody, LoginUserResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getAccessToken()).isEqualTo(accessToken);
		assertThat(response.getExpiresIn()).isEqualTo(expiresIn);
	}
	
	@Test
	@DisplayName("[IT]updateKakaoLoginInfo: 카카오 로그인 정보 갱신 API - 리프레시 토큰 없음")
	void test_updateKakaoLoginInfo_notExistRefreshToken() throws Exception {
		
		String clientId = "clientId";
		
        // 테스트 대상 호출
		String url = "/api/login/updateKakaoLoginInfo";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("client_id", clientId)
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		LoginUserResponseDto response = objectMapper.readValue(responseBody, LoginUserResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getAccessToken()).isNull();
		assertThat(response.getExpiresIn()).isNull();
	}
	
	@Test
	@DisplayName("[IT]updateKakaoLoginInfo: 카카오 로그인 정보 갱신 API - ID 토큰 없음")
	void test_updateKakaoLoginInfo_notExistIdToken() throws Exception {
		
		String clientId = "clientId";
		String errorMessage = "로그인 페이로드가 비어있습니다.";

		// 쿠키 mock 응답 설정
		String refreshToken = "refresh_token";
        when(cookieUtil.getRefreshToken(any(), anyString())).thenReturn(refreshToken);
        
        // 클라이언트 mock 응답 설정
        String deviceId = "device_id";
        when(cookieUtil.getCookieValue(any(), anyString())).thenReturn(deviceId);
        
		// 서비스 mock 응답 설정
        String idToken = ""; // ID 토큰이 없는 경우
        KakaoIssueTokenDto issueToken = new KakaoIssueTokenDto();
        issueToken.setIdToken(idToken);
        when(loginService.updateKakaoLoginInfo(anyString(), anyString(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(issueToken));
		
        // 테스트 대상 호출
		String url = "/api/login/updateKakaoLoginInfo";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("client_id", clientId)
				)
				.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
				.andExpect(exception -> 
				// 예외 검증
				assertThat(exception.getResolvedException())
						.isInstanceOf(ResponseStatusException.class)
						.hasMessageContaining(errorMessage)
						)
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		CommonErrorResponse response = objectMapper.readValue(responseBody, CommonErrorResponse.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getPath()).isEqualTo(url);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(response.getMessage()).isEqualTo(errorMessage);
	}
	
	@Test
	@DisplayName("[IT]getKakaoUserInfo: 카카오 유저 정보 조회 API")
	void test_getKakaoUserInfo() throws Exception {
		
		String accessToken = "access_token";
		Integer expiresIn = 3600;
        
        // 클라이언트 mock 응답 설정
        LoginUserResponseDto userResponse = new LoginUserResponseDto();
        LoginUserInfoDto userInfo = LoginUserInfoDto.builder()
        		.userId(1L)
        		.provider("KAKAO")
        		.id("id")
        		.nickname("nickname")
        		.name("name")
        		.build();
        userResponse.setUserInfo(userInfo);
        userResponse.setAccessToken(accessToken);
        userResponse.setExpiresIn(expiresIn);
        when(loginClient.getKakaoUserInfo(any(), anyString(), anyInt(), eq(null)))
        .thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(userResponse)));
		
        // 테스트 대상 호출
		String url = "/api/login/getKakaoUserInfo";
		MvcResult mvcResult = mockMvc.perform(get(url)
				.param("access_token", accessToken)
				.param("expires_in", String.valueOf(expiresIn))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		LoginUserResponseDto response = objectMapper.readValue(responseBody, LoginUserResponseDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getAccessToken()).isEqualTo(accessToken);
		assertThat(response.getExpiresIn()).isEqualTo(expiresIn);
		assertThat(response.getUserInfo()).usingRecursiveComparison().isEqualTo(userInfo);
	}
	
	@Test
	@DisplayName("[IT]deleteKakaoToken: 카카오 로그인 정보 삭제 API")
	void test_deleteKakaoToken() throws Exception {
		
		String accessToken = "access_token";
		String targetId = "targetId";
		Long userId = 1L;

		// 쿠키 mock 응답 설정
		String refreshToken = "refresh_token";
        when(cookieUtil.getRefreshToken(any(), anyString())).thenReturn(refreshToken);
        
        // 클라이언트 mock 응답 설정
        String deviceId = "device_id";
        when(cookieUtil.getCookieValue(any(), anyString())).thenReturn(deviceId);
        
		// 서비스 mock 응답 설정
        Long id = 123456789L;
        KakaoUserInfoDto userInfo = new KakaoUserInfoDto();
        userInfo.setId(id);
        when(loginService.deleteKakaoToken(anyString(), anyString(), anyLong(), anyString(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(userInfo));
        
        String providerCookie = "PROVIDER=KAKAO; Max-Age=0; Path=/; Domain=''; Secure; HttpOnly; SameSite=Lax";
        String refreshTokenCookie = "REFRESH_TOKEN=abcd1234; Max-Age=0; Path=/; Domain=''; Secure; HttpOnly; SameSite=Lax";
        LoginCookiesRecord cookiesInfo = new LoginCookiesRecord(providerCookie, refreshTokenCookie);
        when(cookieUtil.getLoginCookiesForDelete()).thenReturn(cookiesInfo);
		
        // 테스트 대상 호출
		String url = "/api/login/deleteKakaoToken";
		MvcResult mvcResult = mockMvc.perform(delete(url)
				.param("access_token", accessToken)
				.param("target_id", targetId)
				.param("user_id", String.valueOf(userId))
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn();
		
		// 쿠키 헤더 값 추출
		List<String> headerValue = mvcResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
		// 응답 본문 추출
		String responseBody = mvcResult.getResponse().getContentAsString();
		// 응답 본문을 DTO로 변환
		KakaoUserInfoDto response = objectMapper.readValue(responseBody, KakaoUserInfoDto.class);
		
		// 검증
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(id);
		boolean isIncludeCookie = headerValue.stream()
                .anyMatch(header -> header.contains("Max-Age=0") && header.contains("PROVIDER=KAKAO"))
                && headerValue.stream()
                .anyMatch(header -> header.contains("Max-Age=0") && header.contains("REFRESH_TOKEN"));
		assertThat(isIncludeCookie).isTrue();
	}

}
