package com.cjy.contenthub.detail.service;

import java.math.BigDecimal;

import com.cjy.contenthub.detail.service.dto.DetailCommentDataServiceDto;
import com.cjy.contenthub.detail.service.dto.DetailCommentServiceDto;

/**
 * 상세 페이지 코멘트 서비스 인터페이스
 */
public interface DetailCommentService {
	
	/**
	 * 코멘트 등록 메소드
	 * 
	 * @param commentDto 상세 코멘트 데이터 서비스 DTO
	 * @return 성공 여부
	 */
	boolean saveComment(DetailCommentDataServiceDto commentDto);
	
	/**
	 * 코멘트 갱신 메소드
	 * 
	 * @param commentDto 상세 코멘트 데이터 서비스 DTO
	 * @return 성공 여부
	 */
	boolean updateComment(DetailCommentDataServiceDto commentDto);
	
	/**
	 * 코멘트 삭제 메소드
	 * 
	 * @param commentNo 코멘트 번호
	 * @return 성공 여부
	 */
	boolean deleteComment(Long commentNo);
	
	/**
	 * 코멘트 목록 조회 메소드
	 * 
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId API ID
	 * @param page 페이지 번호
	 * @param userId 유저 ID
	 * @return 상세 코멘트 서비스 DTO
	 */
	DetailCommentServiceDto getCommentList(String originalMediaType, String apiId, Integer page, String userId);
	
	/**
	 * 별점 평균 조회 메소드
	 * 
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId API ID
	 * @return 별점 평균
	 */
	BigDecimal getStarRatingAverage(String originalMediaType, String apiId);

}
