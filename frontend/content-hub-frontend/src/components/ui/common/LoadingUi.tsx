import { cn } from '@/lib/cn';

/**
 * LoadingUi 컴포넌트
 * 로딩 상태를 나타내는 회전하는 원형 스피너
 * @param className 추가적인 클래스 이름
 */
export const LoadingUi = ({ className }: { className?: string }) => {
  return (
    <div className="flex h-[80px] flex-col items-center justify-center gap-2 text-gray-500">
      <div>Loading...</div>
      <div
        className={cn(
          'border-t-primary inline-block size-5 animate-spin rounded-full border-2 border-black/10',
          className
        )}
        aria-label="로딩중"
        role="status"
      />
    </div>
  );
};
