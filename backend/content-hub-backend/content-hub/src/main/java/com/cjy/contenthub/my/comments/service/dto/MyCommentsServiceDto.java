package com.cjy.contenthub.my.comments.service.dto;

import java.util.List;

import com.cjy.contenthub.my.comments.repository.dto.MyCommentsDto;

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
public class MyCommentsServiceDto extends MyCommentsDto {
	
	/** 나의 코멘트 리스트 */
	List<MyCommentsServiceDataDto> myCommentList;
	
	/** 전체 페이지 수 */
	int totalPages;
	
	/** 전체 요소 수 */
	long totalElements;
	
}
