package com.cjy.contenthub.common.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 공통 enum 정의 클래스
 */
public class CommonEnum {

	/**
	 * TMDB API 장르 정의 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum TmdbGenreEnum {

		/** 애니메이션 */
		GENRE_ANI(16, "Animation", "애니메이션"),
		/** 코메디 */
		GENRE_COMEDY(35, "Comedy", "코메디"),
		/** 드라마 */
		GENRE_DRAMA(18, "Drama", "드라마"),
		/** 범죄 */
		GENRE_CRIME(80, "Crime", "범죄"),
		/** 다큐 */
		GENRE_DOCUMENTARY(99, "Documentary", "다큐"),
		/** 미스테리 */
		GENRE_MYSTERY(9648, "Mystery", "미스테리"),
		/** 가족 */
		GENRE_FAMILY(10751, "Family", "가족"),
		/** 액션&어드벤처 */
		GENRE_ACTION_ADVENTURE(10759, "Action & Adventure", "액션&어드벤처"),
		/** 어린이 */
		GENRE_KIDS(10762, "Kids", "어린이"),
		/** 뉴스 */
		GENRE_NEWS(10763, "News", "뉴스"),
		/** 리얼리티 */
		GENRE_REALITY(10764, "Reality", "리얼리티"),
		/** 공상과학 판타지 */
		GENRE_SCI_FI_FANTASY(10765, "Sci-Fi & Fantasy", "공상과학 판타지"),
		/** 연속극 */
		GENRE_SOAP(10766, "Soap", "연속극"),
		/** 토크쇼 */
		GENRE_TALK(10767, "Talk", "토크쇼"),
		/** 전쟁&정치 */
		GENRE_WAR_POLITICS(10768, "War & Politics", "전쟁&정치"),
		/** 서부극 */
		GENRE_WESTERN(37, "Western", "서부극"),
		/** 액션 */
		GENRE_ACTION(28, "Action", "액션"),
		/** 어드벤처 */
		GENRE_ADVENTURE(12, "Adventure", "어드벤처"),
		/** 판타지 */
		GENRE_FANTASY(14, "Fantasy", "판타지"),
		/** 역사극 */
		GENRE_HISTORY(36, "History", "역사극"),
		/** 호러 */
		GENRE_HORROR(27, "Horror", "호러"),
		/** 음악 */
		GENRE_MUSIC(10402, "Music", "음악"),
		/** 로맨스 */
		GENRE_ROMANCE(10749, "Romance", "로맨스"),
		/** 공상과학 */
		GENRE_SCIENCE_FICTION(878, "Science Fiction", "공상과학"),
		/** TV 영화 */
		GENRE_TV_MOVIE(10770, "TV Movie", "TV 영화"),
		/** 스릴러 */
		GENRE_THRILLER(53, "Thriller", "스릴러"),
		/** 전쟁 */
		GENRE_WAR(10752, "War", "전쟁");

		/** 장르 */
		private int genreId;
		
		/** 장르(영어) */
		private String genreEnglish;

		/** 장르(한국어) */
		private String genreKorean;

		/** 장르 맵 */
		private static final Map<String, String> GENRE_MAP = Stream.of(values()).collect(
				Collectors.toMap(TmdbGenreEnum::getGenreEnglish, TmdbGenreEnum::getGenreKorean, (oldKey,newKey) -> newKey));

		/** 영어 장르명 -> 한글 장르명으로 변경 */
		public static String getTranslatedGenre(String genre) {
			return StringUtils.defaultString(GENRE_MAP.get(genre));
		}
	}

	/**
	 * 공통 미디어 타입 정의 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum CommonMediaTypeEnum {

		/** Ani */
		MEDIA_TYPE_ANI("1", "ani"),
		/** Drama */
		MEDIA_TYPE_DRAMA("2", "drama"),
		/** Movie */
		MEDIA_TYPE_MOVIE("3", "movie"),
		/** Comics */
		MEDIA_TYPE_COMICS("4", "comics"),
		/** TV(TMDB API) */
		TMDB_MEDIA_TYPE_TV("11", "tv"),
		/** Movie(TMDB API) */
		TMDB_MEDIA_TYPE_MOVIE("12", "movie"),
		/** Person(TMDB API) */
		TMDB_MEDIA_TYPE_PERSON("13", "person");

		/** 미디어 타입 코드 */
		private String mediaTypeCode;
		
		/** 미디어 타입 문자열 */
		private String mediaTypeValue;
	}
	
	/**
	 * TMDB API 성별 정의 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum TmdbGenderEnum {
		
		NOT_SPECIFIED(0, "불명"),
		
		FEMALE(1, "여성"),
		
		MALE(2, "남성"),
		
		NON_BINARY(3, "논바이너리");
		
		/** 성별 코드 */
		private int genderCode;
		
		/** 성별 값 */
		private String genderValue;
		
		/**
		 * 성별 코드로부터 TmdbGenderEnum을 반환
		 * 
		 * @param genderCode 성별 코드
		 * @return TmdbGenderEnum
		 */
		public static TmdbGenderEnum getGender(int genderCode) {
			return Arrays.stream(values())
					.filter(e -> e.genderCode == genderCode) // 성별 코드가 일치하는지 확인
					.findFirst() // 첫 번째 일치하는 성별을 찾음
					.orElse(NOT_SPECIFIED); // 기본값으로 NOT_SPECIFIED 반환
		}
		
	}
	
	/**
	 * AniList 미디어 타입 정의 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum AniListMediaTypeEnum {

		/** 애니 */
		MEDIA_TYPE_ANIME("ANIME"),
		/** 만화 */
		MEDIA_TYPE_MANGA("MANGA");
		
		/** 미디어 타입 */
		private String mediaType;
	}

	/**
	 * 캐시 타입 정의 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum CacheTypeEnum {

		/** TV 장르 캐시 */
		API_TV_GENRE(CommonConstants.API_TV_GENRE_NAME),
		/** 영화 장르 캐시 */
		API_MOVIE_GENRE(CommonConstants.API_MOVIE_GENRE_NAME),
		/** 번역 API 캐시 */
		API_TRANSLATE_NAME(CommonConstants.API_TRANSLATE_NAME);

		/** 캐시 이름 */
		private String cacheName;
		
		/** 캐시 만료 시간(초) */
		private int expireAfterWrite;
		
		/** 캐시 최대 크기 */
		private int maximumSize;

		/**
		 * 캐시 타입 생성자
		 * 
		 * @param cacheName 캐시 이름
		 */
		CacheTypeEnum(String cacheName) {
			this.cacheName = cacheName;
			this.expireAfterWrite = EXPIRE_AFTER_WRITE; // 기본 만료 시간 설정
			this.maximumSize = MAXIMUM_SIZE; // 기본 최대 크기 설정
		}

		/** 캐시 만료 시간(분) */
		public static final int EXPIRE_AFTER_WRITE = 60;
		
		/** 캐시 최대 크기 */
		public static final int MAXIMUM_SIZE = 1000;

		/**
		 * 캐시 이름으로부터 CacheTypeEnum을 반환
		 * 
		 * @param name 캐시 이름
		 * @return CacheTypeEnum
		 */
		public static CacheTypeEnum getCacheType(String name) {
			return Arrays.stream(values())
					.filter(e -> e.cacheName.equals(name)) // 캐시 이름이 일치하는지 확인
					.findFirst() // 첫 번째 일치하는 캐시 타입을 찾음
					.orElseThrow(() -> new IllegalArgumentException("Unknown cache name: " + name)); // 예외 처리
		}
	}
	
	/**
	 * 로그인 제공자 정의 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum LoginProviderEnum {
		
		/** 네이버 */
		NAVER("NAVER", "NAVER_USER"),
		/** 카카오 */
		KAKAO("KAKAO", "KAKAO_USER"),
		/** 구글 */
		GOOGLE("GOOGLE", "GOOGLE_USER");
		
		/** 로그인 제공자 */
		private String provider;
		
		/** 로그인 제공자 유저 */
		private String providerUser;
		
	}
	
	/**
	 * 네이버 회원 프로필 조회 에러코드 정의 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum NaverProfileErrorEnum {
		
		/** 인증 실패 */
		AUTHENTICATION_FAILED(401, "024"),
		/** 인증 헤더 미존재 */
		AUTHENTICATION_HEADER_NOT_EXISTS(401, "028"),
		/** 접근 권한 없음 */
		FORBIDDEN(403, "403"),
		/** 리소스를 찾을 수 없음 */
		NOT_FOUND(404, "404"),
		/** 서버 에러 */
		INTERNAL_SERVER_ERROR(500, "500");
		
		/** HTTP 에러 코드 */
		private int httpErrorCode;
		
		/** 결과 에러 코드 */
		private String resultErrorCode;
		
		/** 결과 에러 메시지 */
		public static NaverProfileErrorEnum getNaverProfileError(String resultCode) {
			return Arrays.stream(values())
					.filter(e -> e.resultErrorCode.equals(resultCode)) // 결과 에러 코드가 일치하는지 확인
					.findFirst() // 첫 번째 일치하는 에러 코드를 찾음
					.orElse(INTERNAL_SERVER_ERROR); // 기본값으로 INTERNAL_SERVER_ERROR 반환
		}
	}
	
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

}
