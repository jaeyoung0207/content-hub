package com.cjy.contenthub.detail.recommendation.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cjy.contenthub.common.annotation.ApiController;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsComicsResponseDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsComicsResultDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsMovieDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsMovieResultsDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsTvDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsTvResultsDto;
import com.cjy.contenthub.detail.recommendation.mapper.DetailRecommendationMapper;
import com.cjy.contenthub.detail.recommendation.service.DetailRecommendationNoCacheService;
import com.cjy.contenthub.detail.recommendation.service.DetailRecommendationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 상세 화면 추천 작품 API 컨트롤러 클래스
 */
@Tag(name = "detail-recommendation-api", description = "Detail Recommendation APIs")
@ApiController
@RequestMapping("/detail/recommendation")
@RequiredArgsConstructor
public class DetailRecommendationController {

	/** 상세 추천 서비스 */
	private final DetailRecommendationService recommendationService;
	
	/** 상세 추천 서비스(캐시 미사용) */
	private final DetailRecommendationNoCacheService detailRecommendationNoCacheService;
	
	/** 상세 추천 매퍼 */
	private final DetailRecommendationMapper detailRecommendationMapper;

	/** 리퀘스트 파라미터 키 : TV SERIES ID */
	private static final String PARAM_TV_SERIES_ID = "series_id";

	/** 리퀘스트 파라미터 키 : MOVIE ID */
	private static final String PARAM_MOVIE_ID = "movie_id";

	/** 리퀘스트 파라미터 키 : 페이지 번호 */
	private static final String PARAM_PAGE = "page";

	/** 리퀘스트 파라미터 키 : 미디어 ID */
	private static final String PARAM_MEDIA_ID = "media_id";
	
	/** 리퀘스트 파라미터 키 : 유저 ID */
	private static final String PARAM_USER_ID = "user_id";

	/**
	 * TMDB TV 추천 작품 조회 API
	 * 
	 * @param seriesId TV 시리즈 ID
	 * @param page 페이지 번호
	 * @param userId 유저 테이블 ID
	 * @return 추천 작품 응답 DTO
	 */
	@Operation(summary = "TV 추천 작품 조회")
	@GetMapping(value = "/getTvRecommendations")
	public ResponseEntity<DetailRecommendationsTvDto> getTvRecommendations(
			@RequestParam(PARAM_TV_SERIES_ID) Integer seriesId,
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_USER_ID, required = false) Long userId
			) {
		
		// 캐시된 응답 객체를 깊은 복사하여 새로운 객체 생성(캐시된 객체를 직접 수정하지 않고 새로운 객체를 사용)
		DetailRecommendationsTvDto cachedResponse = recommendationService.getTvRecommendations(seriesId, page, userId);
		
		// 추천 결과 리스트 깊은 복사
		List<DetailRecommendationsTvResultsDto> newResponseList =
				detailRecommendationMapper.deepCopyForRecommendationsTvResultsList(cachedResponse.getResults());
		
		// 새로운 응답 객체 생성
		DetailRecommendationsTvDto newResponse = DetailRecommendationsTvDto.builder()
				.page(cachedResponse.getPage())
				.results(newResponseList)
				.totalPages(cachedResponse.getTotalPages())
				.totalResults(cachedResponse.getTotalResults())
				.build();
		
		// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
		if (userId != null) {
			detailRecommendationNoCacheService.setWishlistFromTvResponse(newResponse, userId);
		}
		
		// 응답 반환
		return ResponseEntity.ok(newResponse);
	}

	/**
	 * TMDB 영화 추천 작품 조회 API
	 * 
	 * @param movieId 영화 ID
	 * @param page 페이지 번호
	 * @param userId 유저 테이블 ID
	 * @return 추천 작품 응답 DTO
	 */
	@Operation(summary = "영화 추천 작품 조회")
	@GetMapping(value = "/getMovieRecommendations")
	public ResponseEntity<DetailRecommendationsMovieDto> getMovieRecommendations(
			@RequestParam(PARAM_MOVIE_ID) Integer movieId,
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_USER_ID, required = false) Long userId
			) {
		
		// 캐시된 응답 객체를 깊은 복사하여 새로운 객체 생성(캐시된 객체를 직접 수정하지 않고 새로운 객체를 사용)
		DetailRecommendationsMovieDto cachedResponse = recommendationService.getMovieRecommendations(movieId, page, userId);
		
		// 추천 결과 리스트 깊은 복사
		List<DetailRecommendationsMovieResultsDto> newResponseList =
				detailRecommendationMapper.deepCopyForRecommendationsMovieResultsList(cachedResponse.getResults());
		
		// 새로운 응답 객체 생성
		DetailRecommendationsMovieDto newResponse = DetailRecommendationsMovieDto.builder()
				.page(cachedResponse.getPage())
				.results(newResponseList)
				.totalPages(cachedResponse.getTotalPages())
				.totalResults(cachedResponse.getTotalResults())
				.build();
		
		// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
		if (userId != null) {
			detailRecommendationNoCacheService.setWishlistFromMovieResponse(newResponse, userId);
		}
		
		// 응답 반환
		return ResponseEntity.ok(newResponse);
	}

	/**
	 * AniList Comics 추천 작품 조회 API
	 * 
	 * @param mediaId 미디어 추천 ID
	 * @param page 페이지 번호
	 * @param userId 유저 테이블 ID
	 * @return 추천 작품 응답 DTO
	 */
	@Operation(summary = "만화 추천 작품 조회")
	@GetMapping(value = "/getComicsRecommendations")
	public ResponseEntity<DetailRecommendationsComicsResponseDto> getComicsRecommendations(
			@RequestParam(PARAM_MEDIA_ID) Integer mediaId,
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_USER_ID, required = false) Long userId
			) throws IOException {
		
		// 캐시된 응답 객체를 깊은 복사하여 새로운 객체 생성(캐시된 객체를 직접 수정하지 않고 새로운 객체를 사용)
		DetailRecommendationsComicsResponseDto cachedResponse = recommendationService.getComicsRecommendations(mediaId, page, userId);
		
		// 추천 결과 리스트 깊은 복사
		List<DetailRecommendationsComicsResultDto> newResponseList =
				detailRecommendationMapper.deepCopyForRecommendationsComicsResultsList(cachedResponse.getResults());
		
		// 새로운 응답 객체 생성
		DetailRecommendationsComicsResponseDto newResponse = DetailRecommendationsComicsResponseDto.builder()
				.results(newResponseList)
				.build();
		
		// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
		if (userId != null) {
			detailRecommendationNoCacheService.setWishlistFromComicsResponse(newResponse, userId);
		}
		
		// 응답 반환
		return ResponseEntity.ok(newResponse);
	}

}
