import { HomeRankingReponseDto, LoginUserInfoDto } from '@/api/data-contracts';
import { useHome } from './useHome';
import { useTranslation } from 'react-i18next';
import {
  COMMON_IMAGES,
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_300,
} from '@/components/common/constants/constants';
import { LoadingUi } from '@/components/ui/LoadingUi';
import { BsStarFill } from 'react-icons/bs';
import { commonErrorHandler } from '@/components/common/utils/errorUtil';
import { checkApiId } from '@/components/common/utils/checkUtil';
import { useNavigate } from 'react-router-dom';
import { detailUrlQuery } from '@/components/common/utils/urlUtil';
import {
  HIGHLIGHT_HOVER_COLOR,
  OVERFLOW_AUTO_STYLE,
} from '@/components/common/constants/tailwindStyles';
import { WishlistUi } from '@/components/ui/WishlistUi';
import { NodataMessageUi } from '@/components/ui/common/NodataMessageUi';
import { getDisplayMediaType } from '@/components/common/utils/convertUtil';
import {
  useContentMediaTypeMapStore,
  useDisplayMediaTypeMapStore,
} from '@/components/common/store/globalStateStore';

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
  // 컨텐츠 미디어 타입 맵 상태관리
  const { isContentMediaTypeInitialized } = useContentMediaTypeMapStore();
  // 화면 표시용 미디어 타입 맵 상태관리
  const { isDisplayMediaTypeInitialized } = useDisplayMediaTypeMapStore();

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
      title: `${t('info.documentary')} - ${t('info.top10')}`,
      items:
        data && data.documentaryRankingList ? data.documentaryRankingList : [],
    },
    {
      title: `${t('info.kids')} - ${t('info.top10')}`,
      items: data && data.kidsRankingList ? data.kidsRankingList : [],
    },
    {
      title: `${t('info.news')} - ${t('info.top10')}`,
      items: data && data.newsRankingList ? data.newsRankingList : [],
    },
    {
      title: `${t('info.variety')} - ${t('info.top10')}`,
      items: data && data.varietyRankingList ? data.varietyRankingList : [],
    },
    {
      title: `${t('info.comics')} - ${t('info.top10')}`,
      items: data && data.comicsRankingList ? data.comicsRankingList : [],
    },
  ];

  // 모든 랭킹 데이터가 비어있는지 여부
  const isDataEmpty = contentRankings.every(
    (ranking) => ranking.items.length === 0
  );

  return (
    <div className="w-sm lg:w-7xl">
      <div className="mt-30">
        {
          // 콘텐츠 미디어 타입 및 화면 표시용 미디어 타입이 초기화 되었을 때만 렌더링
          isContentMediaTypeInitialized && isDisplayMediaTypeInitialized ? (
            <>
              {isLoading ? (
                <LoadingUi />
              ) : (
                data && (
                  <>
                    <div className="text-4xl font-bold mb-10">
                      {t('info.rankingTitle')}
                    </div>
                    {contentRankings.map(
                      (ranking, index) =>
                        ranking.items.length > 0 && (
                          <DisplayRankings
                            key={index}
                            title={ranking.title}
                            items={ranking.items}
                            user={user}
                          />
                        )
                    )}
                  </>
                )
              )}
              {!isLoading && isDataEmpty && (
                <div className="text-2xl">
                  <NodataMessageUi message={t('warn.noRankingData')} />
                </div>
              )}
            </>
          ) : (
            <LoadingUi />
          )
        }
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
      <div className="flex items-start text-3xl font-bold mb-5">{title}</div>
      <div className={`whitespace-nowrap flex ${OVERFLOW_AUTO_STYLE}`}>
        {items.map((items, index) => {
          // 썸네일 이미지
          const thumbnailImageUrl =
            items.displayMediaType === getDisplayMediaType().comicsCode
              ? items.thumbnailImageUrl
              : thumbnailImagePath + items.thumbnailImageUrl;
          const widthStyle =
            items.displayMediaType === getDisplayMediaType().comicsCode
              ? 'w-[195px]'
              : 'w-[300px]';
          const heightStyle =
            items.displayMediaType === getDisplayMediaType().comicsCode
              ? 'h-[270px]'
              : 'h-[180px]';
          const heartStyle =
            items.displayMediaType === getDisplayMediaType().comicsCode
              ? 'z-1 relative top-28 left-16'
              : 'z-1 relative top-15 left-30';
          return (
            <ul
              key={index}
              className={`ml-1 mr-1 block ${HIGHLIGHT_HOVER_COLOR} cursor-pointer ${widthStyle}`}
              onClick={commonErrorHandler(() => {
                // apiId 체크
                checkApiId(Number(items.apiId));
                // 상세화면 URL 생성
                const detailUrl = detailUrlQuery({
                  contentMediaType: items.contentMediaType!,
                  apiId: items.apiId,
                  tabNo: 0,
                });
                // 상세화면 이동
                navigate(detailUrl);
              })}
            >
              <li className="mb-1 flex justify-center text-lg font-bold">{`TOP ${items.rowNum}`}</li>
              <li
                className={`relative flex justify-center items-center ${widthStyle} ${heightStyle}`}
              >
                <img
                  src={thumbnailImageUrl}
                  alt={items.title}
                  className={
                    'z-0 absolute max-w-full max-h-full object-scale-down rounded-2xl'
                  }
                  onError={(e) => {
                    e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                  }}
                />
                <div className={heartStyle}>
                  <WishlistUi
                    contentMediaType={items.contentMediaType!}
                    apiId={Number(items.apiId)}
                    title={items.title!}
                    userId={user?.userId}
                    isWishlisted={items.wishlisted!}
                    thumbnailImageUrl={items.thumbnailImageUrl!}
                    displayMediaType={items.displayMediaType}
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
