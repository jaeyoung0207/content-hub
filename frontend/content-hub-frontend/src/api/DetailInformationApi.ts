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
  DetailMovieResponseDto,
  DetailTvResponseDto,
} from './data-contracts';
import { HttpClient, RequestParams } from './http-client';

export class DetailInformationApi<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags detail-information-api
   * @name GetTvDetail
   * @summary TV 상세 조회
   * @request GET:/api/detail/information/getTvDetail
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
      path: `/api/detail/information/getTvDetail`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-information-api
   * @name GetMovieDetail
   * @summary 영화 상세 조회
   * @request GET:/api/detail/information/getMovieDetail
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
      path: `/api/detail/information/getMovieDetail`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-information-api
   * @name GetComicsStaffList
   * @summary 스태프 리스트 조회
   * @request GET:/api/detail/information/getComicsStaffList
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
      path: `/api/detail/information/getComicsStaffList`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-information-api
   * @name GetComicsDetail
   * @summary 만화 상세 조회
   * @request GET:/api/detail/information/getComicsDetail
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
      path: `/api/detail/information/getComicsDetail`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags detail-information-api
   * @name GetComicsCharacterList
   * @summary 캐릭터 리스트 조회
   * @request GET:/api/detail/information/getComicsCharacterList
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
      path: `/api/detail/information/getComicsCharacterList`,
      method: 'GET',
      query: query,
      ...params,
    });
}
