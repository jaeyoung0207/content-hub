package com.cjy.contenthub.detail.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cjy.contenthub.common.api.dto.aniist.AniListCharactersDto;
import com.cjy.contenthub.common.api.dto.aniist.AniListStaffDto;
import com.cjy.contenthub.detail.controller.dto.DetailComicsResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailMovieResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailTvResponseDto;
import com.cjy.contenthub.detail.service.DetailInformationService;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 상세 화면 기본 정보 API 컨트롤러 클래스
 */
@RestController
@RequestMapping("/detail/information")
@RequiredArgsConstructor
@Slf4j
public class DetailInformationController {

	/** 상세 정보 서비스 */
	private final DetailInformationService informationService;
	
	/** 리퀘스트 파라미터 키 : TV SERIES ID */
	private static final String PARAM_TV_SERIES_ID = "series_id";

	/** 리퀘스트 파라미터 키 : MOVIE ID */
	private static final String PARAM_MOVIE_ID = "movie_id";

	/** 리퀘스트 파라미터 키 : COMICS ID */
	private static final String PARAM_COMICS_ID = "comics_id";

	/** 리퀘스트 파라미터 키 : 페이지 번호 */
	private static final String PARAM_PAGE = "page";

	/**
	 * TMDB TV 상세 조회 API
	 * 
	 * @param seriesId TV 시리즈 ID
	 * @param originalMediaType 원본 미디어 타입
	 * @return TV 상세 응답 DTO
	 */
	@GetMapping(value = "/getTvDetail")
	public ResponseEntity<DetailTvResponseDto> getTvDetail(
			@NotNull @RequestParam(PARAM_TV_SERIES_ID) Integer seriesId
			) {
		return ResponseEntity.ok(informationService.getTvDetail(seriesId));
	}

	/**
	 * TMDB 영화 상세 조회 API
	 * 
	 * @param movieId 영화 ID
	 * @param originalMediaType 원본 미디어 타입
	 * @return ResponseEntity<DetailMovieResponseDto> 영화 상세 응답 DTO
	 */
	@GetMapping(value = "/getMovieDetail")
	public ResponseEntity<DetailMovieResponseDto> getMovieDetail(
			@NotNull @RequestParam(PARAM_MOVIE_ID) Integer movieId
			) {
		return ResponseEntity.ok(informationService.getMovieDetail(movieId));
	}

	/**
	 * AniList Comics 상세 조회 API
	 * 
	 * @param comicsId Comics ID
	 * @param page 페이지 번호
	 * @return Comics 상세 응답 DTO
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@GetMapping(value = "/getComicsDetail")
	public ResponseEntity<DetailComicsResponseDto> getComicsDetail(
			@NotNull @RequestParam(PARAM_COMICS_ID) Integer comicsId
			) throws IOException {
		return ResponseEntity.ok(informationService.getComicsDetail(comicsId));
	}
	
	/**
	 * AniList Comics 캐릭터 리스트 조회 API
	 * 
	 * @param comicsId Comics ID
	 * @param page     페이지 번호
	 * @return Comics 캐릭터 리스트 응답 DTO
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@GetMapping(value = "/getComicsCharacterList")
	public ResponseEntity<AniListCharactersDto> getComicsCharacterList(
			@NotNull @RequestParam(PARAM_COMICS_ID) Integer comicsId,
			@NotNull @RequestParam(PARAM_PAGE) Integer page
			) throws IOException {
		return ResponseEntity.ok(informationService.getComicsCharacterList(comicsId, page));
	}
	
	/**
	 * AniList Comics 스태프 리스트 조회 API
	 * 
	 * @param comicsId Comics ID
	 * @param page     페이지 번호
	 * @return Comics 스태프 리스트 응답 DTO
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	@GetMapping(value = "/getComicsStaffList")
	public ResponseEntity<AniListStaffDto> getComicsStaffList(
			@NotNull @RequestParam(PARAM_COMICS_ID) Integer comicsId,
			@NotNull @RequestParam(PARAM_PAGE) Integer page
			) throws IOException {
		return ResponseEntity.ok(informationService.getComicsStaffList(comicsId, page));
	}
}
