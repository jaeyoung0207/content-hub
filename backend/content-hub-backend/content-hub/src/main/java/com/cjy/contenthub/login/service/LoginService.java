package com.cjy.contenthub.login.service;

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

}
