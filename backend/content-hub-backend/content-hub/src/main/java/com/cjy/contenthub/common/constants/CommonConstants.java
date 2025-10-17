package com.cjy.contenthub.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 공통 상수 정의 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstants {

	/** TV 장르명 */
	public static final String API_TV_GENRE_NAME = "tvGenre";
	
	/** 영화 장르명 */
    public static final String API_MOVIE_GENRE_NAME = "movieGenre";
    
    /** 번역 API 이름 */
    public static final String API_TRANSLATE_NAME = "translate";
    
    /** 번역 API 언어 코드 : 한국어 */
    public static final String API_LANGUAGE_KOREAN = "KO";
    
    /** 번역 API 언어 코드 : 일본어 */
    public static final String API_LANGUAGE_JAPANESE = "JA";
    
    /** 번역 API 언어 코드 : 영어 */
    public static final String API_LANGUAGE_ENGLISH = "EN";
    
    /** DB 스키마명: content */
    public static final String SCHEMA_NAME_CONTENT = "content";
    
    /** 성인물 플래그 */
    public static final String ADULT_FLG = "adult_flg";
    
    /** 인증 토큰 접두어 */
    public static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";
    
    /** 인증 헤더 이름 */
    public static final String AUTHORIZATION_HEADER = "Authorization";
    
    /** 리프레시 토큰 */
    public static final String REFRESH_TOKEN = "refreshToken";
    
    /** 로그인 제공자 */
    public static final String PROVIDER = "provider";
    
    /** 날짜 형식: yyyyMMddHHmmss */
    public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    
    /** COMMA */
    public static final String COMMA = ",";
    
    /** COLON */
    public static final String COLON = ":";
    
    /** HYPEN */
    public static final String HYPEN = "-";
    
    /** SLASH */
    public static final String SLASH = "/";
    
    /** 날짜 형식 정규식: yyyy-MM-dd */
    public static final String STR_DATE_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";
    
    /** csrf token 헤더 */
    public static final String CSRF_TOKEN_HEADER = "X-XSRF-TOKEN";
    
    /** 첫번째 페이지 번호 */
	public static final int FIRST_PAGE_NO = 1;
	
	/** 위시리스트 경로 */
	public static final String WISHLIST_PATH = "/wishlist";
	
	/** 1KB */
	public static final int ONE_KB = 1024;
	
	/** 1MB */
	public static final int ONE_MB = ONE_KB * 1024;
	
	/** 60초 */
	public static final int SIXTY_SECONDS = 60;
	
}
