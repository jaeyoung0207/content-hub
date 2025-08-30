import {
  VIDEO_CREDITS_TYPE,
  MEDIA_TYPE,
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

export const CrewInformation = memo(
  ({ detailResult, originalMediaType }: VideoInformationPropsType) => {
    // i18n
    const { t } = useTranslation();
    // 비디오 제작진 정보 존재 여부
    const isVideoCrew =
      (isDetailTvType(detailResult, originalMediaType) ||
        isDetailMovieType(detailResult, originalMediaType)) &&
      detailResult.credits &&
      detailResult.credits.crew &&
      detailResult.credits.crew.length !== 0;
    // 만화 제작진 정보 존재 여부
    const isComicsStaff =
      isDetailComicsType(detailResult, originalMediaType) &&
      detailResult.staff &&
      detailResult.staff.edges &&
      detailResult.staff.edges.length !== 0;
    return (
      <div className="ml-5 mr-5">
        {(originalMediaType === MEDIA_TYPE.ANI ||
          originalMediaType === MEDIA_TYPE.DRAMA ||
          originalMediaType === MEDIA_TYPE.MOVIE) &&
          (isVideoCrew ? (
            <DisplayVideoCredits
              detailResult={detailResult}
              originalMediaType={originalMediaType}
              creditsType={VIDEO_CREDITS_TYPE.CREW}
              isOmit={false}
            />
          ) : (
            <NodataMessageUi message={t('warn.noStaffInfo')} />
          ))}
        {originalMediaType === MEDIA_TYPE.COMICS &&
          (isComicsStaff ? (
            <ComicsCharacterInformation
              detailResult={detailResult}
              originalMediaType={originalMediaType}
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
