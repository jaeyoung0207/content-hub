package com.cjy.contenthub.home.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjy.contenthub.common.constants.CommonEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.common.constants.CommonEnum.DisplayMediaTypeEnum;
import com.cjy.contenthub.common.controller.dto.CommonContentMediaTypeDto;
import com.cjy.contenthub.common.util.BusinessUtil;
import com.cjy.contenthub.home.mapper.HomeMapper;
import com.cjy.contenthub.home.repository.HomeRankingViewRepository;
import com.cjy.contenthub.home.repository.entity.HomeRankingViewEntity;
import com.cjy.contenthub.home.service.dto.HomeRankingListServiceDto;
import com.cjy.contenthub.home.service.dto.HomeRankingServiceDto;
import com.cjy.contenthub.wishlist.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;

/**
 * 홈 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class HomeServiceImpl implements HomeService {

	/** 콘텐츠 뷰 리포지토리 */
	private final HomeRankingViewRepository homeRankingViewRepository;

	/** 홈 매퍼 */
	private final HomeMapper mapper;

	/** 위시리스트 리포지토리 */
	private final WishlistRepository wishlistRepository;

	/** 비즈니스 유틸리티 */
	private final BusinessUtil businessUtil;

	/**
	 * 콘텐츠 랭킹 정보를 조회
	 * 
	 * @param userId 유저 테이블 ID
	 * @return 콘텐츠 랭킹 서비스 DTO 리스트
	 */
	@Override
	public HomeRankingListServiceDto getContentRankings(Long userId) {

		// 콘텐츠 뷰 엔티티 리스트 조회
		List<HomeRankingViewEntity> entityList = homeRankingViewRepository.findAll();

		// 엔티티 리스트를 서비스 DTO 리스트로 매핑
		List<HomeRankingServiceDto> serviceList =  mapper.entityListToServiceList(entityList);

		// 미디어 타입별로 필터링
		List<HomeRankingServiceDto> aniRankingList = serviceList.stream().filter(
				e -> e.getDisplayMediaType().equals(DisplayMediaTypeEnum.MEDIA_TYPE_ANI.getDisplayMediaTypeCode()))
				.toList();
		List<HomeRankingServiceDto> dramaRankingList = serviceList.stream().filter(e -> e.getDisplayMediaType()
				.equals(DisplayMediaTypeEnum.MEDIA_TYPE_DRAMA.getDisplayMediaTypeCode())).toList();
		List<HomeRankingServiceDto> documentaryRankingList = serviceList.stream()
				.filter(e -> e.getDisplayMediaType().equals(DisplayMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getDisplayMediaTypeCode())).toList();
		List<HomeRankingServiceDto> kidsRankingList = serviceList.stream()
				.filter(e -> e.getDisplayMediaType().equals(DisplayMediaTypeEnum.MEDIA_TYPE_KIDS.getDisplayMediaTypeCode())).toList();
		List<HomeRankingServiceDto> newsRankingList = serviceList.stream()
				.filter(e -> e.getDisplayMediaType().equals(DisplayMediaTypeEnum.MEDIA_TYPE_NEWS.getDisplayMediaTypeCode())).toList();
		List<HomeRankingServiceDto> varietyRankingList = serviceList.stream()
				.filter(e -> e.getDisplayMediaType().equals(DisplayMediaTypeEnum.MEDIA_TYPE_VARIETY.getDisplayMediaTypeCode())).toList();
		List<HomeRankingServiceDto> movieRankingList = serviceList.stream().filter(
				e -> e.getDisplayMediaType().equals(DisplayMediaTypeEnum.MEDIA_TYPE_MOVIE.getDisplayMediaTypeCode()))
				.toList();
		List<HomeRankingServiceDto> comicsRankingList = serviceList.stream().filter(
				e -> e.getDisplayMediaType().equals(DisplayMediaTypeEnum.MEDIA_TYPE_COMICS.getDisplayMediaTypeCode()))
				.toList();

		// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
		if (userId != null) {
			businessUtil.setWishlisted(
					aniRankingList, 
					List.of(ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode(), 
							ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode()),
					userId, 
					dto -> dto.getApiId(),
					HomeRankingServiceDto::setWishlisted,
					wishlistRepository);
			businessUtil.setWishlisted(
					dramaRankingList, 
					List.of(ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode()), 
					userId, 
					dto -> dto.getApiId(),
					HomeRankingServiceDto::setWishlisted,
					wishlistRepository);
			businessUtil.setWishlisted(
					documentaryRankingList, 
					List.of(ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode()), 
					userId, dto -> dto.getApiId(),
					HomeRankingServiceDto::setWishlisted, wishlistRepository);
			businessUtil.setWishlisted(
					kidsRankingList, 
					List.of(ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode()), 
					userId, 
					dto -> dto.getApiId(),
					HomeRankingServiceDto::setWishlisted, wishlistRepository);
			businessUtil.setWishlisted(
					newsRankingList, 
					List.of(ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode()), 
					userId, 
					dto -> dto.getApiId(),
					HomeRankingServiceDto::setWishlisted, wishlistRepository);
			businessUtil.setWishlisted(
					varietyRankingList, 
					List.of(ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode()),
					userId, 
					dto -> dto.getApiId(),
					HomeRankingServiceDto::setWishlisted, wishlistRepository);
			businessUtil.setWishlisted(
					movieRankingList, 
					List.of(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode()), 
					userId, 
					dto -> dto.getApiId(),
					HomeRankingServiceDto::setWishlisted,
					wishlistRepository);
			businessUtil.setWishlisted(
					comicsRankingList, 
					List.of(ContentMediaTypeEnum.MEDIA_TYPE_COMICS.getContentMediaTypeCode()), 
					userId, 
					dto -> dto.getApiId(),
					HomeRankingServiceDto::setWishlisted,
					wishlistRepository);
		}

		// 필터링 된 서비스 DTO 반환
		return HomeRankingListServiceDto.builder()
				.aniRankingList(aniRankingList)
				.dramaRankingList(dramaRankingList)
				.documentaryRankingList(documentaryRankingList)
				.kidsRankingList(kidsRankingList)
				.newsRankingList(newsRankingList)
				.varietyRankingList(varietyRankingList)
				.movieRankingList(movieRankingList)
				.comicsRankingList(comicsRankingList)
				.build();
	}

}
