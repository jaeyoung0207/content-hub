package com.cjy.contenthub.wishlist.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 위시리스트 서비스 리스트 DTO
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistListServiceDto {

	/** 애니메이션 위시리스트 */
	private List<WishlistServiceDto> aniWishlist;
	
	/** 드라마 위시리스트 */
	private List<WishlistServiceDto> dramaWishlist;
	
	/** 영화 위시리스트 */
	private List<WishlistServiceDto> movieWishlist;
	
	/** 코믹스 위시리스트 */
	private List<WishlistServiceDto> comicsWishlist;

}
