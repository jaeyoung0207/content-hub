package com.cjy.contenthub.detail.information.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbMovieDetailsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbTvDetailsDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailComicsResponseDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailMovieResponseDto;
import com.cjy.contenthub.detail.information.controller.dto.DetailTvResponseDto;

/**
 * 상세 정보 관련 데이터 매핑을 위한 MapStruct 매퍼 인터페이스
 * MapStruct를 사용하여 빌드시 자동으로 구현체가 생성됨
 * 상세 페이지의 코멘트와 미디어 정보를 DTO 간에 변환하는 메서드를 정의
 * 각 메서드는 매핑 규칙을 정의하며, 특정 필드를 무시하거나 날짜 형식을 지정하는 등의 작업을 수행함
 * 각 DTO 간의 변환을 통해 각 컨트롤러 계층, 서비스 계층, 리포지토리 계층 간의 데이터 전송을 용이하게 함
 */
@Mapper(componentModel = "spring")
public interface DetailInformationMapper {
	
	/**
	 * TmdbDetailsTvDto를 DetailTvResponseDto로 변환
	 * 
	 * @param apiResponse TMDB 상세 TV API 응답 DTO 
	 */
	@Mapping(target = "link", ignore = true)
	@Mapping(target = "starRatingAverage", ignore = true)
	@Mapping(target = "credits", source = "aggregateCredits")
	DetailTvResponseDto detailTvToDetailTvResponse(TmdbTvDetailsDto apiResponse);
	
	/**
	 * TmdbDetailsMovieDto를 DetailMovieResponseDto로 변환
	 * 
	 * @param apiResponse TMDB 상세 영화 API 응답 DTO
	 */
	@Mapping(target = "link", ignore = true)
	@Mapping(target = "starRatingAverage", ignore = true)
	DetailMovieResponseDto detailMovieToDetailMovieResponse(TmdbMovieDetailsDto apiResponse);
	
	/**
	 * DetailMovieResponseDto를 깊은 복사하여 새로운 인스턴스를 생성
	 * 
	 * @param source 원본 DetailMovieResponseDto 객체
	 * @return 새로운 DetailMovieResponseDto 객체
	 */
	DetailTvResponseDto deepCopyForTvResponse(DetailTvResponseDto source);
	
	/**
	 * DetailMovieResponseDto를 깊은 복사하여 새로운 인스턴스를 생성
	 * 
	 * @param source 원본 DetailMovieResponseDto 객체
	 * @return 새로운 DetailMovieResponseDto 객체
	 */
	DetailMovieResponseDto deepCopyForMovieResponse(DetailMovieResponseDto source);
	
	/**
	 * DetailComicsResponseDto를 깊은 복사하여 새로운 인스턴스를 생성
	 * 
	 * @param source 원본 DetailComicsResponseDto 객체
	 * @return 새로운 DetailComicsResponseDto 객체
	 */
	DetailComicsResponseDto deepCopyForComicsResponse(DetailComicsResponseDto source);
	
}
