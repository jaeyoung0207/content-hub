package com.cjy.contenthub.person.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbPersonCreditsCastDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbPersonCreditsCrewDto;
import com.cjy.contenthub.person.controller.dto.PersonCreditsCastDto;
import com.cjy.contenthub.person.controller.dto.PersonCreditsCrewDto;
import com.cjy.contenthub.person.controller.dto.PersonDto;
import com.cjy.contenthub.person.controller.dto.PersonResponseDto;

/**
 * 인물 화면 관련 데이터 매핑을 위한 MapStruct 매퍼 인터페이스
 * MapStruct를 사용하여 빌드시 자동으로 구현체가 생성됨
 * 각 메서드는 매핑 규칙을 정의하며, 특정 필드를 무시하거나 날짜 형식을 지정하는 등의 작업을 수행함
 * 각 DTO 간의 변환을 통해 각 컨트롤러 계층, 서비스 계층, 리포지토리 계층 간의 데이터 전송을 용이하게 함
 */
@Mapper(componentModel = "spring")
public interface PersonMapper {
	
	/**
	 * PersonDetailsDto를 PersonDetailsResponseDto로 변환
	 * 
	 * @param detailsDto 인물 상세 정보 DTO
	 */
	@Mapping(target = "cast", ignore = true)
	@Mapping(target = "crew", ignore = true)
	PersonResponseDto personToPersonResponse(PersonDto detailsDto);
	
	/**
	 * TmdbPersonCreditsCastDto를 PersonDetailsCreditsCastDto로 변환
	 * 
	 * @param castDto TMDB 인물 출연진 정보 DTO
	 */
	PersonCreditsCastDto tmdbPersonCastToPersonCast(TmdbPersonCreditsCastDto castDto);
	
	/**
	 * TmdbPersonCreditsCrewDto를 PersonDetailsCreditsCrewDto로 변환
	 * 
	 * @param castDto TMD 인물 제작진 정보 DTO
	 */
	PersonCreditsCrewDto tmdbPersonCrewToPersonCrew(TmdbPersonCreditsCrewDto castDto);

}
