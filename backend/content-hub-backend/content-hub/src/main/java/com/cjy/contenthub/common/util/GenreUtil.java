package com.cjy.contenthub.common.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.cjy.contenthub.common.constants.CommonEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.common.constants.CommonEnum.TmdbGenreEnum;

import lombok.NoArgsConstructor;

/**
 * 장르 유틸리티 클래스
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class GenreUtil {
	
	/**
	 * TMDB API 드라마 장르 여부 체크
	 * 
	 * @param tvGenreMap TV 장르 맵
	 * @param genreIds 장르 ID 리스트
	 * @return 드라마 장르 여부
	 */
	public static boolean isDramaGenre(Map<String, Integer> tvGenreMap, List<Integer> genreIds) {
		
		// TV 장르 맵 또는 장르 ID 리스트가 비어있으면 false 반환
		if (tvGenreMap == null || CollectionUtils.isEmpty(genreIds)) {
			return false;
		}
		
		// 드라마 장르 리스트
		List<Integer> dramaCodeList = List.of(
				tvGenreMap.get(TmdbGenreEnum.GENRE_DRAMA.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_COMEDY.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_WESTERN.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_CRIME.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_MYSTERY.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_FAMILY.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_ACTION_ADVENTURE.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_SCI_FI_FANTASY.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_SOAP.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_WAR_POLITICS.getGenreEnglish()));
		// 기타 장르 리스트
		List<Integer> othersCodeList = List.of(
				tvGenreMap.get(TmdbGenreEnum.GENRE_ANI.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_DOCUMENTARY.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_KIDS.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_NEWS.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_REALITY.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_TALK.getGenreEnglish()));
		
		// 기타 장르에 포함되지 않고 드라마 장르에 포함되는 경우 true 반환
		return !CollectionUtils.containsAny(genreIds, othersCodeList)
				&& CollectionUtils.containsAny(genreIds, dramaCodeList);
	}
	
	/**
	 * TMDB API 버라이어티 장르 여부 체크
	 * 
	 * @param tvGenreMap
	 * @param genreIds
	 * @return
	 */
	public static boolean isVarietyGenre(Map<String, Integer> tvGenreMap, List<Integer> genreIds) {
		
		// TV 장르 맵 또는 장르 ID 리스트가 비어있으면 false 반환
		if (tvGenreMap == null || CollectionUtils.isEmpty(genreIds)) {
			return false;
		}
		
		// 버라이어티 장르 리스트
		List<Integer> varietyCodeList = List.of(
				tvGenreMap.get(TmdbGenreEnum.GENRE_REALITY.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_TALK.getGenreEnglish()));
		
		// 버라이어티 장르에 포함되는 경우 true 반환
		return CollectionUtils.containsAny(genreIds, varietyCodeList);
	}
	
	/**
	 * TMDB API 타겟 장르 여부 체크
	 * 
	 * @param genreMap        장르 맵
	 * @param genreIds        장르 ID 리스트
	 * @param targetGenreName 타겟 장르명
	 * @return 타겟 장르 여부
	 */
	public static boolean isTargetGenre(Map<String, Integer> genreMap, List<Integer> genreIds, String targetGenreName) {
		
		// TV 장르 맵, 장르 ID 리스트 또는 타겟 장르명이 비어있으면 false 반환
		if (genreMap == null || CollectionUtils.isEmpty(genreIds) || StringUtils.isEmpty(targetGenreName)) {
			return false;
		}
		// 타겟 장르명이 장르 ID 리스트에 포함되는지 여부 반환
		return genreIds.contains(genreMap.get(targetGenreName));
	}
	
	/**
	 * TMDB API 장르 ID 리스트로 컨텐츠 미디어 타입 코드 조회
	 * 
	 * @param tvGenreMap TV 장르 맵
	 * @param genreIds   장르 ID 리스트
	 * @return 컨텐츠 미디어 타입 코드
	 */
	public static String getContentMediaTypeByGenre(Map<String, Integer> tvGenreMap, List<Integer> genreIds) {

		// 장르 ID 리스트가 비어있으면 버라이어티 미디어 타입 코드 반환
		if (CollectionUtils.isEmpty(genreIds)) {
			return ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode();
		}
		// 장르 판정 및 컨텐츠 미디어 타입 코드 반환
		if (isDramaGenre(tvGenreMap, genreIds)) {
			return ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode();
		} else if (isVarietyGenre(tvGenreMap, genreIds)) {
			return ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode();
		} else {
			if (genreIds.contains(TmdbGenreEnum.GENRE_ANI.getGenreId())) {
				return ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode();
			} else if (genreIds.contains(TmdbGenreEnum.GENRE_DOCUMENTARY.getGenreId())) {
				return ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode();
			} else if (genreIds.contains(TmdbGenreEnum.GENRE_KIDS.getGenreId())) {
				return ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode();
			} else if (genreIds.contains(TmdbGenreEnum.GENRE_NEWS.getGenreId())) {
				return ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode();
			} else {
				return ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode();
			}
		}
	}

}
