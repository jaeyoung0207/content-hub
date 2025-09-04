package com.cjy.contenthub.home.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cjy.contenthub.home.repository.entity.HomeRankingViewEntity;

/**
 * HomeRankingViewEntity에 대한 CRUD 작업을 수행하는 인터페이스
 * JpaRepository를 상속받아 기본적인 CRUD 메소드를 사용가능
 * 추가적인 커스텀 메소드가 필요하다면 여기에 정의
 */
@Repository
public interface HomeRankingViewRepository extends JpaRepository<HomeRankingViewEntity, Long> {

}
