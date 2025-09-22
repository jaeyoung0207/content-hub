package com.cjy.contenthub.detail.information.service;

import java.util.function.Function;

import com.cjy.contenthub.detail.information.function.DetailWishlistSetter;

/**
 * 상세 정보 서비스 인터페이스(캐시 미사용)
 */
public interface DetailInformationNoCacheService {
	
	/**
	 * 상세 응답 DTO에서 위시리스트 여부 설정
	 *
	 * @param <T>               상세 응답 DTO 타입
	 * @param wishlistSetter    위시리스트 설정 함수형 인터페이스
	 * @param detailResponse    상세 응답 DTO
	 * @param idExtractor       상세 응답 DTO에서 ID를 추출하는 함수
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param userId            유저 테이블 ID
	 */
	<T> void setWishlistFromResponse(
			DetailWishlistSetter<T> wishlistSetter, T detailResponse, Function<T, String> idExtractor, String contentMediaType, Long userId);

}
