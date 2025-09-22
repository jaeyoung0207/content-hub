package com.cjy.contenthub.home.repository.entity;

import java.io.Serializable;
import java.math.BigDecimal;

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
 * 콘텐츠 뷰 엔티티 클래스
 * 홈 화면 및 기타 페이지에서 콘텐츠 정보를 조회하기 위한 뷰 엔티티 클래스
 * JPA를 사용하여 ORM 매핑을 수행
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Immutable
@Subselect(
		"SELECT * FROM ("
				+ "SELECT ROW_NUMBER() OVER (PARTITION BY con.display_media_type ORDER BY AVG(com.star_rating) DESC, COUNT(com.star_rating) DESC) as row_num, "
				+ "con.content_id, con.content_media_type, con.display_media_type, con.api_id , AVG(com.star_rating) AS star_rating_average, "
				+ "COUNT(com.star_rating) AS star_rating_count, con.title , con.thumbnail_image_url "
				+ "FROM content.comment com "
				+ "INNER JOIN content.content con ON com.content_id = con.content_id "
				+ "GROUP BY con.content_id, con.content_media_type, con.display_media_type, con.api_id, con.title, con.thumbnail_image_url"
				+ ") top "
				+ "WHERE top.row_num <= 10 "
				+ "ORDER BY top.display_media_type, top.star_rating_average DESC, top.star_rating_count DESC"
		)
public class HomeRankingViewEntity implements Serializable {

	/** 직렬화 ID */
	private static final long serialVersionUID = 1L;

	/** 콘텐츠 ID */
	@Id
	@Column(name = "content_id")
	private Long contentId;

	/** 행 번호 */
//	@Id
	@Column(name = "row_num")
	private Long rowNum;
	
	/** 컨텐츠 미디어 타입 */
	@Column(name = "content_media_type")
	private String contentMediaType;
	
	/** 미디어 타입(화면 표시용) */
	@Column(name = "display_media_type")
	private String displayMediaType;

	/** API ID */
	@Column(name = "api_id")
	private String apiId;

	/** 별점 평균 */
	@Column(name = "star_rating_average")
	private BigDecimal starRatingAverage;
	
	/** 별점 평가 개수 */
	@Column(name = "star_rating_count")
	private Long starRatingCount;

	/** 제목 */
	@Column(name = "title")
	private String title;

	/** 썸네일 이미지 URL */
	@Column(name = "thumbnail_image_url")
	private String thumbnailImageUrl;

}
