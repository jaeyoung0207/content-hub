package com.cjy.contenthub.detail.helper;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.cjy.contenthub.common.api.dto.aniist.AniListCoverImageDto;
import com.cjy.contenthub.common.api.dto.aniist.AniListMediaDto;
import com.cjy.contenthub.common.api.dto.aniist.AniListMediaRecommendationDetailDto;
import com.cjy.contenthub.common.constants.CommonEnum;
import com.cjy.contenthub.detail.controller.dto.DetailComicsRecommendationsResultDto;
import com.cjy.contenthub.detail.repository.DetailCommentViewRepository;
import com.cjy.contenthub.detail.repository.entity.DetailCommentViewEntity;

import lombok.RequiredArgsConstructor;

/**
 * 상세 화면 헬퍼 클래스
 */
@Component
@RequiredArgsConstructor
public class DetailHelper {

	/** 코멘트 뷰 엔티티 리포지토리 */
	private final DetailCommentViewRepository commentViewRepository;

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

	private static final int FIRST_PAGE_INDEX = 0;

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
	 * 각 페이지당 코멘트 리스트 처리
	 * 유저ID에 해당하는 코멘트를 첫번째 페이지에 추가
	 * 
	 * @param commentList 코멘트 엔티티 리스트
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId API ID
	 * @param page 페이지 번호
	 * @param userId 유저 ID
	 */
	public void getCommentListPerPage(
			List<DetailCommentViewEntity> commentList,
			String originalMediaType,
			String apiId,
			Integer page,
			String userId
			) {

		// 유저ID에 해당하는 코멘트를 추출
		DetailCommentViewEntity myCommentViewEntity = commentList.stream()
				.filter(e -> StringUtils.equals(e.getUserId(), userId))
				.findFirst()
				.orElse(commentViewRepository.findByOriginalMediaTypeAndApiIdAndUserId(originalMediaType, apiId, userId));

		// 유저ID에 해당하는 코멘트가 없는 경우 처리 종료
		if (ObjectUtils.isEmpty(myCommentViewEntity)) {
			return;
		}

		// 유저ID에 해당하는 코멘트 삭제
		commentList.removeIf(e -> StringUtils.equals(e.getUserId(), userId));

		// 첫번째 페이지의 경우 유저ID의 코멘트 추가
		if (page.equals(FIRST_PAGE_INDEX)) {
			commentList.add(0, myCommentViewEntity);
		}
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
								.build());
					});
		}
	}
}
