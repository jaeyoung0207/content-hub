import { LoginApi } from '@/api/LoginApi';
import {
  ESC_KEY,
  LOGIN_PROVIDER,
  REDIRECT_URL,
} from '@/components/common/constants/constants';
import { useUserStore } from '@/components/common/store/globalStateStore';
import { clearUserData } from '@/components/common/utils/clearUtil';
import { useQueryClient } from '@tanstack/react-query';
import {
  Dispatch,
  RefObject,
  SetStateAction,
  useCallback,
  useEffect,
  useRef,
  useState,
} from 'react';
import { useNavigate } from 'react-router-dom';
import { headerQueryKeys } from '../queryKeys/headerQueryKeys';
import { AppApi } from '@/api/AppApi';
import { settings } from '@/components/common/config/settings';
import { LoginUserInfoDto } from '@/api/data-contracts';
import { setLoginInfo } from '@/components/common/utils/loginUtil';

/**
 * 헤더 훅 반환 타입
 */
export type UseHeaderLoginReturnType = {
  handleLoginOnClick: () => void; // 로그인 버튼 클릭 처리 함수
  handleLogoutOnClick: () => void; // 로그아웃 버튼 클릭 처리 함수
  user: LoginUserInfoDto | null; // 유저 정보
  userOptionIsOpen: boolean; // 유저 옵션 열림 상태
  handleUserOptionToggle: () => void; // 유저 옵션 토글 함수
  userOptionRef: RefObject<HTMLDivElement | null>; // 유저 옵션 참조
  setUserOptionIsOpen: Dispatch<SetStateAction<boolean>>; // 유저 옵션 열림 상태 설정 함수
};

export const useHeaderLogin = (): UseHeaderLoginReturnType => {
  // ================================================================================================== react hook

  // navigate 훅
  const navigate = useNavigate();

  // 유저 옵션 열림 상태
  const [userOptionIsOpen, setUserOptionIsOpen] = useState<boolean>(false);

  // 유저 옵션 참조
  const userOptionRef = useRef<HTMLDivElement>(null);
  // 중복 실행 방지 플래그
  const isExecutingRef = useRef<boolean>(false);

  // ================================================================================================== zustand

  // 유저 정보 전역 상태 저장용 훅
  const { user } = useUserStore();

  // ================================================================================================== react query

  // react query 클라이언트 훅
  const queryClient = useQueryClient();

  // 공통 API 인스턴스 생성
  const appApi = new AppApi();
  // 로그인 API 인스턴스 생성
  const loginApi = new LoginApi();

  // ================================================================================================== function

  /**
   * 로그인 클릭시 처리
   */
  const handleLoginOnClick = useCallback(() => {
    // URL 생성
    const searchUrl = location.pathname + location.search;
    // URL 저장
    sessionStorage.setItem(REDIRECT_URL, searchUrl);
    // 유저 옵션 초기화
    setUserOptionIsOpen(false);
    // 로그인 페이지로 이동
    navigate('/login');
  }, [navigate, setUserOptionIsOpen]);

  /**
   * 로그아웃 클릭시 처리
   */
  const handleLogoutOnClick = useCallback(() => {
    // URL 생성
    const searchUrl = location.pathname + location.search;
    // URL 저장
    sessionStorage.setItem(REDIRECT_URL, searchUrl);
    // 로그아웃 페이지로 이동
    navigate('/logout');
  }, [navigate]);

  /**
   * 유저 옵션 토글 함수
   */
  const handleUserOptionToggle = useCallback(() => {
    setUserOptionIsOpen((prev) => !prev);
  }, []);

  // ================================================================================================== useEffect

  /**
   * 화면 첫 로드 시 처리
   */
  /* eslint-disable react-hooks/exhaustive-deps */
  // 최초 한번만 실행돼야 하므로 의존성 배열 미지정
  useEffect(() => {
    // 맨 처음 접속시에는 유저정보 초기화
    clearUserData();
    // csrf token 초기화 API 호출
    queryClient.fetchQuery({
      queryKey: headerQueryKeys.getCsrfToken(),
      queryFn: async () => {
        return await appApi.getCsrfToken();
      },
    });

    // 로그인 쿠키 정보가 존재하는지 확인 후 유저정보 갱신 처리
    (async () => {
      // 중복 실행 방지
      if (isExecutingRef.current) {
        return;
      }
      // 중복 실행 방지 플래그 설정
      isExecutingRef.current = true;

      try {
        // 로그인 쿠키 정보 조회
        const cookiesResponse = await queryClient.fetchQuery({
          queryKey: headerQueryKeys.getLoginCookies(),
          queryFn: async () => {
            return await appApi.getLoginCookies();
          },
        });
        // 로그인 쿠키가 없는 경우 처리 종료
        if (
          !cookiesResponse.data.deviceId ||
          !cookiesResponse.data.provider ||
          !cookiesResponse.data.hasRefreshToken
        ) {
          return;
        }

        // 제공자 쿠키 정보
        const providerCookie = cookiesResponse.data.provider;
        // 쿠키의 provider가 NAVER인 경우
        if (providerCookie === LOGIN_PROVIDER.NAVER) {
          try {
            const updateResponse = await queryClient.fetchQuery({
              queryKey: headerQueryKeys.login(LOGIN_PROVIDER.NAVER),
              queryFn: async () => {
                // 네이버 로그인 정보 업데이트 API 호출
                return (await loginApi.updateNaverLoginInfo()).data;
              },
              retry: 0, // 재시도 없음 명시
            });
            setLoginInfo(updateResponse, LOGIN_PROVIDER.NAVER);
            return updateResponse;
          } catch (error) {
            // 유저 정보 초기화
            clearUserData();
            console.error('네이버 로그인 정보 갱신 실패', error);
          }
        }
        // 쿠키의 provider가 KAKAO인 경우
        else if (providerCookie === LOGIN_PROVIDER.KAKAO) {
          try {
            const updateResponse = await queryClient.fetchQuery({
              queryKey: headerQueryKeys.login(LOGIN_PROVIDER.KAKAO),
              queryFn: async () => {
                // 카카오 로그인 정보 업데이트 API 호출
                return (
                  await loginApi.updateKakaoLoginInfo({
                    client_id: settings.kakaoClientId,
                  })
                ).data;
              },
              retry: 0, // 재시도 없음 명시
            });
            setLoginInfo(updateResponse, LOGIN_PROVIDER.KAKAO);
            return updateResponse;
          } catch (error) {
            // 유저 정보 초기화
            clearUserData();
            console.error('카카오 로그인 정보 갱신 실패', error);
          }
        }
      } finally {
        // 중복 실행 방지 플래그 해제
        isExecutingRef.current = false;
      }
    })();
  }, []);

  /**
   * 마우스 클릭/키보드 키다운 이벤트
   */
  useEffect(() => {
    // 유저 옵션 바깥 영역 클릭 이벤트
    const handleOnClickOutside = (e: MouseEvent) => {
      // 유저 옵션 바깥영역 클릭시
      if (
        userOptionRef.current &&
        !userOptionRef.current.contains(e.target as Node)
      ) {
        setUserOptionIsOpen(false);
      }
    };
    // 필터 및 자동완성박스 esc 키다운 이벤트
    const handleOnKeyDown = (e: globalThis.KeyboardEvent) => {
      if (e.key === ESC_KEY) {
        // 유저 옵션 닫기
        setUserOptionIsOpen(false);
      }
    };

    // 각 이벤트 리스너 추가
    document.addEventListener('mousedown', handleOnClickOutside);
    document.addEventListener('keydown', handleOnKeyDown);

    return () => {
      // 각 이벤트 리스너 제거
      document.removeEventListener('mousedown', handleOnClickOutside);
      document.removeEventListener('keydown', handleOnKeyDown);
    };
  }, []);

  // ================================================================================================== return

  return {
    handleLoginOnClick: handleLoginOnClick,
    handleLogoutOnClick: handleLogoutOnClick,
    user: user,
    userOptionIsOpen: userOptionIsOpen,
    handleUserOptionToggle: handleUserOptionToggle,
    userOptionRef: userOptionRef,
    setUserOptionIsOpen: setUserOptionIsOpen,
  };
};
