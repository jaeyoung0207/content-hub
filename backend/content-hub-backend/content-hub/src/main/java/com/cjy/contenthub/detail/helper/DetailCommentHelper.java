package com.cjy.contenthub.detail.helper;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cjy.contenthub.detail.repository.DetailCommentViewRepository;
import com.cjy.contenthub.detail.repository.entity.DetailCommentViewEntity;

import lombok.RequiredArgsConstructor;

/**
 * 상세 화면 헬퍼 클래스
 */
@Component
@RequiredArgsConstructor
public class DetailCommentHelper {

	/** 코멘트 뷰 엔티티 리포지토리 */
	private final DetailCommentViewRepository commentViewRepository;

	/** TMDB API TV 추천 작품 API 패스 */
	@Value("${tmdb.url.tvRecommendationsPath}")
	private String tvRecommendationsPath;

	/** TMDB API Movie 추천 작품 API 패스 */
	@Value("${tmdb.url.movieRecommendationsPath}")
	private String movieRecommendationsPath;

	private static final int FIRST_PAGE_INDEX = 0;

	/**
	 * 각 페이지당 코멘트 리스트 처리
	 * 유저ID에 해당하는 코멘트를 첫번째 페이지에 추가
	 * 
	 * @param commentList 코멘트 엔티티 리스트
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId API ID
	 * @param page 페이지 번호
	 * @param userId 유저 ID
	 */
	public void getCommentListPerPage(
			List<DetailCommentViewEntity> commentList,
			String originalMediaType,
			String apiId,
			Integer page,
			String userId
			) {

		// 유저ID에 해당하는 코멘트를 추출
		DetailCommentViewEntity myCommentViewEntity = commentList.stream()
				.filter(e -> StringUtils.equals(e.getUserId(), userId))
				.findFirst()
				.orElse(commentViewRepository.findByOriginalMediaTypeAndApiIdAndUserId(originalMediaType, apiId, userId));

		// 유저ID에 해당하는 코멘트가 없는 경우 처리 종료
		if (ObjectUtils.isEmpty(myCommentViewEntity)) {
			return;
		}

		// 유저ID에 해당하는 코멘트 삭제
		commentList.removeIf(e -> StringUtils.equals(e.getUserId(), userId));

		// 첫번째 페이지의 경우 유저ID의 코멘트 추가
		if (page.equals(FIRST_PAGE_INDEX)) {
			commentList.add(0, myCommentViewEntity);
		}
	}
}
