import {
  VIDEO_CREDITS_TYPE,
  COMICS_CREDITS_TYPE,
} from '@/components/common/constants/constants';
import { VideoInformationPropsType } from '../contentInformation/VideoInformation';
import { ComicsCharacterInformation } from '../contentInformation/ComicsCharacterInformation';
import { memo } from 'react';
import DisplayVideoCredits from '@/components/ui/DisplayVideoCreditsUi';
import { useTranslation } from 'react-i18next';
import {
  isDetailComicsType,
  isDetailMovieType,
  isDetailTvType,
} from '@/components/common/utils/typeGuardUtil';
import { NodataMessageUi } from '@/components/ui/common/NodataMessageUi';
import { getContentMediaType } from '@/components/common/utils/convertUtil';

/**
 * 제작진 정보 컴포넌트
 * @param detailResult 상세 정보
 * @param contentMediaType 컨텐츠 미디어 타입
 * @param setObserveTarget 관찰 대상 ref 설정 함수
 * @param displayCount 표시할 항목 수
 */
export const CrewInformation = memo(
  ({
    detailResult,
    contentMediaType,
    setObserveTarget,
    displayCount,
  }: VideoInformationPropsType) => {
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
      <div className="ml-5 mr-5">
        {(contentMediaType === getContentMediaType().aniCode ||
          contentMediaType === getContentMediaType().dramaCode ||
          contentMediaType === getContentMediaType().movieCode ||
          contentMediaType === getContentMediaType().documentaryCode ||
          contentMediaType === getContentMediaType().kidsCode ||
          contentMediaType === getContentMediaType().newsCode ||
          contentMediaType === getContentMediaType().varietyCode) &&
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
            <NodataMessageUi message={t('warn.noStaffInfo')} />
          ))}
        {contentMediaType === getContentMediaType().comicsCode &&
          (isComicsStaff ? (
            <ComicsCharacterInformation
              detailResult={detailResult}
              contentMediaType={contentMediaType}
              creditsType={COMICS_CREDITS_TYPE.STAFF}
            />
          ) : (
            <NodataMessageUi message={t('warn.noStaffInfo')} />
          ))}
      </div>
    );
  }
);

export default CrewInformation;
