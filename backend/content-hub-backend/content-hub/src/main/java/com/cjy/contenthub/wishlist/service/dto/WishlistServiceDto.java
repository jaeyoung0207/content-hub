package com.cjy.contenthub.wishlist.service.dto;

import java.util.List;

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
	
	/** 컨텐츠 미디어 타입 */
	private String contentMediaType;
	
	/** apiId */
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
