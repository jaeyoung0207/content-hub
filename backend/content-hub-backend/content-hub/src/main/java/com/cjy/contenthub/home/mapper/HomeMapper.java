package com.cjy.contenthub.home.mapper;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.cjy.contenthub.home.controller.dto.HomeRankingListResponseDto;
import com.cjy.contenthub.home.repository.entity.HomeRankingViewEntity;
import com.cjy.contenthub.home.service.dto.HomeRankingListServiceDto;
import com.cjy.contenthub.home.service.dto.HomeRankingServiceDto;

/**
 * 홈 화면 관련 데이터 매핑을 위한 MapStruct 매퍼 인터페이스
 * MapStruct를 사용하여 빌드시 자동으로 구현체가 생성됨
 */
@Mapper(componentModel = "spring")
public interface HomeMapper {
	
	/**
	 * ContentViewEntity를 HomeServiceDto로 변환
	 * 
	 * @param entity 콘텐츠 뷰 엔티티
	 */
	@Named("entityToService")
	@Mapping(target = "wishlisted", ignore = true)
	HomeRankingServiceDto entityToService(HomeRankingViewEntity entity);
	
	/**
	 * ContentViewEntity 리스트를 HomeServiceDto 리스트로 변환
	 * 
	 * @param entity 콘텐츠 뷰 엔티티 리스트
	 */
	@IterableMapping(qualifiedByName = "entityToService")
	List<HomeRankingServiceDto> entityListToServiceList(List<HomeRankingViewEntity> entity);
	
	/**
	 * HomeRankingListServiceDto를 HomeRankingListResponseDto로 변환
	 * 
	 * @param serviceDto 홈 랭킹 리스트 서비스 DTO
	 */
	HomeRankingListResponseDto serviceListToResponseList(HomeRankingListServiceDto serviceDto);

}
