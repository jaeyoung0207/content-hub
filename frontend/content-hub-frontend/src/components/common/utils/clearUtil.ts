import { useProviderStore, useUserStore } from '../store/globalStateStore';

/**
 * 데이터를 클리어하는 함수를 정의하는 유틸 파일
 */

/**
 * 유저정보, provider정보, 세션스토리지 클리어
 */
export const clearUserData = () => {
  // 유저정보
  const { clearUser } = useUserStore.getState();
  // provider정보
  const { clearProvider } = useProviderStore.getState();
  // 유저정보 클리어
  clearUser();
  // provider정보 클리어
  clearProvider();
  // sessionStorage클리어
  sessionStorage.removeItem('accessToken');
  sessionStorage.removeItem('jwt');
  sessionStorage.removeItem('expireDate');
};
