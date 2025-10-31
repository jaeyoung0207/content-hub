import {
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_300,
  COMMON_IMAGES,
  DETAIL_TAB_ID,
  WIDTH_185,
  TV_RELEASE_STATUS,
  MOVIE_RELEASE_STATUS,
  COMICS_RELEASE_STATUS,
} from '@/components/common/constants/constants';
import { Suspense, lazy, memo } from 'react';
import { useTranslation } from 'react-i18next';
import { useDetail } from './useDetail';
import { useParams, useSearchParams } from 'react-router-dom';
import { LoadingUi } from '@/components/ui/common/LoadingUi';
import {
  isDetailComicsType,
  isDetailMovieType,
  isDetailTvType,
} from '@/components/common/utils/typeGuardUtil';
import { commonErrorHandler } from '@/components/common/utils/errorUtil';
import { LazyImage } from '@/components/ui/common/LazyImageUi';
import { WishlistUi } from '@/components/ui/WishlistUi';
import {
  convertDate,
  getContentMediaType,
} from '@/components/common/utils/convertUtil';
import { useIsMobile } from '@/components/common/hooks/useIsMobile';

// lazy loading
const DetailVideoInformation = lazy(
  () => import('./tabs/information/contentInformation/DetailVideoInformation')
);
const DetailComicsInformation = lazy(
  () => import('./tabs/information/contentInformation/DetailComicsInformation')
);
const DetailCastInformation = lazy(
  () => import('./tabs/information/creditsInformation/DetailCastInformation')
);
const DetailCrewInformation = lazy(
  () => import('./tabs/information/creditsInformation/DetailCrewInformation')
);
const DetailComments = lazy(() => import('./tabs/comments/DetailComments'));
const RecommendationContent = lazy(
  () => import('./tabs/recommendation/DetailRecommendation')
);

/**
 * 상세 화면 컴포넌트
 * @param contentMediaType 컨텐츠 미디어 타입
 * @param apiId API ID
 * @param tabNo 탭 번호
 */
export const Detail = memo(() => {
  // URL 파라미터에서 값을 가져오는 useParams 훅
  const { contentMediaType } = useParams();
  const { apiId } = useParams();

  // URL query string 값을 가져오는 useSearchParams 훅
  const [searchParams, setSearchParams] = useSearchParams();

  // 모바일 여부 훅
  const isMobile = useIsMobile();

  // 탭 번호, 없으면 0으로 초기화
  const tabNo = Number(searchParams.get('tabNo') ?? 0);

  // useDetail 훅을 사용하여 상세 정보 조회
  const {
    tabIndex,
    setTabIndex,
    data,
    isLoading,
    isError,
    userStarRating,
    user,
    setCastObserveTarget,
    setCrewObserveTarget,
    castDisplayCount,
    crewDisplayCount,
  } = useDetail(contentMediaType!, apiId!, tabNo);

  // i18n 훅
  const { t } = useTranslation();

  // 탭 정보 배열
  const tabInfo = [
    {
      id: DETAIL_TAB_ID.mediaInfo,
      tabTitle: t('info.mediaInfo'),
    },
    {
      id: DETAIL_TAB_ID.cast,
      tabTitle:
        contentMediaType === getContentMediaType().comicsCode
          ? t('info.characters')
          : t('info.cast'),
    },
    {
      id: DETAIL_TAB_ID.crew,
      tabTitle: t('info.crew'),
    },
    {
      id: DETAIL_TAB_ID.review,
      tabTitle: t('info.review'),
    },
    {
      id: DETAIL_TAB_ID.recommendation,
      tabTitle: t('info.recommend'),
    },
  ];
  // 작품 정보 스타일
  const detailInfoStyle = 'flex mb-2 break-all text-sm md:text-base lg:text-lg';
  // 소제목 스타일
  const subTitleStyle = 'mr-2 whitespace-nowrap font-medium text-foreground';

  // 작품 타입에 따라 조건부 렌더링을 위한 변수 설정
  const isTvType =
    data && contentMediaType && isDetailTvType(data, contentMediaType);
  const isMovieType =
    data && contentMediaType && isDetailMovieType(data, contentMediaType);
  const isComicsType =
    data && contentMediaType && isDetailComicsType(data, contentMediaType);
  // 작품 제목
  const title = isTvType
    ? data.name
    : isMovieType || isComicsType
      ? data.title
      : 'No Title';
  // 장르
  const videoGenres =
    (isTvType || isMovieType) &&
    data.genres?.map((genre) => genre.name).join(', ');
  const comicsGenres = isComicsType && data.comicsGenres?.join(', ');
  const genres = videoGenres || comicsGenres;
  // 방영 시간
  const tvRuntime =
    isTvType && data.episodeRunTime && data.episodeRunTime.length > 0
      ? data.episodeRunTime[0] + t('info.minutes')
      : undefined;
  // 상영 시간
  const movieRuntime =
    isMovieType && data.runtime ? data.runtime + t('info.minutes') : undefined;
  // 총 권수
  const comicsVolume =
    isComicsType && data.volumes
      ? data.status === 'RELEASING'
        ? t('info.notEndedYet')
        : data.volumes + t('info.volume')
      : undefined;
  // 출시일(방영 시작일, 개봉일, 연재 시작일)
  const releaseDateArray =
    (isTvType && data.firstAirDate?.split('-')) ||
    (isMovieType && data.releaseDate?.split('-')) ||
    (isComicsType && data.startDate?.split('/'));
  const idValidateDateArray =
    Array.isArray(releaseDateArray) &&
    releaseDateArray.length === 3 &&
    releaseDateArray.every((date) => !isNaN(Number(date)));
  const convertedReleaseDate = idValidateDateArray
    ? convertDate(
        Number(releaseDateArray[0]),
        Number(releaseDateArray[1]),
        Number(releaseDateArray[2])
      )
    : undefined;

  // 포스터 URL (세로형 포스터 비율)
  const posterSrc = data?.posterPath
    ? contentMediaType === getContentMediaType().comicsCode
      ? data.posterPath
      : TMDB_API_IMAGE_DOMAIN +
        (isMobile ? WIDTH_185 : WIDTH_300) +
        data.posterPath
    : COMMON_IMAGES.NO_IMAGE;

  return (
    <div className="pt-16 pb-10 sm:pt-20">
      {/* 탭 버튼 */}
      <div
        className="mb-8 flex items-center justify-center"
        role="tablist"
        aria-label={t('info.review')}
      >
        {
          // 탭 정보 배열 수만큼 반복하여 탭 버튼 생성
          tabInfo.map((tabInfo) => {
            const isActive = tabIndex === tabInfo.id;
            return (
              <div
                key={tabInfo.id}
                className={`mx-2 border-b-4 px-1 py-2 text-sm transition duration-200 sm:text-lg md:mx-4 md:text-xl ${isActive ? 'border-blue-600 font-bold text-blue-600' : 'border-transparent text-gray-500 hover:border-blue-300 hover:text-blue-500'} `}
              >
                <button
                  id={`tab-${tabInfo.id}`}
                  role="tab"
                  aria-selected={isActive}
                  aria-controls={`panel-${tabInfo.id}`}
                  className={
                    'cursor-pointer text-sm md:text-2xl ' +
                    (isActive ? 'font-bold' : '')
                  }
                  onClick={commonErrorHandler(() => {
                    // URL query string에 tabNo를 설정
                    searchParams.set('tabNo', String(tabInfo.id));
                    setSearchParams(searchParams);
                    // 탭 인덱스 상태 설정
                    setTabIndex(tabInfo.id);
                  })}
                >
                  {tabInfo.tabTitle}
                </button>
              </div>
            );
          })
        }
      </div>

      {/* 작품 공통 정보 */}
      {
        // 로딩 중이면 로딩 UI 표시, 에러가 발생하면 에러 메시지 표시
        isLoading ? (
          <LoadingUi />
        ) : (
          isError && (
            <div className="mt-20 flex justify-center text-3xl">
              {t('warn.noData')}
            </div>
          )
        )
      }
      {!isLoading && !isError && data && contentMediaType && (
        <div className="px-2 md:px-6 lg:px-8">
          <div className="grid grid-cols-1 gap-6 md:grid-cols-3">
            {/* 작품 이미지 */}
            <div className="flex items-center justify-center md:col-span-1">
              <div className="relative aspect-[3/4] w-3/4 md:w-full lg:w-4/5 xl:w-2/3 2xl:w-11/20">
                {/* <div className="relative mx-auto w-3/4 md:mx-0 md:w-full max-w-[360px] aspect-[2/3]"> */}
                <LazyImage
                  src={posterSrc}
                  alt={
                    isTvType
                      ? data.name
                      : isMovieType
                        ? data.title
                        : isComicsType
                          ? data.title
                          : ''
                  }
                  className={`h-full w-full rounded-2xl ${isComicsType ? 'bg-white object-contain' : 'object-cover'}`}
                />
                <div className="absolute right-3 bottom-2 z-10">
                  <WishlistUi
                    contentMediaType={contentMediaType!}
                    apiId={Number(data.id)}
                    title={title!}
                    userId={user?.userId}
                    isWishlisted={data.wishlisted!}
                    genreIds={data.genreIds}
                    thumbnailImageUrl={
                      data.backdropPath! ?? data.posterPath ?? ''
                    }
                  />
                </div>
              </div>
            </div>

            {
              // 모바일이 아니거나, 모바일이면서 미디어 정보 탭인 경우에만 작품 기본 정보 표시
              ((isMobile && tabIndex === DETAIL_TAB_ID.mediaInfo) ||
                !isMobile) && (
                // 작품 기본 정보
                <div className="md:col-span-2">
                  {/* 제목 */}
                  <h1 className="mb-5 line-clamp-2 text-2xl font-bold sm:text-3xl">
                    {title}
                  </h1>
                  {/* 상세 정보 */}
                  <ul className="mt-2 mb-5">
                    {/* 다른 제목(만화) */}
                    {isComicsType &&
                      data.synonyms &&
                      data.synonyms.length > 0 && (
                        <li className={detailInfoStyle}>
                          <div className={subTitleStyle}>
                            {t('info.otherTitles') + t('info.colon')}
                          </div>
                          <div>{data.synonyms.join(', ')}</div>
                        </li>
                      )}
                    {/* 장르 */}
                    {genres && (
                      <li className={detailInfoStyle}>
                        <div className={subTitleStyle}>
                          {t('info.genre') + t('info.colon')}
                        </div>
                        <div>{genres}</div>
                      </li>
                    )}
                    {/* 연령 제한 */}
                    {data.adult && (
                      <li className={detailInfoStyle}>
                        <div className={subTitleStyle}>
                          {t('info.movieRating') + t('info.colon')}
                        </div>
                        <div>{t('info.adultContent')}</div>
                      </li>
                    )}
                    {/* 방영시작일 OR 개봉일 OR 연재시작일 */}
                    {convertedReleaseDate && (
                      <li className={detailInfoStyle}>
                        <div className={subTitleStyle}>
                          {((isTvType && t('info.tvReleaseDate')) ||
                            (isMovieType && t('info.movieReleaseDate')) ||
                            (isComicsType && t('info.serializeDate'))) +
                            t('info.colon')}
                        </div>
                        <div>{convertedReleaseDate}</div>
                      </li>
                    )}
                    {/* 총 권수(만화) */}
                    {comicsVolume && (
                      <li className={detailInfoStyle}>
                        <div className={subTitleStyle}>
                          {t('info.volumes') + t('info.colon')}
                        </div>
                        <div>{comicsVolume}</div>
                      </li>
                    )}
                    {/* 시즌 수(TV) */}
                    {isTvType && data.numberOfSeasons && (
                      <li className={detailInfoStyle}>
                        <div className={subTitleStyle}>
                          {t('info.seasonNumbers') + t('info.colon')}
                        </div>
                        <div>{data.numberOfSeasons + t('info.season')}</div>
                      </li>
                    )}
                    {/* 총 에피소드 수(TV) */}
                    {isTvType && data.numberOfEpisodes && (
                      <li className={detailInfoStyle}>
                        <div className={subTitleStyle}>
                          {t('info.totalEpisodeNumbers') + t('info.colon')}
                        </div>
                        <div>{data.numberOfEpisodes + t('info.episode')}</div>
                      </li>
                    )}
                    {/* 방영 시간 OR 상영 시간 */}
                    {(tvRuntime || movieRuntime) && (
                      <li className={detailInfoStyle}>
                        <div className={subTitleStyle}>
                          {((tvRuntime && t('info.tvRunningTime')) ||
                            (movieRuntime && t('info.movieRunningTime'))) +
                            t('info.colon')}
                        </div>
                        <div>{tvRuntime || movieRuntime}</div>
                      </li>
                    )}
                    {/* 방영 상태 OR 상영 상태 OR 연재 상태 */}
                    {data.status && (
                      <li className={detailInfoStyle}>
                        <div className={subTitleStyle}>
                          {((isTvType && t('info.tvReleaseStatus')) ||
                            (isMovieType && t('info.movieReleaseStatus')) ||
                            (isComicsType && t('info.comicsReleaseStatus'))) +
                            t('info.colon')}
                        </div>
                        <div>
                          {(isTvType &&
                            (TV_RELEASE_STATUS[data.status] ??
                              t('info.unknown'))) ||
                            (isMovieType &&
                              (MOVIE_RELEASE_STATUS[data.status] ??
                                t('info.unknown'))) ||
                            (isComicsType &&
                              (COMICS_RELEASE_STATUS[data.status] ??
                                t('info.unknown')))}
                        </div>
                      </li>
                    )}
                    {/* 홈페이지 */}
                    {data.homepage && (
                      <li className={detailInfoStyle}>
                        <div className={subTitleStyle}>
                          {t('info.homepage') + t('info.colon')}
                        </div>
                        <div>
                          <a
                            className="text-blue-600 underline"
                            href={data.homepage}
                            target="_blank"
                            rel="noopener noreferrer"
                          >
                            {data.homepage}
                          </a>
                        </div>
                      </li>
                    )}
                    {/* 유저 평점 */}
                    <li className={detailInfoStyle}>
                      <div className={subTitleStyle}>
                        {t('info.userStarRating') + t('info.colon')}
                      </div>
                      <div>
                        {userStarRating ? userStarRating : t('info.notExist')}
                      </div>
                    </li>
                    {/* 볼 수 있는 곳 */}
                    {(isTvType || isMovieType) && data.link && (
                      <li className={detailInfoStyle}>
                        <div className={subTitleStyle}>
                          {t('info.ableToWatching') + t('info.colon')}
                        </div>
                        <div>
                          <a
                            className="text-blue-600 underline"
                            href={data.link}
                            target="_blank"
                            rel="noopener noreferrer"
                          >
                            {data.link}
                          </a>
                        </div>
                      </li>
                    )}
                  </ul>
                </div>
              )
            }
          </div>
        </div>
      )}

      {/* 탭 내용 */}
      {data && contentMediaType && (
        <div className="mt-4 px-2 md:px-6 lg:mt-10 lg:px-8">
          {tabIndex === DETAIL_TAB_ID.mediaInfo && (
            <Suspense fallback={<LoadingUi />}>
              <div>
                {/* 만화 정보 */}
                {contentMediaType === getContentMediaType().comicsCode ? (
                  <DetailComicsInformation
                    detailResult={data}
                    contentMediaType={contentMediaType}
                  />
                ) : (
                  <DetailVideoInformation
                    detailResult={data}
                    contentMediaType={contentMediaType}
                  />
                )}
              </div>
            </Suspense>
          )}
          {tabIndex === DETAIL_TAB_ID.cast && (
            <Suspense fallback={<LoadingUi />}>
              {/* 출연진 */}
              <DetailCastInformation
                detailResult={data}
                contentMediaType={contentMediaType}
                setObserveTarget={setCastObserveTarget}
                displayCount={castDisplayCount}
              />
            </Suspense>
          )}
          {tabIndex === DETAIL_TAB_ID.crew && (
            <Suspense fallback={<LoadingUi />}>
              {/* 제작진 */}
              <DetailCrewInformation
                detailResult={data}
                contentMediaType={contentMediaType}
                setObserveTarget={setCrewObserveTarget}
                displayCount={crewDisplayCount}
              />
            </Suspense>
          )}
          {tabIndex === DETAIL_TAB_ID.review && (
            <Suspense fallback={<LoadingUi />}>
              {/* 평가&리뷰 */}
              <DetailComments
                detailResult={data}
                contentMediaType={contentMediaType}
              />
            </Suspense>
          )}
          {tabIndex === DETAIL_TAB_ID.recommendation && (
            <Suspense fallback={<LoadingUi />}>
              {/* 비슷한 작품 */}
              <RecommendationContent
                detailResult={data}
                contentMediaType={contentMediaType}
              />
            </Suspense>
          )}
        </div>
      )}
    </div>
  );
});

export default Detail;
