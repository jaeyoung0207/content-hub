package com.cjy.contenthub.login.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cjy.contenthub.common.integration.naver.dto.NaverProfileDataDto;
import com.cjy.contenthub.core.repository.entity.UserEntity;
import com.cjy.contenthub.login.controller.dto.LoginUserInfoDto;
import com.cjy.contenthub.login.service.dto.LoginUserServiceDto;

/**
 * 로그인 관련 데이터 매핑 인터페이스
 */
@Mapper(componentModel = "spring")
public interface LoginMapper {

	/**
	 * NaverProfileDataDto를 LoginUserServiceDto로 변환
	 * 
	 * @param requestDto NaverProfileDataDto
	 */
	@Mapping(target = "providerId", source = "id")
	LoginUserServiceDto profileDataDtoToUserServiceDto(NaverProfileDataDto requestDto);

	/**
	 * LoginUserServiceDto를 UserEntity로 변환
	 * 
	 * @param serviceDto LoginUserServiceDto
	 */
	UserEntity userServiceDtoToUserEntity(LoginUserServiceDto serviceDto);
	
	/**
	 * UserEntity를 LoginUserServiceDto로 변환
	 * 
	 * @param userEntity UserEntity
	 */
	LoginUserServiceDto userEntityToUserServiceDto(UserEntity userEntity);
	
	/**
	 * NaverProfileDataDto를 LoginUserInfoDto로 변환
	 * 
	 * @param userEntity UserEntity
	 */
	LoginUserInfoDto profileDataDtoToProfileDataDto(NaverProfileDataDto requestDto);
	
}
