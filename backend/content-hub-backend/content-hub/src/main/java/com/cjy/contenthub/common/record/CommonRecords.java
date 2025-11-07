package com.cjy.contenthub.common.record;

/**
 * 공통 레코드 정의 클래스
 */
public class CommonRecords {
	
	/** 
	 * 로그인 쿠키 정보 레코드
	 * @param provider 로그인 제공자
	 * @param refreshToken 리프레시 토큰
	 */
	public record LoginCookiesRecord(String provider, String refreshToken) {}

}
