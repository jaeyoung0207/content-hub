package com.cjy.contenthub.wishlist.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 
 * 위시리스트 서비스 DTO 
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistServiceDto {
	
	/** user 테이블 ID */
	private Long userId;
	
	/** originalMediaType */
	private String originalMediaType;
	
	/** apiId */
	private String apiId;
	
	/** 제목 */
	private String title;
	
	/** 썸네일 이미지 */
	private String thumbnailImageUrl;

}
