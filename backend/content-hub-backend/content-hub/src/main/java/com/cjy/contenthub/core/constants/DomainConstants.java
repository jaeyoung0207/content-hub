package com.cjy.contenthub.core.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 공통 상수 정의 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DomainConstants {

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
    
    /** 리프레시 토큰 */
    public static final String REFRESH_TOKEN = "refreshToken";
    
    /** 로그인 제공자 */
    public static final String PROVIDER = "provider";
    
    /** 첫번째 페이지 번호 */
	public static final int FIRST_PAGE_NO = 1;
	
}
