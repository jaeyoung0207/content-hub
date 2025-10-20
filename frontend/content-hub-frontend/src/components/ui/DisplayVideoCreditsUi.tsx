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
  // 높이 스타일
  const heightStyle = 190;
  return (
    <div className="mb-8">
      {creditsList && (
        <>
          {/* 출연진 or 제작진 */}
          <div className="flex justify-between mt-5 mb-5">
            <div className="text-3xl font-bold">
              {creditsType === VIDEO_CREDITS_TYPE.CREW
                ? t('info.crew')
                : t('info.cast')}
            </div>
            {/* 더보기 링크 */}
            <div className="text-lx">
              {isOmit && creditsAll.length > settings.detailVideoCount && (
                <Link
                  to={detailUrlQuery({
                    contentMediaType: contentMediaType,
                    apiId: String(detailResult.id),
                    tabNo: tabNo,
                  })}
                  className={`ml-5 ${HIGHLIGHT_HOVER_COLOR}`}
                >
                  {t('info.seeMore') + ' >'}
                </Link>
              )}
            </div>
          </div>
          <div className="flex flex-wrap items-start mt-5">
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
                <div
                  key={items.id + '_' + index}
                  className={`ml-1 mr-1 w-[390px]`}
                  style={{ height: `${heightStyle}px` }}
                  onClick={() => checkPersonId(items.id)}
                >
                  <Link
                    to={items.id ? personUrlQuery({ personId: items.id }) : '#'}
                  >
                    <ul
                      className={`flex justify-center items-center ${HIGHLIGHT_HOVER_COLOR} w-full h-full`}
                    >
                      {/* 이미지 */}
                      <li className="max-w-[30%]">
                        <LazyImage
                          src={
                            items.profilePath
                              ? thumbnailImagePath + items.profilePath
                              : COMMON_IMAGES.NO_IMAGE
                          }
                          alt={items.name}
                          className="rounded-2xl"
                        />
                      </li>
                      {/* 이름 & 역할 */}
                      <li className="ml-4 mr-1 text-lg w-[70%] break-words">
                        <div
                          className={`flex items-center-safe ${OVERFLOW_AUTO_STYLE}`}
                          style={{ height: `${heightStyle - 15}px` }}
                        >
                          <div>
                            <div className="mr-1">{items.name!}</div>
                            <div className="flex items-center">
                              {role && '(' + role + ')'}
                            </div>
                          </div>
                        </div>
                      </li>
                    </ul>
                  </Link>
                </div>
              );
            })}
          </div>
        </>
      )}
    </div>
  );
};

export default DisplayVideoCredits;
