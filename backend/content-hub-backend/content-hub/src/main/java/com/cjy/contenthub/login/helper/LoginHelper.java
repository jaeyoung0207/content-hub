package com.cjy.contenthub.login.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.MessagesErrorEnum;
import com.cjy.contenthub.common.exception.CommonBusinessException;
import com.cjy.contenthub.common.repository.UserRepository;
import com.cjy.contenthub.common.repository.entity.UserEntity;
import com.cjy.contenthub.common.util.MessageUtil;

import jakarta.servlet.http.Cookie;
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
	
	/** 메시지 유틸 */
	private final MessageUtil messageUtil;

	/**
	 * 쿠키에서 로그인 유저의 프로바이더 정보를 추출
	 *
	 * @param request HttpServletRequest
	 * @param provider 로그인 제공자
	 * @return 리프레시 토큰
	 */
	public String getRefreshToken(HttpServletRequest request, String provider) {
		// 쿠키 추출
		String refreshToken = null;
		Cookie[] cookies = request.getCookies();
		// 쿠키가 존재하는 경우
		if (cookies != null) {
			List<Cookie> cookieList = new ArrayList<>();
			if (Arrays.stream(cookies)
					.anyMatch(c -> 
					StringUtils.equals(c.getName(), CommonConstants.PROVIDER) && // 쿠키 이름이 PROVIDER이고
					StringUtils.equals(c.getValue(), provider)) // 쿠키 값이 provider 파라미터 값과 일치하는 경우
					) {
				// 쿠키이름이 리프레시 토큰인 쿠키 추출
				cookieList = Arrays.stream(cookies)
						.filter(c -> StringUtils.equals(c.getName(), CommonConstants.REFRESH_TOKEN))
						.toList();
			}
			// 쿠키가 존재하는 경우
			if (!ObjectUtils.isEmpty(cookieList)) {
				// 리프레시 토큰 값 추출
				refreshToken = cookieList.get(0).getValue();
			}
		}
		// 리프레시 토큰 반환
		return refreshToken;
	}
	
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
					messageUtil.getMessageKO(MessagesErrorEnum.ERROR_LOGIN_NOT_FOUND_USER.getMessageCode()));
		}
		
		// 유저 상태를 LOGOUT으로 변경
		userInfo.get().setStatus(status);
		
		// 테이블에 등록(갱신)
		userRepository.save(userInfo.get());
	}
	
}
