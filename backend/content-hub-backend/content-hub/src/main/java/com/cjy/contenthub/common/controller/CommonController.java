package com.cjy.contenthub.common.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.util.SessionUtil;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * 공통 컨트롤러 클래스
 */
@RestController
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
    @GetMapping("/getCsrfToken")
    public CsrfToken getCsrfToken(@Nullable CsrfToken token) {
        return token;
    }
	
	/**
	 * 성인 여부 플래그 조회
	 * 
	 * @return ResponseEntity<Boolean> 처리 결과
	 */
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
