import { cn } from '@/lib/cn';
import { MouseEvent } from 'react';
import { RiCloseLargeFill } from 'react-icons/ri';

/**
 * 닫기 버튼 공통 컴포넌트 props 타입
 */
type CloseButtonPropsType = {
  modalClose: () => void;
  className?: string;
  disabled?: boolean;
  ariaLabel?: string; // 접근성 라벨
  stopPropagation?: boolean; // 부모 클릭 전파 차단 여부
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
  ariaLabel = '닫기',
  stopPropagation = true,
}: CloseButtonPropsType) => {
  const handleWrapperClick = (e: MouseEvent) => {
    if (stopPropagation) e.stopPropagation();
  };
  const handleClick = (e: MouseEvent) => {
    if (stopPropagation) e.stopPropagation();
    if (!disabled) modalClose();
  };

  return (
    <div
      className={cn('mt-1 mr-1 flex justify-end', className)}
      onClick={handleWrapperClick}
    >
      <button
        type="button"
        aria-label={ariaLabel}
        disabled={!!disabled}
        onClick={handleClick}
        className={cn(
          'text-foreground/90 inline-flex items-center justify-center rounded p-1 hover:bg-black/5',
          'focus-visible:ring-primary focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:outline-none',
          disabled && 'pointer-events-none opacity-50'
        )}
      >
        <RiCloseLargeFill className="aria-hidden size-6" />
      </button>
    </div>
  );
};
