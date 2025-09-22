package com.cjy.contenthub.detail.comment.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cjy.contenthub.detail.comment.controller.dto.DetailCommentGetDataDto;
import com.cjy.contenthub.detail.comment.controller.dto.DetailCommentGetResponseDto;
import com.cjy.contenthub.detail.comment.controller.dto.DetailCommentSaveRequestDto;
import com.cjy.contenthub.detail.comment.controller.dto.DetailCommentUpdateRequestDto;
import com.cjy.contenthub.detail.comment.mapper.DetailCommentMapper;
import com.cjy.contenthub.detail.comment.service.DetailCommentService;
import com.cjy.contenthub.detail.comment.service.dto.DetailCommentDataServiceDto;
import com.cjy.contenthub.detail.comment.service.dto.DetailCommentServiceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 상세 화면 코멘트 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/detail/comment")
@RequiredArgsConstructor
@Slf4j
public class DetailCommentController {

	/** 코멘트 서비스 */
	private final DetailCommentService commentService;

	/** 상세 매퍼 */
	private final DetailCommentMapper mapper;

	/** 리퀘스트 파라미터 키 : 코멘트 번호 */
	private static final String PARAM_COMMENT_ID = "comment_id";

	/** 리퀘스트 파라미터 키 : Original Media Type */
	private static final String PARAM_ORIGINAL_MEDIATYPE = "content_media_type";

	/** 리퀘스트 파라미터 키 : API ID */
	private static final String PARAM_API_ID = "api_id";

	/** 리퀘스트 파라미터 키 : 페이지 번호 */
	private static final String PARAM_PAGE = "page";

	/** 리퀘스트 파라미터 키 : 프로바이더ID */
	private static final String PARAM_PROVIDER_ID = "provider_id";

	/**
	 * 코멘트 등록 API
	 * 
	 * @param params 상세 코멘트 등록 요청 DTO
	 * @return ResponseEntity<Boolean> 등록 결과
	 */
	@PostMapping(value = "/saveComment")
	public ResponseEntity<Boolean> saveComent(@RequestBody @Validated DetailCommentSaveRequestDto params) {

		// 요청 파라미터를 서비스 DTO로 변환
		DetailCommentDataServiceDto commentDto = mapper.commentSaveReqToCommentService(params);

		// 등록 서비스 호출
		Boolean saveResult = commentService.saveComment(commentDto);

		// 등록 결과 반환
		return ResponseEntity.ok(saveResult);

	}

	/**
	 * 코멘트 갱신 API
	 * 
	 * @param params 상세 코멘트 갱신 요청 DTO
	 * @return ResponseEntity<Boolean> 갱신 결과
	 */
	@PutMapping(value = "/updateComment")
	public ResponseEntity<Boolean> updateComent(@RequestBody @Validated DetailCommentUpdateRequestDto params) {

		// 요청 파라미터를 서비스 DTO로 변환
		DetailCommentDataServiceDto commentDto = mapper.commentUpdateReqToCommentService(params);

		// 갱신 서비스 호출
		Boolean updateResult = commentService.updateComment(commentDto);

		// 갱신 결과 반환
		return ResponseEntity.ok(updateResult);

	}

	/**
	 * 코멘트 삭제 API
	 * 
	 * @param commentId 코멘트 ID
	 * @return ResponseEntity<Boolean> 삭제 결과
	 */
	@DeleteMapping(value = "/deleteComment")
	public ResponseEntity<Boolean> deleteComment(@RequestParam(PARAM_COMMENT_ID) Long commentId) {

		// 삭제 서비스 호출
		Boolean deleteResult = commentService.deleteComment(commentId);

		// 삭제 결과 반환
		return ResponseEntity.ok(deleteResult);

	}

	/**
	 * 코멘트 목록 조회 API
	 * 
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param apiId API ID
	 * @param page 페이지 번호
	 * @param providerId 프로바이더 ID
	 * @return ResponseEntity<DetailCommentGetResponseDto> 코멘트 목록 응답 DTO
	 */
	@GetMapping(value = "/getCommentList")
	public ResponseEntity<DetailCommentGetResponseDto> getCommentList(
			@RequestParam(PARAM_ORIGINAL_MEDIATYPE)  String contentMediaType,
			@RequestParam(PARAM_API_ID)  String apiId,
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_PROVIDER_ID, required = false)  String providerId
			) {

		// 응답 DTO 초기화
		DetailCommentGetResponseDto response = new DetailCommentGetResponseDto();

		// 코멘트 조회 서비스 호출
		DetailCommentServiceDto serviceResult = commentService.getCommentList(contentMediaType, apiId, page, providerId);

		// 서비스 DTO를 응답 DTO로 변환
		List<DetailCommentGetDataDto> responseDtoList = mapper.commentServiceDtoListToCommentGetResponseDtoList(serviceResult.getDataList());

		// 응답 DTO 설정
		response.setResponseList(responseDtoList);
		response.setTotalElements(serviceResult.getTotalElements());

		// 코멘트 목록 반환
		return ResponseEntity.status(HttpStatus.OK).body(response);

	}

	/**
	 * 별점 평균 조회 API
	 * 
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param apiId API ID
	 * @return ResponseEntity<BigDecimal> 별점 평균
	 */
	@GetMapping(value = "/getStarRatingAverage")
	public ResponseEntity<BigDecimal> getStarRatingAverage(
			@RequestParam(PARAM_ORIGINAL_MEDIATYPE) String contentMediaType,
			@RequestParam(PARAM_API_ID) String apiId
			) {

		// 별점 평균 조회 서비스 호출
		BigDecimal starRating = 
				Optional.ofNullable(commentService.getStarRatingAverage(contentMediaType, apiId))
				.orElse(BigDecimal.ZERO);

		// 별점 평균 반환
		return ResponseEntity.status(HttpStatus.OK).body(starRating);

	}
}
