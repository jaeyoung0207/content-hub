package com.cjy.contenthub.my.comments.mapper;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.cjy.contenthub.my.comments.controller.dto.MyCommentsResponseDto;
import com.cjy.contenthub.my.comments.repository.dto.MyCommentsDto;
import com.cjy.contenthub.my.comments.service.dto.MyCommentsServiceDataDto;
import com.cjy.contenthub.my.comments.service.dto.MyCommentsServiceDto;

/**
 * MyCommentMapper 인터페이스
 */
@Mapper(componentModel = "spring")
public interface MyCommentsMapper {

	/**
	 * MyCommentDto를 MyCommentServiceDto로 변환
	 * 
	 * @param dto MyCommentDto 객체
	 * @return MyCommentServiceDto 객체
	 */
	@Named("repositoryDtoToServiceDto")
	@Mapping(target = "createTimeStr", source = "createTime", dateFormat = "yyyy/MM/dd HH:mm:ss")
	MyCommentsServiceDataDto repositoryToService(MyCommentsDto dto);
	
	/**
	 * MyCommentDto 리스트를 MyCommentServiceDto 리스트로 변환
	 * 
	 * @param dtoList MyCommentDto 리스트
	 * @return MyCommentServiceDto 리스트
	 */
	@IterableMapping(qualifiedByName = "repositoryDtoToServiceDto")
	List<MyCommentsServiceDataDto> repositoryListToServiceList(List<MyCommentsDto> dtoList);
	
	/**
	 * MyCommentServiceDataDto를 MyCommentDataResponseDto로 변환
	 * 
	 * @param dto MyCommentServiceDataDto 객체
	 * @return MyCommentDataResponseDto 객체
	 */
	MyCommentsResponseDto serviceToResponse(MyCommentsServiceDto dto);
	
}
