import { useInView } from 'react-intersection-observer';
import { COMMON_IMAGES } from '@/components/common/constants/constants';
import { useState } from 'react';
import { cn } from '@/lib/cn';
import { SkeletonUi } from './SkeletonUi';

/**
 * LazyImageProps 타입
 */
type LazyImageProps = {
  src: string; // 이미지 소스 URL
  alt?: string; // 대체 텍스트
  wrapperClassName?: string; // 외부 래퍼 클래스
  className?: string; // 이미지 클래스
};

/**
 * LazyImage 컴포넌트
 * 뷰포트에 들어왔을 때 이미지를 로딩
 */
export const LazyImage = ({
  src,
  alt,
  wrapperClassName,
  className,
  // fill = false,
}: LazyImageProps) => {
  // 이미지가 뷰포트에 들어왔는지 감지
  const { ref, inView } = useInView({
    triggerOnce: true, // 한번만 로딩
    threshold: 0.1, // 10%가 보이면 로딩 시작
  });
  // 이미지 로딩 상태
  const [loaded, setLoaded] = useState<boolean>(false);

  return (
    <div ref={ref} className={cn('relative', wrapperClassName)}>
      {inView ? (
        <>
          {!loaded && (
            <div className="absolute inset-0 grid place-items-center">
              <SkeletonUi />
            </div>
          )}
          <img
            src={src}
            alt={alt}
            loading="lazy"
            decoding="async"
            onLoad={() => setLoaded(true)}
            className={cn(
              'block h-auto w-full transition-opacity duration-300',
              loaded ? 'opacity-100' : 'opacity-0',
              className
            )} // 이미지가 로드될 때 투명도(opacity)가 0에서 100으로 300ms 동안 자연스럽게 전환
            onError={(e) => {
              e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
            }}
          />
        </>
      ) : (
        <div className="grid place-items-center">
          <SkeletonUi />
        </div>
      )}
    </div>
  );
};
