package com.cjy.contenthub.detail.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsMovieDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsTvDto;
import com.cjy.contenthub.detail.controller.dto.DetailComicsRecommendationsResponseDto;
import com.cjy.contenthub.detail.service.DetailRecommendationService;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 상세 화면 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/detail/recommendation")
@RequiredArgsConstructor
@Slf4j
public class DetailRecommendationController {

	/** 상세 추천 서비스 */
	private final DetailRecommendationService recommendationService;

	/** TMDB API 통신용 WebClient 클래스 */
	@Qualifier("tmdbWebClient")
	private final WebClient tmdbWebClient;

	/** AniList API 통신용 WebClient 클래스 */
	@Qualifier("anilistWebClient")
	private final WebClient anilistWebClient;

	/** TMDB API TV 추천 작품 API 패스 */
	@Value("${tmdb.url.tvRecommendationsPath}")
	private String tvRecommendationsPath;

	/** TMDB API Movie 추천 작품 API 패스 */
	@Value("${tmdb.url.movieRecommendationsPath}")
	private String movieRecommendationsPath;

	/** TMDB API TV 비슷한 작품 API 패스 */
	@Value("${tmdb.url.tvSimilarPath}")
	private String tvSimilarPath;

	/** TMDB API Movie 비슷한 작품 API 패스 */
	@Value("${tmdb.url.movieSimilarPath}")
	private String movieSimilarPath;

	/** AniList API 전체보기화면 작품 표시 개수 */
	@Value("${anilist.custom.perMorePage}")
	private int anilistPerMorePage;
	
	/** AniList API 상세화면 캐릭터 표시 개수 */
	@Value("${anilist.custom.perCharacterPage}")
	private int anilistPerCharacterPage;

	/** 리퀘스트 파라미터 키 : TV SERIES ID */
	private static final String PARAM_TV_SERIES_ID = "series_id";

	/** 리퀘스트 파라미터 키 : MOVIE ID */
	private static final String PARAM_MOVIE_ID = "movie_id";

	/** 리퀘스트 파라미터 키 : 페이지 번호 */
	private static final String PARAM_PAGE = "page";

	/** 리퀘스트 파라미터 키 : 미디어 ID */
	private static final String PARAM_MEDIA_ID = "mediaId";

	/**
	 * TMDB TV 추천 작품 조회 API
	 * 
	 * @param seriesId TV 시리즈 ID
	 * @param page 페이지 번호 (선택)
	 * @return ResponseEntity<TmdbRecommendationsTvDto> 추천 작품 응답 DTO
	 */
	@GetMapping(value = "/getTvRecommendations")
	public ResponseEntity<TmdbRecommendationsTvDto> getTvRecommendations(
			@NotNull @RequestParam(PARAM_TV_SERIES_ID) Integer seriesId,
			@Nullable @RequestParam(PARAM_PAGE) Integer page
			) {
		return ResponseEntity.ok(recommendationService.getTvRecommendations(seriesId, page));
	}

	/**
	 * TMDB 영화 추천 작품 조회 API
	 * 
	 * @param movieId 영화 ID
	 * @param page 페이지 번호 (선택)
	 * @return ResponseEntity<TmdbRecommendationsMovieDto> 추천 작품 응답 DTO
	 */
	@GetMapping(value = "/getMovieRecommendations")
	public ResponseEntity<TmdbRecommendationsMovieDto> getMovieRecommendations(
			@NotNull @RequestParam(PARAM_MOVIE_ID) Integer movieId,
			@Nullable @RequestParam(PARAM_PAGE) Integer page
			) {
		return ResponseEntity.ok(recommendationService.getMovieRecommendations(movieId, page));
	}

	/**
	 * AniList Comics 추천 작품 조회 API
	 * 
	 * @param mediaId 미디어 추천 ID
	 * @param page 페이지 번호 (선택)
	 * @return ResponseEntity<DetailComicsRecommendationsResponseDto> 추천 작품 응답 DTO
	 */
	@GetMapping(value = "/getComicsRecommendations")
	public ResponseEntity<DetailComicsRecommendationsResponseDto> getComicsRecommendations(
			@NotNull @RequestParam(PARAM_MEDIA_ID) Integer mediaId,
			@Nullable @RequestParam(PARAM_PAGE) Integer page
			) throws IOException {
		return ResponseEntity.ok(recommendationService.getComicsRecommendations(mediaId, page));
	}

}
