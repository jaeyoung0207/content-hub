package com.cjy.contenthub.wishlist.repository.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.repository.entity.ContentEntity;
import com.cjy.contenthub.common.repository.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * 위시리스트 엔티티 클래스
 * JPA를 사용하여 ORM 매핑을 수행하며, 데이터베이스의 wishlist 테이블에 매핑됨
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
		schema = CommonConstants.SCHEMA_NAME_CONTENT, 
		name = "wishlist",
		uniqueConstraints = @UniqueConstraint(name = "wishlist_unique", columnNames = {"user_id", "content_id"}),
		indexes = {
				@Index(name = "idx_wishlist_user_id", columnList = "user_id"), 
				@Index(name = "idx_user_id_content_id", columnList = "user_id, content_id")
		})
public class WishlistEntity implements Serializable {

	/** 직렬화 ID */
	private static final long serialVersionUID = 1L;

	/** 위시리스트 ID */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "wishlist_id")
	private Long wishlistId;

	/** user 테이블 ID */
	@NotNull
	@ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private UserEntity user;

	/** content 테이블 ID */
	@NotNull
	@ManyToOne(targetEntity = ContentEntity.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id", referencedColumnName = "content_id")
	private ContentEntity content;

	/** 작성 시간 */
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
