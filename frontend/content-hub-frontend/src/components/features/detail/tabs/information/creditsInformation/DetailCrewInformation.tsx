import {
  VIDEO_CREDITS_TYPE,
  COMICS_CREDITS_TYPE,
} from '@/components/common/constants/constants';
import { DetailVideoInformationPropsType } from '../contentInformation/DetailVideoInformation';
import { DetailComicsCharacterInformation } from './DetailComicsCharacterInformation';
import { memo } from 'react';
import DisplayVideoCredits from '@/components/ui/DisplayVideoCreditsUi';
import { useTranslation } from 'react-i18next';
import {
  isDetailComicsType,
  isDetailMovieType,
  isDetailTvType,
} from '@/components/common/utils/typeGuardUtil';
import { NoDataMessageUi } from '@/components/ui/common';
import { getContentMediaType } from '@/components/common/utils/convertUtil';

/**
 * 제작진 정보 컴포넌트
 * @param detailResult 상세 정보
 * @param contentMediaType 컨텐츠 미디어 타입
 * @param setObserveTarget 관찰 대상 ref 설정 함수
 * @param displayCount 표시할 항목 수
 */
export const DetailCrewInformation = memo(
  ({
    detailResult,
    contentMediaType,
    setObserveTarget,
    displayCount,
  }: DetailVideoInformationPropsType) => {
    // i18n
    const { t } = useTranslation();
    // 비디오 제작진 정보 존재 여부
    const isVideoCrew =
      (isDetailTvType(detailResult, contentMediaType) ||
        isDetailMovieType(detailResult, contentMediaType)) &&
      detailResult.credits &&
      detailResult.credits.crew &&
      detailResult.credits.crew.length !== 0;
    // 만화 제작진 정보 존재 여부
    const isComicsStaff =
      isDetailComicsType(detailResult, contentMediaType) &&
      detailResult.staff &&
      detailResult.staff.edges &&
      detailResult.staff.edges.length !== 0;
    return (
      <div className="lg:px-8">
        {contentMediaType !== getContentMediaType().comicsCode &&
          (isVideoCrew ? (
            <>
              <DisplayVideoCredits
                detailResult={detailResult}
                contentMediaType={contentMediaType}
                creditsType={VIDEO_CREDITS_TYPE.CREW}
                isOmit={false}
                displayCount={displayCount}
              />
              <div ref={(el) => setObserveTarget!(el)} />
            </>
          ) : (
            <NoDataMessageUi message={t('warn.noStaffInfo')} />
          ))}
        {contentMediaType === getContentMediaType().comicsCode &&
          (isComicsStaff ? (
            <DetailComicsCharacterInformation
              detailResult={detailResult}
              contentMediaType={contentMediaType}
              creditsType={COMICS_CREDITS_TYPE.STAFF}
            />
          ) : (
            <NoDataMessageUi message={t('warn.noStaffInfo')} />
          ))}
      </div>
    );
  }
);

export default DetailCrewInformation;
