package com.cjy.contenthub.common.config;

import org.mapstruct.MapperConfig;

/**
 * 공통 MapStruct 설정 클래스
 * 컴포넌트 모델을 'spring'으로 설정하여 스프링 컨텍스트에서 MapStruct 매퍼를 사용할 수 있도록 함
 * 이 설정을 통해 MapStruct 매퍼가 스프링 빈으로 등록되어 의존성 주입이 가능해짐
 */
@MapperConfig(componentModel = "spring")
public interface CommonMapStruct {

}
