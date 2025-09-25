import naverLoginBtn from '@assets/buttons/btnG_naver_login.png';
import kakaoLoginBtn from '@assets/buttons/kakao_login_large_narrow.png';
import { settings } from '@/components/common/config/settings';
import { useEffect } from 'react';
import { useUserStore } from '@/components/common/store/globalStateStore';
import { useNavigate } from 'react-router-dom';

/**
 * 로그인 컴포넌트
 * 각 로그인 제공자를 통한 로그인을 위한 버튼을 렌더링
 * 사용자가 버튼을 클릭하면 해당 로그인 서비스의 OAuth 인증 페이지로 리다이렉트
 */
export const Login = () => {
  const STATE = Math.random().toString(36).substring(2, 11);
  const NONCE = Math.random().toString(36).substring(2, 11);
  const NAVER_AUTH_URL = `https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=${settings.naverClientId}&state=${STATE}&redirect_uri=${settings.naverRedirectUri}`;
  const KAKAO_AUTH_URL = `https://kauth.kakao.com/oauth/authorize?client_id=${settings.kakaoClientId}&redirect_uri=${settings.kakaoRedirectUri}&response_type=code&state=${STATE}&nonce=${NONCE}`;

  // 네이버의 OAuth 인증 페이지로 이동하는 함수
  const naverLogin = () => {
    window.location.href = NAVER_AUTH_URL;
  };
  // 카카오의 OAuth 인증 페이지로 이동하는 함수
  const kakaoLogin = () => {
    window.location.href = KAKAO_AUTH_URL;
  };

  // navigate 훅
  const navigate = useNavigate();

  // 유저 정보 전역 상태 저장 훅
  const { user } = useUserStore();

  /**
   * 유저 정보 존재 시 리다이렉트 처리
   */
  useEffect(() => {
    if (user) {
      // 사용자가 로그인한 상태일 때 처리
      navigate('/');
    }
  }, [user, navigate]);

  return (
    <>
      <div className="mt-60">
        <div className="flex justify-center">
          {/* 네이버 로그인 버튼 */}
          <img
            className="w-[320px] h-[80px] cursor-pointer"
            src={naverLoginBtn}
            onClick={naverLogin}
            alt="네이버 로그인"
          />
        </div>
        <div className="mt-10 flex justify-center">
          {/* 카카오 로그인 버튼 */}
          <img
            className="w-[320px] h-[80px] cursor-pointer"
            src={kakaoLoginBtn}
            onClick={kakaoLogin}
            alt="카카오 로그인"
          />
        </div>
      </div>
    </>
  );
};

export default Login;
