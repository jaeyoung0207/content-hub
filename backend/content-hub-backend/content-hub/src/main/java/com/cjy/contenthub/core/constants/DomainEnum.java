package com.cjy.contenthub.core.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 도메인 enum 정의 클래스
 */
public class DomainEnum {
	
	/** 장르명 : 애니메이션 */
	private static final String ANI = "ani";
	
	/** 장르명 : 드라마 */
	private static final String DRAMA = "drama";
	
	/** 장르명 : 다큐멘터리 */
	private static final String DOCUMENTARY = "documentary";
	
	/** 장르명 : 키즈 */
	private static final String KIDS = "kids";
	
	/** 장르명 : 뉴스 */
	private static final String NEWS = "news";
	
	/** 장르명 : 예능 */
	private static final String VARIETY = "variety";
	
	/** 장르명 : 영화 */
	private static final String MOVIE = "movie";
	
	/** 장르명 : 코믹스 */
	private static final String COMICS = "comics";
	
	/** 장르명 : 인물 */
	private static final String PERSON = "person";

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

		/** 장르 ID */
		private int genreId;
		
		/** 장르(영어) */
		private String genreEnglish;

		/** 장르(한국어) */
		private String genreKorean;

		/** 장르 맵(영어 -> 한국어) */
		public static final Map<String, String> GENRE_EN_KO_MAP = Stream.of(values()).collect(
				Collectors.toMap(TmdbGenreEnum::getGenreEnglish, TmdbGenreEnum::getGenreKorean, (oldKey,newKey) -> newKey));
		
		/** 장르 맵(ID -> 영어) */
		public static final Map<Integer, String> GENRE_ID_EN_MAP = Stream.of(values()).collect(
				Collectors.toMap(TmdbGenreEnum::getGenreId, TmdbGenreEnum::getGenreEnglish, (oldKey,newKey) -> newKey));

		/** 영어 장르명 -> 한글 장르명으로 변경 */
		public static String getTranslatedGenre(String genre) {
			return StringUtils.defaultString(GENRE_EN_KO_MAP.get(genre));
		}
	}
	
	/**
	 * AniList 장르 정의 enum
	 * TMDB 장르와 매핑되는 AniList 장르의 경우 TMDB 장르 ID를 함께 정의
	 */
	@AllArgsConstructor
	@Getter
	public enum AniListGenreEnum {
		
		/** 액션 */
        GENRE_ACTION(TmdbGenreEnum.GENRE_ACTION.getGenreId() , "Action", "액션"),
        /** 어드벤처 */
        GENRE_ADVENTURE(TmdbGenreEnum.GENRE_ADVENTURE.getGenreId(), "Adventure", "어드벤처"),
        /** 코미디 */
        GENRE_COMEDY(TmdbGenreEnum.GENRE_COMEDY.getGenreId(), "Comedy", "코미디"),
        /** 드라마 */
        GENRE_DRAMA(TmdbGenreEnum.GENRE_DRAMA.getGenreId(), "Drama", "드라마"),
        /** 에로물 */
        GENRE_ECCHI(0, "Ecchi", "에로물"),
        /** 판타지 */
        GENRE_FANTASY(TmdbGenreEnum.GENRE_FANTASY.getGenreId(), "Fantasy", "판타지"),
        /** 성인물 */
        GENRE_HENTAI(0, "Hentai", "성인물"),
        /** 호러 */
        GENRE_HORROR(TmdbGenreEnum.GENRE_HORROR.getGenreId(), "Horror", "호러"),
        /** 마법소녀물 */
        GENRE_MAGIC(0, "Mahou Shoujo", "마법소녀물"),
        /** 메카물 */
        GENRE_MECHA(0, "Mecha", "메카물"),
        /** 음악 */
        GENRE_MUSIC(TmdbGenreEnum.GENRE_MUSIC.getGenreId(), "Music", "음악"),
        /** 미스테리 */
        GENRE_MYSTERY(TmdbGenreEnum.GENRE_MYSTERY.getGenreId(), "Mystery", "미스테리"),
        /** 심리극 */
        GENRE_PSYCHOLOGICAL(0, "Psychological", "심리극"),
        /** 로맨스 */
        GENRE_ROMANCE(TmdbGenreEnum.GENRE_ROMANCE.getGenreId(), "Romance", "로맨스"),
        /** 공상과학 */
        GENRE_SCIENCE_FICTION(TmdbGenreEnum.GENRE_SCIENCE_FICTION.getGenreId(), "Sci-Fi", "공상과학"),
        /** 일상물 */
        GENRE_SLICE_OF_LIFE(0, "Slice of Life", "일상물"),
        /** 스포츠 */
        GENRE_SPORTS(0, "Sports", "스포츠"),
        /** 초자연적 */
        GENRE_SUPERNATURAL(0, "Supernatural", "초자연적"),
        /** 스릴러 */
        GENRE_THRILLER(TmdbGenreEnum.GENRE_THRILLER.getGenreId(), "Thriller", "스릴러");
		
		/** 장르 ID */
		private int genreId;

		/** 장르(영어) */
        private String genreEnglish;
        
        /** 장르(한국어) */
        private String genreKorean;
        
        /** 장르 맵 */
        public static final Map<String, Integer> GENRE_EN_ID_MAP = Stream.of(values()).collect(
        		Collectors.toMap(AniListGenreEnum::getGenreEnglish, AniListGenreEnum::getGenreId, (oldKey,newKey) -> newKey));
        
	}

	/**
	 * 컨텐츠 미디어 타입 정의 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum ContentMediaTypeEnum {

		/** Ani */
		MEDIA_TYPE_ANI("1101", ANI),
		/** Drama */
		MEDIA_TYPE_DRAMA("1102", DRAMA),
        /** Documentary */		
		MEDIA_TYPE_DOCUMENTARY("1103", DOCUMENTARY),
		/** Kids */
		MEDIA_TYPE_KIDS("1104", KIDS),
		/** News */
		MEDIA_TYPE_NEWS("1105", NEWS),
		/** Variety(Reality, Talk Etc) */
		MEDIA_TYPE_VARIETY("1106", VARIETY),
		/** Movie */
		MEDIA_TYPE_MOVIE("1201", MOVIE),
		/** Person */
		MEDIA_TYPE_PERSON("1301", PERSON),
		/** Comics */
		MEDIA_TYPE_COMICS("2101", COMICS),
		/** TV(TMDB API) */
		TMDB_MEDIA_TYPE_TV("1100", "tv"),
		/** Movie(TMDB API) */
		TMDB_MEDIA_TYPE_MOVIE("1200", MOVIE),
		/** Person(TMDB API) */
		TMDB_MEDIA_TYPE_PERSON("1300", PERSON),
		/** Manga(AniList API) */
		ANILIST_MEDIA_TYPE_MANGA("2100", "manga"),
		/** Anime(AniList API) */
		ANILIST_MEDIA_TYPE_ANIME("2200", "anime");

		/** 미디어 타입 코드 */
		private String contentMediaTypeCode;
		
		/** 미디어 타입 문자열 */
		private String contentMediaTypeValue;
		
		/** 
		 * TV 미디어 타입 리스트 반환 
		 */
		public static List<String> getBelongToTvList() {
			List<String> tvList = new ArrayList<>();
			for (ContentMediaTypeEnum value : values()) {
                if (value.getContentMediaTypeCode().startsWith("11")) {
                    tvList.add(value.getContentMediaTypeCode());
                }
			}
			return tvList;
		}
	}
	
	/**
	 * 화면 표시용 미디어 타입 정의 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum DisplayMediaTypeEnum {

		/** Ani */
		MEDIA_TYPE_ANI("1", ANI),
		/** Drama */
		MEDIA_TYPE_DRAMA("2", DRAMA),
		/** Movie */
		MEDIA_TYPE_MOVIE("3", MOVIE),
        /** Documentary */		
		MEDIA_TYPE_DOCUMENTARY("4", DOCUMENTARY),
		/** Kids */
		MEDIA_TYPE_KIDS("5", KIDS),
		/** News */
		MEDIA_TYPE_NEWS("6", NEWS),
		/** Variety(Reality, Talk Etc) */
		MEDIA_TYPE_VARIETY("7", VARIETY),
		/** Comics */
		MEDIA_TYPE_COMICS("21", COMICS),
		/** Person */
		MEDIA_TYPE_PERSON("31", PERSON);

		/** 미디어 타입 코드 */
		private String displayMediaTypeCode;
		
		/** 미디어 타입 문자열 */
		private String displayMediaTypeValue;
	}
	
	/**
	 * 컨텐츠 미디어 타입 <-> 화면 표시용 미디어 타입 매핑 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum MediaTypeMappingEnum {
		
		/** Ani */
		MEDIA_TYPE_ANI(ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode(),
				DisplayMediaTypeEnum.MEDIA_TYPE_ANI.getDisplayMediaTypeCode()),
		/** Drama */
		MEDIA_TYPE_DRAMA(ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode(),
				DisplayMediaTypeEnum.MEDIA_TYPE_DRAMA.getDisplayMediaTypeCode()),
		/** Documentary */
		MEDIA_TYPE_DOCUMENTARY(ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode(),
				DisplayMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getDisplayMediaTypeCode()),
		/** Kids */
		MEDIA_TYPE_KIDS(ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode(),
				DisplayMediaTypeEnum.MEDIA_TYPE_KIDS.getDisplayMediaTypeCode()),
		/** News */
		MEDIA_TYPE_NEWS(ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode(),
				DisplayMediaTypeEnum.MEDIA_TYPE_NEWS.getDisplayMediaTypeCode()),
		/** Variety(Reality, Talk Etc) */
		MEDIA_TYPE_VARIETY(ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode(),
				DisplayMediaTypeEnum.MEDIA_TYPE_VARIETY.getDisplayMediaTypeCode()),
		/** Movie */
		MEDIA_TYPE_MOVIE(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode(),
				DisplayMediaTypeEnum.MEDIA_TYPE_MOVIE.getDisplayMediaTypeCode()),
		/** Comics */
		MEDIA_TYPE_COMICS(ContentMediaTypeEnum.MEDIA_TYPE_COMICS.getContentMediaTypeCode(),
				DisplayMediaTypeEnum.MEDIA_TYPE_COMICS.getDisplayMediaTypeCode()),
		/** Person */
		MEDIA_TYPE_PERSON(ContentMediaTypeEnum.MEDIA_TYPE_PERSON.getContentMediaTypeCode(),
				DisplayMediaTypeEnum.MEDIA_TYPE_PERSON.getDisplayMediaTypeCode());

		/** 컨텐츠 미디어 타입 코드 */
		private String contentMediaTypeCode;

		/** 화면 표시용 미디어 타입 코드 */
		private String displayMediaTypeCode;
		
		/** 컨텐츠 미디어 타입 -> 화면 표시용 미디어 타입 맵 */
		public static final Map<String, String> CONTENT_DISPLAY_MEDIA_TYPE_MAP = Stream.of(values())
				.collect(Collectors.toMap(MediaTypeMappingEnum::getContentMediaTypeCode,
						MediaTypeMappingEnum::getDisplayMediaTypeCode, (oldKey, newKey) -> newKey));
		
		/** 화면 표시용 미디어 타입 -> 컨텐츠 미디어 타입 맵 */
		public static final Map<String, String> DISPLAY_CONTENT_MEDIA_TYPE_MAP = Stream.of(values())
				.collect(Collectors.toMap(MediaTypeMappingEnum::getDisplayMediaTypeCode,
						MediaTypeMappingEnum::getContentMediaTypeCode, (oldKey, newKey) -> newKey));
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
		private String anilistMediaType;
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
	 * 로그인 상태 정의 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum LoginStatusEnum {
		/** 로그인 */
		LOGIN("1"),
		/** 로그아웃 */
		LOGOUT("0");
		
		private String loginStatus;
	}
	
	/**
	 * AniList 정렬 Enum
	 */
	@AllArgsConstructor
	@Getter
	public enum SortEnum {
		ID,
        ID_DESC,
        ROLE,
        ROLE_DESC,
        NAME,
        NAME_DESC,
        FAVORITES,
        FAVORITES_DESC;
	}
	
	/**
	 * 경고 메시지 코드 정의 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum DomainMessagesWarnEnum {
		
		/** 캐릭터 - 캐릭터 정보 없음 */
		WARN_CHARACTER_CHARACTER_NOT_FOUND("warn.character.characterNotFound"),
		/** 캐릭터 - 스태프 정보 없음 */
		WARN_CHARACTER_STAFF_NOT_FOUND("warn.character.staffNotFound"),
		/** 상세 정보 - 만화 정보 없음 */
		WARN_DETAIL_INFORMATION_COMICS_NOT_FOUND("warn.detailInformation.comicsNotFound"),
		/** 상세 정보 - 캐릭터 정보 없음 */
		WARN_DETAIL_INFORMATION_CHARACTERS_NOT_FOUND("warn.detailInformation.charactersNotFound"),
		/** 상세 정보 - 스태프 정보 없음 */
		WARN_DETAIL_INFORMATION_STAFF_NOT_FOUND("warn.detailInformation.staffNotFound"),
		/** 상세 추천 - 추천 정보 없음 재시도 */
		WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND_THEN_RETRY("warn.detailRecommendation.recommendationNotFoundThenRetry"),
		/** 상세 추천 - 추천 정보 없음 */
		WARN_DETAIL_RECOMMENDATION_RECOMMENDATION_NOT_FOUND("warn.detailRecommendation.recommendationNotFound"),
		/** 인물 - 인물 정보 없음 */
		WARN_PERSON_PERSON_NOT_FOUND("warn.person.personNotFound"),
		/** 검색 - 잘못된 컨텐츠 미디어 타입 */
		WARN_SEARCH_WRONG_CONTENT_MEDIA_TYPE("warn.search.wrongContentMediaType"),
		/** 위시리스트 - 위시리스트 없음 */
		WARN_WISHLIST_WISHLIST_NOT_FOUND("warn.wishlist.wishlistNotFound"),
		/** 위시리스트 - 이미 존재하는 위시리스트 */
		WARN_WISHLIST_WISHLIST_ALREADY_EXISTS("warn.wishlist.wishlistAlreadyExists");
		
		/** 메시지 코드 */
		private String messageCode;
	}
	
	/**
	 * 에러 메시지 코드 정의 enum
	 */
	@AllArgsConstructor
	@Getter
	public enum DomainMessagesErrorEnum {
		
		/** 로그인 - 유저 없음 */
		ERROR_LOGIN_NOT_FOUND_USER("error.login.notFoundUser"),
		/** 로그인 - 프로필 없음 */
		ERROR_LOGIN_NOT_FOUND_PROFILE("error.login.notFoundProfile"),
		/** 로그인 - 페이로드 없음 */
		ERROR_LOGIN_PAYLOAD_EMPTY("error.login.payloadEmpty"),
		/** 상세 코멘트 - 코멘트 정보 없음 */
		ERROR_DETAIL_COMMENT_COMMENT_NOT_FOUND("error.detailComment.commentNotFound");
		
		/** 메시지 코드 */
		private String messageCode;
	}

}
