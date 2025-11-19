import { MutationCache, QueryCache, QueryClient } from '@tanstack/react-query';
import {
  getErrorMessage,
  changeConsoleColor,
  formattingErrorMsg,
  formattingApiErrorMsg,
} from '../utils/errorUtil';
import i18n from '@/i18n';
import { toast } from 'react-toastify';
import { AxiosError, isAxiosError } from 'axios';
import { ERROR_CODE, ERROR_MESSAGE, ONE_MINUTE } from '../constants/constants';

// Sentry 동적 import
const Sentry = import('@sentry/react');

/**
 * AxiosErrorType
 */
export type AxiosErrorType = {
  path: string;
  status: number;
  message: string;
  body?: string;
  name: string;
};

/**
 * React Query의 QueryClient 설정
 */
export const queryClientConfig = new QueryClient({
  defaultOptions: {
    queries: {
      throwOnError: false, // 에러 발생시 throw하지 않음
      retry: false, // 재시도 해제
      staleTime: ONE_MINUTE * 5, // 데이터를 5분간 fresh로 간주
      gcTime: ONE_MINUTE * 10, // 캐시된 데이터를 10분간 유지(旧 react-query의 cacheTime)
      refetchOnWindowFocus: false, // 브라우저 포커스시 재요청 방지
      refetchOnMount: false, // 컴포넌트 리마운트 시 재요청 방지
    },
  },
  // queryCache 설정
  queryCache: new QueryCache({
    onError: (error) => {
      outputError(error);
    },
  }),
  // mutationCache 설정
  mutationCache: new MutationCache({
    onError: (error) => {
      outputError(error);
    },
  }),
});

/**
 * 에러 출력 함수
 * AxiosError를 처리하고, 에러 메시지를 console, toast로 표시
 * @param error 에러 객체
 */
const outputError = (error: Error) => {
  // 에러가 AxiosError인 경우
  if (isAxiosError(error)) {
    // 에러페이지 또는 점검페이지의 경우 처리 종료(window.location.href 실행시 중복로딩하는 경우에 중복처리 방지)
    if (
      window.location.pathname.startsWith('/error') ||
      window.location.pathname.startsWith('/maintenance')
    ) {
      return;
    }
    const axiosError: AxiosError<AxiosErrorType> = error;
    // AxiosErrorType의 response가 없는 경우
    if (!axiosError.response) {
      // 네트워크 에러 코드
      const NETWORK_ERROR = 'ERR_NETWORK';
      // 에러 이름
      const errorName =
        axiosError.code === NETWORK_ERROR
          ? ERROR_MESSAGE.NETWORK_ERROR.name
          : axiosError.code
            ? axiosError.code
            : ERROR_MESSAGE.UNEXPECTED_ERROR.name;
      // 에러 메시지
      const errorMsg =
        axiosError.code === NETWORK_ERROR
          ? ERROR_MESSAGE.NETWORK_ERROR.message
          : axiosError.message
            ? axiosError.message
            : ERROR_MESSAGE.UNEXPECTED_ERROR.message;
      changeConsoleColor(formattingErrorMsg(errorName, errorMsg));
      toast.error(formattingErrorMsg(errorName, errorMsg), {
        toastId: 'unexpectedError', // 중복 토스트 방지
      });
      return;
    }
    // AxiosErrorType의 response가 있지만 data가 없는 경우
    else if (!axiosError.response.data) {
      changeConsoleColor(
        formattingErrorMsg(
          ERROR_MESSAGE.API_RESPONSE_ERROR.name,
          axiosError.message
        )
      );
      toast.error(
        formattingErrorMsg(
          ERROR_MESSAGE.API_RESPONSE_ERROR.name,
          ERROR_MESSAGE.API_RESPONSE_ERROR.message
        ),
        {
          toastId: 'apiResponseError', // 중복 토스트 방지
        }
      );
      return;
    }
    // AxiosErrorType의 속성들을 추출
    const data = axiosError.response.data;
    const name = data.name ?? ERROR_MESSAGE.UNEXPECTED_ERROR.name;
    const path = data.path;
    const status = data.status;
    const message = data.message;
    const body = data.body;
    // 콘솔 에러 메시지 출력
    const consoleErrorMsg = formattingApiErrorMsg({
      name,
      path,
      status,
      message,
      body,
    });
    changeConsoleColor(consoleErrorMsg);
    // 토스트 에러 메시지 출력
    const toastErrorMsg =
      status === 429
        ? i18n.t('warn.apiRateLimitExceeded', {
            retryAfter: axiosError.response.headers['retry-after'],
          })
        : getErrorMessage(name);
    // 인증 에러인 경우 세션 스토리지에 에러 메시지 저장
    if (status === ERROR_CODE.UNAUTHORIZED.status) {
      sessionStorage.setItem('consoleMessage', consoleErrorMsg);
      sessionStorage.setItem(
        'toastMessage',
        formattingErrorMsg(name, toastErrorMsg)
      );
    }
    // 리다이렉트 처리
    if (!redirectFromErrorCode(status)) {
      toast.error(formattingErrorMsg(name, toastErrorMsg), {
        toastId: 'apiResponseError', // 중복 토스트 방지
      });
    }
    (async () => {
      // Sentry 에러 보고
      (await Sentry).setContext('apiError', {
        path,
        status,
      });
      (await Sentry).captureMessage(consoleErrorMsg);
    })();
  } else {
    changeConsoleColor(
      formattingErrorMsg(
        ERROR_MESSAGE.UNEXPECTED_ERROR.name,
        error.stack || error.message
      )
    );
    toast.error(
      formattingErrorMsg(
        ERROR_MESSAGE.UNEXPECTED_ERROR.name,
        ERROR_MESSAGE.UNEXPECTED_ERROR.message
      ),
      {
        toastId: 'unexpectedError', // 중복 토스트 방지
      }
    );
    // Sentry 에러 보고
    (async () => {
      (await Sentry).captureMessage(error.stack || error.message);
    })();
  }
  // Sentry 에러 보고
  (async () => {
    (await Sentry).setTag('page', window.location.pathname);
    (await Sentry).captureException(error);
  })();
};

/**
 * 에러 코드에 따라 리다이렉트 처리
 * @param status 상태 코드
 * @returns boolean
 */
const redirectFromErrorCode = (status: number): boolean => {
  if (status === ERROR_CODE.UNAUTHORIZED.status) {
    window.location.replace('/');
    return true;
  } else if (status === ERROR_CODE.FORBIDDEN.status) {
    const message = i18n.t('error.forbiddenError');
    window.location.replace(
      `/error?status=${status}&message=${encodeURIComponent(message)}`
    );
    return true;
  } else if (status === ERROR_CODE.NOT_FOUND.status) {
    window.location.replace('/error');
    return true;
  } else if (status === ERROR_CODE.SERVICE_UNAVAILABLE.status) {
    window.location.replace('/maintenance');
    return true;
  }
  return false;
};
