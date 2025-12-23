package com.cjy.contenthub.detail.recommendation.sevice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cjy.contenthub.core.constants.DomainEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.core.shared.service.WishlistSharedService;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsComicsResponseDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsComicsResultDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsMovieDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsMovieResultsDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsTvDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsTvResultsDto;
import com.cjy.contenthub.detail.recommendation.service.DetailRecommendationNoCacheServiceImpl;
import com.cjy.contenthub.wishlist.repository.WishlistRepository;

@ExtendWith(MockitoExtension.class)
class DetailRecommendationNoCacheServiceImplTest {
	
	DetailRecommendationNoCacheServiceImpl service;
	
	@Mock
	WishlistRepository wishlistRepository;

	@Mock
	WishlistSharedService wishlistFlagSharedService;
	
	@BeforeEach
	void setUp() {
        // 서비스 인스턴스 생성
		service = new DetailRecommendationNoCacheServiceImpl(
				wishlistRepository,
				wishlistFlagSharedService
				);
	}
	
	@Test
	@DisplayName("[UT]setWishlistFromTvResponse: TV 상세 추천 응답에서 위시리스트 설정")
	void test_setWishlistFromTvResponse() {
		
		Long userId = 1L;
		
		DetailRecommendationsTvDto tvResponse = new DetailRecommendationsTvDto();
		List<DetailRecommendationsTvResultsDto> resultsList = new ArrayList<>();
		DetailRecommendationsTvResultsDto aniTvResults = new DetailRecommendationsTvResultsDto();
		aniTvResults.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode());
		DetailRecommendationsTvResultsDto dramaTvResults = new DetailRecommendationsTvResultsDto();
		dramaTvResults.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode());
		DetailRecommendationsTvResultsDto documentaryTvResults = new DetailRecommendationsTvResultsDto();
		documentaryTvResults.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode());
		DetailRecommendationsTvResultsDto kidsTvResults = new DetailRecommendationsTvResultsDto();
		kidsTvResults.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode());
		DetailRecommendationsTvResultsDto newsTvResults = new DetailRecommendationsTvResultsDto();
		newsTvResults.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode());
		DetailRecommendationsTvResultsDto varietyTvResults = new DetailRecommendationsTvResultsDto();
		varietyTvResults.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode());
		resultsList.add(aniTvResults);
		resultsList.add(dramaTvResults);
		resultsList.add(documentaryTvResults);
		resultsList.add(kidsTvResults);
		resultsList.add(newsTvResults);
		resultsList.add(varietyTvResults);
		tvResponse.setResults(resultsList);
		
		doAnswer(invocation -> {
			List<DetailRecommendationsTvResultsDto> dtoList = invocation.getArgument(0);
			for (DetailRecommendationsTvResultsDto dto : dtoList) {
				dto.setWishlisted(true);
			}
			return null;
		}).when(wishlistFlagSharedService).setWishlisted(
				anyList(), 
				anyList(), 
				eq(userId), 
				any(), 
				any(),
				eq(wishlistRepository));
		
		// 실제 테스트 메소드 호출
		service.setWishlistFromTvResponse(tvResponse, userId);
		
		// 검증
		for (DetailRecommendationsTvResultsDto resultDto : tvResponse.getResults()) {
			assertThat(resultDto.isWishlisted()).isTrue();
		}
	}
	
	@Test
	@DisplayName("[UT]setWishlistFromMovieResponse: 영화 상세 추천 응답에서 위시리스트 설정")
	void test_setWishlistFromMovieResponse() {
		
		Long userId = 1L;
		
		DetailRecommendationsMovieDto movieResponse = new DetailRecommendationsMovieDto();
		List<DetailRecommendationsMovieResultsDto> resultsList = new ArrayList<>();
		DetailRecommendationsMovieResultsDto movieTvResults = new DetailRecommendationsMovieResultsDto();
		movieTvResults.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode());
		resultsList.add(movieTvResults);
		movieResponse.setResults(resultsList);
		
		doAnswer(invocation -> {
			List<DetailRecommendationsMovieResultsDto> dtoList = invocation.getArgument(0);
			for (DetailRecommendationsMovieResultsDto dto : dtoList) {
				dto.setWishlisted(true);
			}
			return null;
		}).when(wishlistFlagSharedService).setWishlisted(
				anyList(), 
				anyList(), 
				eq(userId), 
				any(), 
				any(),
				eq(wishlistRepository));
		
		// 실제 테스트 메소드 호출
		service.setWishlistFromMovieResponse(movieResponse, userId);
		
		// 검증
		for (DetailRecommendationsMovieResultsDto resultDto : movieResponse.getResults()) {
			assertThat(resultDto.isWishlisted()).isTrue();
		}
	}
	
	@Test
	@DisplayName("[UT]setWishlistFromComicsResponse: 만화 상세 추천 응답에서 위시리스트 설정")
	void test_setWishlistFromComicsResponse() {
		
		Long userId = 1L;
		
		DetailRecommendationsComicsResponseDto comicsResponse = new DetailRecommendationsComicsResponseDto();
		List<DetailRecommendationsComicsResultDto> resultsList = new ArrayList<>();
		DetailRecommendationsComicsResultDto comicsTvResults = new DetailRecommendationsComicsResultDto();
		comicsTvResults.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_COMICS.getContentMediaTypeCode());
		resultsList.add(comicsTvResults);
		comicsResponse.setResults(resultsList);
		
		doAnswer(invocation -> {
			List<DetailRecommendationsComicsResultDto> dtoList = invocation.getArgument(0);
			for (DetailRecommendationsComicsResultDto dto : dtoList) {
				dto.setWishlisted(true);
			}
			return null;
		}).when(wishlistFlagSharedService).setWishlisted(
				anyList(), 
				anyList(), 
				eq(userId), 
				any(), 
				any(),
				eq(wishlistRepository));
		
		// 실제 테스트 메소드 호출
		service.setWishlistFromComicsResponse(comicsResponse, userId);
		
		// 검증
		for (DetailRecommendationsComicsResultDto resultDto : comicsResponse.getResults()) {
			assertThat(resultDto.isWishlisted()).isTrue();
		}
	}
	

}
