package com.cjy.contenthub.detail.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 상세 코멘트 서비스 DTO
 * 유저 코멘트의 목록과 총 요소 수를 전달하기 위한 DTO 클래스
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailCommentServiceDto {
	
	/** 전체 요소 수 */
	private long totalElements;
	
	/** 상세 코멘트 데이터 리스트 */
	private List<DetailCommentDataServiceDto> dataList;
	
}
