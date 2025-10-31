import naverLoginBtn from '@assets/buttons/btnG_naver_login.png';
import kakaoLoginBtn from '@assets/buttons/kakao_login_large_narrow.png';
import { settings } from '@/components/common/config/settings';
import { useEffect, useMemo } from 'react';
import { useUserStore } from '@/components/common/store/globalStateStore';
import { useNavigate } from 'react-router-dom';
import { t } from 'i18next';

/**
 * 로그인 컴포넌트
 * 각 로그인 제공자를 통한 로그인을 위한 버튼을 렌더링
 * 사용자가 버튼을 클릭하면 해당 로그인 서비스의 OAuth 인증 페이지로 리다이렉트
 */
export const Login = () => {
  const STATE = useMemo(() => Math.random().toString(36).substring(2, 11), []);
  const NONCE = useMemo(() => Math.random().toString(36).substring(2, 11), []);

  // NAVER URL
  const NAVER_AUTH_URL = useMemo(() => {
    const url = new URL('https://nid.naver.com/oauth2.0/authorize');
    url.searchParams.set('response_type', 'code');
    url.searchParams.set('client_id', settings.naverClientId);
    url.searchParams.set('state', STATE);
    url.searchParams.set('redirect_uri', settings.naverRedirectUri);
    return url.toString();
  }, [STATE]);

  // KAKAO URL
  const KAKAO_AUTH_URL = useMemo(() => {
    const url = new URL('https://kauth.kakao.com/oauth/authorize');
    url.searchParams.set('client_id', settings.kakaoClientId);
    url.searchParams.set('redirect_uri', settings.kakaoRedirectUri);
    url.searchParams.set('response_type', 'code');
    url.searchParams.set('state', STATE);
    url.searchParams.set('nonce', NONCE);
    return url.toString();
  }, [STATE, NONCE]);

  /**
   * 로그인 페이지로 리다이렉트 처리
   * @param url 리다이렉트할 URL
   */
  const redirectLoginPage = (url: string) => {
    window.location.replace(url);
  };

  // navigate 훅
  const navigate = useNavigate();

  // 유저 정보 전역 상태 저장 훅
  const { user } = useUserStore();

  // 버튼 클래스명
  const buttonClassName =
    'group relative w-full max-w-[240px] overflow-hidden border-black/10 bg-white p-0 transition-colors hover:bg-black/5 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-2 cursor-pointer';
  // 이미지 래퍼 클래스명
  const imgWraperClassName = 'flex h-14 items-center justify-center sm:h-16';
  // 이미지 클래스명
  const imgClassName = 'select-none object-contain rounded-lg';

  /**
   * 유저 정보 존재 시 리다이렉트 처리
   */
  useEffect(() => {
    if (user) {
      // 사용자가 로그인한 상태일 때 처리
      navigate('/', { replace: true });
    }
  }, [user, navigate]);

  return (
    <>
      <div className="min-h-[60vh] px-4 py-26 sm:px-6 lg:px-8">
        <h1 className="mb-2 w-full text-left text-2xl font-bold sm:text-3xl">
          {t('info.login')}
        </h1>
        <div className="mx-auto flex max-w-md flex-col items-center justify-center gap-8">
          {/* 네이버 로그인 버튼 */}
          <button
            type="button"
            onClick={() => redirectLoginPage(NAVER_AUTH_URL)}
            className={buttonClassName}
            aria-label="네이버로 로그인"
            title="네이버로 로그인"
          >
            <div className={imgWraperClassName}>
              <img
                src={naverLoginBtn}
                alt="네이버 로그인"
                className={imgClassName}
                loading="lazy"
                decoding="async"
                draggable={false}
              />
            </div>
          </button>

          {/* 카카오 로그인 버튼 */}
          <button
            type="button"
            onClick={() => redirectLoginPage(KAKAO_AUTH_URL)}
            className={buttonClassName}
            aria-label="카카오로 로그인"
            title="카카오로 로그인"
          >
            <div className={imgWraperClassName}>
              <img
                src={kakaoLoginBtn}
                alt="카카오 로그인"
                className={imgClassName}
                loading="lazy"
                decoding="async"
                draggable={false}
              />
            </div>
          </button>
        </div>
      </div>
    </>
  );
};

export default Login;
