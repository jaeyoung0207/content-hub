package com.cjy.contenthub.wishlist.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistCheckResultServiceDto {
	
	/** 위시리스트 최대 등록 개수 */
	private int maxWishlistCount;
	
	/** 위시리스트(중복체크용) */
	private List<WishlistServiceDto> wishlists;

}
