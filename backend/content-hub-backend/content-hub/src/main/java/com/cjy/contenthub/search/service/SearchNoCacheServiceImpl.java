package com.cjy.contenthub.search.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.core.constants.DomainEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.core.constants.DomainEnum.DomainMessagesWarnEnum;
import com.cjy.contenthub.core.shared.service.WishlistSharedService;
import com.cjy.contenthub.search.controller.dto.SearchComicsResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchComicsResultDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;
import com.cjy.contenthub.wishlist.repository.WishlistRepository;

import io.jsonwebtoken.lang.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 검색 콘텐츠 서비스 구현 클래스 (캐시 미사용)
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class SearchNoCacheServiceImpl implements SearchNoCacheService {

	/** 위시리스트 레포지토리 */
	private final WishlistRepository wishlistRepository;
	
	/** 위시리스트 플래그 공유 서비스 */
	private final WishlistSharedService wishlistFlagSharedService;
	
	/** 메시지 유틸 클래스 */
	private final MessageUtil messageUtil;
	
	/** 비디오 컨텐츠 미디어 타입 리스트 */
	private static final List<String> VIDEO_CONTENT_MEDIA_TYPE_LIST = Arrays.asList(ContentMediaTypeEnum.values()).stream()
			.filter(cm -> !StringUtils.equalsAny(cm.getContentMediaTypeCode(),
					ContentMediaTypeEnum.TMDB_MEDIA_TYPE_TV.getContentMediaTypeCode(),
					ContentMediaTypeEnum.TMDB_MEDIA_TYPE_MOVIE.getContentMediaTypeCode(),
					ContentMediaTypeEnum.TMDB_MEDIA_TYPE_PERSON.getContentMediaTypeCode(),
					ContentMediaTypeEnum.ANILIST_MEDIA_TYPE_ANIME.getContentMediaTypeCode(),
					ContentMediaTypeEnum.ANILIST_MEDIA_TYPE_MANGA.getContentMediaTypeCode(),
					ContentMediaTypeEnum.MEDIA_TYPE_PERSON.getContentMediaTypeCode(),
					ContentMediaTypeEnum.MEDIA_TYPE_COMICS.getContentMediaTypeCode())
					)
            .map(ContentMediaTypeEnum::getContentMediaTypeCode)
            .toList();

	/** TV 응답 맵 (미디어 타입별 결과 추출 함수 매핑) */
	private static final Map<String, Function<SearchTvResponseDto, List<SearchTvResultsDto>>> TV_RESPOSE_MAP =
			Map.of(
					ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode(), SearchTvResponseDto::getAniResults,
					ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode(), SearchTvResponseDto::getDramaResults,
					ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode(), SearchTvResponseDto::getDocumentaryResults,
					ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode(), SearchTvResponseDto::getKidsResults,
					ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode(), SearchTvResponseDto::getNewsResults,
					ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode(), SearchTvResponseDto::getVarietyResults
					);
	
	/**
	 * 검색 결과에 위시리스트 여부 설정
	 * 
	 * @param serchVideoResponse 검색 결과 DTO
	 * @param userId             유저 ID
	 */
	@Override
	public void setWishlistFromVideoResponse(SearchVideoResponseDto serchVideoResponse, Long userId) {
		
		// 모든 컨텐츠 미디어 타입에 대해 처리
		for (String contentMediaTypeCode : VIDEO_CONTENT_MEDIA_TYPE_LIST) {
			// 미디어 타입이 영화인 경우
			if (StringUtils.equals(contentMediaTypeCode,
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
				// TV 검색 결과에서 미디어 타입별 결과 추출
				List<String> contentMediaTypeList;
				Function<SearchTvResponseDto, List<SearchTvResultsDto>> tvResultsFunction = TV_RESPOSE_MAP.get(contentMediaTypeCode);
				// 해당 미디어 타입에 대한 결과 추출 함수가 없는 경우
				if (tvResultsFunction == null) {
					// 경고 로그 출력 후 종료
					Object[] params = {contentMediaTypeCode};
					log.warn(messageUtil.getMessageKO(
							DomainMessagesWarnEnum.WARN_SEARCH_WRONG_CONTENT_MEDIA_TYPE.getMessageCode(), params));
					return;
				}
				// 미디어 타입별 TV 결과 추출
				List<SearchTvResultsDto> tvResults = tvResultsFunction.apply(serchVideoResponse);
				// 애니인 경우 모든 애니 컨텐츠 미디어 타입 리스트 생성
				if (StringUtils.equals(contentMediaTypeCode,
						ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode())) {
					contentMediaTypeList = serchVideoResponse.getAniResults().stream()
							.map(dto -> dto.getContentMediaType())
							.distinct()
							.toList();
				} 
				// 그 외 미디어 타입인 경우 단일 컨텐츠 미디어 타입 리스트 생성
				else {
					contentMediaTypeList = List.of(contentMediaTypeCode);
				}

				// 결과가 존재하는 경우
				if (!CollectionUtils.isEmpty(tvResults)) {
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
		
		// TV 검색 결과에서 미디어 타입별 결과 추출
		Function<SearchTvResponseDto, List<SearchTvResultsDto>> tvResultsFunction = 
				TV_RESPOSE_MAP.get(contentMediaType);
		// 해당 미디어 타입에 대한 결과 추출 함수가 없는 경우
		if (tvResultsFunction == null) {
			// 경고 로그 출력 후 종료
			Object[] params = {contentMediaType};
			log.warn(messageUtil.getMessageKO(
					DomainMessagesWarnEnum.WARN_SEARCH_WRONG_CONTENT_MEDIA_TYPE.getMessageCode(), params));
			return;
		}
		// 미디어 타입별 TV 결과 추출
		List<SearchTvResultsDto> tvResults = tvResultsFunction.apply(searchTvResponse);
		List<String> contentMediaTypeList = List.of(contentMediaType);
		// 결과가 존재하는 경우
		if (!CollectionUtils.isEmpty(tvResults)) {
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
