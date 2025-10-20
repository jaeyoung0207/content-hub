package com.cjy.contenthub.common.integration.tmdb.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * TMDB API 파라미터 상수 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TmdbParamConstants {
	
	/** 리퀘스트 파라미터 키 : TV SERIES ID */
	public static final String PARAM_TV_SERIES_ID = "series_id";

	/** 리퀘스트 파라미터 키 : MOVIE ID */
	public static final String PARAM_MOVIE_ID = "movie_id";
	
	/** 리퀘스트 파라미터 키 : 인물 ID */
	public static final String PARAM_PERSON_ID = "person_id";
	
	/** 리퀘스트 파라미터 키 : 검색어 */
	public static final String PARAM_QUERY = "query";

	/** 리퀘스트 파라미터 키 : 페이지 번호 */
	public static final String PARAM_PAGE = "page";
	
	/** 리퀘스트 파라미터 키 : 페이지당 표시 건수 */
	public static final String PARAM_PER_PAGE = "perPage";

	/** 리퀘스트 파라미터 키 : 언어 */
	public static final String PARAM_LANGUAGE = "language";
	
	/** 리퀘스트 파라미터 키 : 성인물 포함 여부 */
	public static final String PARAM_INCLUDE_ADULT = "include_adult";
	
	/** 리퀘스트 파라미터 키 : append_to_response */
	public static final String PARAM_APPEND_TO_RESPONSE = "append_to_response";

	/** 크레딧 : credits */
	public static final String CREDITS = "credits";

	/** 종합 크레딧 : aggregate credits */
	public static final String AGGREGATE_CREDITS = "aggregate_credits";
	
	/** 크레딧 : credits */
	public static final String PERSON_CREDITS = "tv_credits,movie_credits";
	
	/** 언어 : 한국어 */
	public static final String LANGUAGE_KOREAN = "ko-KR";
	
	/** 언어 : 영어 */
	public static final String LANGUAGE_ENGLISH = "en-US";

}
