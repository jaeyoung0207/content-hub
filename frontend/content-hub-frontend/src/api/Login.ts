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

import {
  KakaoUserInfoDto,
  LoginUserResponseDto,
  NaverDeleteTokenDto,
} from "./data-contracts";
import { HttpClient, RequestParams } from "./http-client";

export class Login<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags login-controller
   * @name UpdateNaverLoginInfo
   * @request GET:/login/updateNaverLoginInfo
   */
  updateNaverLoginInfo = (params: RequestParams = {}) =>
    this.request<LoginUserResponseDto, any>({
      path: `/login/updateNaverLoginInfo`,
      method: "GET",
      ...params,
    });
  /**
   * No description
   *
   * @tags login-controller
   * @name UpdateKakaoLoginInfo
   * @request GET:/login/updateKakaoLoginInfo
   */
  updateKakaoLoginInfo = (
    query: {
      client_id: string;
    },
    params: RequestParams = {},
  ) =>
    this.request<LoginUserResponseDto, any>({
      path: `/login/updateKakaoLoginInfo`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags login-controller
   * @name GetNaverUserInfo
   * @request GET:/login/getNaverUserInfo
   */
  getNaverUserInfo = (
    query: {
      access_token: string;
      /** @format int32 */
      expires_in: number;
    },
    params: RequestParams = {},
  ) =>
    this.request<LoginUserResponseDto, any>({
      path: `/login/getNaverUserInfo`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags login-controller
   * @name GetNaverLoginInfo
   * @request GET:/login/getNaverLoginInfo
   */
  getNaverLoginInfo = (
    query: {
      code: string;
      state: string;
    },
    params: RequestParams = {},
  ) =>
    this.request<LoginUserResponseDto, any>({
      path: `/login/getNaverLoginInfo`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags login-controller
   * @name GetKakaoUserInfo
   * @request GET:/login/getKakaoUserInfo
   */
  getKakaoUserInfo = (
    query: {
      access_token: string;
      /** @format int32 */
      expires_in: number;
    },
    params: RequestParams = {},
  ) =>
    this.request<LoginUserResponseDto, any>({
      path: `/login/getKakaoUserInfo`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags login-controller
   * @name GetKakaoLoginInfo
   * @request GET:/login/getKakaoLoginInfo
   */
  getKakaoLoginInfo = (
    query: {
      client_id: string;
      redirect_uri: string;
      code: string;
    },
    params: RequestParams = {},
  ) =>
    this.request<LoginUserResponseDto, any>({
      path: `/login/getKakaoLoginInfo`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags login-controller
   * @name DeleteNaverToken
   * @request GET:/login/deleteNaverToken
   */
  deleteNaverToken = (
    query: {
      access_token: string;
    },
    params: RequestParams = {},
  ) =>
    this.request<NaverDeleteTokenDto, any>({
      path: `/login/deleteNaverToken`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags login-controller
   * @name DeleteKakaoToken
   * @request GET:/login/deleteKakaoToken
   */
  deleteKakaoToken = (
    query: {
      access_token: string;
      target_id: string;
    },
    params: RequestParams = {},
  ) =>
    this.request<KakaoUserInfoDto, any>({
      path: `/login/deleteKakaoToken`,
      method: "GET",
      query: query,
      ...params,
    });
}
