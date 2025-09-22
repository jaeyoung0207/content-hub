package com.cjy.contenthub.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.cjy.contenthub.common.constants.CommonEnum.AniListGenreEnum;
import com.cjy.contenthub.common.constants.CommonEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.common.constants.CommonEnum.DisplayMediaTypeEnum;
import com.cjy.contenthub.common.constants.CommonEnum.MediaTypeMappingEnum;
import com.cjy.contenthub.common.constants.CommonEnum.TmdbGenreEnum;
import com.cjy.contenthub.common.function.WishlistedSetter;
import com.cjy.contenthub.common.repository.ContentRepository;
import com.cjy.contenthub.common.repository.entity.ContentEntity;
import com.cjy.contenthub.wishlist.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;

/**
 * 비즈니스 로직 관련 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
public class BusinessUtil {
	
	/** 콘텐츠 엔티티 리포지토리 */
	private final ContentRepository contentRepository;

	/**
	 * 검색 결과 리스트에 위시리스트 여부를 설정하는 유틸리티 메서드
	 *
	 * @param resultsList        검색 결과 리스트
	 * @param contentMediaTypeList  컨텐츠 미디어 타입
	 * @param userId             유저 테이블 ID
	 * @param idExtractor        검색 결과에서 API ID를 추출하는 함수
	 * @param wishlistedSetter   검색 결과에 찜 여부를 설정하는 함수
	 * @param wishlistRepository 찜 목록을 조회하는 리포지토리
	 * @param <T>                검색 결과 객체 타입
	 */
	public <T> void setWishlisted(
			List<T> resultsList,
			List<String> contentMediaTypeList,
			Long userId,
			Function<T, String> idExtractor,
			WishlistedSetter<T> wishlistedSetter,
			WishlistRepository wishlistRepository) {

		// 검색 결과가 없으면 처리 종료
		if (resultsList == null || resultsList.isEmpty()) {
			return;
		}
		
		// 검색 결과의 모든 항목에 대해 위시리스트 여부를 false로 초기화
		for (T results : resultsList) {
			wishlistedSetter.setWishlisted(results, false);
		}
		
		// 검색 결과에서 API ID 리스트 추출
		List<String> apiIdList = resultsList.stream().map(idExtractor).toList();
		
		// 유저의 위시리스트 목록에서 해당 미디어 타입과 API ID에 해당하는 콘텐츠 조회
		List<ContentEntity> contentList = wishlistRepository.getContentListByUserIdAndContentMediaTypeInAndApiIdIn(userId, contentMediaTypeList, apiIdList);

		// 위시리스트에 등록된 콘텐츠가 없으면 처리 종료
		if (contentList == null || contentList.isEmpty()) {
			return;
		}

		// 검색 결과와 위시리스트 콘텐츠를 비교하여 위시리스트 여부 설정
		for (T results : resultsList) {
			for (ContentEntity content : contentList) {
				if (contentMediaTypeList.contains(content.getContentMediaType())
						&& StringUtils.equals(content.getApiId(), idExtractor.apply(results))) {
					wishlistedSetter.setWishlisted(results, true);
					break;
				}
			}
		}
	}
	
	/**
	 * 콘텐츠 엔티티 조회 또는 등록
	 * 
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param apiId             API ID
	 * @param title             제목
	 * @param thumbnailImageUrl 썸네일 이미지 URL
	 * @param genreIdList       장르 ID 리스트
	 * @param displaymediaType        미디어 타입
	 * @return 콘텐츠 엔티티
	 */
	public ContentEntity getContentEntity(String contentMediaType, String apiId, String title, String thumbnailImageUrl, List<Integer> genreIdList, String displaymediaType) {
		
		// 콘텐츠 엔티티 조회
		ContentEntity content = contentRepository.findByContentMediaTypeAndApiId(contentMediaType, apiId);
		
		// 콘텐츠 엔티티가 존재하지 않는 경우 콘텐츠 테이블 등록
		if (ObjectUtils.isEmpty(content)) {
			// 미디어 타입 설정
			String convertedDisplayMediaType = "";
			// 명시된 미디어 타입이 있는 경우 해당 값 사용
			if (StringUtils.isNotEmpty(displaymediaType)) {
				convertedDisplayMediaType = displaymediaType;
			} 
			// 명시된 미디어 타입이 없는 경우 장르 정보를 기반으로 미디어 타입 결정
			else {
				// 애니메이션 장르가 포함된 영화는 미디어 타입을 애니메이션으로 설정
				if (StringUtils.equals(contentMediaType, ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode())
						&& genreIdList.stream().anyMatch(id -> id.equals(TmdbGenreEnum.GENRE_ANI.getGenreId()))) {
					convertedDisplayMediaType = DisplayMediaTypeEnum.MEDIA_TYPE_ANI.getDisplayMediaTypeCode();
				}
				// 그 외에는 컨텐츠 미디어 타입에 해당하는 화면 표시용 미디어 타입 사용
				else {
					convertedDisplayMediaType = MediaTypeMappingEnum.CONTENT_DISPLAY_MEDIA_TYPE_MAP.get(contentMediaType);
				}
			}
            // 콘텐츠 엔티티 생성 및 저장
			ContentEntity newContent = ContentEntity.builder()
					.contentMediaType(contentMediaType)
					.displayMediaType(convertedDisplayMediaType)
					.apiId(apiId)
					.title(title)
					.thumbnailImageUrl(thumbnailImageUrl)
					.build();
			content = contentRepository.save(newContent);
		}
		return content;
	}
	
	/**
	 * Anilist 장르명을 TMDB 장르 ID로 매핑
	 * 
	 * @param anilistGenreList Anilist 장르명 리스트
	 * @return TMDB 장르 ID 리스트
	 */
	public List<Integer> genreMappingFromAniListToTmdb(List<String> anilistGenreList) {
		
		// 결과 리스트
		List<Integer> genreIdList = new ArrayList<>();
		
		// 입력 장르명이 없는 경우 빈 리스트 반환
		if (anilistGenreList == null || anilistGenreList.isEmpty()) {
			return genreIdList;
		}
		
		// Anilist 장르명을 Tmdb 장르 ID로 매핑
		for (String anilistGenre : anilistGenreList) {
			Integer genreId = AniListGenreEnum.GENRE_EN_ID_MAP.get(anilistGenre);
			if (genreId != null) {
				genreIdList.add(genreId);
			}
		}
		return genreIdList;
	}

}
