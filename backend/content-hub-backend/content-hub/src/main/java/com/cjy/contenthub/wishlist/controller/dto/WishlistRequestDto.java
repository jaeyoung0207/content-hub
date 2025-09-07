package com.cjy.contenthub.wishlist.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class WishlistRequestDto {
	
	/** user 테이블 ID */
	@NotNull
	private Long userId;
	
	/** originalMediaType */
	@NotEmpty
	private String originalMediaType;
	
	/** apiId */
	@NotEmpty
	private String apiId;
	
	/** 제목 */
	private String title;
	
	/** 썸네일 이미지 */
	private String thumbnailImageUrl;

}
