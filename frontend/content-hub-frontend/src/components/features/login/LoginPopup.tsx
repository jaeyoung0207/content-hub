import { LOGIN_PROVIDER } from '@/components/common/constants/constants';
import { useEffect } from 'react';
import { isMobile, isTablet } from 'react-device-detect';
import { useTranslation } from 'react-i18next';
import { useLocation, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';

/**
 * 로그인 팝업 컴포넌트
 * 팝업 창에서 인증 코드를 부모 창으로 전달하고 창을 닫음
 */
export const LoginPopup = () => {
  // 다국어 번역 훅
  const { t } = useTranslation();
  // 로케이션 훅
  const location = useLocation();
  // 네비게이션 훅
  const navigate = useNavigate();

  /**
   * 팝업 창에서 부모 창으로 인증 코드와 상태를 전달
   */
  useEffect(() => {
    // URL 쿼리 파라미터 훅
    const searchParams = new URLSearchParams(location.search);
    // URL에서 인증 코드와 상태를 가져옴
    const code = searchParams.get('code') ?? '';
    const state = searchParams.get('state') ?? '';
    const error = searchParams.get('error') ?? '';
    const error_description = searchParams.get('error_description') ?? '';
    // 로그인 제공자 정보 추출
    const provider = location.pathname.replace('/login/', '').toUpperCase();
    // 쿼리 파라미터에 제공자 정보 추가
    searchParams.set('provider', provider);
    // 유효한 로그인 제공자인지 확인
    const isProviderValid = Object.values(LOGIN_PROVIDER).includes(
      provider as LOGIN_PROVIDER
    );

    // 모바일 또는 태블릿 환경일 경우
    if (isMobile || isTablet || !globalThis.opener) {
      if (isProviderValid) {
        navigate(`/login?${searchParams.toString()}`, { replace: true });
      } else {
        toast.error(t('error.loginProviderError'));
        navigate('/login');
      }
      return;
    }

    // 부모 창이 존재할 경우(모바일/태블릿 이외 환경)
    if (globalThis.opener) {
      // 에러가 있을 경우 부모 창에 에러 메시지 전달
      if (error) {
        globalThis.opener.postMessage(
          { error, error_description, provider },
          globalThis.location.origin
        );
      }
      // 인증 데이터가 모두 존재할 경우
      else if (code && state && provider) {
        // 유효한 로그인 제공자인지 확인
        if (isProviderValid) {
          // 부모 창에 메시지로 코드와 상태 전달
          globalThis.opener.postMessage(
            { code, state, provider },
            globalThis.location.origin
          );
        } else {
          // 유효하지 않은 로그인 제공자일 경우 에러 메시지 전달
          globalThis.opener.postMessage(
            {
              error: 'Invalid Provider',
              error_description: t('error.loginProviderError'),
              provider,
            },
            globalThis.location.origin
          );
        }
      } else {
        // 인증 데이터가 부족할 경우 에러 메시지 전달
        globalThis.opener.postMessage(
          {
            error: 'Invalid Data',
            error_description: t('error.loginNoOAuthDataError'),
            provider,
          },
          globalThis.location.origin
        );
      }
      // 팝업 창 닫기
      globalThis.close();
    }
  }, [location, t, navigate]);

  return <div className="mt-20">로그인 처리중...</div>;
};

export default LoginPopup;
