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
  DetailComicsRecommendationsResponseDto,
  DetailComicsResponseDto,
  DetailCommentGetResponseDto,
  DetailCommentSaveRequestDto,
  DetailCommentUpdateRequestDto,
  DetailMovieResponseDto,
  DetailTvResponseDto,
  TmdbRecommendationsMovieDto,
  TmdbRecommendationsTvDto,
} from "./data-contracts";
import { ContentType, HttpClient, RequestParams } from "./http-client";

export class Detail<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags detail-controller
   * @name UpdateComent
   * @request PUT:/detail/updateComment
   */
  updateComent = (
    data: DetailCommentUpdateRequestDto,
    params: RequestParams = {},
  ) =>
    this.request<boolean, any>({
      path: `/detail/updateComment`,
      method: "PUT",
      body: data,
      type: ContentType.Json,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-controller
   * @name SaveComent
   * @request POST:/detail/saveComment
   */
  saveComent = (
    data: DetailCommentSaveRequestDto,
    params: RequestParams = {},
  ) =>
    this.request<boolean, any>({
      path: `/detail/saveComment`,
      method: "POST",
      body: data,
      type: ContentType.Json,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-controller
   * @name GetTvRecommendations
   * @request GET:/detail/getTvRecommendations
   */
  getTvRecommendations = (
    query: {
      /** @format int32 */
      series_id: number;
      /** @format int32 */
      page?: number;
    },
    params: RequestParams = {},
  ) =>
    this.request<TmdbRecommendationsTvDto, any>({
      path: `/detail/getTvRecommendations`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-controller
   * @name GetTvDetail
   * @request GET:/detail/getTvDetail
   */
  getTvDetail = (
    query: {
      /** @format int32 */
      series_id: number;
      originalMediaType: string;
    },
    params: RequestParams = {},
  ) =>
    this.request<DetailTvResponseDto, any>({
      path: `/detail/getTvDetail`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-controller
   * @name GetStarRatingAverage
   * @request GET:/detail/getStarRatingAverage
   */
  getStarRatingAverage = (
    query: {
      originalMediaType: string;
      apiId: string;
    },
    params: RequestParams = {},
  ) =>
    this.request<number, any>({
      path: `/detail/getStarRatingAverage`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-controller
   * @name GetMovieRecommendations
   * @request GET:/detail/getMovieRecommendations
   */
  getMovieRecommendations = (
    query: {
      /** @format int32 */
      movie_id: number;
      /** @format int32 */
      page?: number;
    },
    params: RequestParams = {},
  ) =>
    this.request<TmdbRecommendationsMovieDto, any>({
      path: `/detail/getMovieRecommendations`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-controller
   * @name GetMovieDetail
   * @request GET:/detail/getMovieDetail
   */
  getMovieDetail = (
    query: {
      /** @format int32 */
      movie_id: number;
      originalMediaType: string;
    },
    params: RequestParams = {},
  ) =>
    this.request<DetailMovieResponseDto, any>({
      path: `/detail/getMovieDetail`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-controller
   * @name GetCommentList
   * @request GET:/detail/getCommentList
   */
  getCommentList = (
    query: {
      originalMediaType: string;
      apiId: string;
      /** @format int32 */
      page?: number;
      userId?: string;
    },
    params: RequestParams = {},
  ) =>
    this.request<DetailCommentGetResponseDto, any>({
      path: `/detail/getCommentList`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-controller
   * @name GetComicsRecommendations
   * @request GET:/detail/getComicsRecommendations
   */
  getComicsRecommendations = (
    query: {
      /** @format int32 */
      mediaId: number;
      /** @format int32 */
      page?: number;
    },
    params: RequestParams = {},
  ) =>
    this.request<DetailComicsRecommendationsResponseDto, any>({
      path: `/detail/getComicsRecommendations`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-controller
   * @name GetComicsDetail
   * @request GET:/detail/getComicsDetail
   */
  getComicsDetail = (
    query: {
      /** @format int32 */
      comics_id: number;
    },
    params: RequestParams = {},
  ) =>
    this.request<DetailComicsResponseDto, any>({
      path: `/detail/getComicsDetail`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-controller
   * @name DeleteComment
   * @request DELETE:/detail/deleteComment
   */
  deleteComment = (
    query: {
      /** @format int64 */
      commentNo: number;
    },
    params: RequestParams = {},
  ) =>
    this.request<boolean, any>({
      path: `/detail/deleteComment`,
      method: "DELETE",
      query: query,
      ...params,
    });
}
