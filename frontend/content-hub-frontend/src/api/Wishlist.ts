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

import { WishlistListResponseDto, WishlistRequestDto } from './data-contracts';
import { ContentType, HttpClient, RequestParams } from './http-client';

export class Wishlist<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags wishlist-controller
   * @name SaveWishlist
   * @request POST:/wishlist/save
   */
  saveWishlist = (data: WishlistRequestDto, params: RequestParams = {}) =>
    this.request<boolean, any>({
      path: `/wishlist/save`,
      method: 'POST',
      body: data,
      type: ContentType.Json,
      ...params,
    });
  /**
   * No description
   *
   * @tags wishlist-controller
   * @name GetWishlist
   * @request POST:/wishlist/getWishlist
   */
  getWishlist = (
    query: {
      /** @format int64 */
      userId: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<WishlistListResponseDto, any>({
      path: `/wishlist/getWishlist`,
      method: 'POST',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags wishlist-controller
   * @name DeleteWishlist
   * @request DELETE:/wishlist/delete
   */
  deleteWishlist = (data: WishlistRequestDto, params: RequestParams = {}) =>
    this.request<boolean, any>({
      path: `/wishlist/delete`,
      method: 'DELETE',
      body: data,
      type: ContentType.Json,
      ...params,
    });
}
