package com.cjy.contenthub.home.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cjy.contenthub.core.constants.DomainEnum.ContentMediaTypeEnum;
import com.cjy.contenthub.core.constants.DomainEnum.DisplayMediaTypeEnum;
import com.cjy.contenthub.core.shared.service.WishlistSharedService;
import com.cjy.contenthub.home.mapper.HomeMapper;
import com.cjy.contenthub.home.repository.HomeRankingViewRepository;
import com.cjy.contenthub.home.repository.entity.HomeRankingViewEntity;
import com.cjy.contenthub.home.service.dto.HomeRankingListServiceDto;
import com.cjy.contenthub.home.service.dto.HomeRankingServiceDto;
import com.cjy.contenthub.wishlist.repository.WishlistRepository;

@ExtendWith(MockitoExtension.class)
class HomeServiceImplTest {
	
	HomeServiceImpl service;
	
	@Mock
	HomeRankingViewRepository homeRankingViewRepository;

	@Mock
	HomeMapper mapper;

	@Mock
	WishlistRepository wishlistRepository;

	@Mock
	WishlistSharedService wishlistFlagSharedService;
	
	@BeforeEach
	void setUp() {
        // 서비스 클래스 생성
		service = new HomeServiceImpl(
				homeRankingViewRepository,
				mapper,
				wishlistRepository,
				wishlistFlagSharedService
				);
	}
	
	@Test
	@DisplayName("[UT]getContentRankings: 콘텐츠 랭킹 정보를 조회 - 유저 ID 존재")
	void test_getContentRankings_existUserId() {
		
		Long userId = 1L;
		
		List<HomeRankingViewEntity> rankingEntityList = new ArrayList<>();
		HomeRankingViewEntity aniEntity = HomeRankingViewEntity.builder()
				.apiId("1")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_ANI.getDisplayMediaTypeCode())
				.build();
		HomeRankingViewEntity dramaEntity = HomeRankingViewEntity.builder()
				.apiId("2")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_DRAMA.getDisplayMediaTypeCode())
				.build();
		HomeRankingViewEntity documentory = HomeRankingViewEntity.builder()
				.apiId("3")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getDisplayMediaTypeCode())
				.build();
		HomeRankingViewEntity kids = HomeRankingViewEntity.builder()
				.apiId("4")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_KIDS.getDisplayMediaTypeCode())
				.build();
		HomeRankingViewEntity news = HomeRankingViewEntity.builder()
				.apiId("5")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_NEWS.getDisplayMediaTypeCode())
				.build();
		HomeRankingViewEntity variety = HomeRankingViewEntity.builder()
				.apiId("6")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_VARIETY.getDisplayMediaTypeCode())
				.build();
		HomeRankingViewEntity movieEntity = HomeRankingViewEntity.builder()
				.apiId("7")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_MOVIE.getDisplayMediaTypeCode()).build();
		HomeRankingViewEntity comicsEntity = HomeRankingViewEntity.builder()
				.apiId("8")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_COMICS.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_COMICS.getDisplayMediaTypeCode()).build();
		rankingEntityList.add(aniEntity);
		rankingEntityList.add(dramaEntity);
		rankingEntityList.add(documentory);
		rankingEntityList.add(kids);
		rankingEntityList.add(news);
		rankingEntityList.add(variety);
		rankingEntityList.add(movieEntity);
		rankingEntityList.add(comicsEntity);
		when(homeRankingViewRepository.findAll()).thenReturn(rankingEntityList);
		
		List<HomeRankingServiceDto> serviceList = new ArrayList<>();
		HomeRankingServiceDto aniDto = HomeRankingServiceDto.builder()
				.apiId(aniEntity.getApiId())
				.contentMediaType(aniEntity.getContentMediaType())
				.displayMediaType(aniEntity.getDisplayMediaType()).build();
		HomeRankingServiceDto dramaDto = HomeRankingServiceDto.builder()
				.apiId(dramaEntity.getApiId()).contentMediaType(dramaEntity.getContentMediaType())
				.displayMediaType(dramaEntity.getDisplayMediaType()).build();
		HomeRankingServiceDto documentoryDto = HomeRankingServiceDto.builder()
				.apiId(documentory.getApiId()).contentMediaType(documentory.getContentMediaType())
				.displayMediaType(documentory.getDisplayMediaType()).build();
		HomeRankingServiceDto kidsDto = HomeRankingServiceDto.builder()
				.apiId(kids.getApiId()).contentMediaType(kids.getContentMediaType())
				.displayMediaType(kids.getDisplayMediaType()).build();
		HomeRankingServiceDto newsDto = HomeRankingServiceDto.builder()
				.apiId(news.getApiId()).contentMediaType(news.getContentMediaType())
				.displayMediaType(news.getDisplayMediaType()).build();
		HomeRankingServiceDto varietyDto = HomeRankingServiceDto.builder()
				.apiId(variety.getApiId()).contentMediaType(variety.getContentMediaType())
				.displayMediaType(variety.getDisplayMediaType()).build();
		HomeRankingServiceDto movieDto = HomeRankingServiceDto.builder()
				.apiId(movieEntity.getApiId()).contentMediaType(movieEntity.getContentMediaType())
				.displayMediaType(movieEntity.getDisplayMediaType()).build();
		HomeRankingServiceDto comicsDto = HomeRankingServiceDto.builder()
				.apiId(comicsEntity.getApiId()).contentMediaType(comicsEntity.getContentMediaType())
				.displayMediaType(comicsEntity.getDisplayMediaType()).build();
		serviceList.add(aniDto);
		serviceList.add(dramaDto);
		serviceList.add(documentoryDto);
		serviceList.add(kidsDto);
		serviceList.add(newsDto);
		serviceList.add(varietyDto);
		serviceList.add(movieDto);
		serviceList.add(comicsDto);
		when(mapper.entityListToServiceList(rankingEntityList)).thenReturn(serviceList);
		
		doAnswer(invocation -> {
            List<HomeRankingServiceDto> dtoList = invocation.getArgument(0);
            for (HomeRankingServiceDto dto : dtoList) {
                dto.setWishlisted(true);
            }
            return null;
        }).when(wishlistFlagSharedService).setWishlisted(
                anyList(),
                anyList(),
                eq(userId),
                any(),
                any(),
                eq(wishlistRepository)
                );
		
		// 실제 서비스 메서드 호출
		HomeRankingListServiceDto result = service.getContentRankings(userId);
		
		// 결과 검증
		HomeRankingListServiceDto resultAniDto = HomeRankingListServiceDto.builder()
				.aniRankingList(List.of(aniDto))
				.dramaRankingList(List.of(dramaDto))
				.documentaryRankingList(List.of(documentoryDto))
				.kidsRankingList(List.of(kidsDto))
				.newsRankingList(List.of(newsDto))
				.varietyRankingList(List.of(varietyDto))
				.movieRankingList(List.of(movieDto))
				.comicsRankingList(List.of(comicsDto))
				.build();
		assertThat(result).usingRecursiveComparison().isEqualTo(resultAniDto);
		assertThat(result.getAniRankingList().get(0).isWishlisted()).isTrue();
		assertThat(result.getDramaRankingList().get(0).isWishlisted()).isTrue();
		assertThat(result.getDocumentaryRankingList().get(0).isWishlisted()).isTrue();
		assertThat(result.getKidsRankingList().get(0).isWishlisted()).isTrue();
		assertThat(result.getNewsRankingList().get(0).isWishlisted()).isTrue();
		assertThat(result.getVarietyRankingList().get(0).isWishlisted()).isTrue();
		assertThat(result.getMovieRankingList().get(0).isWishlisted()).isTrue();
		assertThat(result.getComicsRankingList().get(0).isWishlisted()).isTrue();
		
		verify(wishlistFlagSharedService, times(8))
		.setWishlisted(
				anyList(),
				anyList(),
				eq(userId), 
				any(), 
				any(), 
				eq(wishlistRepository));
	}
	
	@Test
	@DisplayName("[UT]getContentRankings: 콘텐츠 랭킹 정보를 조회 - 유저 ID 없음")
	void test_getContentRankings_notExistUserId() {
		
		Long userId = null;
		
		List<HomeRankingViewEntity> rankingEntityList = new ArrayList<>();
		HomeRankingViewEntity aniEntity = HomeRankingViewEntity.builder()
				.apiId("1")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_ANI.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_ANI.getDisplayMediaTypeCode())
				.build();
		HomeRankingViewEntity dramaEntity = HomeRankingViewEntity.builder()
				.apiId("2")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_DRAMA.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_DRAMA.getDisplayMediaTypeCode())
				.build();
		HomeRankingViewEntity documentory = HomeRankingViewEntity.builder()
				.apiId("3")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_DOCUMENTARY.getDisplayMediaTypeCode())
				.build();
		HomeRankingViewEntity kids = HomeRankingViewEntity.builder()
				.apiId("4")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_KIDS.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_KIDS.getDisplayMediaTypeCode())
				.build();
		HomeRankingViewEntity news = HomeRankingViewEntity.builder()
				.apiId("5")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_NEWS.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_NEWS.getDisplayMediaTypeCode())
				.build();
		HomeRankingViewEntity variety = HomeRankingViewEntity.builder()
				.apiId("6")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_VARIETY.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_VARIETY.getDisplayMediaTypeCode())
				.build();
		HomeRankingViewEntity movieEntity = HomeRankingViewEntity.builder()
				.apiId("7")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_MOVIE.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_MOVIE.getDisplayMediaTypeCode()).build();
		HomeRankingViewEntity comicsEntity = HomeRankingViewEntity.builder()
				.apiId("8")
				.contentMediaType(ContentMediaTypeEnum.MEDIA_TYPE_COMICS.getContentMediaTypeCode())
				.displayMediaType(DisplayMediaTypeEnum.MEDIA_TYPE_COMICS.getDisplayMediaTypeCode()).build();
		rankingEntityList.add(aniEntity);
		rankingEntityList.add(dramaEntity);
		rankingEntityList.add(documentory);
		rankingEntityList.add(kids);
		rankingEntityList.add(news);
		rankingEntityList.add(variety);
		rankingEntityList.add(movieEntity);
		rankingEntityList.add(comicsEntity);
		when(homeRankingViewRepository.findAll()).thenReturn(rankingEntityList);
		
		List<HomeRankingServiceDto> serviceList = new ArrayList<>();
		HomeRankingServiceDto aniDto1 = HomeRankingServiceDto.builder()
				.apiId(aniEntity.getApiId())
				.contentMediaType(aniEntity.getContentMediaType())
				.displayMediaType(aniEntity.getDisplayMediaType()).build();
		HomeRankingServiceDto aniDto2 = HomeRankingServiceDto.builder()
				.displayMediaType("99").build();
		HomeRankingServiceDto dramaDto = HomeRankingServiceDto.builder()
				.apiId(dramaEntity.getApiId()).contentMediaType(dramaEntity.getContentMediaType())
				.displayMediaType(dramaEntity.getDisplayMediaType()).build();
		HomeRankingServiceDto documentoryDto = HomeRankingServiceDto.builder()
				.apiId(documentory.getApiId()).contentMediaType(documentory.getContentMediaType())
				.displayMediaType(documentory.getDisplayMediaType()).build();
		HomeRankingServiceDto kidsDto = HomeRankingServiceDto.builder()
				.apiId(kids.getApiId()).contentMediaType(kids.getContentMediaType())
				.displayMediaType(kids.getDisplayMediaType()).build();
		HomeRankingServiceDto newsDto = HomeRankingServiceDto.builder()
				.apiId(news.getApiId()).contentMediaType(news.getContentMediaType())
				.displayMediaType(news.getDisplayMediaType()).build();
		HomeRankingServiceDto varietyDto = HomeRankingServiceDto.builder()
				.apiId(variety.getApiId()).contentMediaType(variety.getContentMediaType())
				.displayMediaType(variety.getDisplayMediaType()).build();
		HomeRankingServiceDto movieDto = HomeRankingServiceDto.builder()
				.apiId(movieEntity.getApiId()).contentMediaType(movieEntity.getContentMediaType())
				.displayMediaType(movieEntity.getDisplayMediaType()).build();
		HomeRankingServiceDto comicsDto = HomeRankingServiceDto.builder()
				.apiId(comicsEntity.getApiId()).contentMediaType(comicsEntity.getContentMediaType())
				.displayMediaType(comicsEntity.getDisplayMediaType()).build();
		serviceList.add(aniDto1);
		serviceList.add(aniDto2);
		serviceList.add(dramaDto);
		serviceList.add(documentoryDto);
		serviceList.add(kidsDto);
		serviceList.add(newsDto);
		serviceList.add(varietyDto);
		serviceList.add(movieDto);
		serviceList.add(comicsDto);
		when(mapper.entityListToServiceList(rankingEntityList)).thenReturn(serviceList);
		
		// 실제 서비스 메서드 호출
		HomeRankingListServiceDto result = service.getContentRankings(userId);
		
		// 결과 검증
		HomeRankingListServiceDto resultAniDto = HomeRankingListServiceDto.builder()
				.aniRankingList(List.of(aniDto1))
				.dramaRankingList(List.of(dramaDto))
				.documentaryRankingList(List.of(documentoryDto))
				.kidsRankingList(List.of(kidsDto))
				.newsRankingList(List.of(newsDto))
				.varietyRankingList(List.of(varietyDto))
				.movieRankingList(List.of(movieDto))
				.comicsRankingList(List.of(comicsDto))
				.build();
		assertThat(result).usingRecursiveComparison().isEqualTo(resultAniDto);
		
		verify(wishlistFlagSharedService, times(0))
		.setWishlisted(
				anyList(),
				anyList(),
				eq(userId), 
				any(), 
				any(), 
				eq(wishlistRepository));
	}
	

}
