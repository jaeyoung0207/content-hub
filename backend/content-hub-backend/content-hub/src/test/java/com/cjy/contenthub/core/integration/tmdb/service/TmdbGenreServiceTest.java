package com.cjy.contenthub.core.integration.tmdb.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cjy.contenthub.common.integration.tmdb.client.TmdbApiGenreClient;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbGenreDto;
import com.cjy.contenthub.common.util.MessageUtil;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class TmdbGenreServiceTest {
	
	TmdbGenreService tmdbGenreService;
	
	@Mock
	TmdbApiGenreClient tmdbApiGenreClient;

	@Mock
	MessageUtil messageUtil;
	
	@BeforeEach
	void setUp() {
		tmdbGenreService = new TmdbGenreService(
				tmdbApiGenreClient, 
				messageUtil);
	}
	
	@Test
	@DisplayName("[UT]getTvGenres: TMDB API를 사용하여 TV 장르 정보 조회")
	void test_getTvGenres() {
		
		List<TmdbGenreDto> genreList = new ArrayList<>();
		TmdbGenreDto genre1 = new TmdbGenreDto();
		genre1.setId(16);
		genre1.setName("Animation");
		TmdbGenreDto genre2 = new TmdbGenreDto();
		genre2.setId(0);
		genre2.setName("newGenreName");
		TmdbGenreDto genre3 = new TmdbGenreDto();
		genre3.setId(16);
		genre3.setName("Animation");
		genreList.add(genre1);
		genreList.add(genre2);
		genreList.add(genre3);
		
		when(tmdbApiGenreClient.getTmdbTvGenres()).thenReturn(Mono.just(genreList));
		
		// 실제 메서드 호출
		Map<String, Integer> result = tmdbGenreService.getTvGenres();
		
		// 검증
		Map<String, Integer> expectedMap = new HashMap<>();
		expectedMap.put(genre1.getName(), genre1.getId());
		expectedMap.put(genre2.getName(), genre2.getId());
		expectedMap.put(genre3.getName(), genre3.getId());
		assertThat(result).isEqualTo(expectedMap);
		
		verify(tmdbApiGenreClient, times(1)).getTmdbTvGenres();
	}
	
	@Test
	@DisplayName("[UT]getMovieGenres: TMDB API를 사용하여 영화 장르 정보 조회")
	void test_getMovieGenres() {
		
		List<TmdbGenreDto> genreList = new ArrayList<>();
		TmdbGenreDto genre1 = new TmdbGenreDto();
		genre1.setId(28);
		genre1.setName("Action");
		genreList.add(genre1);
		
		when(tmdbApiGenreClient.getTmdbMovieGenres()).thenReturn(Mono.just(genreList));
		
		// 실제 메서드 호출
		Map<String, Integer> result = tmdbGenreService.getMovieGenres();
		
		// 검증
		Map<String, Integer> expectedMap = new HashMap<>();
		expectedMap.put(genre1.getName(), genre1.getId());
		assertThat(result).isEqualTo(expectedMap);
		
		verify(tmdbApiGenreClient, times(1)).getTmdbMovieGenres();
	}

}
