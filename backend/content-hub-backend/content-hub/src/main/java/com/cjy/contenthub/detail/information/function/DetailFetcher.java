package com.cjy.contenthub.detail.information.function;

/**
 * 상세 정보를 가져오는 함수형 인터페이스
 * 서로 다른 DTO에 대해 상세 정보를 가져오는 기능을 제공
 * 
 * @param <T> 상세 응답 DTO 타입
 */
@FunctionalInterface
public interface DetailFetcher<T> {
	/**
	 * 상세 정보를 가져오는 함수형 인터페이스
	 * 
	 * @param apiId             API ID
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param userId            사용자 ID
	 * @param isWishlisted      위시리스트 상태
	 * @return 상세 응답 DTO
	 */
	T fetch(Integer apiId, String contentMediaType, Long userId, boolean isWishlisted);
}
