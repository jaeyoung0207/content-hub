package com.cjy.contenthub.core.integration.tmdb.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMessagesWarnEnum;
import com.cjy.contenthub.common.integration.tmdb.client.TmdbApiGenreClient;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbGenreDto;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.core.constants.DomainEnum.TmdbGenreEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TMDB API 장르 서비스 클래스
 * TMDB API를 통해 영화 및 TV 프로그램의 장르 정보를 조회하는 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TmdbGenreService {

	/** TMDB API 장르 클라이언트 */
	private final TmdbApiGenreClient tmdbApiGenreClient;

	/** 메시지 유틸 */
	private final MessageUtil messageUtil;

	/**
	 * TMDB API를 사용하여 TV 장르 정보 조회
	 * @Cacheable 캐시를 적용하여 동일한 요청에 대해 반복적인 API 호출을 방지
	 * @Cacheable를 사용하기 위해서는 동기화 된 메소드여야 하므로, .block() 메소드를 사용하여 결과를 반환
	 * 
	 * @return TV 장르 정보 Map
	 */
	@Cacheable(cacheNames = CommonConstants.API_TV_GENRE_NAME, unless = "#result == null || #result.isEmpty()")
	public Map<String, Integer> getTvGenres() {
		List<TmdbGenreDto> genreList = tmdbApiGenreClient.getTmdbTvGenres().block();
		return getGenreMap(genreList);
	}

	/**
	 * TMDB API를 사용하여 영화 장르 정보 조회
	 * @Cacheable 캐시를 적용하여 동일한 요청에 대해 반복적인 API 호출을 방지
	 * @Cacheable를 사용하기 위해서는 동기화 된 메소드여야 하므로, .block() 메소드를 사용하여 결과를 반환
	 * 
	 * @return 영화 장르 정보 Map
	 */
	@Cacheable(cacheNames = CommonConstants.API_MOVIE_GENRE_NAME, unless = "#result == null || #result.isEmpty()")
	public Map<String, Integer> getMovieGenres() {
		List<TmdbGenreDto> genreList = tmdbApiGenreClient.getTmdbMovieGenres().block();
		return getGenreMap(genreList);
	}

	/**
	 * 장르 리스트를 받아서 장르명-장르ID 맵으로 변환
	 * 
	 * @param genreList TMDB 장르 리스트
	 * @return 장르명-장르ID 맵
	 */
	public Map<String, Integer> getGenreMap(List<TmdbGenreDto> genreList) {
		return genreList.stream().collect(
				Collectors.toMap(
						genre -> {
							String genreName = TmdbGenreEnum.GENRE_ID_EN_MAP.get(genre.getId());
							// 장르명이 매핑되지 않은 경우 TMDB에서 제공하는 원래 장르명 사용
							if (genreName == null) {
								Object[] args = { genre.getName(), genre.getId() };
								log.warn(messageUtil.getMessageKO(CommonMessagesWarnEnum.WARN_COMMON_TMDB_GENRE_NAME_NOTFOUND.getMessageCode(), args));
								return genre.getName();
							}
							// 매핑된 장르명을 키로 사용
							return genreName;
						},
						TmdbGenreDto::getId, // 값
						(oldId, newId) -> newId, // 키 중복일 경우, 새로운 키로 덮어씌움
						HashMap::new // 반환형 지정
						)
				);
	}

}
