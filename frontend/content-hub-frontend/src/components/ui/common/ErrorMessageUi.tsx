import { useEffect } from 'react';
import { toast } from 'react-toastify';

/**
 * 에러 메시지를 표시하는 UI 컴포넌트 props 타입
 */
type ErrorMessageUiPropsType = {
  errorMsg?: string; // 표시할 에러 메시지
  toastId?: string; // toast 메시지 ID
  isOnlyToast?: boolean; // toast 메시지로 표시할지 여부
};

/**
 * 에러 메시지를 표시하는 UI 컴포넌트
 * @param errorMsg 표시할 에러 메시지
 */
export const ErrorMessageUi = ({
  errorMsg,
  toastId,
  isOnlyToast,
}: ErrorMessageUiPropsType) => {
  /**
   * 에러 메시지가 변경될 때마다 toast 메시지를 표시하거나 제거
   * 각 렌더링마다 toast 메세지를 표시하지 않고, 의존성 배열의 변경에 따라서만 토스트를 표시하도록 useEffect 훅을 사용
   */
  useEffect(() => {
    const errorToastId = toastId ? toastId : errorMsg;
    // toast 메시지 표시
    if (errorMsg && isOnlyToast) {
      toast.error(errorMsg, { toastId: errorToastId, autoClose: false });
    }
    // toast 메시지 제거
    if (!errorMsg && isOnlyToast) {
      toast.dismiss(errorToastId);
    }
    // 컴포넌트 언마운트 시 toast 메시지 제거
    return () => toast.dismiss(errorToastId);
  }, [errorMsg, toastId, isOnlyToast]);

  return (
    <>
      {/* 에러 메시지를 일반적으로 표시 */}
      {errorMsg && !isOnlyToast && (
        <div className="text-lg text-red-500">{errorMsg}</div>
      )}
    </>
  );
};
