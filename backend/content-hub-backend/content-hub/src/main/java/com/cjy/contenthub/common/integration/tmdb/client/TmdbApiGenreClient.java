package com.cjy.contenthub.common.integration.tmdb.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.common.integration.tmdb.dto.TmdbGenreDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbGenreListDto;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

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
	 * 
	 * @return TV 장르 정보
	 */
	public Mono<List<TmdbGenreDto>> getTmdbTvGenres() {
		return tmdbWebClient.get()
				.uri(builder -> builder
						.path(tvGenrePath)
						.queryParam(PARAM_LANGUAGE, "en")
						.build())
				.retrieve()
				.bodyToMono(TmdbGenreListDto.class)
				.map(TmdbGenreListDto::getGenres);
	}

	/**
	 * TMDB API를 호출하여 영화 장르 정보를 취득
	 * 
	 * @return 영화 장르 정보
	 */
	public Mono<List<TmdbGenreDto>> getTmdbMovieGenres() {
		return tmdbWebClient.get()
				.uri(builder -> builder
						.path(movieGenrePath)
						.queryParam(PARAM_LANGUAGE, "en")
						.build())
				.retrieve()
				.bodyToMono(TmdbGenreListDto.class)
				.map(TmdbGenreListDto::getGenres);
	}

}
