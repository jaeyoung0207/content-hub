import { useTranslation } from 'react-i18next';
import { VIDEO_CREDITS_TYPE } from '@/components/common/constants/constants';
import { DetailResponseType } from '../../../useDetail';
import {
  isDetailMovieType,
  isDetailTvType,
} from '@/components/common/utils/typeGuardUtil';
import DisplayVideoCredits from '@/components/ui/DisplayVideoCreditsUi';
import { Dispatch, SetStateAction } from 'react';
import { isMobileOnly } from 'react-device-detect';

/**
 * 비디오 정보 컴포넌트 props 타입
 */
export type DetailVideoInformationPropsType = {
  detailResult: DetailResponseType;
  contentMediaType: string;
  setObserveTarget?: Dispatch<SetStateAction<HTMLDivElement | null>>;
  displayCount?: number;
};

/**
 * 비디오 정보 컴포넌트
 * @param detailResult 상세 정보 결과
 * @param contentMediaType 컨텐츠 미디어 타입
 */
export const DetailVideoInformation = ({
  detailResult,
  contentMediaType,
}: DetailVideoInformationPropsType) => {
  // i18n 번역 훅
  const { t } = useTranslation();

  // 공통 클래스
  const commonClass = 'flex items-center';

  return (
    <div className="lg:px-8">
      {detailResult.overview && (
        <>
          {/* 개요 */}
          <div className="mb-3 text-2xl font-bold sm:text-3xl">
            {t('info.overview')}
          </div>
          <div className="mb-8 text-sm leading-relaxed whitespace-pre-line sm:text-base">
            {detailResult.overview}
          </div>
        </>
      )}

      {
        // 모바일이 아니고 TV/MOVIE 타입일 때
        !isMobileOnly &&
          (isDetailTvType(detailResult, contentMediaType) ||
            isDetailMovieType(detailResult, contentMediaType)) && (
            <>
              {detailResult.credits &&
                detailResult.credits.cast &&
                detailResult.credits.cast.length !== 0 && (
                  <div className={commonClass}>
                    {/* 출연진 */}
                    <DisplayVideoCredits
                      detailResult={detailResult}
                      contentMediaType={contentMediaType}
                      creditsType={VIDEO_CREDITS_TYPE.CAST}
                      isOmit={true}
                    />
                  </div>
                )}
              {detailResult.credits &&
                detailResult.credits.crew &&
                detailResult.credits.crew.length !== 0 && (
                  <div className={commonClass}>
                    {/* 제작진 */}
                    <DisplayVideoCredits
                      detailResult={detailResult}
                      contentMediaType={contentMediaType}
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

export default DetailVideoInformation;
