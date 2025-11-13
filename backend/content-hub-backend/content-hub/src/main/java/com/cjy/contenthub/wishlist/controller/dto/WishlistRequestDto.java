package com.cjy.contenthub.wishlist.controller.dto;

import java.util.List;

import com.cjy.contenthub.common.annotation.MaskingTarget;

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
	@MaskingTarget
	private Long userId;
	
	/** 컨텐츠 미디어 타입 */
	@NotEmpty
	private String contentMediaType;
	
	/** apiId */
	@NotEmpty
	private String apiId;
	
	/** 장르 ID 리스트 */
	private List<Integer> genreIds;
	
	/** 미디어 타입(화면 표시용) */
	private String displayMediaType;
	
	/** 제목 */
	private String title;
	
	/** 썸네일 이미지 */
	private String thumbnailImageUrl;

}
