package com.cjy.contenthub.search.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjy.contenthub.core.constants.DomainEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.core.shared.service.WishlistSharedService;
import com.cjy.contenthub.search.controller.dto.SearchComicsResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchComicsResultDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;
import com.cjy.contenthub.wishlist.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;

/**
 * 검색 콘텐츠 서비스 구현 클래스 (캐시 미사용)
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SearchNoCacheServiceImpl implements SearchNoCacheService {

	/** 위시리스트 레포지토리 */
	private final WishlistRepository wishlistRepository;
	
	/** 위시리스트 플래그 공유 서비스 */
	private final WishlistSharedService wishlistFlagSharedService;

	/**
	 * 검색 결과에 위시리스트 여부 설정
	 * 
	 * @param serchVideoResponse 검색 결과 DTO
	 * @param userId             유저 ID
	 */
	@Override
	public void setWishlistFromVideoResponse(SearchVideoResponseDto serchVideoResponse, Long userId) {
		
		// 모든 컨텐츠 미디어 타입에 대해 처리
		for (ContentMediaTypeEnum contentMediaTypeEnum : ContentMediaTypeEnum.values()) {
			// TMDB 및 AniList 미디어 타입은 스킵
			if (StringUtils.equalsAny(contentMediaTypeEnum.getContentMediaTypeCode(), 
					ContentMediaTypeEnum.TMDB_MEDIA_TYPE_TV.getContentMediaTypeCode(),
					ContentMediaTypeEnum.TMDB_MEDIA_TYPE_MOVIE.getContentMediaTypeCode(),
					ContentMediaTypeEnum.TMDB_MEDIA_TYPE_PERSON.getContentMediaTypeCode(),
					ContentMediaTypeEnum.ANILIST_MEDIA_TYPE_ANIME.getContentMediaTypeCode(),
					ContentMediaTypeEnum.ANILIST_MEDIA_TYPE_MANGA.getContentMediaTypeCode()
					)) {
				continue;
			}
			// 미디어 타입이 영화인 경우
			if (StringUtils.equals(contentMediaTypeEnum.getContentMediaTypeCode(),
					ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode())) {
				// 각 미디어 타입별로 위시리스트 여부 설정
				wishlistFlagSharedService.setWishlisted(
						serchVideoResponse.getMovieResults(), 
						List.of(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode()), 
						userId, 
						dto -> String.valueOf(dto.getId()),
						SearchMovieResultsDto::setWishlisted,
						wishlistRepository);
			} 
			// 그 외 TV 미디어 타입인 경우
			else {
				// 미디어 타입별 설정
				List<SearchTvResultsDto> tvResults = new ArrayList<>();
				List<String> contentMediaTypeList = new ArrayList<>();
				if (StringUtils.equals(contentMediaTypeEnum.getContentMediaTypeCode(),
						ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode())) {
					tvResults = serchVideoResponse.getAniResults();
					// 애니 컨텐츠 미디어 타입 리스트 생성
					contentMediaTypeList = serchVideoResponse.getAniResults().stream()
							.map(dto -> dto.getContentMediaType())
							.distinct()
							.toList();
				} else if (StringUtils.equals(contentMediaTypeEnum.getContentMediaTypeCode(),
						ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode())) {
					tvResults = serchVideoResponse.getDramaResults();
					contentMediaTypeList = List.of(ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode());
				} else if (StringUtils.equals(contentMediaTypeEnum.getContentMediaTypeCode(),
						ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode())) {
					tvResults = serchVideoResponse.getDocumentaryResults();
					contentMediaTypeList = List.of(ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode());
				} else if (StringUtils.equals(contentMediaTypeEnum.getContentMediaTypeCode(),
						ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode())) {
					tvResults = serchVideoResponse.getKidsResults();
					contentMediaTypeList = List.of(ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode());
				} else if (StringUtils.equals(contentMediaTypeEnum.getContentMediaTypeCode(),
						ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode())) {
					tvResults = serchVideoResponse.getNewsResults();
					contentMediaTypeList = List.of(ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode());
				} else if (StringUtils.equals(contentMediaTypeEnum.getContentMediaTypeCode(),
						ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode())) {
					tvResults = serchVideoResponse.getVarietyResults();
					contentMediaTypeList = List.of(ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode());
				}
				// 결과가 존재하는 경우
				if (!tvResults.isEmpty()) {
					// 위시리스트 여부 설정
					wishlistFlagSharedService.setWishlisted(
							tvResults, 
							contentMediaTypeList, 
							userId,
							dto -> String.valueOf(dto.getId()), 
							SearchTvResultsDto::setWishlisted, 
							wishlistRepository);
				}
			}
		}
	}

	/**
	 * 애니 검색 결과에 위시리스트 여부 설정
	 * 
	 * @param searchTvResponse 검색 결과 DTO
	 * @param userId           유저 ID
	 */
	@Override
	public void setWishlistFromAniResponse(SearchTvResponseDto searchTvResponse, Long userId) {
		
		// 애니메이션 컨텐츠 미디어 타입 리스트 생성
		List<String> aniContentMediaTypeList = searchTvResponse.getAniResults().stream()
				.map(dto -> dto.getContentMediaType())
				.distinct()
				.toList();
		
		// 애니메이션 검색 결과에 위시리스트 여부 설정
		wishlistFlagSharedService.setWishlisted(
				searchTvResponse.getAniResults(), 
				aniContentMediaTypeList, 
				userId, 
				dto -> String.valueOf(dto.getId()),
				SearchTvResultsDto::setWishlisted,
				wishlistRepository);
	}

	/**
	 * TV 검색 결과에 위시리스트 여부 설정 (애니 제외)
	 * 
	 * @param searchTvResponse 검색 결과 DTO
	 * @param userId           유저 ID
	 * @param contentMediaType 컨텐츠 미디어 타입
	 */
	@Override
	public void setWishlistFromTvExceptAniResponse(SearchTvResponseDto searchTvResponse, Long userId, String contentMediaType) {
		
		// 미디어 타입별 설정
		List<SearchTvResultsDto> tvResults = new ArrayList<>();
		List<String> contentMediaTypeList = new ArrayList<>();
		if (StringUtils.equals(contentMediaType, ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode())) {
			tvResults = searchTvResponse.getDramaResults();
			contentMediaTypeList = List.of(ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode());
		} else if (StringUtils.equals(contentMediaType,
				ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode())) {
			tvResults = searchTvResponse.getDocumentaryResults();
			contentMediaTypeList = List.of(ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode());
		} else if (StringUtils.equals(contentMediaType,
				ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode())) {
			tvResults = searchTvResponse.getKidsResults();
			contentMediaTypeList = List.of(ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode());
		} else if (StringUtils.equals(contentMediaType,
				ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode())) {
			tvResults = searchTvResponse.getNewsResults();
			contentMediaTypeList = List.of(ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode());
		} else if (StringUtils.equals(contentMediaType,
				ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode())) {
			tvResults = searchTvResponse.getVarietyResults();
			contentMediaTypeList = List.of(ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode());
		}
		// 결과가 존재하는 경우
		if (!tvResults.isEmpty()) {
			// 위시리스트 여부 설정		
			wishlistFlagSharedService.setWishlisted(
					tvResults, 
					contentMediaTypeList,
					userId, 
					dto -> String.valueOf(dto.getId()),
					SearchTvResultsDto::setWishlisted,
					wishlistRepository);
		}
	}

	/**
	 * 영화 검색 결과에 위시리스트 여부 설정
	 * 
	 * @param searchMovieResponse 검색 결과 DTO
	 * @param userId              유저 ID
	 */
	@Override
	public void setWishlistFromMovieResponse(SearchMovieResponseDto searchMovieResponse, Long userId) {
		
		// 영화 검색 결과에 위시리스트 여부 설정
		wishlistFlagSharedService.setWishlisted(
				searchMovieResponse.getMovieResults(), 
				List.of(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode()), 
				userId, 
				dto -> String.valueOf(dto.getId()),
				SearchMovieResultsDto::setWishlisted,
				wishlistRepository);
	}

	/**
	 * 만화 검색 결과에 위시리스트 여부 설정
	 * 
	 * @param searchComicsResponse 검색 결과 DTO
	 * @param userId               유저 ID
	 */
	@Override
	public void setWishlistFromComicsResponse(SearchComicsResponseDto searchComicsResponse, Long userId) {
		
		// 만화 검색 결과에 위시리스트 여부 설정
		wishlistFlagSharedService.setWishlisted(
				searchComicsResponse.getComicsResults(), 
				List.of(ContentMediaTypeEnum.MEDIA_TYPE_COMICS.getContentMediaTypeCode()), 
				userId, 
				dto -> String.valueOf(dto.getId()),
				SearchComicsResultDto::setWishlisted,
				wishlistRepository);
	}

}
