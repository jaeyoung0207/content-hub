package com.cjy.contenthub.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 공통 상수 정의 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstants {

    /** 인증 토큰 접두어 */
    public static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";
    
    /** 인증 헤더 이름 */
    public static final String AUTHORIZATION_HEADER = "Authorization";
    
    /** 리프레시 토큰 */
    public static final String REFRESH_TOKEN = "refreshToken";
    
    /** 로그인 제공자 */
    public static final String PROVIDER = "provider";
    
    /** 디바이스 ID */
    public static final String DEVICE_ID = "deviceId";
    
    /** COMMA */
    public static final String COMMA = ",";
    
    /** COLON */
    public static final String COLON = ":";
    
    /** HYPEN */
    public static final String HYPEN = "-";
    
    /** SLASH */
    public static final String SLASH = "/";
    
    /** csrf token 헤더 */
    public static final String CSRF_TOKEN_HEADER = "X-XSRF-TOKEN";
    
	/** 위시리스트 경로 */
	public static final String WISHLIST_PATH = "/wishlist";
	
    /** 날짜 형식: yyyyMMddHHmmss */
    public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    
    /** 날짜 형식 정규식: yyyy-MM-dd */
    public static final String STR_DATE_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";
	
	/** 1KB */
	public static final int ONE_KB = 1024;
	
	/** 1MB */
	public static final int ONE_MB = ONE_KB * 1024;
	
	/** 60초 */
	public static final int SIXTY_SECONDS = 60;
	
}
