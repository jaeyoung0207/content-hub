import { RiHeartLine, RiHeartFill } from 'react-icons/ri';
import { useWishlist } from './hooks/useWishlist';
import { useState } from 'react';

/**
 * 위시리스트 UI 컴포넌트 Props 타입
 */
export type WishlistUiPropsType = {
  isWishlisted: boolean;
  userId: number;
  originalMediaType: string;
  apiId: number;
  title: string;
  thumbnailImageUrl?: string;
  genreIds?: number[];
  mediaType?: string;
};

/**
 * 위시리스트 UI 컴포넌트
 */
export const WishlistUi = ({
  isWishlisted,
  userId,
  originalMediaType,
  apiId,
  title,
  thumbnailImageUrl,
  genreIds,
  mediaType,
}: WishlistUiPropsType) => {
  // 위시리스트 훅
  const { addToWishlist, handleOnClickHeart, isExecuting } = useWishlist({
    isWishlisted,
    userId,
    originalMediaType,
    apiId,
    title,
    thumbnailImageUrl,
    genreIds,
    mediaType,
  });

  // 하트 아이콘 hover 상태
  const [hover, setHover] = useState<boolean>(false);
  // 버튼 스타일
  const buttonStyle =
    'flex justify-center items-center w-8 h-8 border-0 text-red-500 cursor-pointer';
  // 아이콘 색상
  const iconStyle = 'text-red-500';
  // 아이콘 크기
  const iconSize = 24;
  return (
    <div>
      {addToWishlist ? (
        <button
          onClick={(e) => {
            e.stopPropagation(); // 클릭 이벤트 전파 방지
            handleOnClickHeart();
          }}
          className={buttonStyle}
          disabled={isExecuting}
          onMouseEnter={() => setHover(addToWishlist ? true : false)}
          onMouseLeave={() => setHover(addToWishlist ? false : true)}
        >
          {hover ? (
            <RiHeartLine className={iconStyle} size={iconSize} />
          ) : (
            <RiHeartFill className={iconStyle} size={iconSize} />
          )}
        </button>
      ) : (
        <button
          onClick={(e) => {
            e.stopPropagation(); // 클릭 이벤트 전파 방지
            handleOnClickHeart();
          }}
          className={buttonStyle}
          disabled={isExecuting}
          onMouseEnter={() => setHover(addToWishlist ? false : true)}
          onMouseLeave={() => setHover(addToWishlist ? true : false)}
        >
          {hover ? (
            <RiHeartFill className={iconStyle} size={iconSize} />
          ) : (
            <RiHeartLine className={iconStyle} size={iconSize} />
          )}
        </button>
      )}
    </div>
  );
};
