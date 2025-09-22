package com.cjy.contenthub.detail.information.service;

import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjy.contenthub.detail.information.function.DetailWishlistSetter;
import com.cjy.contenthub.detail.information.helper.DetailInformationHelper;

import lombok.RequiredArgsConstructor;

/**
 * 상세 정보 서비스 구현 클래스(캐시 미사용)
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class DetailInformationNoCacheServiceImpl implements DetailInformationNoCacheService {
	
	/** 상세 정보 헬퍼 */
	private final DetailInformationHelper detailInformationHelper;

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
	@Override
	public <T> void setWishlistFromResponse(DetailWishlistSetter<T> wishlistSetter, T detailResponse, Function<T, String> idExtractor, String contentMediaType, Long userId) {
		
		String apiId = idExtractor.apply(detailResponse);
		boolean isWishlisted = detailInformationHelper.setWishlisted(userId, contentMediaType, apiId);
		wishlistSetter.setWishlisted(detailResponse, isWishlisted);
	}

}
