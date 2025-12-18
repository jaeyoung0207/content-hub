package com.cjy.contenthub;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractBaseIT {
	
	@Autowired
	protected ObjectMapper objectMapper;
	
	@Autowired
	protected MockMvc mockMvc;
	
	@Autowired
	protected RedisTemplate<String, Object> redisTemplate;
	
	@AfterEach
	void teardown() {
	    // 테스트 메서드마다 Redis 데이터를 비워줌으로써 독립성 보장
	    redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
	}
}
