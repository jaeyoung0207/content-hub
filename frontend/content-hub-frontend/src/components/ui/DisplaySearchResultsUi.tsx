import { useTranslation } from 'react-i18next';
import { RecommendationContentResultType } from '../features/detail/tabs/recommendationContent/useRecommendationContent';
import { SearchCommonResultType } from '../features/search/useSearch';
import { Link, useNavigate } from 'react-router-dom';
import { detailUrlQuery, viewMoreUrlQuery } from '../common/utils/urlUtil';
import {
  COMMON_IMAGES,
  MEDIA_TYPE,
  REDIRECT_URL,
  SEARCH_SCREEN_TYPE,
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_300,
} from '../common/constants/constants';
import { commonErrorHandler } from '../common/utils/errorUtil';
import { checkContentId } from '../common/utils/checkUtil';
import {
  isRecommendationsTvType,
  isSearchTvType,
} from '../common/utils/typeGuardUtil';
import { LazyImage } from './common/LazyImageUi';

/**
 * 각 미디어 검색결과 컴포넌트 props 타입
 */
export type DisplaySearchResultsPropsType = {
  mediaName?: string; // 미디어 이름
  results: SearchCommonResultType[] | RecommendationContentResultType[]; // 검색 결과 리스트
  isViewMore?: boolean; // 전체보기 여부
  mediaType: string; // 미디어 타입
  keyword: string; // 검색어
  isAdult: string; // 성인물 포함 여부
  searchScreenType: string; // 검색 화면 타입
};

/**
 * 각 미디어 검색결과 컴포넌트
 */
export const DisplaySearchResults = ({
  mediaName,
  results,
  isViewMore,
  mediaType,
  keyword,
  isAdult,
  searchScreenType,
}: DisplaySearchResultsPropsType) => {
  // i18n 번역 훅
  const { t } = useTranslation();
  // navigate 훅
  const navigate = useNavigate();
  // 검색 화면에서 사용할 썸네일 이미지 경로
  const thumbnailImagePath = TMDB_API_IMAGE_DOMAIN + WIDTH_300;

  return (
    <div>
      {searchScreenType === SEARCH_SCREEN_TYPE.MAIN && (
        // 미디어 이름
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
      )}
      {searchScreenType === SEARCH_SCREEN_TYPE.VIEW_MORE && (
        // 키워드 미디어 검색 결과
        <div className="text-3xl font-bold ml-5">
          "{keyword}" {mediaName} {t('info.searchResults')}
        </div>
      )}
      {/* 검색 결과 */}
      <div
        className={`"w-full flex flex-wrap items-start mt-6 " ${searchScreenType === SEARCH_SCREEN_TYPE.MAIN ? 'ml-5' : ''}`}
      >
        {results.length !== 0 &&
          results.map((items, index) => {
            return (
              <ul
                key={'frame' + index}
                className={
                  'ml-1 mr-1 block hover:font-bold cursor-pointer ' +
                  (mediaType === MEDIA_TYPE.COMICS ? 'w-[195px]' : 'w-[300px]')
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
                  sessionStorage.setItem(REDIRECT_URL, detailUrl);
                  // 상세화면 이동
                  navigate(detailUrl);
                })}
              >
                {/* 썸네일 */}
                <li
                  key={'poster_path' + index}
                  className="flex justify-center items-center"
                >
                  <LazyImage
                    src={
                      items.backdropPath
                        ? mediaType === MEDIA_TYPE.COMICS
                          ? items.backdropPath
                          : items.backdropPath
                            ? thumbnailImagePath + items.backdropPath
                            : thumbnailImagePath + items.posterPath
                        : COMMON_IMAGES.NO_IMAGE
                    }
                    alt={'Thumbnail Image'}
                    className={
                      (mediaType === MEDIA_TYPE.COMICS
                        ? 'max-w-full h-[270px]'
                        : 'max-w-full h-[180px]') + ' object-scale-down'
                    }
                  />
                </li>
                {/* 제목 */}
                <li key={'title' + index} className="ml-1 mr-1 mb-4 text-lg">
                  {isSearchTvType(items, mediaType) ||
                  isRecommendationsTvType(items, mediaType)
                    ? items.name
                    : items.title}
                </li>
              </ul>
            );
          })}
      </div>
    </div>
  );
};

export default DisplaySearchResults;
