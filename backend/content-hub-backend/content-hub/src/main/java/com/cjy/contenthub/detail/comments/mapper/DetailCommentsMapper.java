package com.cjy.contenthub.detail.comments.mapper;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.cjy.contenthub.detail.comments.controller.dto.DetailCommentsGetDataDto;
import com.cjy.contenthub.detail.comments.controller.dto.DetailCommentsSaveRequestDto;
import com.cjy.contenthub.detail.comments.controller.dto.DetailCommentsUpdateRequestDto;
import com.cjy.contenthub.detail.comments.repository.entity.DetailCommentsEntity;
import com.cjy.contenthub.detail.comments.repository.entity.DetailCommentsViewEntity;
import com.cjy.contenthub.detail.comments.service.dto.DetailCommentsDataServiceDto;

/**
 * 상세 코멘트 관련 데이터 매핑을 위한 MapStruct 매퍼 인터페이스
 * MapStruct를 사용하여 빌드시 자동으로 구현체가 생성됨
 * 상세 페이지의 코멘트와 미디어 정보를 DTO 간에 변환하는 메서드를 정의
 * 각 메서드는 매핑 규칙을 정의하며, 특정 필드를 무시하거나 날짜 형식을 지정하는 등의 작업을 수행함
 * 각 DTO 간의 변환을 통해 각 컨트롤러 계층, 서비스 계층, 리포지토리 계층 간의 데이터 전송을 용이하게 함
 */
@Mapper(componentModel = "spring")
public interface DetailCommentsMapper {
	
	/**
	 * DetailCommentSaveRequestDto를 DetailCommentDataServiceDto로 변환
	 * 
	 * @param requestDto 상세 코멘트 저장 요청 DTO
	 */
	@Mapping(target = "createTime", ignore = true)
	@Mapping(target = "commentId", ignore = true)
	DetailCommentsDataServiceDto commentsSaveReqToCommentsService(DetailCommentsSaveRequestDto requestDto);
	
	/**
	 * DetailCommentUpdateRequestDto를 DetailCommentDataServiceDto로 변환
	 * 
	 * @param requestDto 상세 코멘트 업데이트 요청 DTO
	 */
	@Mapping(target = "createTime", ignore = true)
	@Mapping(target = "genreIds", ignore = true)
	@Mapping(target = "title", ignore = true)
	@Mapping(target = "thumbnailImageUrl", ignore = true)
	@Mapping(target = "provider", ignore = true)
	DetailCommentsDataServiceDto commentUpdateReqToCommentsService(DetailCommentsUpdateRequestDto requestDto);
	
	/**
	 * DetailCommentDataServiceDto를 DetailCommentsEntity로 변환
	 * 
	 * @param serviceDto 상세 코멘트 서비스 DTO
	 */
	@Mapping(target = "content", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "updateTime", ignore = true)
	DetailCommentsEntity commentsServiceToCommentsEntity(DetailCommentsDataServiceDto serviceDto);
	
	/**
	 * DetailCommentViewEntity를 DetailCommentDataServiceDto로 변환
	 * 
	 * @param entity 상세 코멘트 엔티티
	 */
	@Mapping(source = "createTime", target = "createTime", dateFormat = "uuuu/MM/dd hh:mm:ss")
	@Named("commentsEntityToCommentService")
	@Mapping(target = "genreIds", ignore = true)
	@Mapping(target = "title", ignore = true)
	@Mapping(target = "thumbnailImageUrl", ignore = true)
	@Mapping(target = "provider", ignore = true)
	@Mapping(target = "good", ignore = true)
	@Mapping(target = "bad", ignore = true)
	DetailCommentsDataServiceDto commentsEntityToCommentService(DetailCommentsViewEntity entity);
	
	/**
	 * List<DetailCommentViewEntity>를 List<DetailCommentDataServiceDto>로 변환
	 * 
	 * @param entityList 상세 코멘트 엔티티 리스트
	 */
	@IterableMapping(qualifiedByName = "commentsEntityToCommentService")
	List<DetailCommentsDataServiceDto> commentEntityListToCommentsServiceList(List<DetailCommentsViewEntity> entityList);
	
	/**
	 * DetailCommentDataServiceDto를 DetailCommentGetDataDto로 변환
	 * 
	 * @param entity 상세 코멘트 서비스 DTO
	 */
	@Named("commentsServiceToCommentGetData")
	DetailCommentsGetDataDto commentsServiceToCommentGetData(DetailCommentsDataServiceDto entity);
	
	/**
	 * List<DetailCommentDataServiceDto>를 List<DetailCommentGetDataDto>로 변환
	 * 
	 * @param entityList 상세 코멘트 서비스 DTO 리스트
	 */
	@IterableMapping(qualifiedByName = "commentsServiceToCommentGetData")
	List<DetailCommentsGetDataDto> commentsServiceDtoListToCommentsGetResponseDtoList(List<DetailCommentsDataServiceDto> entityList);

}
