package com.cjy.contenthub.detail.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cjy.contenthub.detail.repository.entity.DetailCommentViewEntity;

/**
 * DetailCommentViewEntity에 대한 CRUD 작업을 수행하는 인터페이스
 * JpaRepository를 상속받아 기본적인 CRUD 메소드를 사용가능
 * 추가적인 커스텀 메소드가 필요하다면 여기에 정의
 */
@Repository
public interface DetailCommentViewRepository extends JpaRepository<DetailCommentViewEntity, Long> {

	/**
	 * 코멘트 뷰 엔티티 타입 Page 조회
	 * 
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId API ID
	 * @param pageable 페이지 정보
	 * @return 상세 코멘트 페이지
	 */
	Page<DetailCommentViewEntity> findByOriginalMediaTypeAndApiId(String originalMediaType, String apiId, Pageable pageable);

	/**
	 * 코멘트 뷰 엔티티 조회
	 * 
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId API ID
	 * @param userId 유저 ID
	 * @return 상세 코멘트 뷰 엔티티
	 */
	DetailCommentViewEntity findByOriginalMediaTypeAndApiIdAndUserId(String originalMediaType, String apiId, String userId);

	/**
	 * 별점 평균 조회
	 * native SQL 쿼리를 사용하여 데이터베이스에서 직접 계산
	 * 
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId API ID
	 * @return 별점 평균
	 */
	@Query(value = "SELECT ROUND(AVG(comment.star_rating),1) AS star_rating_average "
			+ "FROM \"content\".\"comment\" comment "
			+ "WHERE comment.original_media_type = :originalMediaType AND comment.api_id = :apiId",
			nativeQuery = true
			)
	BigDecimal getStarRatingAverage(@Param("originalMediaType") String originalMediaType, @Param("apiId") String apiId);

}
