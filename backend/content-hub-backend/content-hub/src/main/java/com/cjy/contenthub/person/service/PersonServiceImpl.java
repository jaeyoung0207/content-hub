package com.cjy.contenthub.person.service;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.cjy.contenthub.common.integration.tmdb.constants.TmdbParamConstants;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbPersonMovieCreditsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbPersonTvCreditsDto;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.core.constants.CacheNames;
import com.cjy.contenthub.core.constants.DomainEnum.DomainMessagesWarnEnum;
import com.cjy.contenthub.core.constants.DomainEnum.TmdbGenderEnum;
import com.cjy.contenthub.core.facade.ApiFacade;
import com.cjy.contenthub.person.controller.dto.PersonCreditsCastDto;
import com.cjy.contenthub.person.controller.dto.PersonCreditsCrewDto;
import com.cjy.contenthub.person.controller.dto.PersonDto;
import com.cjy.contenthub.person.controller.dto.PersonResponseDto;
import com.cjy.contenthub.person.helper.PersonHelper;
import com.cjy.contenthub.person.mapper.PersonMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

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
	
	/** 공통 유틸리티 */
	private final ApiFacade apiUtil;
	
	/** 메시지 유틸리티 */
	private final MessageUtil messageUtil;

	/** TMDB API 인물 상세 매퍼 */
	private final PersonMapper mapper;

	/** TMDB API 인물 상세 헬퍼 */
	private final PersonHelper helper;

	/** TMDB API Person Detail API 패스 */
	@Value("${tmdb.url.person-detail-path}")
	private String personDetailPath;

	/**
	 * 인물 상세 정보 조회
	 * 
	 * @param personId 인물 ID
	 * @return 인물 상세 정보 DTO
	 */
	@Override
	@Cacheable(value = CacheNames.PERSON_DETAILS, unless = "#result == null")
	public PersonResponseDto getPersonDetails(int personId) {

		// 장르 맵 조회
		Mono<Map<String, Integer>> tvGenreMapMono = apiUtil.getTvGenres();
		Mono<Map<String, Integer>> movieGenreMapMono = apiUtil.getMovieGenres();

		return Mono.zip(tvGenreMapMono, movieGenreMapMono).flatMap(genreTuple -> {
			Map<String, Integer> tvGenreMap = genreTuple.getT1();
			Map<String, Integer> movieGenreMap = genreTuple.getT2();

			// TMDB 인물 상세 정보 조회
			return tmdbWebClient.get()
					.uri(uriBuilder -> uriBuilder.path(String.format(personDetailPath, personId))
							.queryParam(TmdbParamConstants.PARAM_PERSON_ID, personId)
							.queryParam(TmdbParamConstants.PARAM_APPEND_TO_RESPONSE, TmdbParamConstants.PERSON_CREDITS)
							.queryParam(TmdbParamConstants.PARAM_LANGUAGE, TmdbParamConstants.LANGUAGE_KOREAN)
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
						if (response.getTvCredits() == null
								&& response.getMovieCredits() == null) {
							Object[] messageParams = { personId };
							log.warn(messageUtil.getMessageKO(
									DomainMessagesWarnEnum.WARN_PERSON_PERSON_NOT_FOUND.getMessageCode(), messageParams));
							return personResponse;
						}

						// TV 프로그램 크레딧
						TmdbPersonTvCreditsDto tvCredits = response.getTvCredits();
						// 영화 크레딧 
						TmdbPersonMovieCreditsDto movieCredits = response.getMovieCredits();		

						// 출연작 정보 설정
						if (ObjectUtils.isNotEmpty(tvCredits)) {
							helper.setCreditsCast(personResponse, tvCredits.getCast(), tvGenreMap);
							helper.setCreditsCrew(personResponse, tvCredits.getCrew(), tvGenreMap);
						}

						// 제작 참여작 정보 설정
						if (ObjectUtils.isNotEmpty(movieCredits)) {
							helper.setCreditsCast(personResponse, movieCredits.getCast(), movieGenreMap);
							helper.setCreditsCrew(personResponse, movieCredits.getCrew(), movieGenreMap);
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
					});
		}).block();
	}

}
