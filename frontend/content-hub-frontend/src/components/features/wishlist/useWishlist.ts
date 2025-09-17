import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Wishlist } from '@/api/Wishlist';
import {
  WishlistListResponseDto,
  WishlistRequestDto,
} from '@/api/data-contracts';
import { wishlistQueryKeys } from './queryKeys/wishlistQueryKeys';
import { toast } from 'react-toastify';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUserStore } from '@/components/common/store/globalStateStore';

/**
 * useWishlist 훅 반환 타입
 */
type UseWishlistReturnType = {
  data: WishlistListResponseDto | undefined;
  isLoading: boolean;
  handleWishlistDeleteOnClick: (
    apiId: number,
    originalMediaType: string,
    title: string
  ) => void;
  isExecuting: boolean;
};

/**
 * 위시리스트 조회 훅
 * @param userId 유저 id
 */
export const useWishlist = (userId: number): UseWishlistReturnType => {
  // ================================================================================================== react hook

  // navigate 훅
  const navigate = useNavigate();

  // 실행 중 상태
  const [isExecuting, setIsExecuting] = useState(false);

  // ================================================================================================== zustand

  // user 전역 상태 훅
  const { user } = useUserStore();

  // ================================================================================================== react query

  // queryClient 훅
  const queryClient = useQueryClient();

  // 위시리스트 API 인스턴스
  const wishlistApi = new Wishlist();

  // 위시리스트 조회
  const { data, isLoading } = useQuery<WishlistListResponseDto>({
    queryKey: wishlistQueryKeys.wishlist.list(userId),
    queryFn: async () => {
      return (await wishlistApi.getWishlist({ userId })).data;
    },
    enabled: !!userId,
  });

  /**
   * 위시리스트 삭제 뮤테이션
   */
  const deleteWishlistMutation = useMutation({
    mutationKey: wishlistQueryKeys.wishlist.delete(userId),
    mutationFn: async ({
      apiId,
      originalMediaType,
      userId,
    }: WishlistRequestDto & { title: string }) =>
      (
        await wishlistApi.deleteWishlist({
          userId: userId,
          originalMediaType: originalMediaType,
          apiId: apiId,
        })
      ).data,
    onSuccess: (res, { title }) => {
      if (res) {
        toast.success('위시리스트에서 제거되었습니다 : \r\n' + title, {
          autoClose: 1000,
          style: { whiteSpace: 'pre-line' },
        });
      } else {
        toast.warning('위시리스트에 존재하지 않습니다 : \r\n' + title, {
          autoClose: 1000,
          style: { whiteSpace: 'pre-line' },
        });
      }
    },
    onError: (_err, { title }) => {
      console.error('위시리스트 제거 실패 : ' + title);
      toast.error('"' + title + '"의 위시리스트 제거에 실패했습니다.', {
        toastId: 'wishlist_remove_error_' + title,
      });
    },
    onSettled: () => {
      setIsExecuting(false);
      queryClient.invalidateQueries({
        queryKey: wishlistQueryKeys.wishlist.all,
      });
    },
  });

  // ================================================================================================== function

  /**
   * 위시리스트 삭제 버튼 클릭시 처리
   * @param apiId Api ID
   * @param originalMediaType 원본 미디어 타입
   * @param title 제목
   */
  const handleWishlistDeleteOnClick = (
    apiId: number,
    originalMediaType: string,
    title: string
  ) => {
    setIsExecuting(true);
    deleteWishlistMutation.mutate({
      apiId: String(apiId),
      originalMediaType: originalMediaType,
      userId: userId,
      title: title,
    });
  };

  // ================================================================================================== useEffect

  /**
   * user정보가 없으면 홈으로 이동
   */
  useEffect(() => {
    if (!user) {
      navigate('/');
      return;
    }
  }, [user, navigate]);

  // ================================================================================================== return

  return {
    data: data,
    isLoading: isLoading,
    handleWishlistDeleteOnClick: handleWishlistDeleteOnClick,
    isExecuting: isExecuting,
  };
};
