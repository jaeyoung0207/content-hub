import { MutationCache, QueryCache, QueryClient } from "@tanstack/react-query";
import { formattingErrorMessage, getErrorMessage, changConsoleColor } from "../utils/commonUtil";
import i18n from "@/i18n";
import { toast } from "react-toastify";
import { AxiosError, isAxiosError } from "axios";
import { ERROR_CODE, ERROR_MESSAGE } from "../constants/constants";

/**
 * AxiosErrorType
 */
export type AxiosErrorType = {
  path: string;
  status: string;
  message: string;
  body?: string;
  name: string;
};

/**
 * React Query의 QueryClient 설정
 * 재시도 기능을 비활성화하고, 브라우저 포커스 시 데이터 재요청을 방지
 * API 요청의 에러를 일관되게 처리하기 위해 사용
 */
export const queryClientConfig = new QueryClient({
  defaultOptions: {
    queries: {
      throwOnError: true,
      retry: false, // 재시도 해제
      refetchOnWindowFocus: false, // 브라우저 포커스시 재요청 방지
      // refetchOnMount: false, // 컴포넌트 리마운트 시 재요청 방지
      // staleTime: 1000 * 60 * 5, // 데이터를 5분간 fresh로 간주
    }
  },
  // queryCache 설정
  queryCache: new QueryCache({
    onError: (error) => {
      outputError(error);
    }
  }),
  // mutationCache 설정
  mutationCache: new MutationCache({
    onError: (error) => {
      outputError(error);
    }
  })
});

/**
 * 에러 출력 함수
 * AxiosError를 처리하고, 에러 메시지를 toast로 표시
 * 에러 페이지나 점검 페이지로 리다이렉트하는 로직 포함
 * 에러 메시지와 상태 코드에 따라 다른 처리를 수행
 * 에러 메시지를 콘솔에 출력하고, 사용자에게 알림
 * 에러 페이지나 점검 페이지의 경우 중복 로딩 방지
 * @param error 에러 객체
 */
const outputError = (error: Error) => {
  // 에러가 AxiosError인 경우
  if (isAxiosError(error)) {
    // 에러페이지 또는 점검페이지의 경우 처리 종료(window.location.href 실행시 중복로딩하는 경우에 중복처리 방지)
    if (window.location.pathname.startsWith("/error")
      || window.location.pathname.startsWith("/maintenance")) {
      return;
    }
    const axiosError: AxiosError<AxiosErrorType> = error;
    // AxiosErrorType의 response가 없는 경우
    if (!axiosError.response) {
      changConsoleColor(formattingErrorMessage(ERROR_MESSAGE.NETWORK_ERROR.name, ERROR_MESSAGE.NETWORK_ERROR.message));
      toast.error(formattingErrorMessage(ERROR_MESSAGE.NETWORK_ERROR.name, ERROR_MESSAGE.NETWORK_ERROR.message), {
        toastId: "networkError" // 중복 토스트 방지
      });
      return;
    } 
    // AxiosErrorType의 response가 있지만 data가 없는 경우
    else if (!axiosError.response.data) {
      changConsoleColor(formattingErrorMessage(ERROR_MESSAGE.API_RESPONSE_ERROR.name, axiosError.message));
      toast.error(formattingErrorMessage(ERROR_MESSAGE.API_RESPONSE_ERROR.name, ERROR_MESSAGE.API_RESPONSE_ERROR.message), {
        toastId: "apiResponseError" // 중복 토스트 방지
      });
      return;
    }
    // AxiosErrorType에서 data 속성을 가져옴
    const data = axiosError.response.data;
    // AxiosErrorType의 속성들을 추출
    const name = data.name;
    const path = data.path;
    const status = data.status;
    const message = data.message;
    const body = data.body;
    // 콘솔 에러 메시지 출력
    const consoleErrorMsg = `[${name}]: path=${path}, status=${status}, message=${message}` + (body ? `, body=${body}` : '');
    changConsoleColor(consoleErrorMsg);
    // 토스트 에러 메시지 출력
    const toastErrorMsg = getErrorMessage(name);
    if (!redirectFromErrCode(status)) {
      toast.error(formattingErrorMessage(name, toastErrorMsg), {
        toastId: "apiResponseError" // 중복 토스트 방지
      });
    }
  } else {
    changConsoleColor(formattingErrorMessage(ERROR_MESSAGE.UNEXPECTED_ERROR.name, error.stack || error.message));
    toast.error(formattingErrorMessage(ERROR_MESSAGE.UNEXPECTED_ERROR.name, ERROR_MESSAGE.UNEXPECTED_ERROR.message), {
      toastId: "unexpectedError" // 중복 토스트 방지
    });
  }
}

/**
 * 에러 코드에 따라 리다이렉트 처리
 * @param status 상태 코드
 * @returns boolean
 */
const redirectFromErrCode = (status: string): boolean => {
  if (status === ERROR_CODE.UNAUTHORIZED.name) {
    // 응답 인터셉터에서 대응 -> true반환
    return true;
  } else if (status === ERROR_CODE.FORBIDDEN.name) {
    const message = i18n.t("error.forbidden");
    window.location.href = `/error?status=${status}&message=${encodeURIComponent(message)}`;
    return true;
  } else if (status === ERROR_CODE.NOT_FOUND.name) {
    window.location.href = `/error`;
    return true;
  } else if (status === ERROR_CODE.SERVICE_UNAVAILABLE.name) {
    window.location.href = `/maintenance`;
    return true;
  }
  return false;
}