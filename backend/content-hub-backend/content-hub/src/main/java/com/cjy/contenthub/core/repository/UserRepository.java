package com.cjy.contenthub.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cjy.contenthub.core.repository.entity.UserEntity;

/**
 * UserEntity 엔티티에 대한 CRUD 작업을 수행하는 레포지토리 인터페이스
 * Spring Data JPA의 JpaRepository를 상속받아 기본적인 CRUD 메소드를 제공
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	
	/**
	 * provider와 providerId에 해당하는 UserEntity를 조회
	 * 
	 * @param provider   로그인 제공자
	 * @param providerId 로그인 ID
	 * @return UserEntity
	 */
	UserEntity findByProviderAndProviderId(String provider, String providerId);
	
	/**
	 * provider와 providerId에 해당하는 UserEntity가 존재하는지 확인
	 * 
	 * @param provider   로그인 제공자
	 * @param providerId 로그인 ID
	 * @return 존재 여부
	 */
	boolean existsByProviderAndProviderId(String provider, String providerId);
	
}
