import { forwardRef, InputHTMLAttributes } from 'react';
import { cn } from '@/lib/cn';

// 입력 필드 크기 타입
type Size = 'sm' | 'md' | 'lg';

// 입력 필드 크기 클래스
const sizes: Record<Size, string> = {
  sm: 'h-9 px-3 text-sm',
  md: 'h-10 px-4 text-sm',
  lg: 'h-11 px-4 text-base',
};

/**
 * InputUi 컴포넌트 Props 타입
 */
export interface InputUiProps extends InputHTMLAttributes<HTMLInputElement> {
  inputSize?: Size;
  invalid?: boolean;
}

/**
 * InputUi 컴포넌트
 * 사용자 입력을 받는 텍스트 필드 UI
 * @param className 추가적인 클래스 이름
 * @param inputSize 입력 필드 크기
 * @param invalid 유효성 검사 실패 여부
 */
export const InputUi = forwardRef<HTMLInputElement, InputUiProps>(
  ({ className, inputSize = 'md', invalid = false, ...props }, ref) => {
    return (
      <input
        ref={ref}
        className={cn(
          'text-foreground placeholder:text-muted-foreground/70 w-full rounded-md border bg-white outline-none',
          'focus-visible:ring-primary focus-visible:ring-2 focus-visible:ring-offset-2',
          invalid && 'border-danger focus-visible:ring-danger',
          !invalid && 'border-black/10',
          sizes[inputSize],
          className
        )}
        {...props}
      />
    );
  }
);
InputUi.displayName = 'InputUi';
