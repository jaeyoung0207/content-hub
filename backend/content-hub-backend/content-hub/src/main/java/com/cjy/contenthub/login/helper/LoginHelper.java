package com.cjy.contenthub.login.helper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.cjy.contenthub.common.exception.CommonBusinessException;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.core.constants.DomainEnum.DomainMessagesErrorEnum;
import com.cjy.contenthub.core.repository.UserRepository;
import com.cjy.contenthub.core.repository.entity.UserEntity;

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
	
}
