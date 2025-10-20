package com.cjy.contenthub.wishlist.mapper;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import com.cjy.contenthub.core.repository.entity.ContentEntity;
import com.cjy.contenthub.wishlist.controller.dto.WishlistCheckResultResponseDto;
import com.cjy.contenthub.wishlist.controller.dto.WishlistListResponseDto;
import com.cjy.contenthub.wishlist.controller.dto.WishlistRequestDto;
import com.cjy.contenthub.wishlist.controller.dto.WishlistResponseDto;
import com.cjy.contenthub.wishlist.service.dto.WishlistCheckResultServiceDto;
import com.cjy.contenthub.wishlist.service.dto.WishlistListServiceDto;
import com.cjy.contenthub.wishlist.service.dto.WishlistServiceDto;

/**
 * 위시리스트 MapStruct 매퍼 인터페이스
 * MapStruct를 사용하여 빌드시 자동으로 구현체가 생성됨
 */
@Mapper(componentModel = "spring")
public interface WishlistMapper {
	
	/**
	 * WishlistRequestDto를 WishlistServiceDto로 변환
	 * 
	 * @param requestDto
	 * @return WishlistSaveServiceDto
	 */
	WishlistServiceDto requestToService(WishlistRequestDto requestDto);
	
	/**
	 * ContentEntity를 WishlistServiceDto로 변환
	 * 
	 * @param contentEntity
	 * @return WishlistServiceDto
	 */
	@Named("contentToService")
	WishlistServiceDto contentToService(ContentEntity contentEntity);
	
	/**
	 * ContentEntity 리스트를 WishlistServiceDto 리스트로 변환
	 * 
	 * @param contentEntityList
	 * @return WishlistServiceDto 리스트
	 */
	@IterableMapping(qualifiedByName = "contentToService")
	List<WishlistServiceDto> contentListToServiceList(List<ContentEntity> contentEntityList);
	
	/**
	 * WishlistServiceDto를 WishlistResponseDto로 변환
	 * 
	 * @param serviceDto
	 * @return WishlistResponseDto
	 */
	@Named("serviceToResponse")
	WishlistResponseDto serviceToResponse(WishlistServiceDto serviceDto);
	
	/**
	 * WishlistServiceDto 리스트를 WishlistResponseDto 리스트로 변환
	 * 
	 * @param serviceDtoList
	 * @return WishlistResponseDto 리스트
	 */
	@IterableMapping(qualifiedByName = "serviceToResponse")
	List<WishlistResponseDto> serviceListToResponseList(List<WishlistServiceDto> serviceDtoList);
	
	/**
	 * WishlistListServiceDto를 WishlistListResponseDto로 변환
	 * 
	 * @param listServiceDto
	 * @return WishlistListResponseDto
	 */
	WishlistListResponseDto listServiceToListResponse(WishlistListServiceDto listServiceDto);
	
	/**
	 * WishlistCheckResultServiceDto를 WishlistCheckResultResponseDto로 변환
	 * 
	 * @param serviceDto
	 * @return WishlistCheckResultResponseDto
	 */
	WishlistCheckResultResponseDto serviceToCheckResultResponse(WishlistCheckResultServiceDto serviceDto);

}
