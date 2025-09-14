package com.cjy.contenthub.detail.recommendation.mapper;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsMovieResultsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbRecommendationsTvResultsDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsComicsResultDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsMovieResultsDto;
import com.cjy.contenthub.detail.recommendation.controller.dto.DetailRecommendationsTvResultsDto;

/**
 * 상세 추천 관련 데이터 매핑을 위한 MapStruct 매퍼 인터페이스
 * MapStruct를 사용하여 빌드시 자동으로 구현체가 생성됨
 * 상세 페이지의 코멘트와 미디어 정보를 DTO 간에 변환하는 메서드를 정의
 * 각 메서드는 매핑 규칙을 정의하며, 특정 필드를 무시하거나 날짜 형식을 지정하는 등의 작업을 수행함
 * 각 DTO 간의 변환을 통해 각 컨트롤러 계층, 서비스 계층, 리포지토리 계층 간의 데이터 전송을 용이하게 함
 */
@Mapper(componentModel = "spring")
public interface DetailRecommendationMapper {
	
	/**
	 * TmdbRecommendationsTvResultsDto를 DetailRecommendationsTvResultsDto로 변환
	 * 
	 * @param apiResponse TMDB 추천 TV API 응답 DTO
	 */
	@Named("tmdbRecommendationsTvToDetailRecommendationsTv")
	DetailRecommendationsTvResultsDto tmdbRecommendationsTvToDetailRecommendationsTv(TmdbRecommendationsTvResultsDto apiResponse);
	
	/**
	 * List<TmdbRecommendationsTvResultsDto>를
	 * List<DetailRecommendationsTvResultsDto>로 변환
	 * 
	 * @param apiResponseList TMDB 추천 TV API 응답 DTO 리스트
	 */
	@IterableMapping(qualifiedByName = "tmdbRecommendationsTvToDetailRecommendationsTv")
	List<DetailRecommendationsTvResultsDto> tmdbRecommendationsTvListToDetailRecommendationsTvList(List<TmdbRecommendationsTvResultsDto> apiResponseList);
	
	/**
	 * TmdbRecommendationsMovieResultsDto를 DetailRecommendationsMovieResultsDto로 변환
	 * 
	 * @param apiResponse TMDB 추천 영화 API 응답 DTO
	 */
	@Named("tmdbRecommendationsMovieToDetailRecommendationsMovie")
	DetailRecommendationsMovieResultsDto tmdbRecommendationsMovieToDetailRecommendationsMovie(TmdbRecommendationsMovieResultsDto apiResponse);
	
	/**
	 * List<TmdbRecommendationsMovieResultsDto>를
	 * List<DetailRecommendationsMovieResultsDto>로 변환
	 * 
	 * @param apiResponseList TMDB 추천 영화 API 응답 DTO 리스트
	 */
	@IterableMapping(qualifiedByName = "tmdbRecommendationsMovieToDetailRecommendationsMovie")
	List<DetailRecommendationsMovieResultsDto> tmdbRecommendationsMovieListToDetailRecommendationsMovieList(List<TmdbRecommendationsMovieResultsDto> apiResponseList);
	
	/**
	 * DetailRecommendationsTvResultsDto를 깊은 복사
	 * 
	 * @param source 복사할 원본 DetailRecommendationsTvResultsDto 객체
	 * @return 복사된 새로운 DetailRecommendationsTvResultsDto 객체
	 */
	@Named("deepCopyForRecommendationsTvResults")
	DetailRecommendationsTvResultsDto deepCopyForRecommendationsTvResults(DetailRecommendationsTvResultsDto source);
	
	/**
	 * List<DetailRecommendationsTvResultsDto>를 깊은 복사
	 * 
	 * @param sourceList 복사할 원본 DetailRecommendationsTvResultsDto 객체 리스트
	 * @return 복사된 새로운 DetailRecommendationsTvResultsDto 객체 리스트
	 */
	@IterableMapping(qualifiedByName = "deepCopyForRecommendationsTvResults")
	List<DetailRecommendationsTvResultsDto> deepCopyForRecommendationsTvResultsList(List<DetailRecommendationsTvResultsDto> sourceList);
	
	/**
	 * DetailRecommendationsMovieResultsDto를 깊은 복사
	 * 
	 * @param source 복사할 원본 DetailRecommendationsMovieResultsDto 객체
	 * @return 복사된 새로운 DetailRecommendationsMovieResultsDto 객체
	 */
	@Named("deepCopyForRecommendationsMovieResults")
	DetailRecommendationsMovieResultsDto deepCopyForRecommendationsMovieResults(DetailRecommendationsMovieResultsDto source);
	
	/**
	 * List<DetailRecommendationsMovieResultsDto>를 깊은 복사
	 * 
	 * @param sourceList 복사할 원본 DetailRecommendationsMovieResultsDto 객체 리스트
	 * @return 복사된 새로운 DetailRecommendationsMovieResultsDto 객체 리스트
	 */
	@IterableMapping(qualifiedByName = "deepCopyForRecommendationsMovieResults")
	List<DetailRecommendationsMovieResultsDto> deepCopyForRecommendationsMovieResultsList(List<DetailRecommendationsMovieResultsDto> sourceList);
	
	
	/**
	 * DetailComicsRecommendationsResultDto를 깊은 복사
	 * 
	 * @param source 복사할 원본 DetailComicsRecommendationsResultDto 객체
	 * @return 복사된 새로운 DetailComicsRecommendationsResultDto 객체
	 */
	@Named("deepCopyForRecommendationsComicsResults")
	DetailRecommendationsComicsResultDto deepCopyForRecommendationsComicsResults(DetailRecommendationsComicsResultDto source);
	
	/**
	 * List<DetailComicsRecommendationsResultDto>를 깊은 복사
	 * 
	 * @param sourceList 복사할 원본 DetailComicsRecommendationsResultDto 객체 리스트
	 * @return 복사된 새로운 DetailComicsRecommendationsResultDto 객체 리스트
	 */
	@IterableMapping(qualifiedByName = "deepCopyForRecommendationsComicsResults")
	List<DetailRecommendationsComicsResultDto> deepCopyForRecommendationsComicsResultsList(List<DetailRecommendationsComicsResultDto> sourceList);

}
