import { useSearchParams } from 'react-router-dom';
import { Search } from './Search';
import { SearchMore } from './modal/SearchMore';

/**
 * 검색 페이지 컴포넌트의 props 타입
 */
export type SearchPropsType = {
  keyword: string; // 검색어
  isAdult?: string; // 성인물 포함 여부
  mediaType?: string; // 미디어 타입
  viewMore?: string; // 전체보기 여부
  originalMediaType?: string; // 원본 미디어 타입
  contentId?: string; // 콘텐츠 ID
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
  const viewMore = searchParams.get('viewMore'); // 전체보기 여부

  return (
    <>
      {/* 검색 화면 컴포넌트 */}
      <Search keyword={keyword} isAdult={isAdult} />
      {
        // 검색어, 성인물 포함 여부, 미디어 타입, 전체보기 여부가 존재하는 경우
        // 전체보기 모달화면 컴포넌트를 렌더링
        keyword && isAdult && viewMore && (
          <SearchMore
            keyword={keyword}
            isAdult={isAdult}
            mediaType={viewMore}
          />
        )
      }
    </>
  );
};
