import { RiHeartLine, RiHeartFill } from 'react-icons/ri';
import { useWishlistUi } from './hooks/useWishlistUi';
import { cn } from '@/lib/cn';

/**
 * 위시리스트 UI 컴포넌트 Props 타입
 */
export type WishlistUiPropsType = {
  isWishlisted: boolean; // 위시리스트 등록 여부
  userId?: number; // 사용자 ID
  contentMediaType: string; // 콘텐츠 미디어 타입
  apiId: number; // 콘텐츠 API ID
  title: string; // 콘텐츠 제목
  thumbnailImageUrl?: string; // 썸네일 이미지 URL
  genreIds?: number[]; // 장르 ID 목록
  displayMediaType?: string; // 표시할 미디어 타입
  className?: string; // 추가적인 클래스 이름
};

/**
 * 위시리스트 UI 컴포넌트
 * @param WishlistUiPropsType
 */
export const WishlistUi = ({
  isWishlisted,
  userId,
  contentMediaType,
  apiId,
  title,
  thumbnailImageUrl,
  genreIds,
  displayMediaType,
  className,
}: WishlistUiPropsType) => {
  // 위시리스트 훅
  const { handleOnClickHeart, isExecuting } = useWishlistUi({
    isWishlisted,
    userId,
    contentMediaType,
    apiId,
    title,
    thumbnailImageUrl,
    genreIds,
    displayMediaType,
  });
  // 접근성 라벨
  const ariaLabel = isWishlisted ? '위시리스트에서 제거' : '위시리스트에 추가';

  return (
    <button
      type="button"
      aria-label={ariaLabel}
      aria-pressed={isWishlisted}
      aria-busy={isExecuting}
      disabled={isExecuting}
      className={cn(
        // 버튼 레이아웃
        'inline-flex size-8 items-center justify-center rounded-full',
        // 색/상태
        'text-red-500 disabled:opacity-50',
        // 포커스/인터랙션
        'focus-visible:ring-primary transition-transform focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:outline-none active:scale-95',
        className
      )}
      onClick={(e) => {
        e.stopPropagation(); // 클릭 이벤트 전파 방지
        handleOnClickHeart();
      }}
    >
      {isWishlisted ? (
        <RiHeartFill className="h-6 w-6" />
      ) : (
        <RiHeartLine className="h-6 w-6" />
      )}
    </button>
  );
};
