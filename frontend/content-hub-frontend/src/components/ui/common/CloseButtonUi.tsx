import { RiCloseLargeFill } from 'react-icons/ri';

/**
 * 닫기 버튼 공통 컴포넌트 props 타입
 */
type CloseButtonPropsType = {
  modalClose: () => void;
  className?: string;
  disabled?: boolean;
};

/**
 * 닫기 버튼 공통 컴포넌트
 *
 * @param modalClose
 */
export const CloseButtonUi = ({
  modalClose,
  className,
  disabled,
}: CloseButtonPropsType) => {
  return (
    <div
      className={className ? className : 'flex justify-end mr-1 mt-1'}
      onClick={(e) => e.stopPropagation()}
    >
      <RiCloseLargeFill
        className="w-6 h-6 text-black cursor-pointer"
        onClick={() => !disabled && modalClose()}
      />
    </div>
  );
};
