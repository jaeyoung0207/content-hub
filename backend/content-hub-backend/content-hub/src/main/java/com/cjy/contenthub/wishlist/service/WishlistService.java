package com.cjy.contenthub.wishlist.service;

import com.cjy.contenthub.wishlist.service.dto.WishlistCheckResultServiceDto;
import com.cjy.contenthub.wishlist.service.dto.WishlistListServiceDto;
import com.cjy.contenthub.wishlist.service.dto.WishlistServiceDto;

/**
 * 위시리스트 서비스 인터페이스
 */
public interface WishlistService {
	
	/**
	 * 위시리스트에 콘텐츠 추가
	 * 
	 * @param saveServiceDto
	 */
	boolean addToWishlist(WishlistServiceDto saveServiceDto);

	/**
	 * 위시리스트에서 콘텐츠 제거
	 * 
	 * @param saveServiceDto
	 */
	boolean removeFromWishlist(WishlistServiceDto saveServiceDto);
	
	/**
	 * 위시리스트에 이미 존재하는지 확인
	 * 
	 * @param serviceDto
	 * @return 존재 여부
	 */
	WishlistCheckResultServiceDto checkWishlist(Long userId, String apiId, String contentMediaType);
	
	/**
	 * 유저가 등록한 위시리스트 조회
	 * 
	 * @param userId 유저 테이블 ID
	 * @return 위시리스트 항목 정보
	 */
	WishlistListServiceDto getWishlist(Long userId);

}
