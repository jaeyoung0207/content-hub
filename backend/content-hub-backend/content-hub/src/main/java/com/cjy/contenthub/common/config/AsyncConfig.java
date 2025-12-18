package com.cjy.contenthub.common.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 비동기 처리 관련 설정 클래스
 */
@Configuration
public class AsyncConfig {
	
	/** 외부 API 호출 후처리 전용 스레드: 핵심 스레드 수 */
	@Value("${app.thread-pool.api-task.core-pool-size:2}")
	private int apiTaskCorePoolSize;

	/** 외부 API 호출 후처리 전용 스레드: 최대 스레드 수 */
	@Value("${app.thread-pool.api-task.max-pool-size:4}")
	private int apiTaskMaxPoolSize;
	
	/** 외부 API 호출 후처리 전용 스레드: 큐 용량 */
	@Value("${app.thread-pool.api-task.queue-capacity:50}")
	private int apiTaskQueueCapacity;
	
	/** 블로킹 작업 전용 스레드: 핵심 스레드 수 */
	@Value("${app.thread-pool.blocking-task.core-pool-size:1}")
	private int blockingTaskCorePoolSize;
	
	/** 블로킹 작업 전용 스레드: 최대 스레드 수 */
	@Value("${app.thread-pool.blocking-task.max-pool-size:2}")
	private int blockingTaskMaxPoolSize;
	
	/** 블로킹 작업 전용 스레드: 큐 용량 */
	@Value("${app.thread-pool.blocking-task.queue-capacity:25}")
	private int blockingTaskQueueCapacity;
	
    /**
     * 외부 I/O 대기(WebClient toFuture 후처리)를 처리하기 위한 전용 스레드 풀 빈 정의
     * Tomcat 스레드 풀과 JVM의 CommonPool로부터 격리하여 안정성을 확보
     */
    @Bean
    Executor apiTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 스레드 풀이 평소에 항상 유지하는 최소한의 스레드 개수
        executor.setCorePoolSize(apiTaskCorePoolSize); 
        // 최대 스레드 수 (현재 스레드가 작업중이고 큐가 꽉찼을 경우에 생성되는 최대 스레드 수)
        executor.setMaxPoolSize(apiTaskMaxPoolSize); 
        // 작업을 일시적으로 보관해 두는 대기열(Queue)의 최대 크기
        executor.setQueueCapacity(apiTaskQueueCapacity); 
        executor.setThreadNamePrefix("ApiTask-");
        executor.initialize();
        return executor;
    }
    
	/**
	 * 블로킹 작업(외부 API 동기 호출 등)을 처리하기 위한 전용 스레드 풀 빈 정의
	 * Tomcat 스레드 풀과 JVM의 CommonPool로부터 격리하여 안정성을 확보
	 */
    @Bean
    Executor blockingTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 스레드 풀이 평소에 항상 유지하는 최소한의 스레드 개수
        executor.setCorePoolSize(blockingTaskCorePoolSize); 
        // 최대 스레드 수 (현재 스레드가 작업중이고 큐가 꽉찼을 경우에 생성되는 최대 스레드 수)
        executor.setMaxPoolSize(blockingTaskMaxPoolSize); 
        // 작업을 일시적으로 보관해 두는 대기열(Queue)의 최대 크기
        executor.setQueueCapacity(blockingTaskQueueCapacity); 
        executor.setThreadNamePrefix("BlockingTask-");
        executor.initialize();
        return executor;
    }

}
