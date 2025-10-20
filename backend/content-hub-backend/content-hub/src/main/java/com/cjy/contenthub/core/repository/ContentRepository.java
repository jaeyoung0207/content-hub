package com.cjy.contenthub.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cjy.contenthub.core.repository.entity.ContentEntity;

/**
 * ContentEntity 엔티티에 대한 CRUD 작업을 수행하는 레포지토리 인터페이스 Spring Data JPA의
 * JpaRepository를 상속받아 기본적인 CRUD 메소드를 제공
 */
public interface ContentRepository extends JpaRepository<ContentEntity, Long> {

	/**
	 * contentMediaType과 apiId에 해당하는 ContentEntity를 조회
	 * 
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param apiId            API ID
	 * @return ContentEntity
	 */
	ContentEntity findByContentMediaTypeAndApiId(String contentMediaType, String apiId);
	
	/**
	 * apiId에 해당하는 ContentEntity 리스트를 조회
	 * 
	 * @param apiId API ID
	 * @return ContentEntity 리스트
	 */
	List<ContentEntity> findByApiId(String apiId);

	/**
	 * contentMediaType과 apiId에 해당하는 ContentEntity가 존재하는지 확인
	 * 
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param apiId            API ID
	 * @return 존재 여부
	 */
	boolean existsByContentMediaTypeAndApiId(String contentMediaType, String apiId);

	/**
	 * contentMediaType과 apiId 리스트에 해당하는 ContentEntity 리스트를 조회
	 * 
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param apiIdList        API ID 리스트
	 * @return ContentEntity 리스트
	 */
	List<ContentEntity> findByContentMediaTypeAndApiIdIn(String contentMediaType, List<String> apiIdList);

}
