import { forwardRef, TextareaHTMLAttributes } from 'react';
import { cn } from '@/lib/cn';

// TextareaUi 크기 타입 및 클래스 맵핑
type Size = 'sm' | 'md' | 'lg';

// TextareaUi 크기별 클래스 정의
const sizes: Record<Size, string> = {
  sm: 'min-h-24 p-3 text-sm',
  md: 'min-h-32 p-4 text-sm',
  lg: 'min-h-40 p-4 text-base',
};

/**
 * TextareaUiProps 타입
 */
export interface TextareaUiProps
  extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  textareaSize?: Size; // 크기 옵션
  invalid?: boolean; // 유효성 상태
}

/**
 * TextareaUi 컴포넌트
 * 다양한 크기와 유효성 상태를 지원하는 텍스트 영역 컴포넌트
 */
export const TextareaUi = forwardRef<HTMLTextAreaElement, TextareaUiProps>(
  ({ className, textareaSize = 'md', invalid = false, ...props }, ref) => {
    return (
      <textarea
        ref={ref}
        className={cn(
          'text-foreground placeholder:text-muted-foreground/70 w-full resize-none rounded-md border bg-white outline-none',
          'focus-visible:ring-primary focus-visible:ring-2 focus-visible:ring-offset-2',
          invalid
            ? 'border-danger focus-visible:ring-danger'
            : 'border-black/10',
          sizes[textareaSize],
          className
        )}
        {...props}
      />
    );
  }
);
TextareaUi.displayName = 'TextareaUi';
