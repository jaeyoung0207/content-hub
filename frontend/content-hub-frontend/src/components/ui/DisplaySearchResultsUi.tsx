import { useTranslation } from 'react-i18next';
import { RecommendationContentResultType } from '../features/detail/tabs/recommendation/useDetailRecommendation';
import { SearchCommonResultType } from '../features/search/Search';
import { Link, useNavigate } from 'react-router-dom';
import { viewMoreUrlQuery } from '../common/utils/urlUtil';
import {
  COMMON_IMAGES,
  SEARCH_SCREEN_TYPE,
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_300,
} from '../common/constants/constants';
import { commonErrorHandler } from '../common/utils/errorUtil';
import {
  isRecommendationsTvType,
  isSearchTvType,
} from '../common/utils/typeGuardUtil';
import { LazyImage } from './common/LazyImageUi';
import { HIGHLIGHT_HOVER_COLOR } from '../common/constants/tailwindStyles';
import { WishlistUi } from './WishlistUi';
import { useUserStore } from '../common/store/globalStateStore';
import { getDisplayMediaType } from '../common/utils/convertUtil';
import { navigateToDetailPage } from '../common/utils/navigateUtil';

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
 * @param mediaName 미디어 이름
 * @param results 검색 결과 리스트
 * @param isViewMore 전체보기 여부
 * @param displayMediaType 화면 표시용 미디어 타입
 * @param keyword 검색어
 * @param isAdult 성인물 포함 여부
 * @param searchScreenType 검색 화면 타입
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
  // 만화일 경우
  const isComics = displayMediaType === getDisplayMediaType().comicsCode;
  // 카드 그리드: 데스크톱 5열, 태블릿 3~4열, 모바일 2열
  const gridCols =
    'grid gap-x-3 gap-y-5 ' +
    (isComics
      ? 'grid-cols-3 sm:grid-cols-4 md:grid-cols-5 lg:grid-cols-7'
      : 'grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5');
  // 썸네일 비율 클래스
  const aspectClass = isComics ? 'aspect-[2/3]' : 'aspect-[16/9]';
  // 하트 아이콘 style
  const heartClass = 'absolute z-10 bottom-2 right-2';

  return (
    <section>
      {searchScreenType === SEARCH_SCREEN_TYPE.MAIN && (
        // 미디어 이름
        <div className="flex items-center justify-between">
          <h2 className="text-2xl font-bold md:text-3xl">{mediaName}</h2>
          {
            // 전체보기 링크
            isViewMore && (
              <div className={`text-base md:text-lg ${HIGHLIGHT_HOVER_COLOR}`}>
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
        <h2 className="mt-2 text-xl font-bold md:text-2xl lg:text-3xl">
          &quot;{keyword}&quot; {mediaName} {t('info.searchResults')}
        </h2>
      )}

      {/* 검색 결과 */}
      <div className={`mt-6 ${gridCols}`}>
        {results.length !== 0 &&
          results.map((items, index) => {
            // 썸네일 이미지 경로
            let thumbnail: string;
            if (items.backdropPath) {
              if (isComics) {
                thumbnail = items.backdropPath;
              } else {
                thumbnail = thumbnailImagePath + items.backdropPath;
              }
            } else if (items.posterPath) {
              thumbnail = thumbnailImagePath + items.posterPath;
            } else {
              thumbnail = COMMON_IMAGES.NO_IMAGE;
            }
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
              <div
                key={items.id + '-' + index}
                className={`${HIGHLIGHT_HOVER_COLOR}`}
              >
                {/* 썸네일 */}
                <div className="overflow-hidden">
                  <div
                    className={`relative flex h-full w-full justify-center ${aspectClass}`}
                  >
                    <button
                      type="button"
                      className="cursor-pointer"
                      onClick={commonErrorHandler(() => {
                        navigateToDetailPage(
                          navigate,
                          items.id,
                          items.contentMediaType!
                        );
                      })}
                    >
                      <LazyImage
                        src={thumbnail}
                        alt={title || 'Thumbnail Image'}
                        className={`h-full w-full rounded-2xl object-cover`}
                      />
                    </button>
                    <div className={heartClass}>
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
                  </div>
                </div>
                {/* 제목 */}
                <button
                  type="button"
                  onClick={commonErrorHandler(() => {
                    navigateToDetailPage(
                      navigate,
                      items.id,
                      items.contentMediaType!
                    );
                  })}
                  className="relative mt-2 line-clamp-2 cursor-pointer px-1 text-left text-base hover:underline sm:text-lg"
                  title={title || ''} // 툴팁용 title 속성
                >
                  {title}
                </button>
              </div>
            );
          })}
      </div>
    </section>
  );
};

export default DisplaySearchResults;
