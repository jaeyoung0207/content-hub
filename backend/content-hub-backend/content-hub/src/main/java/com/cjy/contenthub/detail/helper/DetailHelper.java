package com.cjy.contenthub.detail.helper;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 상세 화면 헬퍼 클래스
 */
@Component
public class DetailHelper {
	
	/** 리퀘스트 파라미터 키 : TV SERIES ID */
	private static final String PARAM_TV_SERIES_ID = "series_id";
	
	/** 리퀘스트 파라미터 키 : MOVIE ID */
	private static final String PARAM_MOVIE_ID = "movie_id";
	
	/** 리퀘스트 파라미터 키 : 페이지 번호 */
	private static final String PARAM_PAGE = "page";
	
	/** 리퀘스트 파라미터 키 : 언어 */
	private static final String PARAM_LANGUAGE = "language";
	
	/** TMDB API TV 추천 작품 API 패스 */
	@Value("${tmdb.url.tvRecommendationsPath}")
	private String tvRecommendationsPath;
	
	/** TMDB API Movie 추천 작품 API 패스 */
	@Value("${tmdb.url.movieRecommendationsPath}")
	private String movieRecommendationsPath;
	
	/**
	 * TMDB 영화 추천 작품 조회를 위한 URI 생성
	 * 
	 * @param movieId  영화 ID
	 * @param page 페이지 번호
	 * @param language 언어 코드
	 * @return String 생성된 URI
	 */
	public String getMovieRecommendationUri(Integer movieId, Integer page, String language) {
		return UriComponentsBuilder.fromPath(String.format(movieRecommendationsPath, movieId))
				.queryParam(PARAM_MOVIE_ID, movieId)
				.queryParam(PARAM_LANGUAGE, language)
				.queryParam(PARAM_PAGE, Optional.ofNullable(page).orElse(1))
				.toUriString();
	}
	
	/**
	 * TMDB TV 추천 작품 조회를 위한 URI 생성
	 * 
	 * @param seriesId TV 시리즈 ID
	 * @param page 페이지 번호
	 * @param language 언어 코드
	 * @return String 생성된 URI
	 */
	public String getTVRecommendationUri(Integer seriesId, Integer page, String language) {
		return UriComponentsBuilder.fromPath(String.format(tvRecommendationsPath, seriesId))
				.queryParam(PARAM_TV_SERIES_ID, seriesId)
				.queryParam(PARAM_LANGUAGE, language)
				.queryParam(PARAM_PAGE, Optional.ofNullable(page).orElse(1))
				.toUriString();
	}

}
