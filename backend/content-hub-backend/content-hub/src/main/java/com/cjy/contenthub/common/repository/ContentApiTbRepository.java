package com.cjy.contenthub.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cjy.contenthub.common.repository.entity.ContentApiTbEntity;

/**
 * ContentApiTbEntity 엔티티에 대한 CRUD 작업을 수행하는 레포지토리 인터페이스
 * Spring Data JPA의 JpaRepository를 상속받아 기본적인 CRUD 메소드를 제공
 */
@Repository
public interface ContentApiTbRepository extends JpaRepository<ContentApiTbEntity, String> {

}
