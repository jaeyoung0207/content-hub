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

import { CsrfToken } from './data-contracts';
import { HttpClient, RequestParams } from './http-client';

export class Common<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags common-controller
   * @name SetAdultFlg
   * @request POST:/common/setAdultFlg
   */
  setAdultFlg = (
    query: {
      adult_flg: boolean;
    },
    params: RequestParams = {}
  ) =>
    this.request<void, any>({
      path: `/common/setAdultFlg`,
      method: 'POST',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags common-controller
   * @name ClearAdultFlg
   * @request POST:/common/clearAdultFlg
   */
  clearAdultFlg = (params: RequestParams = {}) =>
    this.request<void, any>({
      path: `/common/clearAdultFlg`,
      method: 'POST',
      ...params,
    });
  /**
   * No description
   *
   * @tags common-controller
   * @name GetCsrfToken
   * @request GET:/common/getCsrfToken
   */
  getCsrfToken = (
    query?: {
      token?: CsrfToken;
    },
    params: RequestParams = {}
  ) =>
    this.request<CsrfToken, any>({
      path: `/common/getCsrfToken`,
      method: 'GET',
      query: query,
      ...params,
    });
}
