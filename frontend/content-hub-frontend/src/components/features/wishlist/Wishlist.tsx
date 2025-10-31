import { useNavigate } from 'react-router-dom';
import { useWishlist } from './useWishlist';
import { WishlistResponseDto } from '@/api/data-contracts';
import { LoadingUi } from '@/components/ui/common/LoadingUi';
import {
  COMMON_IMAGES,
  SEARCH_TYPE,
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_300,
} from '@/components/common/constants/constants';
import { useTranslation } from 'react-i18next';
import { HIGHLIGHT_HOVER_COLOR } from '@/components/common/constants/tailwindStyles';
import { commonErrorHandler } from '@/components/common/utils/errorUtil';
import { checkApiId } from '@/components/common/utils/checkUtil';
import { detailUrlQuery } from '@/components/common/utils/urlUtil';
import { BiDotsVerticalRounded } from 'react-icons/bi';
import { RefObject } from 'react';
import { settings } from '@/components/common/config/settings';
import { ButtonUi, LazyImage } from '@/components/ui/common';
import { useIsMobile } from '@/components/common/hooks/useIsMobile';

/**
 * DisplayWishlist 컴포넌트 props 타입
 */
type DisplayWishlistPropsType = {
  mediaName: string;
  resultList: WishlistResponseDto[];
  handleWishlistDeleteOnClick: (
    apiId: number,
    contentMediaType: string,
    title: string
  ) => void;
  isExecuting: boolean;
  wishlistOptionIsOpen: boolean;
  handleWishlistOptionOnClick: (
    contentMediaType: string,
    index: number
  ) => void;
  wishlistOptionRef: RefObject<HTMLDivElement[] | null[]>;
  wishlistContentMediaType: string;
  wishlistOptionIndex: number;
  searchType: string;
  handleOnClickOmitWishlist: (searchType: string) => void;
  isOmit: boolean;
};

/**
 * 위시리스트 컴포넌트
 */
export const Wishlist = () => {
  // i18n
  const { t } = useTranslation();

  // 위시리스트 조회 훅
  const {
    data,
    isLoading,
    handleWishlistDeleteOnClick,
    isExecuting,
    wishlistOptionIsOpen,
    handleWishlistOptionOnClick,
    aniWishlistOptionRef,
    dramaWishlistOptionRef,
    documentaryWishlistOptionRef,
    kidsWishlistOptionRef,
    newsWishlistOptionRef,
    varietyWishlistOptionRef,
    movieWishlistOptionRef,
    comicsWishlistOptionRef,
    wishlistContentMediaType,
    wishlistOptionIndex,
    isOmitAniList,
    isOmitDramaList,
    isOmitDocumentaryList,
    isOmitKidsList,
    isOmitNewsList,
    isOmitVarietyList,
    isOmitMovieList,
    isOmitComicsList,
    handleOnClickOmitWishlist,
  } = useWishlist();

  // 각 미디어 타입별 위시리스트 결과
  const aniResultList = data?.aniWishlist;
  const dramaResultList = data?.dramaWishlist;
  const documentaryResultList = data?.documentaryWishlist;
  const kidsResultList = data?.kidsWishlist;
  const newsResultList = data?.newsWishlist;
  const varietyResultList = data?.varietyWishlist;
  const movieResultList = data?.movieWishlist;
  const comicsResultList = data?.comicsWishlist;

  // 데이터가 비어있는지 여부
  const isDataEmpty = Object.values(data || {}).every(
    (list) => !Array.isArray(list) || list.length === 0
  );

  // 미디어 타입별 위시리스트 항목 배열
  const wishlistItems = [
    {
      mediaName: t('info.animation'),
      searchType: SEARCH_TYPE.ANI,
      resultList: aniResultList,
      ref: aniWishlistOptionRef,
      isOmit: isOmitAniList,
    },
    {
      mediaName: t('info.drama'),
      searchType: SEARCH_TYPE.DRAMA,
      resultList: dramaResultList,
      ref: dramaWishlistOptionRef,
      isOmit: isOmitDramaList,
    },
    {
      mediaName: t('info.movie'),
      searchType: SEARCH_TYPE.MOVIE,
      resultList: movieResultList,
      ref: movieWishlistOptionRef,
      isOmit: isOmitMovieList,
    },
    {
      mediaName: t('info.documentary'),
      searchType: SEARCH_TYPE.DOCUMENTARY,
      resultList: documentaryResultList,
      ref: documentaryWishlistOptionRef,
      isOmit: isOmitDocumentaryList,
    },
    {
      mediaName: t('info.kids'),
      searchType: SEARCH_TYPE.KIDS,
      resultList: kidsResultList,
      ref: kidsWishlistOptionRef,
      isOmit: isOmitKidsList,
    },
    {
      mediaName: t('info.news'),
      searchType: SEARCH_TYPE.NEWS,
      resultList: newsResultList,
      ref: newsWishlistOptionRef,
      isOmit: isOmitNewsList,
    },
    {
      mediaName: t('info.variety'),
      searchType: SEARCH_TYPE.VARIETY,
      resultList: varietyResultList,
      ref: varietyWishlistOptionRef,
      isOmit: isOmitVarietyList,
    },
    {
      mediaName: t('info.comics'),
      searchType: SEARCH_TYPE.COMICS,
      resultList: comicsResultList,
      ref: comicsWishlistOptionRef,
      isOmit: isOmitComicsList,
    },
  ];

  return (
    <div className="pt-20">
      {isLoading ? (
        <LoadingUi />
      ) : !isDataEmpty ? (
        <>
          <div className="mb-10 text-4xl font-bold">{t('info.wishlist')}</div>
          {wishlistItems.map((items) => {
            return (
              items.resultList &&
              items.resultList.length !== 0 && (
                <div key={items.mediaName}>
                  <DisplayWishlist
                    mediaName={items.mediaName}
                    searchType={items.searchType}
                    resultList={items.resultList}
                    handleWishlistDeleteOnClick={handleWishlistDeleteOnClick}
                    isExecuting={isExecuting}
                    wishlistOptionIsOpen={wishlistOptionIsOpen}
                    handleWishlistOptionOnClick={handleWishlistOptionOnClick}
                    wishlistOptionRef={items.ref}
                    wishlistContentMediaType={wishlistContentMediaType}
                    wishlistOptionIndex={wishlistOptionIndex}
                    handleOnClickOmitWishlist={handleOnClickOmitWishlist}
                    isOmit={items.isOmit}
                  />
                </div>
              )
            );
          })}
        </>
      ) : (
        <div className="mt-25 flex items-center justify-center font-['Inter'] text-xl font-normal text-black lg:mt-60 lg:text-2xl">
          {t('info.noWishlist')}
        </div>
      )}
    </div>
  );
};

/**
 * 위시리스트 결과 표시 컴포넌트
 * @param mediaName 미디어 이름
 * @param searchType 검색 타입
 * @param resultList 위시리스트 결과 리스트
 * @param handleWishlistDeleteOnClick 위시리스트 삭제 클릭 핸들러
 * @param isExecuting 실행 중 여부
 * @param wishlistOptionIsOpen 위시리스트 옵션 열림 여부
 * @param handleWishlistOptionOnClick 위시리스트 옵션 클릭 핸들러
 * @param wishlistOptionRef 위시리스트 옵션 ref
 * @param wishlistContentMediaType 위시리스트 콘텐츠 미디어 타입
 * @param wishlistOptionIndex 위시리스트 옵션 인덱스
 * @param handleOnClickOmitWishlist 위시리스트 간략히/더보기 클릭 핸들러
 * @param isOmit 생략 여부
 */
const DisplayWishlist = ({
  mediaName,
  searchType,
  resultList,
  handleWishlistDeleteOnClick,
  isExecuting,
  wishlistOptionIsOpen,
  handleWishlistOptionOnClick,
  wishlistOptionRef,
  wishlistContentMediaType,
  wishlistOptionIndex,
  handleOnClickOmitWishlist,
  isOmit,
}: DisplayWishlistPropsType) => {
  // navigate 훅
  const navigate = useNavigate();
  // i18n
  const { t } = useTranslation();
  // 모바일 여부 판단 훅
  const isMobile = useIsMobile();

  // 만화일 경우
  const isComics = searchType === SEARCH_TYPE.COMICS;
  // 항목별 남길 개수
  const restCount =
    searchType === SEARCH_TYPE.COMICS
      ? isMobile
        ? 6
        : settings.wishlistComicsOmissionLength
      : isMobile
        ? 6
        : settings.wishlistVideoOmissionLength;
  // 표시할 항목 리스트
  const displayList = isOmit ? filterList(resultList, restCount) : resultList;
  // 썸네일 이미지 경로
  const videoThumbnail = TMDB_API_IMAGE_DOMAIN + WIDTH_300;
  // 카드 그리드
  const gridCols =
    'grid gap-x-3 gap-y-5 ' +
    (isComics
      ? 'grid-cols-3 sm:grid-cols-4 md:grid-cols-5 lg:grid-cols-7'
      : 'grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5');
  // 썸네일 비율 클래스
  const aspectClass = isComics ? 'aspect-[2/3]' : 'aspect-[16/9]';

  return (
    <div className="mb-10">
      <div className="text-3xl font-bold">{mediaName}</div>

      {/* 위시리스트 항목들 */}
      <div className={`mt-6 ${gridCols}`}>
        {displayList.map((items, index) => {
          // 만화일 경우
          const thumbnail = isComics
            ? items.thumbnailImageUrl
            : videoThumbnail + items.thumbnailImageUrl;
          return (
            <div
              key={items.apiId}
              className={`${HIGHLIGHT_HOVER_COLOR} cursor-pointer`}
              aria-label={items.title || 'wishlist item'}
              onClick={commonErrorHandler(() => {
                // apiId 체크
                checkApiId(Number(items.apiId));
                // 상세화면 URL 생성
                const detailUrl = detailUrlQuery({
                  contentMediaType: items.contentMediaType!,
                  apiId: String(items.apiId),
                  tabNo: 0,
                });
                // 상세화면 이동
                navigate(detailUrl);
              })}
            >
              {/* 썸네일 */}
              <div
                className={`relative w-full overflow-hidden ${aspectClass} bg-white`}
                // <div className='relative'
                ref={(el) => {
                  if (el) {
                    // ref 배열에 각 항목의 ref 저장
                    wishlistOptionRef.current[index] = el;
                  }
                }}
              >
                {/* 썸네일 이미지 */}
                <div
                  className={`relative flex h-full w-full justify-center ${aspectClass}`}
                >
                  <LazyImage
                    src={thumbnail ?? COMMON_IMAGES.NO_IMAGE}
                    alt={items.title || 'thumbnail'}
                    className={`inset-0 h-full w-full rounded-2xl object-cover`}
                  />
                </div>

                {/* 옵션 버튼 */}
                <button
                  type="button"
                  onClick={(e) => {
                    e.stopPropagation();
                    handleWishlistOptionOnClick(items.contentMediaType!, index);
                  }}
                  className="absolute top-2 right-2 z-10 cursor-pointer rounded-sm bg-white p-0.5 text-black shadow"
                  aria-label={t('info.options') || 'options'}
                >
                  <BiDotsVerticalRounded className="text-2xl" />
                </button>

                {/* 옵션 메뉴 */}
                {wishlistContentMediaType === items.contentMediaType &&
                  wishlistOptionIndex === index &&
                  wishlistOptionIsOpen && (
                    <div className="absolute top-10 right-2 z-10 mt-1 flex w-24 justify-center rounded-md border border-gray-300 bg-white p-2 shadow-md">
                      {/* 삭제 */}
                      <ButtonUi
                        type="button"
                        variant="ghost"
                        size="sm"
                        className="w-full cursor-pointer hover:bg-gray-200"
                        onClick={(e) => {
                          e.stopPropagation();
                          if (!isExecuting) {
                            handleWishlistDeleteOnClick(
                              Number(items.apiId),
                              items.contentMediaType!,
                              items.title || ''
                            );
                          }
                        }}
                      >
                        {t('info.delete')}
                      </ButtonUi>
                    </div>
                  )}
              </div>

              {/* 제목 */}
              <div
                className="mt-2 line-clamp-2 px-1 text-base font-medium sm:text-lg"
                title={items.title || ''}
              >
                {items.title}
              </div>
            </div>
          );
        })}
      </div>
      {/* 더보기/간략히 버튼 */}
      <div>
        {resultList.length > restCount && (
          <ButtonUi
            type="button"
            variant="ghost"
            size="sm"
            className="mt-2 cursor-pointer text-base text-gray-600 underline"
            aria-label={isOmit ? t('info.readMore') : t('info.inShort')}
            onClick={() => {
              handleOnClickOmitWishlist(searchType);
            }}
          >
            {isOmit ? t('info.readMore') : t('info.inShort')}
          </ButtonUi>
        )}
      </div>
    </div>
  );
};

/**
 * 리스트를 필터링하는 함수
 * @param list 필터링할 리스트
 * @param restCount 남길 항목 수
 * @returns 필터링된 리스트
 */
const filterList = (list: WishlistResponseDto[], restCount: number) => {
  return list.length > restCount ? list.slice(0, restCount) : list;
};

export default Wishlist;
