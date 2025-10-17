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

export class MyCommentsApi<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags my-comments-api
   * @name GetMyCommentList
   * @summary 나의 코멘트 리스트 조회
   * @request GET:/api/my/comments/getMyCommentList
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
      path: `/api/my/comments/getMyCommentList`,
      method: 'GET',
      query: query,
      ...params,
    });
}
