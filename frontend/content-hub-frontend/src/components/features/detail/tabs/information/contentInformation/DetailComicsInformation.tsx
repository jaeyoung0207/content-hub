import { useTranslation } from 'react-i18next';
import DOMPurify from 'dompurify';
import { COMICS_CREDITS_TYPE } from '@/components/common/constants/constants';
import { DetailResponseType } from '../../../useDetail';
import { isDetailComicsType } from '@/components/common/utils/typeGuardUtil';
import DisplayComicsCredits from '@/components/ui/DisplayComicsCreditsUi';

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

  return (
    <div className="ml-5 mr-5">
      {overview && (
        <>
          {/* 개요 */}
          <div className="text-3xl font-bold mb-5">{t('info.overview')}</div>
          <div className="mb-10">
            <div dangerouslySetInnerHTML={{ __html: overview }}></div>
          </div>
        </>
      )}

      {detailResult &&
        isDetailComicsType(detailResult, contentMediaType) &&
        detailResult.characters &&
        detailResult.characters.edges &&
        detailResult.characters.edges.length > 0 && (
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

      {detailResult &&
        isDetailComicsType(detailResult, contentMediaType) &&
        detailResult.staff &&
        detailResult.staff.edges &&
        detailResult.staff.edges.length > 0 && (
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
