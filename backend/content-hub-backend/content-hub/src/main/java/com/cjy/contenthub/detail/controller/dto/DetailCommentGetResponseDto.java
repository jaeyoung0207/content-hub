package com.cjy.contenthub.detail.controller.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 상세 코멘트 응답 DTO
 * 콘텐츠에 대한 유저 코멘트의 상세 정보를 담고 있으며, 클라이언트 요청에 대한 응답으로 사용됨
 */
@Setter
@Getter
@NoArgsConstructor
public class DetailCommentGetResponseDto {
	
	/** 전체 코멘트 수 */
	private long totalElements;
	
	/** 상세 코멘트 데이터 리스트 */
	private List<DetailCommentGetDataDto> responseList;
	
}
