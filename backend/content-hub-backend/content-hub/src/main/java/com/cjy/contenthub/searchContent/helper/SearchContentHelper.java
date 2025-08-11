package com.cjy.contenthub.searchContent.helper;

import java.util.ArrayList;
import java.util.List;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchMovieResultsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchTvResultsDto;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMediaTypeEnum;

/**
 * 검색 컨텐츠 헬퍼 클래스
 */
public class SearchContentHelper {
	
	/**
	 * private 생성자로 외부에서 인스턴스 생성을 막음
	 */
	private SearchContentHelper() {}
	
	/**
	 * 키워드로 시작하는 검색결과가 먼저 오도록 정렬
	 * 
	 * @param resultStrList 검색결과 문자열 리스트
	 * @param keyword 검색어
	 * @return 정렬된 검색결과 문자열 리스트
	 */
	public static List<String> sortKeywordList(List<String> resultStrList, String keyword) {
		// 키워드 소문자화
		String lowerCaseKeyword = keyword.toLowerCase();
		// 키워드로 시작하는 문자열을 담을 리스트
		List<String> sortedList = new ArrayList<>();
		// 그 외에 리스트
		List<String> otherList = new ArrayList<>();
		// 검색결과에서 문자열을 추출하여 반복처리 
		for (String resultStr : resultStrList) {
			// 검색결과 소문자화
			String lowerCaseResult = resultStr.toLowerCase();
			// 키워드로 시작하는지 판단
			if (lowerCaseResult.startsWith(lowerCaseKeyword)) {
				sortedList.add(resultStr);
			} else {
				otherList.add(resultStr);
			}
		}
		// 키워드 시작 문자열 리스트 + 그 외에 리스트
		sortedList.addAll(otherList);
		
		// 정렬된 리스트 반환
		return sortedList;
	}
	
	/**
	 * 영화 정보 DTO -> 애니 정보 DTO로 변환
	 * 
	 * @param movieResult 영화 정보 DTO
	 * @return 애니 정보 DTO
	 */
	public static TmdbSearchTvResultsDto convertMovieToAni(TmdbSearchMovieResultsDto movieResult) {

		// 영화 정보 DTO를 애니 정보 DTO로 변환
		TmdbSearchTvResultsDto aniResult = new TmdbSearchTvResultsDto();
		aniResult.setAdult(movieResult.isAdult());
		aniResult.setBackdropPath(movieResult.getBackdropPath());
		aniResult.setGenreIds(movieResult.getGenreIds());
		aniResult.setId(movieResult.getId());
		aniResult.setOriginalLanguage(movieResult.getOriginalLanguage());
		aniResult.setOriginalName(movieResult.getOriginalTitle());
		aniResult.setOverview(movieResult.getOverview());
		aniResult.setPopularity(movieResult.getPopularity());
		aniResult.setPosterPath(movieResult.getPosterPath());
		aniResult.setFirstAirDate(movieResult.getReleaseDate());
		aniResult.setName(movieResult.getTitle());
		aniResult.setVoteAverage(movieResult.getVoteAverage());
		aniResult.setVoteCount(movieResult.getVoteCount());
		aniResult.setOriginalMediaType(CommonMediaTypeEnum.MEDIA_TYPE_MOVIE.getMediaTypeCode());

		// 변환 결과 반환
		return aniResult;
	}

}
