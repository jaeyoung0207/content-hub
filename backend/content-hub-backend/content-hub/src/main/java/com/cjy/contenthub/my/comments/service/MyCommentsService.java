package com.cjy.contenthub.my.comments.service;

import com.cjy.contenthub.my.comments.service.dto.MyCommentsServiceDto;

/**
 * 나의 코멘트 서비스 인터페이스
 */
public interface MyCommentsService {
	
	/**
	 * 나의 코멘트 리스트 조회
	 * 
	 * @param userId 사용자 ID
	 * @param pageNo 페이지 번호
	 * @return 나의 코멘트 리스트
	 */
	MyCommentsServiceDto getMyCommentList(Long userId, Integer pageNo);

}
