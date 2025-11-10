import { useTranslation } from 'react-i18next';
import DOMPurify from 'dompurify';
import { COMICS_CREDITS_TYPE } from '@/components/common/constants/constants';
import { DetailResponseType } from '../../../useDetail';
import { isDetailComicsType } from '@/components/common/utils/typeGuardUtil';
import DisplayComicsCredits from '@/components/ui/DisplayComicsCreditsUi';
import { isMobileOnly } from 'react-device-detect';

/**
 * 만화 정보 컴포넌트 props 타입
 */
type DetailComicsInformationPropsType = {
  detailResult: DetailResponseType;
  contentMediaType: string;
};

/**
 * 만화 정보 컴포넌트
 * @param detailResult 상세 정보 결과
 * @param contentMediaType 컨텐츠 미디어 타입
 */
export const DetailComicsInformation = ({
  detailResult,
  contentMediaType,
}: DetailComicsInformationPropsType) => {
  // i18n 번역 훅
  const { t } = useTranslation();

  const characterList = isDetailComicsType(detailResult, contentMediaType)
    ? (detailResult.characters?.edges ?? [])
    : [];

  const staffList = isDetailComicsType(detailResult, contentMediaType)
    ? (detailResult.staff?.edges ?? [])
    : [];

  // 개요 변수 선언
  // DOMPurify를 사용하여 XSS 공격을 방지하며 HTML로 처리
  const overview =
    detailResult.overview && DOMPurify.sanitize(detailResult.overview);

  const isCharacters = !isMobileOnly && characterList.length > 0;
  const isStaff = !isMobileOnly && staffList.length > 0;

  return (
    <div>
      {overview && (
        <>
          {/* 개요 */}
          <div className="mb-3 text-2xl font-bold sm:text-3xl">
            {t('info.overview')}
          </div>
          <div className="mb-8 text-sm leading-relaxed whitespace-pre-line sm:text-base">
            <div dangerouslySetInnerHTML={{ __html: overview }}></div>
          </div>
        </>
      )}

      {isCharacters && (
        <>
          {/* 캐릭터 */}
          <DisplayComicsCredits
            apiId={detailResult.id!}
            creditsAllList={characterList}
            contentMediaType={contentMediaType}
            creditsType={COMICS_CREDITS_TYPE.CHARACTER}
            isOmit={true}
          />
        </>
      )}

      {isStaff && (
        <>
          {/* 제작진 */}
          <DisplayComicsCredits
            apiId={detailResult.id!}
            creditsAllList={staffList}
            contentMediaType={contentMediaType}
            creditsType={COMICS_CREDITS_TYPE.STAFF}
            isOmit={true}
          />
        </>
      )}
    </div>
  );
};

export default DetailComicsInformation;
