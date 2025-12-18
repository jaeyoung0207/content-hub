package com.cjy.contenthub.core.facade;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.cjy.contenthub.core.integration.deepl.service.DeepLService;
import com.cjy.contenthub.core.integration.tmdb.service.TmdbGenreService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 외부 API facade 클래스
 */
@Component
@RequiredArgsConstructor
public class ApiFacade {

	/** DeepL 서비스 클래스 */
	private final DeepLService deeplService;

	/** TMDB 장르 서비스 클래스 */
	private final TmdbGenreService tmdbGenreService;

	/**
	 * 어플리케이션 기동시 ApplicationReadyEvent를 이용하여, 
	 * 모든 빈 초기화 + 어플리케이션 준비 완료 후에 캐시화 로직을 실행
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void initializeTMdbApiGenreInfo() {
		// TMDB API 애니/영화 장르 정보 캐시화 
		tmdbGenreService.getTvGenres().join();
		tmdbGenreService.getMovieGenres().join();
	}

	/**
	 * TMDB API를 사용하여 TV 장르 정보 조회
	 * 
	 * @return Mono 형태의 TV 장르 정보 Map
	 */
	public Mono<Map<String, Integer>> getTvGenres() {
		return Mono.fromFuture(tmdbGenreService.getTvGenres());
	}

	/**
	 * TMDB API를 사용하여 영화 장르 정보 조회
	 * 
	 * @return Mono 형태의 영화 장르 정보 Map
	 */
	public Mono<Map<String, Integer>> getMovieGenres() {
		return Mono.fromFuture(tmdbGenreService::getMovieGenres);
	}

	/**
	 * DeepL API를 사용하여 대상 문자열을 설정언어로 변역
	 * 
	 * @param keyword 번역할 문자열
	 * @param targetLang 번역할 언어 (예: "KO", "JA")
	 * @param sourceLang 원본 언어 (예: "KO", "JA")
	 * @return 번역된 문자열
	 */
	public CompletableFuture<String> getTranslationText(String keyword, String targetLang, String sourceLang) {
		return deeplService.translateText(keyword, targetLang, sourceLang);
	}

}
