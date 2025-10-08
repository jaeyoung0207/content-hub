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
  AniListCharactersDto,
  AniListStaffDto,
  DetailComicsResponseDto,
  DetailCommentsGetResponseDto,
  DetailCommentsSaveRequestDto,
  DetailCommentsUpdateRequestDto,
  DetailMovieResponseDto,
  DetailRecommendationsComicsResponseDto,
  DetailRecommendationsMovieDto,
  DetailRecommendationsTvDto,
  DetailTvResponseDto,
} from './data-contracts';
import { ContentType, HttpClient, RequestParams } from './http-client';

export class Detail<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags detail-comments-controller
   * @name UpdateComent
   * @request PUT:/detail/comment/updateComment
   */
  updateComent = (
    data: DetailCommentsUpdateRequestDto,
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
   * @tags detail-comments-controller
   * @name SaveComent
   * @request POST:/detail/comment/saveComment
   */
  saveComent = (
    data: DetailCommentsSaveRequestDto,
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
      /** @format int64 */
      user_id?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<DetailRecommendationsTvDto, any>({
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
      /** @format int64 */
      user_id?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<DetailRecommendationsMovieDto, any>({
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
      media_id: number;
      /** @format int32 */
      page?: number;
      /** @format int64 */
      user_id?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<DetailRecommendationsComicsResponseDto, any>({
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
      content_media_type: string;
      /** @format int64 */
      user_id?: number;
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
      content_media_type: string;
      /** @format int64 */
      user_id?: number;
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
   * @name GetComicsStaffList
   * @request GET:/detail/information/getComicsStaffList
   */
  getComicsStaffList = (
    query: {
      /** @format int32 */
      comics_id: number;
      /** @format int32 */
      page: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<AniListStaffDto, any>({
      path: `/detail/information/getComicsStaffList`,
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
      content_media_type: string;
      /** @format int64 */
      user_id?: number;
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
   * @tags detail-information-controller
   * @name GetComicsCharacterList
   * @request GET:/detail/information/getComicsCharacterList
   */
  getComicsCharacterList = (
    query: {
      /** @format int32 */
      comics_id: number;
      /** @format int32 */
      page: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<AniListCharactersDto, any>({
      path: `/detail/information/getComicsCharacterList`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-comments-controller
   * @name GetStarRatingAverage
   * @request GET:/detail/comment/getStarRatingAverage
   */
  getStarRatingAverage = (
    query: {
      content_media_type: string;
      api_id: string;
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
   * @tags detail-comments-controller
   * @name GetCommentList
   * @request GET:/detail/comment/getCommentList
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
      path: `/detail/comment/getCommentList`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-comments-controller
   * @name DeleteComment
   * @request DELETE:/detail/comment/deleteComment
   */
  deleteComment = (
    query: {
      /** @format int64 */
      comment_id: number;
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
