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

export const CastInformation = memo(
  ({ detailResult, originalMediaType }: VideoInformationPropsType) => {
    // i18n
    const { t } = useTranslation();
    // 비디오 출연진 정보 존재 여부
    const isVideoCast =
      (isDetailTvType(detailResult, originalMediaType) ||
        isDetailMovieType(detailResult, originalMediaType)) &&
      detailResult &&
      detailResult.credits &&
      detailResult.credits.cast &&
      detailResult.credits.cast.length !== 0;
    // 만화 캐릭터 정보 존재 여부
    const isComicsCharacter =
      isDetailComicsType(detailResult, originalMediaType) &&
      detailResult &&
      detailResult.characters &&
      detailResult.characters.edges &&
      detailResult.characters.edges.length !== 0;
    return (
      <div className="ml-5 mr-5">
        {(originalMediaType === MEDIA_TYPE.ANI ||
          originalMediaType === MEDIA_TYPE.DRAMA ||
          originalMediaType === MEDIA_TYPE.MOVIE) &&
          (isVideoCast ? (
            <DisplayVideoCredits
              detailResult={detailResult}
              originalMediaType={originalMediaType}
              creditsType={VIDEO_CREDITS_TYPE.CAST}
              isOmit={false}
            />
          ) : (
            <NodataMessageUi message={t('warn.noCastInfo')} />
          ))}
        {originalMediaType === MEDIA_TYPE.COMICS &&
          (isComicsCharacter ? (
            <ComicsCharacterInformation
              detailResult={detailResult}
              originalMediaType={originalMediaType}
              creditsType={COMICS_CREDITS_TYPE.CHARACTER}
            />
          ) : (
            <NodataMessageUi message={t('warn.noCharacterInfo')} />
          ))}
      </div>
    );
  }
);

export default CastInformation;
