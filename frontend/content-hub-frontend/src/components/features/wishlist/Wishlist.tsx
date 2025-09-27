import { useNavigate, useParams } from 'react-router-dom';
import { useWishlist } from './useWishlist';
import { WishlistResponseDto } from '@/api/data-contracts';
import { LoadingUi } from '@/components/ui/LoadingUi';
import {
  COMMON_IMAGES,
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_300,
} from '@/components/common/constants/constants';
import { useTranslation } from 'react-i18next';
import { highlightHoverColor } from '@/components/common/constants/tailwindStyles';
import { commonErrorHandler } from '@/components/common/utils/errorUtil';
import { checkApiId } from '@/components/common/utils/checkUtil';
import { detailUrlQuery } from '@/components/common/utils/urlUtil';
import { BiDotsVerticalRounded } from 'react-icons/bi';
import { RefObject } from 'react';
import { getContentMediaType } from '@/components/common/utils/convertUtil';

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
};

/**
 * 위시리스트 컴포넌트
 */
export const Wishlist = () => {
  // i18n
  const { t } = useTranslation();
  // URL 파라미터에서 userId 추출
  const { userId } = useParams();

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
  } = useWishlist(Number(userId));

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
      resultList: aniResultList,
      ref: aniWishlistOptionRef,
    },
    {
      mediaName: t('info.drama'),
      resultList: dramaResultList,
      ref: dramaWishlistOptionRef,
    },
    {
      mediaName: t('info.movie'),
      resultList: movieResultList,
      ref: movieWishlistOptionRef,
    },
    {
      mediaName: t('info.documentary'),
      resultList: documentaryResultList,
      ref: documentaryWishlistOptionRef,
    },
    {
      mediaName: t('info.kids'),
      resultList: kidsResultList,
      ref: kidsWishlistOptionRef,
    },
    {
      mediaName: t('info.news'),
      resultList: newsResultList,
      ref: newsWishlistOptionRef,
    },
    {
      mediaName: t('info.variety'),
      resultList: varietyResultList,
      ref: varietyWishlistOptionRef,
    },
    {
      mediaName: t('info.comics'),
      resultList: comicsResultList,
      ref: comicsWishlistOptionRef,
    },
  ];

  return (
    <div className="mt-30">
      {isLoading ? (
        <LoadingUi />
      ) : !isDataEmpty ? (
        <>
          <div className="text-4xl font-bold ml-5 mb-10">
            {t('info.wishlist')}
          </div>
          {wishlistItems.map((items) => {
            return (
              items.resultList &&
              items.resultList.length !== 0 && (
                <div key={items.mediaName}>
                  <DisplayWishlist
                    mediaName={items.mediaName}
                    resultList={items.resultList}
                    handleWishlistDeleteOnClick={handleWishlistDeleteOnClick}
                    isExecuting={isExecuting}
                    wishlistOptionIsOpen={wishlistOptionIsOpen}
                    handleWishlistOptionOnClick={handleWishlistOptionOnClick}
                    wishlistOptionRef={items.ref}
                    wishlistContentMediaType={wishlistContentMediaType}
                    wishlistOptionIndex={wishlistOptionIndex}
                  />
                </div>
              )
            );
          })}
        </>
      ) : (
        <div className="mt-25 lg:mt-60 flex justify-center items-center text-black text-xl lg:text-2xl font-normal font-['Inter']">
          {t('info.noWishlist')}
        </div>
      )}
    </div>
  );
};

/**
 * 위시리스트 결과 표시 컴포넌트
 * @param mediaName 미디어 이름
 * @param resultList 위시리스트 결과 리스트
 */
const DisplayWishlist = ({
  mediaName,
  resultList,
  handleWishlistDeleteOnClick,
  isExecuting,
  wishlistOptionIsOpen,
  handleWishlistOptionOnClick,
  wishlistOptionRef,
  wishlistContentMediaType,
  wishlistOptionIndex,
}: DisplayWishlistPropsType) => {
  // navigate 훅
  const navigate = useNavigate();
  // i18n
  const { t } = useTranslation();
  // 썸네일 이미지 경로
  const thumbnailImagePath = TMDB_API_IMAGE_DOMAIN + WIDTH_300;
  return (
    <div className="mb-10">
      <div className="ml-5 text-3xl font-bold">{mediaName}</div>
      <div className="ml-5 w-full flex flex-wrap items-start mt-6">
        {resultList.map((items, index) => {
          return (
            <ul
              key={items.apiId}
              className={
                `${highlightHoverColor} ml-1 mr-3 cursor-pointer ` +
                (items.contentMediaType === getContentMediaType().comicsCode
                  ? 'w-[195px] h-full'
                  : 'w-[290px] h-full')
              }
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
              <li className="relative flex justify-center items-center">
                <div
                  ref={(el) => {
                    if (el) {
                      // ref 배열에 각 항목의 ref 저장
                      wishlistOptionRef.current[index] = el;
                    }
                  }}
                >
                  {/* 옵션 버튼 */}
                  <div
                    onClick={(e) => {
                      e.stopPropagation();
                      handleWishlistOptionOnClick(
                        items.contentMediaType!,
                        index
                      );
                    }}
                    className="absolute top-0.5 right-0.5 z-10"
                  >
                    <BiDotsVerticalRounded className="absolute top-2 right-2 z-10 bg-white text-black text-2xl rounded-sm" />
                  </div>
                  {wishlistContentMediaType === items.contentMediaType &&
                    wishlistOptionIndex === index &&
                    wishlistOptionIsOpen && (
                      <div className="absolute flex justify-center mt-1 top-8 right-2 z-10 w-20 bg-white border border-gray-300 rounded-md shadow-md p-2">
                        {/* 삭제 */}
                        <div
                          className="px-4 text-sm text-gray-700 hover:bg-gray-200 cursor-pointer"
                          onClick={(e) => {
                            e.stopPropagation();
                            if (!isExecuting) {
                              handleWishlistDeleteOnClick(
                                Number(items.apiId),
                                items.contentMediaType!,
                                items.title!
                              );
                            }
                          }}
                        >
                          {t('info.delete')}
                        </div>
                      </div>
                    )}
                  {/* 썸네일 이미지 */}
                  <img
                    src={
                      items.contentMediaType ===
                      getContentMediaType().comicsCode
                        ? items.thumbnailImageUrl
                        : thumbnailImagePath + items.thumbnailImageUrl
                    }
                    alt={items.title}
                    className={
                      (items.contentMediaType ===
                      getContentMediaType().comicsCode
                        ? 'max-w-full h-[270px]'
                        : 'max-w-full h-[180px]') + ' object-cover rounded-2xl'
                    }
                    onError={(e) => {
                      e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                    }}
                  />
                </div>
              </li>
              {/* 제목 */}
              <li
                key={'title_' + index}
                className="ml-1 mr-1 mt-1 mb-4 text-lg"
              >
                {items.title}
              </li>
            </ul>
          );
        })}
      </div>
    </div>
  );
};

export default Wishlist;
