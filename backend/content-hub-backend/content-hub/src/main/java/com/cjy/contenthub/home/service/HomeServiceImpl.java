package com.cjy.contenthub.home.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjy.contenthub.common.constants.CommonEnum;
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

	/**
	 * 콘텐츠 랭킹 정보를 조회
	 * 
	 * @param userId 유저 테이블 ID
	 * @return 콘텐츠 랭킹 서비스 DTO 리스트
	 */
	@Override
	public HomeRankingListServiceDto getContentRankings(Long userId) {
		
		String aniMediaType = CommonEnum.CommonMediaTypeEnum.MEDIA_TYPE_ANI.getMediaTypeCode();
		String dramaMediaType = CommonEnum.CommonMediaTypeEnum.MEDIA_TYPE_DRAMA.getMediaTypeCode();
		String movieMediaType = CommonEnum.CommonMediaTypeEnum.MEDIA_TYPE_MOVIE.getMediaTypeCode();
		String comicsMediaType = CommonEnum.CommonMediaTypeEnum.MEDIA_TYPE_COMICS.getMediaTypeCode();
		
		// 콘텐츠 뷰 엔티티 리스트 조회
		List<HomeRankingViewEntity> entityList = homeRankingViewRepository.findAll();
		
		// 엔티티 리스트를 서비스 DTO 리스트로 매핑
		List<HomeRankingServiceDto> serviceList =  mapper.entityListToServiceList(entityList);
		
		// 미디어 타입별로 필터링
		List<HomeRankingServiceDto> aniRankingList = serviceList.stream().filter(
				e -> e.getOriginalMediaType().equals(aniMediaType))
				.toList();
		List<HomeRankingServiceDto> dramaRankingList = serviceList.stream().filter(e -> e.getOriginalMediaType()
				.equals(dramaMediaType)).toList();
		List<HomeRankingServiceDto> movieRankingList = serviceList.stream().filter(
				e -> e.getOriginalMediaType().equals(movieMediaType))
				.toList();
		List<HomeRankingServiceDto> comicsRankingList = serviceList.stream().filter(
				e -> e.getOriginalMediaType().equals(comicsMediaType))
				.toList();
		
		// 로그인한 유저 정보가 존재하는 경우 위시리스트 여부 설정
		if (userId != null) {
			BusinessUtil.setWishlisted(
					aniRankingList, 
					aniMediaType, 
					userId, 
					dto -> dto.getApiId(),
					HomeRankingServiceDto::setWishlisted,
					wishlistRepository);
			BusinessUtil.setWishlisted(
					dramaRankingList, 
					dramaMediaType, 
					userId, 
					dto -> dto.getApiId(),
					HomeRankingServiceDto::setWishlisted,
					wishlistRepository);
			BusinessUtil.setWishlisted(
					movieRankingList, 
					movieMediaType, 
					userId, 
					dto -> dto.getApiId(),
					HomeRankingServiceDto::setWishlisted,
					wishlistRepository);
			BusinessUtil.setWishlisted(
					comicsRankingList, 
					comicsMediaType, 
					userId, 
					dto -> dto.getApiId(),
					HomeRankingServiceDto::setWishlisted,
					wishlistRepository);
		}
		
		// 필터링 된 서비스 DTO 반환
		return HomeRankingListServiceDto.builder()
				.aniRankingList(aniRankingList)
				.dramaRankingList(dramaRankingList)
				.movieRankingList(movieRankingList)
				.comicsRankingList(comicsRankingList)
				.build();
	}

}
