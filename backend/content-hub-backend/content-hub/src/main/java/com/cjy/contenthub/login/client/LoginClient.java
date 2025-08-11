package com.cjy.contenthub.login.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.cjy.contenthub.common.api.dto.kakao.KakaoProfileDto;
import com.cjy.contenthub.common.api.dto.kakao.KakaoUserDetails;
import com.cjy.contenthub.common.api.dto.kakao.KakaoUserInfoDto;
import com.cjy.contenthub.common.api.dto.naver.NaverProfileDataDto;
import com.cjy.contenthub.common.api.dto.naver.NaverProfileResultDto;
import com.cjy.contenthub.common.api.dto.naver.NaverUserDetails;
import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.LoginProviderEnum;
import com.cjy.contenthub.common.constants.CommonEnum.NaverProfileErrorEnum;
import com.cjy.contenthub.common.util.JwtUtil;
import com.cjy.contenthub.login.controller.dto.LoginUserInfoDto;
import com.cjy.contenthub.login.controller.dto.LoginUserResponseDto;
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

	/** 네이버 API WebClient */
	@Qualifier("naverWebClient")
	private final WebClient naverWebClient;

	/** 카카오 API WebClient */
	@Qualifier("kakaoWebClient")
	private final WebClient kakaoWebClient;

	/** 네이버 API 유저 정보 조회 URL */
	@Value("${login.naver.userInfoUrl}")
	private String naverUserInfoUrl;

	/** 카카오 API 유저 정보 조회 URL */
	@Value("${login.kakao.userInfoUrl}")
	private String kakaoUserInfoUrl;

	/** 로그인 서비스 */
	private final LoginService service;

	/** JWT 유틸리티 */
	private final JwtUtil jwtUtil;

	/** 네이버 API 클라이언트 ID */
	@Value("${login.naver.clientId}")
	private String clientId;

	/** 네이버 API 클라이언트 시크릿 */
	@Value("${login.naver.clientSecret}")
	private String clientSecret;

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
	 * @param cookieArray 쿠키 배열
	 * @return Mono<ResponseEntity<LoginUserResponseDto>> 유저 정보 응답
	 */
	public Mono<ResponseEntity<LoginUserResponseDto>> getNaverUserInfo(
			HttpServletRequest request,
			String accessToken,
			int expiresIn,
			String[] cookieArray
			) {
		
//		Mono.just(response)
//		.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
//		.flatMap(context -> {
//
//		});

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
						throw new ResponseStatusException(
								ObjectUtils.isNotEmpty(errorCode) ? HttpStatus.valueOf(errorCode) : null,
										response.getMessage());
					}
					
					// 프로필 정보
					NaverProfileDataDto profile = response.getResponse();
					// 유저 서비스 파라미터 설정
					LoginUserServiceDto userServiceDto = mapper.profileDataDtoToUserServiceDto(profile);
					userServiceDto.setProvider(LoginProviderEnum.NAVER.getProvider());

					// user 등록 확인 후 등록
					// Mono.fromRunnable()은 비동기 작업을 수행하고, 처리가 완료되면 Mono<Void>를 반환
					return Mono.fromRunnable(() -> service.saveUser(userServiceDto))
							.then(Mono.fromSupplier(() -> {

								// 유저 정보 설정
								NaverUserDetails userDetails = new NaverUserDetails(profile);

								// 권한 정보 설정 후 SecurityContextHolder에 저장
								Authentication auth = new UsernamePasswordAuthenticationToken(
										userDetails, null, userDetails.getAuthorities());
								SecurityContextHolder.getContext().setAuthentication(auth);

								// 세션에 유저 정보 저장
								request.getSession().setAttribute(LoginProviderEnum.NAVER.getProviderUser(), userDetails);

								String jwt;
								Date expireDate;
								try {
									// 현재시각 설정
									Date currentDate = Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
									// 만료시각 설정
									Calendar calendar = Calendar.getInstance();
									calendar.setTime(currentDate);
									calendar.add(Calendar.SECOND, expiresIn);
									expireDate = calendar.getTime();
									// jwt 생성
									jwt = jwtUtil.createToken(profile.getId(), LoginProviderEnum.NAVER.getProvider(), profile.getNickname(), currentDate, expireDate);
								} catch (ParseException ex) {
									throw new IllegalStateException("create JWT error!", ex);
								}

								// 만료시각 변환(Date -> String)
								SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DATE_FORMAT_YYYYMMDDHHMMSS);
								String expireDateStr = sdf.format(expireDate);

								// 유저 프로필 정보 매핑
								LoginUserInfoDto userInfo = mapper.profileDataDtoToProfileDataDto(profile);
								// 결과값 설정
								LoginUserResponseDto userResponse = LoginUserResponseDto.builder()
										.resultcode(response.getResultcode())
										.message(response.getMessage())
										.userInfo(userInfo)
										.accessToken(accessToken)
										.jwt(jwt)
										.expireDate(expireDateStr)
										.build();
								if (ObjectUtils.isNotEmpty(cookieArray) && cookieArray.length != 0) {
									// 파라미터에 쿠키가 존재하는 경우(쿠키가 없는 경우) 헤더에 쿠키설정(토큰 발급시) 
									return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookieArray).body(userResponse);
								} else {
									// 파라미터에 쿠키가 존재하지 않는 경우(이미 있는 경우) 헤더 미설정(토큰 갱신시)
									return ResponseEntity.ok().body(userResponse);
								}
							}));
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
	 * @param cookieArray 쿠키 배열
	 * @return Mono<ResponseEntity<LoginUserResponseDto>> 유저 정보 응답
	 */
	public Mono<ResponseEntity<LoginUserResponseDto>> getKakaoUserInfo(
			HttpServletRequest request, 
			String accessToken,
			int expiresIn,
			String[] cookieArray
			) {

		// 유저 정보 가져오기 API 조회
		return kakaoWebClient.get()
				.uri(kakaoUserInfoUrl)
				.headers(header -> 
					header.set(HttpHeaders.AUTHORIZATION, CommonConstants.AUTHORIZATION_HEADER_PREFIX.concat(accessToken))
				)
				.retrieve()
				.bodyToMono(KakaoUserInfoDto.class)
				.flatMap(response -> {

					// 프로필 정보가 존재하지 않을 경우, 400 에러
					if (ObjectUtils.isEmpty(response.getKakaoAccount())
							|| ObjectUtils.isEmpty(response.getKakaoAccount().getProfile())) {
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile is Empty");
					}

					// 카카오 프로필
					KakaoProfileDto profile = response.getKakaoAccount().getProfile();
					// ID
					String userId = response.getId().toString();
					// 유저 서비스 파라미터 설정
					LoginUserServiceDto userServiceDto = 
							LoginUserServiceDto.builder()
							.providerId(userId)
							.provider(LoginProviderEnum.KAKAO.getProvider())
							.nickname(profile.getNickname())
							.build();

					// user 등록 확인 후 등록
					// Mono.fromRunnable()은 비동기 작업을 수행하고, 처리가 완료되면 Mono<Void>를 반환
					// Mono.fromSupplier()는 공급자에서 값을 가져와 Mono로 감싸 반환
					return Mono.fromRunnable(() -> service.saveUser(userServiceDto))
							.then(Mono.fromSupplier(() -> {

								// 유저 정보 설정
								KakaoUserDetails userDetails = new KakaoUserDetails(response);

								// 권한 정보 설정 후 SecurityContextHolder에 저장
								Authentication auth = new UsernamePasswordAuthenticationToken(
										userDetails, null, userDetails.getAuthorities());
								SecurityContextHolder.getContext().setAuthentication(auth);

								// 세션에 유저 정보 저장
								request.getSession().setAttribute(LoginProviderEnum.KAKAO.getProviderUser(), userDetails);

								String jwt;
								Date expireDate;
								try {
									// 현재시각 설정
									Date currentDate = Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
									// 만료시각 설정
									Calendar calendar = Calendar.getInstance();
									calendar.setTime(currentDate);
									calendar.add(Calendar.SECOND, expiresIn);
									expireDate = calendar.getTime();
									// jwt 생성
									jwt = jwtUtil.createToken(userId, LoginProviderEnum.KAKAO.getProvider(), profile.getNickname(), currentDate, expireDate);
								} catch (ParseException ex) {
									throw new IllegalStateException("create JWT error!", ex);
								}
								// 유저 프로필 정보 매핑
								LoginUserInfoDto userInfo = LoginUserInfoDto.builder()
										.id(userId)
										.nickname(profile.getNickname())
										.build();

								// 만료시각 변환(Date -> String)
								SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DATE_FORMAT_YYYYMMDDHHMMSS);
								String expireDateStr = sdf.format(expireDate);
								
								// 결과값 설정
								LoginUserResponseDto userResponse = LoginUserResponseDto.builder()
										.resultcode(PROFILE_API_SUCCESS)
										.message("")
										.userInfo(userInfo)
										.accessToken(accessToken)
										.jwt(jwt)
										.expireDate(expireDateStr)
										.build();

								if (ObjectUtils.isNotEmpty(cookieArray) && cookieArray.length != 0) {
									// 파라미터에 쿠키가 존재하는 경우(쿠키가 없는 경우) 헤더에 쿠키설정(토큰 발급시) 
									return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookieArray).body(userResponse);
								} else {
									// 파라미터에 쿠키가 존재하지 않는 경우(이미 있는 경우) 헤더 미설정(토큰 갱신시)
									return ResponseEntity.ok().body(userResponse);
								}
							}));
				});
	}

}
