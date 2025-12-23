package com.cjy.contenthub;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Content Hub Application 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
class ContentHubApplicationIT {

	@Test
	@DisplayName("[IT]contextLoads: 애플리케이션 컨텍스트 로드 테스트")
	void test_contextLoads() {
		assertTrue(true);
	}
}
