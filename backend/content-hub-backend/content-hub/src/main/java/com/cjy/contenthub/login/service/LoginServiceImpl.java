package com.cjy.contenthub.login.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMessagesErrorEnum;
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

import lombok.RequiredArgsConstructor;

/**
 * 로그인 서비스 구현 클래스
 */
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

	/** 유저 리포지토리 */
	private final UserRepository userRepository;
	
	/** 로그인 헬퍼 */
	private final LoginHelper loginHelper;

	/** 로그인 매퍼 */
	private final LoginMapper loginMapper;
	
	/** Redis 유틸리티 클래스 */
	private final RedisUtil redisUtil;
	
	/** 메시지 유틸리티 클래스 */
	private final MessageUtil messageUtil;

	/** 네이버 API WebClient */
	@Qualifier("naverWebClient")
	private final WebClient naverWebClient;

	/** 카카오 API WebClient */
	@Qualifier("kakaoWebClient")
	private final WebClient kakaoWebClient;

	/** 네이버 클라이언트 ID */
	@Value("${login.naver.api.clientId}")
	private String naverClientId;

	/** 네이버 클라이언트 시크릿 */
	@Value("${login.naver.api.clientSecret}")
	private String naverClientSecret;
	
	/** 카카오 클라이언트 시크릿 */
	@Value("${login.kakao.api.clientSecret}")
	private String kakaoClientSecret;

	/** 네이버 API 토큰 발행 URL */
	@Value("${login.naver.url.tokenIssueUrl}")
	private String naverTokenIssueUrl;

	/** 네이버 API 유저 정보 URL */
	@Value("${login.kakao.url.userInfoUrl}")
	private String kakaoOidcUserInfoUrl;

	/** 카카오 API 토큰 발행 URL */
	@Value("${login.kakao.url.tokenIssueUrl}")
	private String kakaoTokenIssueUrl;

	/** 카카오 API 유저 정보 URL */
	@Value("${login.kakao.url.userInfoUrl}")
	private String kakaoUserInfoUrl;

	/** 카카오 API 로그아웃 URL */
	@Value("${login.kakao.url.logoutUrl}")
	private String kakaoLogoutUrl;

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
	 * 로그인 유저 정보 저장
	 *
	 * @param loginUserServiceDto 로그인 유저 서비스 DTO
	 */
	@Override
	public LoginUserServiceDto saveUser(LoginUserServiceDto loginUserServiceDto) {

		// provider, providerId에 해당하는 유저정보가 등록되어 있는지 확인
		UserEntity userInfo = userRepository.findByProviderAndProviderId(loginUserServiceDto.getProvider(), loginUserServiceDto.getProviderId());

		// 유저 정보가 등록되어 있지 않은 경우
		if (userInfo == null) {
			// LoginUserServiceDto -> UserEntity 매핑
			UserEntity userEntity = loginMapper.userServiceDtoToUserEntity(loginUserServiceDto);
			// 유저 정보 등록
			userInfo = userRepository.save(userEntity);
		} 
		// 유저 정보가 등록되어 있는 경우
		else {
			// 유저 상태를 LOGIN으로 변경
			loginHelper.updateUserStatus(userInfo.getUserId(), LoginStatusEnum.LOGIN.getLoginStatus());
		}
		// UserEntity -> LoginUserServiceDto 매핑 후 리턴
		return loginMapper.userEntityToUserServiceDto(userInfo);
	}

	/**
	 * 네이버 로그인 토큰 발행
	 *
	 * @param code  인증 코드
	 * @param state 상태 값
	 * @return NaverIssueTokenDto
	 */
	@Override
	public NaverIssueTokenDto getNaverIssueToken(String code, String state) {

		// 네이버 토큰 발급 URL 생성
		String uri = UriComponentsBuilder.fromUriString(naverTokenIssueUrl)
				.queryParam(PARAM_GRANT_TYPE, VALUE_AUTHORIZATION_CODE)
				.queryParam(PARAM_CLIENT_ID, naverClientId)
				.queryParam(PARAM_CLIENT_SECRET, naverClientSecret)
				.queryParam(PARAM_CODE, code)
				.queryParam(PARAM_STATE, state)
				.toUriString();

		// 네이버 토큰 발급 API 호출
		return naverWebClient.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(NaverIssueTokenDto.class)
				.block();
	}

	/**
	 * 네이버 로그인 토큰 갱신
	 *
	 * @param refreshToken 리프레시 토큰
	 * @return NaverIssueTokenDto
	 */
	@Override
	public NaverIssueTokenDto getNaverUpdateToken(String refreshToken) {
		
		// 제공자 정보 조회
		ProviderInfo providerInfo = redisUtil.getProviderInfoByRefreshToken(refreshToken);
		
		// 제공자 정보가 없는 경우 예외 처리
		if (providerInfo == null) {
            throw new AccountExpiredException(
                messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_JWT_REFRESH_TOKEN_VALIDATION.getMessageCode()));
        }
		// 리프레시 토큰 검증
		if (!redisUtil.validateRefreshToken(providerInfo.provider(), providerInfo.providerId(), refreshToken)) {
			throw new AccountExpiredException(
					messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_JWT_REFRESH_TOKEN_VALIDATION.getMessageCode()));
		}

		// 네이버 토큰 갱신 URL 생성
		String uri = UriComponentsBuilder.fromUriString(naverTokenIssueUrl)
				.queryParam(PARAM_GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN)
				.queryParam(PARAM_CLIENT_ID, naverClientId)
				.queryParam(PARAM_CLIENT_SECRET, naverClientSecret)
				.queryParam(PARAM_REFRESH_TOKEN, refreshToken)
				.toUriString();

		// 네이버 토큰 갱신 API 호출
		return naverWebClient.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(NaverIssueTokenDto.class)
				.block();
	}

	/**
	 * 네이버 로그인 토큰 삭제
	 *
	 * @param accessToken 액세스 토큰
	 * @param targetId    타겟 ID (제공자 ID)
	 * @param userId      유저 테이블 ID
	 * @return NaverDeleteTokenDto
	 */
	@Override
	public NaverDeleteTokenDto deleteNaverToken(String accessToken, String targetId, Long userId, String refreshToken) {
		
		// Redis에서 리프레시 토큰 및 제공자 정보 삭제
		if (StringUtils.isNotEmpty(refreshToken)) {
			redisUtil.deleteRefreshToken(LoginProviderEnum.NAVER.getProvider(), targetId);
			redisUtil.deleteProviderInfoByRefreshToken(refreshToken);
		}
		
		// 유저 상태를 LOGOUT으로 변경
		loginHelper.updateUserStatus(userId, LoginStatusEnum.LOGOUT.getLoginStatus());

		// 네이버 토큰 삭제 URL 생성
		String uri = UriComponentsBuilder.fromUriString(naverTokenIssueUrl)
				.queryParam(PARAM_GRANT_TYPE, GRANT_TYPE_DELETE)
				.queryParam(PARAM_CLIENT_ID, naverClientId)
				.queryParam(PARAM_CLIENT_SECRET, naverClientSecret)
				.queryParam(PARAM_ACCESS_TOKEN, accessToken)
				.queryParam(PARAM_SERVICE_PROVIDER, LoginProviderEnum.NAVER.getProvider())
				.toUriString();

		// 네이버 토큰 삭제 API 호출
		return naverWebClient.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(NaverDeleteTokenDto.class)
				.block();
	}

	/**
	 * 카카오 로그인 토큰 발행
	 *
	 * @param clientId    클라이언트 ID
	 * @param redirectUri 리다이렉트 URI
	 * @param code        인증 코드
	 * @return 카카오 토큰 발행 DTO
	 */
	@Override
	public KakaoIssueTokenDto getKakaoIssueToken(String clientId, String redirectUri, String code) {

		// 카카오 토큰 발급 URL 생성
		String uri = UriComponentsBuilder.fromUriString(kakaoTokenIssueUrl)
				.queryParam(PARAM_GRANT_TYPE, VALUE_AUTHORIZATION_CODE)
				.queryParam(PARAM_CLIENT_ID, clientId)
				.queryParam(PARAM_REDIRECT_URI, redirectUri)
				.queryParam(PARAM_CODE, code)
				.queryParam(PARAM_CLIENT_SECRET, kakaoClientSecret)
				.toUriString();

		// 카카오 토큰 발급 API 호출
		return kakaoWebClient.post()
				.uri(uri)
				.retrieve()
				.bodyToMono(KakaoIssueTokenDto.class)
				.block();
	}

	/**
	 * 카카오 로그인 토큰 갱신
	 *
	 * @param clientId               클라이언트 ID
	 * @param refreshToken 리프레시 토큰
	 * @return 카카오 토큰 발행 DTO
	 */
	@Override
	public KakaoIssueTokenDto updateKakaoLoginInfo(String clientId, String refreshToken) {
		
		// 제공자 정보 조회
		ProviderInfo providerInfo = redisUtil.getProviderInfoByRefreshToken(refreshToken);
		
		// 제공자 정보가 없는 경우 예외 처리
		if (providerInfo == null) {
			throw new AccountExpiredException(
					messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_JWT_REFRESH_TOKEN_VALIDATION.getMessageCode()));
		}
		// 리프레시 토큰 검증
		if (!redisUtil.validateRefreshToken(providerInfo.provider(), providerInfo.providerId(), refreshToken)) {
			throw new AccountExpiredException(
					messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_JWT_REFRESH_TOKEN_VALIDATION.getMessageCode()));
		}

		// 카카오 토큰 갱신 URL 생성
		String uri = UriComponentsBuilder.fromUriString(kakaoTokenIssueUrl)
				.queryParam(PARAM_GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN)
				.queryParam(PARAM_CLIENT_ID, clientId)
				.queryParam(PARAM_REFRESH_TOKEN, refreshToken)
				.queryParam(PARAM_CLIENT_SECRET, kakaoClientSecret)
				.toUriString();

		// 카카오 토큰 갱신 API 호출		
		return kakaoWebClient.post()
				.uri(uri)
				.retrieve()
				.bodyToMono(KakaoIssueTokenDto.class)
				.block();
	}

	/**
	 * 카카오 로그인 토큰 삭제
	 *
	 * @param accessToken 액세스 토큰
	 * @param targetId    대상 ID (제공자 ID)
	 * @param userId      유저 테이블 ID
	 * @return KakaoUserInfoDto
	 */
	@Override
	public KakaoUserInfoDto deleteKakaoToken(String accessToken, String targetId, Long userId, String refreshToken) {
		
		// Redis에서 리프레시 토큰 및 제공자 정보 삭제
		if (StringUtils.isNotEmpty(refreshToken)) {
			redisUtil.deleteRefreshToken(LoginProviderEnum.KAKAO.getProvider(), targetId);
			redisUtil.deleteProviderInfoByRefreshToken(refreshToken);
		}
		
		// 유저 상태를 LOGOUT으로 변경
		loginHelper.updateUserStatus(userId, LoginStatusEnum.LOGOUT.getLoginStatus());

		// 카카오 토큰 삭제 URL 생성
		String uri = UriComponentsBuilder.fromUriString(kakaoLogoutUrl)
				.queryParam(PARAM_TARGET_ID_TYPE, VALUE_USER_ID)
				.queryParam(PARAM_TARGET_ID, Long.valueOf(targetId))
				.toUriString();

		// 카카오 토큰 삭제 API 호출
		return kakaoWebClient.get()
				.uri(uri)
				.headers(header -> header.set(HttpHeaders.AUTHORIZATION, 
						CommonConstants.AUTHORIZATION_HEADER_PREFIX.concat(accessToken)))
				.retrieve()
				.bodyToMono(KakaoUserInfoDto.class)
				.block();
	}

}
