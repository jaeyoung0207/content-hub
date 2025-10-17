package com.cjy.contenthub.common.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cjy.contenthub.common.annotation.ApiController;
import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.common.constants.CommonEnum.DisplayMediaTypeEnum;
import com.cjy.contenthub.common.controller.dto.CommonContentMediaTypeDto;
import com.cjy.contenthub.common.controller.dto.CommonDisplayMediaTypeDto;
import com.cjy.contenthub.common.controller.dto.CommonMediaTypeResponseDto;
import com.cjy.contenthub.common.util.SessionUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * 공통 컨트롤러 클래스
 */
@Tag(name = "common-api", description = "Common APIs")
@ApiController
@RequestMapping("/common")
@RequiredArgsConstructor
public class CommonController {
	
	/** 세션 유틸리티 클래스 */
	private final SessionUtil sessionUtil;

	/**
	 * 첫 로드시 csrf 토큰을 생성하기 위한 API
	 * Spring Security가 자동으로 XSRF-TOKEN 쿠키를 생성해 내려줌
	 * 
	 * @return 처리 결과
	 */
	@Operation(summary = "CSRF 토큰 조회")
    @GetMapping("/getCsrfToken")
    public CsrfToken getCsrfToken(@Nullable CsrfToken token) {
        return token;
    }
    
	/**
	 * 공통 미디어 타입 조회
	 * 
	 * @return Map<String, String> 미디어 타입 맵
	 */
	@Operation(summary = "공통 미디어 타입 조회")
    @GetMapping("/getMediaTypes")
    public CommonMediaTypeResponseDto getMediaTypes() {
    	// 컨텐츠 미디어 타입 DTO 생성
    	CommonContentMediaTypeDto contentMediaTypeDto = CommonContentMediaTypeDto.builder()
    			.aniCode(ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode())
    			.dramaCode(ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode())
    			.documentaryCode(ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode())
    			.kidsCode(ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode())
    			.newsCode(ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode())
    			.varietyCode(ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode())
    			.movieCode(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode())
    			.personCode(ContentMediaTypeEnum.MEDIA_TYPE_PERSON.getContentMediaTypeCode())
    			.comicsCode(ContentMediaTypeEnum.MEDIA_TYPE_COMICS.getContentMediaTypeCode())
    			.build();
    	// 화면표시용 미디어 타입 DTO 생성
    	CommonDisplayMediaTypeDto displayMediaTypeDto = CommonDisplayMediaTypeDto.builder()
    			.aniCode(DisplayMediaTypeEnum.MEDIA_TYPE_ANI.getDisplayMediaTypeCode())
    			.dramaCode(DisplayMediaTypeEnum.MEDIA_TYPE_DRAMA.getDisplayMediaTypeCode())
    			.documentaryCode(DisplayMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getDisplayMediaTypeCode())
    			.kidsCode(DisplayMediaTypeEnum.MEDIA_TYPE_KIDS.getDisplayMediaTypeCode())
    			.newsCode(DisplayMediaTypeEnum.MEDIA_TYPE_NEWS.getDisplayMediaTypeCode())
    			.varietyCode(DisplayMediaTypeEnum.MEDIA_TYPE_VARIETY.getDisplayMediaTypeCode())
    			.movieCode(DisplayMediaTypeEnum.MEDIA_TYPE_MOVIE.getDisplayMediaTypeCode())
    			.personCode(DisplayMediaTypeEnum.MEDIA_TYPE_PERSON.getDisplayMediaTypeCode())
    			.comicsCode(DisplayMediaTypeEnum.MEDIA_TYPE_COMICS.getDisplayMediaTypeCode())
    			.build();
    	// 미디어 타입 응답 DTO 생성 및 반환
		return CommonMediaTypeResponseDto.builder()
				.contentMediaTypeDto(contentMediaTypeDto)
				.displayMediaTypeDto(displayMediaTypeDto)
				.build();
    }
	
	/**
	 * 성인 여부 플래그 조회
	 * 
	 * @return ResponseEntity<Boolean> 처리 결과
	 */
	@Operation(summary = "성인 여부 플래그 조회")
	@PostMapping("/setAdultFlg")
	public ResponseEntity<Void> setAdultFlg(@RequestParam(CommonConstants.ADULT_FLG) boolean adultFlg) {
		
		// 세션을 가져옴
		HttpSession session = sessionUtil.getSession();
		// 세션에서 성인 여부 플래그를 설정
		session.setAttribute(CommonConstants.ADULT_FLG, adultFlg);
		
		// 처리 결과를 ResponseEntity로 반환
		return ResponseEntity.ok().build();
	}

	/**
	 * 성인 여부 플래그 클리어
	 * 
	 * @return ResponseEntity<Boolean> 처리 결과
	 */
	@Operation(summary = "성인 여부 플래그 클리어")
	@PostMapping("/clearAdultFlg")
	public ResponseEntity<Void> clearAdultFlg() {
		
		// 세션을 가져옴
		HttpSession session = sessionUtil.getSession();

		// 세션에서 성인 여부 플래그가 존재하는지 확인
		if (ObjectUtils.isNotEmpty(session.getAttribute(CommonConstants.ADULT_FLG))) {			
			// 세션에서 성인 여부 플래그를 제거
			session.removeAttribute(CommonConstants.ADULT_FLG);
		}
		
		// 처리 결과를 ResponseEntity로 반환
		return ResponseEntity.ok().build();
	}
	
}
