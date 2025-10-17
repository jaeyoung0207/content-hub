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

import { CommonMediaTypeResponseDto, CsrfToken } from './data-contracts';
import { HttpClient, RequestParams } from './http-client';

export class CommonApi<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags common-api
   * @name SetAdultFlg
   * @summary 성인 여부 플래그 조회
   * @request POST:/api/common/setAdultFlg
   */
  setAdultFlg = (
    query: {
      adult_flg: boolean;
    },
    params: RequestParams = {}
  ) =>
    this.request<void, any>({
      path: `/api/common/setAdultFlg`,
      method: 'POST',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags common-api
   * @name ClearAdultFlg
   * @summary 성인 여부 플래그 클리어
   * @request POST:/api/common/clearAdultFlg
   */
  clearAdultFlg = (params: RequestParams = {}) =>
    this.request<void, any>({
      path: `/api/common/clearAdultFlg`,
      method: 'POST',
      ...params,
    });
  /**
   * No description
   *
   * @tags common-api
   * @name GetMediaTypes
   * @summary 공통 미디어 타입 조회
   * @request GET:/api/common/getMediaTypes
   */
  getMediaTypes = (params: RequestParams = {}) =>
    this.request<CommonMediaTypeResponseDto, any>({
      path: `/api/common/getMediaTypes`,
      method: 'GET',
      ...params,
    });
  /**
   * No description
   *
   * @tags common-api
   * @name GetCsrfToken
   * @summary CSRF 토큰 조회
   * @request GET:/api/common/getCsrfToken
   */
  getCsrfToken = (
    query?: {
      token?: CsrfToken;
    },
    params: RequestParams = {}
  ) =>
    this.request<CsrfToken, any>({
      path: `/api/common/getCsrfToken`,
      method: 'GET',
      query: query,
      ...params,
    });
}
