package com.cjy.contenthub.common.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * GraphQL 유틸리티 클래스
 * GraphQL 쿼리를 로드하고 요청 본문을 빌드하는 메소드를 포함
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GraphqlUtil {
	
	/**
	 * 지정된 파일 이름에 해당하는 GraphQL 쿼리를 클래스패스에서 로드하여 문자열로 반환
	 * 
	 * @param fileName GraphQL 쿼리 파일 이름
	 * @return 로드된 GraphQL 쿼리 문자열
	 * @throws IOException 파일을 읽는 중 발생할 수 있는 예외
	 */
	public static String loadQuery(String fileName) throws IOException {
		// 클래스패스에서 GraphQL 쿼리 파일을 읽어 문자열로 반환
		ClassPathResource classPathResource = new ClassPathResource("graphql/" + fileName);
		// 파일의 내용을 UTF-8 인코딩으로 읽어 문자열로 변환하여 반환
		return new String(classPathResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
	}
	
	/**
	 * GraphQL 쿼리와 파라미터 맵을 이용하여 요청 본문을 JSON 문자열로 빌드
	 * 
	 * @param query GraphQL 쿼리 문자열
	 * @param parameterMap 파라미터 맵
	 * @return JSON 형식의 요청 본문 문자열
	 * @throws JsonProcessingException JSON 변환 중 발생할 수 있는 예외
	 */
	public static String buildRequestBody(String query, Map<String, Object> parameterMap) throws JsonProcessingException {
		// Jackson ObjectMapper를 이용하여 java Object -> json 형식의 문자열로 변환
		// ObjectMapper를 사용하여 Map을 JSON 문자열로 변환하여 반환
		ObjectMapper objectMapper = new ObjectMapper();
		// 쿼리와 파라미터 맵을 포함하는 맵 생성
		Map<String, Object> queryMap = new HashMap<>();
		// 쿼리와 파라미터 맵을 queryMap에 추가
		queryMap.put("query", query);
		queryMap.put("variables", parameterMap);
		// ObjectMapper를 사용하여 queryMap을 JSON 문자열로 변환하여 반환
		return objectMapper.writeValueAsString(queryMap);
	}
}
