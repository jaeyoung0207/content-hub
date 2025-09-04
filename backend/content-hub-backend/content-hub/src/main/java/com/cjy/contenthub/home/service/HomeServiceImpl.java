package com.cjy.contenthub.home.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjy.contenthub.common.constants.CommonEnum;
import com.cjy.contenthub.home.mapper.HomeMapper;
import com.cjy.contenthub.home.repository.HomeRankingViewRepository;
import com.cjy.contenthub.home.repository.entity.HomeRankingViewEntity;
import com.cjy.contenthub.home.service.dto.HomeRankingListServiceDto;
import com.cjy.contenthub.home.service.dto.HomeRankingServiceDto;

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

	/**
	 * 콘텐츠 랭킹 정보를 조회
	 * 
	 * @return 콘텐츠 랭킹 서비스 DTO 리스트
	 */
	@Override
	public HomeRankingListServiceDto getContentRankings() {
		
		// 콘텐츠 뷰 엔티티 리스트 조회
		List<HomeRankingViewEntity> entityList = homeRankingViewRepository.findAll();
		
		// 엔티티 리스트를 서비스 DTO 리스트로 매핑
		List<HomeRankingServiceDto> serviceList =  mapper.entityListToServiceList(entityList);
		
		// 미디어 타입별로 필터링
		List<HomeRankingServiceDto> aniRankingList = serviceList.stream().filter(
				e -> e.getOriginalMediaType().equals(CommonEnum.CommonMediaTypeEnum.MEDIA_TYPE_ANI.getMediaTypeCode()))
				.toList();
		List<HomeRankingServiceDto> movieRankingList = serviceList.stream().filter(
				e -> e.getOriginalMediaType().equals(CommonEnum.CommonMediaTypeEnum.MEDIA_TYPE_MOVIE.getMediaTypeCode()))
				.toList();
		List<HomeRankingServiceDto> dramaRankingList = serviceList.stream().filter(e -> e.getOriginalMediaType()
				.equals(CommonEnum.CommonMediaTypeEnum.MEDIA_TYPE_DRAMA.getMediaTypeCode())).toList();
		List<HomeRankingServiceDto> comicsRankingList = serviceList.stream().filter(
				e -> e.getOriginalMediaType().equals(CommonEnum.CommonMediaTypeEnum.MEDIA_TYPE_COMICS.getMediaTypeCode()))
				.toList();
		
		// 필터링 된 서비스 DTO 반환
		return HomeRankingListServiceDto.builder()
				.aniRankingList(aniRankingList)
				.dramaRankingList(dramaRankingList)
				.movieRankingList(movieRankingList)
				.comicsRankingList(comicsRankingList)
				.build();
	}

}
