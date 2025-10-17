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

import { HomeRankingListResponseDto } from './data-contracts';
import { HttpClient, RequestParams } from './http-client';

export class HomeApi<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags home-api
   * @name GetContentRankings
   * @summary 콘텐츠 랭킹 정보 조회
   * @request GET:/api/home/rankings
   */
  getContentRankings = (
    query?: {
      /** @format int64 */
      user_id?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<HomeRankingListResponseDto, any>({
      path: `/api/home/rankings`,
      method: 'GET',
      query: query,
      ...params,
    });
}
