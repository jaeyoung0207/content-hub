import { cn } from '@/lib/cn';
import { PropsWithChildren } from 'react';

/**
 * 컨테이너 컴포넌트
 * - 중앙 정렬 + 반응형 패딩
 * - Tailwind container는 뷰포트별 최대폭을 자동 관리
 * @param className 추가적인 CSS 클래스 이름
 * @param children 컨테이너 내부에 렌더링될 자식 요소들
 * @returns 페이지 컨테이너 컴포넌트
 */
export const PageContainer = ({
  className,
  children,
}: PropsWithChildren<{ className?: string }>) => {
  return (
    <div className={cn('container mx-auto px-4 md:px-6 lg:px-8', className)}>
      {children}
    </div>
  );
};
