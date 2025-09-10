package com.cjy.contenthub.detail.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cjy.contenthub.detail.controller.dto.DetailComicsRecommendationsResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailRecommendationsMovieDto;
import com.cjy.contenthub.detail.controller.dto.DetailRecommendationsTvDto;
import com.cjy.contenthub.detail.service.DetailRecommendationService;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 상세 화면 추천 작품 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/detail/recommendation")
@RequiredArgsConstructor
@Slf4j
public class DetailRecommendationController {

	/** 상세 추천 서비스 */
	private final DetailRecommendationService recommendationService;

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
	@GetMapping(value = "/getTvRecommendations")
	public ResponseEntity<DetailRecommendationsTvDto> getTvRecommendations(
			@RequestParam(PARAM_TV_SERIES_ID) Integer seriesId,
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_USER_ID, required = false) Long userId
			) {
		return ResponseEntity.ok(recommendationService.getTvRecommendations(seriesId, page, userId));
	}

	/**
	 * TMDB 영화 추천 작품 조회 API
	 * 
	 * @param movieId 영화 ID
	 * @param page 페이지 번호
	 * @param userId 유저 테이블 ID
	 * @return 추천 작품 응답 DTO
	 */
	@GetMapping(value = "/getMovieRecommendations")
	public ResponseEntity<DetailRecommendationsMovieDto> getMovieRecommendations(
			@RequestParam(PARAM_MOVIE_ID) Integer movieId,
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_USER_ID, required = false) Long userId
			) {
		return ResponseEntity.ok(recommendationService.getMovieRecommendations(movieId, page, userId));
	}

	/**
	 * AniList Comics 추천 작품 조회 API
	 * 
	 * @param mediaId 미디어 추천 ID
	 * @param page 페이지 번호
	 * @param userId 유저 테이블 ID
	 * @return 추천 작품 응답 DTO
	 */
	@GetMapping(value = "/getComicsRecommendations")
	public ResponseEntity<DetailComicsRecommendationsResponseDto> getComicsRecommendations(
			@RequestParam(PARAM_MEDIA_ID) Integer mediaId,
			@RequestParam(value = PARAM_PAGE, required = false) Integer page,
			@RequestParam(value = PARAM_USER_ID, required = false) Long userId
			) throws IOException {
		return ResponseEntity.ok(recommendationService.getComicsRecommendations(mediaId, page, userId));
	}

}
