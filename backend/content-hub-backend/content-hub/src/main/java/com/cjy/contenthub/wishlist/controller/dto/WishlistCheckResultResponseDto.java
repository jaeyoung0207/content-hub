package com.cjy.contenthub.wishlist.controller.dto;

import java.util.List;

import com.cjy.contenthub.wishlist.service.dto.WishlistServiceDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class WishlistCheckResultResponseDto {
	
	/** 위시리스트 최대 등록 개수 */
	private int maxWishlistCount;
	
	/** 위시리스트(중복체크용) */
	private List<WishlistServiceDto> wishlists;

}
