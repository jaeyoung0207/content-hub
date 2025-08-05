package com.cjy.contenthub.common.repository.entity;

import java.io.Serializable;

import com.cjy.contenthub.common.constants.CommonConstants;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * ContentApiTbEntity 클래스는 API 이름과 aniKey를 저장하는 엔티티 클래스입니다.
 * 이 클래스는 데이터베이스의 api_tb 테이블에 매핑됩니다.
 */
@Entity
@Setter
@Getter
@Table(schema = CommonConstants.SCHEMA_NAME_CONTENT, name = "api_tb")
public class ContentApiTbEntity implements Serializable {

	/** 직렬화 ID */
	private static final long serialVersionUID = 1L;

	/** API 이름 */
	@Id
	private String apiName;
	
	/** API 키 */
	private String apiKey;

}
