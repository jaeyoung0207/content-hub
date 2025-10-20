package com.cjy.contenthub.app.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 미디어 타입 DTO
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppMediaTypeResponseDto {
	
	/** 애니메이션 미디어 타입 코드 */
	@Schema(requiredMode = RequiredMode.REQUIRED)
	private AppContentMediaTypeDto contentMediaTypeDto;
	
	/** 드라마 미디어 타입 코드 */
	@Schema(requiredMode = RequiredMode.REQUIRED)
	private AppDisplayMediaTypeDto displayMediaTypeDto;

}
