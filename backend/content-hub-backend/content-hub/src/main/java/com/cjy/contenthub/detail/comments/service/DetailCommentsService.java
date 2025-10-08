package com.cjy.contenthub.detail.comments.service;

import java.math.BigDecimal;

import com.cjy.contenthub.detail.comments.service.dto.DetailCommentsDataServiceDto;
import com.cjy.contenthub.detail.comments.service.dto.DetailCommentsServiceDto;

/**
 * 상세 코멘트 서비스 인터페이스
 */
public interface DetailCommentsService {
	
	/**
	 * 코멘트 등록 메소드
	 * 
	 * @param commentDto 상세 코멘트 데이터 서비스 DTO
	 * @return 성공 여부
	 */
	boolean saveComment(DetailCommentsDataServiceDto commentDto);
	
	/**
	 * 코멘트 갱신 메소드
	 * 
	 * @param commentDto 상세 코멘트 데이터 서비스 DTO
	 * @return 성공 여부
	 */
	boolean updateComment(DetailCommentsDataServiceDto commentDto);
	
	/**
	 * 코멘트 삭제 메소드
	 * 
	 * @param commentId 코멘트 번호
	 * @return 성공 여부
	 */
	boolean deleteComment(Long commentId);
	
	/**
	 * 코멘트 목록 조회 메소드
	 * 
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param apiId API ID
	 * @param page 페이지 번호
	 * @param providerId 프로바이더 ID
	 * @return 상세 코멘트 서비스 DTO
	 */
	DetailCommentsServiceDto getCommentList(String contentMediaType, String apiId, Integer page, String providerId);
	
	/**
	 * 별점 평균 조회 메소드
	 * 
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param apiId API ID
	 * @return 별점 평균
	 */
	BigDecimal getStarRatingAverage(String contentMediaType, String apiId);

}
