package com.cjy.contenthub.search.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.cjy.contenthub.common.api.dto.aniist.AniListMediaDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchMovieResultsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchTvResultsDto;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMediaTypeEnum;
import com.cjy.contenthub.common.constants.CommonEnum.TmdbGenreEnum;
import com.cjy.contenthub.search.controller.dto.SearchComicsMediaResultDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;

/**
 * 검색 컨텐츠 헬퍼 클래스
 */
@Component
public class SearchHelper {
	
	/** TMDB API 페이지당 작품 표시 개수 */
	@Value("${tmdb.custom.perMainPage}")
	private int tmdbPerMainPage;

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
	public List<String> sortKeywordList(List<String> resultStrList, String keyword) {
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
	 * 비디오 검색 결과 DTO를 설정
	 * 
	 * @param aniList         애니 리스트
	 * @param dramaList       드라마 리스트
	 * @param movieList       영화 리스트
	 * @param tmdbPerMainPage 페이지당 작품 표시 개수
	 * @param tvPage          TV 페이지 번호
	 * @param tvTotalPages    TV 전체 페이지 수
	 * @param moviePage       영화 페이지 번호
	 * @param movieTotalPages 영화 전체 페이지 수
	 * @return 설정된 비디오 검색 결과 DTO
	 */
	public SearchVideoResponseDto setVideoResponse(
			List<TmdbSearchTvResultsDto> aniList,
			List<TmdbSearchTvResultsDto> dramaList,
			List<TmdbSearchMovieResultsDto> movieList,
			int tvPage,
			int tvTotalPages,
			int moviePage,
			int movieTotalPages
			) {
		// 설정된 페이지당 작품 표시 개수 이상의 애니, 드라마, 영화 정보가 있는지 여부
		boolean isMoreAni = aniList.size() > tmdbPerMainPage;
		boolean isMoreDrama = dramaList.size() > tmdbPerMainPage;
		boolean isMoreMovie = movieList.size() > tmdbPerMainPage;
		// 애니, 드라마, 영화 정보의 전체보기 여부
		boolean isAniViewMore = tvPage < tvTotalPages || moviePage < movieTotalPages 
				|| isMoreAni || isMoreMovie;
		boolean isDramaViewMore = tvPage < tvTotalPages || isMoreDrama;
		boolean isMovieViewMore = moviePage < movieTotalPages || isMoreMovie;
		// 응답값 생성
		return SearchVideoResponseDto.builder()
				.aniResults(isMoreAni ? 
						aniList.stream().limit(tmdbPerMainPage).toList()
						: aniList)
				.dramaResults(isMoreDrama? 
						dramaList.stream().limit(tmdbPerMainPage).toList()
						: dramaList)
				.movieResults(isMoreMovie ? 
						movieList.stream().limit(tmdbPerMainPage).toList() 
						: movieList)
				.isAniViewMore(isAniViewMore)
				.isDramaViewMore(isDramaViewMore)
				.isMovieViewMore(isMovieViewMore)
				.build();
	}

	/**
	 * 영화 정보 DTO -> 애니 정보 DTO로 변환
	 * 
	 * @param movieResult 영화 정보 DTO
	 * @return 애니 정보 DTO
	 */
	public TmdbSearchTvResultsDto convertMovieToAni(TmdbSearchMovieResultsDto movieResult) {
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
	public List<TmdbSearchTvResultsDto> getAniList(List<TmdbSearchTvResultsDto> resultList, 
			Map<String, Integer> tvGenreMap) {
		List<TmdbSearchTvResultsDto> aniList = new ArrayList<>();
		resultList.stream()
		.filter(result -> !CollectionUtils.isEmpty(result.getGenreIds())
				&& result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_ANI.getGenreEnglish())))
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
	public List<TmdbSearchTvResultsDto> getDramaList(List<TmdbSearchTvResultsDto> resultList, 
			Map<String, Integer> tvGenreMap) {
		List<TmdbSearchTvResultsDto> dramaList = new ArrayList<>();
		resultList.stream()
		.filter(result -> !CollectionUtils.isEmpty(result.getGenreIds())
				&& (!result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_ANI.getGenreEnglish()))
						&& !result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_DOCUMENTARY.getGenreEnglish()))
						&& !result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_KIDS.getGenreEnglish()))
						&& !result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_NEWS.getGenreEnglish()))
						&& !result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_REALITY.getGenreEnglish()))
						&& !result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_TALK.getGenreEnglish())))
				)
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
	public List<TmdbSearchTvResultsDto> getAniMovieList(List<TmdbSearchMovieResultsDto> resultList, 
			Map<String, Integer> movieGenreMap) {
		List<TmdbSearchTvResultsDto> aniMovieList = new ArrayList<>();
		resultList.stream()
		.filter(result -> !CollectionUtils.isEmpty(result.getGenreIds())
				&& result.getGenreIds().contains(movieGenreMap.get(TmdbGenreEnum.GENRE_ANI.getGenreEnglish())))
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
	public List<TmdbSearchMovieResultsDto> getMovieList(List<TmdbSearchMovieResultsDto> resultList, 
			Map<String, Integer> movieGenreMap) {
		List<TmdbSearchMovieResultsDto> movieList = new ArrayList<>();
		resultList.stream()
		.filter(result -> result.getGenreIds() != null && result.getGenreIds().isEmpty()
		|| (!CollectionUtils.isEmpty(result.getGenreIds())
				&& !result.getGenreIds().contains(movieGenreMap.get(TmdbGenreEnum.GENRE_ANI.getGenreEnglish()))))
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
	public List<SearchComicsMediaResultDto> setComicsResponse(List<AniListMediaDto> resultList) {
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
