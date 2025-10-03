import ReactPaginate from 'react-paginate';
import { OMMIT_TEXT } from '../common/constants/constants';

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
 * @param pageCount 총 페이지 수
 * @param pageRangeDisplayed 한 번에 표시할 페이지 수
 * @param marginPagesDisplayed 양쪽에 표시할 페이지 수
 * @param onPageChange 페이지 변경 시 호출되는 함수
 * @param currentPage 현재 페이지 (0부터 시작)
 * @returns 페이지네이션 UI 컴포넌트
 */
export const PagenationUi = ({
  pageCount,
  pageRangeDisplayed,
  marginPagesDisplayed,
  currentPage,
  onPageChange,
}: PagenationUiProps) => {
  return (
    <div className="flex justify-center mt-4">
      <ReactPaginate
        pageCount={pageCount} // 전체 페이지 수
        pageRangeDisplayed={pageRangeDisplayed} // 한 번에 표시할 페이지 수
        marginPagesDisplayed={marginPagesDisplayed} // 양쪽에 표시할 페이지 수
        forcePage={currentPage} // 현재 페이지 (0부터 시작)
        onPageChange={onPageChange} // 페이지 변경 시 호출되는 함수
        containerClassName="flex items-center justify-center" // 페이지네이션 컨테이너 클래스 이름
        activeClassName="bg-blue-600 text-lg underline cursor-default" // 활성화된 페이지 클래스 이름
        breakLabel={OMMIT_TEXT} // 생략 표시
        // breakClassName="break-me" // 생략 표시 클래스 이름
        pageClassName="flex items-center justify-center w-8 h-8 mx-1 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded hover:bg-gray-100 cursor-pointer" // 페이지 아이템 클래스 이름
        pageLinkClassName="flex items-center justify-center w-full h-full" // 페이지 링크 클래스 이름
        nextLabel="next" // 다음 페이지 라벨
        previousLabel="previous" // 이전 페이지 라벨
        nextClassName="flex items-center justify-center mx-1 w-10 h-8 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded hover:bg-gray-100 cursor-pointer" // 다음 페이지 클래스 이름
        nextLinkClassName="flex items-center justify-center w-full h-full" // 다음 페이지 링크 클래스 이름
        previousClassName="flex items-center justify-center mx-1 w-16 h-8 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded hover:bg-gray-100 cursor-pointer" // 이전 페이지 클래스 이름
        previousLinkClassName="flex items-center justify-center w-full h-full" // 이전 페이지 링크 클래스 이름
      />
    </div>
  );
};
