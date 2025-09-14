package com.cjy.contenthub.detail.function;

/**
 * 상세 정보를 가져오는 함수형 인터페이스
 * 
 * @param <T> 상세 응답 DTO 타입
 */
@FunctionalInterface
public interface DetailFetcher<T> {
	/**
	 * 상세 정보를 가져오는 함수형 인터페이스
	 * 
	 * @param apiId             API ID
	 * @param originalMediaType 원본 미디어 타입
	 * @param userId            사용자 ID
	 * @param isWishlisted      위시리스트 상태
	 * @return 상세 응답 DTO
	 */
	T fetch(Integer apiId, String originalMediaType, Long userId, boolean isWishlisted);
}
