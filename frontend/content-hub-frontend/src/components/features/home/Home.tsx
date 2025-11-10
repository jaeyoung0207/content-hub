import { HomeRankingReponseDto, LoginUserInfoDto } from '@/api/data-contracts';
import { useHome } from './useHome';
import { useTranslation } from 'react-i18next';
import {
  COMMON_IMAGES,
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_300,
} from '@/components/common/constants/constants';
import { LoadingUi } from '@/components/ui/common/LoadingUi';
import { BsStarFill } from 'react-icons/bs';
import { commonErrorHandler } from '@/components/common/utils/errorUtil';
import { checkApiId } from '@/components/common/utils/checkUtil';
import { useNavigate } from 'react-router-dom';
import { detailUrlQuery } from '@/components/common/utils/urlUtil';
import { LazyImage, NoDataMessageUi } from '@/components/ui/common';
import { HIGHLIGHT_HOVER_COLOR } from '@/components/common/constants/tailwindStyles';
import { WishlistUi } from '@/components/ui/WishlistUi';
import { getDisplayMediaType } from '@/components/common/utils/convertUtil';
import {
  useContentMediaTypeMapStore,
  useDisplayMediaTypeMapStore,
} from '@/components/common/store/globalStateStore';

/**
 * 각 랭킹 표시 컴포넌트 props 타입
 */
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
    <div className="pt-20 pb-10 md:pt-24">
      {
        // 콘텐츠 미디어 타입 및 화면 표시용 미디어 타입이 초기화 되었을 때만 렌더링
        isContentMediaTypeInitialized && isDisplayMediaTypeInitialized ? (
          <>
            {isLoading ? (
              <LoadingUi />
            ) : (
              data && (
                <div className="space-y-5">
                  <div className="text-2xl font-bold md:text-3xl">
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
                </div>
              )
            )}
            {!isLoading && isDataEmpty && (
              <div className="text-2xl">
                <NoDataMessageUi message={t('warn.noRankingData')} />
              </div>
            )}
          </>
        ) : (
          <LoadingUi />
        )
      }
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

  // 섹션 내 미디어 타입(코믹스 여부)
  const isComics =
    items[0]?.displayMediaType === getDisplayMediaType().comicsCode;
  // 그리드 컬럼 클래스
  const gridCols =
    'grid gap-x-3 gap-y-5 ' +
    (isComics
      ? 'grid-cols-3 sm:grid-cols-4 md:grid-cols-5 lg:grid-cols-7'
      : 'grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5');
  // 썸네일 컨테이너 비율 클래스
  const aspectClass = isComics ? 'aspect-[2/3]' : 'aspect-[16/9]';
  // 하트 위치 클래스
  const heartClass = 'absolute z-10 bottom-2 right-2';

  return (
    <section>
      {/* 각 랭킹 타이틀 */}
      <h2 className="mb-4 text-xl font-bold sm:text-2xl">{title}</h2>

      {/* 랭킹 아이템 리스트 */}
      <div className={gridCols}>
        {items.map((items, index) => {
          // 썸네일 이미지
          const isComics =
            items.displayMediaType === getDisplayMediaType().comicsCode;
          const thumbnailImageUrl = isComics
            ? items.thumbnailImageUrl
            : thumbnailImagePath + items.thumbnailImageUrl;

          return (
            <div
              key={index}
              className={`${HIGHLIGHT_HOVER_COLOR} cursor-pointer ${aspectClass}`}
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
              {/* 순위 */}
              <div className="mb-1 flex justify-center text-sm font-bold sm:text-base">{`TOP ${items.rowNum}`}</div>
              {/* 썸네일 이미지 */}
              <div
                className={`relative max-w-full overflow-hidden ${aspectClass} bg-white`}
              >
                <div
                  className={`relative flex max-h-full max-w-full justify-center rounded-2xl ${aspectClass}`}
                >
                  <LazyImage
                    src={thumbnailImageUrl || COMMON_IMAGES.NO_IMAGE}
                    alt={items.title}
                    className={`inset-0 h-full w-full rounded-2xl object-cover`}
                  />
                </div>
                {/* 위시리스트 버튼 */}
                <div className={heartClass}>
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
              </div>
              {/* 평점 */}
              <div className="mt-1 flex items-center text-sm sm:text-base">
                <BsStarFill className={'mr-2 text-red-500'} />
                {`${items.starRatingAverage?.toFixed(1)} ${items.starRatingCount ? ' (' + (items.starRatingCount > 9999 ? '9999+' : items.starRatingCount) + ')' : ''}`}
              </div>
              {/* 제목 */}
              <div
                className="relative mr-1 mb-4 ml-1 line-clamp-2 text-base sm:text-lg"
                title={items.title}
              >
                {items.title}
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
};
