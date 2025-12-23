package com.cjy.contenthub.home.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjy.contenthub.core.constants.DomainEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.core.constants.DomainEnum.DisplayMediaTypeEnum;
import com.cjy.contenthub.core.constants.DomainEnum.MediaTypeMappingEnum;
import com.cjy.contenthub.core.shared.service.WishlistSharedService;
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

	/** 위시리스트 플래그 공유 서비스 */
	private final WishlistSharedService wishlistFlagSharedService;
	
	/** 화면 표시용 미디어 타입 리스트 */
	private static final List<String> DISPLAY_MEDIA_TYPE_LIST = Arrays.asList(DisplayMediaTypeEnum.values()).stream()
			.filter(cm -> !StringUtils.equalsAny(cm.getDisplayMediaTypeCode(),
					DisplayMediaTypeEnum.MEDIA_TYPE_PERSON.getDisplayMediaTypeCode())
					)
			.map(DisplayMediaTypeEnum::getDisplayMediaTypeCode)
			.toList();
	
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
		
		// 미디어 타입별 랭킹 리스트 초기화
		Map<String, List<HomeRankingServiceDto>> rankingMap = new HashMap<>();
		// 미디어 타입별 빈 맵 생성
		for (String displayMediaType: DISPLAY_MEDIA_TYPE_LIST) {
			rankingMap.put(displayMediaType, new ArrayList<>());
		}
		
		// 미디어 타입별로 필터링
		for (HomeRankingServiceDto serviceDto: serviceList) {
			String displayMediaType = serviceDto.getDisplayMediaType();
			// 미디어 타입이 랭킹 맵에 존재할 경우 해당 리스트에 추가
			if (rankingMap.containsKey(displayMediaType)) {
				rankingMap.get(displayMediaType).add(serviceDto);
			}
		}
		
		// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
		if (userId != null) {
			// 각 미디어 타입별로 위시리스트 여부 설정
			for (String rankingKey: rankingMap.keySet().stream().toList()) {
				// 화면 표시용 미디어 타입 -> 콘텐츠 미디어 타입
				String contentMediaType = MediaTypeMappingEnum.DISPLAY_CONTENT_MEDIA_TYPE_MAP.get(rankingKey);
				// 콘텐츠 미디어 타입 리스트 설정
				List<String> contentMediaTypeList;
				if (StringUtils.equals(rankingKey, DisplayMediaTypeEnum.MEDIA_TYPE_ANI.getDisplayMediaTypeCode())) {
					contentMediaTypeList = List.of(
							ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode(),
							ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode());
				} else {
					contentMediaTypeList = List.of(contentMediaType);
				}
				wishlistFlagSharedService.setWishlisted(
						rankingMap.get(rankingKey), 
						contentMediaTypeList,
						userId, 
						dto -> dto.getApiId(),
						HomeRankingServiceDto::setWishlisted,
						wishlistRepository);
			}
		}

		// 필터링 된 서비스 DTO 반환
		return HomeRankingListServiceDto.builder()
				.aniRankingList(rankingMap.get(DisplayMediaTypeEnum.MEDIA_TYPE_ANI.getDisplayMediaTypeCode()))
				.dramaRankingList(rankingMap.get(DisplayMediaTypeEnum.MEDIA_TYPE_DRAMA.getDisplayMediaTypeCode()))
				.documentaryRankingList(rankingMap.get(DisplayMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getDisplayMediaTypeCode()))
				.kidsRankingList(rankingMap.get(DisplayMediaTypeEnum.MEDIA_TYPE_KIDS.getDisplayMediaTypeCode()))
				.newsRankingList(rankingMap.get(DisplayMediaTypeEnum.MEDIA_TYPE_NEWS.getDisplayMediaTypeCode()))
				.varietyRankingList(rankingMap.get(DisplayMediaTypeEnum.MEDIA_TYPE_VARIETY.getDisplayMediaTypeCode()))
				.movieRankingList(rankingMap.get(DisplayMediaTypeEnum.MEDIA_TYPE_MOVIE.getDisplayMediaTypeCode()))
				.comicsRankingList(rankingMap.get(DisplayMediaTypeEnum.MEDIA_TYPE_COMICS.getDisplayMediaTypeCode()))
				.build();
	}

}
