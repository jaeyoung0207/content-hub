import { useConfirmDialogStore } from '@/components/common/store/globalStateStore';
import { wishlistUrlQuery } from '@/components/common/utils/urlUtil';
import { useCallback } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';

/**
 * 헤더 컴포넌트의 위시리스트 아이콘 클릭 처리 훅 반환 타입
 */
export type UseHeaderWishlistReturnType = {
  handleWishlistOnClick: () => void; // 위시리스트 아이콘 클릭 처리 함수
};

/**
 * 헤더 컴포넌트의 위시리스트 아이콘 클릭 처리 훅
 */
export const useHeaderWishlist = (
  userId?: number
): UseHeaderWishlistReturnType => {
  // ================================================================================================== react hook

  // navigate 훅
  const navigate = useNavigate();

  // i18n 훅
  const { t } = useTranslation();

  // ================================================================================================== zustand

  // confirm dialog 상태 훅
  const { setIsConfirmDialogOpen, setOnOk, setOnCancel, setConfirmMsg } =
    useConfirmDialogStore();

  // ================================================================================================== function

  /**
   * 로그인 확인 다이얼로그에서 OK 버튼 클릭 시
   */
  const handleConfirmOk = useCallback(() => {
    setIsConfirmDialogOpen(false);
    navigate('/login');
  }, [navigate, setIsConfirmDialogOpen]);

  /**
   * 로그인 확인 다이얼로그에서 Cancel 버튼 클릭 시
   */
  const handleConfirmCancel = useCallback(() => {
    setIsConfirmDialogOpen(false);
  }, [setIsConfirmDialogOpen]);

  /**
   * 위시리스트 아이콘 클릭시 처리
   */
  const handleWishlistOnClick = useCallback(() => {
    if (!userId) {
      setIsConfirmDialogOpen(true);
      setOnOk(handleConfirmOk);
      setOnCancel(handleConfirmCancel);
      setConfirmMsg(t('info.loginConfirmMsg2'));
      return;
    } else {
      // 위시리스트 페이지로 이동
      navigate(wishlistUrlQuery({ userId: userId }));
    }
  }, [
    navigate,
    userId,
    setIsConfirmDialogOpen,
    setOnOk,
    setOnCancel,
    setConfirmMsg,
    t,
    handleConfirmOk,
    handleConfirmCancel,
  ]);

  // ================================================================================================== return

  return {
    handleWishlistOnClick: handleWishlistOnClick,
  };
};
