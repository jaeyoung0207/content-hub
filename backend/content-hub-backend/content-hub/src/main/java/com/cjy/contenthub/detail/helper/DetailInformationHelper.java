package com.cjy.contenthub.detail.helper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cjy.contenthub.wishlist.repository.WishlistRepository;
import com.cjy.contenthub.wishlist.repository.entity.WishlistEntity;

import lombok.RequiredArgsConstructor;

/**
 * 상세 정보 헬퍼 클래스
 */
@Component
@RequiredArgsConstructor
public class DetailInformationHelper {
	
	/** 위시리스트 레포지토리 */
	private final WishlistRepository wishlistRepository;
	
	/**
	 * 특정 유저가 특정 콘텐츠를 위시리스트에 등록했는지 여부를 확인
	 * 
	 * @param userId            유저 테이블 ID
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId             API ID
	 * @return 위시리스트에 등록된 경우 true, 그렇지 않은 경우 false
	 */
	public boolean setWishlisted(Long userId, String originalMediaType, String apiId) {
		
		List<WishlistEntity> wishlistList = wishlistRepository.getRegisteredWishlist(userId, originalMediaType, apiId);
		
		return wishlistList != null && !wishlistList.isEmpty();
	}

}
