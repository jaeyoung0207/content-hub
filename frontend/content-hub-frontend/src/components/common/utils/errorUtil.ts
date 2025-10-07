import { ERROR_MESSAGE } from '../constants/constants';
import { isAxiosError } from 'axios';
import { toast } from 'react-toastify';
import i18n from 'i18next';
import { AxiosErrorType } from '../config/queryClientConfig';

/**
 * 콘솔 출력시 글자색을 붉은색으로 출력
 * @param error 에러 객체
 * @param message 에러 메시지
 */
export const handleUnExceptedError = (error: unknown, message?: string) => {
  // axios에러가 아닌경우에만 에러 표시
  if (!isAxiosError(error)) {
    changeConsoleColor('[ERROR]: ' + error); // 콘솔 출력시 글자색을 붉은색으로 출력
    toast.error(message ?? i18n.t('error.problemsOccurred'));
  }
};

/**
 * 이벤트 처리 공통 에러 핸들러
 * @param fn 이벤트 처리 함수
 */
export const commonErrorHandler =
  <T extends unknown[]>(fn: (...args: T) => void) =>
  (...args: T) => {
    try {
      fn(...args);
    } catch (err) {
      handleUnExceptedError(err, '이벤트 처리 중 문제가 발생했습니다.');
    }
  };

/**
 * 콘솔 출력시 글자색을 붉은색으로 출력
 * @param text 콘솔 출력 텍스트
 */
export const changeConsoleColor = (text: string) => {
  console.error('%c' + text, 'color:red');
};

/**
 * 에러 메세지 포맷팅
 * @param errorName 에러 이름
 * @param message 에러 메세지
 * @returns 포맷팅된 에러 메세지
 */
export const formattingErrorMsg = (
  errorName: string,
  message: string
): string => {
  return `[${errorName}]: ${message}`;
};

/**
 * API 에러 메세지 포멧팅
 * @param name 에러 이름
 * @param message 에러 메세지
 * @param path 에러가 발생한 경로
 * @param status HTTP 상태 코드
 * @param body 에러 응답 본문
 * @returns 포멧팅된 에러 메세지
 */
export const formattingApiErrorMsg = ({
  name,
  path,
  status,
  message,
  body,
}: AxiosErrorType) => {
  return (
    `[${name}]: path=${path}, status=${status}, message=${message}` +
    (body ? `, body=${body}` : '')
  );
};

/**
 * 에러 메시지 가져오기 함수
 * @param errorName 에러 이름
 * @returns 에러 메시지
 */
export const getErrorMessage = (errorName: string): string => {
  switch (errorName) {
    case ERROR_MESSAGE.NETWORK_ERROR.name:
      return ERROR_MESSAGE.NETWORK_ERROR.message;
    case ERROR_MESSAGE.AUTHORIZATION_ERROR.name:
      return ERROR_MESSAGE.AUTHORIZATION_ERROR.message;
    case ERROR_MESSAGE.VALIDATION_ERROR.name:
      return ERROR_MESSAGE.VALIDATION_ERROR.message;
    case ERROR_MESSAGE.API_RESPONSE_ERROR.name:
      return ERROR_MESSAGE.API_RESPONSE_ERROR.message;
    case ERROR_MESSAGE.BUSINESS_ERROR.name:
      return ERROR_MESSAGE.BUSINESS_ERROR.message;
    case ERROR_MESSAGE.SERVER_ERROR.name:
      return ERROR_MESSAGE.SERVER_ERROR.message;
    case ERROR_MESSAGE.BAD_REQUEST_ERROR.name:
      return ERROR_MESSAGE.BAD_REQUEST_ERROR.message;
    case ERROR_MESSAGE.SYSTEM_ERROR.name:
      return ERROR_MESSAGE.SYSTEM_ERROR.message;
    case ERROR_MESSAGE.TIMEOUT_ERROR.name:
      return ERROR_MESSAGE.TIMEOUT_ERROR.message;
    case ERROR_MESSAGE.UNEXPECTED_ERROR.name:
      return ERROR_MESSAGE.UNEXPECTED_ERROR.message;
    case ERROR_MESSAGE.API_RATE_LIMIT_EXCEEDED_ERROR.name:
      return ERROR_MESSAGE.API_RATE_LIMIT_EXCEEDED_ERROR.message;
    default:
      return i18n.t('error.unexpectedError');
  }
};
