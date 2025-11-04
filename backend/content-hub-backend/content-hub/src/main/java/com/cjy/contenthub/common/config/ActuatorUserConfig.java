package com.cjy.contenthub.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Actuator 사용자 설정 클래스
 */
@Configuration
public class ActuatorUserConfig {

	/** Actuator 사용자 이름 */
	@Value("${spring.security.user.name}")
	private String username;

	/** Actuator 사용자 비밀번호 */
	@Value("${spring.security.user.password}")
	private String password;

	/** Actuator 사용자 역할 */
	@Value("${spring.security.user.roles}")
	private String roles;

	/**
	 * Actuator 사용자용 인메모리 사용자 상세 서비스 빈 생성
	 *
	 * @return UserDetailsService 인스턴스
	 */
	@Bean
	UserDetailsService actuatorUsers() {
		return new InMemoryUserDetailsManager(
				User.withUsername(username)
				.password(password)
				.roles(roles)
				.build()
				);
	}

}
