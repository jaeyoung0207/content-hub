package com.cjy.contenthub.wishlist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cjy.contenthub.common.repository.entity.ContentEntity;
import com.cjy.contenthub.wishlist.repository.entity.WishlistEntity;


/**
 * WishlistEntity 엔티티에 대한 CRUD 작업을 수행하는 레포지토리 인터페이스
 * Spring Data JPA의 JJpaRepository를 상속받아 기본적인 CRUD 메소드를 제공
 */
public interface WishlistRepository extends JpaRepository<WishlistEntity, Long> {

	/**
	 * userId와 contentId에 해당하는 WishlistEntity 목록을 조회
	 * 
	 * @param userId    사용자 ID
	 * @param contentId 콘텐츠 ID
	 * @return WishlistEntity 목록
	 */
	List<WishlistEntity> findByUser_UserIdAndContent_ContentId(Long userId, Long contentId);

	/**
	 * 위시리스트에 등록된 userId에 해당하는 ContentEntity 목록을 조회
	 * 
	 * @param userId 유저 테이블 ID
	 * @return WishlistEntity 목록
	 */
	@Query(value = "SELECT con.* " + "FROM \"content\".\"wishlist\" wish "
			+ "INNER JOIN content.content con ON wish.content_id = con.content_id "
			+ "WHERE wish.user_id = :userId "
			+ "ORDER BY con.original_media_type, wish.create_time DESC",
			nativeQuery = true)
	List<ContentEntity> getWishlistByUserId(Long userId);

	/**
	 * 특정 유저가 위시리스트에 등록한 ContentEntity 목록을 조회
	 * 
	 * @param userId       유저 테이블 ID
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiIdList         API ID 목록
	 * @return ContentEntity 목록
	 */
	@Query(value = "SELECT con.* " 
			+ "FROM \"content\".\"wishlist\" wish "
			+ "INNER JOIN content.content con ON wish.content_id = con.content_id "
			+ "WHERE wish.user_id = :userId "
			+ "AND con.original_media_type IN ( :originalMediaTypeList )"
			+ "AND con.api_id IN ( :apiIdList )",
			nativeQuery = true
			)
	List<ContentEntity> getWishlistedContent(@Param("userId") Long userId, 
			@Param("originalMediaTypeList") List<String> originalMediaTypeList, @Param("apiIdList") List<String> apiIdList);

	/**
	 * 특정 유저가 특정 콘텐츠를 위시리스트에 등록했는지 여부를 조회
	 * 
	 * @param userId            사용자 ID
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId             API ID
	 * @return WishlistEntity 목록
	 */
	@Query(value = "SELECT wish.*"
			+ " FROM \"content\".\"wishlist\" wish "
			+ " INNER JOIN content.content con ON wish.content_id = con.content_id "
			+ " WHERE wish.user_id = :userId "
			+ " AND con.original_media_type = :originalMediaType "
			+ " AND con.api_id = :apiId ",
			nativeQuery = true)
	List<WishlistEntity> getRegisteredWishlist(@Param("userId") Long userId, @Param("originalMediaType") String originalMediaType, @Param("apiId") String apiId);

}
