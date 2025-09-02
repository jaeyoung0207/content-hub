package com.cjy.contenthub.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cjy.contenthub.common.repository.entity.ContentEntity;

/**
 * ContentEntity 엔티티에 대한 CRUD 작업을 수행하는 레포지토리 인터페이스
 * Spring Data JPA의JpaRepository를 상속받아 기본적인 CRUD 메소드를 제공
 */
public interface ContentRepository extends JpaRepository<ContentEntity, Long> {
	
	/**
	 * originalMediaType과 apiId에 해당하는 ContentEntity를 조회
	 * 
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId             API ID
	 * @return ContentEntity
	 */
	ContentEntity findByOriginalMediaTypeAndApiId(String originalMediaType, String apiId);

	/**
	 * originalMediaType과 apiId에 해당하는 ContentEntity가 존재하는지 확인
	 * 
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId             API ID
	 * @return 존재 여부
	 */
	boolean existsByOriginalMediaTypeAndApiId(String originalMediaType, String apiId);

}
