package com.cjy.contenthub.common.advice.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 공통 에러 응답 오브젝트 클래스
 */
@Getter
@Builder
public class CommonErrorResponse {
	
	/** 경로 */
	private String path;
	
	/** 상태코드 */
	private String status;
	
	/** 메세지 */
	private String message;
	
	/** body */
	private String body;
	
	/** 에러명 */
	private String name;
	
}
