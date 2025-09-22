package com.cjy.contenthub.wishlist.controller.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 위시리스트 리퀘스트 DTO
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistListResponseDto {
	
	/** 애니메이션 위시리스트 */
	private List<WishlistResponseDto> aniWishlist;
	
	/** 드라마 위시리스트 */
	private List<WishlistResponseDto> dramaWishlist;
	
	/** 다큐멘터리 위시리스트 */
	private List<WishlistResponseDto> documentaryWishlist;
	
	/** 키즈 위시리스트 */
	private List<WishlistResponseDto> kidsWishlist;
	
	/** 뉴스 위시리스트 */
	private List<WishlistResponseDto> newsWishlist;
	
	/** 예능 위시리스트 */
	private List<WishlistResponseDto> varietyWishlist;
	
	/** 영화 위시리스트 */
	private List<WishlistResponseDto> movieWishlist;
	
	/** 코믹스 위시리스트 */
	private List<WishlistResponseDto> comicsWishlist;

}
