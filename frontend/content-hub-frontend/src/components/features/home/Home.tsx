import { HomeRankingReponseDto, LoginUserInfoDto } from '@/api/data-contracts';
import { useHome } from './useHome';
import { useTranslation } from 'react-i18next';
import {
  COMMON_IMAGES,
  MEDIA_TYPE,
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_300,
} from '@/components/common/constants/constants';
import { LoadingUi } from '@/components/ui/LoadingUi';
import { BsStarFill } from 'react-icons/bs';
import { commonErrorHandler } from '@/components/common/utils/errorUtil';
import { checkApiId } from '@/components/common/utils/checkUtil';
import { useNavigate } from 'react-router-dom';
import { detailUrlQuery } from '@/components/common/utils/urlUtil';
import { highlightHoverColor } from '@/components/common/constants/tailwindStyles';
import { WishlistUi } from '@/components/ui/WishlistUi';

type DisplayRankingsProps = {
  title: string;
  items: HomeRankingReponseDto[];
  user: LoginUserInfoDto | null;
};

/**
 * 홈 화면 컴포넌트
 */
export const Home = () => {
  // i18n 번역 훅
  const { t } = useTranslation();

  // 홈 화면 훅 호출
  const { data, isLoading, user } = useHome();

  // 각 콘텐츠 랭킹 데이터
  const contentRankings = [
    {
      title: `${t('info.animation')} - ${t('info.top10')}`,
      items: data && data.aniRankingList ? data.aniRankingList : [],
    },
    {
      title: `${t('info.drama')} - ${t('info.top10')}`,
      items: data && data.dramaRankingList ? data.dramaRankingList : [],
    },
    {
      title: `${t('info.movie')} - ${t('info.top10')}`,
      items: data && data.movieRankingList ? data.movieRankingList : [],
    },
    {
      title: `${t('info.comics')} - ${t('info.top10')}`,
      items: data && data.comicsRankingList ? data.comicsRankingList : [],
    },
  ];

  return (
    <div className="w-sm lg:w-7xl">
      <div className="mt-30">
        {isLoading ? (
          <LoadingUi />
        ) : (
          data && (
            <>
              <h2 className="text-3xl font-bold mb-10">
                {t('info.rankingTitle')}
              </h2>
              {contentRankings.map((ranking) => (
                <DisplayRankings
                  title={ranking.title}
                  items={ranking.items}
                  user={user}
                />
              ))}
            </>
          )
        )}
      </div>
    </div>
  );
};

export default Home;

/**
 * 각 랭킹을 표시하는 컴포넌트
 * @param title 랭킹 타이틀
 * @param items 랭킹 아이템 배열
 */
const DisplayRankings = ({ title, items, user }: DisplayRankingsProps) => {
  // navigate 훅
  const navigate = useNavigate();
  // 썸네일 이미지 경로
  const thumbnailImagePath = TMDB_API_IMAGE_DOMAIN + WIDTH_300;
  return (
    <div className="block mb-10">
      {/* 각 랭킹 타이틀 */}
      <div className="flex items-start text-2xl font-bold mb-5">{title}</div>
      <div className="whitespace-nowrap flex overflow-x-auto not-hover:scrollbar-default">
        {items.map((items, index) => {
          // 썸네일 이미지
          const thumbnailImageUrl =
            items.originalMediaType === MEDIA_TYPE.COMICS
              ? items.thumbnailImageUrl
              : thumbnailImagePath + items.thumbnailImageUrl;
          const widthStyle =
            items.originalMediaType === MEDIA_TYPE.COMICS
              ? 'w-[195px]'
              : 'w-[300px]';
          const heightStyle =
            items.originalMediaType === MEDIA_TYPE.COMICS
              ? 'h-[270px]'
              : 'h-[180px]';
          const heartStyle =
            items.originalMediaType === MEDIA_TYPE.COMICS
              ? 'relative top-5/12 right-1/6'
              : 'relative top-5/14 right-1/8';
          return (
            <ul
              key={index}
              className={`ml-1 mr-1 block ${highlightHoverColor} cursor-pointer ${widthStyle}`}
              onClick={commonErrorHandler(() => {
                // apiId 체크
                checkApiId(Number(items.apiId));
                // 상세화면 URL 생성
                const detailUrl = detailUrlQuery({
                  originalMediaType: items.originalMediaType!,
                  apiId: items.apiId,
                  tabNo: 0,
                });
                // 상세화면 이동
                navigate(detailUrl);
              })}
            >
              <li className="mb-1 flex justify-center text-lg font-bold">{`TOP ${items.rowNum}`}</li>
              <li
                className={`flex justify-center items-center ${widthStyle} ${heightStyle}`}
              >
                <img
                  src={thumbnailImageUrl}
                  alt={items.title}
                  className={
                    'max-w-full max-h-full object-scale-down rounded-2xl'
                  }
                  onError={(e) => {
                    e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                  }}
                />
                <div className={heartStyle}>
                  <WishlistUi
                    originalMediaType={items.originalMediaType!}
                    apiId={Number(items.apiId)}
                    title={items.title!}
                    userId={user?.userId!}
                    isWishlisted={items.wishlisted!}
                    thumbnailImageUrl={items.thumbnailImageUrl!}
                    mediaType={items.mediaType}
                  />
                </div>
              </li>
              <li className="flex items-center text-lg">
                <BsStarFill className={'text-red-500 mr-2'} />
                {`${items.starRatingAverage?.toFixed(1)} ${items.starRatingCount ? ' (' + (items.starRatingCount > 9999 ? '9999+' : items.starRatingCount) + ')' : ''}`}
              </li>
              <li className="ml-1 mr-1 mb-4 text-lg whitespace-break-spaces">
                {items.title}
              </li>
            </ul>
          );
        })}
      </div>
    </div>
  );
};
