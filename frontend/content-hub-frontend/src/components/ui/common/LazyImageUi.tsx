import { useInView } from 'react-intersection-observer';
import { LoadingUi } from '../LoadingUi';
import { COMMON_IMAGES } from '@/components/common/constants/constants';

/**
 * LazyImageProps 타입
 */
type LazyImageProps = {
  src: string;
  alt?: string;
  className?: string;
};

/**
 * LazyImage 컴포넌트
 * 뷰포트에 들어왔을 때 이미지를 로딩
 */
export const LazyImage = ({ src, alt, className }: LazyImageProps) => {
  // 이미지가 뷰포트에 들어왔는지 감지
  const { ref, inView } = useInView({
    triggerOnce: true, // 한번만 로딩
    threshold: 0.1, // 10%가 보이면 로딩 시작
  });
  return (
    <div ref={ref}>
      {inView ? (
        <img
          src={src}
          alt={alt}
          className={className}
          loading="lazy"
          onError={(e) => {
            e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
          }}
        />
      ) : (
        <LoadingUi />
      )}
    </div>
  );
};
