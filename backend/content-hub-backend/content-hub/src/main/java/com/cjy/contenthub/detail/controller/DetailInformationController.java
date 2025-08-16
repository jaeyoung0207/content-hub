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

import com.cjy.contenthub.detail.controller.dto.DetailComicsResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailMovieResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailTvResponseDto;
import com.cjy.contenthub.detail.service.DetailInformationService;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 상세 화면 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/detail/information")
@RequiredArgsConstructor
@Slf4j
public class DetailInformationController {

	/** 상세 정보 서비스 */
	private final DetailInformationService informationService;

	/** TMDB API 통신용 WebClient 클래스 */
	@Qualifier("tmdbWebClient")
	private final WebClient tmdbWebClient;

	/** AniList API 통신용 WebClient 클래스 */
	@Qualifier("anilistWebClient")
	private final WebClient anilistWebClient;

	/** DeepL API 통신용 WebClient 클래스 */
	@Qualifier("deeplWebClient")
	private final WebClient deeplWebClient;

	/** TMDB API TV Detail API 패스 */
	@Value("${tmdb.url.tvDetailPath}")
	private String tvDetailPath;

	/** TMDB API Movie Detail API 패스 */
	@Value("${tmdb.url.movieDetailPath}")
	private String movieDetailPath;

	/** TMDB API TV Watch Providers API 패스 */
	@Value("${tmdb.url.tvWatchProvidersPath}")
	private String tvWatchProvidersPath;

	/** TMDB API Movie Watch Providers API 패스 */
	@Value("${tmdb.url.movieWatchProvidersPath}")
	private String movieWatchProvidersPath;

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

	/** 리퀘스트 파라미터 키 : COMICS ID */
	private static final String PARAM_COMICS_ID = "comics_id";

	/** 리퀘스트 파라미터 키 : 페이지 번호 */
	private static final String PARAM_PAGE = "page";

	/**
	 * TMDB TV 상세 조회 API
	 * 
	 * @param seriesId TV 시리즈 ID
	 * @param originalMediaType 원본 미디어 타입
	 * @return TV 상세 응답 DTO
	 */
	@GetMapping(value = "/getTvDetail")
	public ResponseEntity<DetailTvResponseDto> getTvDetail(
			@NotNull @RequestParam(PARAM_TV_SERIES_ID) Integer seriesId
			) {
		return ResponseEntity.ok(informationService.getTvDetail(seriesId));
	}

	/**
	 * TMDB 영화 상세 조회 API
	 * 
	 * @param movieId 영화 ID
	 * @param originalMediaType 원본 미디어 타입
	 * @return ResponseEntity<DetailMovieResponseDto> 영화 상세 응답 DTO
	 */
	@GetMapping(value = "/getMovieDetail")
	public ResponseEntity<DetailMovieResponseDto> getMovieDetail(
			@NotNull @RequestParam(PARAM_MOVIE_ID) Integer movieId
			) {
		return ResponseEntity.ok(informationService.getMovieDetail(movieId));
	}

	/**
	 * AniList Comics 상세 조회 API
	 * 
	 * @param comicsId Comics ID
	 * @param page 페이지 번호
	 * @return Comics 상세 응답 DTO
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@GetMapping(value = "/getComicsDetail")
	public ResponseEntity<DetailComicsResponseDto> getComicsDetail(
			@NotNull @RequestParam(PARAM_COMICS_ID) Integer comicsId,
			@Nullable @RequestParam(PARAM_PAGE) Integer page
			) throws IOException {
		return ResponseEntity.ok(informationService.getComicsDetail(comicsId, page));
	}
}
