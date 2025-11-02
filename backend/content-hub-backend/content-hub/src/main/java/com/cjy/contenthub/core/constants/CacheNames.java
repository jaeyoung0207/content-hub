package com.cjy.contenthub.core.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 캐시명 정의 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheNames {
	
	/** 캐시명(TMDB): TV 장르 */
	public static final String TV_GENRE = "tvGenre";
	
	/** 캐시명(TMDB): 영화 장르 */
    public static final String MOVIE_GENRE = "movieGenre";
    
    /** 캐시명(DeepL): 번역 API 이름 */
    public static final String TRANSLATE = "translate";
    
    /** 캐시명(캐릭터): 캐릭터 */
    public static final String CHARACTER = "character";
    
    /** 캐시명(캐릭터): 스태프 */
    public static final String STAFF = "staff";
    
    /** 캐시명(상세 정보): TV 상세 */
    public static final String TV_DETAIL = "tvDetail";
    
    /** 캐시명(상세 정보): 영화 상세 */
    public static final String MOVIE_DETAIL = "movieDetail";
    
    /** 캐시명(상세 정보): 만봐 상세 */
    public static final String COMICS_DETAIL = "comicsDetail";
    
    /** 캐시명(상세 정보): 만화 캐릭터 목록 */
    public static final String COMICS_CHARACTER_LIST = "comicsCharacterList";
    
    /** 캐시명(상세 정보): 만화 스태프 목록 */
    public static final String COMICS_STAFF_LIST = "comicsStaffList";
    
    /** 캐시명(상세 추천): TMDB TV 추천 */
    public static final String TMDB_TV_RECOMMENDATIONS = "tmdbTvRecommendations";
    
    /** 캐시명(상세 추천): TMDB 영화 추천 */
    public static final String TMDB_MOVIE_RECOMMENDATIONS = "tmdbMovieRecommendations";
    
    /** 캐시명(상세 추천): AniList 만화 추천 */
    public static final String ANILIST_COMICS_RECOMMENDATIONS = "anilistComicsRecommendations";
    
    /** 캐시명(인물): 인물 상세 */
    public static final String PERSON_DETAILS = "personDetails";
    
    /** 캐시명(검색): 키워드 검색 */
    public static final String SEARCH_KEYWORD = "searchKeyword";
    
    /** 캐시명(검색): 비디오 검색 */
    public static final String SEARCH_VIDEO = "searchVideo";
    
    /** 캐시명(검색): 애니메이션 검색 */
    public static final String SEARCH_ANI = "searchAni";
    
    /** 캐시명(검색): 애니메이션 제외 TV 검색 */
    public static final String SEARCH_TV_EXCEPT_ANI = "searchTvExceptAni";
    
    /** 캐시명(검색): 영화 검색 */
    public static final String SEARCH_MOVIE = "searchMovie";
    
    /** 캐시명(검색): 만화 검색 */
    public static final String SEARCH_COMICS = "searchComics";

}
