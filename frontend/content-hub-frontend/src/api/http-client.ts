/* eslint-disable */
/* tslint:disable */
// @ts-nocheck
/*
 * ---------------------------------------------------------------
 * ## THIS FILE WAS GENERATED VIA SWAGGER-TYPESCRIPT-API        ##
 * ##                                                           ##
 * ## AUTHOR: acacode                                           ##
 * ## SOURCE: https://github.com/acacode/swagger-typescript-api ##
 * ---------------------------------------------------------------
 */

import { AxiosErrorType } from '@/components/common/config/queryClientConfig'; // add custom config
import { settings } from '@/components/common/config/settings'; // add custom config
import {
  useConfirmDialogStore,
  useProviderStore,
  useUserStore,
} from '@/components/common/store/globalStateStore'; // add custom config
import { clearUserData } from '@/components/common/utils/commonUtil'; // add custom config
import type {
  AxiosError,
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  HeadersDefaults,
  ResponseType,
} from 'axios'; // add custom config
import axios from 'axios';
import dayjs from 'dayjs'; // add custom config
import { LoginProfileResultDto } from './data-contracts'; // add custom config

export type QueryParamsType = Record<string | number, any>;

export interface FullRequestParams
  extends Omit<AxiosRequestConfig, 'data' | 'params' | 'url' | 'responseType'> {
  /** set parameter to `true` for call `securityWorker` for this request */
  secure?: boolean;
  /** request path */
  path: string;
  /** content type of request body */
  type?: ContentType;
  /** query params */
  query?: QueryParamsType;
  /** format of response (i.e. response.json() -> format: "json") */
  format?: ResponseType;
  /** request body */
  body?: unknown;
}

export type RequestParams = Omit<
  FullRequestParams,
  'body' | 'method' | 'query' | 'path'
>;

export interface ApiConfig<SecurityDataType = unknown>
  extends Omit<AxiosRequestConfig, 'data' | 'cancelToken'> {
  securityWorker?: (
    securityData: SecurityDataType | null
  ) => Promise<AxiosRequestConfig | void> | AxiosRequestConfig | void;
  secure?: boolean;
  format?: ResponseType;
}

export enum ContentType {
  Json = 'application/json',
  FormData = 'multipart/form-data',
  UrlEncoded = 'application/x-www-form-urlencoded',
  Text = 'text/plain',
}

const backendUrl = settings.appBackendUrl; // add custom config

export class HttpClient<SecurityDataType = unknown> {
  public instance: AxiosInstance;
  private securityData: SecurityDataType | null = null;
  private securityWorker?: ApiConfig<SecurityDataType>['securityWorker'];
  private secure?: boolean;
  private format?: ResponseType;

  constructor({
    securityWorker,
    secure,
    format,
    navigate,
    ...axiosConfig
  }: ApiConfig<SecurityDataType> = {}) {
    this.instance = axios.create({
      ...axiosConfig,
      baseURL: axiosConfig.baseURL || backendUrl,
    }); // add custom config
    this.secure = secure;
    this.format = format;
    this.securityWorker = securityWorker;
    axios.defaults.withCredentials = true; // add custom config

    // 유저정보
    const { user, clearUser } = useUserStore.getState();
    // provider 정보
    const { provider } = useProviderStore.getState();

    // axios 공통 요청 인터셉터 // add custom config
    this.instance.interceptors.request.use(
      async (request) => {
        // 접근토큰
        const jwt = sessionStorage.getItem('jwt');
        // 만료시각
        const expireDateStr = sessionStorage.getItem('expireDate');
        const expireDate =
          expireDateStr && dayjs(expireDateStr, 'YYYYMMDDHHmmss');
        // 현재시각
        const now = dayjs();
        // 접근토큰 만료 확인
        if (jwt && dayjs(expireDate).isBefore(dayjs(now)) && user && provider) {
          // 접근토큰 갱신 API 조회
          let res;
          if (provider === LOGIN_PROVIDER.NAVER) {
            res = (await axios.get(`${backendUrl}/login/updateNaverLoginInfo`))
              .data as LoginProfileResultDto;
          } else if (provider === LOGIN_PROVIDER.KAKAO) {
            res = (
              await axios.get(`${backendUrl}/login/updateKakaoLoginInfo`, {
                params: {
                  clientId: settings.kakaoClientId,
                },
              })
            ).data as LoginProfileResultDto;
          }
          if (res) {
            console.log('접근토큰 갱신');
            // 접근토큰을 sessionStorage에 저장
            sessionStorage.setItem('accessToken', res.accessToken);
            // JWT를 sessionStorage에 저장
            sessionStorage.setItem('jwt', res.jwt);
            // 만료시각을 sessionStorage에 저장
            sessionStorage.setItem('expireDate', res.expireDate);
          } else {
            // 유저정보 클리어
            clearUserData();
          }
        } else if (!jwt && user) {
          // 유저정보 클리어
          clearUserData();
          // 로그인 확인 다이얼로그 표시
          // useConfirmDialogStore.getState().setIsConfirmDialogOpen();
        }
        return request;
      },
      (error) => {
        // 요청 전 단계의 예외만 처리
        return Promise.reject(error);
      }
    );

    // axios 공통 응답 인터셉터 // add custom config
    this.instance.interceptors.response.use(
      (response) => {
        console.log('API 조회 성공: ' + response.config.url);
        return response;
      },
      (error: AxiosError<AxiosErrorType>) => {
        const data = error.response?.data;
        // 로그인 만료시
        if (data?.status === 401 || data?.status === '401') {
          // 유저정보 클리어
          clearUserData();
          // 로그인 확인 다이얼로그 표시
          useConfirmDialogStore.getState().setIsConfirmDialogOpen();
        }
        return Promise.reject(error);
      }
    );
  }

  public setSecurityData = (data: SecurityDataType | null) => {
    this.securityData = data;
  };

  protected mergeRequestParams(
    params1: AxiosRequestConfig,
    params2?: AxiosRequestConfig
  ): AxiosRequestConfig {
    const method = params1.method || (params2 && params2.method);

    return {
      ...this.instance.defaults,
      ...params1,
      ...(params2 || {}),
      headers: {
        ...((method &&
          this.instance.defaults.headers[
            method.toLowerCase() as keyof HeadersDefaults
          ]) ||
          {}),
        ...(params1.headers || {}),
        ...((params2 && params2.headers) || {}),
      },
    };
  }

  protected stringifyFormItem(formItem: unknown) {
    if (typeof formItem === 'object' && formItem !== null) {
      return JSON.stringify(formItem);
    } else {
      return `${formItem}`;
    }
  }

  protected createFormData(input: Record<string, unknown>): FormData {
    if (input instanceof FormData) {
      return input;
    }
    return Object.keys(input || {}).reduce((formData, key) => {
      const property = input[key];
      const propertyContent: any[] =
        property instanceof Array ? property : [property];

      for (const formItem of propertyContent) {
        const isFileType = formItem instanceof Blob || formItem instanceof File;
        formData.append(
          key,
          isFileType ? formItem : this.stringifyFormItem(formItem)
        );
      }

      return formData;
    }, new FormData());
  }

  public request = async <T = any, _E = any>({
    secure,
    path,
    type,
    query,
    format,
    body,
    ...params
  }: FullRequestParams): Promise<AxiosResponse<T>> => {
    const secureParams =
      ((typeof secure === 'boolean' ? secure : this.secure) &&
        this.securityWorker &&
        (await this.securityWorker(this.securityData))) ||
      {};
    const requestParams = this.mergeRequestParams(params, secureParams);
    const responseFormat = format || this.format || undefined;

    if (
      type === ContentType.FormData &&
      body &&
      body !== null &&
      typeof body === 'object'
    ) {
      body = this.createFormData(body as Record<string, unknown>);
    }

    if (
      type === ContentType.Text &&
      body &&
      body !== null &&
      typeof body !== 'string'
    ) {
      body = JSON.stringify(body);
    }

    const token = sessionStorage.getItem('jwt'); // add custom config
    return this.instance.request({
      ...requestParams,
      headers: {
        ...(requestParams.headers || {}),
        ...(type ? { 'Content-Type': type } : {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}), // add custom config
      },
      params: query,
      responseType: responseFormat,
      data: body,
      url: path,
    });
  };
}
