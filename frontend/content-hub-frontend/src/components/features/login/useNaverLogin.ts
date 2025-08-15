import { Login } from '@/api/Login';
import {
  useProviderStore,
  useUserStore,
} from '@/components/common/store/globalStateStore';
import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import { LOGIN_PROVIDER } from '@/components/common/constants/constants';
import { afterLoginRedirect } from '@/components/common/utils/commonUtil';

/**
 * Naver 로그인 훅
 * 네이버 로그인을 처리하며, 로그인 성공 시 유저 정보를 전역 상태에 저장하고, 세션 스토리지에 토큰을 저장
 * 로그인 실패 시 에러 메시지를 표시하고 로그인 페이지로 리다이렉트
 * 로그인 후 유저 정보를 가져오는 함수도 포함되어 있어, 액세스 토큰과 만료 시간을 받아 유저 정보를 갱신
 */
export const useNaverLogin = () => {
  // 네비게이션 훅
  const navigate = useNavigate();
  // URL 쿼리 파라미터 훅
  const [searchParams] = useSearchParams();
  // 유저 정보 전역 상태 저장 훅
  const { setUser } = useUserStore();
  // provider 전역 상태 저장 훅
  const { setProvider } = useProviderStore();
  // 로그인 API 인스턴스 생성
  const loginApi = new Login();

  /**
   * 네이버 로그인 인증 및 유저 정보 조회 API 요청
   * @param code 인증 코드
   * @param state 인증시의 상태 토큰
   * @returns 로그인 유저 정보
   */
  const getNaverLoginInfo = async (code: string, state: string) => {
    // 네이버 로그인 API 호출
    const response = await loginApi.getNaverLoginInfo({
      code: code!,
      state: state!,
    });
    const loginInfo = response.data;
    if (loginInfo && loginInfo.userInfo) {
      // 유저정보를 전역상태저장
      setUser(loginInfo.userInfo);
      // provider 전역상태저장
      setProvider(LOGIN_PROVIDER.NAVER);
      // 액세스 토큰을 sessionStorage에 저장
      sessionStorage.setItem('accessToken', loginInfo.accessToken!);
      // JWT를 sessionStorage에 저장
      sessionStorage.setItem('jwt', loginInfo.jwt!);
      // 만료시각을 sessionStorage에 저장
      sessionStorage.setItem('expireDate', loginInfo.expireDate!);
    }
    return loginInfo;
  };

  // /**
  //  * 네이버 로그인 유저 정보 요청
  //  * @param accessToken 접근 토큰
  //  * @returns 유저 정보
  //  */
  // const getNaverProfile = async (accessToken: string) => {
  //     const expireDate = localStorage.getItem("expireDate")!;
  //     const response = await loginApi.getNaverProfile({ accessToken: accessToken, expireDate: expireDate });
  //     // 유저정보를 전역상태저장
  //     if (response.data.response) {
  //         setUser(response.data.response);
  //         // 액세스 토큰을 localStorage에 저장
  //         localStorage.setItem("accessToken", response.data.jwt!);
  //     }
  //     return response.data;
  // }

  /**
   * URL에서 인증 코드를 가져와 네이버 로그인 API를 호출
   * 로그인 성공 후, 리다이렉트 URL이 있다면 해당 URL로 이동하고, 없으면 홈으로 이동
   * 이 훅은 네이버 로그인 버튼 클릭 시 호출되어야 함
   */
  /* eslint-disable react-hooks/exhaustive-deps */
  // 최초 한번만 실행돼야 하므로 의존성 배열 미지정
  useEffect(() => {
    // URL에서 인증 코드와 상태를 가져옴
    const code = searchParams.get('code')!;
    const state = searchParams.get('state')!;
    // 네이버 로그인 인증 및 유저 정보 조회 API 요청
    getNaverLoginInfo(code, state).catch((err) => {
      console.error('네이버 로그인 실패', err);
      toast.error('로그인에 실패했습니다. 다시 시도해주세요.', {
        toastId: 'naverLoginError',
      });
      navigate('/login');
    });
    // 리다이렉트 URL이 있다면 해당 URL로 이동
    afterLoginRedirect(navigate);
  }, []);
};
