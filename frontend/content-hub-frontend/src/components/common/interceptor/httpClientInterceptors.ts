import { LoginUserResponseDto } from '@/api/data-contracts';
import {
  AxiosError,
  AxiosResponse,
  AxiosStatic,
  InternalAxiosRequestConfig,
} from 'axios';
import dayjs from 'dayjs';
import { LOGIN_PROVIDER } from '../constants/constants';
import { settings } from '../config/settings';
import { clearUserData } from '../utils/clearUtil';
import { AxiosErrorType } from '../config/queryClientConfig';
import { useProviderStore, useUserStore } from '../store/globalStateStore';
import { setLoginInfo } from '../utils/loginUtil';
import { toast } from 'react-toastify';

// Sentry 동적 import
const Sentry = import('@sentry/react');
// 토큰 갱신 중복 호출 방지용 Promise 변수
let tokenRefreshPromise: Promise<LoginUserResponseDto | null> | null = null;

/**
 * httpClient 요청 인터셉터
 */
export const httpClientRequestInterceptor = async (
  request: InternalAxiosRequestConfig,
  axios: AxiosStatic
) => { // NOSONAR

  // 로그인 관련 API는 토큰 갱신 로직 스킵
  if (request.url?.startsWith('/api/login/')) {
    return request;
  }
  // 유저정보, JWT, 만료시각
  const { user, jwt, expireDate } = useUserStore.getState();
  // provider 정보
  const { provider } = useProviderStore.getState();
  // JWT가 없고 유저정보가 있는 경우 처리 종료
  if (!jwt && user) {
    // 유저정보 클리어
    clearUserData();
    return request;
  }
  // 현재시각
  const now = dayjs();
  // 접근토큰 만료 확인
  const isJwtExpired =
    user && provider && jwt && dayjs(expireDate).isBefore(dayjs(now));
  if (isJwtExpired) {
    // 토큰 갱신이 진행 중이 아니라면 갱신 시작
    tokenRefreshPromise ??= (async () => {
      // API 응답 저장용 변수
      let apiRes: LoginUserResponseDto | null = null;
      try {
        // 백엔드 URL
        const backendUrl = settings.appBackendUrl;
        // 접근토큰 갱신 API 조회
        if (provider === LOGIN_PROVIDER.NAVER) {
          apiRes = (await axios.get(`${backendUrl}/api/login/updateNaverLoginInfo`))
            .data as LoginUserResponseDto;
        } else if (provider === LOGIN_PROVIDER.KAKAO) {
          apiRes = (
            await axios.get(`${backendUrl}/api/login/updateKakaoLoginInfo`, {
              params: {
                clientId: settings.kakaoClientId,
              },
            })
          ).data as LoginUserResponseDto;
        }
        if (apiRes && 'accessToken' in apiRes && 'jwt' in apiRes && 'expireDate' in apiRes) {
          // 로그인 정보 설정
          await setLoginInfo(apiRes, provider);
        }
        else {
          // 유저정보 클리어
          clearUserData();
        }
      } catch (error) {
        // 유저정보 클리어
        clearUserData();
        console.error('토큰 갱신 처리 중 에러 발생: ', error);
        toast.error('로그인이 만료되었습니다. 다시 로그인 해주세요.', {
          toastId: 'tokenRefreshError',
        });
        // 2초 정도 대기 후 페이지 이동 (사용자가 읽을 시간 확보)
        setTimeout(() => {
          globalThis.location.href = '/login';
        }, 2000);
      }
      finally {
        // 토큰 갱신 Promise 초기화
        // 여기서 null로 초기화 해도 후속의 await 가 Promise 객체의 참조를 유지 하여 응답을 받을 수 있으므로 문제 없음
        tokenRefreshPromise = null;
      }
      return apiRes;
    })();

    // 모든 요청(최초 요청 포함)이 여기서 동일한 결과를 기다림
    const res = await tokenRefreshPromise;
    // 갱신된 접근토큰으로 헤더 설정
    if (res && 'jwt' in res) {
      request.headers.Authorization = `Bearer ${res.jwt}`;
    }
  }
  return request;
};

/**
 * httpClient 응답 인터셉터
 */
export const httpClientResponseInterceptor = async (
  response: AxiosResponse
) => {

  // Sentry에 성공 로그 남기기
  (async () => {
    (await Sentry).addBreadcrumb({
      category: 'api',
      message: `API Success: ${response.config.url}`,
      level: 'info',
    });
  })();
  return response;
};

/**
 * httpClient 응답 에러 인터셉터
 */
export const httpClientResponseErrorInterceptor = async (
  error: AxiosError<AxiosErrorType>
) => {

  const data = error.response?.data;
  // 401 또는 403 에러인 경우 유저정보 클리어
  if (data?.status === 401 || data?.status === 403) {
    clearUserData();
  }
  return error;
};
