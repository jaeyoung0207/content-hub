import { NavigateFunction } from 'react-router-dom';
import { LOGIN_PROVIDER, REDIRECT_URL } from '../constants/constants';
import i18n from '@/i18n';
import {
  useConfirmDialogStore,
  useProviderStore,
  useUserStore,
} from '../store/globalStateStore';
import { LoginUserResponseDto } from '@/api/data-contracts';
import { clearUserData } from './clearUtil';
import { toast } from 'react-toastify';

// Sentry 동적 import
const Sentry = import('@sentry/react');

/**
 * 로그인 후 리다이렉트 처리
 */
export const afterLoginRedirect = (navigate: NavigateFunction) => {
  // 리다이렉트 URL이 있다면 해당 URL로 이동
  const redirectUrl = sessionStorage.getItem(REDIRECT_URL);
  if (redirectUrl?.startsWith('/')) {
    // 대상 URL로 이동
    navigate(redirectUrl, { replace: true });
    // 리다이렉트 URL 삭제
    sessionStorage.removeItem(REDIRECT_URL);
  } else {
    // 리다이렉트 URL이 없다면 홈으로 이동
    navigate('/', { replace: true });
  }
};

/**
 * 로그인 확인 다이얼로그 처리
 */
export const loginConfirmDialog = (
  message: string,
  navigate: NavigateFunction
) => {
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
  useConfirmDialogStore
    .getState()
    .setOnCancel(() =>
      useConfirmDialogStore.getState().setIsConfirmDialogOpen(false)
    );
  // 타이틀 설정
  useConfirmDialogStore
    .getState()
    .setTitle(i18n.t('info.requiredLoginConfirmTitle'));
  // 메시지 설정
  useConfirmDialogStore.getState().setConfirmMsg(i18n.t(message));
};

/**
 * 로그인 정보 설정
 * @param loginInfo 로그인 정보
 */
export const setLoginInfo = async (
  loginInfo: LoginUserResponseDto,
  provider: string
) => {
  // 로그인 정보가 없으면 유저정보 클리어 후 종료
  if (!loginInfo.userInfo || !loginInfo.accessToken || !loginInfo.jwt || !loginInfo.expireDate) {
    clearUserData();
    console.error('로그인 정보가 불완전합니다: ', loginInfo);
    toast.error('로그인 정보가 불완전합니다. 다시 로그인 해주세요.', {
      toastId: 'incompleteLoginInfo',
    });
    return;
  }
  // 유저정보 전역 상태 저장
  useUserStore.getState().setUser(loginInfo.userInfo);
  // 액세스 토큰 전역 상태 저장
  useUserStore.getState().setAccessToken(loginInfo.accessToken);
  // JWT 전역 상태 저장
  useUserStore.getState().setJwt(loginInfo.jwt);
  // 만료시각 전역 상태 저장
  useUserStore.getState().setExpireDate(loginInfo.expireDate);
  // provider 전역 상태 저장
  useProviderStore.getState().setProvider(provider as LOGIN_PROVIDER);
  // Sentry에 유저 정보 설정
  (await Sentry).setUser({
    id: loginInfo.userInfo.userId,
    username: loginInfo.userInfo.nickname,
  });
};
