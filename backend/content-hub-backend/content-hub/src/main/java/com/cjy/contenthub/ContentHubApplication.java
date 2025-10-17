package com.cjy.contenthub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Content Hub Application 메인 클래스
 * @SpringBootApplication: 루트 패키지(com.cjy.contenthub) 이하를 컴포넌트 스캔 대상으로 설정
 * @ConfigurationPropertiesScan: @ConfigurationProperties 어노테이션이 붙은 클래스를 스캔하여 빈으로 등록
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ContentHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContentHubApplication.class, args);
	}

}
