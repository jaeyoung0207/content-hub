package com.cjy.contenthub.detail.comments.helper;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cjy.contenthub.detail.comments.repository.DetailCommentsViewRepository;
import com.cjy.contenthub.detail.comments.repository.entity.DetailCommentsViewEntity;

import lombok.RequiredArgsConstructor;

/**
 * 상세 화면 헬퍼 클래스
 */
@Component
@RequiredArgsConstructor
public class DetailCommentsHelper {

	/** 코멘트 뷰 엔티티 리포지토리 */
	private final DetailCommentsViewRepository commentsViewRepository;
	
	/** TMDB API TV 추천 작품 API 패스 */
	@Value("${tmdb.url.tvRecommendationsPath}")
	private String tvRecommendationsPath;

	/** TMDB API Movie 추천 작품 API 패스 */
	@Value("${tmdb.url.movieRecommendationsPath}")
	private String movieRecommendationsPath;

	/** 첫번째 페이지 인덱스 */
	private static final int FIRST_PAGE_INDEX = 0;

	/**
	 * 각 페이지당 코멘트 리스트 처리
	 * 유저ID에 해당하는 코멘트를 첫번째 페이지에 추가
	 * 
	 * @param commentList 코멘트 엔티티 리스트
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param apiId API ID
	 * @param page 페이지 번호
	 * @param providerId 프로바이더 ID
	 */
	public void getCommentListPerPage(
			List<DetailCommentsViewEntity> commentList,
			String contentMediaType,
			String apiId,
			Integer page,
			String providerId
			) {

		// 유저ID에 해당하는 코멘트를 추출
		DetailCommentsViewEntity myCommentViewEntity = commentList.stream()
				.filter(e -> StringUtils.equals(e.getProviderId(), providerId))
				.findFirst()
				.orElse(commentsViewRepository.findByContentMediaTypeAndApiIdAndProviderId(contentMediaType, apiId, providerId));

		// 유저ID에 해당하는 코멘트가 없는 경우 처리 종료
		if (ObjectUtils.isEmpty(myCommentViewEntity)) {
			return;
		}

		// 유저ID에 해당하는 코멘트 삭제
		commentList.removeIf(e -> StringUtils.equals(e.getProviderId(), providerId));

		// 첫번째 페이지의 경우 유저ID의 코멘트 추가
		if (page.equals(FIRST_PAGE_INDEX)) {
			commentList.add(0, myCommentViewEntity);
		}
	}
	
}
