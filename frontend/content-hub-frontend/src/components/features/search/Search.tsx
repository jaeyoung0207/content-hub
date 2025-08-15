import { memo } from 'react';
import { useTranslation } from 'react-i18next';
import { SearchCommonResultListType, useSearch } from './useSearch';
import { Link, useNavigate } from 'react-router-dom';
import {
  MEDIA_TYPE,
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_300,
  COMMON_IMAGES,
} from '@/components/common/constants/constants';
import { useSearchTypeStore } from '@/components/common/store/globalStateStore';
import { LoadingUi } from '@/components/ui/LoadingUi';
import { SearchPropsType } from './SearchPage';
import { NodataMessageUi } from '@/components/ui/common/NodataMessageUi';
import {
  checkContentId,
  commonErrorHandler,
  detailUrlQuery,
  isSearchTvType,
  viewMoreUrlQuery,
} from '@/components/common/utils/commonUtil';

/**
 * 각 미디어 검색결과 컴포넌트 props 타입
 */
export type SearchResultsPropsType = {
  mediaName: string; // 미디어 이름
  results: SearchCommonResultListType; // 검색 결과 리스트
  isViewMore?: boolean; // 전체보기 여부
  mediaType: string; // 미디어 타입
  keyword: string; // 검색어
  isAdult: string; // 성인물 포함 여부
};

/**
 * 검색 화면 컴포넌트
 * @param keyword 검색어
 * @param isAdult 성인물 포함 여부
 */
export const Search = ({ keyword, isAdult }: SearchPropsType) => {
  // i18n 번역 훅
  const { t } = useTranslation();
  // 검색 타입 상태 훅
  const { searchTypeState } = useSearchTypeStore();

  // 검색 훅 호출
  const { data, isLoading } = useSearch(keyword, isAdult!);

  // 검색결과에 따라 미디어 타입별로 결과를 분리
  const aniSearchResults = data?.videoResult?.aniResults;
  const dramaSearchResults = data?.videoResult?.dramaResults;
  const movieSearchResults = data?.videoResult?.movieResults;
  const comicsSearchResults = data?.comicsResult?.comicsResults;
  const isAniViewMore = data?.videoResult?.aniViewMore;
  const isDramaViewMore = data?.videoResult?.dramaViewMore;
  const isMovieViewMore = data?.videoResult?.movieViewMore;
  const isComicsViewMore = data?.comicsResult?.comicsViewMore;

  // 검색 결과 인자값 리스트
  const dataList = [
    {
      displayFlg: searchTypeState.aniFlg,
      dataResults: aniSearchResults,
      media: t('info.ani'),
      isViewMore: isAniViewMore,
      mediaType: MEDIA_TYPE.ANI,
    },
    {
      displayFlg: searchTypeState.dramaFlg,
      dataResults: dramaSearchResults,
      media: t('info.drama'),
      isViewMore: isDramaViewMore,
      mediaType: MEDIA_TYPE.DRAMA,
    },
    {
      displayFlg: searchTypeState.movieFlg,
      dataResults: movieSearchResults,
      media: t('info.movie'),
      isViewMore: isMovieViewMore,
      mediaType: MEDIA_TYPE.MOVIE,
    },
    {
      displayFlg: searchTypeState.comicsFlg,
      dataResults: comicsSearchResults,
      media: t('info.comics'),
      isViewMore: isComicsViewMore,
      mediaType: MEDIA_TYPE.COMICS,
    },
  ];

  return (
    <>
      <div className="mt-28 w-sm lg:w-7xl">
        {/* 검색 결과 */}
        {isLoading ? (
          <LoadingUi />
        ) : (
          data &&
          dataList.map((items, index) => {
            return (
              <div key={index}>
                {items.displayFlg &&
                  items.dataResults &&
                  items.dataResults.length !== 0 && (
                    // 각 미디어 검색결과 컴포넌트
                    <DisplayResults
                      mediaName={items.media}
                      results={items.dataResults}
                      isViewMore={items.isViewMore}
                      mediaType={items.mediaType}
                      keyword={keyword!}
                      isAdult={isAdult!}
                    />
                  )}
              </div>
            );
          })
        )}
        {/* 검색 결과가 없을 때 표시할 메시지 */}
        {aniSearchResults?.length === 0 &&
          dramaSearchResults?.length === 0 &&
          movieSearchResults?.length === 0 &&
          comicsSearchResults?.length === 0 && (
            <NodataMessageUi message={t('warn.noSearchData')} />
          )}
        {/* 검색 전 메세지 */}
        {aniSearchResults === undefined &&
          dramaSearchResults === undefined &&
          movieSearchResults === undefined &&
          comicsSearchResults === undefined && (
            <div className="mt-25 lg:mt-60 flex justify-center items-center text-black text-xl lg:text-2xl font-normal font-['Inter']">
              {t('info.beforeSearchMessage')}
            </div>
          )}
      </div>
    </>
  );
};

/**
 * 각 미디어 검색결과 컴포넌트
 */
const DisplayResults = memo(
  ({
    mediaName,
    results,
    isViewMore,
    mediaType,
    keyword,
    isAdult,
  }: SearchResultsPropsType) => {
    // i18n 번역 훅
    const { t } = useTranslation();
    // navigate 훅
    const navigate = useNavigate();
    // 검색 화면에서 사용할 썸네일 이미지 경로
    const thumbnailImagePath = TMDB_API_IMAGE_DOMAIN + WIDTH_300;

    return (
      <div>
        {/* 미디어 이름 */}
        <div className="ml-6 mt-6 flex justify-between items-center">
          <div className="text-4xl font-bold">{mediaName}</div>
          {
            // 전체보기 링크
            isViewMore && (
              <div className="text-xl hover:font-bold">
                <Link
                  to={viewMoreUrlQuery({
                    keyword: keyword,
                    isAdult: isAdult,
                    mediaType: mediaType,
                  })}
                >
                  {t('info.viewMore')} &gt;
                </Link>
              </div>
            )
          }
        </div>
        {/* 검색 결과 */}
        <div className="w-full flex flex-wrap items-start mt-5 ml-5">
          {results.length !== 0 &&
            results.map((items, index) => {
              return (
                <ul
                  key={'frame' + index}
                  className={
                    'ml-1 mr-1 block hover:font-bold cursor-pointer ' +
                    (mediaType === MEDIA_TYPE.COMICS
                      ? 'w-[200px]'
                      : 'w-[300px]')
                  }
                  onClick={commonErrorHandler(() => {
                    // contentId 체크
                    checkContentId(items.id);
                    // 상세화면 URL 생성
                    const detailUrl = detailUrlQuery({
                      originalMediaType: items.originalMediaType!,
                      contentId: String(items.id),
                      tabNo: 0,
                    });
                    // 상세화면 URL 저장
                    sessionStorage.setItem('redirectUrl', detailUrl);
                    // 상세화면 이동
                    navigate(detailUrl);
                  })}
                >
                  {/* 썸네일 */}
                  <li
                    key={'poster_path' + index}
                    className="flex justify-center items-center"
                  >
                    <img
                      src={
                        mediaType === MEDIA_TYPE.COMICS
                          ? items.backdropPath
                          : items.backdropPath
                            ? thumbnailImagePath + items.backdropPath
                            : thumbnailImagePath + items.posterPath
                      }
                      onError={(e) => {
                        e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                      }}
                      className={
                        (mediaType === MEDIA_TYPE.COMICS
                          ? 'w-[190px] h-[270px]'
                          : 'w-full h-[180px]') + ' object-scale-down'
                      }
                      alt={'Thumbnail Image'}
                    />
                  </li>
                  {/* 제목 */}
                  <li key={'title' + index} className="ml-1 mr-1 mb-4 text-lg">
                    {isSearchTvType(items, mediaType)
                      ? items.name
                      : items.title}
                  </li>
                </ul>
              );
            })}
        </div>
      </div>
    );
  }
);
