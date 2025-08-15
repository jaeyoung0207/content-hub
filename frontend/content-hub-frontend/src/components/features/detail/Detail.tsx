import {
  MEDIA_TYPE,
  IS_MOBILE,
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_300,
  COMMON_IMAGES,
} from '@/components/common/constants/constants';
import { memo } from 'react';
import { useTranslation } from 'react-i18next';
import { VideoInformation } from './tabs/contentInformation/VideoInformation';
import { useDetail } from './useDetail';
import { ContentComment } from './tabs/contentComment/ContentComment';
import { useParams, useSearchParams } from 'react-router-dom';
import { LoadingUi } from '@/components/ui/LoadingUi';
import { RecommendContent } from './tabs/recommendationContent/RecommendationContent';
import {
  commonErrorHandler,
  isDetailComicsType,
  isDetailMovieType,
  isDetailTvType,
} from '@/components/common/utils/commonUtil';
import { ComicsInfomation } from './tabs/contentInformation/ComicsInformation';

/**
 * 상세 화면 컴포넌트
 * @param originalMediaType 원본 미디어 타입
 * @param contentId 컨텐츠 ID
 * @param tabNo 탭 번호
 */
export const Detail = memo(() => {
  // URL 파라미터에서 값을 가져오는 useParams 훅
  const { originalMediaType } = useParams();
  const { contentId } = useParams();

  // URL query string 값을 가져오는 useSearchParams 훅
  const [searchParams, setSearchParams] = useSearchParams();

  // 탭 번호, 없으면 0으로 초기화
  const tabNo = Number(searchParams.get('tabNo') ?? 0);

  // useDetail 훅을 사용하여 상세 정보 조회
  const { tabIndex, setTabIndex, data, isLoading, isError, userStarRating } =
    useDetail(originalMediaType!, contentId!, tabNo);

  // i18n 훅
  const { t } = useTranslation();

  // 탭 ID 매핑
  const tabId = {
    mediaInfo: 0,
    cast: 1,
    crew: 2,
    review: 3,
    recommendation: 4,
  };

  // 탭 정보 배열
  const tabInfo = [
    {
      id: tabId.mediaInfo,
      tabTitle: t('info.mediaInfo'),
    },
    // {
    //   id: tabId.cast,
    //   tabTitle: t('info.cast'),
    // },
    // {
    //   id: tabId.crew,
    //   tabTitle: t('info.crew'),
    // },
    {
      id: tabId.review,
      tabTitle: t('info.review'),
    },
    {
      id: tabId.recommendation,
      tabTitle: t('info.recommend'),
    },
  ];

  // 작품 타입에 따라 조건부 렌더링을 위한 변수 설정
  const isTvType =
    data && originalMediaType && isDetailTvType(data, originalMediaType);
  const isMovieType =
    data && originalMediaType && isDetailMovieType(data, originalMediaType);
  const isComicsType =
    data && originalMediaType && isDetailComicsType(data, originalMediaType);

  return (
    <>
      <div className="mt-30 mb-10">
        {/* 탭 버튼 */}
        <div className="mb-10 flex justify-center items-center">
          {
            // 탭 정보 배열 수만큼 반복하여 탭 버튼 생성
            tabInfo.map((tabInfo, index) => {
              const isActive = tabIndex === tabInfo.id;
              return (
                <div
                  key={index}
                  className={`text-sm sm:text-lg md:text-xl px-4 py-2 mx-2 transition duration-200
                                            ${
                                              isActive
                                                ? 'font-bold border-b-4 border-blue-500 text-blue-600'
                                                : 'text-gray-500 hover:text-blue-500 hover:border-b-4 hover:border-blue-300'
                                            }`}
                >
                  <button
                    role="tab"
                    className={
                      'text-2xl cursor-pointer ' +
                      (tabIndex === tabInfo.id && 'font-bold')
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
            isError && <div className="mt-60 text-3xl">{t('warn.noData')}</div>
          )
        }
        <div className="flex justify-center m-5">
          {data && originalMediaType && (
            <>
              {/* 작품 이미지 */}
              <div className="flex justify-center items-center mb-4 w-[30%]">
                <img
                  src={
                    originalMediaType === MEDIA_TYPE.COMICS
                      ? data?.posterPath
                      : TMDB_API_IMAGE_DOMAIN +
                        (IS_MOBILE ? WIDTH_300 : WIDTH_300) +
                        data?.posterPath // TODO
                  }
                  className={
                    (IS_MOBILE ? 'w-[200px]' : 'w-[300px]') + ' h-full'
                  }
                  onError={(e) => {
                    e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                  }}
                  alt={
                    isTvType
                      ? data.name
                      : isMovieType
                        ? data.title
                        : isComicsType
                          ? data.title
                          : ''
                  }
                />
              </div>
              {/* 제목 */}
              <div className="w-[70%] block">
                <div className="text-2xl mb-3 mr-3">
                  {(isTvType && data.name) ||
                    (isMovieType && data.title) ||
                    (isComicsType && data.title)}
                </div>
                <div className="flex">
                  <div className="w-full">
                    <ul className="mt-5 mb-5">
                      {/* 장르 */}
                      <li className="mb-2 flex">
                        <div className="mr-2">
                          {t('info.genre') + t('info.colon')}
                        </div>
                        <div>
                          {(isTvType || isMovieType) &&
                            data.genres?.map(
                              (genre, index) =>
                                genre.name +
                                (index + 1 !== data.genres?.length ? ', ' : '')
                            )}
                          {isComicsType &&
                            data.comicsGenres?.map(
                              (genre, index) =>
                                genre +
                                (index + 1 !== data.comicsGenres?.length
                                  ? ', '
                                  : '')
                            )}
                        </div>
                      </li>
                      {/* 연령 제한 */}
                      {data.adult && (
                        <li className="mb-2 flex">
                          <div className="mr-2">
                            {t('info.movieRating') + t('info.colon')}
                          </div>
                          <div>{t('info.adultContent')}</div>
                        </li>
                      )}
                      {/* 방영 시간 OR 총 권 수 */}
                      <li className="mb-2 flex">
                        <div className="mr-2">
                          {((isTvType && t('info.tvRunningTime')) ||
                            (isMovieType && t('info.movieRunningTime')) ||
                            (isComicsType && t('info.volumes'))) +
                            t('info.colon')}
                        </div>
                        <div>
                          {(isTvType &&
                            data.episodeRunTime + t('info.minutes')) ||
                            (isMovieType && data.runtime + t('info.minutes')) ||
                            (isComicsType &&
                              (data.status === 'RELEASING'
                                ? t('info.notEndedYet')
                                : data.volumes + t('info.volume')))}
                        </div>
                      </li>
                      {/* 방영시작일 OR 연재시작일 */}
                      <li className="mb-2 flex">
                        <div className="mr-2">
                          {((isTvType && t('info.tvReleaseDate')) ||
                            (isMovieType && t('info.movieReleaseDate')) ||
                            (isComicsType && t('info.serializeDate'))) +
                            t('info.colon')}
                        </div>
                        <div>
                          {(isTvType && data.firstAirDate) ||
                            (isMovieType && data.releaseDate) ||
                            (isComicsType && data.startDate)}
                        </div>
                      </li>
                      {/* 시즌 수 */}
                      {isTvType && data.seasons && (
                        <li className="mb-2 flex">
                          <div className="mr-2">
                            {t('info.seasonNumbers') + t('info.colon')}
                          </div>
                          <div>{data.seasons?.length + t('info.season')}</div>
                        </li>
                      )}
                      {/* 방영 상태 OR 연재 상태 */}
                      <li className="mb-2 flex">
                        <div className="mr-2">
                          {((isTvType && t('info.tvReleaseStatus')) ||
                            (isMovieType && t('info.movieReleaseStatus')) ||
                            (isComicsType && t('info.serializeStatus'))) +
                            t('info.colon')}
                        </div>
                        <div>
                          {(isTvType &&
                            (data.status === 'Ended'
                              ? t('info.ended')
                              : t('info.onAir'))) ||
                            (isMovieType &&
                              (data.status === 'Released'
                                ? t('info.released')
                                : t('info.planned'))) ||
                            (isComicsType &&
                              (data.status === 'FINISHED'
                                ? t('info.finished')
                                : t('info.releasing')))}
                        </div>
                      </li>
                      {/* 홈페이지 */}
                      {data.homepage && (
                        <li className="mb-2 flex">
                          <div className="mr-2">
                            {t('info.homepage') + t('info.colon')}
                          </div>
                          <div>
                            <a
                              className="text-blue-600"
                              href={data.homepage}
                              target="_blank"
                            >
                              {data.homepage}
                            </a>
                          </div>
                        </li>
                      )}
                      {/* 유저 평점 */}
                      <li className="mb-2 flex">
                        <div className="mr-2">
                          {t('info.userStarRating') + t('info.colon')}
                        </div>
                        <div>
                          {userStarRating ? userStarRating : t('info.notExist')}
                        </div>
                      </li>
                      {/* 볼 수 있는 곳 */}
                      {isTvType ||
                        (isMovieType && (
                          <li className="mb-2 flex">
                            <div className="mr-2">
                              {t('info.ableToWatching') + t('info.colon')}
                            </div>
                            <div>
                              {data.link ? (
                                <a
                                  className="text-blue-600"
                                  href={data.link}
                                  target="_blank"
                                >
                                  {data.link}
                                </a>
                              ) : (
                                t('info.unknown')
                              )}
                            </div>
                          </li>
                        ))}
                    </ul>
                  </div>
                </div>
              </div>
            </>
          )}
        </div>

        {/* 탭 내용 */}
        <div className="mt-5 p-4">
          {data && originalMediaType && (
            <>
              {tabIndex === tabId.mediaInfo && (
                <div>
                  {originalMediaType === MEDIA_TYPE.COMICS && (
                    <ComicsInfomation
                      detailResult={data}
                      originalMediaType={originalMediaType}
                    />
                  )}
                  {(originalMediaType === MEDIA_TYPE.ANI ||
                    originalMediaType === MEDIA_TYPE.DRAMA ||
                    originalMediaType === MEDIA_TYPE.MOVIE) && (
                    <VideoInformation
                      detailResult={data}
                      originalMediaType={originalMediaType}
                    />
                  )}
                </div>
              )}
              {tabIndex === tabId.review && (
                <div>
                  {/* 평가&리뷰 */}
                  <ContentComment
                    detailResult={data}
                    originalMediaType={originalMediaType}
                  />
                </div>
              )}
              {tabIndex === tabId.recommendation && (
                <div>
                  {/* 비슷한 작품 */}
                  <RecommendContent
                    detailResult={data}
                    originalMediaType={originalMediaType}
                  />
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </>
  );
});
