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
import { getContentMediaType } from '@/components/common/utils/convertUtil';

export const CrewInformation = memo(
  ({ detailResult, contentMediaType }: VideoInformationPropsType) => {
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
            <DisplayVideoCredits
              detailResult={detailResult}
              contentMediaType={contentMediaType}
              creditsType={VIDEO_CREDITS_TYPE.CREW}
              isOmit={false}
            />
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
