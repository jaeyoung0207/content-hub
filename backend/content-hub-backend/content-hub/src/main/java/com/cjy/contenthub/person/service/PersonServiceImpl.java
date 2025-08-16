package com.cjy.contenthub.person.service;

import java.util.ArrayList;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.common.api.dto.tmdb.TmdbPersonMovieCreditsDto;
import com.cjy.contenthub.common.api.dto.tmdb.TmdbPersonTvCreditsDto;
import com.cjy.contenthub.common.constants.CommonEnum.TmdbGenderEnum;
import com.cjy.contenthub.person.controller.dto.PersonCreditsCastDto;
import com.cjy.contenthub.person.controller.dto.PersonCreditsCrewDto;
import com.cjy.contenthub.person.controller.dto.PersonDto;
import com.cjy.contenthub.person.controller.dto.PersonResponseDto;
import com.cjy.contenthub.person.helper.PersonHelper;
import com.cjy.contenthub.person.mapper.PersonMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인물 정보 API 컨트롤러 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PersonServiceImpl implements PersonService {

	/** TMDB API 통신용 WebClient 클래스 */
	@Qualifier("tmdbWebClient")
	private final WebClient tmdbWebClient;

	/** TMDB API 인물 상세 매퍼 */
	private final PersonMapper mapper;

	/** TMDB API Person Detail API 패스 */
	@Value("${tmdb.url.personDetailPath}")
	private String personDetailPath;

	/** TMDB API 인물 상세 정보 헬퍼 클래스 */
	private final PersonHelper helper;

	/** 리퀘스트 파라미터 키 : 인물 ID */
	private static final String PARAM_PERSON_ID = "personId";

	/** 리퀘스트 파라미터 키 : append_to_response */
	private static final String PARAM_APPEND_TO_RESPONSE = "append_to_response";

	/** 크레딧 : credits */
	private static final String PERSON_CREDITS = "tv_credits,movie_credits";

	/** 리퀘스트 파라미터 키 : 언어 */
	private static final String PARAM_LANGUAGE = "language";

	/** 언어 : 한국어 */
	private static final String LANGUAGE_KOREAN = "ko-KR";

	/**
	 * 인물 상세 정보 조회
	 * 
	 * @param personId 인물 ID
	 * @return 인물 상세 정보 DTO
	 */
	@Override
	@Cacheable(value = "personDetails", key = "#personId", unless = "#result == null")
	public PersonResponseDto getPersonDetails(int personId) {

		// TMDB 인물 상세 정보 조회
		return tmdbWebClient.get()
				.uri(uriBuilder -> uriBuilder.path(String.format(personDetailPath, personId))
						.queryParam(PARAM_PERSON_ID, personId)
						.queryParam(PARAM_APPEND_TO_RESPONSE, PERSON_CREDITS)
						.queryParam(PARAM_LANGUAGE, LANGUAGE_KOREAN)
						.build())
				.retrieve()
				.bodyToMono(PersonDto.class)
				.map(response -> {
					// 응답 데이터 매핑
					PersonResponseDto personResponse = mapper.personToPersonResponse(response);
					// 성별코드에서 성별 값 설정
					personResponse.setGenderValue(TmdbGenderEnum.getGender(response.getGender()).getGenderValue());
					// 출연작 목록과 초기화
					personResponse.setCast(new ArrayList<>());
					// 제작 참여작 목록 초기화
					personResponse.setCrew(new ArrayList<>());

					// 크레딧 정보가 없는 경우 경고 로그 출력 후 응답 반환
					if (ObjectUtils.isEmpty(response.getTvCredits())
							&& ObjectUtils.isEmpty(response.getMovieCredits())) {
						log.warn("Person ID {} has no credits data.", personId);
						return personResponse;
					}

					// TV 프로그램 크레딧
					TmdbPersonTvCreditsDto tvCredits = response.getTvCredits();
					// 영화 크레딧 
					TmdbPersonMovieCreditsDto movieCredits = response.getMovieCredits();		

					// 출연작 정보 설정
					if (ObjectUtils.isNotEmpty(tvCredits)) {
						helper.setCreditsCast(personResponse, tvCredits.getCast());
						helper.setCreditsCrew(personResponse, tvCredits.getCrew());
					}

					// 제작 참여작 정보 설정
					if (ObjectUtils.isNotEmpty(movieCredits)) {
						helper.setCreditsCast(personResponse, movieCredits.getCast());
						helper.setCreditsCrew(personResponse, movieCredits.getCrew());
					}

					int castCount = 0;
					int crewCount = 0;
					// 출연작 목록이 비어있지 않은 경우
					if (!CollectionUtils.isEmpty(personResponse.getCast())) {
						// 출연작 목록 정렬
						personResponse.getCast().sort((o1,o2) -> 
						StringUtils.compare(o2.getReleaseYear(), o1.getReleaseYear())
								);
						// 출연작 수
						castCount = (int) personResponse.getCast().stream()
								.filter(cast -> StringUtils.isNotEmpty(cast.getTitle()))
								.map(PersonCreditsCastDto::getTitle)
								.distinct()
								.count();
						personResponse.setCastCount(castCount);
					}

					// 제작 참여작 목록이 비어있지 않은 경우
					if (!CollectionUtils.isEmpty(personResponse.getCrew())) {
						// 제작 참여작 목록 정렬
						personResponse.getCrew().sort((o1,o2) -> 
						StringUtils.compare(o2.getReleaseYear(), o1.getReleaseYear())
								);
						// 제작 참여작 수
						crewCount = (int) personResponse.getCrew().stream()
								.filter(crew -> StringUtils.isNotEmpty(crew.getTitle()))
								.map(PersonCreditsCrewDto::getTitle)
								.distinct()
								.count();
						personResponse.setCrewCount(crewCount);
					}

					// 응답 반환
					return personResponse;
				}).block();
	}

}
