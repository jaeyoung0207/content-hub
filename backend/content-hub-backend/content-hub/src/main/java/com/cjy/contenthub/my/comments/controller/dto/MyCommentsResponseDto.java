package com.cjy.contenthub.my.comments.controller.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 나의 코멘트 서비스 DTO 
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MyCommentsResponseDto {
	
	/** 나의 코멘트 리스트 */
	List<MyCommentsDataResponseDto> myCommentList;
	
	/** 전체 페이지 수 */
	int totalPages;
	
	/** 전체 요소 수 */
	long totalElements;
	
}
