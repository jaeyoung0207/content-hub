package com.cjy.contenthub.login.client;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.integration.kakao.dto.KakaoProfileDto;
import com.cjy.contenthub.common.integration.kakao.dto.KakaoUserInfoDto;
import com.cjy.contenthub.common.integration.naver.dto.NaverProfileDataDto;
import com.cjy.contenthub.common.integration.naver.dto.NaverProfileResultDto;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.core.constants.DomainEnum.DomainMessagesErrorEnum;
import com.cjy.contenthub.core.constants.DomainEnum.LoginProviderEnum;
import com.cjy.contenthub.core.constants.DomainEnum.NaverProfileErrorEnum;
import com.cjy.contenthub.login.controller.dto.LoginUserInfoDto;
import com.cjy.contenthub.login.controller.dto.LoginUserResponseDto;
import com.cjy.contenthub.login.helper.LoginHelper;
import com.cjy.contenthub.login.helper.LoginHelper.JwtCreationRecord;
import com.cjy.contenthub.login.mapper.LoginMapper;
import com.cjy.contenthub.login.service.LoginService;
import com.cjy.contenthub.login.service.dto.LoginUserServiceDto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 로그인 관련 API 클라이언트
 * 
 * @see LoginService
 */
@Component
@RequiredArgsConstructor
public class LoginClient {

	/** 로그인 매퍼 */
	private final LoginMapper mapper;
	
	/** 메시지 유틸 */
	private final MessageUtil messageUtil;
	
	/** 로그인 서비스 */
	private final LoginService service;
	
	/** 로그인 헬퍼 */
	private final LoginHelper loginHelper;

	/** 네이버 API WebClient */
	@Qualifier("naverWebClient")
	private final WebClient naverWebClient;

	/** 카카오 API WebClient */
	@Qualifier("kakaoWebClient")
	private final WebClient kakaoWebClient;
	
	/** 네이버 API 유저 정보 조회 URL */
	@Value("${login.naver.url.user-info-url}")
	private String naverUserInfoUrl;

	/** 카카오 API 유저 정보 조회 URL */
	@Value("${login.kakao.url.user-info-url}")
	private String kakaoUserInfoUrl;

	/** 네이버 리프레시 토큰 만료 시간 (일) */
	@Value("${login.naver.custom.refresh-token-expires-in}")
	private long naverExpiresIn;
	
	/** 네이버 리프레시 토큰 만료 시간 (일) */
	@Value("${login.kakao.custom.refresh-token-expires-in}")
	private long kakaoExpiresIn;

	/** 유저 정보 조회 API 응답 성공 결과 코드 */
	private static final String PROFILE_API_SUCCESS = "00";

	/**
	 * 네이버 유저 정보 조회
	 * 로그인 후 네이버 API를 통해 유저 정보를 조회하고,
	 * 유저 정보를 저장하며 JWT를 생성하여 반환
	 * 
	 * @param request HttpServletRequest
	 * @param accessToken 네이버 API 접근 토큰
	 * @param expiresIn 토큰 만료 시간(초 단위)
	 * @param refreshToken 리프레시 토큰
	 * @return 유저 정보 응답 오브젝트
	 */
	public Mono<ResponseEntity<LoginUserResponseDto>> getNaverUserInfo(
			HttpServletRequest request,
			String accessToken,
			int expiresIn,
			String refreshToken
			) {

		// 회원 프로필 조회 API 조회
		return naverWebClient.get()
				.uri(naverUserInfoUrl)
				.header(HttpHeaders.AUTHORIZATION, CommonConstants.AUTHORIZATION_HEADER_PREFIX.concat(accessToken))
				.retrieve()
				.bodyToMono(NaverProfileResultDto.class)
				.flatMap(response -> {

					// 프로필 조회 API 응답 코드가 성공이 아닌 경우 예외 처리
					if (!StringUtils.equals(response.getResultcode(), PROFILE_API_SUCCESS)) {
						// 프로필 조회 API 에러 발생시 처리
						Integer errorCode = NaverProfileErrorEnum.getNaverProfileError(response.getResultcode()).getHttpErrorCode();
						throw new ResponseStatusException(HttpStatus.valueOf(errorCode), response.getMessage());
					}

					// 프로필 정보
					NaverProfileDataDto profile = response.getResponse();
					String provider = LoginProviderEnum.NAVER.getProvider();
					String providerId = profile.getId();
					// 유저 서비스 파라미터 설정
					LoginUserServiceDto userServiceDto = mapper.profileDataDtoToUserServiceDto(profile);
					userServiceDto.setProvider(provider);

					// user 등록 확인 후 등록
					return Mono.fromCallable(() -> service.saveUser(userServiceDto))
							.map(saveResponse -> {
								// JWT 및 만료일자 생성
								JwtCreationRecord jwtRecord = loginHelper.createJwt(expiresIn, providerId, provider, userServiceDto);
								String jwt = jwtRecord.jwt();
								String expireDateStr = jwtRecord.expireDateStr();

								// 유저 프로필 정보 매핑
								LoginUserInfoDto userInfo = mapper.profileDataDtoToProfileDataDto(profile);
								// 유저 테이블 PK 설정
								userInfo.setUserId(saveResponse.getUserId());
								// provider 설정
								userInfo.setProvider(provider);
								// 결과값 설정
								LoginUserResponseDto userResponse = LoginUserResponseDto.builder()
										.resultcode(response.getResultcode())
										.message(response.getMessage())
										.userInfo(userInfo)
										.accessToken(accessToken)
										.jwt(jwt)
										.expireDate(expireDateStr)
										.build();

								// 파라미터에 리프레시 토큰이 존재하는 경우(쿠키가 없는 경우) 헤더에 쿠키설정
								if (StringUtils.isNotEmpty(refreshToken)) {
									// 로그인 쿠키 설정
									String[] cookieArray = loginHelper
											.setLoginCookies(request, refreshToken, provider, providerId, naverExpiresIn);
									
									// 헤더에 쿠키 설정 후 응답 반환
									return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookieArray).body(userResponse);
								} 
								// 파라미터에 리프레시 토큰이 존재하지 않는 경우(쿠키가 이미 있는 경우) 헤더 미설정
								return ResponseEntity.ok().body(userResponse);
							});
				});
	}

	/**
	 * 카카오 유저 정보 조회
	 * 로그인 후 카카오 API를 통해 유저 정보를 조회하고,
	 * 유저 정보를 저장하며 JWT를 생성하여 반환
	 * 
	 * @param request HttpServletRequest
	 * @param accessToken 카카오 API 접근 토큰
	 * @param expiresIn 토큰 만료 시간(초 단위)
	 * @param refreshToken 리프레시 토큰
	 * @return 유저 정보 응답 오브젝트
	 */
	public Mono<ResponseEntity<LoginUserResponseDto>> getKakaoUserInfo(
			HttpServletRequest request, 
			String accessToken,
			int expiresIn,
			String refreshToken
			) {

		// 유저 정보 가져오기 API 조회
		return kakaoWebClient.get()
				.uri(kakaoUserInfoUrl)
				.header(HttpHeaders.AUTHORIZATION, CommonConstants.AUTHORIZATION_HEADER_PREFIX.concat(accessToken))
				.retrieve()
				.bodyToMono(KakaoUserInfoDto.class)
				.flatMap(response -> {
					// 프로필 정보가 존재하지 않을 경우, 400 에러
					if (ObjectUtils.isEmpty(response.getKakaoAccount())
							|| ObjectUtils.isEmpty(response.getKakaoAccount().getProfile())) {
						throw new ResponseStatusException(
								HttpStatus.BAD_REQUEST, messageUtil.getMessageKO(DomainMessagesErrorEnum.ERROR_LOGIN_NOT_FOUND_PROFILE.getMessageCode()));
					}

					// 카카오 프로필
					KakaoProfileDto profile = response.getKakaoAccount().getProfile();
					// ID
					String providerId = response.getId().toString();
					// Provider
					String provider = LoginProviderEnum.KAKAO.getProvider();
					// 유저 서비스 파라미터 설정
					LoginUserServiceDto userServiceDto = 
							LoginUserServiceDto.builder()
							.provider(provider)
							.providerId(providerId)
							.nickname(profile.getNickname())
							.build();

					// user 등록 확인 후 등록
					return Mono.fromCallable(() -> service.saveUser(userServiceDto))
							.map(saveResponse -> {
								// JWT 및 만료일자 생성
								JwtCreationRecord jwtRecord = loginHelper.createJwt(expiresIn, providerId, provider, userServiceDto);
								String jwt = jwtRecord.jwt();
								String expireDateStr = jwtRecord.expireDateStr();
								// 유저 프로필 정보 매핑
								LoginUserInfoDto userInfo = LoginUserInfoDto.builder()
										.userId(saveResponse.getUserId())
										.provider(provider)
										.id(providerId)
										.nickname(profile.getNickname())
										.build();
								// 결과값 설정
								LoginUserResponseDto userResponse = LoginUserResponseDto.builder()
										.resultcode(PROFILE_API_SUCCESS)
										.message("")
										.userInfo(userInfo)
										.accessToken(accessToken)
										.jwt(jwt)
										.expireDate(expireDateStr)
										.build();

								// 파라미터에 쿠키가 존재하는 경우(쿠키가 없는 경우) 헤더에 쿠키설정
								if (StringUtils.isNotEmpty(refreshToken)) {
									// 로그인 쿠키 설정
									String[] cookieArray = loginHelper.setLoginCookies(request, refreshToken, provider, providerId, kakaoExpiresIn);
									
									// 헤더에 쿠키 설정 후 응답 반환
									return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookieArray).body(userResponse);
								}
								// 파라미터에 쿠키가 존재하지 않는 경우(쿠키가 이미 있는 경우) 헤더 미설정
								return ResponseEntity.ok().body(userResponse);
							});
				});
	}

}
