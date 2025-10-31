import { useTranslation } from 'react-i18next';
import { DetailVideoInformationPropsType } from '../features/detail/tabs/information/contentInformation/DetailVideoInformation';
import {
  COMMON_IMAGES,
  DETAIL_TAB_ID,
  SEPERATE_SLASH,
  TMDB_API_IMAGE_DOMAIN,
  VIDEO_CREDITS_TYPE,
  WIDTH_185,
} from '../common/constants/constants';
import {
  isDetailCreditsCrewType,
  isDetailMovieType,
  isDetailTvType,
} from '../common/utils/typeGuardUtil';
import { settings } from '../common/config/settings';
import { Link } from 'react-router-dom';
import { detailUrlQuery, personUrlQuery } from '../common/utils/urlUtil';
import { checkPersonId } from '../common/utils/checkUtil';
import { LazyImage } from './common/LazyImageUi';
import {
  HIGHLIGHT_HOVER_COLOR,
  OVERFLOW_AUTO_STYLE,
} from '../common/constants/tailwindStyles';

/**
 * 크레딧 정보 표시 컴포넌트 props 타입
 */
type DisplayVideoCreditsPropsType = DetailVideoInformationPropsType & {
  creditsType: string;
  isOmit?: boolean;
  displayCount?: number;
};

/**
 * 크레딧 정보 표시 컴포넌트
 * @param detailResult 상세 정보
 * @param contentMediaType 컨텐츠 미디어 타입
 * @param creditsType 크레딧 타입
 * @param isOmit 생략 여부
 * @param displayCount 표시할 항목 수
 */
export const DisplayVideoCredits = ({
  detailResult,
  contentMediaType,
  creditsType,
  isOmit,
  displayCount,
}: DisplayVideoCreditsPropsType) => {
  // i18n
  const { t } = useTranslation();
  // 썸네일 이미지 경로
  const thumbnailImagePath = TMDB_API_IMAGE_DOMAIN + WIDTH_185;
  // 비디오(TV or Movie) 크레딧
  const videoCredits =
    isDetailTvType(detailResult, contentMediaType) ||
    isDetailMovieType(detailResult, contentMediaType)
      ? detailResult.credits
      : undefined;
  // 크레딧(cast or crew) 전 데이터
  const creditsAll =
    videoCredits &&
    (creditsType === VIDEO_CREDITS_TYPE.CAST
      ? videoCredits.cast
      : videoCredits.crew);
  // 크레딧(cast or crew) 필터링 데이터
  const creditsList =
    creditsAll &&
    (isOmit
      ? creditsAll.filter((_, index) => index < settings.detailVideoCount)
      : displayCount
        ? creditsAll.slice(0, displayCount)
        : creditsAll);
  // 탭 번호
  const tabNo =
    creditsType === VIDEO_CREDITS_TYPE.CAST
      ? DETAIL_TAB_ID.cast
      : DETAIL_TAB_ID.crew;
  return (
    <>
      {creditsList && (
        <div className="mb-8">
          {/* 출연진 or 제작진 */}
          <div className="mt-5 mb-5 flex items-center justify-between">
            <div className="text-2xl font-bold sm:text-3xl">
              {creditsType === VIDEO_CREDITS_TYPE.CREW
                ? t('info.crew')
                : t('info.cast')}
            </div>
            {/* 더보기 링크 */}
            {isOmit && creditsAll.length > settings.detailVideoCount && (
              <div className="text-lx">
                <Link
                  to={detailUrlQuery({
                    contentMediaType: contentMediaType,
                    apiId: String(detailResult.id),
                    tabNo: tabNo,
                  })}
                  className={`${HIGHLIGHT_HOVER_COLOR}`}
                >
                  {t('info.seeMore')} &gt;
                </Link>
              </div>
            )}
          </div>

          {/* 출연진 or 제작진 리스트 */}
          <div className="grid grid-cols-2 gap-3 md:grid-cols-3 lg:grid-cols-4 lg:gap-x-3 lg:gap-y-5">
            {creditsList.map((items, index) => {
              // 역할
              const role = isDetailCreditsCrewType(items)
                ? isDetailTvType(detailResult, contentMediaType)
                  ? items.jobs?.map((job) => job.job).join(SEPERATE_SLASH)
                  : items.job
                : isDetailTvType(detailResult, contentMediaType)
                  ? items.roles
                      ?.map((role) => role.character)
                      .join(SEPERATE_SLASH)
                  : items.character;

              return (
                <Link
                  key={items.id + '_' + index}
                  to={items.id ? personUrlQuery({ personId: items.id }) : '#'}
                  className={`${HIGHLIGHT_HOVER_COLOR} block cursor-pointer`}
                  onClick={() => checkPersonId(items.id)}
                >
                  <div className="flex h-[140px] w-full items-center rounded-2xl border border-black/5 p-2 sm:p-3 lg:h-[180px]">
                    {/* 이미지 */}
                    <div className="relative flex h-full max-w-[30%] shrink-0 basis-[30%] items-center">
                      <LazyImage
                        src={
                          items.profilePath
                            ? thumbnailImagePath + items.profilePath
                            : COMMON_IMAGES.NO_IMAGE
                        }
                        alt={items.name}
                        className="rounded-2xl"
                      />
                    </div>
                    {/* 이름 & 역할 */}
                    <div className="ml-3 flex h-full max-w-[70%] basis-[70%] flex-col">
                      <div
                        className={`grow rounded-md bg-white/0 p-1 text-base md:text-lg ${OVERFLOW_AUTO_STYLE}`}
                      >
                        <div className="font-medium">{items.name!}</div>
                        {role && (
                          <div className="mt-1 text-sm text-gray-600">
                            {role}
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                </Link>
              );
            })}
          </div>
        </div>
      )}
    </>
  );
};

export default DisplayVideoCredits;
