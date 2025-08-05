package com.cjy.contenthub.common.api.dto.kakao;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Kakao 유저 상세 정보
 * Kakao API를 통해 가져온 유저 정보를 Spring Security의 UserDetails로 변환하여 사용
 * 이 DTO는 Kakao API에서 제공하는 유저 정보를 포함하며,
 * Spring Security의 인증 및 권한 부여 시스템에서 사용됨
 */
@Getter
@RequiredArgsConstructor
public class KakaoUserDetails implements UserDetails {
	
	/** 직렬화 ID */
	private static final long serialVersionUID = 1L;
	
	/** Kakao 유저 정보 DTO */
	private final KakaoUserInfoDto profileData;

	/**
	 * 권한 정보를 빈 컬렉션으로 반환 
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	/**
	 * 비밀번호 정보는 Kakao API에서 제공하지 않으므로 null 반환
	 */
	@Override
	public String getPassword() {
		return null;
	}

	/**
	 * 유저 이름(ID) 정보를 반환
	 */
	@Override
	public String getUsername() {
		return profileData.getId().toString();
	}

}
