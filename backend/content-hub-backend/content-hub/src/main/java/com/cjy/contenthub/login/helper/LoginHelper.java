package com.cjy.contenthub.login.helper;

import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMessagesErrorEnum;
import com.cjy.contenthub.common.exception.CommonBusinessException;
import com.cjy.contenthub.common.record.CommonRecords.LoginCookiesRecord;
import com.cjy.contenthub.common.util.CookieUtil;
import com.cjy.contenthub.common.util.JwtUtil;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.common.util.RedisUtil;
import com.cjy.contenthub.core.constants.DomainEnum.DomainMessagesErrorEnum;
import com.cjy.contenthub.core.repository.UserRepository;
import com.cjy.contenthub.core.repository.entity.UserEntity;
import com.cjy.contenthub.login.service.dto.LoginUserServiceDto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * 로그인 관련 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
public class LoginHelper {
	
	/** 유저 리포지토리 */
	private final UserRepository userRepository;
	
	/** Redis 유틸 */
	private final RedisUtil redisUtil;
	
	/** 쿠키 유틸 */
	private final CookieUtil cookieUtil;
	
	/** JWT 유틸리티 */
	private final JwtUtil jwtUtil;
	
	/** 메시지 유틸 */
	private final MessageUtil messageUtil;
	
	/** JWT 생성 결과 레코드 */
	public record JwtCreationRecord(String jwt, String expireDateStr) {}
	
	/**
	 * 유저 상태 갱신
	 * 
	 * @param userId 유저 ID
	 * @param status 유저 상태
	 */
	public void updateUserStatus(Long userId, String status) {
		
		// 유저 정보 조회
		Optional<UserEntity> userInfo = userRepository.findById(userId);
		
		// 유저 정보가 존재하지 않는 경우 예외 처리
		if (!userInfo.isPresent()) {
			throw new CommonBusinessException(
					messageUtil.getMessageKO(DomainMessagesErrorEnum.ERROR_LOGIN_NOT_FOUND_USER.getMessageCode()));
		}
		
		// 유저 상태를 LOGOUT으로 변경
		userInfo.get().setStatus(status);
		
		// 테이블에 등록(갱신)
		userRepository.save(userInfo.get());
	}
	
	/**
	 * JWT 생성
	 * 
	 * @param expiresIn  만료 시간(초)
	 * @param providerId 로그인 제공자 유저 ID
	 * @param provider   로그인 제공자
	 * @param profile    로그인 유저 서비스 DTO
	 * @return JWT 생성 결과 레코드
	 */
	public JwtCreationRecord createJwt(int expiresIn, String providerId, String provider,
			LoginUserServiceDto profile) {
		try {
			// 만료 시각 계산
			Instant now = Instant.now();                         // UTC 기준 현재
			Instant expiryInstant = now.plusSeconds(expiresIn);  // 만료 Instant
			// 현재시각 설정
			Date currentDate = Date.from(now);
			// 만료시각 설정
			Date expireDate = Date.from(expiryInstant);
			// ISO8601 UTC 문자열
			String expireDateStr = DateTimeFormatter.ISO_INSTANT.format(expiryInstant);
			// jwt 생성
			String jwt = jwtUtil.createToken(providerId, provider, profile.getNickname(), currentDate, expireDate);
			// 결과 반환
			return new JwtCreationRecord(jwt, expireDateStr);
		} catch (ParseException ex) {
			throw new IllegalStateException(
					messageUtil.getMessageKO(CommonMessagesErrorEnum.ERROR_COMMON_JWT_CREATION.getMessageCode()), ex);
		}
	}
	
	/**
	 * 로그인 쿠키 설정
	 * 
	 * @param request      HTTP 요청
	 * @param refreshToken 리프레시 토큰
	 * @param provider     로그인 제공자
	 * @param providerId   로그인 제공자 유저 ID
	 * @param expiresIn    만료 시간(초)
	 * @return 로그인 쿠키 배열
	 */
	public String[] setLoginCookies(HttpServletRequest request, String refreshToken, String provider, String providerId, long expiresIn) {
		// 디바이스 ID 조회
		String deviceId = cookieUtil.getCookieValue(request, CommonConstants.DEVICE_ID);
		// refresh token을 redis에 저장
		redisUtil.saveRefreshToken(provider, providerId,
				refreshToken, deviceId, expiresIn);
		// 로그인 쿠키 설정
		LoginCookiesRecord loginCookies = cookieUtil.getLoginCookiesForRegister(refreshToken, provider, expiresIn);
		// 쿠키 배열 생성
		return new String[] {
				loginCookies.refreshToken(), 
				loginCookies.provider()
		};
	}
	
}
