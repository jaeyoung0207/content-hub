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

import { AppMediaTypeResponseDto, CsrfToken } from './data-contracts';
import { HttpClient, RequestParams } from './http-client';

export class AppApi<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags app-api
   * @name SetAdultFlg
   * @summary 성인 여부 플래그 조회
   * @request POST:/api/app/setAdultFlg
   */
  setAdultFlg = (
    query: {
      adult_flg: boolean;
    },
    params: RequestParams = {}
  ) =>
    this.request<void, any>({
      path: `/api/app/setAdultFlg`,
      method: 'POST',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags app-api
   * @name ClearAdultFlg
   * @summary 성인 여부 플래그 클리어
   * @request POST:/api/app/clearAdultFlg
   */
  clearAdultFlg = (params: RequestParams = {}) =>
    this.request<void, any>({
      path: `/api/app/clearAdultFlg`,
      method: 'POST',
      ...params,
    });
  /**
   * No description
   *
   * @tags app-api
   * @name GetMediaTypes
   * @summary 공통 미디어 타입 조회
   * @request GET:/api/app/getMediaTypes
   */
  getMediaTypes = (params: RequestParams = {}) =>
    this.request<AppMediaTypeResponseDto, any>({
      path: `/api/app/getMediaTypes`,
      method: 'GET',
      ...params,
    });
  /**
   * No description
   *
   * @tags app-api
   * @name GetCsrfToken
   * @summary CSRF 토큰 조회
   * @request GET:/api/app/getCsrfToken
   */
  getCsrfToken = (
    query?: {
      token?: CsrfToken;
    },
    params: RequestParams = {}
  ) =>
    this.request<CsrfToken, any>({
      path: `/api/app/getCsrfToken`,
      method: 'GET',
      query: query,
      ...params,
    });
}
