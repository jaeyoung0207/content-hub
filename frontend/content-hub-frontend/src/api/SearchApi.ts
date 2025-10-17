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
  SearchComicsResponseDto,
  SearchMovieResponseDto,
  SearchTvResponseDto,
  SearchVideoResponseDto,
} from './data-contracts';
import { HttpClient, RequestParams } from './http-client';

export class SearchApi<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags search-api
   * @name SearchVideo
   * @summary 비디오 검색
   * @request GET:/api/search/searchVideo
   */
  searchVideo = (
    query: {
      keyword: string;
      /** @format int64 */
      user_id?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<SearchVideoResponseDto, any>({
      path: `/api/search/searchVideo`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags search-api
   * @name SearchTvExceptAni
   * @summary 애니메이션 제외한 TV 시리즈 검색
   * @request GET:/api/search/searchTvExceptAni
   */
  searchTvExceptAni = (
    query: {
      keyword: string;
      content_media_type: string;
      /** @format int32 */
      page?: number;
      /** @format int64 */
      user_id?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<SearchTvResponseDto, any>({
      path: `/api/search/searchTvExceptAni`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags search-api
   * @name SearchMovie
   * @summary 영화 정보 검색
   * @request GET:/api/search/searchMovie
   */
  searchMovie = (
    query: {
      keyword: string;
      /** @format int32 */
      page?: number;
      /** @format int64 */
      user_id?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<SearchMovieResponseDto, any>({
      path: `/api/search/searchMovie`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags search-api
   * @name SearchKeyword
   * @summary 검색어 리스트 조회
   * @request GET:/api/search/searchKeyword
   */
  searchKeyword = (
    query: {
      keyword: string;
    },
    params: RequestParams = {}
  ) =>
    this.request<string[], any>({
      path: `/api/search/searchKeyword`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags search-api
   * @name SearchComics
   * @summary 만화 정보 검색
   * @request GET:/api/search/searchComics
   */
  searchComics = (
    query: {
      keyword: string;
      /** @format int32 */
      page?: number;
      is_main_page: boolean;
      /** @format int64 */
      user_id?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<SearchComicsResponseDto, any>({
      path: `/api/search/searchComics`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags search-api
   * @name SearchAni
   * @summary 애니메이션 검색
   * @request GET:/api/search/searchAni
   */
  searchAni = (
    query: {
      keyword: string;
      /** @format int32 */
      page?: number;
      /** @format int64 */
      user_id?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<SearchTvResponseDto, any>({
      path: `/api/search/searchAni`,
      method: 'GET',
      query: query,
      ...params,
    });
}
