import ReactPaginate from 'react-paginate';
import { OMISSION_TEXT } from '../common/constants/constants';

/**
 * 페이지네이션 UI 컴포넌트 Props 타입
 */
type PagenationUiProps = {
  pageCount: number;
  pageRangeDisplayed: number;
  marginPagesDisplayed: number;
  onPageChange: (selectedItem: { selected: number }) => void;
  currentPage: number;
};

/**
 * 페이지네이션 UI 컴포넌트
 * - 디자인 토큰/포커스 링/반응형 터치 타깃 적용
 * @param pageCount 총 페이지 수
 * @param pageRangeDisplayed 한 번에 표시할 페이지 수
 * @param marginPagesDisplayed 양쪽에 표시할 페이지 수
 * @param currentPage 현재 페이지 (0부터 시작)
 * @param onPageChange 페이지 변경 시 호출되는 함수
 */
export const PagenationUi = ({
  pageCount,
  pageRangeDisplayed,
  marginPagesDisplayed,
  currentPage,
  onPageChange,
}: PagenationUiProps) => {
  return (
    <nav className="mt-4 flex justify-center" aria-label="페이지네이션">
      <ReactPaginate
        // 데이터
        pageCount={pageCount} // 전체 페이지 수
        pageRangeDisplayed={pageRangeDisplayed} // 한 번에 표시할 페이지 수
        marginPagesDisplayed={marginPagesDisplayed} // 양쪽에 표시할 페이지 수
        forcePage={currentPage} // 현재 페이지 (0부터 시작)
        onPageChange={onPageChange} // 페이지 변경 시 호출되는 함수
        renderOnZeroPageCount={null} // 페이지 수가 0일 때 렌더링하지 않음
        // 컨테이너
        containerClassName="flex items-center gap-1" // 페이지네이션 컨테이너 클래스 이름
        // 일반 페이지
        pageClassName="mx-0.5 cursor-pointer" // 페이지 아이템 클래스 이름
        pageLinkClassName="
          inline-flex h-9 min-w-9 items-center justify-center rounded-md
          border border-black/10 bg-white px-3 text-sm text-foreground
          hover:bg-muted
          focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-primary
        " // 페이지 링크 클래스 이름
        // 활성화된 페이지
        activeClassName="pointer-events-none" // 활성화된 페이지 클래스 이름
        activeLinkClassName="bg-primary underline hover:bg-primary" // 활성화된 페이지 링크 클래스 이름
        // 생략
        breakClassName="mx-0.5"
        breakLinkClassName="inline-flex h-9 min-w-9 items-center justify-center px-2 text-sm text-muted-foreground"
        breakLabel={OMISSION_TEXT} // 생략 표시
        // 이전/다음
        previousClassName="mx-1 cursor-pointer" // 이전 버튼 클래스 이름
        nextClassName="mx-1 cursor-pointer" // 다음 버튼 클래스 이름
        previousLinkClassName="
          inline-flex h-9 items-center justify-center rounded-md
          border border-black/10 bg-white px-3 text-sm text-foreground
          hover:bg-muted
          focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-primary
        " // 이전 버튼 링크 클래스 이름
        nextLinkClassName="
          inline-flex h-9 items-center justify-center rounded-md
          border border-black/10 bg-white px-3 text-sm text-foreground
          hover:bg-muted
          focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-primary
        " // 다음 버튼 링크 클래스 이름
        previousLabel="previous" // 이전 버튼 라벨
        nextLabel="next" // 다음 버튼 라벨
        // 비활성 상태(양 끝)
        disabledClassName="opacity-50 pointer-events-none"
        disabledLinkClassName="cursor-default"
      />
    </nav>
  );
};
