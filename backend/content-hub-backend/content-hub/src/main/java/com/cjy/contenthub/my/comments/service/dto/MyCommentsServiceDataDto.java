package com.cjy.contenthub.my.comments.service.dto;

import com.cjy.contenthub.my.comments.repository.dto.MyCommentsDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 나의 코멘트 데이터 서비스 DTO
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class MyCommentsServiceDataDto extends MyCommentsDto {
	
	/** 작성 일자 문자열 */
	private String createTimeStr; 
	
}
