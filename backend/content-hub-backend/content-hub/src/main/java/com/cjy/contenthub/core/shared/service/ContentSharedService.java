package com.cjy.contenthub.core.shared.service;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjy.contenthub.core.constants.DomainEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.core.constants.DomainEnum.DisplayMediaTypeEnum;
import com.cjy.contenthub.core.constants.DomainEnum.MediaTypeMappingEnum;
import com.cjy.contenthub.core.constants.DomainEnum.TmdbGenreEnum;
import com.cjy.contenthub.core.repository.ContentRepository;
import com.cjy.contenthub.core.repository.entity.ContentEntity;

import lombok.RequiredArgsConstructor;

/**
 * 콘텐츠 공유 서비스 클래스
 */
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ContentSharedService {
	
	/** 콘텐츠 엔티티 리포지토리 */
	private final ContentRepository contentRepository;
	
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

}
