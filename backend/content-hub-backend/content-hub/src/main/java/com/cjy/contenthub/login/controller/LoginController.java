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
import org.springframework.web.util.UriComponentsBuilder;

import com.cjy.contenthub.common.api.dto.kakao.KakaoIssueTokenDto;
import com.cjy.contenthub.common.api.dto.kakao.KakaoUserInfoDto;
import com.cjy.contenthub.common.api.dto.naver.NaverDeleteTokenDto;
import com.cjy.contenthub.common.api.dto.naver.NaverIssueTokenDto;
import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.LoginProviderEnum;
import com.cjy.contenthub.login.client.LoginClient;
import com.cjy.contenthub.login.controller.dto.LoginUserResponseDto;
import com.cjy.contenthub.login.util.LoginUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 로그인 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

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

	/** 파라미터 : 권한 종별 */
	private static final String PARAM_GRANT_TYPE = "grant_type";

	/** 파라미터 : 클라이언트 ID */
	private static final String PARAM_CLIENT_ID = "client_id";

	/** 파라미터 : 클라이언트 시크릿 */
	private static final String PARAM_CLIENT_SECRET = "client_secret";

	/** 파라미터 : 코드 */
	private static final String PARAM_CODE = "code";

	/** 파라미터 : 상태 */
	private static final String PARAM_STATE = "state";

	/** 파라미터 : 액세스 토큰 */
	private static final String PARAM_ACCESS_TOKEN = "access_token";

	/** 파라미터 : 리프레쉬 토큰 */
	private static final String PARAM_REFRESH_TOKEN = "refresh_token";

	/** 파라미터 : 만료까지의 시간(초) */
	private static final String PARAM_EXPIRES_IN = "expires_in";

	/** 파라미터 : 리다이렉 URI */
	private static final String PARAM_REDIRECT_URI = "redirect_uri";

	/** 파라미터 : 서비스 제공자 */
	private static final String PARAM_SERVICE_PROVIDER = "service_provider";

	/** 파라미터 : 대상 ID */
	private static final String PARAM_TARGET_ID = "target_id";

	/** 파라미터 : 대상 ID 타입 */
	private static final String PARAM_TARGET_ID_TYPE = "target_id_type";

	/** 권한 종별 : 리프레시 토큰 */
	private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

	/** 권한 종별 : 삭제 */
	private static final String GRANT_TYPE_DELETE = "delete";

	/** 값 : 유저 ID */
	private static final String VALUE_USER_ID = "user_id";

	/** 값 : 인증 코드 */
	private static final String VALUE_AUTHORIZATION_CODE = "authorization_code";

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

		// 네이버 토큰 발급 URL 생성
		String uri = UriComponentsBuilder.fromUriString(naverTokenIssueUrl)
				.queryParam(PARAM_GRANT_TYPE, VALUE_AUTHORIZATION_CODE)
				.queryParam(PARAM_CLIENT_ID, clientId)
				.queryParam(PARAM_CLIENT_SECRET, clientSecret)
				.queryParam(PARAM_CODE, code)
				.queryParam(PARAM_STATE, state)
				.toUriString();

		// 네이버 토큰 발급 API 호출
		return naverWebClient.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(NaverIssueTokenDto.class)
				.flatMap(issueResponse -> {
					// 토큰 발행 API 응답이 정상적인 경우
					if (StringUtils.isEmpty(issueResponse.getError())) {
						// 리프레시 토큰 쿠키
						String refreshToken = issueResponse.getRefreshToken();
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
						return loginClient.getNaverUserInfo(request, issueResponse.getAccessToken(), issueResponse.getExpiresIn(), cookieArray);

					} 
					// 토큰 발행 API 응답에 에러가 있는 경우
					else {
						// 에러 코드와 설명을 추출하여 예외 처리
						String errorCode = issueResponse.getError();
						Integer status =  errorCode.chars().allMatch(Character::isDigit) ? Integer.parseInt(errorCode) : HttpStatus.INTERNAL_SERVER_ERROR.value();
						throw new ResponseStatusException(HttpStatus.valueOf(status), issueResponse.getErrorDescription());
					}
				}).block();
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
		String refreshToken = LoginUtil.getRefreshToken(request, LoginProviderEnum.NAVER.getProvider());

		// refresh token이 없는 경우 처리 종료
		if (StringUtils.isEmpty(refreshToken)) {
			return ResponseEntity.ok(new LoginUserResponseDto());
		}

		// 네이버 토큰 갱신 URL 생성
		String uri = UriComponentsBuilder.fromUriString(naverTokenIssueUrl)
				.queryParam(PARAM_GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN)
				.queryParam(PARAM_CLIENT_ID, clientId)
				.queryParam(PARAM_CLIENT_SECRET, clientSecret)
				.queryParam(PARAM_REFRESH_TOKEN, refreshToken)
				.toUriString();

		// 네이버 토큰 갱신 API 호출
		return naverWebClient.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(NaverIssueTokenDto.class)
				.flatMap(updateResponse -> 
				// 네이버 프로필 조회 처리
				loginClient.getNaverUserInfo(request, updateResponse.getAccessToken(), updateResponse.getExpiresIn(), null)
						).block();
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

		// 네이버 토큰 삭제 URL 생성
		String uri = UriComponentsBuilder.fromUriString(naverTokenIssueUrl)
				.queryParam(PARAM_GRANT_TYPE, GRANT_TYPE_DELETE)
				.queryParam(PARAM_CLIENT_ID, clientId)
				.queryParam(PARAM_CLIENT_SECRET, clientSecret)
				.queryParam(PARAM_ACCESS_TOKEN, accessToken)
				.queryParam(PARAM_SERVICE_PROVIDER, LoginProviderEnum.NAVER.getProvider())
				.toUriString();

		// 네이버 토큰 삭제 API 호출
		return naverWebClient.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(NaverDeleteTokenDto.class)
				.map(response -> {
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
							.body(response);

				}).block();
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

		// 카카오 토큰 발급 URL 생성
		String uri = UriComponentsBuilder.fromUriString(kakaoTokenIssueUrl)
				.queryParam(PARAM_GRANT_TYPE, VALUE_AUTHORIZATION_CODE)
				.queryParam(PARAM_CLIENT_ID, clientId)
				.queryParam(PARAM_REDIRECT_URI, redirectUri)
				.queryParam(PARAM_CODE, code)
				.queryParam(PARAM_CLIENT_SECRET, kakaoClientSecret)
				.toUriString();

		// 카카오 토큰 발급 API 조회
		return kakaoWebClient.post()
				.uri(uri)
				.retrieve()
				.bodyToMono(KakaoIssueTokenDto.class)
				.flatMap(issueResponse -> {
					// idToken 확인
					String[] idTokenArray = issueResponse.getIdToken().split("\\.");
					// ID토큰 정보가 존재하지 않을 경우, 400 에러
					if (ObjectUtils.isEmpty(idTokenArray) || idTokenArray.length != 3) {
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payload is Empty");
					}
					// 리프레시 토큰 쿠키
					String refreshToken = issueResponse.getRefreshToken();
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
					return loginClient.getKakaoUserInfo(request, issueResponse.getAccessToken(), issueResponse.getExpiresIn(), cookieArray);
				}).block();
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
		String refreshTokenFromCookie = LoginUtil.getRefreshToken(request, LoginProviderEnum.KAKAO.getProvider());

		// 리프레시 토큰이 없는 경우 처리 종료
		if (StringUtils.isEmpty(refreshTokenFromCookie)) {
			return ResponseEntity.ok(null);
		}

		// 카카오 토큰 갱신 URL 생성
		String uri = UriComponentsBuilder.fromUriString(kakaoTokenIssueUrl)
				.queryParam(PARAM_GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN)
				.queryParam(PARAM_CLIENT_ID, clientId)
				.queryParam(PARAM_REFRESH_TOKEN, refreshTokenFromCookie)
				.queryParam(PARAM_CLIENT_SECRET, kakaoClientSecret)
				.toUriString();

		// 카카오 토큰 갱신 API 조회
		return kakaoWebClient.post()
				.uri(uri)
				.retrieve()
				.bodyToMono(KakaoIssueTokenDto.class)
				.flatMap(issueResponse -> {
					// idToken 확인
					String[] idTokenArray = issueResponse.getIdToken().split("\\.");
					// ID토큰 정보가 존재하지 않을 경우, 400 에러
					if (ObjectUtils.isEmpty(idTokenArray) || idTokenArray.length != 3) {
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payload is Empty");
					}
					// 리프레시 토큰
					String refreshToken = issueResponse.getRefreshToken();
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
					return loginClient.getKakaoUserInfo(request, issueResponse.getAccessToken(), issueResponse.getExpiresIn(), cookieArray);
				}).block();
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

		// 카카오 토큰 삭제 URL 생성
		String uri = UriComponentsBuilder.fromUriString(kakaoLogoutUrl)
				.queryParam(PARAM_TARGET_ID_TYPE, VALUE_USER_ID)
				.queryParam(PARAM_TARGET_ID, targetId)
				.toUriString();

		// 유저 정보 가져오기 API 조회
		return kakaoWebClient.get()
				.uri(uri)
				.headers(header -> 
				header.set(HttpHeaders.AUTHORIZATION, CommonConstants.AUTHORIZATION_HEADER_PREFIX.concat(accessToken))
						)
				.retrieve()
				.bodyToMono(KakaoUserInfoDto.class)
				.map(response -> {
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
							.body(response);
				}).block();
	}

}
