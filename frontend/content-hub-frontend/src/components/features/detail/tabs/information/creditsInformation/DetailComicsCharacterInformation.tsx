import { useTranslation } from 'react-i18next';
import { DetailResponseType } from '../../../useDetail';
import { useDetailComicsCharacterInformation } from './useDetailComicsCharacterInformation';
import { LoadingUi } from '@/components/ui/common/LoadingUi';
import { NoDataMessageUi } from '@/components/ui/common';
import DisplayComicsCredits from '@/components/ui/DisplayComicsCreditsUi';
import { COMICS_CREDITS_TYPE } from '@/components/common/constants/constants';

/**
 * 만화 캐릭터/제작진 정보 컴포넌트 props 타입
 */
type DetailComicsCharacterInformationPropsType = {
  detailResult: DetailResponseType;
  contentMediaType: string;
  creditsType: string;
};

/**
 * 만화 캐릭터/제작진 정보 컴포넌트
 * @param detailResult 상세 정보 결과
 * @param contentMediaType 컨텐츠 미디어 타입
 */
export const DetailComicsCharacterInformation = ({
  detailResult,
  contentMediaType,
  creditsType,
}: DetailComicsCharacterInformationPropsType) => {
  // i18n 번역 훅
  const { t } = useTranslation();

  // 만화 캐릭터/제작진 정보 무한스크롤 훅
  const { data, isFetchingNextPage, hasNextPage, setObserveTarget } =
    useDetailComicsCharacterInformation(
      detailResult,
      contentMediaType,
      creditsType
    );

  return (
    <div>
      {data && data.pages.length > 0 ? (
        <>
          <DisplayComicsCredits
            apiId={detailResult.id!}
            contentMediaType={contentMediaType}
            creditsAllList={data.pages.flat()}
            creditsType={creditsType}
            isOmit={false}
          />
          {
            // 다음 페이지 로딩 중인 경우 로딩 UI 표시
            isFetchingNextPage && (
              <div className="py-4">
                <LoadingUi />
              </div>
            )
          }
          {
            // 다음 페이지가 있는 경우 무한 스크롤을 위한 div 태그
            hasNextPage && (
              <div
                ref={(el) => setObserveTarget && setObserveTarget(el)}
                aria-hidden="true"
              ></div>
            )
          }
        </>
      ) : (
        <LoadingUi />
      )}
      {
        // 데이터가 없을 때 표시할 메시지
        data && data.pages[0]?.length === 0 && (
          <NoDataMessageUi
            message={
              creditsType === COMICS_CREDITS_TYPE.CHARACTER
                ? t('warn.noCharacterInfo')
                : t('warn.noStaffInfo')
            }
          />
        )
      }
    </div>
  );
};
