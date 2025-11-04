package com.cjy.contenthub.common.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Actuator 보안 설정 클래스
 */
@Configuration
public class SecurityActuatorConfig {
	
	  /**
	   * Actuator 보안 필터 체인 설정
	   * 
	   * @param http HttpSecurity
	   * @return SecurityFilterChain 인스턴스
	   * @throws Exception
	   */
	  @Bean
	  @Order(0) // 우선순위를 높게(0) 주어 Actuator 체인이 먼저 매칭되도록 함
	  SecurityFilterChain actuatorSecurity(HttpSecurity http) throws Exception {
	    http
	      // Actuator 엔드포인트에만 이 체인을 적용
	      .securityMatcher(EndpointRequest.toAnyEndpoint())
	      .authorizeHttpRequests(auth -> auth
	        // health/info는 공개(프로브/헬스체커 용도)
	        .requestMatchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class)).permitAll()
	        // 그 외 모든 Actuator 엔드포인트는 ROLE_ACTUATOR 필요
	        .anyRequest().hasRole("ACTUATOR")
	      )
	      // Actuator는 Basic 인증 사용
	      .httpBasic(Customizer.withDefaults())
	      // CSRF는 비활성
	      .csrf(csrf -> csrf.disable())
	      // 세션은 만들지 않음
	      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
	    // 보안 필터 체인 빌드 및 반환
	    return http.build();
	  }

}
