package com.cjy.contenthub.my.comments.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cjy.contenthub.my.comments.mapper.MyCommentsMapper;
import com.cjy.contenthub.my.comments.repository.MyCommentsRepository;
import com.cjy.contenthub.my.comments.repository.dto.MyCommentsDto;
import com.cjy.contenthub.my.comments.service.dto.MyCommentsServiceDataDto;
import com.cjy.contenthub.my.comments.service.dto.MyCommentsServiceDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * 나의 코멘트 서비스 구현체
 */
@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class MyCommentsServiceImpl implements MyCommentsService {

	/** 나의 코멘트 리포지토리 */
	private final MyCommentsRepository myCommentRepository;

	/** 나의 코멘트 매퍼 */
	private final MyCommentsMapper myCommentsMapper;

	/** 페이지당 항목 수 설정값 */
	@Value("${app.my-comments.per-page}")
	private int perPage;

	/**
	 * 나의 코멘트 리스트 조회
	 * 
	 * @param userId 사용자 ID
	 * @param pageNo 페이지 번호
	 * @return 나의 코멘트 리스트
	 */
	@Override
	public MyCommentsServiceDto getMyCommentList(Long userId, Integer pageNo) {

		Pageable pageable = PageRequest.of(Optional.ofNullable(pageNo).orElse(0), perPage, Sort.by("create_time").descending());

		Page<MyCommentsDto> commentList = myCommentRepository.getCommentByUserId(userId, pageable);

		List<MyCommentsDto> contentList = commentList.getContent();

		// 내용이 존재할 경우 매핑 후 반환
		if (!CollectionUtils.isEmpty(contentList)) {
			List<MyCommentsServiceDataDto> resultList = myCommentsMapper.repositoryListToServiceList(contentList);
			return MyCommentsServiceDto.builder()
					.myCommentList(resultList)
					.totalPages(commentList.getTotalPages())
					.totalElements(commentList.getTotalElements())
					.build();
		}
		// 내용이 없을 경우 빈 리스트 반환
		return MyCommentsServiceDto.builder()
				.myCommentList(new ArrayList<>())
				.totalPages(0)
				.totalElements(0)
				.build();
	}

}
