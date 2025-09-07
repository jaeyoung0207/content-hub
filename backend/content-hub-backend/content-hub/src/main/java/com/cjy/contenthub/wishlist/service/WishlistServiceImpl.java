package com.cjy.contenthub.wishlist.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjy.contenthub.common.constants.CommonEnum;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMediaTypeEnum;
import com.cjy.contenthub.common.exception.CommonBusinessException;
import com.cjy.contenthub.common.repository.ContentRepository;
import com.cjy.contenthub.common.repository.entity.ContentEntity;
import com.cjy.contenthub.common.repository.entity.UserEntity;
import com.cjy.contenthub.detail.helper.DetailCommentHelper;
import com.cjy.contenthub.wishlist.mapper.WishlistMapper;
import com.cjy.contenthub.wishlist.repository.WishlistRepository;
import com.cjy.contenthub.wishlist.repository.entity.WishlistEntity;
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
	
	/** 상세 페이지 헬퍼 */
	private final DetailCommentHelper detailCommentHelper;
	
	/** 위시리스트 매퍼 */
	private final WishlistMapper wishlistMapper;
	
	/**
	 * 위시리스트에 콘텐츠 추가
	 * 
	 * @param saveServiceDto
	 * @return 추가 성공 여부
	 */
	@Override
	public boolean addToWishlist(WishlistServiceDto saveServiceDto) {
		
		// Content 조회 또는 생성
		ContentEntity content = detailCommentHelper.getContentEntity(
				saveServiceDto.getOriginalMediaType(), saveServiceDto.getApiId(), saveServiceDto.getTitle(), saveServiceDto.getThumbnailImageUrl());

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
			log.warn("Wishlist entry already exists for userId: {}, originalMediaType: {}, apiId: {}", 
					saveServiceDto.getUserId(), saveServiceDto.getOriginalMediaType(), saveServiceDto.getApiId());
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
		ContentEntity content = contentRepository.findByOriginalMediaTypeAndApiId(saveServiceDto.getOriginalMediaType(), saveServiceDto.getApiId());
		
		// Content가 존재하지 않는 경우 예외 처리
		if (content == null) {
			throw new CommonBusinessException(
					"Content not found for originalMediaType: " + saveServiceDto.getOriginalMediaType() + ", apiId: " + saveServiceDto.getApiId());
		}
		// 위시리스트에서 해당 항목 조회
		List<WishlistEntity> wishlistList = wishlistRepository.findByUser_UserIdAndContent_ContentId(saveServiceDto.getUserId(), content.getContentId());
		
		// 존재하는 경우에만 삭제
		if (!wishlistList.isEmpty()) {
			wishlistRepository.deleteAll(wishlistList);
			return true;
		} else {
			log.warn("No wishlist entry found for userId: {}, originalMediaType: {}, apiId: {}", 
					saveServiceDto.getUserId(), saveServiceDto.getOriginalMediaType(), saveServiceDto.getApiId());
			return false;
		}
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
		List<ContentEntity> wishlistContentList = wishlistRepository.getWishlistByUserId(userId);
		
		// 위시리스트의 콘텐츠가 존재하는 경우
		if (wishlistContentList != null && !wishlistContentList.isEmpty()) {
			// 결과 리스트 매핑
			List<WishlistServiceDto> wisilistList = wishlistMapper.contentListToServiceList(wishlistContentList);
			// userId 설정
			wisilistList.forEach(wishlist -> wishlist.setUserId(userId));
			
			// 미디어 타입별로 필터링
			List<WishlistServiceDto> aniWishlist = wisilistList.stream().filter(e -> e.getOriginalMediaType()
					.equals(CommonEnum.CommonMediaTypeEnum.MEDIA_TYPE_ANI.getMediaTypeCode())).toList();
			List<WishlistServiceDto> dramaWisilist = wisilistList.stream().filter(e -> e.getOriginalMediaType()
					.equals(CommonEnum.CommonMediaTypeEnum.MEDIA_TYPE_DRAMA.getMediaTypeCode())).toList();
			List<WishlistServiceDto> movieWisilist = wisilistList.stream().filter(e -> e.getOriginalMediaType()
					.equals(CommonEnum.CommonMediaTypeEnum.MEDIA_TYPE_MOVIE.getMediaTypeCode())).toList();
			List<WishlistServiceDto> comicsWisilist = wisilistList.stream().filter(e -> e.getOriginalMediaType()
					.equals(CommonEnum.CommonMediaTypeEnum.MEDIA_TYPE_COMICS.getMediaTypeCode())).toList();
			
			// 결과 반환
			return WishlistListServiceDto.builder()
					.aniWishlist(aniWishlist)
					.dramaWishlist(dramaWisilist)
					.movieWishlist(movieWisilist)
					.comicsWishlist(comicsWisilist)
					.build();
		}
		// 위시리스트에 콘텐츠가 없는 경우 빈 리스트 반환
		return new WishlistListServiceDto();
	}

}
