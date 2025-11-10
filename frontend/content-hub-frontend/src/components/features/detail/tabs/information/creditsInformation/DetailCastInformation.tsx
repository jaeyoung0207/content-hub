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
import { NoDataMessageUi } from '@/components/ui/common/NoDataMessageUi';
import { getContentMediaType } from '@/components/common/utils/convertUtil';

/**
 * 출연진 정보 컴포넌트
 * @param detailResult 상세 정보
 * @param contentMediaType 컨텐츠 미디어 타입
 * @param setObserveTarget 관찰 대상 ref 설정 함수
 * @param displayCount 표시할 항목 수
 */
export const DetailCastInformation = memo(
  ({
    detailResult,
    contentMediaType,
    setObserveTarget,
    displayCount,
  }: DetailVideoInformationPropsType) => {
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
      <div>
        {contentMediaType !== getContentMediaType().comicsCode &&
          (isVideoCast ? (
            <>
              <DisplayVideoCredits
                detailResult={detailResult}
                contentMediaType={contentMediaType}
                creditsType={VIDEO_CREDITS_TYPE.CAST}
                isOmit={false}
                displayCount={displayCount}
              />
              <div ref={(el) => setObserveTarget!(el)} aria-hidden="true" />
            </>
          ) : (
            <NoDataMessageUi message={t('warn.noCastInfo')} />
          ))}
        {contentMediaType === getContentMediaType().comicsCode &&
          (isComicsCharacter ? (
            <DetailComicsCharacterInformation
              detailResult={detailResult}
              contentMediaType={contentMediaType}
              creditsType={COMICS_CREDITS_TYPE.CHARACTER}
            />
          ) : (
            <NoDataMessageUi message={t('warn.noCharacterInfo')} />
          ))}
      </div>
    );
  }
);

export default DetailCastInformation;
