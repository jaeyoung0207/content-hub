import { useNavigate, useParams } from 'react-router-dom';
import { useWishlist } from './useWishlist';
import { WishlistResponseDto } from '@/api/data-contracts';
import { LoadingUi } from '@/components/ui/LoadingUi';
import {
  COMMON_IMAGES,
  MEDIA_TYPE,
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_300,
} from '@/components/common/constants/constants';
import { useTranslation } from 'react-i18next';
import { highlightHoverColor } from '@/components/common/constants/tailwindStyles';
import { commonErrorHandler } from '@/components/common/utils/errorUtil';
import { checkApiId } from '@/components/common/utils/checkUtil';
import { detailUrlQuery } from '@/components/common/utils/urlUtil';
import { CloseButtonUi } from '@/components/ui/common/CloseButtonUi';

/**
 * DisplayWishlist 컴포넌트 props 타입
 */
type DisplayWishlistPropsType = {
  mediaName: string;
  resultList: WishlistResponseDto[];
  handleWishlistDeleteOnClick: (
    apiId: number,
    originalMediaType: string,
    title: string
  ) => void;
  isExecuting: boolean;
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
  const { data, isLoading, handleWishlistDeleteOnClick, isExecuting } =
    useWishlist(Number(userId));

  // 각 미디어 타입별 위시리스트 결과
  const aniResultList = data?.aniWishlist;
  const dramaResultList = data?.dramaWishlist;
  const movieResultList = data?.movieWishlist;
  const comicsResultList = data?.comicsWishlist;

  // 미디어 타입별 위시리스트 항목 배열
  const wishlistItems = [
    { mediaName: t('info.animation'), resultList: aniResultList },
    { mediaName: t('info.drama'), resultList: dramaResultList },
    { mediaName: t('info.movie'), resultList: movieResultList },
    { mediaName: t('info.comics'), resultList: comicsResultList },
  ];

  return (
    <div className="mt-30">
      {isLoading ? (
        <LoadingUi />
      ) : (
        data &&
        wishlistItems.map((items) => {
          return (
            items.resultList && (
              <DisplayWishlist
                mediaName={items.mediaName}
                resultList={items.resultList}
                handleWishlistDeleteOnClick={handleWishlistDeleteOnClick}
                isExecuting={isExecuting}
              />
            )
          );
        })
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
}: DisplayWishlistPropsType) => {
  // navigate 훅
  const navigate = useNavigate();
  // 썸네일 이미지 경로
  const thumbnailImagePath = TMDB_API_IMAGE_DOMAIN + WIDTH_300;
  return (
    <div className="mb-10">
      <div className="ml-5 text-4xl font-bold">{mediaName}</div>
      <div className="ml-5 w-full flex flex-wrap items-start mt-6">
        {resultList.map((items, index) => {
          return (
            <ul
              key={items.apiId}
              className={
                `${highlightHoverColor} ml-1 mr-3 cursor-pointer ` +
                (items.originalMediaType === MEDIA_TYPE.COMICS
                  ? 'w-[195px] h-full'
                  : 'w-[290px] h-full')
              }
              onClick={commonErrorHandler(() => {
                // apiId 체크
                checkApiId(Number(items.apiId));
                // 상세화면 URL 생성
                const detailUrl = detailUrlQuery({
                  originalMediaType: items.originalMediaType!,
                  apiId: String(items.apiId),
                  tabNo: 0,
                });
                // 상세화면 이동
                navigate(detailUrl);
              })}
            >
              {/* 썸네일 */}
              <li className="relative flex justify-center items-center">
                <CloseButtonUi
                  modalClose={() => {
                    handleWishlistDeleteOnClick(
                      Number(items.apiId),
                      items.originalMediaType!,
                      items.title!
                    );
                  }}
                  className="absolute top-2 right-2 z-10"
                  disabled={isExecuting}
                />
                <img
                  src={
                    items.originalMediaType === MEDIA_TYPE.COMICS
                      ? items.thumbnailImageUrl
                      : thumbnailImagePath + items.thumbnailImageUrl
                  }
                  alt={items.title}
                  className={
                    (items.originalMediaType === MEDIA_TYPE.COMICS
                      ? 'max-w-full h-[270px]'
                      : 'max-w-full h-[180px]') + ' object-cover rounded-2xl'
                  }
                  onError={(e) => {
                    e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                  }}
                />
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
