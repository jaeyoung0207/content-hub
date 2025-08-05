package com.cjy.contenthub.common.client;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbGenreDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbGenreListDto;
import com.cjy.contenthub.common.constants.CommonConstants;

import lombok.RequiredArgsConstructor;

/**
 * TMDB API 장르 클라이언트 클래스
 * TMDB API를 사용하여 TV 및 영화 장르 정보를 취득하는 기능을 제공하는 클래스
 */
@Component
@RequiredArgsConstructor
public class TmdbApiGenreClient {

	/** API 통신용 WebClient 공통 클래스 */
	@Qualifier("tmdbWebClient")
	private final WebClient tmdbWebClient;
	
	/** TMDB TV 장르 정보 취득 경로 */
	@Value("${tmdb.url.tvGenrePath}")
	private String tvGenrePath;

	/** TMDB 영화 장르 정보 취득 경로 */
	@Value("${tmdb.url.movieGenrePath}")
	private String movieGenrePath;

	/** 리퀘스트 파라미터 키 : 언어 */
	private static final String PARAM_LANGUAGE = "language";

	/**
	 * TMDB API를 호출하여 TV 장르 정보를 취득
	 * 캐시를 사용하여 동일한 요청에 대해 반복적인 API 호출을 방지
	 * @Cacheable 어노테이션을 사용하여 캐시를 적용
	 * @Cacheable를 사용하기 위해서는 동기화 된 메소드여야 하므로, .block() 메소드를 사용하여 결과를 반환
	 * 
	 * @return TV 장르 정보
	 */
	@Cacheable(CommonConstants.API_TV_GENRE_NAME)
	public Map<String, Integer> getTvGenres() {
		
		// TMDB API를 호출하여 TV 장르 정보를 취득
		return tmdbWebClient.get()
				.uri(builder -> builder
						.path(tvGenrePath)
						.queryParam(PARAM_LANGUAGE, "en")
						.build())
				.retrieve()
				.bodyToMono(TmdbGenreListDto.class)
				.map(response -> {
					Map<String, Integer> genreMap = response.getGenres().stream().collect(
							Collectors.toMap(
									TmdbGenreDto::getName, // 키
									TmdbGenreDto::getId, // 값
									(oldId, newId) -> newId, // 키 중복일 경우, 새로운 키로 덮어씌움
									HashMap::new // 반환형 지정
									)
							);
					return genreMap;
				}).block();
	}

	/**
	 * TMDB API를 호출하여 영화 장르 정보를 취득
	 * 캐시를 사용하여 동일한 요청에 대해 반복적인 API 호출을 방지
	 * @Cacheable 어노테이션을 사용하여 캐시를 적용
	 * @Cacheable를 사용하기 위해서는 동기화 된 메소드여야 하므로, .block() 메소드를 사용하여 결과를 반환
	 * 
	 * @return 영화 장르 정보
	 */
	@Cacheable(CommonConstants.API_MOVIE_GENRE_NAME)
	public Map<String, Integer> getMovieGenres() {
		
		// TMDB API를 호출하여 영화 장르 정보를 취득
		return tmdbWebClient.get()
				.uri(builder -> builder
						.path(movieGenrePath)
						.queryParam(PARAM_LANGUAGE, "en")
						.build())
				.retrieve()
				.bodyToMono(TmdbGenreListDto.class)
				.map(response -> {
					Map<String, Integer> genreMap = response.getGenres().stream().collect(
							Collectors.toMap(
									TmdbGenreDto::getName, // 키
									TmdbGenreDto::getId, // 값
									(oldId, newId) -> newId, // 키 중복일 경우, 새로운 키로 덮어씌움
									HashMap::new // 반환형 지정
									)
							);
					return genreMap;
				}).block();
	}

}
