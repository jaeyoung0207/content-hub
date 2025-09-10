package com.cjy.contenthub.common.util;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.cjy.contenthub.common.function.WishlistedSetter;
import com.cjy.contenthub.common.repository.entity.ContentEntity;
import com.cjy.contenthub.wishlist.repository.WishlistRepository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 비즈니스 로직 관련 유틸리티 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BusinessUtil {

	/**
	 * 검색 결과 리스트에 위시리스트 여부를 설정하는 유틸리티 메서드
	 *
	 * @param resultsList        검색 결과 리스트
	 * @param originalMediaType  원본 미디어 타입
	 * @param userId             유저 테이블 ID
	 * @param idExtractor        검색 결과에서 API ID를 추출하는 함수
	 * @param wishlistedSetter   검색 결과에 찜 여부를 설정하는 함수
	 * @param wishlistRepository 찜 목록을 조회하는 리포지토리
	 * @param <T>                검색 결과 객체 타입
	 */
	public static <T> void setWishlisted(
			List<T> resultsList,
			String originalMediaType,
			Long userId,
			Function<T, String> idExtractor,
			WishlistedSetter<T> wishlistedSetter,
			WishlistRepository wishlistRepository) {

		// 검색 결과가 없으면 처리 종료
		if (resultsList == null || resultsList.isEmpty()) {
			return;
		}
		// 검색 결과에서 API ID 리스트 추출
		List<String> apiIdList = resultsList.stream().map(idExtractor).toList();
		
		// 유저의 위시리스트 목록에서 해당 미디어 타입과 API ID에 해당하는 콘텐츠 조회
		List<ContentEntity> contentList = wishlistRepository.getWishlistedContent(userId, originalMediaType, apiIdList);

		// 위시리스트에 등록된 콘텐츠가 없으면 처리 종료
		if (contentList == null || contentList.isEmpty()) {
			return;
		}

		// 검색 결과와 위시리스트 콘텐츠를 비교하여 위시리스트 여부 설정
		for (T results : resultsList) {
			for (ContentEntity content : contentList) {
				if (StringUtils.equals(content.getOriginalMediaType(), originalMediaType)
						&& StringUtils.equals(content.getApiId(), idExtractor.apply(results))) {
					wishlistedSetter.setWishlisted(results, true);
					break;
				}
			}
		}

	}

}
