package com.cjy.contenthub.my.comments.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cjy.contenthub.common.annotation.ApiController;
import com.cjy.contenthub.common.annotation.MaskingTarget;
import com.cjy.contenthub.my.comments.controller.dto.MyCommentsResponseDto;
import com.cjy.contenthub.my.comments.mapper.MyCommentsMapper;
import com.cjy.contenthub.my.comments.service.MyCommentsService;
import com.cjy.contenthub.my.comments.service.dto.MyCommentsServiceDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 나의 코멘트 컨트롤러
 */
@Tag(name = "my-comments-api", description = "Wishlist APIs")
@ApiController
@RequestMapping("/my/comments")
@RequiredArgsConstructor
public class MyCommentsController {
	
	/** 나의 코멘트 서비스 */
	private final MyCommentsService myCommentService;
	
	/** 나의 코멘트 매퍼 */
	private final MyCommentsMapper myCommentsMapper;
	
	/** 사용자 ID 파라미터명 */
	private static final String PARAM_USER_ID = "user_id";
	
	/** 페이지 번호 파라미터명 */
	private static final String PARAM_PAGE_NO = "page_no";
	
	/**
	 * 나의 코멘트 리스트 조회 API
	 * 
	 * @param userId 사용자 ID
	 * @param pageNo 페이지 번호
	 * @return 나의 코멘트 리스트
	 */
	@Operation(summary = "나의 코멘트 리스트 조회")
	@GetMapping("/getMyCommentList")
	public ResponseEntity<MyCommentsResponseDto> getMyCommentList(@RequestParam(PARAM_USER_ID) @MaskingTarget Long userId,
			@RequestParam(PARAM_PAGE_NO) Integer pageNo) {

		MyCommentsServiceDto serviceResult = myCommentService.getMyCommentList(userId, pageNo);
		
		MyCommentsResponseDto response = myCommentsMapper.serviceToResponse(serviceResult);

		return ResponseEntity.ok(response);
	}
}
