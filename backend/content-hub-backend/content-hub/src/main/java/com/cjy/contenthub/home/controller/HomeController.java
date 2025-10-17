package com.cjy.contenthub.home.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cjy.contenthub.common.annotation.ApiController;
import com.cjy.contenthub.home.controller.dto.HomeRankingListResponseDto;
import com.cjy.contenthub.home.mapper.HomeMapper;
import com.cjy.contenthub.home.service.HomeService;
import com.cjy.contenthub.home.service.dto.HomeRankingListServiceDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 홈 화면 API 컨트롤러 클래스
 */
@Tag(name = "home-api", description = "Home APIs")
@ApiController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {
	
	/** 홈 서비스 */
	private final HomeService homeService;
	
	/** 홈 매퍼 */
	private final HomeMapper mapper;
	
	/** 리퀘스트 파라미터 키 : 유저 테이블 ID */
	private static final String PARAM_USER_ID = "user_id";
	
	/**
	 * 콘텐츠 랭킹 정보 조회
	 * 
	 * @param userId 유저 테이블 ID
	 * @return 콘텐츠 랭킹 응답 DTO 리스트
	 */
	@Operation(summary = "콘텐츠 랭킹 정보 조회")
	@GetMapping("/rankings")
	public ResponseEntity<HomeRankingListResponseDto> getContentRankings(@RequestParam(value = PARAM_USER_ID, required = false) Long userId) {
		// 서비스에서 콘텐츠 랭킹 데이터 조회
		HomeRankingListServiceDto serviceResult = homeService.getContentRankings(userId);
		// 서비스 DTO 리스트를 응답 DTO 리스트로 매핑
		HomeRankingListResponseDto response = mapper.serviceListToResponseList(serviceResult);
		// 응답 DTO 리스트 반환
		return ResponseEntity.ok(response);
	}
}
