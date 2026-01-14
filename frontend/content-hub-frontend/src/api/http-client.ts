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
  httpClientRequestInterceptor,
  httpClientResponseErrorInterceptor,
  httpClientResponseInterceptor,
} from '@/components/common/interceptor/httpClientInterceptors'; // add custom config
import type {
  AxiosError,
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  HeadersDefaults,
  ResponseType,
} from 'axios'; // add custom config
import axios from 'axios';
import { useUserStore } from '@/components/common/store/globalStateStore'; // add custom config

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

function getXsrfTokenFromCookie() {
  const cookies = document.cookie.split('; ');
  const xsrf = cookies.find((cookie) => cookie.startsWith('XSRF-TOKEN='));
  return xsrf ? xsrf.split('=')[1] : null;
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
      withCredentials: true,
    }); // 인스턴스에서 발생하는 모든 요청에 대해 쿠키 추가
    this.secure = secure;
    this.format = format;
    this.securityWorker = securityWorker;
    axios.defaults.withCredentials = true; // axios를 직접 사용시 쿠키 추가

    // axios 공통 요청 인터셉터 // add custom config
    this.instance.interceptors.request.use(
      async (request) => {
        // 요청 인터셉터 처리
        return httpClientRequestInterceptor(request, axios);
      },
      (error) => {
        // 요청 전 단계의 예외만 처리
        return Promise.reject(error);
      }
    );

    // axios 공통 응답 인터셉터 // add custom config
    this.instance.interceptors.response.use(
      (response) => {
        // 응답 성공 인터셉터 처리
        return httpClientResponseInterceptor(response);
      },
      (error: AxiosError<AxiosErrorType>) => {
        // 응답 에러 인터셉터 처리
        const errorInterceptor = httpClientResponseErrorInterceptor(error);
        return Promise.reject(errorInterceptor);
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

    const jwt = useUserStore.getState().jwt; // add custom config
    const xsrfToken = getXsrfTokenFromCookie(); // add custom config
    return this.instance.request({
      ...requestParams,
      withCredentials: true, // 개별 요청마다 명시적으로 쿠키 추가
      headers: {
        ...(requestParams.headers || {}),
        ...(type ? { 'Content-Type': type } : {}),
        ...(jwt ? { Authorization: `Bearer ${jwt}` } : {}), // add custom config
        ...(xsrfToken ? { 'X-XSRF-TOKEN': xsrfToken } : {}), // add custom config
      },
      params: query,
      responseType: responseFormat,
      data: body,
      url: path,
    });
  };
}
