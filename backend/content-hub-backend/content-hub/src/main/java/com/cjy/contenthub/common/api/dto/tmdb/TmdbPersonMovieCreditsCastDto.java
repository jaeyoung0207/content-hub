package com.cjy.contenthub.common.api.dto.tmdb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB 인물 영화 크레딧 출연작 DTO
 * 
 * @see <a href="https://developer.themoviedb.org/reference/person-movie-credits">TMDB Person Movie Credits API 문서</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbPersonMovieCreditsCastDto extends TmdbPersonCreditsCastDto {
	
	/** 원제목 */
	private String originalTitle;
	
	/** 첫 상영일 */
	private String releaseDate;
	
	/** 제목 */
	private String title;
	
	/** 동영상 여부 */
	private boolean video;
	
	/** 순서 */
	private int order;
	
}
