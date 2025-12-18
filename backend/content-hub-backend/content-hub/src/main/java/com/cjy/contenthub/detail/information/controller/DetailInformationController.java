package com.cjy.contenthub.detail.information.controller;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cjy.contenthub.common.annotation.ApiController;
import com.cjy.contenthub.common.annotation.MaskingTarget;
import com.cjy.contenthub.common.integration.anilist.dto.AniListCharactersDto;
import com.cjy.contenthub.common.integration.anilist.dto.AniListStaffDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailComicsResponseDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailMovieResponseDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailTvResponseDto;
import com.cjy.contenthub.detail.information.mapper.DetailInformationMapper;
import com.cjy.contenthub.detail.information.service.DetailInformationNoCacheService;
import com.cjy.contenthub.detail.information.service.DetailInformationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 상세 화면 기본 정보 API 컨트롤러 클래스
 */
@Tag(name = "detail-information-api", description = "Detail Information APIs")
@ApiController
@RequestMapping("/detail/information")
@RequiredArgsConstructor
public class DetailInformationController {

	/** 상세 정보 서비스 */
	private final DetailInformationService informationService;
	
	/** 상세 정보 서비스(캐시 미사용) */
	private final DetailInformationNoCacheService detailInformationNoCacheService;
	
	/** 상세 정보 매퍼 */
	private final DetailInformationMapper detailInformationMapper;
	
	/** 리퀘스트 파라미터 키 : TV SERIES ID */
	private static final String PARAM_TV_SERIES_ID = "series_id";

	/** 리퀘스트 파라미터 키 : MOVIE ID */
	private static final String PARAM_MOVIE_ID = "movie_id";

	/** 리퀘스트 파라미터 키 : COMICS ID */
	private static final String PARAM_COMICS_ID = "comics_id";

	/** 리퀘스트 파라미터 키 : 페이지 번호 */
	private static final String PARAM_PAGE = "page";
	
	/** 리퀘스트 파라미터 키 : 컨텐츠 미디어 타입 */
	private static final String PARAM_CONTENT_MEDIA_TYPE = "content_media_type";
	
	/** 리퀘스트 파라미터 키 : 유저 ID */
	private static final String PARAM_USER_ID = "user_id";

	/**
	 * TMDB TV 상세 조회 API
	 * 
	 * @param seriesId TV 시리즈 ID
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param userId 유저 테이블 ID
	 * @return TV 상세 응답 DTO
	 */
	@Operation(summary = "TV 상세 조회")
	@GetMapping(value = "/getTvDetail")
	public CompletableFuture<ResponseEntity<DetailTvResponseDto>> getTvDetail(
			@RequestParam(PARAM_TV_SERIES_ID) Integer seriesId,
			@RequestParam(PARAM_CONTENT_MEDIA_TYPE) String contentMediaType,
			@RequestParam(value = PARAM_USER_ID, required = false) @MaskingTarget Long userId
			) {
		
		// TV 상세 정보 조회
		CompletableFuture<DetailTvResponseDto> cachedResponse = informationService.getTvDetail(seriesId, contentMediaType)
				// 비동기적으로 작업 실행
				.thenApplyAsync(response -> {
					// 깊은 복사 수행
					DetailTvResponseDto newResponse = detailInformationMapper.deepCopyForTvResponse(response);

					// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
					if (userId != null) {
						detailInformationNoCacheService.setWishlistFromResponse(DetailTvResponseDto::setWishlisted,
								newResponse, dto -> String.valueOf(dto.getId()), contentMediaType, userId);
					}
					return newResponse;
				});
		// 응답 반환
		return cachedResponse.thenApply(ResponseEntity::ok);
	}

	/**
	 * TMDB 영화 상세 조회 API
	 * 
	 * @param movieId 영화 ID
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param userId 유저 테이블 ID
	 * @return 영화 상세 응답 DTO
	 */
	@Operation(summary = "영화 상세 조회")
	@GetMapping(value = "/getMovieDetail")
	public CompletableFuture<ResponseEntity<DetailMovieResponseDto>> getMovieDetail(
			@RequestParam(PARAM_MOVIE_ID) Integer movieId,
			@RequestParam(PARAM_CONTENT_MEDIA_TYPE) String contentMediaType,
			@RequestParam(value = PARAM_USER_ID, required = false) @MaskingTarget Long userId
			) {
		
		// 영화 상세 정보 조회
		CompletableFuture<DetailMovieResponseDto> response = informationService.getMovieDetail(movieId, contentMediaType)
				// 비동기적으로 작업 실행
				.thenApplyAsync(cachedResponse -> {
					// 깊은 복사 수행
					DetailMovieResponseDto newResponse = detailInformationMapper.deepCopyForMovieResponse(cachedResponse);

					// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
					if (userId != null) {
						detailInformationNoCacheService.setWishlistFromResponse(DetailMovieResponseDto::setWishlisted,
								newResponse, dto -> String.valueOf(dto.getId()), contentMediaType, userId);
					}
					return newResponse;
				});
		// 응답 반환
		return response.thenApply(ResponseEntity::ok);
	}

	/**
	 * AniList Comics 상세 조회 API
	 * 
	 * @param comicsId Comics ID
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param userId 유저 테이블 ID
	 * @return Comics 상세 응답 DTO
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@Operation(summary = "만화 상세 조회")
	@GetMapping(value = "/getComicsDetail")
	public CompletableFuture<ResponseEntity<DetailComicsResponseDto>> getComicsDetail(
			@RequestParam(PARAM_COMICS_ID) Integer comicsId,
			@RequestParam(PARAM_CONTENT_MEDIA_TYPE) String contentMediaType,
			@RequestParam(value = PARAM_USER_ID, required = false) @MaskingTarget Long userId
			) throws IOException {
		
		// 만화 상세 정보 조회
		CompletableFuture<DetailComicsResponseDto> response = informationService.getComicsDetail(comicsId, contentMediaType)
				// 비동기적으로 작업 실행
				.thenApplyAsync(cachedResponse -> {
					// 깊은 복사 수행
					DetailComicsResponseDto newResponse = detailInformationMapper.deepCopyForComicsResponse(cachedResponse);
					
					// 로그인 유저 정보가 존재할 경우 위시리스트 여부 설정
					if (userId != null) {
						detailInformationNoCacheService.setWishlistFromResponse(DetailComicsResponseDto::setWishlisted, newResponse, dto -> String.valueOf(dto.getId()), contentMediaType, userId);
					}
					return newResponse;
				});
		// 응답 반환
		return response.thenApply(ResponseEntity::ok);
	}
	
	/**
	 * AniList Comics 캐릭터 리스트 조회 API
	 * 
	 * @param comicsId Comics ID
	 * @param page     페이지 번호
	 * @return Comics 캐릭터 리스트 응답 DTO
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@Operation(summary = "캐릭터 리스트 조회")
	@GetMapping(value = "/getComicsCharacterList")
	public CompletableFuture<ResponseEntity<AniListCharactersDto>> getComicsCharacterList(
			@RequestParam(PARAM_COMICS_ID) Integer comicsId,
			@RequestParam(PARAM_PAGE) Integer page
			) throws IOException {
		return informationService.getComicsCharacterList(comicsId, page)
				.thenApply(ResponseEntity::ok);
	}
	
	/**
	 * AniList Comics 스태프 리스트 조회 API
	 * 
	 * @param comicsId Comics ID
	 * @param page     페이지 번호
	 * @return Comics 스태프 리스트 응답 DTO
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@Operation(summary = "스태프 리스트 조회")
	@GetMapping(value = "/getComicsStaffList")
	public CompletableFuture<ResponseEntity<AniListStaffDto>> getComicsStaffList(
			@RequestParam(PARAM_COMICS_ID) Integer comicsId,
			@RequestParam(PARAM_PAGE) Integer page
			) throws IOException {
		return informationService.getComicsStaffList(comicsId, page)
				.thenApply(ResponseEntity::ok);
	}
}
