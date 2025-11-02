package com.cjy.contenthub.common.constants;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 공통 enum 정의 클래스
 */
public class CommonEnum {

	/**
	 * JWT Validate 결과값 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum JwtValidateResultEnum {
		
		/** 유효한 JWT 토큰 */
		VALID_TOKEN("0", "Success"),
		/** 만료된 JWT 토큰 */
		EXPIRED_TOKEN("1", "Expired JWT Token"),
		/** 유효하지 않은 JWT 토큰 */
		INVALID_TOKEN("2", "Invalid JWT Token");
		
		/** JWT 검증 결과 코드 */
		private String jwtValidateResultCode;
		
		/** JWT 검증 결과 메시지 */
		private String jwtValidateResultMsg;
		
		/**
		 * 결과 코드로부터 JwtValidateResultEnum을 반환
		 * 
		 * @param resultCode 결과 코드
		 * @return JwtValidateResultEnum
		 */
		public static JwtValidateResultEnum getJwtValidateResult(String resultCode) {
			return Arrays.stream(values())
					.filter(e -> e.jwtValidateResultCode.equals(resultCode)) // 결과 코드가 일치하는지 확인
					.findFirst() // 첫 번째 일치하는 결과 코드를 찾음
					.orElse(INVALID_TOKEN); // 기본값으로 INVALID_TOKEN 반환
		}
	}
	
	/**
	 * 디버그 메시지 코드 정의 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum CommonMessagesDebugEnum {
		
		/** 공통 - 세션 생성 */
		DEBUG_COMMON_CREATE_SESSION("debug.common.createSession"),
		/** 공통 - 기존 세션 존재 */
		DEBUG_COMMON_EXISTING_SESSION("debug.common.existingSession"),
		/** 공통 - 세션 삭제 */
		DEBUG_COMMON_DELETE_SESSION("debug.common.deleteSession"),
		/** 공통 - API Rate Limit 체크 */
		DEBUG_COMMON_API_RATE_LIMIT_CHECK("debug.common.apiRateLimitCheck");
		
		/** 메시지 코드 */
		private String messageCode;
	}
	
	/**
	 * 경고 메시지 코드 정의 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum CommonMessagesWarnEnum {
		
		/** 공통 - API Rate Limit 초과 */
		WARN_COMMON_API_RATE_LIMIT_EXCEEDED("warn.common.apiRateLimitExceeded"),
		/** 공통 - 세션 없음 */
		WARN_COMMON_SESSION_NOT_FOUND("warn.common.sessionNotFound"),
		/** 공통 - TMDB 장르명 없음 */
		WARN_COMMON_TMDB_GENRE_NAME_NOTFOUND("warn.common.tmdbGenreNameNotFound");
		
		/** 메시지 코드 */
		private String messageCode;
	}
	
	/**
	 * 에러 메시지 코드 정의 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum CommonMessagesErrorEnum {
		
		/** 공통 - API Rate Limit 초과 */
		ERROR_COMMON_API_RATE_LIMIT_EXCEEDED("error.common.apiRateLimitExceeded"),
		/** 공통 - DeepL API 에러 */
		ERROR_COMMON_DEEPL("error.common.deepl"),
		/** 공통 - DeepL API 에러 상세 */
		ERROR_COMMON_DEEPL_DETAIL("error.common.deeplDetail"),
		/** 공통 - JWT 파싱 에러 */
		ERROR_COMMON_JWT_PARSING("error.common.jwtParsing"),
		/** 공통 - JWT 만료 */
		ERROR_COMMON_JWT_EXPIRED("error.common.jwtExpired"),
		/** 공통 - JWT 만료 상세 */
		ERROR_COMMON_JWT_EXPIRED_DETAIL("error.common.jwtExpiredDetail"),
		/** 공통 - JWT 유효하지 않음 */
		ERROR_COMMON_JWT_INVALID("error.common.jwtInvalid"),
		/** 공통 - JWT 유효하지 않음 상세 */
		ERROR_COMMON_JWT_INVALID_DETAIL("error.common.jwtInvalidDetail"),
		/** 공통 - JWT 생성 에러 */
		ERROR_COMMON_JWT_CREATION("error.common.jwtCreation"),
		/** 공통 - JWT 검증 에러 */
		ERROR_COMMON_JWT_VALIDATION("error.common.jwtValidation"),
		/** 공통 - JWT 리프레시 토큰 검증 에러 */
		ERROR_COMMON_JWT_REFRESH_TOKEN_VALIDATION("error.common.jwtRefreshTokenValidation"),
		/** 공통 - 점검중 */
		ERROR_COMMON_MAINTENANCE("error.common.maintenance"),
		/** 공통 - 점검중 상세 */
		ERROR_COMMON_MAINTENANCE_DETAIL("error.common.maintenanceDetail"),
		/** 공통 - Redis INCREMENT 에러 */
		ERROR_COMMON_REDIS_INCREMENT("error.common.redisIncrement"),
		/** 공통 - 콘텐츠 없음 */
		ERROR_COMMON_CONTENT_NOT_FOUND("error.common.contentNotFound"),
		/** 공통 - 컨트롤러 어드바이스 에러 1 */
		ERROR_COMMON_CONTROLLER_ADVICE_1("error.common.controllerAdvice1"),
		/** 공통 - 컨트롤러 어드바이스 에러 2 (body 포함) */
		ERROR_COMMON_CONTROLLER_ADVICE_2("error.common.controllerAdvice2"),
		/** 공통 - 해시키 변환 에러 */
		ERROR_COMMON_CONVERT_HASHKEY("error.common.convertHashKey");
		
		/** 메시지 코드 */
		private String messageCode;
	}

}
