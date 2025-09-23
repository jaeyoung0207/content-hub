package com.cjy.contenthub.person.helper;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbPersonCreditsCastDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbPersonCreditsCrewDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbPersonMovieCreditsCastDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbPersonMovieCreditsCrewDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbPersonTvCreditsCastDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbPersonTvCreditsCrewDto;
import com.cjy.contenthub.common.constants.CommonConstants;
import com.cjy.contenthub.common.constants.CommonEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.common.util.GenreUtil;
import com.cjy.contenthub.person.controller.dto.PersonCreditsCastDto;
import com.cjy.contenthub.person.controller.dto.PersonCreditsCrewDto;
import com.cjy.contenthub.person.controller.dto.PersonResponseDto;
import com.cjy.contenthub.person.mapper.PersonMapper;

import lombok.RequiredArgsConstructor;

/**
 * 인물 정보 헬퍼 클래스
 */
@Component
@RequiredArgsConstructor
public class PersonHelper {

	/** 인물 정보 매퍼 */
	private final PersonMapper mapper;

	/**
	 * TMDB API 인물 출연작 정보 설정
	 * 
	 * @param personResponse 인물 응답 DTO
	 * @param creditsCastList 출연작 목록
	 * @param genreMap 장르 맵
	 */
	public void setCreditsCast(PersonResponseDto personResponse, List<? extends TmdbPersonCreditsCastDto> creditsCastList, Map<String, Integer> genreMap) {

		// 출연작 정보가 비어있으면 리턴		
		if (ObjectUtils.isEmpty(creditsCastList)) {
			return;
		}
		// 출연작 목록 루프처리
		for (TmdbPersonCreditsCastDto creditsCast  : creditsCastList) {
			List<Integer> genreIds = creditsCast.getGenreIds();
			// 장르 ID가 null 이면 패스
			if (genreIds == null) {
				continue;
			}
			// 출연작 정보 매핑
			PersonCreditsCastDto cast = mapper.tmdbPersonCastToPersonCast(creditsCast);
			// TV 프로그램 출연작인 경우
			if (creditsCast instanceof TmdbPersonTvCreditsCastDto tvCast) {
				// TV 프로그램 출연작 정보 매핑
				cast.setOriginalTitle(tvCast.getOriginalName());
				cast.setTitle(tvCast.getName());
				cast.setReleaseDate(tvCast.getFirstCreditAirDate());
				cast.setEpisodeCount(tvCast.getEpisodeCount());
				cast.setContentMediaTypeName(ContentMediaTypeEnum.TMDB_MEDIA_TYPE_TV.getContentMediaTypeValue().toUpperCase());
				cast.setContentMediaType(GenreUtil.getContentMediaTypeByGenre(genreMap, genreIds));
			} 
			// 영화 출연작인 경우
			else {
				// 영화 출연작 DTO로 형변환
				TmdbPersonMovieCreditsCastDto movieCast = (TmdbPersonMovieCreditsCastDto) creditsCast;
				// 영화 출연작 정보 매핑
				cast.setOriginalTitle(movieCast.getOriginalTitle());
				cast.setTitle(movieCast.getTitle());
				cast.setReleaseDate(movieCast.getReleaseDate());
				cast.setContentMediaTypeName(ContentMediaTypeEnum.TMDB_MEDIA_TYPE_MOVIE.getContentMediaTypeValue().toUpperCase());
				cast.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode());
			}
			// 첫 상영연도 설정
			cast.setReleaseYear(setReleaseYear(cast.getReleaseDate()));

			// 응답 오브젝트 출연작 목록에 추가
			personResponse.getCast().add(cast);

		}
	}

	/**
	 * TMDB API 인물 제작 참여작 정보 설정
	 * 
	 * @param personResponse 인물 응답 DTO
	 * @param creditsCrewList 제작 참여작 목록
	 */
	public void setCreditsCrew(PersonResponseDto personResponse, List<? extends TmdbPersonCreditsCrewDto> creditsCrewList, Map<String, Integer> genreMap) {

		// 출연작 정보가 비어있으면 리턴		
		if (ObjectUtils.isEmpty(creditsCrewList)) {
			return;
		}

		// 제작 참여작 목록 루프처리
		for (TmdbPersonCreditsCrewDto creditsCrew : creditsCrewList) {
			List<Integer> genreIds = creditsCrew.getGenreIds();
			// 장르 ID가 null 이면 패스
			if (genreIds == null) {
				continue;
			}
			// 출연작 정보 매핑
			PersonCreditsCrewDto crew = mapper.tmdbPersonCrewToPersonCrew(creditsCrew);
			// TV 프로그램 출연작인 경우
			if (creditsCrew instanceof TmdbPersonTvCreditsCrewDto tvCrew) {
				// TV 프로그램 출연작 정보 매핑
				crew.setOriginalTitle(tvCrew.getOriginalName());
				crew.setTitle(tvCrew.getName());
				crew.setReleaseDate(tvCrew.getFirstCreditAirDate());
				crew.setEpisodeCount(tvCrew.getEpisodeCount());
				crew.setContentMediaTypeName(ContentMediaTypeEnum.TMDB_MEDIA_TYPE_TV.getContentMediaTypeValue().toUpperCase());
				crew.setContentMediaType(GenreUtil.getContentMediaTypeByGenre(genreMap, genreIds));
			} 
			// 영화 출연작인 경우
			else {
				// 영화 출연작 DTO로 형변환
				TmdbPersonMovieCreditsCrewDto movieCrew = (TmdbPersonMovieCreditsCrewDto) creditsCrew;
				// 영화 출연작 정보 매핑
				crew.setOriginalTitle(movieCrew.getOriginalTitle());
				crew.setTitle(movieCrew.getTitle());
				crew.setReleaseDate(movieCrew.getReleaseDate());
				crew.setContentMediaTypeName(ContentMediaTypeEnum.TMDB_MEDIA_TYPE_MOVIE.getContentMediaTypeValue().toUpperCase());
				crew.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode());
			}
			// 첫 상영연도 설정
			crew.setReleaseYear(setReleaseYear(crew.getReleaseDate()));

			// 응답 오브젝트 출연작 목록에 추가
			personResponse.getCrew().add(crew);
		}
	}

	/**
	 * TMDB API 인물 출연작의 첫 상영연도를 설정
	 * 
	 * @param releaseDate 첫 상영일
	 * @return 첫 상영연도
	 */
	private String setReleaseYear(String releaseDate) {
		// 첫 상영연도 설정
		if (StringUtils.isNotEmpty(releaseDate) && releaseDate.matches(CommonConstants.STR_DATE_REGEX)) {
			return releaseDate.substring(0, 4);
		}
		return null;
	}

}
