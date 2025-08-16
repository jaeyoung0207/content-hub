package com.cjy.contenthub.detail.repository.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * 상세 코멘트 엔티티 클래스 
 * 상세 페이지에서 유저 코멘트 정보를 저장하는 엔티티 클래스
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
		uniqueConstraints = @UniqueConstraint(name = "comment_unique" ,columnNames = {"originalMediaType", "apiId", "commentNo"}),
		indexes = {@Index(name = "idx_original_media_type_api_id_create_time", columnList = "originalMediaType, apiId, createTime")}
		)
public class DetailCommentEntity implements Serializable {
	
	/** 직렬화 ID */
	private static final long serialVersionUID = 1L;
	
	/** 코멘트 번호 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_no")
	private Long commentNo;
	
	/** 원본 미디어 타입 */
	@NotNull
	@Column(name = "original_media_type", length = 1)
	private String originalMediaType;
	
	/** API ID */
	@NotNull
	@Column(name = "api_id")
	private String apiId;
	
	/** user 테이블 시퀀스 */
	@NotNull
	@Column(name = "user_seq")
	private int userSeq;
	
	/** 별점 */
	@Column(name = "star_rating", precision = 2, scale = 1)
	private BigDecimal starRating;
	
	/** 코멘트 */
	@NotNull
	@Column(name = "comment")
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
	 * 유저 시퀀스 설정
	 * 
	 * @param seq 유저 시퀀스
	 */
	public void setUserSeq(int seq) {
		this.userSeq = seq;
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
