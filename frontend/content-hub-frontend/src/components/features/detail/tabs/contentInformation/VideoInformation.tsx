import { useTranslation } from 'react-i18next';
import { VIDEO_CREDITS_TYPE } from '@/components/common/constants/constants';
import { DetailResponseType } from '../../useDetail';
import {
  isDetailMovieType,
  isDetailTvType,
} from '@/components/common/utils/typeGuardUtil';
import DisplayVideoCredits from '@/components/ui/DisplayVideoCreditsUi';
import { Dispatch, SetStateAction } from 'react';

/**
 * 비디오 정보 컴포넌트 props 타입
 */
export type VideoInformationPropsType = {
  detailResult: DetailResponseType;
  contentMediaType: string;
  setObserveTarget?: Dispatch<SetStateAction<HTMLDivElement | null>>;
  displayCount?: number;
};

/**
 * 비디오(애니, 드라마, 영화) 정보 컴포넌트
 * @param detailResult 상세 정보 결과
 * @param contentMediaType 컨텐츠 미디어 타입
 */
export const VideoInformation = ({
  detailResult,
  contentMediaType,
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
        (isDetailTvType(detailResult, contentMediaType) ||
          isDetailMovieType(detailResult, contentMediaType)) && (
          <>
            {detailResult.credits &&
              detailResult.credits.cast &&
              detailResult.credits.cast.length !== 0 && (
                <div className="flex items-center">
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
                <div className="flex items-center">
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

export default VideoInformation;
