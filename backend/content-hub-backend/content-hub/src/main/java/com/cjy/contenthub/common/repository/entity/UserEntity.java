package com.cjy.contenthub.common.repository.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.cjy.contenthub.common.constants.CommonConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 유저 정보를 저장하는 엔티티 클래스
 * JPA를 사용하여 ORM 매핑을 수행하며, 데이터베이스의 user 테이블에 매핑됨
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자는 protected로 설정하여 외부에서 직접 생성하지 못하도록 함
@AllArgsConstructor
@Table(
		schema = CommonConstants.SCHEMA_NAME_CONTENT, // 스키마 이름 
		name = "user", // 테이블 이름
		uniqueConstraints = @UniqueConstraint(name = "user_unique" ,columnNames = {"provider", "providerId"}), // provider와 providerId의 조합이 유일해야 함
		indexes = {@Index(name = "idx_provider_provider_id", columnList = "provider, providerId")} // provider와 providerId에 대한 인덱스 생성
		)
public class UserEntity implements Serializable {

	/** 직렬화 ID */
	private static final long serialVersionUID = 1L;

	/** 시퀀스 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int seq;

	/** 로그인 제공자 */
	@NotNull
	@Column(name = "provider", length = 50)
	private String provider;

	/** 로그인 제공자가 발급한 ID */
	@NotNull
	@Column(name = "provider_id")
	private String providerId;

	/** 닉네임 */
	@NotNull
	@Column(name = "nickname", length = 50)
	private String nickname;

	/** email */
	@Column(name = "email")
	private String email;
	
	/** 상태 */
	@Column(name = "status")
	private String status;

	/** 생성 시간 */
	@NotNull
	@Column(name = "create_time")
	private LocalDateTime createTime;

	/** 갱신 시간 */
	@NotNull
	@Column(name = "update_time")
	private LocalDateTime updateTime;

	/**
	 * Entitiy가 저장되기 전에 실행되는 메소드
	 */
	@PrePersist
	public void prePersist() {
		this.createTime = LocalDateTime.now();
		this.updateTime = LocalDateTime.now();
	}

	/**
	 * Entitiy가 갱신되기 전에 실행되는 메소드
	 */
	@PreUpdate
	public void preUpdate() {
		this.updateTime = LocalDateTime.now();
	}

}
