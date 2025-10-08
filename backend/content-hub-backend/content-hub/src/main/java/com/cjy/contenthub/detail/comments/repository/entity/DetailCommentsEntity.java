package com.cjy.contenthub.detail.comments.repository.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상세 코멘트 엔티티 클래스 
 * JPA를 사용하여 ORM 매핑을 수행하며, 데이터베이스의 comment 테이블에 매핑됨
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자는 protected로 설정하여 외부에서 직접 생성하지 못하도록 함
@AllArgsConstructor
@Table(
		schema = CommonConstants.SCHEMA_NAME_CONTENT,
		name = "comment",
		indexes = {@Index(name = "idx_content_id", columnList = "content_id"), @Index(name = "idx_user_id", columnList = "user_id"),
				@Index(name = "idx_create_time", columnList = "create_time"), @Index(name = "idx_star_rating", columnList = "star_rating")}
		)
public class DetailCommentsEntity implements Serializable {

	/** 직렬화 ID */
	private static final long serialVersionUID = 1L;

	/** 코멘트 번호 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long commentId;

	/** content 테이블 ID */
	@NotNull
	@ManyToOne(targetEntity = ContentEntity.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id", referencedColumnName = "content_id")
	private ContentEntity content;

	/** user 테이블 ID */
	@NotNull
	@ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private UserEntity user;

	/** 별점 */
	@Column(name = "star_rating", precision = 2, scale = 1)
	private BigDecimal starRating;

	/** 코멘트 */
	@NotNull
	@Column(name = "comment", length = 500)
	private String comment;

	/** 추천 수 */
	@Column(name = "good")
	private Long good;

	/** 비추천 수 */
	@Column(name = "bad")
	private Long bad;

	/** 작성 시간 */
	@NotNull
	@Column(name = "create_time")
	private LocalDateTime createTime;

	/** 갱신 시간 */
	@NotNull
	@Column(name = "update_time")
	private LocalDateTime updateTime;

	/**
	 * 유저 엔티티를 설정(내부적으로 user_id에 설정)
	 * 
	 * @param seq 유저 ID
	 */
	public void setUserEntity(UserEntity user) {
		this.user = user;
	}

	/**
	 * 콘텐츠 엔티티를 설정(내부적으로 content_id에 설정)
	 * 
	 * @param id 콘텐츠 ID
	 */
	public void setContentEntity(ContentEntity content) {
		this.content = content;
	}

	/**
	 * 코멘트 및 별점 설정
	 * 
	 * @param comment
	 * @param starRating
	 */
	public void setCommentAndStarRating(String comment, BigDecimal starRating) {
		this.comment = comment;
		this.starRating = starRating;
	}
	
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
