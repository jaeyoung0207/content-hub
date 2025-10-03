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

import { MyCommentsResponseDto } from './data-contracts';
import { HttpClient, RequestParams } from './http-client';

export class My<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags my-comments-controller
   * @name GetMyCommentList
   * @request GET:/my/comments/getMyCommentList
   */
  getMyCommentList = (
    query: {
      /** @format int64 */
      user_id: number;
      /** @format int32 */
      page_no: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<MyCommentsResponseDto, any>({
      path: `/my/comments/getMyCommentList`,
      method: 'GET',
      query: query,
      ...params,
    });
}
