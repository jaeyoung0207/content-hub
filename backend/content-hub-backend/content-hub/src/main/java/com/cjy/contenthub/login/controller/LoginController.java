package com.cjy.contenthub.login.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.cjy.contenthub.common.api.dto.kakao.KakaoIssueTokenDto;
import com.cjy.contenthub.common.api.dto.kakao.KakaoUserInfoDto;
import com.cjy.contenthub.common.api.dto.naver.NaverDeleteTokenDto;
import com.cjy.contenthub.common.api.dto.naver.NaverIssueTokenDto;
import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.LoginProviderEnum;
import com.cjy.contenthub.login.client.LoginClient;
import com.cjy.contenthub.login.controller.dto.LoginUserResponseDto;
import com.cjy.contenthub.login.helper.LoginHelper;
import com.cjy.contenthub.login.service.LoginService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * 로그인 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

	/** 로그인 서비스 */
	private final LoginService loginService;

	/** 네이버 API WebClient */
	@Qualifier("naverWebClient")
	private final WebClient naverWebClient;

	/** 카카오 API WebClient */
	@Qualifier("kakaoWebClient")
	private final WebClient kakaoWebClient;

	/** 로그인 클라이언트 */
	private final LoginClient loginClient;

	/** 네이버 클라이언트 ID */
	@Value("${login.naver.clientId}")
	private String clientId;

	/** 네이버 클라이언트 시크릿 */
	@Value("${login.naver.clientSecret}")
	private String clientSecret;

	/** 네이버 API 토큰 발행 URL */
	@Value("${login.naver.tokenIssueUrl}")
	private String naverTokenIssueUrl;

	/** 네이버 API 유저 정보 URL */
	@Value("${login.kakao.userInfoUrl}")
	private String kakaoOidcUserInfoUrl;

	/** 카카오 API 토큰 발행 URL */
	@Value("${login.kakao.tokenIssueUrl}")
	private String kakaoTokenIssueUrl;

	/** 카카오 API 유저 정보 URL */
	@Value("${login.kakao.userInfoUrl}")
	private String kakaoUserInfoUrl;

	/** 카카오 API 로그아웃 URL */
	@Value("${login.kakao.logoutUrl}")
	private String kakaoLogoutUrl;

	/** 카카오 클라이언트 시크릿 */
	@Value("${login.kakao.clientSecret}")
	private String kakaoClientSecret;

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

	/**
	 * 네이버 로그인 정보 조회
	 * 
	 * @param request HttpServletRequest
	 * @param code 인증 코드
	 * @param state 인증 상태
	 * @return Mono<ResponseEntity<LoginUserResponseDto>>
	 */
	@GetMapping("/getNaverLoginInfo")
	public ResponseEntity<LoginUserResponseDto> getNaverLoginInfo(HttpServletRequest request, @RequestParam(PARAM_CODE) String code, @RequestParam(PARAM_STATE) String state) {

		// 네이버 토큰 발행
		NaverIssueTokenDto tokenResponse = loginService.getNaverIssueToken(code, state);

		// 토큰 발행 API 응답이 정상적인 경우
		if (StringUtils.isEmpty(tokenResponse.getError())) {
			// 리프레시 토큰 쿠키
			String refreshToken = tokenResponse.getRefreshToken();
			ResponseCookie refreshTokenCookie = ResponseCookie.from(CommonConstants.REFRESH_TOKEN, refreshToken)
					.path("/")
					.build();
			// provider 쿠키
			ResponseCookie providerCookie = ResponseCookie.from(CommonConstants.PROVIDER, LoginProviderEnum.NAVER.getProvider())
					.path("/")
					.build();
			// 쿠키 배열 생성
			String[] cookieArray = new String[] {
					refreshTokenCookie.toString(), 
					providerCookie.toString()
			};
			// 네이버 프로필 조회 처리
			return loginClient.getNaverUserInfo(request, tokenResponse.getAccessToken(), tokenResponse.getExpiresIn(), cookieArray).block();

		} 
		// 토큰 발행 API 응답에 에러가 있는 경우
		else {
			// 에러 코드와 설명을 추출하여 예외 처리
			String errorCode = tokenResponse.getError();
			Integer status =  errorCode.chars().allMatch(Character::isDigit) ? Integer.parseInt(errorCode) : HttpStatus.INTERNAL_SERVER_ERROR.value();
			throw new ResponseStatusException(HttpStatus.valueOf(status), tokenResponse.getErrorDescription());
		}
	}

	/**
	 * 네이버 로그인 정보 갱신
	 * 
	 * @param request HttpServletRequest
	 * @return ResponseEntity<LoginUserResponseDto>
	 */
	@GetMapping("/updateNaverLoginInfo")
	public ResponseEntity<LoginUserResponseDto> updateNaverLoginInfo(HttpServletRequest request) {

		// 쿠키에서 리프레시 토큰 추출
		String refreshToken = LoginHelper.getRefreshToken(request, LoginProviderEnum.NAVER.getProvider());

		// refresh token이 없는 경우 처리 종료
		if (StringUtils.isEmpty(refreshToken)) {
			return ResponseEntity.ok(new LoginUserResponseDto());
		}

		// 네이버 토큰 갱신 서비스 호출
		NaverIssueTokenDto tokenResponse = loginService.getNaverUpdateToken(refreshToken);

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
	@GetMapping("/getNaverUserInfo")
	public ResponseEntity<LoginUserResponseDto> getNaverUserInfo(
			HttpServletRequest request, 
			@RequestParam(PARAM_ACCESS_TOKEN) String accessToken,
			@RequestParam(PARAM_EXPIRES_IN) int expiresIn
			) {

		// 유저 정보 가져오기 API 조회
		return loginClient.getNaverUserInfo(request, accessToken, expiresIn, null).block();
	}

	/**
	 * 네이버 토큰 삭제
	 * 
	 * @param accessToken 액세스 토큰
	 * @return ResponseEntity<NaverDeleteTokenDto>
	 */
	@GetMapping("/deleteNaverToken")
	public ResponseEntity<NaverDeleteTokenDto> deleteNaverToken(@RequestParam(PARAM_ACCESS_TOKEN) String accessToken) {

		// 네이버 토큰 삭제 서비스 호출		
		NaverDeleteTokenDto tokenResponse = loginService.deleteNaverToken(accessToken);

		// 리프레시 토큰 쿠키 삭제
		ResponseCookie refreshTokenCookie = ResponseCookie.from(CommonConstants.REFRESH_TOKEN, "")
				.path("/")
				.maxAge(0) // 즉시 만료
				.build();
		// provider 쿠키 삭제
		ResponseCookie providerCookie = ResponseCookie.from(CommonConstants.PROVIDER, "")
				.path("/")
				.maxAge(0) // 즉시 만료
				.build();

		// 쿠키 설정 및 응답 반환
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString(), providerCookie.toString())
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
	@GetMapping("/getKakaoLoginInfo")
	public ResponseEntity<LoginUserResponseDto> getKakaoLoginInfo(
			HttpServletRequest request, 
			@RequestParam(PARAM_CLIENT_ID) String clientId, 
			@RequestParam(PARAM_REDIRECT_URI)String redirectUri,
			@RequestParam(PARAM_CODE) String code
			) {

		// 카카오 토큰 발행		
		KakaoIssueTokenDto tokenResponse = loginService.getKakaoIssueToken(clientId, redirectUri, code);

		// idToken 확인
		String[] idTokenArray = tokenResponse.getIdToken().split("\\.");
		// ID토큰 정보가 존재하지 않을 경우, 400 에러
		if (ObjectUtils.isEmpty(idTokenArray) || idTokenArray.length != 3) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payload is Empty");
		}
		// 리프레시 토큰 쿠키
		String refreshToken = tokenResponse.getRefreshToken();
		ResponseCookie refreshTokenCookie = ResponseCookie.from(CommonConstants.REFRESH_TOKEN, refreshToken)
				.path("/")
				.build();
		// provider 쿠키
		ResponseCookie providerCookie = ResponseCookie.from(CommonConstants.PROVIDER, LoginProviderEnum.KAKAO.getProvider())
				.path("/")
				.build();
		// 쿠키 배열 생성
		String[] cookieArray = new String[] {
				refreshTokenCookie.toString(),
				providerCookie.toString() 
		};

		// 유저 정보 가져오기 API 조회
		return loginClient.getKakaoUserInfo(request, tokenResponse.getAccessToken(), tokenResponse.getExpiresIn(), cookieArray).block();
	}

	/**
	 * 카카오 로그인 정보 갱신
	 * 
	 * @param request  HttpServletRequest
	 * @param clientId 클라이언트 ID
	 * @return ResponseEntity<LoginUserResponseDto>
	 */
	@GetMapping("/updateKakaoLoginInfo")
	public ResponseEntity<LoginUserResponseDto> updateKakaoLoginInfo(
			HttpServletRequest request, 
			@RequestParam(PARAM_CLIENT_ID) String clientId
			) {

		// 쿠키에서 리프레시 토큰 추출
		String refreshTokenFromCookie = LoginHelper.getRefreshToken(request, LoginProviderEnum.KAKAO.getProvider());

		// 리프레시 토큰이 없는 경우 처리 종료
		if (StringUtils.isEmpty(refreshTokenFromCookie)) {
			return ResponseEntity.ok(null);
		}

		// 카카오 토큰 갱신 서비스 호출
		KakaoIssueTokenDto tokenResponse = loginService.updateKakaoLoginInfo(clientId, refreshTokenFromCookie);

		// idToken 확인
		String[] idTokenArray = tokenResponse.getIdToken().split("\\.");
		// ID토큰 정보가 존재하지 않을 경우, 400 에러
		if (ObjectUtils.isEmpty(idTokenArray) || idTokenArray.length != 3) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payload is Empty");
		}
		// 리프레시 토큰
		String refreshToken = tokenResponse.getRefreshToken();
		// 쿠키 리스트
		List<String> cookieList = new ArrayList<>();
		// 리프레시 토큰 유무 확인
		if (StringUtils.isNotEmpty(refreshToken)) {
			// 존재하지 않는 경우 null 반환
			// 리프레시 토큰 쿠키
			ResponseCookie refreshTokenCookie = ResponseCookie.from(CommonConstants.REFRESH_TOKEN, refreshToken)
					.path("/")
					.build();
			// provider 쿠키
			ResponseCookie providerCookie = ResponseCookie.from(CommonConstants.PROVIDER, LoginProviderEnum.KAKAO.getProvider())
					.path("/")
					.build();
			cookieList.add(refreshTokenCookie.toString());
			cookieList.add(providerCookie.toString());
		}
		// 쿠키 배열 생성
		String[] cookieArray = ObjectUtils.isEmpty(cookieList) ? null : cookieList.toArray(new String[cookieList.size()]);

		// 유저 정보 가져오기 API 조회
		return loginClient.getKakaoUserInfo(request, tokenResponse.getAccessToken(), tokenResponse.getExpiresIn(), cookieArray).block();


	}

	/**
	 * 카카오 유저 정보 조회
	 * 
	 * @param request HttpServletRequest
	 * @param accessToken 액세스 토큰
	 * @param expiresIn 토큰 만료까지의 시간(초 단위)
	 * @return ResponseEntity<LoginUserResponseDto>
	 */
	@GetMapping("/getKakaoUserInfo")
	public ResponseEntity<LoginUserResponseDto> getKakaoUserInfo(
			HttpServletRequest request, 
			@RequestParam(PARAM_ACCESS_TOKEN) String accessToken,
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
	 * @return ResponseEntity<KakaoUserInfoDto>
	 */
	@GetMapping("/deleteKakaoToken")
	public ResponseEntity<KakaoUserInfoDto> deleteKakaoToken(
			HttpServletRequest request, 
			@RequestParam(PARAM_ACCESS_TOKEN) String accessToken,
			@RequestParam(PARAM_TARGET_ID) String targetId
			) {

		// 카카오 토큰 삭제 서비스 호출
		KakaoUserInfoDto useInfo = loginService.deleteKakaoToken(accessToken, targetId);

		// 리프레시 토큰 쿠키 삭제
		ResponseCookie refreshTokenCookie = ResponseCookie.from(CommonConstants.REFRESH_TOKEN, "")
				.path("/")
				.maxAge(0) // 즉시 만료
				.build();
		// provider 쿠키 삭제
		ResponseCookie providerCookie = ResponseCookie.from(CommonConstants.PROVIDER, "")
				.path("/")
				.maxAge(0) // 즉시 만료
				.build();
		// 쿠키 설정 및 응답 반환
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString(), providerCookie.toString())
				.body(useInfo);
	}

}
