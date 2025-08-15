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
} from './data-contracts';
import { ContentType, HttpClient, RequestParams } from './http-client';

export class Detail<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags detail-comment-controller
   * @name UpdateComent
   * @request PUT:/detail/comment/updateComment
   */
  updateComent = (
    data: DetailCommentUpdateRequestDto,
    params: RequestParams = {}
  ) =>
    this.request<boolean, any>({
      path: `/detail/comment/updateComment`,
      method: 'PUT',
      body: data,
      type: ContentType.Json,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-comment-controller
   * @name SaveComent
   * @request POST:/detail/comment/saveComment
   */
  saveComent = (
    data: DetailCommentSaveRequestDto,
    params: RequestParams = {}
  ) =>
    this.request<boolean, any>({
      path: `/detail/comment/saveComment`,
      method: 'POST',
      body: data,
      type: ContentType.Json,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-recommendation-controller
   * @name GetTvRecommendations
   * @request GET:/detail/recommendation/getTvRecommendations
   */
  getTvRecommendations = (
    query: {
      /** @format int32 */
      series_id: number;
      /** @format int32 */
      page?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<TmdbRecommendationsTvDto, any>({
      path: `/detail/recommendation/getTvRecommendations`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-recommendation-controller
   * @name GetMovieRecommendations
   * @request GET:/detail/recommendation/getMovieRecommendations
   */
  getMovieRecommendations = (
    query: {
      /** @format int32 */
      movie_id: number;
      /** @format int32 */
      page?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<TmdbRecommendationsMovieDto, any>({
      path: `/detail/recommendation/getMovieRecommendations`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-recommendation-controller
   * @name GetComicsRecommendations
   * @request GET:/detail/recommendation/getComicsRecommendations
   */
  getComicsRecommendations = (
    query: {
      /** @format int32 */
      mediaId: number;
      /** @format int32 */
      page?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<DetailComicsRecommendationsResponseDto, any>({
      path: `/detail/recommendation/getComicsRecommendations`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-information-controller
   * @name GetTvDetail
   * @request GET:/detail/information/getTvDetail
   */
  getTvDetail = (
    query: {
      /** @format int32 */
      series_id: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<DetailTvResponseDto, any>({
      path: `/detail/information/getTvDetail`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-information-controller
   * @name GetMovieDetail
   * @request GET:/detail/information/getMovieDetail
   */
  getMovieDetail = (
    query: {
      /** @format int32 */
      movie_id: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<DetailMovieResponseDto, any>({
      path: `/detail/information/getMovieDetail`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-information-controller
   * @name GetComicsDetail
   * @request GET:/detail/information/getComicsDetail
   */
  getComicsDetail = (
    query: {
      /** @format int32 */
      comics_id: number;
      /** @format int32 */
      page?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<DetailComicsResponseDto, any>({
      path: `/detail/information/getComicsDetail`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-comment-controller
   * @name GetStarRatingAverage
   * @request GET:/detail/comment/getStarRatingAverage
   */
  getStarRatingAverage = (
    query: {
      originalMediaType: string;
      apiId: string;
    },
    params: RequestParams = {}
  ) =>
    this.request<number, any>({
      path: `/detail/comment/getStarRatingAverage`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-comment-controller
   * @name GetCommentList
   * @request GET:/detail/comment/getCommentList
   */
  getCommentList = (
    query: {
      originalMediaType: string;
      apiId: string;
      /** @format int32 */
      page?: number;
      userId?: string;
    },
    params: RequestParams = {}
  ) =>
    this.request<DetailCommentGetResponseDto, any>({
      path: `/detail/comment/getCommentList`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-comment-controller
   * @name DeleteComment
   * @request DELETE:/detail/comment/deleteComment
   */
  deleteComment = (
    query: {
      /** @format int64 */
      commentNo: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<boolean, any>({
      path: `/detail/comment/deleteComment`,
      method: 'DELETE',
      query: query,
      ...params,
    });
}
