import { LoginApi } from '@/api/LoginApi';
import { settings } from '@/components/common/config/settings';
import { LOGIN_PROVIDER } from '@/components/common/constants/constants';
import { useUserStore } from '@/components/common/store/globalStateStore';
import {
  afterLoginRedirect,
  setLoginInfo,
} from '@/components/common/utils/loginUtil';
import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useLocation, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';

/**
 * Kakao 로그인 훅
 * 카카오 로그인을 처리하며, 로그인 성공 시 유저 정보를 전역 상태에 저장하고, 세션 스토리지에 토큰을 저장
 * 로그인 실패 시 에러 메시지를 표시하고 로그인 페이지로 리다이렉트
 * 로그인 후 유저 정보를 가져오는 함수도 포함되어 있어, 액세스 토큰과 만료 시간을 받아 유저 정보를 갱신
 */
export const useKakaoLogin = () => {
  // 네비게이션 훅
  const navigate = useNavigate();

  // 다국어 번역 훅
  const { t } = useTranslation();

  // 네비게이션 훅
  const location = useLocation();
  // state 값 가져오기
  const stateData = location.state as { code: string };
  const code = stateData.code;

  // 유저 정보 전역 상태 저장 훅
  const { user } = useUserStore();
  // 로그인 API 인스턴스 생성
  const loginApi = new LoginApi();

  /**
   * 카카오 로그인 인증 및 유저 정보 조회 API 요청
   * @param code 인증 코드
   * @returns 로그인 유저 정보
   */
  const getKakaoLoginInfo = async (code: string) => {
    // 카카오 로그인 API 호출
    const response = await loginApi.getKakaoLoginInfo({
      client_id: settings.kakaoClientId,
      redirect_uri: settings.kakaoRedirectUri,
      code: code,
    });
    const loginInfo = response.data;
    // 로그인 정보를 전역 상태에 저장
    await setLoginInfo(loginInfo, LOGIN_PROVIDER.KAKAO);
    return loginInfo;
  };

  /* eslint-disable react-hooks/exhaustive-deps */
  // 최초 한번만 실행돼야 하므로 의존성 배열 미지정
  useEffect(() => {
    // 유저 정보가 존재하면 홈으로 이동
    if (user) {
      navigate('/');
    }
    // 인증 코드가 없으면 로그인 페이지로 이동
    if (!code) {
      navigate('/login');
      return;
    }
    // 카카오 로그인 인증 및 유저 정보 조회 API 호출
    getKakaoLoginInfo(code)
      .then(() => {
        // 리다이렉트 URL이 있다면 해당 URL로 이동
        afterLoginRedirect(navigate);
      })
      .catch((err) => {
        console.error('카카오 로그인 실패', err);
        toast.error(t('error.loginError'), {
          toastId: 'kakaoLoginError',
        });
        navigate('/login');
      });
  }, []);
};
