import { forwardRef, ButtonHTMLAttributes } from 'react';
import { cn } from '@/lib/cn';

// 버튼 변형 및 크기 타입
type Variant = 'primary' | 'secondary' | 'ghost' | 'danger';
type Size = 'sm' | 'md' | 'lg';

// 버튼 기본 클래스
const base =
  'inline-flex items-center justify-center font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:opacity-50 disabled:pointer-events-none rounded-md';

// 버튼 변형 클래스
const variants: Record<Variant, string> = {
  primary:
    'bg-primary text-white hover:bg-primary/90 focus-visible:ring-primary',
  secondary:
    'bg-secondary text-secondary-foreground hover:bg-secondary/90 focus-visible:ring-secondary',
  ghost: 'bg-transparent hover:bg-muted text-foreground',
  danger: 'bg-danger text-white hover:bg-danger/90 focus-visible:ring-danger',
};

// 버튼 크기 클래스
const sizes: Record<Size, string> = {
  sm: 'h-9 px-3 text-sm',
  md: 'h-10 px-4 text-sm',
  lg: 'h-11 px-5 text-base',
};

/**
 * ButtonUi 컴포넌트 Props 타입
 */
interface ButtonUiProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
  size?: Size;
}

/**
 * ButtonUi 컴포넌트
 * 다양한 스타일과 크기를 지원하는 버튼 UI
 * @param variant 버튼 변형 스타일
 * @param size 버튼 크기
 */
export const ButtonUi = forwardRef<HTMLButtonElement, ButtonUiProps>(
  ({ className, variant = 'primary', size = 'md', ...props }, ref) => {
    return (
      <button
        ref={ref}
        className={cn(base, variants[variant], sizes[size], className)}
        {...props}
      />
    );
  }
);

// 디버깅을 위한 컴포넌트 이름 설정
ButtonUi.displayName = 'ButtonUi';
