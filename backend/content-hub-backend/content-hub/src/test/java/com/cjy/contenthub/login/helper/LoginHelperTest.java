package com.cjy.contenthub.login.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.exception.CommonBusinessException;
import com.cjy.contenthub.common.record.CommonRecords.LoginCookiesRecord;
import com.cjy.contenthub.common.util.CookieUtil;
import com.cjy.contenthub.common.util.JwtUtil;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.common.util.RedisUtil;
import com.cjy.contenthub.core.repository.UserRepository;
import com.cjy.contenthub.core.repository.entity.UserEntity;
import com.cjy.contenthub.login.helper.LoginHelper.JwtCreationRecord;
import com.cjy.contenthub.login.service.dto.LoginUserServiceDto;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class LoginHelperTest {
	
	@InjectMocks
	LoginHelper helper;
	
	@Mock
	UserRepository userRepository;

	@Mock
	RedisUtil redisUtil;
	
	@Mock
	CookieUtil cookieUtil;
	
	@Mock
	JwtUtil jwtUtil;
	
	@Mock
	MessageUtil messageUtil;
	
	@Mock
	HttpServletRequest request;
	
	@Test
	@DisplayName("[UT]updateUserStatus: 유저 상태 갱신 - 유저 정보 존재")
	void test_updateUserStatus_existUserInfo() {
		
		Long userId = 1L;
		String status = "0";
		
		UserEntity userEntity = UserEntity.builder()
				.userId(userId)
				.status("1")
				.build();
		when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
		userEntity.setStatus(status);
		
		// 실제 메서드 호출
		helper.updateUserStatus(userId, status);
		
		// 검증
		assertThat(userEntity.getStatus()).isEqualTo(status);
		verify(userRepository, times(1)).findById(userId);
		verify(userRepository, times(1)).save(any(UserEntity.class));
	}
	
	@Test
	@DisplayName("[UT]updateUserStatus: 유저 상태 갱신 - 유저 정보 없음")
	void test_updateUserStatus_notExistUserInfo() {
		
		Long userId = 1L;
		String status = "0";
		
		when(userRepository.findById(userId)).thenReturn(Optional.empty());
		String errorMessage = "유저 정보를 찾을 수 없습니다.";
		when(messageUtil.getMessageKO(anyString())).thenReturn(errorMessage);
		
		// 실제 메서드 호출 및 예외 검증
		assertThatThrownBy(() -> 
            helper.updateUserStatus(userId, status))
		.isInstanceOf(CommonBusinessException.class)
		.hasMessage(errorMessage);
		
		// 검증
		verify(userRepository, times(1)).findById(userId);
	}
	
	@Test
	@DisplayName("[UT]createJwt: JWT 생성 - JWT 정상 생성")
	void test_createJwt_success() throws ParseException {
		
		int expiresIn = 3600;
		String providerId = "providerId";
		String provider = "provider";
		LoginUserServiceDto profile = LoginUserServiceDto.builder()
                .nickname("nickname")
                .build();
		String jwt = "JWT";
		when(jwtUtil.createToken(eq(providerId), eq(provider), eq(profile.getNickname()), any(), any()))
				.thenReturn(jwt);
		
		// 실제 메서드 호출
		JwtCreationRecord result = helper.createJwt(expiresIn, providerId, provider, profile);
		
		// 검증
		assertThat(result.jwt()).isEqualTo(jwt);
		
		verify(jwtUtil, times(1)).createToken(eq(providerId), eq(provider), eq(profile.getNickname()), any(), any());
	}
	
	@Test
	@DisplayName("[UT]createJwt: JWT 생성 - JWT 생성 실패")
	void test_createJwt_failure() throws ParseException {
		
		int expiresIn = 3600;
		String provider = "NAVER";
		String providerId = "providerId";
		LoginUserServiceDto profile = LoginUserServiceDto.builder()
                .nickname("nickname")
                .build();
		String errorMessage = "JWT 토큰 생성 에러";
		when(jwtUtil.createToken(eq(providerId), eq(provider), eq(profile.getNickname()), any(), any()))
				.thenThrow(new ParseException(errorMessage, 0));
		
		// 실제 메서드 호출
		assertThatThrownBy(() -> 
			helper.createJwt(expiresIn, providerId, provider, profile))
		.isInstanceOf(IllegalStateException.class)
		.hasRootCauseMessage(errorMessage);
		
		verify(jwtUtil, times(1)).createToken(eq(providerId), eq(provider), eq(profile.getNickname()), any(), any());
	}
	
	@Test
	@DisplayName("[UT]setLoginCookies: 로그인 쿠키 설정")
	void test_setLoginCookies() {
		
		String refreshToken = "refresh token";
		String provider = "KAKAO";
		String providerId = "providerId";
		int expiresIn = 3600;
		
		String deviceId = "deviceId";
		when(cookieUtil.getCookieValue(request, CommonConstants.DEVICE_ID)).thenReturn(deviceId);
		
		String[] expectedCookies = new String[] { refreshToken, provider };
		when(cookieUtil.getLoginCookiesForRegister(refreshToken, provider, expiresIn)).thenReturn(
				new LoginCookiesRecord(provider, refreshToken));
		
		// 실제 메서드 호출
		String[] resultCookies = helper.setLoginCookies(request, refreshToken, provider, providerId, expiresIn);
		
		// 검증
		assertThat(resultCookies).isEqualTo(expectedCookies);
		
		verify(cookieUtil, times(1)).getCookieValue(request, CommonConstants.DEVICE_ID);
	}

}
