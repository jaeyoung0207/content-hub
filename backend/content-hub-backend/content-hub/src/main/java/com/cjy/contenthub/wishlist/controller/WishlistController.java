package com.cjy.contenthub.wishlist.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cjy.contenthub.wishlist.controller.dto.WishlistListResponseDto;
import com.cjy.contenthub.wishlist.controller.dto.WishlistRequestDto;
import com.cjy.contenthub.wishlist.mapper.WishlistMapper;
import com.cjy.contenthub.wishlist.service.WishlistService;
import com.cjy.contenthub.wishlist.service.dto.WishlistListServiceDto;
import com.cjy.contenthub.wishlist.service.dto.WishlistServiceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 위시리스트 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
@Slf4j
public class WishlistController {
	
	/** 위시리스트 서비스 */
	private final WishlistService wishlistService;
	
	/** 위시리스트 매퍼 */
	private final WishlistMapper wishlistMapper;
	
	/**
	 * 위시리스트 저장 API
	 * 
	 * @param requestDto
	 * @return 위시리스트 저장 성공 여부
	 */
	@PostMapping("/save")
	public ResponseEntity<Boolean> saveWishlist(@RequestBody @Validated WishlistRequestDto requestDto) {
		
		WishlistServiceDto serviceDto = wishlistMapper.requestToService(requestDto);
		
		boolean serviceResult = wishlistService.addToWishlist(serviceDto);

		return ResponseEntity.ok(serviceResult);
		
	}
	
	/**
	 * 위시리스트 삭제 API
	 * 
	 * @param requestDto
	 * @return 위시리스트 삭제 성공 여부
	 */
	@DeleteMapping("/delete")
	public ResponseEntity<Boolean> deleteWishlist(@RequestBody @Validated WishlistRequestDto requestDto) {

		WishlistServiceDto serviceDto = wishlistMapper.requestToService(requestDto);

		boolean serviceResult = wishlistService.removeFromWishlist(serviceDto);

		return ResponseEntity.ok(serviceResult);

	}
	
	/**
	 * 유저가 등록한 위시리스트 조회 API
	 * 
	 * @param userId 유저 테이블 ID
	 * @return 위시리스트 항목 정보
	 */
	@PostMapping("/getWishlist")
	public ResponseEntity<WishlistListResponseDto> getWishlist(Long userId) {
		
		WishlistListServiceDto serviceResult = wishlistService.getWishlist(userId);

		WishlistListResponseDto responseDto = wishlistMapper.listServiceToListResponse(serviceResult);

		return ResponseEntity.ok(responseDto);

	}

}
