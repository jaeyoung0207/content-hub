package com.cjy.contenthub.wishlist.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjy.contenthub.common.constants.CommonEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.common.constants.CommonEnum.DisplayMediaTypeEnum;
import com.cjy.contenthub.common.constants.CommonEnum.MessagesErrorEnum;
import com.cjy.contenthub.common.constants.CommonEnum.MessagesWarnEnum;
import com.cjy.contenthub.common.exception.CommonBusinessException;
import com.cjy.contenthub.common.repository.ContentRepository;
import com.cjy.contenthub.common.repository.entity.ContentEntity;
import com.cjy.contenthub.common.repository.entity.UserEntity;
import com.cjy.contenthub.common.util.BusinessUtil;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.wishlist.mapper.WishlistMapper;
import com.cjy.contenthub.wishlist.repository.WishlistRepository;
import com.cjy.contenthub.wishlist.repository.entity.WishlistEntity;
import com.cjy.contenthub.wishlist.service.dto.WishlistCheckResultServiceDto;
import com.cjy.contenthub.wishlist.service.dto.WishlistListServiceDto;
import com.cjy.contenthub.wishlist.service.dto.WishlistServiceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 위시리스트 서비스 클래스
 */
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class WishlistServiceImpl implements WishlistService {
	
	/** content 레포지토리 */
	private final ContentRepository contentRepository;
	
	/** wishlist 레포지토리 */
	private final WishlistRepository wishlistRepository;
	
	/** 위시리스트 매퍼 */
	private final WishlistMapper wishlistMapper;
	
	/** 비즈니스 유틸리티 */
	private final BusinessUtil businessUtil;
	
	/** 메시지 유틸 */
	private final MessageUtil messageUtil;
	
	@Value("${app.wishlist.maxRegistrationSize}")
	private int maxWishlistEntries;
	
	/**
	 * 위시리스트에 콘텐츠 추가
	 * 
	 * @param saveServiceDto
	 * @return 추가 성공 여부
	 */
	@Override
	public boolean addToWishlist(WishlistServiceDto saveServiceDto) {
		
		// Content 조회 또는 생성
		ContentEntity content = businessUtil.getContentEntity(
				saveServiceDto.getContentMediaType(), saveServiceDto.getApiId(),
				saveServiceDto.getTitle(), saveServiceDto.getThumbnailImageUrl(),
				saveServiceDto.getGenreIds(), saveServiceDto.getDisplayMediaType());

		// 위시리스트에서 해당 항목 조회
		List<WishlistEntity> wishlistList = wishlistRepository.findByUser_UserIdAndContent_ContentId(saveServiceDto.getUserId(), content.getContentId());
		
		// 존재하지 않는 경우에만 추가
		if (wishlistList.isEmpty()) {
			WishlistEntity wishlist = WishlistEntity.builder()
					.user(UserEntity.builder().userId(saveServiceDto.getUserId()).build())
					.content(content)
					.build();
			return ObjectUtils.isNotEmpty(wishlistRepository.save(wishlist));
		} else {
			Object[] messageParams = { saveServiceDto.getUserId(), saveServiceDto.getContentMediaType(), saveServiceDto.getApiId() };
			log.warn(messageUtil.getMessageKO(
					MessagesWarnEnum.WARN_WISHLIST_WISHLIST_ALREADY_EXISTS.getMessageCode(), messageParams));
			return false;
		}
	}

	/**
	 * 위시리스트에서 콘텐츠 제거
	 * 
	 * @param saveServiceDto
	 * @return 제거 성공 여부
	 */
	@Override
	public boolean removeFromWishlist(WishlistServiceDto saveServiceDto) {
		
		// Content 조회
		ContentEntity content = contentRepository.findByContentMediaTypeAndApiId(saveServiceDto.getContentMediaType(), saveServiceDto.getApiId());
		
		// Content가 존재하지 않는 경우 예외 처리
		if (content == null) {
			Object[] messageParams = { saveServiceDto.getContentMediaType(), saveServiceDto.getApiId() };
			throw new CommonBusinessException(
					messageUtil.getMessageKO(MessagesErrorEnum.ERROR_COMMON_CONTENT_NOT_FOUND.getMessageCode(), messageParams));
		}
		// 위시리스트에서 해당 항목 조회
		List<WishlistEntity> wishlistList = wishlistRepository.findByUser_UserIdAndContent_ContentId(saveServiceDto.getUserId(), content.getContentId());
		
		// 존재하는 경우에만 삭제
		if (!wishlistList.isEmpty()) {
			wishlistRepository.deleteAll(wishlistList);
			return true;
		} else {
			Object[] messageParams = { saveServiceDto.getUserId(), saveServiceDto.getContentMediaType(), saveServiceDto.getApiId() };
			log.warn(messageUtil.getMessageKO(MessagesWarnEnum.WARN_WISHLIST_WISHLIST_NOT_FOUND.getMessageCode(), messageParams));
			return false;
		}
	}
	
	/**
	 * 위시리스트 체크 처리
	 * 
	 * @param userId           유저 테이블 ID
	 * @param apiId            API ID
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @return 중복 작품 리스트
	 */
	@Override
	public WishlistCheckResultServiceDto checkWishlist(Long userId, String apiId, String contentMediaType) {
		
		// 유저가 등록한 위시리스트 개수 조회
		long wishlistCount = wishlistRepository.countByUser_UserId(userId);
		
		// 등록된 위시리스트가 지정한 개수를 초과한 경우
		if (wishlistCount > maxWishlistEntries) {
			return WishlistCheckResultServiceDto.builder()
					.maxWishlistCount(maxWishlistEntries)
					.build();
		}
		
		List<WishlistServiceDto> resultList = new ArrayList<>();
		
		List<String> contentMediaTypeList = new ArrayList<>();
		
		List<String> tvContentMediaTypeList = ContentMediaTypeEnum.getBelongToTvList();
		
		// TV 콘텐츠인 경우
		if (tvContentMediaTypeList.contains(contentMediaType)) {
			contentMediaTypeList = tvContentMediaTypeList;
		} else {
			contentMediaTypeList.add(contentMediaType);
		}
		
		// 위시리스트에서 해당 작품 중복 리스트 조회
		List<ContentEntity> contentList = wishlistRepository.getContentListByUserIdAndApiIdAndContentMediaTypeIn(userId, apiId, contentMediaTypeList);
		
		// 존재하는 경우 결과 리스트에 추가
		if (!contentList.isEmpty()) {
			// 동일 미디어 타입이 존재하는 경우 리턴
			List<String> contentMediaTypes = contentList.stream().map(ContentEntity::getContentMediaType).toList();
			if (contentMediaTypes.contains(contentMediaType)) {
				return new WishlistCheckResultServiceDto();
			}
			// 다른 미디어 타입이 존재하는 경우 결과 리스트에 추가
			for (ContentEntity content : contentList) {
				WishlistServiceDto wishlistService = WishlistServiceDto.builder()
				.userId(userId)
				.contentMediaType(content.getContentMediaType())
				.apiId(content.getApiId())
				.displayMediaType(content.getDisplayMediaType())
				.title(content.getTitle())
				.thumbnailImageUrl(content.getThumbnailImageUrl())
				.build();
				resultList.add(wishlistService);
			}
			return WishlistCheckResultServiceDto.builder()
					.wishlists(resultList)
					.build();
		}
		return new WishlistCheckResultServiceDto();
	}

	/**
	 * 유저가 등록한 위시리스트 조회
	 * 
	 * @param userId 유저 테이블 ID
	 * @return 위시리스트 항목 정보
	 */
	@Override
	public WishlistListServiceDto getWishlist(Long userId) {
		
		// 위시리스트에서 콘텐츠 조회
		List<ContentEntity> wishlistContentList = wishlistRepository.getContentListByUserId(userId);
		
		// 위시리스트의 콘텐츠가 존재하는 경우
		if (wishlistContentList != null && !wishlistContentList.isEmpty()) {
			// 결과 리스트 매핑
			List<WishlistServiceDto> wisilistList = wishlistMapper.contentListToServiceList(wishlistContentList);
			// userId 설정
			wisilistList.forEach(wishlist -> wishlist.setUserId(userId));
			
			// 미디어 타입별로 필터링
			List<WishlistServiceDto> aniWishlist = wisilistList.stream().filter(e -> e.getDisplayMediaType()
					.equals(DisplayMediaTypeEnum.MEDIA_TYPE_ANI.getDisplayMediaTypeCode())).toList();
			List<WishlistServiceDto> dramaWisilist = wisilistList.stream().filter(e -> e.getDisplayMediaType()
					.equals(DisplayMediaTypeEnum.MEDIA_TYPE_DRAMA.getDisplayMediaTypeCode())).toList();
			List<WishlistServiceDto> documentaryWisilist = wisilistList.stream().filter(e -> e.getDisplayMediaType()
					.equals(DisplayMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getDisplayMediaTypeCode())).toList();
			List<WishlistServiceDto> kidsWisilist = wisilistList.stream().filter(e -> e.getDisplayMediaType()
					.equals(DisplayMediaTypeEnum.MEDIA_TYPE_KIDS.getDisplayMediaTypeCode())).toList();
			List<WishlistServiceDto> newsWisilist = wisilistList.stream().filter(e -> e.getDisplayMediaType()
					.equals(DisplayMediaTypeEnum.MEDIA_TYPE_NEWS.getDisplayMediaTypeCode())).toList();
			List<WishlistServiceDto> varietyWisilist = wisilistList.stream().filter(e -> e.getDisplayMediaType()
					.equals(DisplayMediaTypeEnum.MEDIA_TYPE_VARIETY.getDisplayMediaTypeCode())).toList();
			List<WishlistServiceDto> movieWisilist = wisilistList.stream().filter(e -> e.getDisplayMediaType()
					.equals(DisplayMediaTypeEnum.MEDIA_TYPE_MOVIE.getDisplayMediaTypeCode())).toList();
			List<WishlistServiceDto> comicsWisilist = wisilistList.stream().filter(e -> e.getDisplayMediaType()
					.equals(DisplayMediaTypeEnum.MEDIA_TYPE_COMICS.getDisplayMediaTypeCode())).toList();
			
			// 결과 반환
			return WishlistListServiceDto.builder()
					.aniWishlist(aniWishlist)
					.dramaWishlist(dramaWisilist)
					.documentaryWishlist(documentaryWisilist)
					.kidsWishlist(kidsWisilist)
					.newsWishlist(newsWisilist)
					.varietyWishlist(varietyWisilist)
					.movieWishlist(movieWisilist)
					.comicsWishlist(comicsWisilist)
					.build();
		}
		// 위시리스트에 콘텐츠가 없는 경우 빈 리스트 반환
		return new WishlistListServiceDto();
	}

}
