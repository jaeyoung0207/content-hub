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
  DetailCommentsGetResponseDto,
  DetailCommentsSaveRequestDto,
  DetailCommentsUpdateRequestDto,
} from './data-contracts';
import { ContentType, HttpClient, RequestParams } from './http-client';

export class DetailCommentsApi<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags detail-comments-api
   * @name UpdateComent
   * @summary 코멘트 갱신
   * @request PUT:/api/detail/comments/updateComment
   */
  updateComent = (
    data: DetailCommentsUpdateRequestDto,
    params: RequestParams = {}
  ) =>
    this.request<boolean, any>({
      path: `/api/detail/comments/updateComment`,
      method: 'PUT',
      body: data,
      type: ContentType.Json,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-comments-api
   * @name SaveComent
   * @summary 코멘트 등록
   * @request POST:/api/detail/comments/saveComment
   */
  saveComent = (
    data: DetailCommentsSaveRequestDto,
    params: RequestParams = {}
  ) =>
    this.request<boolean, any>({
      path: `/api/detail/comments/saveComment`,
      method: 'POST',
      body: data,
      type: ContentType.Json,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-comments-api
   * @name GetStarRatingAverage
   * @summary 별점 평균 조회
   * @request GET:/api/detail/comments/getStarRatingAverage
   */
  getStarRatingAverage = (
    query: {
      content_media_type: string;
      api_id: string;
    },
    params: RequestParams = {}
  ) =>
    this.request<number, any>({
      path: `/api/detail/comments/getStarRatingAverage`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-comments-api
   * @name GetCommentList
   * @summary 코멘트 목록 조회
   * @request GET:/api/detail/comments/getCommentList
   */
  getCommentList = (
    query: {
      content_media_type: string;
      api_id: string;
      /** @format int32 */
      page?: number;
      provider_id?: string;
    },
    params: RequestParams = {}
  ) =>
    this.request<DetailCommentsGetResponseDto, any>({
      path: `/api/detail/comments/getCommentList`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-comments-api
   * @name DeleteComment
   * @summary 코멘트 삭제
   * @request DELETE:/api/detail/comments/deleteComment
   */
  deleteComment = (
    query: {
      /** @format int64 */
      comment_id: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<boolean, any>({
      path: `/api/detail/comments/deleteComment`,
      method: 'DELETE',
      query: query,
      ...params,
    });
}
