import { NavigateFunction } from 'react-router-dom';
import { REDIRECT_URL } from '../constants/constants';

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
