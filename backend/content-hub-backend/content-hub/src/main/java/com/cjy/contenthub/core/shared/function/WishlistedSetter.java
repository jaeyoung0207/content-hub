package com.cjy.contenthub.core.shared.function;

/**
 * DTO 객체에 위시리스트 여부를 설정하는 함수형 인터페이스
 *
 * @param <T> DTO 객체 타입
 */
@FunctionalInterface
public interface WishlistedSetter<T> {

	/**
	 * DTO 객체에 위시리스트 여부를 설정하는 메서드
	 *
	 * @param dto        위시리스트 여부를 설정할 DTO 객체
	 * @param wishlisted 위시리스트 여부 (true: 위시리스트에 있음, false: 위시리스트에 없음)
	 */
	void setWishlisted(T dto, boolean wishlisted);

}
