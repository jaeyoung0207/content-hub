import { cn } from '@/lib/cn';

/**
 * SkeletonUi 컴포넌트
 * 로딩 중인 콘텐츠를 나타내는 회색 블록 UI
 * @param className 추가적인 클래스 이름
 */
export const SkeletonUi = ({ className }: { className?: string }) => {
  return (
    <div className={cn('animate-pulse rounded-md bg-black/5', className)} />
  );
};
