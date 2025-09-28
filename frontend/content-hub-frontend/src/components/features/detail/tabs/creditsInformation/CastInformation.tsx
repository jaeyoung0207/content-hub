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
 * 출연진 정보 컴포넌트
 * @param detailResult 상세 정보
 * @param contentMediaType 컨텐츠 미디어 타입
 * @param setObserveTarget 관찰 대상 ref 설정 함수
 * @param displayCount 표시할 항목 수
 */
export const CastInformation = memo(
  ({
    detailResult,
    contentMediaType,
    setObserveTarget,
    displayCount,
  }: VideoInformationPropsType) => {
    // i18n
    const { t } = useTranslation();
    // 비디오 출연진 정보 존재 여부
    const isVideoCast =
      (isDetailTvType(detailResult, contentMediaType) ||
        isDetailMovieType(detailResult, contentMediaType)) &&
      detailResult &&
      detailResult.credits &&
      detailResult.credits.cast &&
      detailResult.credits.cast.length !== 0;
    // 만화 캐릭터 정보 존재 여부
    const isComicsCharacter =
      isDetailComicsType(detailResult, contentMediaType) &&
      detailResult &&
      detailResult.characters &&
      detailResult.characters.edges &&
      detailResult.characters.edges.length !== 0;
    return (
      <div className="ml-5 mr-5">
        {(contentMediaType === getContentMediaType().aniCode ||
          contentMediaType === getContentMediaType().dramaCode ||
          contentMediaType === getContentMediaType().movieCode ||
          contentMediaType === getContentMediaType().documentaryCode ||
          contentMediaType === getContentMediaType().kidsCode ||
          contentMediaType === getContentMediaType().newsCode ||
          contentMediaType === getContentMediaType().varietyCode) &&
          (isVideoCast ? (
            <>
              <DisplayVideoCredits
                detailResult={detailResult}
                contentMediaType={contentMediaType}
                creditsType={VIDEO_CREDITS_TYPE.CAST}
                isOmit={false}
                displayCount={displayCount}
              />
              <div ref={(el) => setObserveTarget!(el)} />
            </>
          ) : (
            <NodataMessageUi message={t('warn.noCastInfo')} />
          ))}
        {contentMediaType === getContentMediaType().comicsCode &&
          (isComicsCharacter ? (
            <ComicsCharacterInformation
              detailResult={detailResult}
              contentMediaType={contentMediaType}
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
