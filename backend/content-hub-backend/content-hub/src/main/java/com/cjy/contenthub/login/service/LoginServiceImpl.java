package com.cjy.contenthub.login.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjy.contenthub.common.repository.UserRepository;
import com.cjy.contenthub.common.repository.entity.UserEntity;
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
	private final UserRepository repository;
	
	/** 로그인 매퍼 */
	private final LoginMapper mapper;

	/**
	 * 로그인 유저 정보 저장
	 *
	 * @param loginUserServiceDto 로그인 유저 서비스 DTO
	 */
	@Override
	public void saveUser(LoginUserServiceDto loginUserServiceDto) {
		
		// provider, providerId에 해당하는 유저정보가 등록되어 있는지 확인
		boolean isSaved = repository.existsByProviderAndProviderId(loginUserServiceDto.getProvider(), loginUserServiceDto.getProviderId());
		
		// 유저 정보가 등록되어 있지 않은 경우
		if (!isSaved) {
			// LoginUserServiceDto -> UserEntity 매핑
			UserEntity userEntity = mapper.userServiceDtoToUserEntity(loginUserServiceDto);
			// 유저 정보 등록
			repository.save(userEntity);
		}
	}

}
