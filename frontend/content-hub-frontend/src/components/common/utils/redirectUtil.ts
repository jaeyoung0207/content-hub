import { NavigateFunction } from "react-router-dom";
import { DETAIL_TAB_ID, REDIRECT_URL } from "../constants/constants";

/**
 * 로그인 후 리다이렉트 처리
 */
export const afterLoginRedirect = (navigate: NavigateFunction) => {
  // 리다이렉트 URL이 있다면 해당 URL로 이동
  const redirectUrl = sessionStorage.getItem(REDIRECT_URL);
  if (redirectUrl) {
    // 상세 화면으로 리다이렉트하는 경우, tabNo가 0인 경우 1로 변경
    if (redirectUrl.includes('tabNo=0')) {
      navigate(
        redirectUrl.replace(
          'tabNo=0',
          'tabNo='.concat(DETAIL_TAB_ID.review.toString())
        )
      );
    }
    // 그 이외의 경우
    else {
      navigate(redirectUrl);
    }
    // 리다이렉트 URL 삭제
    sessionStorage.removeItem(REDIRECT_URL);
  } else {
    // 리다이렉트 URL이 없다면 홈으로 이동
    navigate('/');
  }
};
