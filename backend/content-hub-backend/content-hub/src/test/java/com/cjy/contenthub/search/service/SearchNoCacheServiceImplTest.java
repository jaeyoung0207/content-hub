package com.cjy.contenthub.search.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.LoggerFactory;

import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.core.constants.DomainEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.core.constants.DomainEnum.DomainMessagesWarnEnum;
import com.cjy.contenthub.core.shared.service.WishlistSharedService;
import com.cjy.contenthub.search.controller.dto.SearchComicsResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchComicsResultDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;
import com.cjy.contenthub.wishlist.repository.WishlistRepository;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SearchNoCacheServiceImplTest {

	SearchNoCacheServiceImpl service;

	@Mock
	WishlistRepository wishlistRepository;

	@Mock
	WishlistSharedService wishlistFlagSharedService;
	
	@Mock
	MessageUtil messageUtil;
	
	private Logger logger = (Logger) LoggerFactory.getLogger(SearchNoCacheServiceImpl.class); 
    private ListAppender<ILoggingEvent> listAppender;

	@BeforeEach
	void setUp() {
		// ListAppender 생성
        listAppender = new ListAppender<>();
        listAppender.start();
        // Logger에 ListAppender 추가
        logger.addAppender(listAppender);
        // 서비스 인스턴스 생성
		service = new SearchNoCacheServiceImpl(
				wishlistRepository,
				wishlistFlagSharedService,
				messageUtil
				);
	}

	@Test
	@DisplayName("[UT]setWishlistFromVideoResponse: 검색 결과에 위시리스트 여부 설정")
	void test_setWishlistFromVideoResponse() {

		SearchVideoResponseDto serchVideoResponse = new SearchVideoResponseDto();
		Long userId = 1L;

		List<SearchMovieResultsDto> movieResults = new ArrayList<>();
		SearchMovieResultsDto movieResult = new SearchMovieResultsDto();
		movieResult.setId(1201);
		movieResult.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode());
		movieResults.add(movieResult);
		List<SearchTvResultsDto> aniResults = new ArrayList<>();
		SearchTvResultsDto aniResult = new SearchTvResultsDto();
		aniResult.setId(1101);
		aniResult.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode());
		aniResults.add(aniResult);
		List<SearchTvResultsDto> dramaResults = new ArrayList<>();
		SearchTvResultsDto dramaResult = new SearchTvResultsDto();
		dramaResult.setId(1102);
		dramaResult.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode());
		dramaResults.add(dramaResult);
		List<SearchTvResultsDto> documentaryResults = new ArrayList<>();
		SearchTvResultsDto documentaryResult = new SearchTvResultsDto();
		documentaryResult.setId(1103);
		documentaryResult.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode());
		documentaryResults.add(documentaryResult);
		List<SearchTvResultsDto> kidsResults = new ArrayList<>();
		SearchTvResultsDto kidsResult = new SearchTvResultsDto();
		kidsResult.setId(1104);
		kidsResult.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode());
		kidsResults.add(kidsResult);
		List<SearchTvResultsDto> newsResults = new ArrayList<>();
		SearchTvResultsDto newsResult = new SearchTvResultsDto();
		newsResult.setId(1105);
		newsResult.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode());
		newsResults.add(newsResult);
		List<SearchTvResultsDto> varietyResults = new ArrayList<>();
		SearchTvResultsDto varietyResult = new SearchTvResultsDto();
		varietyResult.setId(1106);
		varietyResult.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode());
		varietyResults.add(varietyResult);
		serchVideoResponse.setMovieResults(movieResults);
		serchVideoResponse.setAniResults(aniResults);
		serchVideoResponse.setDramaResults(dramaResults);
		serchVideoResponse.setDocumentaryResults(documentaryResults);
		serchVideoResponse.setKidsResults(kidsResults);
		serchVideoResponse.setNewsResults(newsResults);
		serchVideoResponse.setVarietyResults(varietyResults);
		// 영화 위시리스트 설정 mock
		doAnswer(invocation -> {
			List<SearchMovieResultsDto> movieResultList = invocation.getArgument(0);
			SearchMovieResultsDto movieDto = movieResults.getFirst();
			movieDto.setWishlisted(true);
			movieResultList.add(movieDto);
			return null;
		})
		.when(wishlistFlagSharedService)
		.setWishlisted(
				eq(movieResults), 
				eq(List.of(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode())),
				eq(userId), 
				any(), 
				any(), 
				eq(wishlistRepository));
		// 애니 위시리스트 설정 mock
		doAnswer(invocation -> {
			List<SearchTvResultsDto> tvResultList = invocation.getArgument(0);
			SearchTvResultsDto tvDto = aniResults.getFirst();
			tvDto.setWishlisted(true);
			tvResultList.add(tvDto);
			return null;
		})
		.when(wishlistFlagSharedService)
		.setWishlisted(
				eq(aniResults), 
				eq(List.of(ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode())), 
				eq(userId), 
				any(), 
				any(), 
				eq(wishlistRepository));
		// 드라마 위시리스트 설정 mock
		doAnswer(invocation -> {
			List<SearchTvResultsDto> tvResultList = invocation.getArgument(0);
			SearchTvResultsDto tvDto = dramaResults.getFirst();
			tvDto.setWishlisted(true);
			tvResultList.add(tvDto);
			return null;
		})
		.when(wishlistFlagSharedService)
		.setWishlisted(
				eq(dramaResults), 
				eq(List.of(ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode())), 
				eq(userId), 
				any(), 
				any(), 
				eq(wishlistRepository));
		// 다큐멘터리 위시리스트 설정 mock
		doAnswer(invocation -> {
			List<SearchTvResultsDto> tvResultList = invocation.getArgument(0);
			SearchTvResultsDto tvDto = documentaryResults.getFirst();
			tvDto.setWishlisted(true);
			tvResultList.add(tvDto);
			return null;
		})
		.when(wishlistFlagSharedService)
		.setWishlisted(
				eq(documentaryResults), 
				eq(List.of(ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode())), 
				eq(userId), 
				any(), 
				any(), 
				eq(wishlistRepository));
		// 키즈 위시리스트 설정 mock
		doAnswer(invocation -> {
			List<SearchTvResultsDto> tvResultList = invocation.getArgument(0);
			SearchTvResultsDto tvDto = kidsResults.getFirst();
			tvDto.setWishlisted(true);
			tvResultList.add(tvDto);
			return null;
		})
		.when(wishlistFlagSharedService)
		.setWishlisted(
				eq(kidsResults), 
				eq(List.of(ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode())), 
				eq(userId), 
				any(), 
				any(), 
				eq(wishlistRepository));
		// 뉴스 위시리스트 설정 mock
		doAnswer(invocation -> {
			List<SearchTvResultsDto> tvResultList = invocation.getArgument(0);
			SearchTvResultsDto tvDto = newsResults.getFirst();
			tvDto.setWishlisted(true);
			tvResultList.add(tvDto);
			return null;
		})
		.when(wishlistFlagSharedService)
		.setWishlisted(
				eq(newsResults), 
				eq(List.of(ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode())), 
				eq(userId), 
				any(), 
				any(), 
				eq(wishlistRepository));
		// 버라이어티 위시리스트 설정 mock
		doAnswer(invocation -> {
			List<SearchTvResultsDto> tvResultList = invocation.getArgument(0);
			SearchTvResultsDto tvDto = varietyResults.getFirst();
			tvDto.setWishlisted(true);
			tvResultList.add(tvDto);
			return null;
		})
		.when(wishlistFlagSharedService)
		.setWishlisted(
				eq(varietyResults), 
				eq(List.of(ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode())), 
				eq(userId), 
				any(), 
				any(), 
				eq(wishlistRepository));
		
		// 실제 메서드 호출
		service.setWishlistFromVideoResponse(serchVideoResponse, userId);

		// 검증
		assertThat(movieResults.getFirst().isWishlisted()).isTrue();
		assertThat(aniResults.getFirst().isWishlisted()).isTrue();
		assertThat(dramaResults.getFirst().isWishlisted()).isTrue();
		assertThat(documentaryResults.getFirst().isWishlisted()).isTrue();
		assertThat(kidsResults.getFirst().isWishlisted()).isTrue();
		assertThat(newsResults.getFirst().isWishlisted()).isTrue();
		assertThat(varietyResults.getFirst().isWishlisted()).isTrue();
	}
	
	@Test
	@DisplayName("[UT]setWishlistFromAniResponse: 애니 검색 결과에 위시리스트 여부 설정")
	void test_setWishlistFromAniResponse() {

		SearchVideoResponseDto serchVideoResponse = new SearchVideoResponseDto();
		Long userId = 1L;

		List<SearchTvResultsDto> aniResults = new ArrayList<>();
		SearchTvResultsDto aniResult = new SearchTvResultsDto();
		aniResult.setId(1101);
		aniResult.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode());
		aniResults.add(aniResult);
		serchVideoResponse.setAniResults(aniResults);
		
		// 애니 위시리스트 설정 mock
		doAnswer(invocation -> {
			List<SearchTvResultsDto> tvResultList = invocation.getArgument(0);
			SearchTvResultsDto tvDto = aniResults.getFirst();
			tvDto.setWishlisted(true);
			tvResultList.add(tvDto);
			return null;
		})
		.when(wishlistFlagSharedService)
		.setWishlisted(
				eq(aniResults), 
				eq(List.of(ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode())), 
				eq(userId), 
				any(), 
				any(), 
				eq(wishlistRepository));
		
		// 실제 메서드 호출
		service.setWishlistFromAniResponse(serchVideoResponse, userId);

		// 검증
		assertThat(aniResults.getFirst().isWishlisted()).isTrue();
	}

	@ParameterizedTest
	@MethodSource("contentMediaTypeParams")
	@DisplayName("[UT]setWishlistFromTvExceptAniResponse: TV 검색 결과에 위시리스트 여부 설정 (애니 제외)")
	void test_setWishlistFromTvExceptAniResponse(SearchTvResponseDto searchTvResponse, Long userId, String contentMediaType) {

		List<SearchTvResultsDto> tvResults = new ArrayList<>();
		List<String> contentMediaTypeList = new ArrayList<>();
		Object[] params = {contentMediaType};
		String errorMessage = String.format("잘못된 컨텐츠 미디어 타입입니다. (contentMediaType: %s)", params);
		if (StringUtils.equals(contentMediaType, ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode())) {
			tvResults = searchTvResponse.getDramaResults();
			contentMediaTypeList = List.of(ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode());
		} else if (StringUtils.equals(contentMediaType,
				ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode())) {
			tvResults = searchTvResponse.getDocumentaryResults();
			contentMediaTypeList = List.of(ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode());
		} else if (StringUtils.equals(contentMediaType,
				ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode())) {
			tvResults = searchTvResponse.getKidsResults();
			contentMediaTypeList = List.of(ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode());
		} else if (StringUtils.equals(contentMediaType,
				ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode())) {
			tvResults = searchTvResponse.getNewsResults();
			contentMediaTypeList = List.of(ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode());
		} else if (StringUtils.equals(contentMediaType,
				ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode())) {
			tvResults = searchTvResponse.getVarietyResults();
			contentMediaTypeList = List.of(ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode());
		} else {
			// 결과 없음 케이스
			when(messageUtil.getMessageKO(
					DomainMessagesWarnEnum.WARN_SEARCH_WRONG_CONTENT_MEDIA_TYPE.getMessageCode(), params))
			.thenReturn(errorMessage);
		}

		// 드라마 위시리스트 설정 mock
		doAnswer(invocation -> {
			List<SearchTvResultsDto> tvResultList = invocation.getArgument(0);
			SearchTvResultsDto tvDto = tvResultList.getFirst();
			tvDto.setWishlisted(true);
			return null;
		}).when(wishlistFlagSharedService).setWishlisted(
				eq(tvResults), 
				eq(contentMediaTypeList),
				eq(userId), 
				any(), 
				any(),
				eq(wishlistRepository));
		
		// 실제 메서드 호출
		service.setWishlistFromTvExceptAniResponse(searchTvResponse, userId, contentMediaType);

		// 검증
		if (StringUtils.equals(contentMediaType, ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode())) {
			assertThat(searchTvResponse.getDramaResults().getFirst().isWishlisted()).isTrue();
		} else if (StringUtils.equals(contentMediaType,
				ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode())) {
			assertThat(searchTvResponse.getDocumentaryResults().getFirst().isWishlisted()).isTrue();
		} else if (StringUtils.equals(contentMediaType,
				ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode())) {
			assertThat(searchTvResponse.getKidsResults().getFirst().isWishlisted()).isTrue();
		} else if (StringUtils.equals(contentMediaType,
				ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode())) {
			assertThat(searchTvResponse.getNewsResults().getFirst().isWishlisted()).isTrue();
		} else if (StringUtils.equals(contentMediaType,
				ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode())) {
			assertThat(searchTvResponse.getVarietyResults().getFirst().isWishlisted()).isTrue();
		} else {
			// 결과 없음 케이스
			assertThat(tvResults).isEmpty();
			// 로그 메시지 검증
			List<ILoggingEvent> logsList = listAppender.list;
			boolean logFound = logsList.stream()
					.map(ILoggingEvent::getFormattedMessage)
					.anyMatch(event -> event.contains(errorMessage));
			assertThat(logFound).isTrue();
			verify(messageUtil, times(1))
					.getMessageKO(DomainMessagesWarnEnum.WARN_SEARCH_WRONG_CONTENT_MEDIA_TYPE.getMessageCode(), params);
			verify(wishlistFlagSharedService, times(0)).setWishlisted(
					eq(tvResults), 
					eq(contentMediaTypeList),
					eq(userId), 
					any(),
					any(), 
					eq(wishlistRepository));
			return;
		}
		verify(wishlistFlagSharedService, times(1)).setWishlisted(
				eq(tvResults), 
				eq(contentMediaTypeList),
				eq(userId), 
				any(),
				any(), 
				eq(wishlistRepository));
	}

	/**
	 * 애니 제외 TV 시리즈에 해당하는 contentMediaType 파라미터 제공
	 * @return
	 */
	static Stream<Arguments> contentMediaTypeParams() {
		Long userId = 1L;
		SearchTvResponseDto searchTvResponse = new SearchTvResponseDto();
		List<SearchTvResultsDto> dramaResults = new ArrayList<>();
		SearchTvResultsDto dramaResult = new SearchTvResultsDto();
		dramaResult.setId(1102);
		dramaResult.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode());
		dramaResults.add(dramaResult);
		List<SearchTvResultsDto> documentaryResults = new ArrayList<>();
		SearchTvResultsDto documentaryResult = new SearchTvResultsDto();
		documentaryResult.setId(1103);
		documentaryResult.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode());
		documentaryResults.add(documentaryResult);
		List<SearchTvResultsDto> kidsResults = new ArrayList<>();
		SearchTvResultsDto kidsResult = new SearchTvResultsDto();
		kidsResult.setId(1104);
		kidsResult.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode());
		kidsResults.add(kidsResult);
		List<SearchTvResultsDto> newsResults = new ArrayList<>();
		SearchTvResultsDto newsResult = new SearchTvResultsDto();
		newsResult.setId(1105);
		newsResult.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode());
		newsResults.add(newsResult);
		List<SearchTvResultsDto> varietyResults = new ArrayList<>();
		SearchTvResultsDto varietyResult = new SearchTvResultsDto();
		varietyResult.setId(1106);
		varietyResult.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode());
		varietyResults.add(varietyResult);
		searchTvResponse.setDramaResults(dramaResults);
		searchTvResponse.setDocumentaryResults(documentaryResults);
		searchTvResponse.setKidsResults(kidsResults);
		searchTvResponse.setNewsResults(newsResults);
		searchTvResponse.setVarietyResults(varietyResults);
		return Stream.of(
				Arguments.of(searchTvResponse, userId, ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode()),
				Arguments.of(searchTvResponse, userId, ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode()),
				Arguments.of(searchTvResponse, userId, ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode()),
				Arguments.of(searchTvResponse, userId, ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode()),
				Arguments.of(searchTvResponse, userId, ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode()),
				Arguments.of(new SearchTvResponseDto(), userId, "9999") // 결과 없음 케이스
				);
	}
	
	@Test
	@DisplayName("[UT]setWishlistFromMovieResponse: 영화 검색 결과에 위시리스트 여부 설정")
	void test_setWishlistFromMovieResponse() {

		SearchMovieResponseDto searchMovieResponse = new SearchMovieResponseDto();
		Long userId = 1L;

		List<SearchMovieResultsDto> movieResults = new ArrayList<>();
		SearchMovieResultsDto movieResult = new SearchMovieResultsDto();
		movieResult.setId(1201);
		movieResult.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode());
		movieResults.add(movieResult);
		searchMovieResponse.setMovieResults(movieResults);
		
		// 영화 위시리스트 설정 mock
		doAnswer(invocation -> {
			List<SearchMovieResultsDto> movieResultList = invocation.getArgument(0);
			SearchMovieResultsDto movieDto = movieResults.getFirst();
			movieDto.setWishlisted(true);
			movieResultList.add(movieDto);
			return null;
		})
		.when(wishlistFlagSharedService)
		.setWishlisted(
				eq(movieResults), 
				eq(List.of(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode())), 
				eq(userId), 
				any(), 
				any(), 
				eq(wishlistRepository));
		
		// 실제 메서드 호출
		service.setWishlistFromMovieResponse(searchMovieResponse, userId);

		// 검증
		assertThat(movieResults.getFirst().isWishlisted()).isTrue();
	}
	
	@Test
	@DisplayName("[UT]setWishlistFromComicsResponse: 만화 검색 결과에 위시리스트 여부 설정")
	void test_setWishlistFromComicsResponse() {

		SearchComicsResponseDto searchComicsResponse = new SearchComicsResponseDto();
		Long userId = 1L;

		List<SearchComicsResultDto> comicsResults = new ArrayList<>();
		SearchComicsResultDto comicsResult = new SearchComicsResultDto();
		comicsResult.setId(2101);
		comicsResult.setContentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_COMICS.getContentMediaTypeCode());
		comicsResults.add(comicsResult);
		searchComicsResponse.setComicsResults(comicsResults);
		
		// 영화 위시리스트 설정 mock
		doAnswer(invocation -> {
			List<SearchComicsResultDto> comicsResultList = invocation.getArgument(0);
			SearchComicsResultDto comicsDto = comicsResults.getFirst();
			comicsDto.setWishlisted(true);
			comicsResultList.add(comicsDto);
			return null;
		})
		.when(wishlistFlagSharedService)
		.setWishlisted(
				eq(comicsResults), 
				eq(List.of(ContentMediaTypeEnum.MEDIA_TYPE_COMICS.getContentMediaTypeCode())), 
				eq(userId), 
				any(), 
				any(), 
				eq(wishlistRepository));
		
		// 실제 메서드 호출
		service.setWishlistFromComicsResponse(searchComicsResponse, userId);

		// 검증
		assertThat(comicsResults.getFirst().isWishlisted()).isTrue();
	}

}
