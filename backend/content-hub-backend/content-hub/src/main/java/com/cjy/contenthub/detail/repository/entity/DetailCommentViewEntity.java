package com.cjy.contenthub.detail.repository.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상세 코멘트 뷰 엔티티 클래스
 * 상세 페이지에서 유저 코멘트 정보를 조회하기 위한 뷰 엔티티 클래스
 * JPA를 사용하여 ORM 매핑을 수행하며, 데이터베이스의 comment 테이블과 user 테이블을 조인하여 조회
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자는 protected로 설정하여 외부에서 직접 생성하지 못하도록 함
@AllArgsConstructor
@Immutable // 뷰 엔티티로, 데이터베이스에서 수정되지 않음을 나타냄
@Subselect(
		"SELECT "
		+ "c.comment_no, c.original_media_type, c.api_id, u.provider_id as user_id, u.nickname, c.star_rating, c.comment, c.create_time "
		+ "FROM content.comment c INNER JOIN content.user u ON c.user_seq = u.seq"
		)
public class DetailCommentViewEntity implements Serializable {
	
	/** 직렬화 ID */
	private static final long serialVersionUID = 1L;
	
	/** 코멘트 번호 */
	@Id
	@Column(name = "comment_no")
	private Long commentNo;
	
	/** 원본 미디어 타입 */
	@Column(name = "original_media_type", length = 1)
	private String originalMediaType;
	
	/** API ID */
	@Column(name = "api_id")
	private String apiId;
	
	/** 유저 ID */
	@Column(name = "user_id")
	private String userId;
	
	/** 닉네임 */
	@Column(name = "nickname", length = 100)
	private String nickname;
	
	/** 별점 */
	@Column(name = "star_rating", precision = 2, scale = 1)
	private BigDecimal starRating;
	
	/** 코멘트 */
	@Column(name = "comment")
	private String comment;
	
	/** 생성 시간 */
	@Column(name = "create_time")
	private LocalDateTime createTime;
	
}
