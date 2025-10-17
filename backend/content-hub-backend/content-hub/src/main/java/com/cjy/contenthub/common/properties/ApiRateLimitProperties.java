package com.cjy.contenthub.common.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * API Rate Limit 프로퍼티 설정 클래스
 * @ConfigurationProperties 어노테이션의 prefix는 application.yml의 설정 키와 매칭되어야 함
 */
@ConfigurationProperties(prefix = "app.rate-limit")
@Setter
@Getter
@RequiredArgsConstructor
@Validated
public class ApiRateLimitProperties {
	
	/** 기본 설정 */
	@Valid
	@NotNull
	private ApiRateLimitDefaults defaults = new ApiRateLimitDefaults();
	
	/** 개별 경로별 설정 리스트 */
	@Valid
	@NotNull
	private List<ApiRateLimitRules> rules = new ArrayList<>();
	
	/**
	 * 기본 설정 클래스
	 */
	@Setter
	@Getter
	@RequiredArgsConstructor
	public static class ApiRateLimitDefaults {
		@Min(1)
		private int maxRequestCount = 60;
		@Min(1)
		private int seconds = 60;
	}
	
	/**
	 * 개별 경로별 설정 클래스
	 */
	@Setter
	@Getter
	@RequiredArgsConstructor
	public static class ApiRateLimitRules {
		@NotBlank
		private String path;
		@Min(1)
		private Integer maxRequestCount;
		@Min(1)
		private Integer seconds;
	}
	

}
