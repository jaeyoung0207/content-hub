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
  DetailRecommendationsComicsResponseDto,
  DetailRecommendationsMovieDto,
  DetailRecommendationsTvDto,
} from './data-contracts';
import { HttpClient, RequestParams } from './http-client';

export class DetailRecommendationApi<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags detail-recommendation-api
   * @name GetTvRecommendations
   * @summary TV 추천 작품 조회
   * @request GET:/api/detail/recommendation/getTvRecommendations
   */
  getTvRecommendations = (
    query: {
      /** @format int32 */
      series_id: number;
      /** @format int32 */
      page?: number;
      /** @format int64 */
      user_id?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<DetailRecommendationsTvDto, any>({
      path: `/api/detail/recommendation/getTvRecommendations`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-recommendation-api
   * @name GetMovieRecommendations
   * @summary 영화 추천 작품 조회
   * @request GET:/api/detail/recommendation/getMovieRecommendations
   */
  getMovieRecommendations = (
    query: {
      /** @format int32 */
      movie_id: number;
      /** @format int32 */
      page?: number;
      /** @format int64 */
      user_id?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<DetailRecommendationsMovieDto, any>({
      path: `/api/detail/recommendation/getMovieRecommendations`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-recommendation-api
   * @name GetComicsRecommendations
   * @summary 만화 추천 작품 조회
   * @request GET:/api/detail/recommendation/getComicsRecommendations
   */
  getComicsRecommendations = (
    query: {
      /** @format int32 */
      media_id: number;
      /** @format int32 */
      page?: number;
      /** @format int64 */
      user_id?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<DetailRecommendationsComicsResponseDto, any>({
      path: `/api/detail/recommendation/getComicsRecommendations`,
      method: 'GET',
      query: query,
      ...params,
    });
}
