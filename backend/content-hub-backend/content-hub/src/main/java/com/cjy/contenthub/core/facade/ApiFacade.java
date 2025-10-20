package com.cjy.contenthub.core.facade;

import java.util.Map;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.cjy.contenthub.common.integration.deepl.client.DeepLApiClient;
import com.cjy.contenthub.core.integration.tmdb.service.TmdbGenreService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 외부 API facade 클래스
 */
@Component
@RequiredArgsConstructor
public class ApiFacade {

	/** DeepL API 번역 WebClient 클래스 */
	private final DeepLApiClient deeplApiClient;

	/** TMDB 장르 서비스 클래스 */
	private final TmdbGenreService tmdbGenreService;

	/**
	 * 어플리케이션 기동시 ApplicationReadyEvent를 이용하여, 
	 * 모든 빈 초기화 + 어플리케이션 준비 완료 후에 캐시화 로직을 실행
	 * (@Cacheable 가 AOP 프록시로 동작하므로, 이 시점에서는 사용 가능)
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void initializeTMdbApiGenreInfo() {
		// TMDB API 애니/영화 장르 정보 캐시화 
		tmdbGenreService.getTvGenres();
		tmdbGenreService.getMovieGenres();
	}

	/**
	 * TMDB API를 사용하여 TV 장르 정보 조회
	 * 
	 * @return Mono 형태의 TV 장르 정보 Map
	 */
	public Mono<Map<String, Integer>> getTvGenres() {
		return Mono.fromCallable(tmdbGenreService::getTvGenres)
				.subscribeOn(Schedulers.boundedElastic());
	}

	/**
	 * TMDB API를 사용하여 영화 장르 정보 조회
	 * 
	 * @return Mono 형태의 영화 장르 정보 Map
	 */
	public Mono<Map<String, Integer>> getMovieGenres() {
		return Mono.fromCallable(tmdbGenreService::getMovieGenres)
				.subscribeOn(Schedulers.boundedElastic());
	}

	/**
	 * DeepL API를 사용하여 대상 문자열을 설정언어로 변역
	 * 
	 * @param keyword 번역할 문자열
	 * @param targetLang 번역할 언어 (예: "KO", "JA")
	 * @param sourceLang 원본 언어 (예: "KO", "JA")
	 * @return 번역된 문자열
	 */
	public Mono<String> getTranslationText(String keyword, String targetLang, String sourceLang) {
		return Mono.fromCallable(() -> 
		deeplApiClient.translateText(keyword, targetLang, sourceLang))
				.subscribeOn(Schedulers.boundedElastic())
				.onErrorResume(ex -> Mono.just(""));
	}

}
