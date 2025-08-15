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
  SearchVideoResponseDto,
  TmdbSearchMovieDto,
  TmdbSearchTvDto,
} from './data-contracts';
import { HttpClient, RequestParams } from './http-client';

export class Search<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags search-controller
   * @name SearchVideo
   * @request GET:/search/searchVideo
   */
  searchVideo = (
    query: {
      query: string;
    },
    params: RequestParams = {}
  ) =>
    this.request<SearchVideoResponseDto, any>({
      path: `/search/searchVideo`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags search-controller
   * @name SearchMovie
   * @request GET:/search/searchMovie
   */
  searchMovie = (
    query: {
      query: string;
      /** @format int32 */
      page?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<TmdbSearchMovieDto, any>({
      path: `/search/searchMovie`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags search-controller
   * @name SearchKeyword
   * @request GET:/search/searchKeyword
   */
  searchKeyword = (
    query: {
      query: string;
    },
    params: RequestParams = {}
  ) =>
    this.request<string[], any>({
      path: `/search/searchKeyword`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags search-controller
   * @name SearchDrama
   * @request GET:/search/searchDrama
   */
  searchDrama = (
    query: {
      query: string;
      /** @format int32 */
      page?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<TmdbSearchTvDto, any>({
      path: `/search/searchDrama`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags search-controller
   * @name SearchComics
   * @request GET:/search/searchComics
   */
  searchComics = (
    query: {
      query: string;
      /** @format int32 */
      page?: number;
      isMainPage: boolean;
    },
    params: RequestParams = {}
  ) =>
    this.request<SearchComicsResponseDto, any>({
      path: `/search/searchComics`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags search-controller
   * @name SearchAni
   * @request GET:/search/searchAni
   */
  searchAni = (
    query: {
      query: string;
      /** @format int32 */
      page?: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<TmdbSearchTvDto, any>({
      path: `/search/searchAni`,
      method: 'GET',
      query: query,
      ...params,
    });
}
