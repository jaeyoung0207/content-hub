import { useSearchParams } from 'react-router-dom';
import { Suspense, lazy } from 'react';
import { LoadingUi } from '@/components/ui/LoadingUi';

const Search = lazy(() => import('./Search'));
const SearchMore = lazy(() => import('./modal/SearchMore'));

/**
 * 검색 페이지 컴포넌트의 props 타입
 */
export type SearchPropsType = {
  keyword: string; // 검색어
  isAdult?: string; // 성인물 포함 여부
  displayMediaType?: string; // 미디어 타입(화면 표시용)
  viewMore?: string; // 전체보기 여부
  contentMediaType?: string; // 컨텐츠 미디어 타입
  apiId?: string; // API ID
  tabNo?: number; // 탭 번호
};

/**
 * 검색 페이지 컴포넌트
 * URL 쿼리 파라미터를 받아서 각 컴포넌트에 필요한 데이터를 전달하고 렌더링하는 컴포넌트
 */
export const SearchPage = () => {
  // URL 쿼리 파라미터를 가져오기 위한 훅
  const [searchParams] = useSearchParams();
  const keyword = searchParams.get('keyword')!; // 검색어
  const isAdult = searchParams.get('isAdult')!; // 성인물 포함 여부
  const displayMediaType = searchParams.get('viewMore'); // 전체보기 여부(화면 표시용 미디어 타입)

  return (
    <>
      {/* 검색 화면 컴포넌트 */}
      <Suspense fallback={<LoadingUi />}>
        <Search keyword={keyword} isAdult={isAdult} />
      </Suspense>
      {
        // 검색어, 성인물 포함 여부, 미디어 타입이 존재하는 경우
        // 전체보기 모달화면 컴포넌트를 렌더링
        keyword && isAdult && displayMediaType && (
          <Suspense fallback={<LoadingUi />}>
            <SearchMore
              keyword={keyword}
              isAdult={isAdult}
              displayMediaType={displayMediaType}
            />
          </Suspense>
        )
      }
    </>
  );
};

export default SearchPage;
