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
  WishlistCheckResultResponseDto,
  WishlistListResponseDto,
  WishlistRequestDto,
} from './data-contracts';
import { ContentType, HttpClient, RequestParams } from './http-client';

export class WishlistApi<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags wishlist-api
   * @name SaveWishlist
   * @summary 위시리스트 등록
   * @request POST:/api/wishlist/saveWishlist
   */
  saveWishlist = (data: WishlistRequestDto, params: RequestParams = {}) =>
    this.request<boolean, any>({
      path: `/api/wishlist/saveWishlist`,
      method: 'POST',
      body: data,
      type: ContentType.Json,
      ...params,
    });
  /**
   * No description
   *
   * @tags wishlist-api
   * @name GetWishlist
   * @summary 위시리스트 조회
   * @request POST:/api/wishlist/getWishlist
   */
  getWishlist = (
    query: {
      /** @format int64 */
      userId: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<WishlistListResponseDto, any>({
      path: `/api/wishlist/getWishlist`,
      method: 'POST',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags wishlist-api
   * @name CheckWishlist
   * @summary 위시리스트 체크
   * @request GET:/api/wishlist/checkWishlist
   */
  checkWishlist = (
    query: {
      /** @format int64 */
      user_id: number;
      api_id: string;
      content_media_type: string;
    },
    params: RequestParams = {}
  ) =>
    this.request<WishlistCheckResultResponseDto, any>({
      path: `/api/wishlist/checkWishlist`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags wishlist-api
   * @name DeleteWishlist
   * @summary 위시리스트 삭제
   * @request DELETE:/api/wishlist/deleteWishlist
   */
  deleteWishlist = (data: WishlistRequestDto, params: RequestParams = {}) =>
    this.request<boolean, any>({
      path: `/api/wishlist/deleteWishlist`,
      method: 'DELETE',
      body: data,
      type: ContentType.Json,
      ...params,
    });
}
