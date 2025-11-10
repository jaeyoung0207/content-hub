import { cn } from '@/lib/cn';

type Placement = 'top' | 'bottom' | 'left' | 'right';
type Align = 'start' | 'center' | 'end';
type Offset = 'none' | 'xs' | 'sm' | 'md';
type HideBelow = 'none' | 'sm' | 'md' | 'lg';
type MaxW = 'xs' | 'sm' | 'md';

/**
 * 툴팁 컴포넌트 Props 타입
 */
type TooltipUiProps = {
  text: string;
  className?: string; // 배치/위치 지정용 클래스
  placement?: Placement; // 기본: bottom
  align?: Align; // 기본: center
  offset?: Offset; // 기본: sm (대략 mt-1 / mb-1 / ml-1 / mr-1)
  hideBelow?: HideBelow; // 기본: sm (모바일 숨김)
  pointerEvents?: boolean; // 기본: false (툴팁 위에서 마우스 이벤트 차단, 툴팁 위에서 마우스 이벤트 허용이 필요할 때만 true)
  maxW?: MaxW; // 기본: xs
};

/**
 * 툴팁 컴포넌트
 * 툴팁은 일반적으로 호버 시 표시되므로 포인터 이벤트를 차단하여 툴팁이 사라지는 것을 방지
 * (툴팁 위에 마우스가 올라가더라도 툴팁이 사라지지 않도록) 합니다.
 * 필요에 따라 pointerEvents prop을 true로 설정하여 툴팁 위에서 마우스 이벤트를 허용할 수 있습니다.
 * 사용 전제: 부모 요소가 "position: relative"여야 함
 */
export const TooltipUi = ({
  text,
  className,
  placement = 'bottom',
  align = 'center',
  offset = 'sm',
  hideBelow = 'sm',
  pointerEvents = false,
  maxW = 'xs',
}: TooltipUiProps) => {
  // 배치
  const basePos =
    placement === 'bottom'
      ? 'top-full'
      : placement === 'top'
        ? 'bottom-full'
        : placement === 'left'
          ? 'right-full'
          : 'left-full';
  // 정렬
  const alignPos =
    placement === 'top' || placement === 'bottom'
      ? align === 'center'
        ? 'left-1/2 -translate-x-1/2'
        : align === 'start'
          ? 'left-0'
          : 'right-0'
      : // left/right
        align === 'center'
        ? 'top-1/2 -translate-y-1/2'
        : align === 'start'
          ? 'top-0'
          : 'bottom-0';
  // 오프셋(여백) - Tailwind 정적 클래스만 사용
  const offsetMap: Record<Offset, string> = {
    none: '',
    xs:
      placement === 'bottom'
        ? 'mt-0.5'
        : placement === 'top'
          ? 'mb-0.5'
          : placement === 'left'
            ? 'mr-0.5'
            : 'ml-0.5',
    sm:
      placement === 'bottom'
        ? 'mt-1'
        : placement === 'top'
          ? 'mb-1'
          : placement === 'left'
            ? 'mr-1'
            : 'ml-1',
    md:
      placement === 'bottom'
        ? 'mt-2'
        : placement === 'top'
          ? 'mb-2'
          : placement === 'left'
            ? 'mr-2'
            : 'ml-2',
  };
  // 반응형 표시 제어
  const hideMap: Record<HideBelow, string> = {
    none: '',
    sm: 'hidden sm:block',
    md: 'hidden md:block',
    lg: 'hidden lg:block',
  };
  // 최대 너비
  const maxWMap: Record<MaxW, string> = {
    xs: 'max-w-xs',
    sm: 'max-w-sm',
    md: 'max-w-md',
  };

  return (
    <div
      role="tooltip"
      className={cn(
        'text-foreground absolute z-50 w-auto rounded border border-black/10 bg-white px-2 py-1 text-sm break-words shadow-lg',
        maxWMap[maxW],
        basePos,
        alignPos,
        offsetMap[offset],
        pointerEvents ? '' : 'pointer-events-none select-none',
        hideMap[hideBelow],
        // className 병합
        className
      )}
    >
      {text}
    </div>
  );
};

export default TooltipUi;
