import naverLoginBtn from '@assets/buttons/btnG_naver_login.png';
import kakaoLoginBtn from '@assets/buttons/kakao_login_large_narrow.png';
import { settings } from '@/components/common/config/settings';
import { useEffect, useMemo } from 'react';
import { useUserStore } from '@/components/common/store/globalStateStore';
import {
  NavigateFunction,
  useNavigate,
  useSearchParams,
} from 'react-router-dom';
import { LOGIN_PROVIDER } from '@/components/common/constants/constants';
import { toast } from 'react-toastify';
import { useTranslation } from 'react-i18next';
import { isMobile, isTablet } from 'react-device-detect';
import { TFunction } from 'i18next';

/**
 * 로그인 컴포넌트
 * 각 로그인 제공자를 통한 로그인을 위한 버튼을 렌더링
 * 사용자가 버튼을 클릭하면 해당 로그인 서비스의 OAuth 인증 페이지로 리다이렉트
 */
export const Login = () => {
  // navigate 훅
  const navigate = useNavigate();

  // 유저 정보 전역 상태 저장 훅
  const { user } = useUserStore();

  // 다국어 번역 훅
  const { t } = useTranslation();

  // URL 쿼리 파라미터 훅
  const [searchParams] = useSearchParams();

  // STATE, NONCE 값 생성
  const STATE = useMemo(() => {
    const array = new Uint32Array(1);
    globalThis.crypto.getRandomValues(array);
    return array[0].toString(36);
  }, []);
  const NONCE = useMemo(() => {
    const array = new Uint32Array(1);
    globalThis.crypto.getRandomValues(array);
    return array[0].toString(36);
  }, []);

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

  // 모바일 또는 태블릿 기기 여부
  const isMobileDevice = isMobile || isTablet;

  // URL 쿼리 파라미터에서 인증 코드 가져오기
  const codeParam = searchParams.get('code');

  /**
   * 로그인 페이지로 리다이렉트 처리
   * @param url 리다이렉트할 URL
   */
  const redirectLoginPage = (url: string, provider: string) => {
    // STATE 값 세션 스토리지에 저장
    sessionStorage.setItem(`${provider}_OAUTH_STATE`, STATE);
    // 모바일 또는 태블릿 환경일 경우
    if (isMobileDevice) {
      // 현재 창에서 로그인 페이지로 리다이렉트
      globalThis.location.replace(url);
    }
    // 데스크탑 환경일 경우
    else {
      // 팝업 창으로 로그인 페이지 열기
      const loginPopup = globalThis.open(url, '로그인', 'width=500,height=700');
      // 팝업이 차단되었을 경우 에러 메시지 출력
      if (!loginPopup) {
        toast.warn(t('warn.popupBlocked'), {
          toastId: 'popupBlocked',
        });
      }
    }
  };

  /**
   * 로그인 팝업창에서 메시지 수신 처리
   */
  useEffect(() => {
    // 모바일/태블릿 환경일 경우
    if (isMobileDevice && codeParam) {
      // 빈 이벤트 객체 생성
      const emptyEvent = {} as MessageEvent;
      // 로그인 OAuth 데이터 체크 후 리다이렉트 처리
      checkAndRedirectLoginPage(emptyEvent, t, navigate, searchParams);
      return;
    }
    // 모바일/태블릿 이외 환경일 경우
    const handleLoginMessage = (event: MessageEvent) => {
      // 메시지의 출처가 현재 도메인과 일치하는지 확인
      if (event.origin !== globalThis.location.origin) {
        return;
      }
      // 로그인 OAuth 데이터 체크 후 리다이렉트 처리
      checkAndRedirectLoginPage(event, t, navigate, searchParams);
    };
    globalThis.addEventListener('message', handleLoginMessage);
    return () => {
      globalThis.removeEventListener('message', handleLoginMessage);
    };
  }, [navigate, t, isMobileDevice, codeParam, searchParams]);

  /**
   * 유저 정보 존재 시 리다이렉트 처리
   */
  useEffect(() => {
    if (user) {
      // 사용자가 로그인한 상태일 때 처리
      navigate('/', { replace: true });
    }
  }, [user, navigate]);

  // 버튼 클래스명
  const buttonClassName =
    'group relative w-full max-w-[240px] overflow-hidden border-black/10 bg-white p-0 transition-colors hover:bg-black/5 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-2 cursor-pointer';
  // 이미지 래퍼 클래스명
  const imgWraperClassName = 'flex h-14 items-center justify-center sm:h-16';
  // 이미지 클래스명
  const imgClassName = 'select-none object-contain rounded-lg';

  return (
    <>
      {codeParam ? (
        <></>
      ) : (
        <div className="min-h-[60vh] px-4 pt-20 pb-10 sm:px-6 md:pt-24 lg:px-8">
          <h1 className="mb-10 w-full text-left text-2xl font-bold sm:text-3xl">
            {t('info.login')}
          </h1>
          <div className="mx-auto flex max-w-md flex-col items-center justify-center gap-8">
            {/* 네이버 로그인 버튼 */}
            <button
              type="button"
              onClick={() =>
                redirectLoginPage(NAVER_AUTH_URL, LOGIN_PROVIDER.NAVER)
              }
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
              onClick={() =>
                redirectLoginPage(KAKAO_AUTH_URL, LOGIN_PROVIDER.KAKAO)
              }
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
      )}
    </>
  );
};

export default Login;

/**
 * 로그인 OAuth 데이터 체크 후 리다이렉트 처리
 * @param event 메시지 이벤트
 * @param t 다국어 번역 함수
 * @param navigate 네비게이트 함수
 * @param searchParams URLSearchParams 객체
 */
const checkAndRedirectLoginPage = (
  event: MessageEvent,
  t: TFunction,
  navigate: NavigateFunction,
  searchParams: URLSearchParams
) => {
  // 인증 데이터 추출
  const eventData = event.data || {};
  const code = eventData.code ?? searchParams.get('code');
  const state = eventData.state ?? searchParams.get('state');
  const error = eventData.error ?? searchParams.get('error');
  const error_description =
    eventData.error_description ?? searchParams.get('error_description');
  const provider = eventData.provider ?? searchParams.get('provider');
  // 에러가 있을 경우 메세지 표시
  if (error) {
    // 사용자가 취소한 경우 에러 메시지 표시 안함
    if (error_description === 'Canceled By User') {
      return;
    }
    console.error('[LOGIN ERROR]: ', error + ' - ' + error_description);
    toast.error(t('error.loginError'), {
      toastId: 'loginPopupError',
    });
    return;
  }
  // 응답 데이터가 모두 존재할 때
  if (code && state) {
    // STATE 검증
    const expectedState = sessionStorage.getItem(`${provider}_OAUTH_STATE`);
    if (!expectedState || state !== expectedState) {
      console.error('[LOGIN ERROR]: ', t('error.loginStateError'));
      toast.error(t('error.loginError'), { toastId: 'loginPopupError' });
      return;
    }
    // 세션 스토리지에서 STATE 값 제거
    sessionStorage.removeItem(`${provider}_OAUTH_STATE`);
    // 로그인 처리 페이지 경로 생성
    const prefixLoginPage = provider.toLowerCase();
    // 로그인 처리 페이지로 리다이렉트
    navigate(`/${prefixLoginPage}Login`, {
      state: { code: code, state: state },
    });
  } else {
    console.error('[LOGIN ERROR]: ', t('error.loginNoOAuthDataError'));
    toast.error(t('error.loginError'), {
      toastId: 'loginPopupError',
    });
  }
};
