import { useTranslation } from 'react-i18next';
import { VideoInformationPropsType } from '../features/detail/tabs/contentInformation/VideoInformation';
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

/**
 * 크레딧 정보 표시 컴포넌트 props 타입
 */
type DisplayVideoCreditsPropsType = VideoInformationPropsType & {
  creditsType: string;
  isOmit?: boolean;
};

/**
 * 크레딧 정보 표시 컴포넌트
 * @param detailResult 상세 정보
 * @param originalMediaType 원본 미디어 타입
 * @param creditsType 크레딧 타입
 * @param isOmit 생략 여부
 */
export const DisplayVideoCredits = ({
  detailResult,
  originalMediaType,
  creditsType,
  isOmit,
}: DisplayVideoCreditsPropsType) => {
  // i18n
  const { t } = useTranslation();
  // 썸네일 이미지 경로
  const thumbnailImagePath = TMDB_API_IMAGE_DOMAIN + WIDTH_185;
  // 비디오(TV or Movie) 크레딧
  const videoCredits =
    isDetailTvType(detailResult, originalMediaType) ||
    isDetailMovieType(detailResult, originalMediaType)
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
      : creditsAll);
  // 링크 스타일
  const linkStyle = 'ml-5 hover:font-bold';
  // 탭 번호
  const tabNo =
    creditsType === VIDEO_CREDITS_TYPE.CAST
      ? DETAIL_TAB_ID.cast
      : DETAIL_TAB_ID.crew;
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
            <div className="text-lx">
              {isOmit && creditsAll.length > settings.detailVideoCount && (
                <Link
                  to={detailUrlQuery({
                    originalMediaType: originalMediaType,
                    contentId: String(detailResult.id),
                    tabNo: tabNo,
                  })}
                  className={linkStyle}
                >
                  {t('info.seeMore') + ' >'}
                </Link>
              )}
            </div>
          </div>
          <div className="flex flex-wrap items-start mt-5">
            {creditsList.map((items, index) => {
              const role = isDetailCreditsCrewType(items)
                ? isDetailTvType(detailResult, originalMediaType)
                  ? items.jobs?.map((job) => job.job).join(SEPERATE_SLASH)
                  : items.job
                : isDetailTvType(detailResult, originalMediaType)
                  ? items.roles
                      ?.map((role) => role.character)
                      .join(SEPERATE_SLASH)
                  : items.character;
              return (
                <div
                  key={index}
                  className="ml-1 mr-1 w-[390px] h-[190px]"
                  onClick={() => checkPersonId(items.id)}
                >
                  <Link
                    to={items.id ? personUrlQuery({ personId: items.id }) : '#'}
                  >
                    <ul className="flex hover:font-bold w-full h-full">
                      <li className="flex justify-center items-center max-w-[30%]">
                        <LazyImage
                          src={
                            items.profilePath
                              ? thumbnailImagePath + items.profilePath
                              : COMMON_IMAGES.NO_IMAGE
                          }
                          alt={items.name}
                        />
                      </li>
                      <li className="flex items-center ml-4 mr-1 text-lg w-[70%] break-words">
                        <div className="block">
                          <span className="mr-1">{items.name!}</span>
                          <span className="flex items-center">
                            {role && '(' + role + ')'}
                          </span>
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
