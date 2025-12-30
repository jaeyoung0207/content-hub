package com.cjy.contenthub.core.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cjy.contenthub.core.integration.deepl.service.DeepLService;
import com.cjy.contenthub.core.integration.tmdb.service.TmdbGenreService;

@ExtendWith(MockitoExtension.class)
class ApiFacadeTest {
	
	@InjectMocks
	ApiFacade apiFacade;
	
	@Mock
	DeepLService deeplService;

	@Mock
	TmdbGenreService tmdbGenreService;
	
	@Test
	@DisplayName("[UT]initializeTMdbApiGenreInfo: TMDB API 애니/영화 장르 정보 캐시화")
	void test_initializeTMdbApiGenreInfo() {
		
		Map<String, Integer> tvGenreMap = Map.of("Drama", 18, "Comedy", 35);
		Map<String, Integer> movieGenreMap = Map.of("Action", 28, "Comedy", 35);

		when(tmdbGenreService.getTvGenres()).thenReturn(tvGenreMap);
		when(tmdbGenreService.getMovieGenres()).thenReturn(movieGenreMap);

		// 실제 메서드 호출
		apiFacade.initializeTMdbApiGenreInfo();

		// 검증
		verify(tmdbGenreService, times(1)).getTvGenres();
		verify(tmdbGenreService, times(1)).getMovieGenres();
		
	}
	
	@Test
	@DisplayName("[UT]getTvGenres: TMDB API를 사용하여 TV 장르 정보 조회")
	void test_getTvGenres() {
		
		Map<String, Integer> genreMap = Map.of("Drama", 18, "Comedy", 35);
		when(tmdbGenreService.getTvGenres()).thenReturn(genreMap);
		
		// 실제 메서드 호출
		Map<String, Integer> result = apiFacade.getTvGenres().block();
		
		// 결과 검증
		assertThat(result).isEqualTo(genreMap);
		
		verify(tmdbGenreService, times(1)).getTvGenres();
	}
	
	@Test
	@DisplayName("[UT]getMovieGenres: TMDB API를 사용하여 영화 장르 정보 조회")
	void test_getMovieGenres() {
		
		Map<String, Integer> genreMap = Map.of("Action", 28, "Comedy", 35);
		when(tmdbGenreService.getMovieGenres()).thenReturn(genreMap);
		
		// 실제 메서드 호출
		Map<String, Integer> result = apiFacade.getMovieGenres().block();
		
		// 결과 검증
		assertThat(result).isEqualTo(genreMap);
		
		verify(tmdbGenreService, times(1)).getMovieGenres();
	}
	
	@Test
	@DisplayName("[UT]getTranslationText: DeepL API를 사용하여 대상 문자열을 설정언어로 변역")
	void test_getTranslationText() {
		
		String text = "드래곤볼";
		String targetLang = "KO";
		String sourceLang = "JP";
		
		String translatedText = "ドラゴンボール";
		when(deeplService.translateText(text, targetLang, sourceLang))
		.thenReturn(translatedText);
		
		// 실제 메서드 호출
		String result = apiFacade.getTranslationText(text, targetLang, sourceLang).block();
		
		// 결과 검증
		assertThat(result).isEqualTo(translatedText);
		
		verify(deeplService, times(1)).translateText(text, targetLang, sourceLang);
	}
	
	
	

}
