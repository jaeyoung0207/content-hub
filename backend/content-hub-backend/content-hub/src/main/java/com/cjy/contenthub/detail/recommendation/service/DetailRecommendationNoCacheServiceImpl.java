package com.cjy.contenthub.detail.recommendation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjy.contenthub.common.constants.CommonEnum.CommonMediaTypeEnum;
import com.cjy.contenthub.common.util.BusinessUtil;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsComicsResponseDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsComicsResultDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsMovieDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsMovieResultsDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsTvDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsTvResultsDto;
import com.cjy.contenthub.wishlist.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;

/**
 * 상세 추천 서비스 구현 클래스 (캐시 미사용)
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class DetailRecommendationNoCacheServiceImpl implements DetailRecommendationNoCacheService {

	/** 위시리스트 레포지토리 */
	private final WishlistRepository wishlistRepository;

	/** 비즈니스 유틸리티 */
	private final BusinessUtil businessUtil;

	/**
	 * TV 상세 추천 응답에서 위시리스트 설정
	 * 
	 * @param tvResponse TV 추천 응답 DTO
	 * @param userId     유저 ID
	 */
	@Override
	public void setWishlistFromTvResponse(DetailRecommendationsTvDto tvResponse, Long userId) {

		// 원본 미디어 타입 리스트 생성
		List<String> originalMediaTypeList = List.of(
				CommonMediaTypeEnum.MEDIA_TYPE_ANI.getMediaTypeCode(),
				CommonMediaTypeEnum.MEDIA_TYPE_DRAMA.getMediaTypeCode()
				);

		// 위시리스트 여부 설정
		businessUtil.setWishlisted(
				tvResponse.getResults(), 
				originalMediaTypeList, 
				userId, 
				dto -> String.valueOf(dto.getId()),
				DetailRecommendationsTvResultsDto::setWishlisted,
				wishlistRepository);
	}

	/**
	 * 영화 상세 추천 응답에서 위시리스트 설정
	 * 
	 * @param movieResponse 영화 추천 응답 DTO
	 * @param userId        유저 ID
	 */
	@Override
	public void setWishlistFromMovieResponse(DetailRecommendationsMovieDto movieResponse, Long userId) {
		
		// 위시리스트 여부 설정
		businessUtil.setWishlisted(
				movieResponse.getResults(), 
				List.of(CommonMediaTypeEnum.MEDIA_TYPE_MOVIE.getMediaTypeCode()), 
				userId, 
				dto -> String.valueOf(dto.getId()),
				DetailRecommendationsMovieResultsDto::setWishlisted,
				wishlistRepository);
	}

	/**
	 * 만화 상세 추천 응답에서 위시리스트 설정
	 * 
	 * @param comicsResponse 만화 추천 응답 DTO
	 * @param userId         유저 ID
	 */
	@Override
	public void setWishlistFromComicsResponse(DetailRecommendationsComicsResponseDto comicsResponse, Long userId) {
		
		// 위시리스트 여부 설정
		businessUtil.setWishlisted(
				comicsResponse.getResults(), 
				List.of(CommonMediaTypeEnum.MEDIA_TYPE_COMICS.getMediaTypeCode()), 
				userId, 
				dto -> String.valueOf(dto.getId()),
				DetailRecommendationsComicsResultDto::setWishlisted,
				wishlistRepository);
	}

}
