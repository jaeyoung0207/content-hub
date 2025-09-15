import { useEffect, useState } from 'react';
import { WishlistUiPropsType } from '../WishlistUi';
import { Wishlist } from '@/api/Wishlist';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { wishlistQueryKeys } from '@/components/features/wishlist/queryKeys/wishlistQueryKeys';
import { toast } from 'react-toastify';
import { throttle } from 'lodash';
import { useConfirmDialogStore } from '@/components/common/store/globalStateStore';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

/**
 * 위시리스트 훅 반환 타입
 */
type UseWishlistReturnType = {
  addToWishlist: boolean;
  handleOnClickHeart: () => void;
  isExecuting: boolean;
};

/**
 * 위시리스트 훅
 */
export const useWishlistUi = ({
  isWishlisted,
  userId,
  originalMediaType,
  apiId,
  title,
  thumbnailImageUrl,
  genreIds,
  mediaType,
}: WishlistUiPropsType): UseWishlistReturnType => {
  // ================================================================================================== react hook

  // queryClient 훅
  const queryClient = useQueryClient();

  // navigate 훅
  const navigate = useNavigate();

  // transition 훅
  const { t } = useTranslation();

  // Wishlist API 인스턴스
  const wishlistApi = new Wishlist();

  // 위시리스트 여부 상태
  const [addToWishlist, setAddToWishlist] = useState<boolean>(isWishlisted);

  // 실행 중 상태
  const [isExecuting, setIsExecuting] = useState(false);

  // ================================================================================================== zustand

  // confirm dialog 상태 훅
  const { setIsConfirmDialogOpen, setOnOk, setOnCancel, setConfirmMsg } =
    useConfirmDialogStore();

  // ================================================================================================== mutation

  /**
   * 위시리스트 추가 뮤테이션
   */
  const addWishlistMutation = useMutation({
    mutationKey: wishlistQueryKeys.wishlist.add(userId),
    mutationFn: async () =>
      (
        await wishlistApi.saveWishlist({
          userId: userId,
          originalMediaType: originalMediaType,
          apiId: String(apiId),
          title: title,
          thumbnailImageUrl: thumbnailImageUrl,
          genreIds: genreIds ?? [],
          mediaType: mediaType,
        })
      ).data,
    onSuccess: (res) => {
      setAddToWishlist(true);
      if (res) {
        toast.success('위시리스트에 추가되었습니다 : \r\n' + title, {
          autoClose: 1000,
          style: { whiteSpace: 'pre-line' },
        });
      } else {
        toast.warning('이미 위시리스트에 존재합니다 : \r\n' + title, {
          autoClose: 1000,
          style: { whiteSpace: 'pre-line' },
        });
      }
    },
    onError: () => {
      console.error('위시리스트 추가 실패 : ' + title);
      toast.error('"' + title + '"의 위시리스트 추가에 실패했습니다.', {
        toastId: 'wishlist_add_error_' + title,
      });
    },
    onSettled: () => {
      setIsExecuting(false);
      queryClient.invalidateQueries({
        queryKey: wishlistQueryKeys.wishlist.all,
      });
    },
  });

  /**
   * 위시리스트 삭제 뮤테이션
   */
  const deleteWishlistMutation = useMutation({
    mutationKey: wishlistQueryKeys.wishlist.delete(userId),
    mutationFn: async () =>
      (
        await wishlistApi.deleteWishlist({
          userId: userId,
          originalMediaType: originalMediaType,
          apiId: String(apiId),
        })
      ).data,
    onSuccess: (res) => {
      setAddToWishlist(false);
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
    onError: () => {
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
   * 로그인 확인 다이얼로그에서 OK 버튼 클릭 시
   */
  const handleConfirmOk = () => {
    setIsConfirmDialogOpen(false);
    navigate('/login');
  };

  /**
   * 로그인 확인 다이얼로그에서 Cancel 버튼 클릭 시
   */
  const handleConfirmCancel = () => {
    setIsConfirmDialogOpen(false);
  };

  /**
   * 하트 아이콘 클릭 핸들러
   * 스로틀을 적용하여 중복 클릭 방지
   */
  const handleOnClickHeart = throttle(() => {
    if (!userId) {
      setIsConfirmDialogOpen(true);
      setOnOk(handleConfirmOk);
      setOnCancel(handleConfirmCancel);
      setConfirmMsg(t('info.loginConfirmMsg2'));
      return;
    }
    if (isExecuting) {
      return;
    }
    setIsExecuting(true);
    if (addToWishlist) {
      deleteWishlistMutation.mutate();
    } else {
      addWishlistMutation.mutate();
    }
  }, 500);

  // ================================================================================================== useEffect

  /**
   * isWishlisted prop이 변경될 때 addToWishlist 상태를 동기화
   */
  useEffect(() => {
    if (userId) {
      setAddToWishlist(isWishlisted);
    }
  }, [isWishlisted]);

  // ================================================================================================== return

  return {
    addToWishlist: addToWishlist,
    handleOnClickHeart: handleOnClickHeart,
    isExecuting: isExecuting,
  };
};
