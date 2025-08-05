package com.cjy.contenthub.common.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.util.SessionUtil;

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
	 * 성인 여부 플래그 조회
	 * 
	 * @return ResponseEntity<Boolean> 처리 결과
	 */
	@PostMapping("/setAdultFlg")
	public ResponseEntity<Boolean> setAdultFlg(@RequestParam(CommonConstants.ADULT_FLG) boolean adultFlg) {
		
		// 세션을 가져옴
		HttpSession session = sessionUtil.getSession();
		// 세션에서 성인 여부 플래그를 설정
		session.setAttribute(CommonConstants.ADULT_FLG, adultFlg);
		
		// 처리 결과를 ResponseEntity로 반환
		return ResponseEntity.ok(true);
	}

	/**
	 * 성인 여부 플래그 클리어
	 * 
	 * @return ResponseEntity<Boolean> 처리 결과
	 */
	@PostMapping("/clearAdultFlg")
	public ResponseEntity<Boolean> clearAdultFlg() {
		
		// 세션을 가져옴
		HttpSession session = sessionUtil.getSession();

		// 세션에서 성인 여부 플래그가 존재하는지 확인
		if (ObjectUtils.isNotEmpty(session.getAttribute(CommonConstants.ADULT_FLG))) {			
			// 세션에서 성인 여부 플래그를 제거
			session.removeAttribute(CommonConstants.ADULT_FLG);
		}
		
		// 처리 결과를 ResponseEntity로 반환
		return ResponseEntity.ok(true);
	}
	
}
