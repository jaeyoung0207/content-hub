import { useTranslation } from 'react-i18next';
import {
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_185,
  COMMON_IMAGES,
  SEPERATE_SLASH,
  VIDEO_CREDITS_TYPE,
  DETAIL_TAB_ID,
} from '@/components/common/constants/constants';
import { DetailResponseType } from '../../useDetail';
import {
  isDetailCreditsCrewType,
  isDetailMovieType,
  isDetailTvType,
} from '@/components/common/utils/typeGuardUtil';
import {
  detailUrlQuery,
  personUrlQuery,
} from '@/components/common/utils/urlUtil';
import { checkPersonId } from '@/components/common/utils/checkUtil';
import { Link } from 'react-router-dom';
import { settings } from '@/components/common/config/settings';

/**
 * 비디오 정보 컴포넌트 props 타입
 */
export type VideoInformationPropsType = {
  detailResult: DetailResponseType;
  originalMediaType: string;
};

type DisplayVideoCreditsPropsType = VideoInformationPropsType & {
  creditsType: string;
  isOmit?: boolean;
};

/**
 * 비디오(애니, 드라마, 영화) 정보 컴포넌트
 * @param detailResult 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 */
export const VideoInformation = ({
  detailResult,
  originalMediaType,
}: VideoInformationPropsType) => {
  // i18n 번역 훅
  const { t } = useTranslation();
  return (
    <div className="ml-5 mr-5">
      {detailResult.overview && (
        <>
          {/* 개요 */}
          <div className="text-3xl font-bold mb-5">{t('info.overview')}</div>
          <div className="mb-10">{detailResult.overview}</div>
        </>
      )}

      {
        // 상세 정보 결과의 타입이 TV 또는 MOVIE인 경우
        (isDetailTvType(detailResult, originalMediaType) ||
          isDetailMovieType(detailResult, originalMediaType)) && (
          <>
            {detailResult.credits &&
              detailResult.credits.cast &&
              detailResult.credits.cast.length !== 0 && (
                <div className="flex items-center">
                  {/* 출연진 */}
                  <DisplayVideoCredits
                    detailResult={detailResult}
                    originalMediaType={originalMediaType}
                    creditsType={VIDEO_CREDITS_TYPE.CAST}
                    isOmit={true}
                  />
                </div>
              )}
            {detailResult.credits &&
              detailResult.credits.crew &&
              detailResult.credits.crew.length !== 0 && (
                <div className="flex items-center">
                  {/* 제작진 */}
                  <DisplayVideoCredits
                    detailResult={detailResult}
                    originalMediaType={originalMediaType}
                    creditsType={VIDEO_CREDITS_TYPE.CREW}
                    isOmit={true}
                  />
                </div>
              )}
          </>
        )
      }
    </div>
  );
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
          <div className="flex justify-center flex-wrap items-start mt-5">
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
                        <img
                          src={thumbnailImagePath + items.profilePath}
                          onError={(e) => {
                            e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                          }}
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
