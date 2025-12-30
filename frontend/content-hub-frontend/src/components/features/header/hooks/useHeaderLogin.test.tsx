import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { fireEvent, renderHook, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, beforeEach, expect, it, vi } from 'vitest';
import { useHeaderLogin } from './useHeaderLogin';
import { AppApi } from '@/api/AppApi';
import { AxiosHeaders, InternalAxiosRequestConfig } from 'axios';
import { LoginApi } from '@/api/LoginApi';
import { act } from 'react';
import { REDIRECT_URL } from '@/components/common/constants/constants';
import * as reactRouterDom from 'react-router-dom';

// 테스트용 QueryClient 생성 함수
const queryClientMock = () =>
  new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });

// useSearchParams 모킹 설정
const reactRouterDomMock = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return {
    ...actual, // 기존 모듈의 모든 내보내기 유지
    useNavigate: () => vi.fn(),
  };
});

// AppApi를 mock 처리
vi.mock('@/api/AppApi', () => {
  return {
    AppApi: class MockAppApi {
      getLoginCookies() {
        // 테스트에서 구현
      }
    },
  };
});

// LoginApi를 mock 처리
vi.mock('@/api/LoginApi', () => {
  return {
    LoginApi: class MockLoginApi {
      updateNaverLoginInfo() {
        // 테스트에서 구현
      }
      updateKakaoLoginInfo() {
        // 테스트에서 구현
      }
    },
  };
});

// loginUtil를 mock 처리
vi.mock('@/components/common/utils/loginUtil', async (importActual) => {
  const actual =
    await importActual<typeof import('@/components/common/utils/loginUtil')>();
  return {
    ...actual,
    setLoginInfo: vi.fn(),
  };
});

// 각 테스트 전에 실행되는 설정
beforeEach(() => {
  // 모킹 초기화
  queryClientMock();
  vi.clearAllMocks();
});

// 커스텀 훅을 렌더링하기 위한 래퍼 컴포넌트
const wrapper = ({ children }: { children: React.ReactNode }) => (
  <QueryClientProvider client={queryClientMock()}>
    <MemoryRouter>{children}</MemoryRouter>
  </QueryClientProvider>
);

// 로그인 제공자 상수
const PROVIDER = {
  NAVER: '네이버',
  KAKAO: '카카오',
};

describe('useHeaderLogin', () => {
  describe('useEffect_화면 첫 로드 시 처리', () => {
    // 각 제공자별 테스트 케이스 생성
    for (const providerKey in PROVIDER) {
      const providerName = PROVIDER[providerKey as keyof typeof PROVIDER];

      it(`${providerName} 로그인 업데이트 API 호출`, () => {
        // AppApi 메서드 모킹
        const appApiMock = vi
          .spyOn(AppApi.prototype, 'getLoginCookies')
          .mockResolvedValue({
            data: {
              deviceId: 'device-id',
              provider: providerKey,
              hasRefreshToken: true,
            },
            headers: new AxiosHeaders(),
            status: 200,
            statusText: 'OK',
            config: {} as InternalAxiosRequestConfig,
          });
        // LoginApi 메서드 모킹
        let loginApiMock;
        if (providerKey === 'NAVER') {
          loginApiMock = vi.spyOn(LoginApi.prototype, 'updateNaverLoginInfo');
        } else {
          loginApiMock = vi.spyOn(LoginApi.prototype, 'updateKakaoLoginInfo');
        }
        loginApiMock.mockResolvedValue({
          data: {
            jwt: 'header.payload.signature',
            accessToken: 'access-token',
          },
          headers: new AxiosHeaders(),
          status: 200,
          statusText: 'OK',
          config: {} as InternalAxiosRequestConfig,
        });

        // useHeaderLogin 훅 렌더링
        const { result } = renderHook(() => useHeaderLogin(), { wrapper });

        // 비동기 처리 완료 대기
        waitFor(() => {
          // 유저 정보가 정상적으로 설정되었는지 확인
          expect(result.current.user).toBeDefined();
          // AppApi 및 LoginApi의 메서드가 호출되었는지 확인
          expect(appApiMock).toHaveBeenCalledTimes(1);
          expect(loginApiMock).toHaveBeenCalledTimes(1);
        });
      });
    }

    it('로그인 쿠키 정보가 없는 경우', () => {
      // AppApi 메서드 모킹 - 로그인 쿠키 정보 없음
      const appApiMock = vi
        .spyOn(AppApi.prototype, 'getLoginCookies')
        .mockResolvedValue({
          data: {
            deviceId: '',
            provider: '',
            hasRefreshToken: false,
          },
          headers: new AxiosHeaders(),
          status: 200,
          statusText: 'OK',
          config: {} as InternalAxiosRequestConfig,
        });
      // LoginApi 메서드 모킹
      const loginApiNaverMock = vi.spyOn(
        LoginApi.prototype,
        'updateNaverLoginInfo'
      );
      const loginApiKakaoMock = vi.spyOn(
        LoginApi.prototype,
        'updateKakaoLoginInfo'
      );
      // useHeaderLogin 훅 렌더링
      const { result } = renderHook(() => useHeaderLogin(), { wrapper });
      // 비동기 처리 완료 대기
      waitFor(() => {
        // 유저 정보가 설정되지 않았는지 확인
        expect(result.current.user).toBeUndefined();
        // AppApi의 메서드가 호출되었는지 확인
        expect(appApiMock).toHaveBeenCalledTimes(1);
        // LoginApi의 메서드가 호출되지 않았는지 확인
        expect(loginApiNaverMock).toHaveBeenCalledTimes(0);
        expect(loginApiKakaoMock).toHaveBeenCalledTimes(0);
      });
    });

    // 각 제공자별 테스트 케이스 생성
    for (const providerKey in PROVIDER) {
      const providerName = PROVIDER[providerKey as keyof typeof PROVIDER];

      it(`${providerName}LoginApi에서 예외 발생하는 경우`, () => {
        // AppApi 메서드 모킹
        const appApiMock = vi
          .spyOn(AppApi.prototype, 'getLoginCookies')
          .mockResolvedValue({
            data: {
              deviceId: 'device-id',
              provider: providerKey,
              hasRefreshToken: true,
            },
            headers: new AxiosHeaders(),
            status: 200,
            statusText: 'OK',
            config: {} as InternalAxiosRequestConfig,
          });
        // LoginApi 메서드 모킹 - 예외 발생
        const errorMessage = `${providerName} 로그인 정보 갱신 실패`;
        let loginApiMock;
        if (providerKey === 'NAVER') {
          loginApiMock = vi
            .spyOn(LoginApi.prototype, 'updateNaverLoginInfo')
            .mockRejectedValue(new Error(errorMessage));
        } else {
          loginApiMock = vi
            .spyOn(LoginApi.prototype, 'updateKakaoLoginInfo')
            .mockRejectedValue(new Error(errorMessage));
        }
        // 콘솔 에러 로그 스파이 설정
        const errorLogSpy = vi
          .spyOn(console, 'error')
          .mockImplementation(() => {});
        // useHeaderLogin 훅 렌더링
        const { result } = renderHook(() => useHeaderLogin(), { wrapper });
        // 비동기 처리 완료 대기
        waitFor(() => {
          // 유저 정보가 설정되지 않았는지 확인
          expect(result.current.user).toBeUndefined();
          // AppApi 및 LoginApi의 메서드가 호출되었는지 확인
          expect(appApiMock).toHaveBeenCalledTimes(1);
          expect(loginApiMock).toHaveBeenCalledTimes(1);
          // 콘솔 에러 로그가 호출되었는지 확인
          expect(errorLogSpy).toHaveBeenCalledWith(
            errorMessage,
            expect.any(Error)
          );
        });
        // 콘솔 에러 로그 스파이 복원(원래대로 되돌림)
        errorLogSpy.mockRestore();
      });
    }
  });

  describe('handleLoginOnClick_로그인 클릭시 처리', () => {
    it('로그인 클릭시 처리', async () => {
      // location 객체 모킹
      Object.defineProperty(globalThis, 'location', {
        value: {
          pathname: '/search',
          search: '?keyword=드래곤&isAdult=false',
        },
        writable: true,
      });
      const pathname = globalThis.location.pathname;
      const search = globalThis.location.search;

      // useNavigate 훅 모킹
      vi.spyOn(reactRouterDom, 'useNavigate').mockReturnValue(
        reactRouterDomMock
      );

      // useHeaderLogin 훅 렌더링
      const { result } = renderHook(() => useHeaderLogin(), { wrapper });

      // 로그인 함수 실행
      act(() => {
        result.current.handleLoginOnClick();
      });

      // 리다이렉트 URL이 세션 스토리지에 저장되었는지 확인
      expect(sessionStorage.getItem(REDIRECT_URL)).toBe(`${pathname}${search}`);
      // 로그인 처리 플래그가 설정되었는지 확인
      expect(result.current.userOptionIsOpen).toBe(false);
      // navigate가 로그인 페이지로 이동했는지 확인
      expect(reactRouterDomMock).toHaveBeenCalledWith('/login');
    });
  });

  describe('handleLogoutOnClick_로그아웃 클릭시 처리', () => {
    it('로그아웃 클릭시 처리', async () => {
      // location 객체 모킹
      Object.defineProperty(globalThis, 'location', {
        value: {
          pathname: '/search',
          search: '?keyword=드래곤&isAdult=false',
        },
        writable: true,
      });
      const pathname = globalThis.location.pathname;
      const search = globalThis.location.search;
      // useNavigate 훅 모킹
      vi.spyOn(reactRouterDom, 'useNavigate').mockReturnValue(
        reactRouterDomMock
      );
      // useHeaderLogin 훅 렌더링
      const { result } = renderHook(() => useHeaderLogin(), { wrapper });
      // 로그아웃 함수 실행
      act(() => {
        result.current.handleLogoutOnClick();
      });
      // 리다이렉트 URL이 세션 스토리지에 저장되었는지 확인
      expect(sessionStorage.getItem(REDIRECT_URL)).toBe(`${pathname}${search}`);
      // navigate가 로그아웃 페이지로 이동했는지 확인
      expect(reactRouterDomMock).toHaveBeenCalledWith('/logout');
    });
  });

  describe('handleUserOptionToggle_유저 옵션 토글 함수', () => {
    it('유저 옵션 토글 처리', () => {
      // useHeaderLogin 훅 렌더링
      const { result } = renderHook(() => useHeaderLogin(), { wrapper });
      // 초기값 확인
      expect(result.current.userOptionIsOpen).toBe(false);
      // 토글 함수 실행
      act(() => {
        result.current.handleUserOptionToggle();
      });
      // 토글 후 값 확인
      expect(result.current.userOptionIsOpen).toBe(true);
      // 다시 토글 함수 실행
      act(() => {
        result.current.handleUserOptionToggle();
      });
      // 다시 토글 후 값 확인
      expect(result.current.userOptionIsOpen).toBe(false);
    });
  });

  describe('useEffect_마우스 클릭/키보드 키다운 이벤트', () => {
    it('유저 옵션 바깥영역 클릭 시 처리', () => {
      // useHeaderLogin 훅 렌더링
      const { result } = renderHook(() => useHeaderLogin(), { wrapper });
      // 유저 옵션 열기
      act(() => {
        result.current.setUserOptionIsOpen(true);
      });
      // 유저 옵션이 열렸는지 확인
      waitFor(() => {
        expect(result.current.userOptionIsOpen).toBe(true);
      });

      // 유저 옵션 ref에 mock 요소 할당
      const mockDiv = document.createElement('div');
      Object.defineProperty(result.current.userOptionRef, 'current', {
        value: mockDiv,
      });

      // 바깥 영역 클릭 이벤트 발생
      act(() => {
        fireEvent.mouseDown(document, { target: document.body });
      });

      waitFor(() => {
        // 유저 옵션이 닫혔는지 확인
        expect(result.current.userOptionIsOpen).toBe(false);
      });
    });
  });

  describe('useEffect_키보드 ESC 키다운 이벤트', () => {
    it('유저 옵션 esc 키다운 이벤트 발생 시 처리', () => {
      // useHeaderLogin 훅 렌더링
      const { result } = renderHook(() => useHeaderLogin(), { wrapper });
      // 유저 옵션 열기
      act(() => {
        result.current.setUserOptionIsOpen(true);
      });
      // ESC 키다운 이벤트 발생
      act(() => {
        fireEvent.keyDown(document, { key: 'Escape' });
      });
      waitFor(() => {
        // 유저 옵션이 닫혔는지 확인
        expect(result.current.userOptionIsOpen).toBe(false);
      });
    });
  });
});
