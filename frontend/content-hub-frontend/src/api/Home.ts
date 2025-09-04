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

export class Home<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags home-controller
   * @name GetContentRankings
   * @request GET:/home/rankings
   */
  getContentRankings = (params: RequestParams = {}) =>
    this.request<HomeRankingListResponseDto, any>({
      path: `/home/rankings`,
      method: 'GET',
      ...params,
    });
}
