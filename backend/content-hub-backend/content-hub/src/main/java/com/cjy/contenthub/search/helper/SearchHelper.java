package com.cjy.contenthub.search.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.cjy.contenthub.common.api.dto.aniist.AniListMediaDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbSearchCommonResultsDto;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMediaTypeEnum;
import com.cjy.contenthub.common.constants.CommonEnum.TmdbGenreEnum;
import com.cjy.contenthub.common.repository.entity.ContentEntity;
import com.cjy.contenthub.search.controller.dto.SearchComicsResultDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;
import com.cjy.contenthub.wishlist.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;

/**
 * 검색 콘텐츠 헬퍼 클래스
 */
@Component
@RequiredArgsConstructor
public class SearchHelper {

	/** wishlist 레포지토리 */
	private final WishlistRepository wishlistRepository;

	/** TMDB API 페이지당 작품 표시 개수 */
	@Value("${tmdb.custom.perMainPage}")
	private int tmdbPerMainPage;

	/** 리퀘스트 파라미터 키 : 검색어 */
	private static final String PARAM_QUERY = "query";

	/** 리퀘스트 파라미터 키 : 페이지 */
	private static final String PARAM_PAGE = "page";

	/** 리퀘스트 파라미터 키 : 성인물 포함 여부 */
	private static final String PARAM_INCLUDE_ADULT = "include_adult";

	/** 리퀘스트 파라미터 키 : 언어 */
	private static final String PARAM_LANGUAGE = "language";

	/** 언어 : 한국어 */
	private static final String LANGUAGE_KOREAN = "ko-KR";

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
	 * TV시리즈 검색 URI 생성
	 * 
	 * @param keyword 검색어
	 * @param isAdult 성인물 포함 여부
	 * @param page    페이지 번호
	 * @return 생성된 URI 문자열
	 */
	public String getSearchUri(String searchPath, String keyword, boolean isAdult, int page) {
		return UriComponentsBuilder.fromPath(searchPath)
				.queryParam(PARAM_QUERY, keyword)
				.queryParam(PARAM_INCLUDE_ADULT, isAdult)
				.queryParam(PARAM_LANGUAGE, LANGUAGE_KOREAN)
				.queryParam(PARAM_PAGE, page)
				.toUriString();
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
			List<SearchTvResultsDto> aniList,
			List<SearchTvResultsDto> dramaList,
			List<SearchMovieResultsDto> movieList,
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
	public SearchTvResultsDto convertMovieToAni(SearchMovieResultsDto movieResult) {
		return SearchTvResultsDto.builder()
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
	public List<SearchTvResultsDto> getAniList(List<SearchTvResultsDto> resultList, 
			Map<String, Integer> tvGenreMap) {
		List<SearchTvResultsDto> aniList = new ArrayList<>();
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
	public List<SearchTvResultsDto> getDramaList(List<SearchTvResultsDto> resultList, 
			Map<String, Integer> tvGenreMap) {
		List<SearchTvResultsDto> dramaList = new ArrayList<>();
		resultList.stream()
		.filter(result -> result.getGenreIds() != null && result.getGenreIds().isEmpty()
		|| (!CollectionUtils.isEmpty(result.getGenreIds())
				&& (!result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_ANI.getGenreEnglish()))
						&& !result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_DOCUMENTARY.getGenreEnglish()))
						&& !result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_KIDS.getGenreEnglish()))
						&& !result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_NEWS.getGenreEnglish()))
						&& !result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_REALITY.getGenreEnglish()))
						&& !result.getGenreIds().contains(tvGenreMap.get(TmdbGenreEnum.GENRE_TALK.getGenreEnglish()))))
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
	public List<SearchTvResultsDto> getAniMovieList(List<SearchMovieResultsDto> resultList, 
			Map<String, Integer> movieGenreMap) {
		List<SearchTvResultsDto> aniMovieList = new ArrayList<>();
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
	public List<SearchMovieResultsDto> getMovieList(List<SearchMovieResultsDto> resultList, 
			Map<String, Integer> movieGenreMap) {
		List<SearchMovieResultsDto> movieList = new ArrayList<>();
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
	 * @param userId 유저 테이블 ID
	 * @return 만화 검색 결과 리스트
	 */
	public List<SearchComicsResultDto> setComicsResponse(List<AniListMediaDto> resultList, Long userId) {
		List<SearchComicsResultDto> comicsList = new ArrayList<>();
		for (AniListMediaDto result : resultList) {
			String mediaTitle = ObjectUtils.isNotEmpty(result.getTitle()) ? result.getTitle().getUserPreferred() : "";
			String mediaLargeImage = ObjectUtils.isNotEmpty(result.getCoverImage()) ? result.getCoverImage().getLarge() : "";
			String mediaExtraLargeImage = ObjectUtils.isNotEmpty(result.getCoverImage()) ? result.getCoverImage().getExtraLarge() : "";
			SearchComicsResultDto mediaResult = SearchComicsResultDto.builder()
					.id(result.getId())
					.title(mediaTitle)
					.backdropPath(mediaLargeImage)
					.posterPath(mediaExtraLargeImage)
					.originalMediaType(CommonMediaTypeEnum.MEDIA_TYPE_COMICS.getMediaTypeCode())
					.build();
			comicsList.add(mediaResult);
		}
		// 로그인 유저의 위시리스트 등록 여부 설정
		if (userId != null) {
			setWishlist(comicsList, CommonMediaTypeEnum.MEDIA_TYPE_COMICS.getMediaTypeCode(), userId);
		}
		
		return comicsList;
	}

	/**
	 * 위시리스트 등록 여부 설정
	 * 
	 * @param resultsList       검색 결과 리스트
	 * @param originalMediaType 원작품 미디어 타입
	 * @param userId       사용자 테이블 ID
	 */
	public void setWishlist(List<? extends TmdbSearchCommonResultsDto> resultsList, String originalMediaType, Long userId) {
		
		// 검색 결과가 비어있으면 처리 종료
		if (resultsList == null || resultsList.isEmpty()) {
			return;
		}

		// 검색 결과에서 ID 리스트 추출
		List<String> apiIdList = resultsList.stream().map(e -> String.valueOf(e.getId())).toList();

		// 로그인 유저가 위시리스트에 등록한 컨텐츠 조회
		List<ContentEntity> contentList = wishlistRepository.getRegisteredWishlist(userId, originalMediaType, apiIdList);
		
		// 위시리스트에 등록한 컨텐츠가 없으면 처리 종료
		if (contentList == null || contentList.isEmpty()) {
			return;
		}

		// 검색 결과와 위시리스트에 등록한 컨텐츠를 비교하여 위시리스트 여부 설정
		for (TmdbSearchCommonResultsDto results : resultsList) {
			for (ContentEntity content : contentList) {
				if (content.getOriginalMediaType().equals(originalMediaType)
						&& content.getApiId().equals(String.valueOf(results.getId()))) {
					if (results instanceof SearchTvResultsDto tvResults) {
						tvResults.setWishlist(true);
					} else if (results instanceof SearchMovieResultsDto movieResults) {
						movieResults.setWishlist(true);
					} else if (results instanceof SearchComicsResultDto comicsResults) {
						comicsResults.setWishlist(true);
					}
					break;
				}
			}
		}
	}

}
