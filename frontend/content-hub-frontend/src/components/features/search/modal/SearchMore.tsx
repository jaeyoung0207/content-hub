import { SEARCH_SCREEN_TYPE } from '@/components/common/constants/constants';
import { CloseButtonUi } from '@/components/ui/common/CloseButtonUi';
import { useSearchMore } from './useSearchMore';
import { useTranslation } from 'react-i18next';
import { memo } from 'react';
import { LoadingUi } from '@/components/ui/common/LoadingUi';
import { SearchPropsType } from '../SearchPage';
import DisplaySearchResults from '@/components/ui/DisplaySearchResultsUi';
import { getDisplayMediaType } from '@/components/common/utils/convertUtil';

/**
 * 전체보기 모달화면 컴포넌트
 * @param keyword 검색어
 * @param isAdult 성인 콘텐츠 포함 여부
 * @param displayMediaType 미디어 타입
 */
export const SearchMore = memo(
  ({ keyword, isAdult, displayMediaType }: SearchPropsType) => {
    // i18n 번역 함수
    const { t } = useTranslation();
    // 성인 콘텐츠 포함 여부
    const adultFlag = isAdult === 'true';

    // 검색 결과를 가져오는 커스텀 훅
    const {
      setObserveTarget,
      data,
      hasNextPage,
      isFetchingNextPage,
      handleModalClose,
      onOverlayClick,
    } = useSearchMore(keyword, adultFlag, displayMediaType!);

    // 각 미디어 이름을 가져오는 함수
    const getMediaName = () => {
      if (displayMediaType === getDisplayMediaType().aniCode) {
        return 'info.animation';
      } else if (displayMediaType === getDisplayMediaType().dramaCode) {
        return 'info.drama';
      } else if (displayMediaType === getDisplayMediaType().movieCode) {
        return 'info.movie';
      } else if (displayMediaType === getDisplayMediaType().documentaryCode) {
        return 'info.documentary';
      } else if (displayMediaType === getDisplayMediaType().kidsCode) {
        return 'info.kids';
      } else if (displayMediaType === getDisplayMediaType().newsCode) {
        return 'info.news';
      } else if (displayMediaType === getDisplayMediaType().varietyCode) {
        return 'info.variety';
      } else if (displayMediaType === getDisplayMediaType().comicsCode) {
        return 'info.comics';
      } else {
        return 'info.variety';
      }
    };

    return (
      <>
        {
          <div
            className="fixed inset-0 z-50 flex items-start justify-center bg-black/40 p-3 backdrop-blur-sm sm:p-6"
            onClick={onOverlayClick}
          >
            <div
              role="dialog"
              aria-modal="true"
              aria-label={t(getMediaName())}
              className="relative h-[calc(100vh-1.5rem)] w-full max-w-screen-2xl overflow-y-auto rounded-xl bg-white shadow-2xl sm:h-[calc(100vh-3rem)]"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="px-4 py-4 sm:px-6 lg:px-8">
                {/* 닫기 버튼 */}
                <div className="mb-4">
                  <CloseButtonUi modalClose={handleModalClose} />
                </div>
                {data ? (
                  <div>
                    {/* 각 미디어 검색결과 컴포넌트 */}
                    <DisplaySearchResults
                      mediaName={t(getMediaName())}
                      results={data.pages.flat()}
                      displayMediaType={displayMediaType!}
                      keyword={keyword!}
                      isAdult={String(false)}
                      searchScreenType={SEARCH_SCREEN_TYPE.VIEW_MORE}
                    />
                    {
                      // 다음 페이지 로딩 중인 경우 로딩 UI 표시
                      isFetchingNextPage && (
                        <div className="py-6">
                          <LoadingUi />
                        </div>
                      )
                    }
                    {
                      // 다음 페이지가 있는 경우 무한 스크롤을 위한 div 태그
                      hasNextPage && (
                        <div
                          ref={(el) => setObserveTarget(el)}
                          className="h-1"
                        ></div>
                      ) // ref를 함수 형태로 지정 -> DOM이 생기거나 없어질 때마다 실행되면서 setObserveTarget을 호출
                    }
                  </div>
                ) : (
                  <LoadingUi />
                )}
              </div>
            </div>
          </div>
        }
      </>
    );
  }
);

export default SearchMore;
