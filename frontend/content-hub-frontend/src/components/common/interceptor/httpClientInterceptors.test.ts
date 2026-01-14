import { describe, it, expect, vi, beforeEach } from 'vitest';
import axios, {
  AxiosError,
  AxiosResponse,
  InternalAxiosRequestConfig,
} from 'axios';
import dayjs from 'dayjs';
import {
  useUserStore,
  useProviderStore,
} from '@/components/common/store/globalStateStore';
import { clearUserData } from '@/components/common/utils/clearUtil';
import { waitFor } from '@testing-library/react';
import {
  httpClientRequestInterceptor,
  httpClientResponseErrorInterceptor,
  httpClientResponseInterceptor,
} from './httpClientInterceptors';
import { AxiosErrorType } from '../config/queryClientConfig';
import { setLoginInfo } from '../utils/loginUtil';

// axios mock
vi.mock('axios', async () => {
  return {
    default: {
      get: vi.fn(),
    },
  };
});

// setLoginInfo mock
vi.mock('@components/common/utils/loginUtil', () => ({
  setLoginInfo: vi.fn(),
}));

// clearUserData mock
vi.mock('@/components/common/utils/clearUtil', () => ({
  clearUserData: vi.fn(),
}));

describe('httpClientRequestInterceptor(NAVER)', () => {
  beforeEach(() => {
    // 초기 상태 설정
    useUserStore.setState({
      user: {
        name: 'name',
        nickname: 'nickname',
      },
      accessToken: 'access_token',
      jwt: 'jwt',
      expireDate: dayjs().subtract(1, 'hour').toISOString(), // 만료된 토큰
      clearUser: vi.fn(),
    });
    useProviderStore.setState({
      provider: 'NAVER',
    });
    // mocks 및 세션 초기화
    vi.clearAllMocks();
    sessionStorage.clear();
  });

  it('로그인 관련 API는 토큰 갱신 로직 스킵', async () => {
    const requestUrl = '/api/login/updateNaverLoginInfo';
    const requestConfig = {
      url: requestUrl,
      method: 'GET',
      headers: {},
    } as InternalAxiosRequestConfig;

    // 실제 인터셉터 호출
    httpClientRequestInterceptor(requestConfig, axios);

    await waitFor(() => {
      expect(requestConfig).toBeDefined();
      expect(requestConfig.url).toBe(requestUrl);
    });
  });

  it('JWT 없고 유저 정보가 있을 때 clearUserData 호출', async () => {
    const requestUrl = '/api/home/rankings';
    const requestConfig = {
      url: requestUrl,
      method: 'GET',
      headers: {},
    } as InternalAxiosRequestConfig;

    // 실제 인터셉터 호출
    httpClientRequestInterceptor(requestConfig, axios);

    await waitFor(() => {
      expect(clearUserData).toHaveBeenCalled();
    });
  });

  it('JWT 만료 시 네이버 로그인 업데이트 API 호출', async () => {
    // 새로운 토큰 정보
    const mockNewToken = {
      accessToken: 'new-access',
      jwt: 'new-jwt',
      expireDate: dayjs().add(1, 'hour').toISOString(), //
    };
    // 모의 응답 설정
    const axiosMock = vi.spyOn(axios, 'get').mockResolvedValueOnce({
      data: mockNewToken,
    });
    const requestUrl = '/api/home/rankings';
    const requestConfig = {
      url: requestUrl,
      method: 'GET',
      headers: {},
    } as InternalAxiosRequestConfig;

    // 실제 인터셉터 호출
    const result = await httpClientRequestInterceptor(requestConfig, axios);

    await waitFor(() => {
      // 검증
      expect(axiosMock).toHaveBeenCalledWith(
        expect.stringContaining('/api/login/updateNaverLoginInfo')
      );
      expect(setLoginInfo).toHaveBeenCalledWith(mockNewToken, 'NAVER');
      expect(result.headers.Authorization).toBe('Bearer new-jwt');
    });
  });

  it('JWT 만료 시 로그인 업데이트 API 호출 실패 시 clearUserData 호출', async () => {
    // 모의 응답 설정
    const axiosMock = vi.spyOn(axios, 'get').mockResolvedValueOnce({
      data: null,
    });
    const requestUrl = '/api/home';
    const requestConfig = {
      url: requestUrl,
      method: 'GET',
      headers: {},
    } as InternalAxiosRequestConfig;

    // 실제 인터셉터 호출
    httpClientRequestInterceptor(requestConfig, axios);

    await waitFor(() => {
      // 검증
      expect(axiosMock).toHaveBeenCalledWith(
        expect.stringContaining('/api/login/updateNaverLoginInfo')
      );
      expect(clearUserData).toHaveBeenCalled();
    });
  });

  it('로그인 갱신 API 호출 중 에러 발생 시 clearUserData 호출', async () => {
    // 모의 응답 설정
    const axiosMock = vi.spyOn(axios, 'get').mockRejectedValueOnce(new Error('Network Error'));
    const requestUrl = '/api/home/rankings';
    const requestConfig = {
      url: requestUrl,
      method: 'GET',
      headers: {},
    } as InternalAxiosRequestConfig;

    // 실제 인터셉터 호출
    httpClientRequestInterceptor(requestConfig, axios);

    await waitFor(() => {
      // 검증
      expect(axiosMock).toHaveBeenCalledWith(
        expect.stringContaining('/api/login/updateNaverLoginInfo')
      );
      expect(clearUserData).toHaveBeenCalled();
    });
  });

  it('로그인 갱신 중복 방지 확인', async () => {
    // 새로운 토큰 정보
    const mockNewToken = {
      accessToken: 'new-access',
      jwt: 'new-jwt',
      expireDate: dayjs().add(1, 'hour').toISOString(), //
    };
    // 모의 응답 설정
    const axiosMock = vi.spyOn(axios, 'get').mockResolvedValue({
      data: mockNewToken,
    });
    const requestUrl = '/api/home/rankings';
    const requestConfig = {
      url: requestUrl,
      method: 'GET',
      headers: {},
    } as InternalAxiosRequestConfig;

    // 동시에 여러번 인터셉터 호출
    await Promise.all([
      httpClientRequestInterceptor(requestConfig, axios),
      httpClientRequestInterceptor(requestConfig, axios),
      httpClientRequestInterceptor(requestConfig, axios),
    ]);

    await waitFor(() => {
      // 검증: axios.get이 한 번만 호출되었는지 확인
      expect(axiosMock).toHaveBeenCalledTimes(1);
      expect(setLoginInfo).toHaveBeenCalledWith(mockNewToken, 'NAVER');
    });
  });

});

describe('httpClientRequestInterceptor(KAKAO)', () => {
  beforeEach(() => {
    // 초기 상태 설정
    useUserStore.setState({
      user: {
        name: 'name',
        nickname: 'nickname',
      },
      clearUser: vi.fn(),
    });
    useProviderStore.setState({
      provider: 'KAKAO',
    });

    vi.clearAllMocks();
    sessionStorage.clear();
  });

  it('JWT 만료 시 카카오 로그인 업데이트 API 호출', async () => {
    // 새로운 토큰 정보
    const mockNewToken = {
      accessToken: 'new-access',
      jwt: 'new-jwt',
      expireDate: dayjs().add(1, 'hour').toISOString(), //
    };
    // 모의 응답 설정
    const axiosMock = vi.spyOn(axios, 'get').mockResolvedValueOnce({
      data: mockNewToken,
    });
    const requestUrl = '/api/home/rankings';
    const requestConfig = {
      url: requestUrl,
      method: 'GET',
      headers: {},
    } as InternalAxiosRequestConfig;

    // 실제 인터셉터 호출
    const result = await httpClientRequestInterceptor(requestConfig, axios);

    await waitFor(() => {
      // 검증
      expect(axiosMock).toHaveBeenCalledWith(
        expect.stringContaining('/api/login/updateKakaoLoginInfo'),
        { params: { clientId: 'kakao_client_id_placeholder' } }
      );
      expect(setLoginInfo).toHaveBeenCalledWith(mockNewToken, 'KAKAO');
      expect(result.headers.Authorization).toBe('Bearer new-jwt');
    });
  });
});

describe('httpClientResponseInterceptor', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    sessionStorage.clear();
  });

  it('정상 응답 시 인터셉터가 응답을 그대로 반환하는지 확인', async () => {
    const mockResponse = {
      data: { message: 'success' },
      config: {
        url: '/api/test',
        method: 'GET',
        headers: {},
      },
    } as AxiosResponse;

    // 실제 인터셉터 호출
    httpClientResponseInterceptor(mockResponse);

    await waitFor(() => {
      expect(mockResponse).toBeDefined();
      expect(mockResponse.data).toEqual({ message: 'success' });
    });
  });
});

describe('httpClientResponseErrorInterceptor', () => {
  it('401 응답 시 인터셉터가 에러를 감지하고 데이터를 비우는지 확인', async () => {
    const mockError = {
      response: {
        data: {
          status: 401,
          message: 'Unauthorized',
          name: 'UnauthorizedError',
          path: '/api/home/rankings',
        },
        config: {
          url: '/api/home/rankings',
          method: 'GET',
          headers: {},
        },
      },
    } as AxiosError<AxiosErrorType>;

    // 실제 인터셉터 호출
    httpClientResponseErrorInterceptor(mockError);

    await waitFor(() => {
      expect(clearUserData).toHaveBeenCalled();
    });
  });

  it('403 응답 시 인터셉터가 에러를 감지하고 데이터를 비우는지 확인', async () => {
    const mockError = {
      response: {
        data: {
          status: 403,
          message: 'Forbidden',
          name: 'ForbiddenError',
          path: '/api/home/rankings',
        },
        config: {
          url: '/api/home/rankings',
          method: 'GET',
          headers: {},
        },
      },
    } as AxiosError<AxiosErrorType>;

    // 실제 인터셉터 호출
    httpClientResponseErrorInterceptor(mockError);

    await waitFor(() => {
      expect(clearUserData).toHaveBeenCalled();
    });
  });
});
