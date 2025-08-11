package com.cjy.contenthub.common.api.dto.tmdb;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * TMDB 인물 상세 정보 DTO
 * 
 * @see <a href="https://developer.themoviedb.org/reference/person-details">TMDB Person Details API 문서</a>
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class TmdbPersonDetailsDto {
	
	/** 성인 작품 여부 */
	private boolean adult;
	
	/** 활동명 */
	private List<String> alsoKnownAs;
	
	/** 약력 */
	private String biography;
	
	/** 생년월일 */
	private String birthday;
	
	/** 사망일 */
	private String deathday;
	
	/** 성별 */
	private Integer gender;
	
	/** 홈페이지 */
	private String homepage;
	
	/** ID */
	private int id;
	
	/** IMDB ID */
	private String imdbId;
	
	/** 알려진 부서 */
	private String knownForDepartment;
	
	/** 이름 */
	private String name;
	
	/** 출생지 */
	private String placeOfBirth;
	
	/** 인기 점수 */
	private BigDecimal popularity;
	
	/** 프로필 이미지 경로 */
	private String profilePath;

}
