package com.cjy.contenthub.app.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 콘텐츠 미디어 타입 DTO
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppContentMediaTypeDto {
	
	/** 애니메이션 미디어 타입 코드 */
	@Schema(requiredMode = RequiredMode.REQUIRED)
	private String aniCode;
	
	/** 드라마 미디어 타입 코드 */
	@Schema(requiredMode = RequiredMode.REQUIRED)
	private String dramaCode;
	
	/** 다큐멘터리 미디어 타입 코드 */
	@Schema(requiredMode = RequiredMode.REQUIRED)
	private String documentaryCode;
	
	/** 키즈 미디어 타입 코드 */
	@Schema(requiredMode = RequiredMode.REQUIRED)
	private String kidsCode;
	
	/** 뉴스 미디어 타입 코드 */
	@Schema(requiredMode = RequiredMode.REQUIRED)
	private String newsCode;
	
	/** 예능 미디어 타입 코드 */
	@Schema(requiredMode = RequiredMode.REQUIRED)
	private String varietyCode;
	
	/** 영화 미디어 타입 코드 */
	@Schema(requiredMode = RequiredMode.REQUIRED)
	private String movieCode;
	
	/** 인물 미디어 타입 코드 */
	@Schema(requiredMode = RequiredMode.REQUIRED)
	private String personCode;
	
	/** 만화 미디어 타입 코드 */
	@Schema(requiredMode = RequiredMode.REQUIRED)
	private String comicsCode;

}
