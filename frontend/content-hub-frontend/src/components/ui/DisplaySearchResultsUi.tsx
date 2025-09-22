import { useTranslation } from 'react-i18next';
import { RecommendationContentResultType } from '../features/detail/tabs/recommendationContent/useRecommendationContent';
import { SearchCommonResultType } from '../features/search/useSearch';
import { Link, useNavigate } from 'react-router-dom';
import { detailUrlQuery, viewMoreUrlQuery } from '../common/utils/urlUtil';
import {
  COMMON_IMAGES,
  MEDIA_TYPE,
  SEARCH_SCREEN_TYPE,
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_300,
} from '../common/constants/constants';
import { commonErrorHandler } from '../common/utils/errorUtil';
import { checkApiId } from '../common/utils/checkUtil';
import {
  isRecommendationsTvType,
  isSearchTvType,
} from '../common/utils/typeGuardUtil';
import { LazyImage } from './common/LazyImageUi';
import { highlightHoverColor } from '../common/constants/tailwindStyles';
import { WishlistUi } from './WishlistUi';
import { useUserStore } from '../common/store/globalStateStore';

/**
 * 각 미디어 검색결과 컴포넌트 props 타입
 */
export type DisplaySearchResultsPropsType = {
  mediaName?: string; // 미디어 이름
  results: SearchCommonResultType[] | RecommendationContentResultType[]; // 검색 결과 리스트
  isViewMore?: boolean; // 전체보기 여부
  displayMediaType: string; // 화면 표시용 미디어 타입
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
  displayMediaType,
  keyword,
  isAdult,
  searchScreenType,
}: DisplaySearchResultsPropsType) => {
  // i18n 번역 훅
  const { t } = useTranslation();
  // navigate 훅
  const navigate = useNavigate();
  // 유저 정보 전역 상태 저장 훅
  const { user } = useUserStore();
  // 썸네일 이미지 경로
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
              <div className={`text-xl ${highlightHoverColor}`}>
                <Link
                  to={viewMoreUrlQuery({
                    keyword: keyword,
                    isAdult: isAdult,
                    displayMediaType: displayMediaType,
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
        className={`w-full flex flex-wrap items-start mt-6 ${searchScreenType === SEARCH_SCREEN_TYPE.MAIN ? 'ml-5' : ''}`}
      >
        {results.length !== 0 &&
          results.map((items, index) => {
            // 썸네일 이미지 경로
            const thumbnail = items.backdropPath
              ? displayMediaType === MEDIA_TYPE.COMICS
                ? items.backdropPath
                : thumbnailImagePath + items.backdropPath
              : items.posterPath
                ? thumbnailImagePath + items.posterPath
                : COMMON_IMAGES.NO_IMAGE;
            // 하트 아이콘 style
            const heartStyle = 'z-1 absolute bottom-2 right-3';
            // 제목
            const title =
              isSearchTvType(items, displayMediaType) ||
              isRecommendationsTvType(items, displayMediaType)
                ? items.name
                : items.title;
            // 이전 요소의 id가 중복이면 렌더링 중지
            if (index !== 0 && results[index - 1].id === items.id) {
              return null;
            }

            return (
              <ul
                key={'frame_' + index}
                className={
                  `${highlightHoverColor} ml-1 cursor-pointer ` +
                  (displayMediaType === MEDIA_TYPE.COMICS
                    ? ' mr-1 w-[195px] h-full'
                    : ' mr-3 w-[290px] h-full')
                }
                onClick={commonErrorHandler(() => {
                  // apiId 체크
                  checkApiId(items.id);
                  // 상세화면 URL 생성
                  const detailUrl = detailUrlQuery({
                    contentMediaType: items.contentMediaType!,
                    apiId: String(items.id),
                    tabNo: 0,
                  });
                  // 상세화면 이동
                  navigate(detailUrl);
                })}
              >
                {/* 썸네일 */}
                <li
                  key={'poster_path' + index}
                  className="relative flex justify-center items-center"
                >
                  <LazyImage
                    src={thumbnail}
                    alt={'Thumbnail Image'}
                    className={
                      (displayMediaType === MEDIA_TYPE.COMICS
                        ? 'max-w-full h-[270px]'
                        : 'max-w-full h-[180px]') + ' object-cover rounded-2xl'
                    }
                  />
                  <div className={heartStyle}>
                    <WishlistUi
                      contentMediaType={items.contentMediaType!}
                      apiId={Number(items.id)}
                      title={title!}
                      userId={user?.userId}
                      isWishlisted={items.wishlisted!}
                      thumbnailImageUrl={
                        items.backdropPath ?? items.posterPath ?? ''
                      }
                      genreIds={items.genreIds ?? []}
                    />
                  </div>
                </li>
                {/* 제목 */}
                <li
                  key={'title_' + index}
                  className="ml-1 mr-1 mt-1 mb-4 text-lg"
                >
                  {title}
                </li>
              </ul>
            );
          })}
      </div>
    </div>
  );
};

export default DisplaySearchResults;
