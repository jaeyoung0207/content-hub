import { DetailResponseType } from '../../useDetail';
import { useRecommendationContent } from './useDetailRecommendation';
import {
  MEDIA_TYPE_KIND,
  SEARCH_SCREEN_TYPE,
} from '@/components/common/constants/constants';
import { LoadingUi } from '@/components/ui/common/LoadingUi';
import { NoDataMessageUi } from '@/components/ui/common';
import { useTranslation } from 'react-i18next';
import { memo } from 'react';
import DisplaySearchResults from '@/components/ui/DisplaySearchResultsUi';
import { mappingToMediaType } from '@/components/common/utils/convertUtil';

/**
 * 추천 콘텐츠 컴포넌트 props 타입
 */
type RecommendationContentPropsType = {
  detailResult: DetailResponseType;
  contentMediaType: string;
};

/**
 * 추천 콘텐츠 컴포넌트
 * @param detailResult 상세 정보 결과
 * @param contentMediaType 컨텐츠 미디어 타입
 * @returns 추천 콘텐츠 컴포넌트
 */
export const RecommendationContent = memo(
  ({ detailResult, contentMediaType }: RecommendationContentPropsType) => {
    // i18n 번역 훅
    const { t } = useTranslation();

    // 화면 표시용 미디어 타입으로 변환
    const displayMediaType = mappingToMediaType(
      contentMediaType,
      MEDIA_TYPE_KIND.DISPLAY_MEDIA_TYPE
    )!;

    // 추천 콘텐츠를 가져오기 위한 커스텀 훅 호출
    const { data, isFetchingNextPage, hasNextPage, setObserveTarget } =
      useRecommendationContent(detailResult, displayMediaType);

    return (
      <section className="lg:px-8">
        <h2 className="mb-4 text-xl font-bold sm:text-2xl">
          {t('info.recommend')}
        </h2>
        {data ? (
          <div>
            {/* 추천 콘텐츠 결과 표시 */}
            <DisplaySearchResults
              mediaName={t('info.recommendation')}
              results={data.pages.flat()}
              displayMediaType={displayMediaType}
              keyword={''}
              isAdult={'false'}
              searchScreenType={SEARCH_SCREEN_TYPE.RECOMMENDATION}
            />
            {
              // 다음 페이지 로딩 중인 경우 로딩 UI 표시
              isFetchingNextPage && <LoadingUi />
            }
            {
              // 다음 페이지가 있는 경우 무한 스크롤을 위한 div 태그
              hasNextPage && (
                <div
                  ref={(el) => setObserveTarget(el)}
                  className="h-1"
                  aria-hidden="true"
                />
              ) // ref를 함수 형태로 지정 -> DOM이 생기거나 없어질 때마다 실행되면서 setObserveTarget을 호출
            }
          </div>
        ) : (
          <div className="py-6">
            <LoadingUi />
          </div>
        )}
        {
          // 추천 콘텐츠 데이터가 없을 때 표시할 메시지
          data && data.pages[0]?.length === 0 && (
            <NoDataMessageUi message={t('warn.noRecommendationInfo')} />
          )
        }
      </section>
    );
  }
);

export default RecommendationContent;
