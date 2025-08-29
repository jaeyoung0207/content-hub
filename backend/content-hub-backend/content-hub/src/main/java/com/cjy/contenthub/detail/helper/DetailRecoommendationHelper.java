package com.cjy.contenthub.detail.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.cjy.contenthub.common.api.dto.aniist.AniListCoverImageDto;
import com.cjy.contenthub.common.api.dto.aniist.AniListMediaDto;
import com.cjy.contenthub.common.api.dto.aniist.AniListMediaRecommendationDetailDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsTvDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsTvResultsDto;
import com.cjy.contenthub.common.constants.CommonEnum;
import com.cjy.contenthub.common.constants.CommonEnum.CommonMediaTypeEnum;
import com.cjy.contenthub.common.constants.CommonEnum.TmdbGenreEnum;
import com.cjy.contenthub.detail.controller.dto.DetailComicsRecommendationsResultDto;

import lombok.RequiredArgsConstructor;

/**
 * 상세 화면 헬퍼 클래스
 */
@Component
@RequiredArgsConstructor
public class DetailRecoommendationHelper {

	/** 리퀘스트 파라미터 키 : TV SERIES ID */
	private static final String PARAM_TV_SERIES_ID = "series_id";

	/** 리퀘스트 파라미터 키 : MOVIE ID */
	private static final String PARAM_MOVIE_ID = "movie_id";

	/** 리퀘스트 파라미터 키 : 페이지 번호 */
	private static final String PARAM_PAGE = "page";

	/** 리퀘스트 파라미터 키 : 언어 */
	private static final String PARAM_LANGUAGE = "language";

	/** TMDB API TV 추천 작품 API 패스 */
	@Value("${tmdb.url.tvRecommendationsPath}")
	private String tvRecommendationsPath;

	/** TMDB API Movie 추천 작품 API 패스 */
	@Value("${tmdb.url.movieRecommendationsPath}")
	private String movieRecommendationsPath;

	/**
	 * TMDB 영화 추천 작품 조회를 위한 URI 생성
	 * 
	 * @param movieId  영화 ID
	 * @param page 페이지 번호
	 * @param language 언어 코드
	 * @return String 생성된 URI
	 */
	public String getMovieRecommendationUri(Integer movieId, Integer page, String language) {
		return UriComponentsBuilder.fromPath(String.format(movieRecommendationsPath, movieId))
				.queryParam(PARAM_MOVIE_ID, movieId)
				.queryParam(PARAM_LANGUAGE, language)
				.queryParam(PARAM_PAGE, Optional.ofNullable(page).orElse(1))
				.toUriString();
	}

	/**
	 * TMDB TV 추천 작품 조회를 위한 URI 생성
	 * 
	 * @param seriesId TV 시리즈 ID
	 * @param page 페이지 번호
	 * @param language 언어 코드
	 * @return String 생성된 URI
	 */
	public String getTVRecommendationUri(Integer seriesId, Integer page, String language) {
		return UriComponentsBuilder.fromPath(String.format(tvRecommendationsPath, seriesId))
				.queryParam(PARAM_TV_SERIES_ID, seriesId)
				.queryParam(PARAM_LANGUAGE, language)
				.queryParam(PARAM_PAGE, Optional.ofNullable(page).orElse(1))
				.toUriString();
	}
	
	/**
	 * TMDB TV 추천 작품 결과에서 장르 정보를 기반으로 미디어 타입 설정 및 필터링
	 * 
	 * @param results  TMDB 추천 작품 결과 리스트
	 * @param genreMap TMDB 장르 맵
	 * @return 필터링된 추천 작품 리스트
	 */
	public List<TmdbRecommendationsTvResultsDto> setTvRecommendationResults(
			List<TmdbRecommendationsTvResultsDto> results,
			Map<String, Integer> genreMap
			) {
		List<TmdbRecommendationsTvResultsDto> filterdResultList = new ArrayList<>();
		for (TmdbRecommendationsTvResultsDto result : results) {
			if (!CollectionUtils.isEmpty(result.getGenreIds())) {
				// 장르 정보에 따라 미디어 타입 설정 및 필터링
				if (result.getGenreIds().contains(
						genreMap.get(TmdbGenreEnum.GENRE_ANI.getGenreEnglish()))) {
					result.setOriginalMediaType(
							CommonMediaTypeEnum.MEDIA_TYPE_ANI.getMediaTypeCode());
					filterdResultList.add(result);
				} else if (!result.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_DOCUMENTARY.getGenreEnglish()))
						&& !result.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_KIDS.getGenreEnglish()))
						&& !result.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_NEWS.getGenreEnglish()))
						&& !result.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_REALITY.getGenreEnglish()))
						&& !result.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_TALK.getGenreEnglish()))) {
					result.setOriginalMediaType(
							CommonMediaTypeEnum.MEDIA_TYPE_DRAMA.getMediaTypeCode());
					filterdResultList.add(result);
				} else {
					result.setOriginalMediaType(
							CommonMediaTypeEnum.MEDIA_TYPE_VARIETY.getMediaTypeCode());
					filterdResultList.add(result);
				}
			}
		}
		return filterdResultList;
	}
	
	/**
	 * 필터링된 결과를 포함하는 새로운 DTO 객체 생성
	 * 
	 * @param response TMDB 추천 작품 응답 DTO
	 * @param genreMap TMDB 장르 맵
	 * @return 필터링된 추천 작품을 포함하는 TMDB 추천 작품 응답 DTO
	 */
	public TmdbRecommendationsTvDto setTvRecommendationResult(TmdbRecommendationsTvDto response,
			Map<String, Integer> genreMap) {
		// 응답 정보 필터링
		List<TmdbRecommendationsTvResultsDto> filterdResultList = 
				setTvRecommendationResults(response.getResults(), genreMap);
		return TmdbRecommendationsTvDto.builder()
				.page(response.getPage())
				.totalPages(response.getTotalPages())
				.totalResults(response.getTotalResults())
				.results(filterdResultList)
				.build();
	}

	/**
	 * 만화 추천 작품과 관련된 작품들을 조회하여 결과 리스트에 추가
	 * 
	 * @param media 만화 미디어 DTO
	 * @param results 결과 리스트
	 */
	public void getComicsRelations(
			AniListMediaDto media,
			List<DetailComicsRecommendationsResultDto> results) {
		// 만화와의 관련 작품이 존재하는 경우 결과 리스트에 추가
		if (ObjectUtils.isNotEmpty(media.getRelations()) 
				&& ObjectUtils.isNotEmpty(media.getRelations().getNodes())) {
			media.getRelations().getNodes().stream()
			.filter(e -> StringUtils.equals(e.getType(), CommonEnum.AniListMediaTypeEnum.MEDIA_TYPE_MANGA.getMediaType()))
			.forEach(
					node -> results.add(DetailComicsRecommendationsResultDto.builder()
							.id(node.getId())
							.title(node.getTitle().getUserPreferred())
							.backdropPath(node.getCoverImage().getLarge())
							.posterPath(node.getCoverImage().getExtraLarge())
							.build())
					);
		}
	}

	/**
	 * 만화 추천 작품을 조회하여 결과 리스트에 추가
	 * 
	 * @param media 만화 미디어 DTO
	 * @param results 결과 리스트
	 */
	public void getComicsRecommendations(AniListMediaDto media,
			List<DetailComicsRecommendationsResultDto> results) {
		// 만화 추천 작품이 존재하는 경우 결과 리스트에 추가
		if (ObjectUtils.isNotEmpty(media.getRecommendations())
				&& ObjectUtils.isNotEmpty(media.getRecommendations().getNodes())) {
			media.getRecommendations().getNodes().stream()
			.filter(e -> StringUtils.equals(e.getMediaRecommendation().getType(), CommonEnum.AniListMediaTypeEnum.MEDIA_TYPE_MANGA.getMediaType()))
			.forEach(
					node -> {
						AniListMediaRecommendationDetailDto recommendationDetail = node.getMediaRecommendation();
						String title = Optional.ofNullable(recommendationDetail.getTitle())
								.map(t -> t.getUserPreferred())
								.orElse(StringUtils.EMPTY);
						String backdropPath = StringUtils.EMPTY;
						String posterPath = StringUtils.EMPTY;
						AniListCoverImageDto coverImage = recommendationDetail.getCoverImage();
						if (ObjectUtils.isNotEmpty(coverImage)) {
							backdropPath = coverImage.getLarge();
							posterPath = coverImage.getExtraLarge();
						}
						results.add(DetailComicsRecommendationsResultDto.builder()
								.id(recommendationDetail.getId())
								.title(title)
								.backdropPath(backdropPath)
								.posterPath(posterPath)
								.originalMediaType(CommonMediaTypeEnum.MEDIA_TYPE_COMICS.getMediaTypeCode())
								.build());
					});
		}
	}
}
