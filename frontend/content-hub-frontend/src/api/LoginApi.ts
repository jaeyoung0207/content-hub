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
} from './data-contracts';
import { HttpClient, RequestParams } from './http-client';

export class LoginApi<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags login-api
   * @name UpdateNaverLoginInfo
   * @summary 네이버 로그인 정보 갱신
   * @request GET:/api/login/updateNaverLoginInfo
   */
  updateNaverLoginInfo = (params: RequestParams = {}) =>
    this.request<LoginUserResponseDto, any>({
      path: `/api/login/updateNaverLoginInfo`,
      method: 'GET',
      ...params,
    });
  /**
   * No description
   *
   * @tags login-api
   * @name UpdateKakaoLoginInfo
   * @summary 카카오 로그인 정보 갱신
   * @request GET:/api/login/updateKakaoLoginInfo
   */
  updateKakaoLoginInfo = (
    query: {
      client_id: string;
    },
    params: RequestParams = {}
  ) =>
    this.request<LoginUserResponseDto, any>({
      path: `/api/login/updateKakaoLoginInfo`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags login-api
   * @name GetNaverUserInfo
   * @summary 네이버 유저 정보 조회
   * @request GET:/api/login/getNaverUserInfo
   */
  getNaverUserInfo = (
    query: {
      access_token: string;
      /** @format int32 */
      expires_in: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<LoginUserResponseDto, any>({
      path: `/api/login/getNaverUserInfo`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags login-api
   * @name GetNaverLoginInfo
   * @summary 네이버 로그인 정보 조회
   * @request GET:/api/login/getNaverLoginInfo
   */
  getNaverLoginInfo = (
    query: {
      code: string;
      state: string;
    },
    params: RequestParams = {}
  ) =>
    this.request<LoginUserResponseDto, any>({
      path: `/api/login/getNaverLoginInfo`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags login-api
   * @name GetKakaoUserInfo
   * @summary 카카오 유저 정보 조회
   * @request GET:/api/login/getKakaoUserInfo
   */
  getKakaoUserInfo = (
    query: {
      access_token: string;
      /** @format int32 */
      expires_in: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<LoginUserResponseDto, any>({
      path: `/api/login/getKakaoUserInfo`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags login-api
   * @name GetKakaoLoginInfo
   * @summary 카카오 로그인 정보 조회
   * @request GET:/api/login/getKakaoLoginInfo
   */
  getKakaoLoginInfo = (
    query: {
      client_id: string;
      redirect_uri: string;
      code: string;
    },
    params: RequestParams = {}
  ) =>
    this.request<LoginUserResponseDto, any>({
      path: `/api/login/getKakaoLoginInfo`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags login-api
   * @name DeleteNaverToken
   * @summary 네이버 토큰 삭제
   * @request GET:/api/login/deleteNaverToken
   */
  deleteNaverToken = (
    query: {
      access_token: string;
      target_id: string;
      /** @format int64 */
      user_id: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<NaverDeleteTokenDto, any>({
      path: `/api/login/deleteNaverToken`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags login-api
   * @name DeleteKakaoToken
   * @summary 카카오 토큰 삭제
   * @request GET:/api/login/deleteKakaoToken
   */
  deleteKakaoToken = (
    query: {
      access_token: string;
      target_id: string;
      /** @format int64 */
      user_id: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<KakaoUserInfoDto, any>({
      path: `/api/login/deleteKakaoToken`,
      method: 'GET',
      query: query,
      ...params,
    });
}
