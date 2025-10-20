package com.cjy.contenthub.wishlist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cjy.contenthub.core.repository.entity.ContentEntity;
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
	 * 특정 유저가 위시리스트에 등록한 콘텐츠 수를 조회
	 * 
	 * @param userId 유저 테이블 ID
	 * @return 콘텐츠 수
	 */
	long countByUser_UserId(Long userId);
	
	/**
	 * 위시리스트에 등록된 userId와 apiId, contentMediaTypeList에 해당하는 ContentEntity 목록을 조회
	 * 
	 * @param userId 유저 테이블 ID
	 * @param apiId  API ID
	 * @return ContentEntity 목록
	 */
	@Query(value = "SELECT con.* "
			+ "FROM \"content\".\"content\" con "
			+ "INNER JOIN \"content\".\"wishlist\" wish ON con.content_id = wish.content_id "
			+ "WHERE wish.user_id = :userId "
			+ "AND con.api_id = :apiId "
			+ "AND con.content_media_type IN ( :contentMediaTypeList ) "
			+ "ORDER BY con.content_media_type"
			, nativeQuery = true)
	List<ContentEntity> getContentListByUserIdAndApiIdAndContentMediaTypeIn(@Param("userId") Long userId, @Param("apiId") String apiId, 
			@Param("contentMediaTypeList") List<String> contentMediaTypeList); 
	
	/**
	 * 위시리스트에 등록된 userId에 해당하는 ContentEntity 목록을 조회
	 * 
	 * @param userId 유저 테이블 ID
	 * @return WishlistEntity 목록
	 */
	@Query(value = "SELECT con.* " 
			+ "FROM \"content\".\"content\" con "
			+ "INNER JOIN \"content\".\"wishlist\" wish ON con.content_id = wish.content_id "
			+ "WHERE wish.user_id = :userId "
			+ "ORDER BY con.content_media_type, wish.create_time DESC",
			nativeQuery = true)
	List<ContentEntity> getContentListByUserId(Long userId);

	/**
	 * 특정 유저가 위시리스트에 등록한 ContentEntity 목록을 조회
	 * 
	 * @param userId       유저 테이블 ID
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param apiIdList         API ID 목록
	 * @return ContentEntity 목록
	 */
	@Query(value = "SELECT con.* " 
			+ "FROM \"content\".\"content\" con "
			+ "INNER JOIN \"content\".\"wishlist\" wish ON con.content_id = wish.content_id "
			+ "WHERE wish.user_id = :userId "
			+ "AND con.content_media_type IN ( :contentMediaTypeList )"
			+ "AND con.api_id IN ( :apiIdList )",
			nativeQuery = true
			)
	List<ContentEntity> getContentListByUserIdAndContentMediaTypeInAndApiIdIn(@Param("userId") Long userId, 
			@Param("contentMediaTypeList") List<String> contentMediaTypeList, @Param("apiIdList") List<String> apiIdList);

	/**
	 * 특정 유저가 특정 콘텐츠를 위시리스트에 등록했는지 여부를 조회
	 * 
	 * @param userId            사용자 ID
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param apiId             API ID
	 * @return WishlistEntity 목록
	 */
	@Query(value = "SELECT wish.*"
			+ " FROM \"content\".\"wishlist\" wish "
			+ " INNER JOIN \"content\".\"content\" con ON wish.content_id = con.content_id "
			+ " WHERE wish.user_id = :userId "
			+ " AND con.content_media_type = :contentMediaType "
			+ " AND con.api_id = :apiId ",
			nativeQuery = true)
	List<WishlistEntity> getWishlistListByUserIdAndContentMediaTypeAndApiId(@Param("userId") Long userId, @Param("contentMediaType") String contentMediaType, @Param("apiId") String apiId);

}
