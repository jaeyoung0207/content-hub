package com.cjy.contenthub.common.api.dto.tmdb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TMDB 인물 영화 크레딧 제작 참여작 DTO
 * 
 * @see <a href="https://developer.themoviedb.org/reference/person-movie-credits">TMDB Person Movie Credits API 문서</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class TmdbPersonMovieCreditsCrewDto extends TmdbPersonCreditsCrewDto {
	
	/** 원 제목 */
	private String originalTitle;
	
	/** 첫 상영일 */
	private String releaseDate;
	
	/** 제목 */
	private String title;
	
	/** 동영상 여부 */
	private boolean video;
	
}
