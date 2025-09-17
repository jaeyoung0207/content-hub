import { NavigateFunction } from 'react-router-dom';
import { REDIRECT_URL } from '../constants/constants';
import i18n from '@/i18n';
import { useConfirmDialogStore } from '../store/globalStateStore';

/**
 * 로그인 후 리다이렉트 처리
 */
export const afterLoginRedirect = (navigate: NavigateFunction) => {
  // 리다이렉트 URL이 있다면 해당 URL로 이동
  const redirectUrl = sessionStorage.getItem(REDIRECT_URL);
  if (redirectUrl) {
    // 대상 URL로 이동
    navigate(redirectUrl);
    // 리다이렉트 URL 삭제
    sessionStorage.removeItem(REDIRECT_URL);
  } else {
    // 리다이렉트 URL이 없다면 홈으로 이동
    navigate('/');
  }
};

/**
 * 로그인 확인 다이얼로그 처리
 */
export const loginConfirmDialog = (message: string, navigate: NavigateFunction) => {
  // 로그인 확인 모달 열기
  useConfirmDialogStore.getState().setIsConfirmDialogOpen(true);
  // 로그인 확인 모달에서 OK 버튼 클릭 시
  useConfirmDialogStore.getState().setOnOk(() => {
    // 로그인 확인 모달 닫기
    useConfirmDialogStore.getState().setIsConfirmDialogOpen(false);
    // URL 생성
    const searchUrl = location.pathname + location.search;
    // URL 저장
    sessionStorage.setItem(REDIRECT_URL, searchUrl);
    // 로그인 페이지로 이동
    navigate('/login');
  });
  // 로그인 확인 모달에서 Cancel 버튼 클릭 시
  useConfirmDialogStore.getState().setOnCancel(() => useConfirmDialogStore.getState().setIsConfirmDialogOpen(false));
  // 메시지 설정
  useConfirmDialogStore.getState().setConfirmMsg(i18n.t(message));
};
