package com.cjy.contenthub.detail.mapper;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbDetailsMovieDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbDetailsTvDto;
import com.cjy.contenthub.detail.controller.dto.DetailCommentGetDataDto;
import com.cjy.contenthub.detail.controller.dto.DetailCommentSaveRequestDto;
import com.cjy.contenthub.detail.controller.dto.DetailCommentUpdateRequestDto;
import com.cjy.contenthub.detail.controller.dto.DetailMovieResponseDto;
import com.cjy.contenthub.detail.controller.dto.DetailTvResponseDto;
import com.cjy.contenthub.detail.repository.entity.DetailCommentEntity;
import com.cjy.contenthub.detail.repository.entity.DetailCommentViewEntity;
import com.cjy.contenthub.detail.service.dto.DetailCommentDataServiceDto;

/**
 * 상세 페이지 관련 데이터 매핑을 위한 MapStruct 매퍼 인터페이스
 * MapStruct를 사용하여 빌드시 자동으로 구현체가 생성됨
 * 상세 페이지의 코멘트와 미디어 정보를 DTO 간에 변환하는 메서드를 정의
 * 각 메서드는 매핑 규칙을 정의하며, 특정 필드를 무시하거나 날짜 형식을 지정하는 등의 작업을 수행함
 * 각 DTO 간의 변환을 통해 각 컨트롤러 계층, 서비스 계층, 리포지토리 계층 간의 데이터 전송을 용이하게 함
 */
@Mapper(componentModel = "spring")
public interface DetailMapper {
	
	/**
	 * DetailCommentSaveRequestDto를 DetailCommentDataServiceDto로 변환
	 * 
	 * @param requestDto 상세 코멘트 저장 요청 DTO
	 */
	@Mapping(target = "createTime", ignore = true)
	DetailCommentDataServiceDto commentSaveReqToCommentService(DetailCommentSaveRequestDto requestDto);
	
	/**
	 * DetailCommentUpdateRequestDto를 DetailCommentDataServiceDto로 변환
	 * 
	 * @param requestDto 상세 코멘트 업데이트 요청 DTO
	 */
	@Mapping(target = "createTime", ignore = true)
	DetailCommentDataServiceDto commentUpdateReqToCommentService(DetailCommentUpdateRequestDto requestDto);
	
	/**
	 * DetailCommentDataServiceDto를 DetailCommentEntity로 변환
	 * 
	 * @param serviceDto 상세 코멘트 서비스 DTO
	 */
	DetailCommentEntity commentServiceToCommentEntity(DetailCommentDataServiceDto serviceDto);
	
	/**
	 * DetailCommentViewEntity를 DetailCommentDataServiceDto로 변환
	 * 
	 * @param entity 상세 코멘트 엔티티
	 */
	@Mapping(source = "createTime", target = "createTime", dateFormat = "uuuu/MM/dd hh:mm:ss")
	@Named("commentEntityToCommentService")
	DetailCommentDataServiceDto commentEntityToCommentService(DetailCommentViewEntity entity);
	
	/**
	 * List<DetailCommentViewEntity>를 List<DetailCommentDataServiceDto>로 변환
	 * 
	 * @param entityList 상세 코멘트 엔티티 리스트
	 */
	@IterableMapping(qualifiedByName = "commentEntityToCommentService")
	List<DetailCommentDataServiceDto> commentEntityListToCommentServiceList(List<DetailCommentViewEntity> entityList);
	
	/**
	 * DetailCommentDataServiceDto를 DetailCommentGetDataDto로 변환
	 * 
	 * @param entity 상세 코멘트 서비스 DTO
	 */
	@Named("commentServiceToCommentGetData")
	DetailCommentGetDataDto commentServiceToCommentGetData(DetailCommentDataServiceDto entity);
	
	/**
	 * List<DetailCommentDataServiceDto>를 List<DetailCommentGetDataDto>로 변환
	 * 
	 * @param entityList 상세 코멘트 서비스 DTO 리스트
	 */
	@IterableMapping(qualifiedByName = "commentServiceToCommentGetData")
	List<DetailCommentGetDataDto> commentServiceDtoListToCommentGetResponseDtoList(List<DetailCommentDataServiceDto> entityList);
	
	/**
	 * TmdbDetailsTvDto를 DetailTvResponseDto로 변환
	 * 
	 * @param apiResponse TMDB 상세 TV API 응답 DTO 
	 */
	@Mapping(target = "link", ignore = true)
	@Mapping(target = "starRatingAverage", ignore = true)
	DetailTvResponseDto detailTvToDetailTvResponse(TmdbDetailsTvDto apiResponse);
	
	/**
	 * TmdbDetailsMovieDto를 DetailMovieResponseDto로 변환
	 * 
	 * @param apiResponse TMDB 상세 영화 API 응답 DTO
	 */
	@Mapping(target = "link", ignore = true)
	@Mapping(target = "starRatingAverage", ignore = true)
	DetailMovieResponseDto detailMovieToDetailMovieResponse(TmdbDetailsMovieDto apiResponse);

}
