package com.cjy.contenthub.common.service;

/**
 * 공통 서비스 인터페이스
 */
public interface CommonService {
	
	/**
	 * API 이름에 해당하는 API 키를 반환
	 *
	 * @param apiName API 이름
	 * @return API 키
	 */
	String getApiKey(String apiName);

}
