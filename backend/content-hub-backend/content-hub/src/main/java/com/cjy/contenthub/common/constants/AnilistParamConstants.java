package com.cjy.contenthub.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * AniList API 파라미터 상수 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnilistParamConstants {
	
	/** 리퀘스트 파라미터 키 : 캐릭터ID */
	public static final String PARAM_CHARACTER_ID = "characterId";
	
	/** 리퀘스트 파라미터 키 : 스태프ID */
	public static final String PARAM_STAFF_ID = "staffId";
	
	/** 리퀘스트 파라미터 키 : 페이지 번호 */
	public static final String PARAM_PAGE = "page";

	/** 리퀘스트 파라미터 키 : 페이지당 표시 건수 */
	public static final String PARAM_PER_PAGE = "perPage";
	
	/** 리퀘스트 파라미터 키 : 정렬 기준 */
	public static final String PARAM_SORT = "sort";

	/** 리퀘스트 파라미터 키 : 페이지 번호 (Staff) */
	public static final String PARAM_STAFF_PAGE = "staffPage2";

	/** 리퀘스트 파라미터 키 : 페이지당 표시 건수 (Staff) */
	public static final String PARAM_STAFF_PERPAGE = "staffPerPage2";
	
	/** 리퀘스트 파라미터 키 : 정렬 기준 (Staff) */
	public static final String PARAM_STAFF_SORT = "staffSort2";

	/** 리퀘스트 파라미터 키 : 미디어 ID */
	public static final String PARAM_MEDIA_ID = "mediaId";
	
	/** 리퀘스트 파라미터 키 : 검색 */
	public static final String PARAM_SEARCH = "search";
	
	/** 리퀘스트 파라미터 키 : 성인물 포함 여부 */
	public static final String PARAM_IS_ADULT = "isAdult";

}
