package com.cjy.contenthub.detail.controller;

import java.math.BigDecimal;
import java.util.List;

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

import com.cjy.contenthub.detail.controller.dto.DetailCommentGetDataDto;
import com.cjy.contenthub.detail.controller.dto.DetailCommentGetResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailCommentSaveRequestDto;
import com.cjy.contenthub.detail.controller.dto.DetailCommentUpdateRequestDto;
import com.cjy.contenthub.detail.mapper.DetailMapper;
import com.cjy.contenthub.detail.service.DetailCommentService;
import com.cjy.contenthub.detail.service.dto.DetailCommentDataServiceDto;
import com.cjy.contenthub.detail.service.dto.DetailCommentServiceDto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 상세 화면 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/detail/comment")
@RequiredArgsConstructor
@Slf4j
public class DetailCommentController {

	/** 코멘트 서비스 */
	private final DetailCommentService service;

	/** 상세 매퍼 */
	private final DetailMapper mapper;

	/** 리퀘스트 파라미터 키 : 코멘트 번호 */
	private static final String PARAM_COMMENT_NO = "commentNo";

	/** 리퀘스트 파라미터 키 : Original Media Type */
	private static final String PARAM_ORIGINAL_MEDIATYPE = "originalMediaType";

	/** 리퀘스트 파라미터 키 : API ID */
	private static final String PARAM_API_ID = "apiId";

	/** 리퀘스트 파라미터 키 : 페이지 번호 */
	private static final String PARAM_PAGE = "page";

	/** 리퀘스트 파라미터 키 : 유저ID */
	private static final String PARAM_USER_ID = "userId";

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
		Boolean saveResult = service.saveComment(commentDto);

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
		Boolean updateResult = service.updateComment(commentDto);

		// 갱신 결과 반환
		return ResponseEntity.ok(updateResult);

	}

	/**
	 * 코멘트 삭제 API
	 * 
	 * @param commentNo 코멘트 번호
	 * @return ResponseEntity<Boolean> 삭제 결과
	 */
	@DeleteMapping(value = "/deleteComment")
	public ResponseEntity<Boolean> deleteComment(@RequestParam(PARAM_COMMENT_NO) @NotNull Long commentNo) {

		// 삭제 서비스 호출
		Boolean deleteResult = service.deleteComment(commentNo);

		// 삭제 결과 반환
		return ResponseEntity.ok(deleteResult);

	}

	/**
	 * 코멘트 목록 조회 API
	 * 
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId API ID
	 * @param page 페이지 번호 (선택)
	 * @param userId 유저 ID (선택)
	 * @return ResponseEntity<DetailCommentGetResponseDto> 코멘트 목록 응답 DTO
	 */
	@GetMapping(value = "/getCommentList")
	public ResponseEntity<DetailCommentGetResponseDto> getCommentList(
			@NotEmpty @RequestParam(PARAM_ORIGINAL_MEDIATYPE)  String originalMediaType,
			@NotEmpty @RequestParam(PARAM_API_ID)  String apiId,
			@Nullable @RequestParam(PARAM_PAGE) Integer page,
			@Nullable @RequestParam(PARAM_USER_ID)  String userId
			) {

		// 응답 DTO 초기화
		DetailCommentGetResponseDto response = new DetailCommentGetResponseDto();

		// 코멘트 조회 서비스 호출
		DetailCommentServiceDto serviceResult = service.getCommentList(originalMediaType, apiId, page, userId);

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
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId API ID
	 * @return ResponseEntity<BigDecimal> 별점 평균
	 */
	@GetMapping(value = "/getStarRatingAverage")
	public ResponseEntity<BigDecimal> getStarRatingAverage(
			@RequestParam(PARAM_ORIGINAL_MEDIATYPE) @NotEmpty String originalMediaType,
			@RequestParam(PARAM_API_ID) @NotEmpty String apiId
			) {

		// 별점 평균 조회 서비스 호출
		BigDecimal starRating = service.getStarRatingAverage(originalMediaType, apiId);

		// 별점 평균 반환
		return ResponseEntity.status(HttpStatus.OK).body(starRating);

	}
}
