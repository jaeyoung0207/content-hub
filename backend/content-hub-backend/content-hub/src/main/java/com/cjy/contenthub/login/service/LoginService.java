package com.cjy.contenthub.login.service;

import java.util.concurrent.CompletableFuture;

import com.cjy.contenthub.common.integration.kakao.dto.KakaoIssueTokenDto;
import com.cjy.contenthub.common.integration.kakao.dto.KakaoUserInfoDto;
import com.cjy.contenthub.common.integration.naver.dto.NaverDeleteTokenDto;
import com.cjy.contenthub.common.integration.naver.dto.NaverIssueTokenDto;
import com.cjy.contenthub.login.service.dto.LoginUserServiceDto;

/**
 * 로그인 서비스 인터페이스
 */
public interface LoginService {

	/**
	 * 로그인 유저 정보 저장
	 *
	 * @param loginUserServiceDto 로그인 유저 서비스 DTO
	 */
	LoginUserServiceDto saveUser(LoginUserServiceDto loginUserServiceDto);
	
	/**
	 * 네이버 로그인 토큰 발행
	 *
	 * @param code 인증 코드
	 * @param state 상태 값
	 * @return 네이버 토큰 발행 DTO
	 */
	CompletableFuture<NaverIssueTokenDto> getNaverIssueToken(String code, String state);
	
	/**
	 * 네이버 로그인 토큰 갱신
	 *
	 * @param refreshToken 리프레시 토큰
	 * @param deviceId 디바이스 ID
	 * @return 네이버 토큰 발행 DTO
	 */
	CompletableFuture<NaverIssueTokenDto> getNaverUpdateToken(String refreshToken, String deviceId);
	
	/**
	 * 네이버 로그인 토큰 삭제
	 *
	 * @param accessToken 액세스 토큰
	 * @param targetId    타겟 ID
	 * @param userId      유저 테이블 ID
	 * @param refreshToken 리프레시 토큰
	 * @param deviceId 디바이스 ID
	 * @return 네이버 토큰 삭제 DTO
	 */
	CompletableFuture<NaverDeleteTokenDto> deleteNaverToken(String accessToken, String targetId, Long userId, String refreshToken, String deviceId);
	
	/**
	 * 카카오 로그인 토큰 발행
	 *
	 * @param clientId    클라이언트 ID
	 * @param redirectUri 리다이렉트 URI
	 * @param code        인증 코드
	 * @return 카카오 토큰 발행 DTO
	 */
	CompletableFuture<KakaoIssueTokenDto> getKakaoIssueToken(String clientId, String redirectUri, String code);
	
	/**
	 * 카카오 로그인 토큰 갱신
	 *
	 * @param clientId 클라이언트 ID
	 * @param refreshToken 리프레시 토큰
	 * @param deviceId 디바이스 ID
	 * @return 카카오 토큰 발행 DTO
	 */
	CompletableFuture<KakaoIssueTokenDto> updateKakaoLoginInfo(String clientId, String refreshToken, String deviceId);
	
	/**
	 * 카카오 로그인 토큰 삭제
	 *
	 * @param accessToken 액세스 토큰
	 * @param targetId    타겟 ID
	 * @param userId      유저 테이블 ID
	 * @param refreshToken 리프레시 토큰
	 * @param deviceId 디바이스 ID
	 * @return 카카오 유저 정보 DTO
	 */
	CompletableFuture<KakaoUserInfoDto> deleteKakaoToken(String accessToken, String targetId, Long userId, String refreshToken, String deviceId);

}
