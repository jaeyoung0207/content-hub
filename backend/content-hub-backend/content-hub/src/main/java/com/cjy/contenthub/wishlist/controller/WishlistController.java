package com.cjy.contenthub.wishlist.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.wishlist.controller.dto.WishlistCheckResultResponseDto;
import com.cjy.contenthub.wishlist.controller.dto.WishlistListResponseDto;
import com.cjy.contenthub.wishlist.controller.dto.WishlistRequestDto;
import com.cjy.contenthub.wishlist.mapper.WishlistMapper;
import com.cjy.contenthub.wishlist.service.WishlistService;
import com.cjy.contenthub.wishlist.service.dto.WishlistCheckResultServiceDto;
import com.cjy.contenthub.wishlist.service.dto.WishlistListServiceDto;
import com.cjy.contenthub.wishlist.service.dto.WishlistServiceDto;

import lombok.RequiredArgsConstructor;

/**
 * 위시리스트 API 컨트롤러 클래스
 */
@RestController
@RequestMapping(CommonConstants.WISHLIST_PATH)
@RequiredArgsConstructor
public class WishlistController {
	
	/** 위시리스트 서비스 */
	private final WishlistService wishlistService;
	
	/** 위시리스트 매퍼 */
	private final WishlistMapper wishlistMapper;
	
	/** 리퀘스트 파라미터 키 : 유저 ID */
	private static final String PARAM_USER_ID = "user_id";
	
	/** 리퀘스트 파라미터 키 : API ID */
	private static final String PARAM_API_ID = "api_id";
	
	/** 리퀘스트 파라미터 키 : 컨텐츠 미디어 타입 */
	private static final String PARAM_CONTENT_MEDIA_TYPE = "content_media_type";
	
	/**
	 * 위시리스트 저장 API
	 * 
	 * @param requestDto
	 * @return 위시리스트 저장 성공 여부
	 */
	@PostMapping("/saveWishlist")
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
	@DeleteMapping("/deleteWishlist")
	public ResponseEntity<Boolean> deleteWishlist(@RequestBody @Validated WishlistRequestDto requestDto) {

		WishlistServiceDto serviceDto = wishlistMapper.requestToService(requestDto);

		boolean serviceResult = wishlistService.removeFromWishlist(serviceDto);

		return ResponseEntity.ok(serviceResult);
	}
	
	/**
	 * 위시리스트 존재 여부 확인 API
	 * 
	 * @param userId           유저 테이블 ID
	 * @param apiId            API ID
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @return 위시리스트 항목 정보
	 */
	@GetMapping("/checkWishlist")
	public ResponseEntity<WishlistCheckResultResponseDto> checkWishlist(
			@RequestParam(PARAM_USER_ID) Long userId, 
			@RequestParam(PARAM_API_ID) String apiId, 
			@RequestParam(PARAM_CONTENT_MEDIA_TYPE) String contentMediaType) {

		WishlistCheckResultServiceDto wishlistCheckResult = wishlistService.checkWishlist(userId, apiId, contentMediaType);
		
		WishlistCheckResultResponseDto responseDto = wishlistMapper.serviceToCheckResultResponse(wishlistCheckResult);

		return ResponseEntity.ok(responseDto);
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
