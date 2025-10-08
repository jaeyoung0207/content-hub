package com.cjy.contenthub.my.comments.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cjy.contenthub.detail.comments.repository.entity.DetailCommentsEntity;
import com.cjy.contenthub.my.comments.repository.dto.MyCommentsDto;

/**
 * 나의 코멘트 리포지토리 인터페이스
 * 유저 코멘트와 관련된 데이터베이스 작업을 수행하기 위한 인터페이스
 */
@Repository
public interface MyCommentsRepository extends JpaRepository<DetailCommentsEntity, Long> {

	/**
	 * 유저 ID로 코멘트 조회
	 *  
	 * @param userId
	 * @return
	 */
	@Query(value = "SELECT com.comment_id, con.content_id, con.content_media_type, con.api_id, con.title, "
			+ "con.thumbnail_image_url, com.comment, com.star_rating, com.create_time "
			+ "FROM \"content\".\"comment\" com INNER JOIN content.content con ON com.content_id = con.content_id "
			+ "WHERE com.user_id = :userId",
			countQuery = "SELECT COUNT(*) "
					+ "FROM \"content\".\"comment\" com INNER JOIN content.content con ON com.content_id = con.content_id "
					+ "WHERE com.user_id = :userId",
					nativeQuery = true)
	Page<MyCommentsDto> getCommentByUserId(@Param("userId") Long userId, Pageable pageable);

}
