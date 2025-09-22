package com.cjy.contenthub.search.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.cjy.contenthub.common.api.dto.aniist.AniListMediaDto;
import com.cjy.contenthub.common.constants.CommonEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.common.constants.CommonEnum.TmdbGenreEnum;
import com.cjy.contenthub.common.constants.TmdbParamConstants;
import com.cjy.contenthub.common.util.BusinessUtil;
import com.cjy.contenthub.search.controller.dto.SearchComicsResultDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;

import lombok.RequiredArgsConstructor;

/**
 * 검색 콘텐츠 헬퍼 클래스
 */
@Component
@RequiredArgsConstructor
public class SearchHelper {

	/** 비즈니스 유틸리티 */
	private final BusinessUtil businessUtil;

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
				.queryParam(PARAM_LANGUAGE, TmdbParamConstants.LANGUAGE_KOREAN)
				.queryParam(PARAM_PAGE, page)
				.toUriString();
	}

	/**
	 * 비디오 검색 결과 DTO를 설정
	 * 
	 * @param tvResponse    TV 검색 응답 DTO
	 * @param movieResponse 영화 검색 응답 DTO
	 * @param movieGenreMap 영화 장르 맵
	 * @return 설정된 비디오 검색 결과 DTO
	 */
	public SearchVideoResponseDto setVideoResponse(
			SearchTvResponseDto tvResponse,
			SearchMovieResponseDto movieResponse,
			Map<String, Integer> movieGenreMap
			) {
		
		// 애니 검색 결과 리스트
		List<SearchTvResultsDto> aniResultList = Optional.ofNullable(tvResponse.getAniResults()).orElse(Collections.emptyList());
		// 드라마 검색 결과 리스트
		List<SearchTvResultsDto> dramaResultList = Optional.ofNullable(tvResponse.getDramaResults()).orElse(Collections.emptyList());
		// 다큐멘터리 검색 결과 리스트
		List<SearchTvResultsDto> documentaryResultList = Optional.ofNullable(tvResponse.getDocumentaryResults()).orElse(Collections.emptyList());
		// 키즈 검색 결과 리스트
		List<SearchTvResultsDto> kidsResultList = Optional.ofNullable(tvResponse.getKidsResults()).orElse(Collections.emptyList());
		// 뉴스 검색 결과 리스트
		List<SearchTvResultsDto> newsResultList = Optional.ofNullable(tvResponse.getNewsResults()).orElse(Collections.emptyList());
		// 버라이어티 검색 결과 리스트
		List<SearchTvResultsDto> varietyResultList = Optional.ofNullable(tvResponse.getVarietyResults()).orElse(Collections.emptyList());
		// 영화 검색 결과 리스트
		List<SearchMovieResultsDto> movieResultList = Optional.ofNullable(movieResponse.getMovieResults()).orElse(Collections.emptyList());
		// 필터링된 영화 리스트
		List<SearchMovieResultsDto> filteredMovieList = new ArrayList<>();
		// 영화 정보에서 애니메이션 정보 추출 
		aniResultList.addAll(getAniMovieList(movieResultList, movieGenreMap));
		// 영화 정보에서 애니영화 제외한 정보 추출
		filteredMovieList.addAll(getMovieList(movieResultList, movieGenreMap));
		
		// 각 미디어 타입별 설정된 페이지당 작품 표시 개수 이상의 데이터가 존재하는지 여부 
		int tvPage = tvResponse.getPage();
		int tvTotalPages = tvResponse.getTotalPages();
		int moviePage = movieResponse.getPage();
		int movieTotalPages = movieResponse.getTotalPages();
		boolean isMoreAni = aniResultList.size() > tmdbPerMainPage;
		boolean isMoreDrama = dramaResultList.size() > tmdbPerMainPage;
		boolean isMoreDocumentary = documentaryResultList.size() > tmdbPerMainPage;
		boolean isMoreKids = kidsResultList.size() > tmdbPerMainPage;
		boolean isMoreNews = newsResultList.size() > tmdbPerMainPage;
		boolean isMoreVariety = varietyResultList.size() > tmdbPerMainPage;
		boolean isMoreMovie = filteredMovieList.size() > tmdbPerMainPage;
		// 애니, 드라마, 영화 정보의 전체보기 여부
		boolean isAniViewMore = tvPage < tvTotalPages || moviePage < movieTotalPages 
				|| isMoreAni || isMoreMovie;
		boolean isDramaViewMore = tvPage < tvTotalPages || isMoreDrama;
		boolean isDocumentaryViewMore = tvPage < tvTotalPages || isMoreDocumentary;
		boolean isKidsViewMore = tvPage < tvTotalPages || isMoreKids;
		boolean isNewsViewMore = tvPage < tvTotalPages || isMoreNews;
		boolean isVarietyViewMore = tvPage < tvTotalPages || isMoreVariety;
		boolean isMovieViewMore = moviePage < movieTotalPages || isMoreMovie;
		
		// 응답 오브젝트 반환
		return SearchVideoResponseDto.builder()
				.aniResults(isMoreAni ? 
						aniResultList.stream().limit(tmdbPerMainPage).toList()
						: aniResultList)
				.dramaResults(isMoreDrama? 
						dramaResultList.stream().limit(tmdbPerMainPage).toList()
						: dramaResultList)
				.movieResults(isMoreMovie ? 
						filteredMovieList.stream().limit(tmdbPerMainPage).toList() 
						: filteredMovieList)
				.documentaryResults(isMoreDocumentary ?
						documentaryResultList.stream().limit(tmdbPerMainPage).toList() : documentaryResultList)
				.kidsResults(isMoreKids ?
						kidsResultList.stream().limit(tmdbPerMainPage).toList() : kidsResultList)
				.newsResults(isMoreNews ?
						newsResultList.stream().limit(tmdbPerMainPage).toList() : newsResultList)
				.varietyResults(isMoreVariety ?
						varietyResultList.stream().limit(tmdbPerMainPage).toList() : varietyResultList)
				.isAniViewMore(isAniViewMore)
				.isDramaViewMore(isDramaViewMore)
				.isDocumentaryViewMore(isDocumentaryViewMore)
				.isKidsViewMore(isKidsViewMore)
				.isNewsViewMore(isNewsViewMore)
				.isVarietyViewMore(isVarietyViewMore)
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
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode())
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
		return getTvListOfGenre(resultList, tvGenreMap, TmdbGenreEnum.GENRE_ANI.getGenreEnglish(),
				ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode());
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
		resultList.stream()
		.filter(result -> !CollectionUtils.isEmpty(result.getGenreIds())
				&& (!CollectionUtils.containsAny(result.getGenreIds(), othersCodeList)
						&& CollectionUtils.containsAny(result.getGenreIds(), dramaCodeList))
				)
		.forEach(result -> {
			result.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode());
			dramaList.add(result);
		});
		return dramaList;
	}

	/**
	 * 다큐멘터리 리스트를 추출
	 * 
	 * @param resultList 검색 결과 리스트
	 * @param tvGenreMap TV 장르 맵
	 * @return 다큐멘터리 리스트
	 */
	public List<SearchTvResultsDto> getDocumentaryList(List<SearchTvResultsDto> resultList, 
			Map<String, Integer> tvGenreMap) {
		return getTvListOfGenre(resultList, tvGenreMap, TmdbGenreEnum.GENRE_DOCUMENTARY.getGenreEnglish(),
				ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode());
	}
	
	/**
	 * 키즈 리스트를 추출
	 * 
	 * @param resultList 검색 결과 리스트
	 * @param tvGenreMap TV 장르 맵
	 * @return 키즈 리스트
	 */
	public List<SearchTvResultsDto> getKidsList(List<SearchTvResultsDto> resultList, 
			Map<String, Integer> tvGenreMap) {
		return getTvListOfGenre(
				resultList, tvGenreMap, TmdbGenreEnum.GENRE_KIDS.getGenreEnglish(), ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode());
	}
	
	/**
	 * 뉴스 리스트를 추출
	 * 
	 * @param resultList 검색 결과 리스트
	 * @param tvGenreMap TV 장르 맵
	 * @return 뉴스 리스트
	 */
	public List<SearchTvResultsDto> getNewsList(List<SearchTvResultsDto> resultList, 
			Map<String, Integer> tvGenreMap) {
		return getTvListOfGenre(
				resultList, tvGenreMap, TmdbGenreEnum.GENRE_NEWS.getGenreEnglish(), ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode());
	}
	
	/**
	 * 버라이어티 리스트를 추출
	 * 
	 * @param resultList 검색 결과 리스트
	 * @param tvGenreMap TV 장르 맵
	 * @return 버라이어티 리스트
	 */
	public List<SearchTvResultsDto> getVarietyList(List<SearchTvResultsDto> resultList, 
			Map<String, Integer> tvGenreMap) {
		List<SearchTvResultsDto> varietyList = new ArrayList<>();
		List<Integer> varietyCodeList = List.of(
				tvGenreMap.get(TmdbGenreEnum.GENRE_REALITY.getGenreEnglish()),
				tvGenreMap.get(TmdbGenreEnum.GENRE_TALK.getGenreEnglish()));
		resultList.stream()
		.filter(result -> result.getGenreIds() != null && result.getGenreIds().isEmpty() 
			|| (!CollectionUtils.isEmpty(result.getGenreIds())
				&& CollectionUtils.containsAny(result.getGenreIds(), varietyCodeList))
			)
		.forEach(result -> {
			result.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode());
			varietyList.add(result);
		});
		return varietyList;
	}
	
	/**
	 * 특정 장르의 TV시리즈 리스트를 추출
	 * 
	 * @param resultList        검색 결과 리스트
	 * @param tvGenreMap        TV 장르 맵
	 * @param targetGenreName   대상 장르 이름
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @return 특정 장르의 TV시리즈 리스트
	 */
	private List<SearchTvResultsDto> getTvListOfGenre(List<SearchTvResultsDto> resultList, 
			Map<String, Integer> tvGenreMap, String targetGenreName, String contentMediaType) {
		List<SearchTvResultsDto> tvList = new ArrayList<>();
		resultList.stream()
		.filter(result -> !CollectionUtils.isEmpty(result.getGenreIds())
				&& result.getGenreIds().contains(tvGenreMap.get(targetGenreName)))
		.forEach(result -> {
			result.setContentMediaType(contentMediaType);
			tvList.add(result);
		});
		return tvList;
	}
	
	/**
	 * 컨텐츠 미디어 타입에 따른 TV시리즈 리스트 추출
	 * 
	 * @param tvResultsList     TV 검색 결과 리스트
	 * @param tvGenreMap        TV 장르 맵
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @return 컨텐츠 미디어 타입에 따른 TV시리즈 리스트
	 */
	public List<SearchTvResultsDto> getTvListOfMediaType(
			List<SearchTvResultsDto> tvResultsList, 
			Map<String, Integer> tvGenreMap,
			String contentMediaType
			) {
		// 컨텐츠 미디어 타입에 따른 TV시리즈 리스트 추출
		if (StringUtils.equals(contentMediaType, ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode())) {
			return getDramaList(tvResultsList, tvGenreMap);
		} else if (StringUtils.equals(contentMediaType, ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode())) {
            return getDocumentaryList(tvResultsList, tvGenreMap);
        } else if (StringUtils.equals(contentMediaType, ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode())) {
            return getKidsList(tvResultsList, tvGenreMap);
        } else if (StringUtils.equals(contentMediaType, ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode())) {
            return getNewsList(tvResultsList, tvGenreMap);
        } else if (StringUtils.equals(contentMediaType, ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode())) {
            return getVarietyList(tvResultsList, tvGenreMap);
        } else {
            return new ArrayList<>();
        }
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
			result.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode());
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
	public List<SearchComicsResultDto> setComicsResponse(List<AniListMediaDto> resultList) {
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
					.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_COMICS.getContentMediaTypeCode())
					.genreIds(businessUtil.genreMappingFromAniListToTmdb(result.getGenres()))
					.build();
			comicsList.add(mediaResult);
		}
		return comicsList;
	}

}
