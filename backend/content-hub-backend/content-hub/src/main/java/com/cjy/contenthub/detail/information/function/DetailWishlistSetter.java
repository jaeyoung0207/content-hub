package com.cjy.contenthub.detail.information.function;

/**
 * 상세 정보의 위시리스트 설정을 위한 함수형 인터페이스
 * 
 * @param <T> 상세 응답 DTO 타입
 */
@FunctionalInterface
public interface DetailWishlistSetter<T> {
	
	/**
	 * 상세 응답 DTO의 위시리스트 상태를 설정
	 * 
	 * @param detailResponse 상세 응답 DTO
	 * @param wishlisted     위시리스트 상태
	 */
	void setWishlisted(T detailResponse, boolean wishlisted);

}
