package com.cjy.contenthub.login.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.cjy.contenthub.common.annotation.ApiController;
import com.cjy.contenthub.common.annotation.MaskingTarget;
import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.integration.kakao.dto.KakaoIssueTokenDto;
import com.cjy.contenthub.common.integration.kakao.dto.KakaoUserInfoDto;
import com.cjy.contenthub.common.integration.naver.dto.NaverDeleteTokenDto;
import com.cjy.contenthub.common.integration.naver.dto.NaverIssueTokenDto;
import com.cjy.contenthub.common.record.CommonRecords.LoginCookiesRecord;
import com.cjy.contenthub.common.util.CookieUtil;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.core.constants.DomainEnum.DomainMessagesErrorEnum;
import com.cjy.contenthub.core.constants.DomainEnum.LoginProviderEnum;
import com.cjy.contenthub.core.constants.DomainEnum.NaverProfileErrorEnum;
import com.cjy.contenthub.login.client.LoginClient;
import com.cjy.contenthub.login.controller.dto.LoginUserResponseDto;
import com.cjy.contenthub.login.service.LoginService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * 로그인 API 컨트롤러 클래스
 */
@Tag(name = "login-api", description = "Login APIs")
@ApiController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

	/** 로그인 서비스 */
	private final LoginService loginService;

	/** 로그인 클라이언트 */
	private final LoginClient loginClient;
	
	/** 메시지 유틸 */
	private final MessageUtil messageUtil;
	
	/** 쿠키 유틸 */
	private final CookieUtil cookieUtil;

	/** 파라미터 : 클라이언트 ID */
	private static final String PARAM_CLIENT_ID = "client_id";

	/** 파라미터 : 코드 */
	private static final String PARAM_CODE = "code";

	/** 파라미터 : 상태 */
	private static final String PARAM_STATE = "state";

	/** 파라미터 : 액세스 토큰 */
	private static final String PARAM_ACCESS_TOKEN = "access_token";

	/** 파라미터 : 만료까지의 시간(초) */
	private static final String PARAM_EXPIRES_IN = "expires_in";

	/** 파라미터 : 리다이렉 URI */
	private static final String PARAM_REDIRECT_URI = "redirect_uri";

	/** 파라미터 : 대상 ID */
	private static final String PARAM_TARGET_ID = "target_id";
	
	/** 파라미터 : 유저 테이블 ID */
	private static final String PARAM_USER_ID = "user_id";

	/**
	 * 네이버 로그인 정보 조회
	 * 
	 * @param request HttpServletRequest
	 * @param code 인증 코드
	 * @param state 인증 상태
	 * @return Mono<ResponseEntity<LoginUserResponseDto>>
	 */
	@Operation(summary = "네이버 로그인 정보 조회")
	@GetMapping("/getNaverLoginInfo")
	public ResponseEntity<LoginUserResponseDto> getNaverLoginInfo(
			HttpServletRequest request, 
			@RequestParam(PARAM_CODE) @MaskingTarget String code, 
			@RequestParam(PARAM_STATE) @MaskingTarget String state) {

		// 네이버 토큰 발행
		NaverIssueTokenDto tokenResponse = loginService.getNaverIssueToken(code, state);

		// 토큰 발행 API 응답이 정상적인 경우
		if (StringUtils.isEmpty(tokenResponse.getError())) {
			// 네이버 프로필 조회 처리
			return loginClient.getNaverUserInfo(request, tokenResponse.getAccessToken(), tokenResponse.getExpiresIn(), tokenResponse.getRefreshToken()).block();

		} 
		// 토큰 발행 API 응답에 에러가 있는 경우
		else {
			// 에러 코드와 설명을 추출하여 예외 처리
			Integer httpErrorCode = NaverProfileErrorEnum.getNaverProfileError(tokenResponse.getError()).getHttpErrorCode();
			throw new ResponseStatusException(HttpStatus.valueOf(httpErrorCode), tokenResponse.getErrorDescription());
		}
	}

	/**
	 * 네이버 로그인 정보 갱신
	 * 
	 * @param request HttpServletRequest
	 * @return ResponseEntity<LoginUserResponseDto>
	 */
	@Operation(summary = "네이버 로그인 정보 갱신")
	@GetMapping("/updateNaverLoginInfo")
	public ResponseEntity<LoginUserResponseDto> updateNaverLoginInfo(HttpServletRequest request) {

		// 쿠키에서 리프레시 토큰 추출
		String refreshToken = cookieUtil.getRefreshToken(request, LoginProviderEnum.NAVER.getProvider());
		
		// 쿠키에서 디바이스 ID 추출
		String deviceId = cookieUtil.getCookieValue(request, CommonConstants.DEVICE_ID);

		// 리프레시 토큰 또는 디바이스 ID가 없는 경우 처리 종료
		if (StringUtils.isAnyEmpty(refreshToken, deviceId)) {
			return ResponseEntity.ok(new LoginUserResponseDto());
		}

		// 네이버 토큰 갱신 서비스 호출
		NaverIssueTokenDto tokenResponse = loginService.getNaverUpdateToken(refreshToken, deviceId);

		// 네이버 프로필 조회 처리
		return loginClient.getNaverUserInfo(request, tokenResponse.getAccessToken(), tokenResponse.getExpiresIn(), null).block();
	}

	/**
	 * 네이버 유저 정보 조회
	 * 
	 * @param request HttpServletRequest
	 * @param accessToken 액세스 토큰
	 * @param expiresIn 토큰 만료까지의 시간(초)
	 * @return ResponseEntity<LoginUserResponseDto>
	 */
	@Operation(summary = "네이버 유저 정보 조회")
	@GetMapping("/getNaverUserInfo")
	public ResponseEntity<LoginUserResponseDto> getNaverUserInfo(
			HttpServletRequest request, 
			@RequestParam(PARAM_ACCESS_TOKEN) @MaskingTarget String accessToken,
			@RequestParam(PARAM_EXPIRES_IN) int expiresIn
			) {

		// 유저 정보 가져오기 API 조회
		return loginClient.getNaverUserInfo(request, accessToken, expiresIn, null).block();
	}

	/**
	 * 네이버 토큰 삭제
	 * 
	 * @param accessToken 액세스 토큰
	 * @param targetId 대상 ID
	 * @param userId 유저 테이블 ID
	 * @return ResponseEntity<NaverDeleteTokenDto>
	 */
	@Operation(summary = "네이버 토큰 삭제")
	@DeleteMapping("/deleteNaverToken")
	public ResponseEntity<NaverDeleteTokenDto> deleteNaverToken(
			HttpServletRequest request,
			@RequestParam(PARAM_ACCESS_TOKEN) @MaskingTarget String accessToken,
			@RequestParam(PARAM_TARGET_ID) @MaskingTarget String targetId,
			@RequestParam(PARAM_USER_ID) @MaskingTarget Long userId
			) {
		
		// 리프레시 토큰 추출
		String refreshToken = cookieUtil.getRefreshToken(request, LoginProviderEnum.NAVER.getProvider());
		
		// 쿠키에서 디바이스 ID 추출
		String deviceId = cookieUtil.getCookieValue(request, CommonConstants.DEVICE_ID);

		// 네이버 토큰 삭제 서비스 호출
		NaverDeleteTokenDto tokenResponse = loginService.deleteNaverToken(accessToken, targetId, userId, refreshToken, deviceId);
		
		// 로그인 쿠키 삭제
		LoginCookiesRecord cookiesInfo = cookieUtil.getLoginCookiesForDelete();

		// 쿠키 설정 및 응답 반환
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookiesInfo.refreshToken(), cookiesInfo.provider())
				.body(tokenResponse);
	}

	/**
	 * 카카오 로그인 정보 조회
	 * 
	 * @param request HttpServletRequest
	 * @param clientId 클라이언트 ID
	 * @param redirectUri 리다이렉트 URI
	 * @param code 인증 코드
	 * @return ResponseEntity<LoginUserResponseDto>
	 */
	@Operation(summary = "카카오 로그인 정보 조회")
	@GetMapping("/getKakaoLoginInfo")
	public ResponseEntity<LoginUserResponseDto> getKakaoLoginInfo(
			HttpServletRequest request, 
			@RequestParam(PARAM_CLIENT_ID) @MaskingTarget String clientId, 
			@RequestParam(PARAM_REDIRECT_URI)String redirectUri,
			@RequestParam(PARAM_CODE) @MaskingTarget String code
			) {

		// 카카오 토큰 발행		
		KakaoIssueTokenDto tokenResponse = loginService.getKakaoIssueToken(clientId, redirectUri, code);

		// idToken 확인
		String[] idTokenArray = tokenResponse.getIdToken().split("\\.");
		// ID토큰 정보가 존재하지 않을 경우, 400 에러
		if (ObjectUtils.isEmpty(idTokenArray) || idTokenArray.length != 3) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, messageUtil.getMessageKO(DomainMessagesErrorEnum.ERROR_LOGIN_PAYLOAD_EMPTY.getMessageCode()));
		}
		// 유저 정보 가져오기 API 조회
		return loginClient.getKakaoUserInfo(request, tokenResponse.getAccessToken(), tokenResponse.getExpiresIn(), tokenResponse.getRefreshToken()).block();
	}

	/**
	 * 카카오 로그인 정보 갱신
	 * 
	 * @param request  HttpServletRequest
	 * @param clientId 클라이언트 ID
	 * @return ResponseEntity<LoginUserResponseDto>
	 */
	@Operation(summary = "카카오 로그인 정보 갱신")
	@GetMapping("/updateKakaoLoginInfo")
	public ResponseEntity<LoginUserResponseDto> updateKakaoLoginInfo(
			HttpServletRequest request, 
			@RequestParam(PARAM_CLIENT_ID) @MaskingTarget String clientId
			) {

		// 쿠키에서 리프레시 토큰 추출
		String refreshToken = cookieUtil.getRefreshToken(request, LoginProviderEnum.KAKAO.getProvider());
		
		// 쿠키에서 디바이스 ID 추출
		String deviceId = cookieUtil.getCookieValue(request, CommonConstants.DEVICE_ID);

		// 리프레시 토큰이 없는 경우 처리 종료
		if (StringUtils.isAnyEmpty(refreshToken, deviceId)) {
			return ResponseEntity.ok(new LoginUserResponseDto());
		}

		// 카카오 토큰 갱신 서비스 호출
		KakaoIssueTokenDto tokenResponse = loginService.updateKakaoLoginInfo(clientId, refreshToken, deviceId);

		// idToken 확인
		String[] idTokenArray = tokenResponse.getIdToken().split("\\.");
		// ID토큰 정보가 존재하지 않을 경우, 400 에러
		if (ObjectUtils.isEmpty(idTokenArray) || idTokenArray.length != 3) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageUtil.getMessageKO(DomainMessagesErrorEnum.ERROR_LOGIN_PAYLOAD_EMPTY.getMessageCode()));
		}
		// 유저 정보 가져오기 API 조회
		return loginClient.getKakaoUserInfo(request, tokenResponse.getAccessToken(), tokenResponse.getExpiresIn(), tokenResponse.getRefreshToken()).block();
	}

	/**
	 * 카카오 유저 정보 조회
	 * 
	 * @param request HttpServletRequest
	 * @param accessToken 액세스 토큰
	 * @param expiresIn 토큰 만료까지의 시간(초 단위)
	 * @return ResponseEntity<LoginUserResponseDto>
	 */
	@Operation(summary = "카카오 유저 정보 조회")
	@GetMapping("/getKakaoUserInfo")
	public ResponseEntity<LoginUserResponseDto> getKakaoUserInfo(
			HttpServletRequest request, 
			@RequestParam(PARAM_ACCESS_TOKEN) @MaskingTarget String accessToken,
			@RequestParam(PARAM_EXPIRES_IN) int expiresIn
			) {

		// 유저 정보 가져오기 API 조회
		return loginClient.getKakaoUserInfo(request, accessToken, expiresIn, null).block();
	}

	/**
	 * 카카오 토큰 삭제
	 * 
	 * @param request HttpServletRequest
	 * @param accessToken 액세스 토큰
	 * @param targetId 대상 ID
	 * @param userId 유저 테이블 ID
	 * @return ResponseEntity<KakaoUserInfoDto>
	 */
	@Operation(summary = "카카오 토큰 삭제")
	@DeleteMapping("/deleteKakaoToken")
	public ResponseEntity<KakaoUserInfoDto> deleteKakaoToken(
			HttpServletRequest request, 
			@RequestParam(PARAM_ACCESS_TOKEN) @MaskingTarget String accessToken,
			@RequestParam(PARAM_TARGET_ID) @MaskingTarget String targetId,
			@RequestParam(PARAM_USER_ID) @MaskingTarget Long userId
			) {
		
		// 리프레시 토큰 추출
		String refreshToken = cookieUtil.getRefreshToken(request, LoginProviderEnum.KAKAO.getProvider());
		
		// 쿠키에서 디바이스 ID 추출
		String deviceId = cookieUtil.getCookieValue(request, CommonConstants.DEVICE_ID);

		// 카카오 토큰 삭제 서비스 호출
		KakaoUserInfoDto useInfo = loginService.deleteKakaoToken(accessToken, targetId, userId, refreshToken, deviceId);
		
		// 로그인 쿠키 삭제
		LoginCookiesRecord cookiesInfo = cookieUtil.getLoginCookiesForDelete();
		
		// 쿠키 설정 및 응답 반환
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookiesInfo.refreshToken(), cookiesInfo.provider())
				.body(useInfo);
	}

}
