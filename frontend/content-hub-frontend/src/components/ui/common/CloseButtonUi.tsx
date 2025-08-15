/**
 * 닫기 버튼 공통 컴포넌트 props 타입
 */
type CloseButtonPropsType = {
  modalClose: () => void;
};

/**
 * 닫기 버튼 공통 컴포넌트
 *
 * @param modalClose
 */
export const CloseButtonUi = ({ modalClose }: CloseButtonPropsType) => {
  return (
    <div className="flex justify-end mr-1 mt-1">
      <button
        className="w-10 h-10 border rounded-sm text-white bg-blue-600 cursor-pointer"
        onClick={modalClose}
      >
        X
      </button>
    </div>
  );
};
