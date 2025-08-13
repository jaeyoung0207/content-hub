package com.cjy.contenthub.search.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;

import com.cjy.contenthub.common.api.dto.aniist.AniListMediaDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchMovieResultsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchTvResultsDto;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMediaTypeEnum;
import com.cjy.contenthub.common.constants.CommonEnum.TmdbGenreEnum;
import com.cjy.contenthub.search.controller.dto.SearchComicsMediaResultDto;

/**
 * 검색 컨텐츠 헬퍼 클래스
 */
public class SearchHelper {

	/**
	 * private 생성자로 외부에서 인스턴스 생성을 막음
	 */
	private SearchHelper() {}

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
		return TmdbSearchTvResultsDto.builder()
				.adult(movieResult.isAdult())
				.backdropPath(movieResult.getBackdropPath())
				.genreIds(movieResult.getGenreIds())
				.id(movieResult.getId())
				.originalLanguage(movieResult.getOriginalLanguage())
				.originalName(movieResult.getOriginalTitle())
				.overview(movieResult.getOverview())
				.popularity(movieResult.getPopularity())
				.posterPath(movieResult.getPosterPath())
				.firstAirDate(movieResult.getReleaseDate())
				.name(movieResult.getTitle())
				.voteAverage(movieResult.getVoteAverage())
				.voteCount(movieResult.getVoteCount())
				.originalMediaType(CommonMediaTypeEnum.MEDIA_TYPE_MOVIE.getMediaTypeCode())
				.build();
	}

	/**
	 * 애니 리스트를 추출
	 * 
	 * @param resultList 검색 결과 리스트
	 * @param tvGenreMap TV 장르 맵
	 * @return 애니 리스트
	 */
	public static List<TmdbSearchTvResultsDto> getAniList(List<TmdbSearchTvResultsDto> resultList, 
			Map<String, Integer> tvGenreMap) {
		List<TmdbSearchTvResultsDto> aniList = new ArrayList<>();
		resultList.stream()
		.filter(result -> !CollectionUtils.isEmpty(result.getGenreIds())
				&& result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_ANI.getGenre())))
		.forEach(result -> {
			result.setOriginalMediaType(CommonMediaTypeEnum.MEDIA_TYPE_ANI.getMediaTypeCode());
			aniList.add(result);
		});
		return aniList;
	}

	/**
	 * 드라마 리스트를 추출
	 * 
	 * @param resultList 검색 결과 리스트
	 * @param tvGenreMap TV 장르 맵
	 * @return 드라마 리스트
	 */
	public static List<TmdbSearchTvResultsDto> getDramaList(List<TmdbSearchTvResultsDto> resultList, 
			Map<String, Integer> tvGenreMap) {
		List<TmdbSearchTvResultsDto> dramaList = new ArrayList<>();
		resultList.stream()
		.filter(result -> !CollectionUtils.isEmpty(result.getGenreIds())
				&& (result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_DRAMA.getGenre()))
						|| result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_SOAP.getGenre()))))
		.forEach(result -> {
			result.setOriginalMediaType(CommonMediaTypeEnum.MEDIA_TYPE_DRAMA.getMediaTypeCode());
			dramaList.add(result);
		});
		return dramaList;
	}

	/**
	 * 애니 영화 리스트를 추출
	 * 
	 * @param resultList    검색 결과 리스트
	 * @param movieGenreMap 영화 장르 맵
	 * @return 애니 영화 리스트
	 */
	public static List<TmdbSearchTvResultsDto> getAniMovieList(List<TmdbSearchMovieResultsDto> resultList, 
			Map<String, Integer> movieGenreMap) {
		List<TmdbSearchTvResultsDto> aniMovieList = new ArrayList<>();
		resultList.stream()
		.filter(result -> !CollectionUtils.isEmpty(result.getGenreIds())
				&& result.getGenreIds().contains(movieGenreMap.get(TmdbGenreEnum.GENRE_ANI.getGenre())))
		.forEach(result -> 
		aniMovieList.add(convertMovieToAni(result))
				);
		return aniMovieList;
	}

	/**
	 * 애니 영화를 제외한 영화 리스트를 추출
	 * 
	 * @param resultList    검색 결과 리스트
	 * @param movieGenreMap 영화 장르 맵
	 * @return 영화 리스트
	 */
	public static List<TmdbSearchMovieResultsDto> getMovieList(List<TmdbSearchMovieResultsDto> resultList, 
			Map<String, Integer> movieGenreMap) {
		List<TmdbSearchMovieResultsDto> movieList = new ArrayList<>();
		resultList.stream()
		.filter(result -> result.getGenreIds() != null && result.getGenreIds().isEmpty()
		|| (!CollectionUtils.isEmpty(result.getGenreIds())
				&& !result.getGenreIds().contains(movieGenreMap.get(TmdbGenreEnum.GENRE_ANI.getGenre()))))
		.forEach(result -> {
			result.setOriginalMediaType(CommonMediaTypeEnum.MEDIA_TYPE_MOVIE.getMediaTypeCode());
			movieList.add(result);
		});
		return movieList;
	}

	/**
	 * 만화 검색 결과를 설정
	 * 
	 * @param resultList 검색 결과 리스트
	 * @return 만화 검색 결과 리스트
	 */
	public static List<SearchComicsMediaResultDto> setComicsResponse(List<AniListMediaDto> resultList) {
		List<SearchComicsMediaResultDto> comicsList = new ArrayList<>();
		for (AniListMediaDto result : resultList) {
			String mediaTitle = ObjectUtils.isNotEmpty(result.getTitle()) ? result.getTitle().getUserPreferred() : "";
			String mediaLargeImage = ObjectUtils.isNotEmpty(result.getCoverImage()) ? result.getCoverImage().getLarge() : "";
			String mediaExtraLargeImage = ObjectUtils.isNotEmpty(result.getCoverImage()) ? result.getCoverImage().getExtraLarge() : "";
			SearchComicsMediaResultDto mediaResult = SearchComicsMediaResultDto.builder()
					.id(result.getId())
					.title(mediaTitle)
					.backdropPath(mediaLargeImage)
					.posterPath(mediaExtraLargeImage)
					.originalMediaType(CommonMediaTypeEnum.MEDIA_TYPE_COMICS.getMediaTypeCode())
					.build();
			comicsList.add(mediaResult);
		}
		return comicsList;
	}

}
