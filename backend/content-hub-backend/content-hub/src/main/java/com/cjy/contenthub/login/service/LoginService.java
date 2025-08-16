package com.cjy.contenthub.login.service;

import com.cjy.contenthub.common.api.dto.kakao.KakaoIssueTokenDto;
import com.cjy.contenthub.common.api.dto.kakao.KakaoUserInfoDto;
import com.cjy.contenthub.common.api.dto.naver.NaverDeleteTokenDto;
import com.cjy.contenthub.common.api.dto.naver.NaverIssueTokenDto;
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
	void saveUser(LoginUserServiceDto loginUserServiceDto);
	
	/**
	 * 네이버 로그인 토큰 발행
	 *
	 * @param userId 유저 ID
	 * @return 네이버 토큰 발행 DTO
	 */
	NaverIssueTokenDto getNaverIssueToken(String code, String state);
	
	/**
	 * 네이버 로그인 토큰 갱신
	 *
	 * @param code  인증 코드
	 * @param state 상태 값
	 * @return 네이버 토큰 발행 DTO
	 */
	NaverIssueTokenDto getNaverUpdateToken(String refreshToken);
	
	
	/**
	 * 네이버 로그인 토큰 삭제
	 *
	 * @param accessToken 액세스 토큰
	 * @return 네이버 토큰 삭제 DTO
	 */
	NaverDeleteTokenDto deleteNaverToken(String accessToken);
	
	/**
	 * 카카오 로그인 토큰 발행
	 *
	 * @param clientId    클라이언트 ID
	 * @param redirectUri 리다이렉트 URI
	 * @param code        인증 코드
	 * @return 카카오 토큰 발행 DTO
	 */
	KakaoIssueTokenDto getKakaoIssueToken(String clientId, String redirectUri, String code);
	
	/**
	 * 카카오 로그인 토큰 갱신
	 *
	 * @param clientId 클라이언트 ID
	 * @param refreshTokenFromCookie 리프레시 토큰
	 * @return 카카오 토큰 발행 DTO
	 */
	KakaoIssueTokenDto updateKakaoLoginInfo(String clientId, String refreshTokenFromCookie);
	
	/**
	 * 카카오 로그인 토큰 삭제
	 *
	 * @param accessToken 액세스 토큰
	 * @param targetId    타겟 ID
	 * @return 카카오 유저 정보 DTO
	 */
	KakaoUserInfoDto deleteKakaoToken(String accessToken, String targetId);

}
