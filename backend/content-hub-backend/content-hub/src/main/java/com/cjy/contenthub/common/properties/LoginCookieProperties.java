package com.cjy.contenthub.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 로그인 쿠키 프로퍼티 설정 클래스
 * @ConfigurationProperties 어노테이션의 prefix는 application.yml의 설정 키와 매칭되어야 함
 */
@ConfigurationProperties(prefix = "login.cookie")
@Setter
@Getter
@RequiredArgsConstructor
public class LoginCookieProperties {
	
	/** Http Only */
	private boolean httpOnly;
	
	/** Secure */
	private boolean secure;
	
	/** Same Site */
	private String sameSite;
	
	/** Domain */
	private String domain;
	
	/** Path */
	private String path;

}
