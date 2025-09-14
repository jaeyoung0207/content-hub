package com.cjy.contenthub.search.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjy.contenthub.common.constants.CommonEnum.CommonMediaTypeEnum;
import com.cjy.contenthub.common.util.BusinessUtil;
import com.cjy.contenthub.search.controller.dto.SearchComicsResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchComicsResultDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;
import com.cjy.contenthub.wishlist.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;

/**
 * 검색 콘텐츠 서비스 구현 클래스 (캐시 미사용)
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SearchNoCacheServiceImpl implements SearchNoCacheService {

	/** 위시리스트 레포지토리 */
	private final WishlistRepository wishlistRepository;
	
	/** 비즈니스 유틸리티 */
	private final BusinessUtil businessUtil;

	/**
	 * 검색 결과에 위시리스트 여부 설정
	 * 
	 * @param serchVideoResponse 검색 결과 DTO
	 * @param userId             유저 ID
	 */
	@Override
	public void setWishlistFromVideoResponse(SearchVideoResponseDto serchVideoResponse, Long userId) {
		
		// 애니메이션 원본 미디어 타입 리스트 생성
		List<String> aniOriginalMediaTypeList = serchVideoResponse.getAniResults().stream()
				.map(dto -> dto.getOriginalMediaType())
				.distinct()
				.toList();
		
		// 각 미디어 타입별로 위시리스트 여부 설정
		businessUtil.setWishlisted(
				serchVideoResponse.getAniResults(), 
				aniOriginalMediaTypeList, 
				userId, 
				dto -> String.valueOf(dto.getId()),
				SearchTvResultsDto::setWishlisted, // (dto, wishlisted) -> dto.setWishlisted(wishlisted)
				wishlistRepository);
		businessUtil.setWishlisted(
				serchVideoResponse.getDramaResults(), 
				List.of(CommonMediaTypeEnum.MEDIA_TYPE_DRAMA.getMediaTypeCode()), 
				userId, 
				dto -> String.valueOf(dto.getId()),
				SearchTvResultsDto::setWishlisted,
				wishlistRepository);
		businessUtil.setWishlisted(
				serchVideoResponse.getMovieResults(), 
				List.of(CommonMediaTypeEnum.MEDIA_TYPE_MOVIE.getMediaTypeCode()), 
				userId, 
				dto -> String.valueOf(dto.getId()),
				SearchMovieResultsDto::setWishlisted, 
				wishlistRepository);
	}

	/**
	 * 애니 검색 결과에 위시리스트 여부 설정
	 * 
	 * @param searchTvResponse 검색 결과 DTO
	 * @param userId           유저 ID
	 */
	@Override
	public void setWishlistFromAniResponse(SearchTvResponseDto searchTvResponse, Long userId) {
		
		// 애니메이션 원본 미디어 타입 리스트 생성
		List<String> aniOriginalMediaTypeList = searchTvResponse.getAniResults().stream()
				.map(dto -> dto.getOriginalMediaType())
				.distinct()
				.toList();
		
		// 애니메이션 검색 결과에 위시리스트 여부 설정
		businessUtil.setWishlisted(
				searchTvResponse.getAniResults(), 
				aniOriginalMediaTypeList, 
				userId, 
				dto -> String.valueOf(dto.getId()),
				SearchTvResultsDto::setWishlisted,
				wishlistRepository);
	}

	/**
	 * 드라마 검색 결과에 위시리스트 여부 설정
	 * 
	 * @param searchTvResponse 검색 결과 DTO
	 * @param userId           유저 ID
	 */
	@Override
	public void setWishlistFromDramaResponse(SearchTvResponseDto searchTvResponse, Long userId) {
		
        // 드라마 검색 결과에 위시리스트 여부 설정		
		businessUtil.setWishlisted(
				searchTvResponse.getDramaResults(), 
				List.of(CommonMediaTypeEnum.MEDIA_TYPE_DRAMA.getMediaTypeCode()), 
				userId, 
				dto -> String.valueOf(dto.getId()),
				SearchTvResultsDto::setWishlisted,
				wishlistRepository);
	}

	/**
	 * 영화 검색 결과에 위시리스트 여부 설정
	 * 
	 * @param searchMovieResponse 검색 결과 DTO
	 * @param userId              유저 ID
	 */
	@Override
	public void setWishlistFromMovieResponse(SearchMovieResponseDto searchMovieResponse, Long userId) {
		
		// 영화 검색 결과에 위시리스트 여부 설정
		businessUtil.setWishlisted(
				searchMovieResponse.getMovieResults(), 
				List.of(CommonMediaTypeEnum.MEDIA_TYPE_MOVIE.getMediaTypeCode()), 
				userId, 
				dto -> String.valueOf(dto.getId()),
				SearchMovieResultsDto::setWishlisted,
				wishlistRepository);
	}

	/**
	 * 만화 검색 결과에 위시리스트 여부 설정
	 * 
	 * @param searchComicsResponse 검색 결과 DTO
	 * @param userId               유저 ID
	 */
	@Override
	public void setWishlistFromComicsResponse(SearchComicsResponseDto searchComicsResponse, Long userId) {
		
		// 만화 검색 결과에 위시리스트 여부 설정
		businessUtil.setWishlisted(
				searchComicsResponse.getComicsResults(), 
				List.of(CommonMediaTypeEnum.MEDIA_TYPE_COMICS.getMediaTypeCode()), 
				userId, 
				dto -> String.valueOf(dto.getId()),
				SearchComicsResultDto::setWishlisted,
				wishlistRepository);
	}

}
