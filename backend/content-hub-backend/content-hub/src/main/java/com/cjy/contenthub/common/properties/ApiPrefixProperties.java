package com.cjy.contenthub.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import com.cjy.contenthub.common.util.PathUtil;
import com.cjy.contenthub.common.constants.CommonConstants;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * API 접두사 및 버전 설정 프로퍼티 클래스
 * @ConfigurationProperties 어노테이션의 prefix는 application.yml의 설정 키와 매칭되어야 함
 */
@ConfigurationProperties(prefix = "app")
@Setter
@Getter
@RequiredArgsConstructor
@Validated
public class ApiPrefixProperties {
	
	/** API 접두사 */
	@Valid
	@NotNull
	private String prefix = "/api";

	/** API 버전 */
	private String version = "";
	
	/**
	 * 전체 접두사 반환 (접두사 + 버전)
	 * @return 전체 접두사
	 */
	public String getFullPrefix() {
        String fullPrefix = PathUtil.joinPath(prefix, version);
        return fullPrefix.equals(CommonConstants.SLASH) ? "" : fullPrefix;
    }

}
