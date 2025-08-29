import { useTranslation } from 'react-i18next';
import { DetailResponseType } from '../../useDetail';
import { useComicsCharacterInformation } from './useComicsCharacterInformation';
import { LoadingUi } from '@/components/ui/LoadingUi';
import { NodataMessageUi } from '@/components/ui/common/NodataMessageUi';
import { DisplayComicsCredits } from './ComicsInformation';

/**
 * 만화 정보 컴포넌트 props 타입
 */
type ComicsCharacterInformationPropsType = {
  detailResult: DetailResponseType;
  originalMediaType: string;
  creditsType: string;
};

/**
 * 만화 정보 컴포넌트
 * @param detailResult 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 */
export const ComicsCharacterInformation = ({
  detailResult,
  originalMediaType,
  creditsType,
}: ComicsCharacterInformationPropsType) => {
  // i18n 번역 훅
  const { t } = useTranslation();

  const { data, isFetchingNextPage, hasNextPage, setObserveTarget } =
    useComicsCharacterInformation(detailResult, originalMediaType, creditsType);

  return (
    <div className="ml-5 mr-5">
      {data && data.pages.length > 0 ? (
        <>
          <DisplayComicsCredits
            contentId={detailResult.id!}
            originalMediaType={originalMediaType}
            creditsAllList={data.pages.flat()}
            creditsType={creditsType}
            isOmit={false}
          />
          {
            // 다음 페이지 로딩 중인 경우 로딩 UI 표시
            isFetchingNextPage && <LoadingUi />
          }
          {
            // 다음 페이지가 있는 경우 무한 스크롤을 위한 div 태그
            hasNextPage && (
              <div ref={(el) => setObserveTarget && setObserveTarget(el)}></div>
            )
          }
        </>
      ) : (
        <LoadingUi />
      )}
      {
        // 데이터가 없을 때 표시할 메시지
        data && data.pages[0]?.length === 0 && (
          <NodataMessageUi message={t('warn.noCharacterId')} />
        )
      }
    </div>
  );
};
