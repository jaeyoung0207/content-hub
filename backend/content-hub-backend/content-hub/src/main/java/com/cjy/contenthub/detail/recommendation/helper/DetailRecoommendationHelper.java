package com.cjy.contenthub.detail.recommendation.helper;

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

import com.cjy.contenthub.common.integration.anilist.dto.AniListCoverImageDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListMediaDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListMediaRecommendationDetailDto;
import com.cjy.contenthub.common.integration.tmdb.constants.TmdbParamConstants;
import com.cjy.contenthub.core.constants.DomainEnum.AniListMediaTypeEnum;
import com.cjy.contenthub.core.constants.DomainEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.core.constants.DomainEnum.TmdbGenreEnum;
import com.cjy.contenthub.core.shared.service.GenreSharedService;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsComicsResultDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsTvResultsDto;

import lombok.RequiredArgsConstructor;

/**
 * 상세 화면 추천 헬퍼 클래스
 */
@Component
@RequiredArgsConstructor
public class DetailRecoommendationHelper {
	
	/** 장르 공유 서비스 */
	private final GenreSharedService genreSharedService;

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
	 * @param page     페이지 번호
	 * @param language 언어 코드
	 * @return String 생성된 URI
	 */
	public String getMovieRecommendationUri(Integer movieId, Integer page, String language) {
		return UriComponentsBuilder.fromPath(String.format(movieRecommendationsPath, movieId))
				.queryParam(TmdbParamConstants.PARAM_MOVIE_ID, movieId)
				.queryParam(TmdbParamConstants.PARAM_LANGUAGE, language)
				.queryParam(TmdbParamConstants.PARAM_PAGE, Optional.ofNullable(page).orElse(1))
				.toUriString();
	}

	/**
	 * TMDB TV 추천 작품 조회를 위한 URI 생성
	 * 
	 * @param seriesId TV 시리즈 ID
	 * @param page     페이지 번호
	 * @param language 언어 코드
	 * @return String 생성된 URI
	 */
	public String getTVRecommendationUri(Integer seriesId, Integer page, String language) {
		return UriComponentsBuilder.fromPath(String.format(tvRecommendationsPath, seriesId))
				.queryParam(TmdbParamConstants.PARAM_TV_SERIES_ID, seriesId)
				.queryParam(TmdbParamConstants.PARAM_LANGUAGE, language)
				.queryParam(TmdbParamConstants.PARAM_PAGE, Optional.ofNullable(page).orElse(1))
				.toUriString();
	}

	/**
	 * TMDB TV 추천 작품 결과에서 장르 정보를 기반으로 미디어 타입 설정 및 필터링
	 * 
	 * @param results  TMDB 추천 작품 결과 리스트
	 * @param genreMap TMDB 장르 맵
	 * @return 필터링된 추천 작품 리스트
	 */
	public List<DetailRecommendationsTvResultsDto> setTvRecommendationResults(
			List<DetailRecommendationsTvResultsDto> results, Map<String, Integer> genreMap) {
		List<DetailRecommendationsTvResultsDto> filterdResultList = new ArrayList<>();
		for (DetailRecommendationsTvResultsDto result : results) {
			// 장르 정보에 따라 미디어 타입 설정 및 필터링
			if (!CollectionUtils.isEmpty(result.getGenreIds())) {
				if (result.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_ANI.getGenreEnglish()))) {
					result.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode());
					filterdResultList.add(result);
				} else if (!result.getGenreIds()
						.contains(genreMap.get(TmdbGenreEnum.GENRE_DOCUMENTARY.getGenreEnglish()))
						&& !result.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_KIDS.getGenreEnglish()))
						&& !result.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_NEWS.getGenreEnglish()))
						&& !result.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_REALITY.getGenreEnglish()))
						&& !result.getGenreIds().contains(genreMap.get(TmdbGenreEnum.GENRE_TALK.getGenreEnglish()))) {
					result.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode());
					filterdResultList.add(result);
				} else {
					result.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode());
					filterdResultList.add(result);
				}
			} else {
				result.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode());
				filterdResultList.add(result);
			}
		}
		return filterdResultList;
	}

	/**
	 * 만화 추천 작품과 관련된 작품들을 조회하여 결과 리스트에 추가
	 * 
	 * @param media   만화 미디어 DTO
	 * @param results 결과 리스트
	 */
	public void getComicsRelations(AniListMediaDto media, List<DetailRecommendationsComicsResultDto> results) {
		// 만화와의 관련 작품이 존재하는 경우 결과 리스트에 추가
		if (ObjectUtils.isNotEmpty(media.getRelations()) && ObjectUtils.isNotEmpty(media.getRelations().getNodes())) {
			media.getRelations().getNodes().stream()
			.filter(e -> StringUtils.equals(e.getType(),
					AniListMediaTypeEnum.MEDIA_TYPE_MANGA.getAnilistMediaType()))
			.forEach(node -> results.add(DetailRecommendationsComicsResultDto.builder()
					.id(node.getId())
					.title(node.getTitle().getUserPreferred())
					.backdropPath(node.getCoverImage().getLarge())
					.posterPath(node.getCoverImage().getExtraLarge())
					.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_COMICS.getContentMediaTypeCode())
					.genreIds(genreSharedService.genreMappingFromAniListToTmdb(node.getGenres()))
					.build()));
		}
	}

	/**
	 * 만화 추천 작품을 조회하여 결과 리스트에 추가
	 * 
	 * @param media   만화 미디어 DTO
	 * @param results 결과 리스트
	 */
	public void getComicsRecommendations(AniListMediaDto media, List<DetailRecommendationsComicsResultDto> results) {
		// 만화 추천 작품이 존재하는 경우 결과 리스트에 추가
		if (ObjectUtils.isNotEmpty(media.getRecommendations())
				&& ObjectUtils.isNotEmpty(media.getRecommendations().getNodes())) {
			media.getRecommendations().getNodes().stream()
			.filter(e -> StringUtils.equals(e.getMediaRecommendation().getType(),
					AniListMediaTypeEnum.MEDIA_TYPE_MANGA.getAnilistMediaType()))
			.forEach(node -> {
				AniListMediaRecommendationDetailDto recommendationDetail = node.getMediaRecommendation();
				String title = Optional.ofNullable(recommendationDetail.getTitle())
						.map(t -> t.getUserPreferred()).orElse(StringUtils.EMPTY);
				String backdropPath = StringUtils.EMPTY;
				String posterPath = StringUtils.EMPTY;
				AniListCoverImageDto coverImage = recommendationDetail.getCoverImage();
				if (ObjectUtils.isNotEmpty(coverImage)) {
					backdropPath = coverImage.getLarge();
					posterPath = coverImage.getExtraLarge();
				}
				results.add(DetailRecommendationsComicsResultDto.builder()
						.id(recommendationDetail.getId())
						.title(title)
						.backdropPath(backdropPath)
						.posterPath(posterPath)
						.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_COMICS.getContentMediaTypeCode())
						.genreIds(genreSharedService.genreMappingFromAniListToTmdb(recommendationDetail.getGenres()))
						.build());
			});
		}
	}

}
