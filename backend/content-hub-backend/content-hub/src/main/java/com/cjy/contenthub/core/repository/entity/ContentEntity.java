package com.cjy.contenthub.core.repository.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.cjy.contenthub.core.constants.DomainConstants;

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
 * 콘텐츠 엔티티 클래스
 * JPA를 사용하여 ORM 매핑을 수행하며, 데이터베이스의 content 테이블에 매핑됨
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
		schema = DomainConstants.SCHEMA_NAME_CONTENT, 
		name = "content",
		uniqueConstraints = @UniqueConstraint(name = "content_unique", columnNames = {"content_media_type", "api_id"}),
		indexes = {
				@Index(name = "idx_content_media_type_api_id", columnList = "content_media_type, api_id"), 
				@Index(name = "idx_api_id", columnList = "api_id")
		})
public class ContentEntity implements Serializable {

	/** 직렬화 ID */
	private static final long serialVersionUID = 1L;

	/** 콘텐츠 ID */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "content_id")
	private Long contentId;

	/** 컨텐츠 미디어 타입 */
	@NotNull
	@Column(name = "content_media_type")
	private String contentMediaType;

	/** 미디어 타입(화면 표시용) */
	@NotNull
	@Column(name = "display_media_type")
	private String displayMediaType;

	/** API ID */
	@NotNull
	@Column(name = "api_id")
	private String apiId;	

	/** 제목 */
	@NotNull
	@Column(name = "title", length = 500)
	private String title;

	/** 썸네일 이미지 URL */
	@Column(name = "thumbnail_image_url")
	private String thumbnailImageUrl;

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
