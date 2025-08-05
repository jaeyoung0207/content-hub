package com.cjy.contenthub.detail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cjy.contenthub.detail.repository.entity.DetailCommentEntity;

/**
 * DetailCommentEntity에 대한 CRUD 작업을 수행하는 인터페이스
 * JpaRepository를 상속받아 기본적인 CRUD 메소드를 사용가능
 * 추가적인 커스텀 메소드가 필요하다면 여기에 정의 가능
 */
@Repository
public interface DetailCommentRepository extends JpaRepository<DetailCommentEntity, Long> {
	
}
